package com.atypon.workflow.phase;

import com.atypon.mapreduce.Phases.PhaseExecutionFailed;
import com.atypon.workflow.Context;

import java.util.Map;

public interface Executor {
    Context execute(Context context) throws PhaseExecutionFailed, InterruptedException;
}
