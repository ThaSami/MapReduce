package com.atypon.mapreduce.Phases.local.composerun;

import com.atypon.gui.Main;
import com.atypon.mapreduce.Phases.PhaseExecutionFailed;
import com.atypon.workflow.Context;
import com.atypon.workflow.phase.Executor;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

public class DockerComposeExecutor implements Executor {

    @Override
    public Context execute(Context context) throws PhaseExecutionFailed, InterruptedException {

        try {

            VelocityEngine velocityEngine = new VelocityEngine();
            velocityEngine.init();
            Template t = velocityEngine.getTemplate("src/main/resources/compose/docker-compose.vm");
            StringWriter writer = new StringWriter();
            t.merge(context.getParam("compose-data"), writer);

            Path file = Paths.get("docker-compose.yml");
            Files.write(file, Collections.singleton(writer.toString()), StandardCharsets.UTF_8);

            Main.appendText("docker-compose created Successfully\n");

            Main.appendText("Running Containers Locally...");
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("sh", "-c", "docker-compose --compatibility up -d");

            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("\nRunning compose Exited with error code : " + exitCode);

        } catch (IOException | InterruptedException e) {
            new PhaseExecutionFailed("Running Docker-Compose Failed");
        }

        return context;
    }
}
