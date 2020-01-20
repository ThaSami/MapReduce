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

  public static void prepareMapperDockerFile() {
    try {
      List<String> lines =
              Arrays.asList(
                      "FROM openjdk:8",
                      "COPY ./target/classes/com/atypon/nodes /",
                      "COPY ./temp/mapper/MapperUtil.class /tmp",
                      "WORKDIR /tmp");
      Path file = Paths.get("mapperDockerFile");
      Files.write(file, lines, StandardCharsets.UTF_8);
    } catch (IOException ex) {
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
                      "        - '" + Constants.MAPPERS_REDUCERADDRESS_RECEIVER_PORT + "'",
                      "        - '" + Constants.TREE_MAP_RECEIVER_PORT + "'",
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
                      "        - '" + Constants.TREE_MAP_RECEIVER_PORT + "'",
                      "        - '" + Constants.COLLECTOR_PORT + "'",
                      "        - '" + Constants.REDUCER_RECEIVER_PORT + "'",
                      "     entrypoint:",
                      "        - java",
                      "        - com.atypon.nodes.reducernode.ReducerNode",
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
