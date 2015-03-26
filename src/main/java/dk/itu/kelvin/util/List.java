/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

/**
 * Minimal implementation of List interface.
 *
 * <p>
 * An ordered {@link Collection} (also known as a sequence). The user of this
 * interface has precise control over where in the list each {@code element}
 * is inserted. The user can access elements by their {@code integer index}
 * (position in the list), and search for elements in the list, either by
 * specifying an index position or {@code Object} to search for.
 *
 * <p>
 * Unlike sets, lists typically allow duplicate elements. More formally,
 * lists typically allow pairs of elements {@code e1} and {@code e2} such
 * that {@code e1.equals(e2)}, however they don't allow {@code null} elements.
 *
 * <p>
 * The {@code List} interface adds additional methods beyond those specified
 * in the {@link Collection} interface. Here methods for
 * {@link #indexOf(Object)}, {@link #get(int)} and {@link #remove(int)} are
 * added, even though there isn't any implementation of it.
 *
 * @param <E> The type of elements stored within the list.
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
   * Add an element to the list at the specified index.
   *
   * @param index   The index at which to add the element.
   * @param element The element to add to the list.
   * @return        A boolean indicating whether or not the list changed as a
   *                result of the call.
   */
  boolean add(final int index, final E element);

  /**
   * Replace the element at the specified index with another element.
   *
   * @param index   The index of the element to replace.
   * @param element The element to add to the specified index.
   * @return        The element previously at the specified index if any.
   */
  E set(final int index, final E element);

  /**
   * Remove an element from the list.
   *
   * @param index The index of the element to remove.
   * @return      The removed element.
   */
  E remove(final int index);
}
