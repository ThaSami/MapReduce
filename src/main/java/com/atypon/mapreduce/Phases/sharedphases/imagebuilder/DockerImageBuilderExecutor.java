package com.atypon.mapreduce.Phases.sharedphases.imagebuilder;

import com.atypon.gui.Main;
import com.atypon.mapreduce.Phases.PhaseExecutionFailed;
import com.atypon.workflow.Context;
import com.atypon.workflow.phase.Executor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DockerImageBuilderExecutor implements Executor {
    @Override
    public Context execute(Context context) throws PhaseExecutionFailed, InterruptedException {

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("sh", "-c", "docker build . -t map_reduce");
        Main.appendText("Building Docker Image , this may take time...");
        try {

            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("\nImage Built Exited with error code : " + exitCode);

        } catch (IOException | InterruptedException e) {
            new PhaseExecutionFailed("Couldn't build docker image");
        }
        Main.appendText("Docker Image Built successfully");
        return context;
    }
}
