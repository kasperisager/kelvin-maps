/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// I/O utilities
import java.io.Serializable;

/**
 * General simplified implementation of Collection interface.
 *
 * <p>
 * The root interface in the collection hierarchy. A collection represents a
 * group of objects known as its elements. Some collections allow duplicate
 * elements while others do not. Some are ordered and some are unordered.
 *
 * <p>
 * All general-purpose {@code Collection} implementations (which typically
 * implement {@link Collection} indirectly through one of its subinterfaces)
 * should provide two "standard" constructors: a void (no arguments)
 * constructor, which creates an empty {@link Collection}, and a constructor
 * with a single argument of type {@code Collection} which creates a new
 * {@link Collection} with the same elements as its argument.
 * Some implementing classes contain an additional constructor with a single
 * argument of type {@code int}, which creates a new {@link Collection} with an
 * initial capacity of its argument.
 *
 * <p>
 * In effect, the second constructor allows the user to copy any collection,
 * producing an equivalent collection of the desired implementation type.
 * There is no way to enforce this convention (as interfaces cannot contain
 * constructors) but all of the general-purpose {@link Collection}
 * implementations in our own collection implementations comply.  The third
 * constructor allows the user to save some time setting an initial size of the
 * array, so that unnecessary time isn't used resizing the array.
 *
 * <p>
 * Some {@link Collection} implementations have restrictions on the elements
 * that they may contain. No implementing classes allow {@code null} elements.
 *
 * @param <E> The type of elements stored within the collection.
 */
public interface Collection<E> extends Iterable<E>, Serializable {
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
   *                as a result of the call.
   */
  boolean add(final E element);

  /**
   * Add a collection of elements to the collection.
   *
   * @param elements  The elements to add to the collection.
   * @return          A boolean indicating whether or not the collection changed
   *                  as a result of the call.
   */
  boolean addAll(final Collection<? extends E> elements);

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

  /**
   * Remove all elements from the collection.
   */
  void clear();

  /**
   * Get an array containing the elements of the collection.
   *
   * <p>
   * The returned array will contain no references to the original elements and
   * is therefore safe for modification.
   *
   * @return An array containing the elements of the collection.
   */
  Object[] toArray();
}
