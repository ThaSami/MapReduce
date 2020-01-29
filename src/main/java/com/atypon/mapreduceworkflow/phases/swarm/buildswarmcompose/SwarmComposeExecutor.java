package com.atypon.mapreduceworkflow.phases.swarm.buildswarmcompose;

import com.atypon.gui.Main;
import com.atypon.mapreduceworkflow.phases.PhaseExecutionFailed;
import com.atypon.utility.Constants;
import com.atypon.utility.CodesUtil;
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

public class SwarmComposeExecutor implements Executor {
  @Override
  public Context execute(Context context) throws PhaseExecutionFailed {
    try {

      VelocityEngine velocityEngine = new VelocityEngine();
      velocityEngine.init();
      Template t = velocityEngine.getTemplate("src/main/resources/compose/Swarm-docker-compose.vm");
      StringWriter writer = new StringWriter();

      VelocityContext vcontext = context.getParam("compose-data");
      vcontext.put("ForwarderPort", Constants.SWARM_FORWARDER_PORT);
      vcontext.put("ForwarderAddress", SwarmUtils.getManagerIP());
      vcontext.put("hostAddress", Constants.MAIN_SERVER_IP);

      t.merge(vcontext, writer);

      Path file = Paths.get("docker-compose.yml");
      Files.write(file, Collections.singleton(writer.toString()), StandardCharsets.UTF_8);

      Main.appendText("docker-compose for swarm created Successfully\n");

    } catch (Exception e) {
      throw new PhaseExecutionFailed("Couldn't Write Compose Data");
    }
    return context;
  }
}
