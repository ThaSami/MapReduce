package com.atypon.mapreduce.Phases.swarm.buildswarmcompose;

import com.atypon.gui.Main;
import com.atypon.mapreduce.Phases.PhaseExecutionFailed;
import com.atypon.utility.Constants;
import com.atypon.utility.MapperImageUtil;
import com.atypon.utility.SwarmUtils;
import com.atypon.workflow.Context;
import com.atypon.workflow.phase.Executor;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

public class SwarmComposeInitExecutor implements Executor {
    @Override
    public Context execute(Context context) throws PhaseExecutionFailed {
        try {
            MapperImageUtil.prepareMapperCode(
                    context.getParam("mappingMethod"), context.getParam("customImports"));
            Main.appendText("Mapper Code Prepared Successfully\n");

            MapperImageUtil.prepareReducerCode(
                    context.getParam("reducingMethod"), context.getParam("customImports"));
            Main.appendText("Reducer Code Prepared Successfully\n");

            VelocityEngine velocityEngine = new VelocityEngine();
            velocityEngine.init();
            Template t = velocityEngine.getTemplate("src/main/resources/compose/docker-compose2.vm");
            StringWriter writer = new StringWriter();
            VelocityContext vcontext = context.getParam("compose-data");
            vcontext.put("IPCport", Constants.SWARM_IP_COLLECTOR_PORT);
            vcontext.put("ipcAddress", SwarmUtils.getManagerIP());
            vcontext.put("hostAddress", Constants.HOST_IP_ADDRESS);

            t.merge(vcontext, writer);

            Path file = Paths.get("docker-compose.yml");
            Files.write(file, Collections.singleton(writer.toString()), StandardCharsets.UTF_8);

            Main.appendText("docker-compose for swarm created Successfully\n");
        } catch (Exception e) {
            new PhaseExecutionFailed("Couldn't Write Compose Data");
        }
        return context;
    }
}
