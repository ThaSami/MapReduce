package com.atypon.mapreduce.Phases.preparedockerphase;

import com.atypon.gui.Main;
import com.atypon.mapreduce.Phases.PhaseExecutionFailed;
import com.atypon.utility.MapperImageUtil;
import com.atypon.workflow.Context;
import com.atypon.workflow.phase.Executor;

public class DockerPrepareExecutor implements Executor {
  @Override
  public Context execute(Context context) throws PhaseExecutionFailed {

    MapperImageUtil.prepareMapperCode(
            context.getParam("mappingMethod"), context.getParam("customImports"));
    Main.appendText("Mapper Code Prepared Successfully\n");

    MapperImageUtil.prepareReducerCode(
            context.getParam("reducingMethod"), context.getParam("customImports"));
    Main.appendText("Reducer Code Prepared Successfully\n");

    MapperImageUtil.prepareDockerCompose(
            context.getParam("numOfMappers"), context.getParam("numOfReducers"));
    Main.appendText("docker-compose created Successfully\n");
    return context;
  }
}
