package com.atypon.mapreduceworkflow.phases.swarm.dockerimageshare;

import com.atypon.mapreduceworkflow.phases.PhaseExecutionFailed;
import com.atypon.workflow.Context;
import com.atypon.workflow.phase.Executor;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SwarmImagesExecutor implements Executor {


    @Override
    public Context execute(Context context) throws PhaseExecutionFailed {

        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(
                    "sh",
                    "-c",
                    "./src/main/resources/Scripts/SwarmImageSender.sh ");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("\nimage sending Exited with error code : " + exitCode);

            if (exitCode != 0) {
                throw new PhaseExecutionFailed("Docker Image Sending Failed");
            }
        } catch (Exception e) {
            throw new PhaseExecutionFailed("Docker Image Sending Failed");
        }


        return context;
    }
}
