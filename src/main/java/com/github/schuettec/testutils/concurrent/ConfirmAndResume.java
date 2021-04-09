package com.github.schuettec.testutils.concurrent;

import java.util.concurrent.Semaphore;

/**
 * Sometimes when testing concurrency you want an asynchronous task to run and pause when a certain point of execution
 * is reached.
 * This class helps to control the execution of an asynchronous task. This class provides a {@link Confirmable} that can
 * be used by asynchronous tasks to paus and wait for the signal to resume.
 * The test, holding the {@link ConfirmAndResume} can wait for the asynchronous task to reach the desired execution
 * point and control when the asynchronous task might resume execution.
 */
public class ConfirmAndResume {

  private Semaphore run = new Semaphore(0);
  private Semaphore wait = new Semaphore(0);

  /**
   * @return Returns the {@link Confirmable}.
   */
  public Confirmable getConfirmable() {
    return new Confirmable() {

      @Override
      public void confirmAndWait() {
        run.release();
        try {
          wait.acquire();
        } catch (InterruptedException e) {
          Thread.interrupted();
          throw new RuntimeException("Confirmable was interrupted.", e);
        }
      }
    };

  }

  /**
   * Wait for holder of {@link Confirmable} to confirm. This method blocks uninteruptibly.
   */
  public ConfirmAndResume waitForUninterruptibly() {
    run.acquireUninterruptibly();
    return this;
  }

  /**
   * Wait for holder of {@link Confirmable} to confirm. This method might be interrupted, see
   * {@link Thread#interrupt()}.
   *
   * @return
   */
  public ConfirmAndResume waitFor() throws InterruptedException {
    run.acquire();
    return this;
  }

  /**
   * Signals the holder of {@link Confirmable} to resume execution after confirmation.
   */
  public void resume() {
    wait.release();
  }

  /**
   * Releases all blocks and aborts the confirm and resume process.
   */
  public void abort() {
    run.release();
    wait.release();
  }
}
