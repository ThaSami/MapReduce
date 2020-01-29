package com.atypon.mapreduceworkflow.phases.sharedphases.splitphase;

import com.atypon.gui.Main;
import com.atypon.mapreduceworkflow.phases.PhaseExecutionFailed;
import com.atypon.utility.FilesUtil;
import com.atypon.workflow.Context;
import com.atypon.workflow.phase.Executor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class SplitExecutor implements Executor {

  @Override
  public Context execute(Context context) throws PhaseExecutionFailed {

    if (FilesUtil.checkIfNotExist(context.getParam("txtFilePath"))) {
      Main.appendText("TEXT FILE NOT FOUND, make sure it is readable");
      throw new PhaseExecutionFailed("TEXT File Not Found");
    }

    // create temp directory.
    File src = new File((String) context.getParam("txtFilePath"));
    File root = new File("./temp");
    File dst = new File(root, "./Data/Data.txt");
    dst.getParentFile().mkdirs();

    try {
      FilesUtil.copyToDir(src, dst);
    } catch (IOException e) {
      throw new PhaseExecutionFailed("Couldn't Copy The File");
    }


    try {
      ProcessBuilder processBuilder = new ProcessBuilder();
      File file = new File("./temp/Data/Data.txt");
      int size = (int) (file.length() / 1024) / (int) context.getParam("numOfMappers") + 1;

      StringBuilder splitCommand = new StringBuilder();
      splitCommand.append("cd ./temp/Data/ &&");
      splitCommand.append(" split -d -C ");
      splitCommand.append(size);
      splitCommand.append("k");
      splitCommand.append(" Data.txt");
      splitCommand.append(" map ");
      splitCommand.append(" && rm Data.txt");

      processBuilder.command("sh", "-c", splitCommand.toString());

      Process process = processBuilder.start();

      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

      String line;
      while ((line = reader.readLine()) != null) {
        System.out.println(line);
      }

      int exitCode = process.waitFor();
      System.out.println("\nSplitter Exited with error code : " + exitCode);

      //process failed to execute
      if (exitCode != 0) {
        throw new PhaseExecutionFailed("Couldn't split the file");
      }

      Main.appendText("Splitted Files\n");
    } catch (Exception e) {
      throw new PhaseExecutionFailed("Couldn't split the file");
    }


    return context;
  }
}
