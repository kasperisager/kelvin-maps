/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// General utilities
import java.util.Iterator;

/**
 * Abstract list class.
 *
 * @version 1.0.0
 */
public abstract class AbstractList<E> extends DynamicArray
  implements List<E> {
  /**
   * Initialize a list with the specified initial capacity.
   *
   * @param capacity The initial capacity of the list.
   */
  public AbstractList(final int capacity) {
    super(
      capacity,
      1.0f,   // Upper load factor
      2.0f,   // Upper resize factor
      0.25f,  // Lower load factor
      0.5f    // Lower resize factor
    );
  }

  /**
   * Shift the elements of an array left between the specified indices.
   *
   * @see <a href="http://stackoverflow.com/questions/22716581/shift-array-
   * elements-to-left-in-java">http://stackoverflow.com/questions/22716581/
   * shift-array-elements-to-left-in-java</a>
   *
   * @param array  The array whose elements to shift.
   * @param index  The index to shift the elements towards.
   * @param shifts The number of elements to shift.
   */
  protected void shiftLeft(
    final Object array,
    final int index,
    final int shifts
  ) {
    System.arraycopy(array, index + 1, array, index, shifts);
  }

  /**
   * Iterate over the elements of the list.
   *
   * @return An iterator over the elements of the list.
   */
  public final Iterator<E> iterator() {
    return new Iterator<E>() {
      /**
       * Keep track of the position within the array.
       */
      private int i = 0;

      /**
       * Check if there are elements left to iterate over.
       *
       * @return  A boolean indicating whether or not there are elements left
       *          to iterate over.
       */
      public boolean hasNext() {
        return i < AbstractList.this.size();
      }

      /**
       * Get the next element.
       *
       * @return The next element.
       */
      public E next() {
        return AbstractList.this.get(i++);
      }
    };
  }
}
