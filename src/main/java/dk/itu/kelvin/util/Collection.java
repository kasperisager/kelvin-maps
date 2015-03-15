/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

/**
 * Collection interface.
 *
 * @param <E> The type of elements stored within the collection.
 *
 * @version 1.0.0
 */
public interface Collection<E> extends Iterable<E> {
  /**
   * Get the size of the collection.
   *
   * @return The size of the collection.
   */
  int size();

  /**
   * Check if the collection is empty.
   *
   * @return A boolean indicating whether or not the collection is empty.
   */
  boolean isEmpty();

  /**
   * Add an element to the collection.
   *
   * @param element The element to add to the collection.
   * @return        A boolean indicating whether or not the collection changed
   *                because of the call.
   */
  boolean add(final E element);

  /**
   * Check if the specified element exists within the collection.
   *
   * @param element The element to check for.
   * @return        A boolean indicating whether or not the collection contains
   *                the specified element.
   */
  boolean contains(final Object element);

  /**
   * Remove an element from the collection.
   *
   * @param element The element to remove.
   * @return        A boolean inidicating whether or not the collection
   *                contained the element to remove.
   */
  boolean remove(final Object element);
}
