/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// General utilities
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Hash set class.
 *
 * @param <E> The type of elements stored within the set.
 */
public class HashSet<E> extends DynamicHashArray implements Set<E> {
  /**
   * UID for identifying serialized objects.
   */
  private static final long serialVersionUID = 591;

  /**
   * The hash collision resolver to use.
   */
  private static final HashResolver RESOLVER = new QuadraticProbe();

  /**
   * Internal element storage.
   */
  private E[] elements;

  /**
   * Initialize a hash set with the default initial capacity.
   */
  public HashSet() {
    this(16);
  }

  /**
   * Initialize a hash set with the specified initial capacity.
   *
   * @param capacity The initial capacity of the hash set.
   */
  @SuppressWarnings("unchecked")
  public HashSet(final int capacity) {
    super(
      capacity,
      0.5f,   // Upper load factor
      2f,     // Upper resize factor
      0.125f, // Lower load factor
      0.5f    // Lower resize factor
    );

    this.elements = (E[]) new Object[this.capacity()];
  }

  /**
   * Initialize a hash set using the elements of an existing collection.
   *
   * @param collection  The collection whose elements to initialize the hash set
   *                    with.
   */
  public HashSet(final Collection<? extends E> collection) {
    this(collection.size());
    this.addAll(collection);
  }

  /**
   * Resize the internal storage of the set to the specified capacity.
   *
   * @param capacity The new capacity of the internal storage of the set.
   */
  protected final void resize(final int capacity) {
    HashSet<E> temp = new HashSet<>(capacity);

    // For each of the elements in the current set, put them in the temporary
    // set, effectively recomputing their hashes.
    for (int i = 0; i < this.capacity(); i++) {
      if (this.elements[i] != null) {
        temp.add(this.elements[i]);
      }
    }

    // Point the elements of the current set to the elements of the temporary,
    // rehashed set.
    this.elements = temp.elements;
  }

  /**
   * Return the index of the specified element.
   *
   * @param element The element to look up the index of.
   * @return        The index of the specified elements.
   */
  private int indexOf(final Object element) {
    if (element == null) {
      return -1;
    }

    return RESOLVER.resolve(this.hash(element), element, this.elements);
  }

  /**
   * Check if the specified element exists within the set.
   *
   * @param element The element to check for.
   * @return        A boolean indicating whether or not the set contains the
   *                specified element.
   */
  public final boolean contains(final Object element) {
    if (element == null) {
      return false;
    }

    return this.elements[this.indexOf(element)] != null;
  }

  /**
   * Add an element to the set.
   *
   * @param element The element to add to the set.
   * @return        A boolean indicating whether or not the set changed as a
   *                result of the call.
   */
  public final boolean add(final E element) {
    if (element == null) {
      return false;
    }

    int i = this.indexOf(element);

    if (this.elements[i] != null && this.elements[i].equals(element)) {
      return false;
    }
    else {
      this.elements[i] = element;
      this.grow();

      return true;
    }
  }

  /**
   * Add a collection of elements to the set.
   *
   * @param elements  The elements to add to the set.
   * @return          A boolean indicating whether or not the set changed as a
   *                  result of the call.
   */
  public final boolean addAll(final Collection<? extends E> elements) {
    if (elements == null || elements.isEmpty()) {
      return false;
    }

    boolean changed = false;

    for (E element: elements) {
      changed = this.add(element) || changed;
    }

    return changed;
  }

  /**
   * Remove a element from the set.
   *
   * @param element The element to remove.
   * @return        A boolean inidicating whether or not the set contained the
   *                element to remove.
   */
  public final boolean remove(final Object element) {
    if (element == null) {
      return false;
    }

    if (!this.contains(element)) {
      return false;
    }

    int i = this.indexOf(element);

    this.elements[i] = null;
    this.shrink();

    return true;
  }

  /**
   * Remove all elements from the set.
   *
   * <p>
   * <b>NB:</b> Unlike Java's Collections, this operation will actually reset
   * the internal storage of the set to its initial capacity instead of simply
   * nullifying all elements.
   */
  @SuppressWarnings("unchecked")
  public final void clear() {
    this.elements = (E[]) new Object[this.initialCapacity()];
    this.reset();
  }

  /**
   * Get an array of elements contained within the set.
   *
   * @return An array of elements contained within the set.
   */
  public final Object[] toArray() {
    Object[] temp = new Object[this.size()];

    Iterator<E> it = this.iterator();

    for (int i = 0; i < this.size(); i++) {
      temp[i] = it.next();
    }

    return temp;
  }

  /**
   * Iterate over the elements of the set.
   *
   * @return An iterator over the elements of the set.
   */
  public final Iterator<E> iterator() {
    return new Iterator<E>() {
      /**
       * Keep track of the position within the array.
       */
      private int i = -1;

      /**
       * Keep track of how many elements we've iterated over.
       */
      private int n = 0;

      /**
       * Check if there are elements left to iterate over.
       *
       * @return  A boolean indicating whether or not there are elements left
       *          to iterate over.
       */
      public boolean hasNext() {
        return n < HashSet.this.size();
      }

      /**
       * Get the next element.
       *
       * @return The next element.
       */
      public E next() {
        if (!this.hasNext()) {
          throw new NoSuchElementException();
        }

        n++;

        while (HashSet.this.elements[++this.i] == null) {
          continue;
        }

        return HashSet.this.elements[this.i];
      }
    };
  }
}
