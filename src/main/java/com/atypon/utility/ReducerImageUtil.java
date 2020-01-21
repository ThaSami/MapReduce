package com.atypon.utility;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class ReducerImageUtil {

  private ReducerImageUtil() {
  }

  public static void prepareReducerCode(String reducerMethod, String customImports) {
    String source =
            customImports
                    + "import java.io.*;\n"
                    + "public class ReducerUtil { "
                    + reducerMethod
                    + " }";
    try {
      File root = new File("./temp");
      File sourceFile = new File(root, "reducer/ReducerUtil.java");
      sourceFile.getParentFile().mkdirs();
      Files.write(sourceFile.toPath(), source.getBytes(StandardCharsets.UTF_8));

      FilesUtil.CompileJavaCode(sourceFile);

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public static void prepareReducerDockerFile() {
    try {
      List<String> lines =
              Arrays.asList(
                      "FROM openjdk:8",
                      "COPY ./target/classes/com.atypon.nodes.ReducerNode.class /tmp",
                      "COPY ./temp/reducer/ReducerUtil.class /tmp",
                      "WORKDIR /tmp");
      Path file = Paths.get("reducerDockerFile");
      Files.write(file, lines, StandardCharsets.UTF_8);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}
