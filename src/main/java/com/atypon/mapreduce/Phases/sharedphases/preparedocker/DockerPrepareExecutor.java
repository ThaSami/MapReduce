package com.atypon.mapreduce.Phases.sharedphases.preparedocker;

import com.atypon.gui.Main;
import com.atypon.mapreduce.Phases.PhaseExecutionFailed;
import com.atypon.utility.MapperImageUtil;
import com.atypon.workflow.Context;
import com.atypon.workflow.phase.Executor;

public class DockerPrepareExecutor implements Executor {
    @Override
    public Context execute(Context context) throws PhaseExecutionFailed {
        try {
            MapperImageUtil.prepareMapperCode(
                    context.getParam("mappingMethod"), context.getParam("customImports"));
            Main.appendText("Mapper Code Prepared Successfully\n");

            MapperImageUtil.prepareReducerCode(
                    context.getParam("reducingMethod"), context.getParam("customImports"));
            Main.appendText("Reducer Code Prepared Successfully\n");

        } catch (Exception e) {
            new PhaseExecutionFailed("Couldn't Write compile UserCode");
        }
        return context;
    }
}
