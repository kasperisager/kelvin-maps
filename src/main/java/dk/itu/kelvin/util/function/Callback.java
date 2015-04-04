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
   */
  void done();
}
