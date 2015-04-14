/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.thread;

// Concurrency utilities
import java.util.concurrent.CompletableFuture;

// JUnit annotations
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

// JUnit assertions
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * {@link TaskQueue} test suite.
 */
public final class TaskQueueTest {
  /**
   * Start up the task queue before each test.
   */
  @Before
  public void before() {
    TaskQueue.startup();
  }

  /**
   * Shut down the task queue after each test.
   */
  @After
  public void after() {
    TaskQueue.shutdown();
  }

  /**
   * Test execution of tasks queued in the task queue.
   *
   * @throws Exception In case of an error.
   */
  @Test
  public void testTaskExecution() throws Exception {
    CompletableFuture<Boolean> success = new CompletableFuture<>();

    TaskQueue.run(() -> success.complete(true));

    assertTrue(success.get());
  }

  /**
   * Test execution of queued grouped tasks in the task queue.
   *
   * @throws Exception In case of an error.
   */
  @Test
  public void testTaskGroupExecution() throws Exception {
    CompletableFuture<Boolean> success1 = new CompletableFuture<>();
    CompletableFuture<Boolean> success2 = new CompletableFuture<>();
    CompletableFuture<Boolean> success3 = new CompletableFuture<>();
    CompletableFuture<Boolean> success4 = new CompletableFuture<>();

    TaskQueue.register("group-1", () -> success1.complete(true));
    TaskQueue.register("group-1", () -> success2.complete(true));

    TaskQueue.register("group-2",
      () -> success3.complete(true),
      () -> success4.complete(true)
    );

    // We haven't started any of the groups yet so none of the futures should be
    // done yet. This ensures that things actually happen asynchronously.
    assertFalse(success1.isDone());
    assertFalse(success2.isDone());
    assertFalse(success3.isDone());
    assertFalse(success4.isDone());

    TaskQueue.start("group-1");
    TaskQueue.start("group-2");

    assertTrue(success1.get());
    assertTrue(success2.get());
    assertTrue(success3.get());
    assertTrue(success4.get());
  }
}
