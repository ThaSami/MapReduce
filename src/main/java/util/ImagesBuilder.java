package util;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImagesBuilder {


    private ImagesBuilder() {
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
            compileMapper(source);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    private static void compileMapper(String source) throws IOException {
        // Save source in .java file.
        File root = new File("./temp");
        File sourceFile = new File(root, "mapper/MapperUtil.java");
        sourceFile.getParentFile().mkdirs();
        Files.write(sourceFile.toPath(), source.getBytes(StandardCharsets.UTF_8));

        // Compile source file.
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        compiler.run(null, null, null, sourceFile.getPath());
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
            compileReducer(source);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    private static void compileReducer(String source) throws IOException {
        // Save source in .java file.
        File root = new File("./temp");
        File sourceFile = new File(root, "reducer/ReducerUtil.java");
        sourceFile.getParentFile().mkdirs();
        Files.write(sourceFile.toPath(), source.getBytes(StandardCharsets.UTF_8));

        // Compile source file.
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        compiler.run(null, null, null, sourceFile.getPath());
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

}
