package com.atypon.utility;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.IllegalFormatCodePointException;

public class CodesUtil {

  private CodesUtil() {}

  public static void prepareMapperCode(String mapperMethod, String customImports)
      throws IOException {
    String source =
        customImports
            + " import java.io.*;\n"
            + " public class MapperUtil { "
            + mapperMethod
            + " }";

    File root = new File("./temp");
    File sourceFile = new File(root, "mapper/MapperUtil.java");
    sourceFile.getParentFile().mkdirs();
    Files.write(sourceFile.toPath(), source.getBytes(StandardCharsets.UTF_8));

    compileJavaCode(sourceFile);
  }

  public static void prepareReducerCode(String reducerMethod, String customImports)
      throws IOException {
    String source =
        customImports
            + "import java.io.*;\n"
            + "public class ReducerUtil { "
            + reducerMethod
            + " }";
    File root = new File("./temp");
    File sourceFile = new File(root, "reducer/ReducerUtil.java");
    sourceFile.getParentFile().mkdirs();
    Files.write(sourceFile.toPath(), source.getBytes(StandardCharsets.UTF_8));
    compileJavaCode(sourceFile);
  }

  public static void compileJavaCode(File sourceFile) throws IllegalFormatCodePointException {

    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    int resultCode = compiler.run(null, null, null, sourceFile.getPath());
    if (resultCode != 0) {
      throw new IllegalFormatCodePointException(1);
    }
  }
}
