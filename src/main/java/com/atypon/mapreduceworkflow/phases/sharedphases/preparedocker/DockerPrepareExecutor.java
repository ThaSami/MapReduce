package com.atypon.mapreduceworkflow.phases.sharedphases.preparedocker;

import com.atypon.gui.Main;
import com.atypon.mapreduceworkflow.phases.PhaseExecutionFailed;
import com.atypon.utility.CodesUtil;
import com.atypon.workflow.Context;
import com.atypon.workflow.phase.Executor;

public class DockerPrepareExecutor implements Executor {
    @Override
    public Context execute(Context context) throws PhaseExecutionFailed {
        try {
            CodesUtil.prepareMapperCode(
                    context.getParam("mappingMethod"), context.getParam("customImports"));
            Main.appendText("Mapper Code Prepared Successfully\n");

            CodesUtil.prepareReducerCode(
                    context.getParam("reducingMethod"), context.getParam("customImports"));
            Main.appendText("Reducer Code Prepared Successfully\n");

        } catch (Exception e) {
            throw new PhaseExecutionFailed("Couldn't compile user given code");
        }
        return context;
    }
}
