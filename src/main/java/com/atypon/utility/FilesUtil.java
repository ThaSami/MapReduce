package com.atypon.utility;


import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class FilesUtil {

    private FilesUtil() {
    }


    public static void copyToDir(File srcPath, File dstWithName) throws IOException {
        Files.copy(srcPath.toPath(), dstWithName.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    public static Boolean checkIfNotExist(String path) {
        File file = new File(path);
        return !file.exists();
    }

    public static List<String> getFilesAbsPathInDirectory(String directoryPath) {
        List<String> results = new ArrayList<>();

    File[] files = new File(directoryPath).listFiles();
    for (File file : files) {
      if (file.isFile()) {
        results.add(file.getAbsolutePath());
      }
    }
    return results;
  }

    public static void fileUploader(String address, String fileAbsPath) throws IOException {

        File f = new File(fileAbsPath);

        try (Socket socket = new Socket(address, Constants.MAPPERS_FILE_RECEIVER_PORT);
             InputStream in = new FileInputStream(f);
             OutputStream out = socket.getOutputStream()) {

            byte[] bytes = new byte[8192];
            int count;
            while ((count = in.read(bytes)) > 0) {
        out.write(bytes, 0, count);
      }
    } catch (Exception e) {
            throw e;
    }
  }

  public static String readFileAsString(String filePath) {
    String content = "";

    try {
      content = new String(Files.readAllBytes(Paths.get(filePath)));
    } catch (IOException e) {
      e.printStackTrace();
    }

    return content;
  }


}
