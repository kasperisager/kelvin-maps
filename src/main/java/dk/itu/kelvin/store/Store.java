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
  /**
   * Remove an element from the store.
   *
   * @param element The element to remove from the store.
   */
  public abstract void remove(final E element);

  /**
   * Look up elements within the store based on the specified criteria.
   *
   * @param criteria  The criteria to look up elements based on.
   * @return          A list of elements matching the search criteria.
   */
  public abstract List<E> search(final C criteria);

  /**
   * Save the current store to the specified file.
   *
   * @param file      The file to save the store to.
   * @param callback  The callback to invoke once saving has finished.
   */
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
