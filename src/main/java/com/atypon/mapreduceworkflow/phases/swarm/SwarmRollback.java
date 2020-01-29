package com.atypon.mapreduceworkflow.phases.swarm;

import com.atypon.gui.Main;
import com.atypon.mapreduceworkflow.phases.DummyRollback;
import com.atypon.mapreduceworkflow.phases.PhaseExecutionFailed;
import com.atypon.workflow.Context;
import com.atypon.workflow.phase.Rollback;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SwarmRollback implements Rollback {

    public static SwarmRollback instance = new SwarmRollback();

    private SwarmRollback() {
    }

    public static SwarmRollback getInstance() {
        return instance;
    }


    @Override
    public void rollback(Context context) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(
                    "sh",
                    "-c",
                    "docker-machine rm $(docker-machine ls -q) -y");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("\nRollingback Exited with error code : " + exitCode);

            if (exitCode != 0) {
                 System.out.println("Failed to rollback");
                 System.exit(1);
            }

            Main.appendText("Programme Failed and rolledback to original state");
            while (true){
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
