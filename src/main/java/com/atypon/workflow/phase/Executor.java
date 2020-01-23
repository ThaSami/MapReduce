package com.atypon.workflow.phase;

import com.atypon.mapreduce.Phases.PhaseExecutionFailed;
import com.atypon.workflow.Context;

public interface Executor {
  Context execute(Context context) throws PhaseExecutionFailed, InterruptedException;
}
