package com.atypon.mapreduceworkflow.phases.sharedphases.runandcollect;

import com.atypon.docker.ContainersDataTracker;
import com.atypon.docker.ContainersHandler;
import com.atypon.gui.Main;
import com.atypon.mapreduceworkflow.Collector;
import com.atypon.mapreduceworkflow.phases.PhaseExecutionFailed;
import com.atypon.workflow.Context;
import com.atypon.workflow.phase.Executor;

public class SendAndCollectExecutor implements Executor {
  @Override
  public Context execute(Context context) throws PhaseExecutionFailed {

    ContainersDataTracker dataTracker = ContainersDataTracker.getInstance();
    ContainersHandler handler = ContainersHandler.getInstance();

    try {
      Main.appendText("Waiting For Containers to Run\n");
      dataTracker
          .getWaitForContainersLatch()
          .await(); // TimeOut of waiting can be injected in await() method.
    } catch (Exception e) {
      throw new PhaseExecutionFailed("TimeOut");
    }

    Main.appendText("Sending Reducers Addresses To Mappers\n");
    try {
      handler.sendReducerAddressesToMappers();
    } catch (Exception e) {
      throw new PhaseExecutionFailed("Failed to send Reducer Addresses");
    }
    Main.appendText("Sending Mappers Addresses To Reducers\n");
    try {
      handler.sendMappersAddressesToReducers();
    } catch (Exception e) {
      throw new PhaseExecutionFailed("Failed to send Reducer Addresses");
    }

    try {
      Main.appendText("Sending Files To Mappers\n");
      handler.sendFilesToMappers("./temp/Data/");
    } catch (Exception e) {
      throw new PhaseExecutionFailed("Failed to files to mappers");
    }

    try {
      Main.appendText("Waiting for mappers to finish working\n");
      dataTracker
          .getFinishedMappersLatch()
          .await(); // TimeOut of waiting can be injected in await() method.
      Main.appendText("Mappers Finished\n");
    } catch (Exception e) {
      throw new PhaseExecutionFailed("Failed to wait mappers to finish working");
    }

    try {
      Main.appendText("started Collector\n");
      Collector.startCollecting(context.getParam("numOfReducers"));
      Collector.getAllDataCollectedLatch().await();

      Main.appendText("Saving Data to output.txt\n");
      Collector.printCollectedDataToFile();
      Main.appendText("Data saved\n");
    } catch (Exception e) {
      throw new PhaseExecutionFailed("data collection failed");
    }
    return context;
  }
}
