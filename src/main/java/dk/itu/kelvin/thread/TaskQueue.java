/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.thread;

// General utilities
import java.util.Map;
import java.util.Queue;

// Concurrency utilities
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

// JavaFX concurrency utilities
import javafx.concurrent.Task;

/**
 * Task queue class.
 */
public final class TaskQueue {
  /**
   * The size of the thread pool.
   *
   * <p>
   * The number of live threads will never exceed this number. Whenever a new
   * task is submitted to the task queue, the thread pool will wait for prior
   * tasks to finish before running the new one if no more slots are available
   * in the pool.
   */
  private static final int POOL_SIZE = 100;

  /**
   * Initialize the different fields of the task queue.
   *
   * <p>
   * If the task queue is never used, this statement will never run thus
   * ensuring that we don't allocate memory to things that are never used.
   */
  static {
    // Initialize the map containing the task groups. We use a concurrent hash
    // map since it will potentially be modified from multiple threads at the
    // same time.
    GROUPS = new ConcurrentHashMap<>();

    // Initialize the thread pool that will take care of task execution.
    POOL = Executors.newFixedThreadPool(POOL_SIZE);
  }

  /**
   * The thread pool that handles execution of tasks on separate threads.
   */
  private static final ExecutorService POOL;

  /**
   * The map containing the different groups mapped to their task queues.
   */
  private static final Map<String, Queue<FunctionalTask>> GROUPS;

  /**
   * Don't allow instantiation of the class.
   *
   * <p>
   * Since the class only contains static fields and methods, we never want to
   * instantiate the class. We therefore define a private constructor so that
   * noone can create instances of the class other than the class itself.
   *
   * <p>
   * NB: This does not make the class a singleton. In fact, there never exists
   * an instance of the class since not even the class instantiates itself.
   */
  private TaskQueue() {
    super();
  }

  /**
   * Register new task(s) belonging to the specified group.
   *
   * <p>
   * Tasks are registered in the following way using lambda functions:
   *
   * <pre>
   * TaskQueue.register("group1",
   *   () -&gt; {
   *     System.out.println("First task");
   *   },
   *   () -&gt; {
   *     System.out.println("Second task");
   *   },
   *   // ...
   * );
   * </pre>
   *
   * <p>
   * One or more tasks may be defined in a single {@code register()} call.
   *
   * @param group The group that the task(s) belongs to.
   * @param tasks The task(s) to perform.
   */
  public static void register(
    final String group,
    final FunctionalTask... tasks
  ) {
    if (group == null || tasks == null || tasks.length == 0) {
      return;
    }

    // Initialize the group if it hasn't been initialized yet.
    if (!GROUPS.containsKey(group)) {
      // A concurrent linked queue is used as the task queue will potentially be
      // modified from several threads at the same time. This will for example
      // be the case if a new task is added before `start()` has finished
      // running all the tasks in the queue.
      GROUPS.put(group, new ConcurrentLinkedQueue<FunctionalTask>());
    }

    Queue<FunctionalTask> queue = GROUPS.get(group);

    for (FunctionalTask task: tasks) {
      queue.add(task);
    }
  }

  /**
   * Run the specified task on a separate thread.
   *
   * <p>
   * Tasks can be run in the following way using lambda functions:
   *
   * <pre>
   * TaskQueue.run(() -&gt; {
   *   System.out.println("Task");
   * });
   * </pre>
   *
   * @param task The task to perform.
   */
  public static void run(final FunctionalTask task) {
    // Encapsulate the FunctionalTask in a JavaFX Task.
    Task<Void> runner = new Task<Void>() {
      @Override
      public Void call() {
        // Run the task.
        try {
          task.run();
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }

        // We're done here.
        return null;
      }
    };

    // Submit the task runner to the thread pool
    POOL.submit(runner);
  }

  /**
   * Start all tasks belonging to the specified group.
   *
   * @param group The group whose tasks to run.
   */
  public static void start(final String group) {
    if (group == null || !GROUPS.containsKey(group)) {
      return;
    }

    // Boot a new thread whose only purpose is to dequeue and run tasks from the
    // task queue. If there are a lot of tasks to run, this ensures that the
    // main thread isn't blocked.
    TaskQueue.run(() -> {
      // Get the task queue for the specified group.
      Queue<FunctionalTask> queue = GROUPS.get(group);

      // While there are tasks left in the queue, get the next one and run it.
      while (!queue.isEmpty()) {
        TaskQueue.run(queue.poll());
      }

      // Remove the group from the map of groups. No loitering!
      GROUPS.remove(group);
    });
  }

  /**
   * A functional interface for defining asynchronous tasks.
   */
  @FunctionalInterface
  public interface FunctionalTask {
    /**
     * Run the task.
     */
    void run();
  }
}
