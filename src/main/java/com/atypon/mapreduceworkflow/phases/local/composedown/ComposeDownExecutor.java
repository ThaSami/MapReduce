package com.atypon.mapreduceworkflow.phases.local.composerun;

import com.atypon.gui.Main;
import com.atypon.mapreduceworkflow.phases.PhaseExecutionFailed;
import com.atypon.workflow.Context;
import com.atypon.workflow.phase.Executor;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

public class ComposeDownExecutor implements Executor {

    @Override
    public Context execute(Context context) throws PhaseExecutionFailed {

        try {


            Main.appendText("Shutting Down docker-compose\n");
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("sh", "-c", "docker-compose --compatibility down");

            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("\nshutting compose Exited with error code : " + exitCode);

            // process failed to execute
            if (exitCode != 0) {
                throw new PhaseExecutionFailed("shutting Docker-Compose Failed");
            }
        } catch (Exception e) {
            throw new PhaseExecutionFailed("shutting Docker-Compose Failed");
        }

        return context;
    }
}
