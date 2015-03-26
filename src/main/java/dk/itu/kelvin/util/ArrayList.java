/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// General utilities
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <h2>Performance optimized minimal ArrayList implementation</h2>
 * <h3>Implements</h3>
 * Resizeable array implementation of {@link List} interface. Implements all
 * optional List operations, and permits all elements not including
 * {@code null} or {@code collections} that are equal to {@code null}.
 * In addition to implementing the {@link List} interface this class provides
 * protected methods for manipulate the size of the array that is used
 * internally to store the list.
 *
 * <h3>Extends</h3>
 * <p>
 * The ArrayList extends {@link DynamicArray} that keeps track of the size of
 * the ArrayList and contains the methods for checking when the array should
 * resize.
 *
 * <h3>Runtime</h3>
 * <p>
 * The {@link #get(int)} and {@link #iterator()} operations runs in constant
 * time. The {@link #add(E)} operation runs in amortized constant time.
 * The {@link #resize(int)}, {@link #shiftLeft(int, int)} and
 * {@link #indexOf(Object)} contains operations runs in ~linear time.
 * The {@link #remove(int)} and {@link #remove(Object)} operations runs in
 * amortized ~linear time. The {@link #addAll(Collection<? extends E>)}
 * operation runs in linear time based on the size of the collection to be
 * added.
 *
 * <h3>Notice:</h3>
 * <p>
 * The ArrayList has an initial capacity of 2 unless an entire collection or
 * specific capacity is specified.
 *
 *
 * Remember new add, clear, trimToSize methods
 *
 * @param <E> The type of elements stored within the list.
 */
public class ArrayList<E> extends DynamicArray implements List<E> {
  /**
   * UID for identifying serialized objects.
   */
  private static final long serialVersionUID = 47;

  /**
   * internal element storage.
   */
  private E[] elements;

  /**
   * Initialize an array list with the default initial capacity.
   */
  public ArrayList() {
    this(2);
  }

  /**
   * Initialize an array list with the specified initial capacity.
   *
   * @param capacity The initial capacity of the array list.
   */
  @SuppressWarnings("unchecked")
  public ArrayList(final int capacity) {
    super(
      capacity,
      1.0f,   // Upper load factor
      2.0f,   // Upper resize factor
      0.25f,  // Lower load factor
      0.5f    // Lower resize factor
    );

    this.elements = (E[]) new Object[capacity];
  }

  /**
   * Initialize an array list using the elements of an existing collection.
   *
   * @param collection  The collection whose elements to initialize the list
   *                    with.
   */
  public ArrayList(final Collection<? extends E> collection) {
    this(collection.size());
    this.addAll(collection);
  }

  /**
   * Resize the internal storage of the list to the specified capacity.
   *
   * @param capacity The new capacity of the internal storage of the list.
   */
  @SuppressWarnings("unchecked")
  protected final void resize(final int capacity) {
    E[] elements = (E[]) new Object[capacity];

    for (int i = 0; i < this.size(); i++) {
      elements[i] = this.elements[i];
    }

    this.elements = elements;
  }

  /**
   * Shift the elements of the list left of the specified index.
   *
   * @see <a href="http://stackoverflow.com/questions/22716581/shift-array-
   * elements-to-left-in-java">http://stackoverflow.com/questions/22716581/
   * shift-array-elements-to-left-in-java</a>
   *
   * @param index The index to shift the elements towards.
   */
  private void shiftLeft(final int index) {
    int shifts = this.size() - index - 1;

    if (shifts <= 0) {
      return;
    }

    System.arraycopy(this.elements, index + 1, this.elements, index, shifts);
  }

  /**
   * Shift the elements of the list right between the specified indices.
   *
   * @see <a href="http://stackoverflow.com/questions/22716581/shift-array-
   * elements-to-left-in-java">http://stackoverflow.com/questions/22716581/
   * shift-array-elements-to-left-in-java</a>
   *
   * @param index The index to shift the elements away from.
   */
  private void shiftRight(final int index) {
    int shifts = this.size() - index;

    System.arraycopy(this.elements, index, this.elements, index + 1, shifts);
  }

  /**
   * Return the index of the specified element.
   *
   * @param element The value to look up the index of.
   * @return        The index of the specified element or -1 if the element
   *                wasn't found within the list.
   */
  public final int indexOf(final Object element) {
    if (element == null) {
      return -1;
    }

    for (int i = 0; i < this.size(); i++) {
      if (element.equals(this.elements[i])) {
        return i;
      }
    }

    return -1;
  }

  /**
   * Get a element by index from the list.
   *
   * @param index The index of the element to get.
   * @return      The element if found.
   */
  public final E get(final int index) {
    if (index < 0 || index >= this.size()) {
      throw new ArrayIndexOutOfBoundsException();
    }

    return this.elements[index];
  }

  /**
   * Check if the specified element exists within the list.
   *
   * @param element The element to check for.
   * @return        A boolean indicating whether or not the list contains the
   *                specified element.
   */
  public final boolean contains(final Object element) {
    if (element == null) {
      return false;
    }

    return this.indexOf(element) != -1;
  }

  /**
   * Add an element to the list.
   *
   * @param element The element to add to the list.
   * @return        A boolean indicating whether or not the list changed as a
   *                result of the call.
   */
  public final boolean add(final E element) {
    if (element == null) {
      return false;
    }

    this.elements[this.size()] = element;
    this.grow();

    return true;
  }

  /**
   * Add an element to the list at the specified index.
   *
   * <p>
   * If an element already exists at the specified index, the old element will
   * be shifted one index to the right alongside subsequent elements.
   *
   * @param index   The index at which to add the element.
   * @param element The element to add to the list.
   * @return        A boolean indicating whether or not the list changed as a
   *                result of the call.
   */
  public final boolean add(final int index, final E element) {
    if (index < 0 || index >= this.size()) {
      throw new ArrayIndexOutOfBoundsException();
    }

    if (element == null) {
      return false;
    }

    this.shiftRight(index);
    this.elements[index] = element;
    this.grow();

    return true;
  }

  /**
   * Add a collection of elements to the list.
   *
   * @param elements  The elements to add to the list.
   * @return          A boolean indicating whether or not the list changed as a
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
   * Replace the element at the specified index with another element.
   *
   * @param index   The index of the element to replace.
   * @param element The element to add to the specified index.
   * @return        The element previously at the specified index if any.
   */
  public final E set(final int index, final E element) {
    if (index < 0 || index >= this.size()) {
      throw new ArrayIndexOutOfBoundsException();
    }

    if (element == null) {
      return null;
    }

    E old = this.elements[index];

    this.elements[index] = element;

    return old;
  }

  /**
   * Remove an element from the list.
   *
   * <p>
   * When removing an element, any subsequent elements to the right of the
   * removed element will be shifted one index to the left.
   *
   * @param index The index of the element to remove.
   * @return      The removed element.
   */
  public final E remove(final int index) {
    if (index < 0 || index >= this.size()) {
      throw new ArrayIndexOutOfBoundsException();
    }

    E element = this.elements[index];

    this.shiftLeft(index);
    this.elements[this.size() - 1] = null;
    this.shrink();

    return element;
  }

  /**
   * Remove an element from the list.
   *
   * @param element The element to remove.
   * @return        A boolean inidicating whether or not the list contained the
   *                element to remove.
   */
  public final boolean remove(final Object element) {
    int index = this.indexOf(element);

    if (index == -1) {
      return false;
    }

    return this.remove(index) != null;
  }

  /**
   * Remove all elements from the list.
   *
   * <p>
   * <b>NB:</b> Unlike Java's Collections, this operation will actually reset
   * the internal storage of the list to its initial capacity instead of simply
   * nullifying all elements.
   */
  @SuppressWarnings("unchecked")
  public final void clear() {
    this.elements = (E[]) new Object[this.initialCapacity()];
    this.reset();
  }

  /**
   * Get an array containing the elements of the list.
   *
   * <p>
   * The returned array will contain no references to the original elements and
   * is therefore safe for modification.
   *
   * @return An array containing the elements of the list.
   */
  public final Object[] toArray() {
    return Arrays.copyOf(this.elements, this.size());
  }

  /**
   * Trim the size of the internal storage to the actual size of the list.
   */
  public final void trimToSize() {
    // Bail out if we've already reached the capacity of the internal storage.
    // In this case there won't be anything to trim.
    if (this.elements.length >= this.size()) {
      return;
    }

    // We add an extra slot for the next element addition since we call
    // shrink() after having added an element to the array.
    this.elements = Arrays.copyOf(this.elements, this.size() + 1);
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
       * Keep track of the last element that was returned.
       */
      private int last = -1;

      /**
       * Check if there are elements left to iterate over.
       *
       * @return  A boolean indicating whether or not there are elements left
       *          to iterate over.
       */
      public boolean hasNext() {
        return this.i < ArrayList.this.size();
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

        this.last = this.i++;

        return ArrayList.this.elements[this.last];
      }

      /**
       * The the last returned element.
       */
      public void remove() {
        if (this.last < 0) {
          throw new IllegalStateException();
        }

        ArrayList.this.remove(this.last);
        this.i = this.last;
        this.last = -1;
      }
    };
  }
}
