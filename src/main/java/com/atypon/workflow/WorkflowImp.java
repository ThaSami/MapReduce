package com.atypon.workflow;

import com.atypon.workflow.phase.Executor;
import com.atypon.workflow.phase.Phase;

import java.util.ArrayList;
import java.util.List;

public class WorkflowImp implements Workflow {

  private List<Phase> phases;
  private volatile boolean isStarted = false;

  public WorkflowImp() {
    this.phases = new ArrayList<>();
  }

  @Override
  public void addPhase(Phase phase) {
    if (isStarted) {
      throw new RuntimeException("Can't add phase when the workflow is running");
    }
    phases.add(phase);
  }

  @Override
  public void start(Context context) {
    isStarted = true;
    for (Phase phase : phases) {
      Executor executor = phase.getExecutor();
      try {
        context = executor.execute(context);
      } catch (Exception e) {
        phase.getRollback().rollback(context);
      }
    }
  }

  @Override
  public void shutdown() {
  }
}
