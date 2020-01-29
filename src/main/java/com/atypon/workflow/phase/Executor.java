package com.atypon.workflow.phase;

import com.atypon.mapreduceworkflow.phases.PhaseExecutionFailed;
import com.atypon.workflow.Context;

public interface Executor {
  Context execute(Context context) throws PhaseExecutionFailed;
}
