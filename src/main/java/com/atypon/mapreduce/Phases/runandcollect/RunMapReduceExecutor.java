package com.atypon.mapreduce.Phases.runandcollect;

import com.atypon.mapreduce.Collector;
import com.atypon.mapreduce.Phases.PhaseExecutionFailed;
import com.atypon.docker.ContainersDataTracker;
import com.atypon.docker.ContainersHandler;
import com.atypon.gui.Main;
import com.atypon.workflow.Context;
import com.atypon.workflow.phase.Executor;

public class RunMapReduceExecutor implements Executor {
    @Override
    public Context execute(Context context) throws PhaseExecutionFailed, InterruptedException {

        ContainersDataTracker dataTracker = ContainersDataTracker.getInstance();
        ContainersHandler handler = ContainersHandler.getInstance();

        try {
            Main.appendText("Waiting For Containers to Run\n");
            dataTracker.getWaitForContainersLatch().await();
        } catch (Exception e) {
            throw new PhaseExecutionFailed("TimeOut");
        }

        Main.appendText("Sending Number of mappers To reducers\n");
        try {
            handler.sendNumOfMappersToReducers();
        } catch (Exception e) {
            e.printStackTrace();
            //throw new PhaseExecutionFailed("Failed to send Reducer Addresses");
        }

        Main.appendText("Sending Reducers Addresses To Mappers\n");
        try {
            handler.sendReducerAddressesToMappers();
        } catch (Exception e) {
            e.printStackTrace();
            //throw new PhaseExecutionFailed("Failed to send Reducer Addresses");
        }

        Main.appendText("Sending Files To Mappers\n");
        handler.sendFilesToMappers("./temp/Data/");

        try {
            Main.appendText("Waiting for mappers to finish working\n");
            dataTracker.getFinishedMappersLatch().await();
        } catch (Exception e) {
            throw new PhaseExecutionFailed("Failed to wait mappers to finish working");
        }


        Main.appendText("Mappers Finished\n");

        new Thread(() -> {
            Main.appendText("started Collector\n");
            Collector.startCollecting(context.getParam("numOfReducers"));
        }).start();


        Main.appendText("sending start flag to Reducers\n");
        handler.sendStartFlagToReducers();

        Collector.getAllDataCollectedLatch().await();
        Collector.printCollectedDataToFile();
        Main.appendText("Data Print\n");
        return context;
    }
}
