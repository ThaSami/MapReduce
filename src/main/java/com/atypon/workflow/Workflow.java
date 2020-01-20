package com.atypon.workflow;

import com.atypon.workflow.phase.Phase;

public interface Workflow {
    void addPhase(Phase phase);

    void start(Context context);

    void shutdown();
}
