package utility;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.IllegalFormatCodePointException;
import java.util.List;

public class DockerImagesCreator {


    private DockerImagesCreator() {
    }

    public static void prepareMapperCode(String mapperMethod) {
        String source =
                "package mapper; "
                        + "import java.util.HashMap;\n"
                        + "import java.util.Map;\n "
                        + "import java.io.*;\n"
                        + "import java.util.TreeMap;\n"
                        + "import java.util.regex.Matcher;\n"
                        + "import java.util.regex.Pattern;"
                        + "public class MapperUtil { "
                        + mapperMethod
                        + " }";
        try {
            File root = new File("./temp");
            File sourceFile = new File(root, "mapper/MapperUtil.java");
            sourceFile.getParentFile().mkdirs();
            Files.write(sourceFile.toPath(), source.getBytes(StandardCharsets.UTF_8));


            CompileJavaCode(sourceFile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    public static void prepareMapperDockerFile() {
        try {
            List<String> lines = Arrays.asList("FROM openjdk:8", "COPY ./target/classes/MapperNode.class /tmp", "COPY ./temp/mapper/MapperUtil.class /tmp", "WORKDIR /tmp");
            Path file = Paths.get("mapperDockerFile");
            Files.write(file, lines, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void prepareReducerCode(String reducerMethod) {
        String source =
                "package mapper; "
                        + "import java.util.HashMap;\n"
                        + "import java.util.Map;\n "
                        + "import java.io.*;\n"
                        + "import java.util.TreeMap;\n"
                        + "import java.util.regex.Matcher;\n"
                        + "import java.util.regex.Pattern;"
                        + "public class ReducerUtil { "
                        + reducerMethod
                        + " }";
        try {
            File root = new File("./temp");
            File sourceFile = new File(root, "reducer/ReducerUtil.java");
            Files.write(sourceFile.toPath(), source.getBytes(StandardCharsets.UTF_8));


            CompileJavaCode(sourceFile);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void prepareReducerDockerFile() {
        try {
            List<String> lines = Arrays.asList("FROM openjdk:8", "COPY ./target/classes/ReducerNode.class /tmp", "COPY ./temp/mapper/ReducerUtil.class /tmp", "WORKDIR /tmp");
            Path file = Paths.get("reducerDockerFile");
            Files.write(file, lines, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void prepareDockerCompose(int numOfMappers, int numOfReducers) {
        String mapperReplicas = "       replicas: " + numOfMappers;
        String reducerReplicas = "       replicas: " + numOfReducers;
        try {
            List<String> lines = Arrays.asList(
                    "version: '3.7'",
                    "services:",
                    "  mappers:",
                    "     image: mapper",
                    "     ports:",
                    "        - '7777'",
                    "     entrypoint:",
                    "        - java",
                    "        - MapperNode",
                    "     deploy:",
                    mapperReplicas,
                    "  reducers:",
                    "     image: reducer",
                    "     ports:",
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

    public static void CompileJavaCode(File sourceFile) {
        sourceFile.getParentFile().mkdirs();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int resultCode = compiler.run(null, null, null, sourceFile.getPath());
        if (resultCode != 0) {
            throw new IllegalFormatCodePointException(1);
        }
    }

}
