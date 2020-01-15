package utility;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;

public class FilesUtil {

  private FilesUtil() {
  }

  public static void splitter(String filename, int numOfFiles) {

    ProcessBuilder processBuilder = new ProcessBuilder();
    File file = new File(filename);
    int size = (int) (file.length() / 1024) / numOfFiles + 1; //convert to kb then split the size evenly , //TODO covert from gb to kb
    String splitCommand = "cd ./temp/Data/ &&" + " split -d -C " + size + "k" + " Data.txt" + " map";
    processBuilder.command("sh", "-c", splitCommand);

    try {

      Process process = processBuilder.start();

      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

      String line;
      while ((line = reader.readLine()) != null) {
        System.out.println(line);
      }

      int exitCode = process.waitFor();
      System.out.println("\nSplitter Exited with error code : " + exitCode);

    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }


  public static void copy(String from, String to) throws IOException {
    File src = new File(from);
    File root = new File("./temp");
    File dst = new File(root, to);
    dst.getParentFile().mkdirs();

    Files.copy(src.toPath(), dst.toPath(), StandardCopyOption.REPLACE_EXISTING);

  }

  public static Boolean checkIfExist(String path) {
    File file = new File(path);
    return file.exists();
  }

  public static Boolean checkIfNotExist(String path) {
    File file = new File(path);
    return !file.exists();
  }

  public static List<String> getFilesInDirectory(String directoryPath) {
    List<String> results = new ArrayList<>();

    File[] files = new File(directoryPath).listFiles();
    for (File file : files) {
      if (file.isFile()) {
        results.add(file.getName());
      }
    }
    return results;
  }

  public static void fileUploader(String host, String path) {

    File f = new File(path);

    try (Socket socket = new Socket(host, Constants.MAPPERS_FILE_RECEIVER_PORT);
         InputStream in = new FileInputStream(f);
         OutputStream out = socket.getOutputStream()) {
      long length = f.length();
      byte[] bytes = new byte[8192];
      int count;
      while ((count = in.read(bytes)) > 0) {
        out.write(bytes, 0, count);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void CompileJavaCode(File sourceFile) {
    sourceFile.getParentFile().mkdirs();
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    int resultCode = compiler.run(null, null, null, sourceFile.getPath());
    if (resultCode != 0) {
      throw new IllegalFormatCodePointException(1);
    }
  }
}
