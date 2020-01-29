package com.atypon.mapreduceworkflow.phases.swarm.swarminit;

import com.atypon.gui.Main;
import com.atypon.mapreduceworkflow.phases.PhaseExecutionFailed;
import com.atypon.workflow.Context;
import com.atypon.workflow.phase.Executor;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SwarmInitExecutor implements Executor {
  @Override
  public Context execute(Context context) throws PhaseExecutionFailed {
    try {
      ProcessBuilder processBuilder = new ProcessBuilder();
      processBuilder.command(
          "sh",
          "-c",
          "./src/main/resources/Scripts/SwarmInit.sh "
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
      System.out.println("\nswarm initialization Exited with error code : " + exitCode);

      if (exitCode != 0) {
        throw new PhaseExecutionFailed("Running Docker-Compose Failed");
      }
    } catch (Exception e) {
      throw new PhaseExecutionFailed("swarm initialization Failed");
    }

    Main.appendText("Swarm Initialized on Clusters successfully\n");
    Main.appendText("visit http://localhost:8080 for swarm monitoring\n");
    return context;
  }
}
