package utility;

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
                      "COPY ./target/classes/MapperNode.class /tmp",
                      "COPY ./temp/mapper/MapperUtil.class /tmp",
                      "WORKDIR /tmp");
      Path file = Paths.get("mapperDockerFile");
      Files.write(file, lines, StandardCharsets.UTF_8);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public static void prepareDockerCompose(int numOfMappers, int numOfReducers) {
    String mapperReplicas = "       replicas: " + numOfMappers;
    String reducerReplicas = "       replicas: " + numOfReducers;
    try {
      List<String> lines =
              Arrays.asList(
                      "version: '3.7'",
                      "services:",
                      "  mappers:",
                      "     image: mapper",
                      "     expose:",
                      "        - '7777'",
                      "     entrypoint:",
                      "        - java",
                      "        - MapperNode",
                      "     deploy:",
                      mapperReplicas,
                      "  reducers:",
                      "     image: reducer",
                      "     expose:",
                      "        - '7777'",
                      "     entrypoint:",
                      "        - java",
                      "        - ReducerNode",
                      "     deploy:",
                      reducerReplicas);

      Path file = Paths.get("docker-dompose.yml");
      Files.write(file, lines, StandardCharsets.UTF_8);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}
