package com.atypon.workflow.phase;

public interface Phase {
  Executor getExecutor();

  Rollback getRollback();
}
