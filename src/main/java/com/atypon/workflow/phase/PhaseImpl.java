package com.atypon.workflow.phase;

public class PhaseImpl implements Phase {

    private Executor executor;
    private Rollback rollback;

    public PhaseImpl(Executor executor, Rollback rollback) {
        this.executor = executor;
        this.rollback = rollback;
    }

    @Override
    public Executor getExecutor() {
        return executor;
    }

    @Override
    public Rollback getRollback() {
        return rollback;
    }
}
