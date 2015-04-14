/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.store;

// General utilities
import java.util.List;

// I/O utilities
import java.io.File;
import java.io.Serializable;

// Functional utilities
import dk.itu.kelvin.util.function.Callback;

/**
 * Store class.
 *
 * @param <E> The type of elements contained within the store.
 * @param <C> The search criteria to use when looking up elements.
 */
public abstract class Store<E, C> implements Serializable {
  public final void save(final File file, final Callback callback) {
    throw new UnsupportedOperationException();
  }

  /**
   * Load a store saved to disk into the current store.
   *
   * @param file      The file containing the saved store.
   * @param callback  The callback to invoke once loading has finished.
   */
  public final void load(final File file, final Callback callback) {
    throw new UnsupportedOperationException();
  }

}
