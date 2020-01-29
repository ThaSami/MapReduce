package com.atypon.mapreduceworkflow.phases;

public class PhaseExecutionFailed extends Exception {
  public PhaseExecutionFailed() {
  }

  public PhaseExecutionFailed(String message) {
    super(message);
  }

  public PhaseExecutionFailed(String message, Throwable cause) {
    super(message, cause);
  }

  public PhaseExecutionFailed(Throwable cause) {
    super(cause);
  }

  public PhaseExecutionFailed(
          String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
