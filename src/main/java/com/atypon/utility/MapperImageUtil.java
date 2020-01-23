package com.atypon.utility;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class MapperImageUtil {

  private MapperImageUtil() {
  }

  public static void prepareMapperCode(String mapperMethod, String customImports) {
    String source =
            customImports
                    + " import java.io.*;\n"
                    + " public class MapperUtil { "
                    + mapperMethod
                    + " }";
    try {
      File root = new File("./temp");
      File sourceFile = new File(root, "mapper/MapperUtil.java");
      sourceFile.getParentFile().mkdirs();
      Files.write(sourceFile.toPath(), source.getBytes(StandardCharsets.UTF_8));

      FilesUtil.CompileJavaCode(sourceFile);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
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

  public static void prepareDockerCompose(int numOfMappers, int numOfReducers) {

    try {
      List<String> lines =
              Arrays.asList(
                      "version: '3.7'",
                      "services:",
                      "  mappers:",
                      "     build:",
                      "        context: .",
                      "        dockerfile: mapperDockerFile",
                      "     image: mapper",
                      "     expose:",
                      "        - '" + Constants.MAPPERS_FILE_RECEIVER_PORT + "'",
                      "        - '" + Constants.MAINSERVER_TO_MAPPERS_PORT + "'",
                      "        - '" + Constants.MAPPERS_TO_REDUCERS_PORT + "'",
                      "        - '" + Constants.MAIN_SERVER_PORT + "'",
                      "     entrypoint:",
                      "        - java",
                      "        - com.atypon.nodes.mappernode.MapperNode",
                      "     deploy:",
                      "        replicas: " + numOfMappers,
                      "  reducers:",
                      "     build:",
                      "        context: .",
                      "        dockerfile: reducerDockerFile",
                      "     image: reducer",
                      "     expose:",
                      "        - '" + Constants.MAPPERS_FILE_RECEIVER_PORT + "'",
                      "        - '" + Constants.MAPPERS_TO_REDUCERS_PORT + "'",
                      "        - '" + Constants.COLLECTOR_PORT + "'",
                      "     entrypoint:",
                      "        - java",
                      "        - com.atypon.nodes.ReducerNode",
                      "     deploy:",
                      "        replicas: " + numOfReducers,
                      "     depends_on:",
                      "        - mappers");

      Path file = Paths.get("docker-compose.yml");
      Files.write(file, lines, StandardCharsets.UTF_8);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}
