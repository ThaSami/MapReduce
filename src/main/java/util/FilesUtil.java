package util;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class FilesUtil {

  private FilesUtil() {
  }

  public static void split(String filename, int numOfFiles) {

    ProcessBuilder processBuilder = new ProcessBuilder();
    File file = new File(filename);
    int size = (int) (file.length() / 1024) / numOfFiles + 1;
    String splitCommand = "split -d -C " + size + "k" + " " + filename + " map";
    processBuilder.command("bash", "-c", splitCommand);

    try {

      Process process = processBuilder.start();

      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

      String line;
      while ((line = reader.readLine()) != null) {
        System.out.println(line);
      }

      int exitCode = process.waitFor();
      System.out.println("\nExited with error code : " + exitCode);

    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static boolean checkOS(String name) {
    String osName = System.getProperty("os.name").toLowerCase();

    return osName.startsWith(name.toLowerCase());
  }
}
