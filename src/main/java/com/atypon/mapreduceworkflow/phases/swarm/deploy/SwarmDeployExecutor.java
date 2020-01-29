package com.atypon.mapreduceworkflow.phases.swarm.deploy;

import com.atypon.mapreduceworkflow.phases.PhaseExecutionFailed;
import com.atypon.workflow.Context;
import com.atypon.workflow.phase.Executor;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SwarmDeployExecutor implements Executor {
  @Override
  public Context execute(Context context) throws PhaseExecutionFailed {

    try {
      ProcessBuilder processBuilder = new ProcessBuilder();
      processBuilder.command(
          "sh",
          "-c",
          "docker-machine scp docker-compose.yml manager:/home/docker/ &&"
              + " docker-machine ssh manager 'docker stack deploy --compose-file=docker-compose.yml mapreduce'");
      Process process = processBuilder.start();
      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

      String line;
      while ((line = reader.readLine()) != null) {
        System.out.println(line);
      }

      int exitCode = process.waitFor();
      System.out.println("\nDeploying to swarm Exited with error code : " + exitCode);

      if (exitCode != 0) {
        throw new PhaseExecutionFailed("Deploying to swarm Failed");
      }
    } catch (Exception e) {
      throw new PhaseExecutionFailed("Deploying to swarm Failed");
    }

    return context;
  }
}
