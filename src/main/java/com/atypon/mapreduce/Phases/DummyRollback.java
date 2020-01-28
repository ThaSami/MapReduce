package com.atypon.mapreduce.Phases;

import com.atypon.workflow.Context;
import com.atypon.workflow.phase.Rollback;

public class DummyRollback implements Rollback {

  public static DummyRollback instance = new DummyRollback();

  private DummyRollback() {
  }

  public static DummyRollback getInstance() {
    return instance;
  }

  @Override
  public void rollback(Context context) {
  }
}
