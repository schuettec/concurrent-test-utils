package com.github.schuettec.testutils.concurrent;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

public class ConfirmAndResumeTest {

  @Test
  public void shouldTimeoutIfNoConfirm() throws InterruptedException {
    ConfirmAndResume execution = new ConfirmAndResume();

    AtomicBoolean seenException = new AtomicBoolean(false);
    Thread waiter = new Thread(() -> {
      try {
        execution.waitFor();
      } catch (InterruptedException e) {
        // Expected
        seenException.set(true);
      }
    });
    waiter.start();
    waiter.interrupt();
    waiter.join(1000);
    assertTrue(seenException.get());
  }

  @Test
  public void shouldRunIfAlreadyConfirmed() throws InterruptedException {
    ConfirmAndResume execution = new ConfirmAndResume();
    Confirmable confirmable = execution.getConfirmable();

    Thread confirmer = new Thread(() -> {
      confirmable.confirmAndWait();
    });
    confirmer.start();

    AtomicBoolean reachedEnd = new AtomicBoolean(false);

    Thread waiter = new Thread(() -> {
      execution.waitForUninterruptibly();
      reachedEnd.set(true);
    });
    waiter.start();

    waiter.join(1000);

    assertTrue(reachedEnd.get());
  }

  @Test
  public void shouldWaitForConfirmationAndThenResume() throws InterruptedException {
    ConfirmAndResume t1Execution = new ConfirmAndResume();
    Confirmable t1Confirmable = t1Execution.getConfirmable();

    ConfirmAndResume execution = new ConfirmAndResume();
    Confirmable confirmable = execution.getConfirmable();

    AtomicBoolean reachedEnd = new AtomicBoolean(false);

    Thread waiter = new Thread(() -> {
      t1Confirmable.confirmAndWait();
      execution.waitForUninterruptibly();
      reachedEnd.set(true);
    });
    waiter.start();
    t1Execution.waitFor()
        .resume();

    Thread confirmer = new Thread(() -> {
      confirmable.confirmAndWait();
    });
    confirmer.start();

    waiter.join(1000);

    assertTrue(reachedEnd.get());
  }
}
