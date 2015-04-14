/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util.function;

/**
 * The {@link Callback} interface describes a functional callback that can be
 * executed after finishing an asynchronous task.
 */
@FunctionalInterface
public interface Callback {
  /**
   * Execute the callback.
   *
   * @throws Exception In case of an error while running the callback.
   */
  void call() throws Exception;
}
