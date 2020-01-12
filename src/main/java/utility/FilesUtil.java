package utility;


import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

  private static Path getPathObject(String pathString) {
    return Paths.get(pathString);
  }

  public static Boolean checkIfExist(String path) {
    File file = new File(path);
    return file.exists();
  }

  public static Boolean checkIfNotExist(String path) {
    File file = new File(path);
    return !file.exists();
  }

  public static void fileUploader(String host, String file) {

    String Host = host;
    File f = new File(file);

    try (Socket socket = new Socket(Host, 4444);
         InputStream in = new FileInputStream(f);
         OutputStream out = socket.getOutputStream();) {
      // Get the size of the file
      long length = f.length();
      byte[] bytes = new byte[16 * 1024];
      int count;
      while ((count = in.read(bytes)) > 0) {
        out.write(bytes, 0, count);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}
