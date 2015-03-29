/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// General utilities
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Minimal implementation of the hash set data structure.
 *
 * <p>
 * This class implements the {@link Set} interface. It makes no guarantees as to
 * the iteration order of the set. In particular, it does not guarantee that the
 * order will remain the same over time. This class doesn't permit null
 * elements.
 *
 * <p>
 * Methods {@link #resize(int)} takes linear time complexity, methods
 * {@link #indexOf(Object)}, {@link #contains} and {@link #iterator()} takes
 * constant time complexity, methods {@link #add(Object)} and
 * {@link #remove(Object)} takes amortized constant time Constructor
 * {@link #HashSet(Collection)} and {@link #addAll(Collection)} takes amortized
 * linear time.
 *
 * @param <E> The type of elements stored within the set.
 */
public class HashSet<E> implements Set<E> {
  /**
   * UID for identifying serialized objects.
   */
  private static final long serialVersionUID = 591;

  /**
   * Default initial capacity of the internal storage of the table.
   */
  private static final int DEFAULT_CAPACITY = 16;

  /**
   * The upper factor to use for resizing the internal storage of the table.
   *
   * <p>
   * When the number of entries in the table reaches this factor of the total
   * capacity of the internal storage, the storage is resized.
   */
  private static final float UPPER_LOAD_FACTOR = 0.5f;

  /**
   * The factor with which to grow the internal storage of the collection when
   * the upper threshold has been reached.
   */
  private static final float UPPER_RESIZE_FACTOR = 2f;

  /**
   * The lower factor to use for resizing the internal storage of the
   * collection.
   *
   * <p>
   * When the number of entries in the collection reaches this factor of the
   * total capacity of the internal storage, the storage is resized.
   */
  private static final float LOWER_LOAD_FACTOR = 0.125f;

  /**
   * The factor with which to shrink the internal storage of the collection when
   * the lower threshold has been reached.
   */
  private static final float LOWER_RESIZE_FACTOR = 0.5f;

  /**
   * The hash collision resolver to use.
   */
  private static final HashResolver RESOLVER = new LinearProbe();

  /**
   * The size of the hash set.
   */
  private int size;

  /**
   * Internal element storage.
   */
  private E[] elements;

  /**
   * Initialize a hash set with the default initial capacity.
   */
  public HashSet() {
    this(DEFAULT_CAPACITY);
  }

  /**
   * Initialize a hash set with the specified initial capacity.
   *
   * @param capacity The initial capacity of the hash set.
   */
  @SuppressWarnings("unchecked")
  public HashSet(final int capacity) {
    this.elements = (E[]) new Object[capacity];
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
   * Get the size of the hash set.
   *
   * @return The size of the hash set.
   */
  public final int size() {
    return this.size;
  }

  /**
   * Check if the hash table is empty.
   *
   * @return A boolean indicating whether or not the hash table is empty.
   */
  public final boolean isEmpty() {
    return this.size == 0;
  }

  /**
   * Resize the internal storage of the set to the specified capacity.
   *
   * @param capacity The new capacity of the internal storage of the set.
   */
  private void resize(final int capacity) {
    HashSet<E> temp = new HashSet<>(capacity);

    // For each of the elements in the current set, put them in the temporary
    // set, effectively recomputing their hashes.
    for (int i = 0; i < this.elements.length; i++) {
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

    return RESOLVER.resolve(element, this.elements);
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
      this.size++;

      if (this.size == (int) (this.elements.length * UPPER_LOAD_FACTOR)) {
        this.resize((int) (this.elements.length * UPPER_RESIZE_FACTOR));
      }

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
    this.size--;

    if (
      this.size > 0
      && this.size == (int) (this.elements.length * LOWER_LOAD_FACTOR)
    ) {
      this.resize((int) (this.elements.length * LOWER_RESIZE_FACTOR));
    }

    return true;
  }

  /**
   * Remove all elements from the set.
   *
   * <p>
   * <b>NB:</b> Unlike Java's Collections, this operation will actually reset
   * the internal storage of the set to its default capacity instead of simply
   * nullifying all elements.
   */
  @SuppressWarnings("unchecked")
  public final void clear() {
    this.elements = (E[]) new Object[DEFAULT_CAPACITY];
    this.size = 0;
  }

  /**
   * Get an array of elements contained within the set.
   *
   * @return An array of elements contained within the set.
   */
  public final Object[] toArray() {
    Object[] temp = new Object[this.size];

    Iterator<E> it = this.iterator();

    for (int i = 0; i < this.size; i++) {
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
        return n < HashSet.this.size;
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
