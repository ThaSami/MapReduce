package com.atypon.mapreduce.Phases.sharedphases.splitphase;

import com.atypon.gui.Main;
import com.atypon.mapreduce.Phases.PhaseExecutionFailed;
import com.atypon.utility.FilesUtil;
import com.atypon.workflow.Context;
import com.atypon.workflow.phase.Executor;

import java.io.IOException;

public class SplitExecutor implements Executor {

  @Override
  public Context execute(Context context) throws PhaseExecutionFailed {

    if (FilesUtil.checkIfNotExist(context.getParam("txtFilePath"))) {
      Main.appendText("TEXT FILE NOT FOUND, make sure it is readable");
      throw new PhaseExecutionFailed("TEXT File Not Found");
    }

    try {
      FilesUtil.copy(context.getParam("txtFilePath"), "./Data/Data.txt");
    } catch (IOException e) {
      throw new PhaseExecutionFailed("TEXT File Not Found");
    }

    FilesUtil.splitter("./temp/Data/Data.txt", context.getParam("numOfMappers"));
    Main.appendText("Splitted Files\n");

    return context;
  }
}
