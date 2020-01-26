package com.atypon.mapreduce.Phases.local.composerun;

import com.atypon.docker.ContainersWorker;
import com.atypon.gui.Main;
import com.atypon.mapreduce.Phases.PhaseExecutionFailed;
import com.atypon.workflow.Context;
import com.atypon.workflow.phase.Executor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import static com.atypon.utility.Constants.MAIN_SERVER_PORT;

public class ComposeRunnerExecutor implements Executor {

    @Override
    public Context execute(Context context) throws PhaseExecutionFailed, InterruptedException {

        new Thread(
                () -> {
                    try (ServerSocket server = new ServerSocket(MAIN_SERVER_PORT)) {

                        Main.appendText("managing server started at " + new Date() + '\n');
                        while (true) {
                            Socket client = server.accept();
                            ContainersWorker thread = new ContainersWorker(client);
                            thread.handle();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                })
                .start();


        return context;
    }
}
