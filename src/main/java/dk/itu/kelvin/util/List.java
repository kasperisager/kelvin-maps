/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

/**
 * List interface.
 *
 * @param <E> The type of elements stored within the list.
 *
 * @version 1.0.0
 */
public interface List<E> extends Collection<E> {
  /**
   * Return the index of the specified element.
   *
   * @param element The value to look up the index of.
   * @return        The index of the specified element or -1 if the element
   *                wasn't found within the list.
   */
  int indexOf(final Object element);

  /**
   * Get a element by index from the list.
   *
   * @param index The index of the element to get.
   * @return      The element if found.
   */
  E get(final int index);

  /**
   * Remove an element from the list.
   *
   * @param index The index of the element to remove.
   * @return      The removed element.
   */
  E remove(final int index);
}
