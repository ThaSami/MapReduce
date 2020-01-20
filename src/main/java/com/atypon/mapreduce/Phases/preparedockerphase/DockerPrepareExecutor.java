package com.atypon.mapreduce.Phases.preparedockerphase;

import com.atypon.mapreduce.Phases.PhaseExecutionFailed;
import com.atypon.gui.Main;
import com.atypon.utility.MapperImageUtil;
import com.atypon.utility.ReducerImageUtil;
import com.atypon.workflow.Context;
import com.atypon.workflow.phase.Executor;

public class DockerPrepareExecutor implements Executor {
    @Override
    public Context execute(Context context) throws PhaseExecutionFailed {

        MapperImageUtil.prepareMapperCode(context.getParam("mappingMethod"), context.getParam("customImports"));
        Main.appendText("Mapper Code Prepared Successfully\n");

        ReducerImageUtil.prepareReducerCode(context.getParam("reducingMethod"), context.getParam("customImports"));
        Main.appendText("Reducer Code Prepared Successfully\n");

        MapperImageUtil.prepareMapperDockerFile();
        Main.appendText("Mapper DockerFile Created Successfully\n");

        ReducerImageUtil.prepareReducerDockerFile();
        Main.appendText("Reducer DockerFile created Successfully\n");

        MapperImageUtil.prepareDockerCompose(context.getParam("numOfMappers"), context.getParam("numOfReducers"));
        Main.appendText("docker-compose created Successfully\n");
        return context;
    }
}
