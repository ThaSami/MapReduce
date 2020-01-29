package com.atypon.utility;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SwarmUtils {

  private SwarmUtils() {}

  public static String getManagerIP() {
    ProcessBuilder processBuilder = new ProcessBuilder();
    String splitCommand = "docker-machine ip manager";

    processBuilder.command("sh", "-c", splitCommand);
    String line = "";
    try {

      Process process = processBuilder.start();

      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      while ((line = reader.readLine()) != null) {
        return line;
      }

      int exitCode = process.waitFor();
      System.out.println("\nManager IP Exited with error code : " + exitCode);

    } catch (Exception e) {
      e.printStackTrace();
    }
    return "";
  }
}
