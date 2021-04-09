package com.github.schuettec.testutils.concurrent;

/**
 * An interface for running tasks to confirm a specific point of execution and wait for the signal to resume execution.
 */
public interface Confirmable {
  /**
   * Called by a task to confirm a certain point of execution and wait for a signal to resume execution.
   */
  public void confirmAndWait();
}
