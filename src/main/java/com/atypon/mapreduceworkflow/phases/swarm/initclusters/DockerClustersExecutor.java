package com.atypon.mapreduceworkflow.phases.swarm.initclusters;

import com.atypon.gui.Main;
import com.atypon.mapreduceworkflow.phases.PhaseExecutionFailed;
import com.atypon.workflow.Context;
import com.atypon.workflow.phase.Executor;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class DockerClustersExecutor implements Executor {

  @Override
  public Context execute(Context context) throws PhaseExecutionFailed {

    try {
      ProcessBuilder processBuilder = new ProcessBuilder();
      processBuilder.command(
          "sh",
          "-c",
          "./src/main/resources/Scripts/ClustersInit.sh "
              + context.getParam("numOfMappers")
              + " "
              + context.getParam("numOfReducers"));
      Process process = processBuilder.start();
      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

      String line;
      while ((line = reader.readLine()) != null) {
        System.out.println(line);
      }

      int exitCode = process.waitFor();
      System.out.println("\nCluster initialization Exited with error code : " + exitCode);

      if (exitCode != 0) {
        throw new PhaseExecutionFailed("Running Docker-Compose Failed");
      }
    } catch (Exception e) {
      throw new PhaseExecutionFailed("Clusters initialization failed Failed");
    }

    Main.appendText("Docker Clusters Initialized successfully\n");

    return context;
  }
}
