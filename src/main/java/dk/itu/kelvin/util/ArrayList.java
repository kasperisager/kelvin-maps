/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// General utilities
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Performance optimized minimal array list implementation.
 *
 * <p>
 * Resizeable array implementation of {@link List} interface. Implements all
 * optional {@link List} operations, and permits all elements not including
 * {@code null} or {@code Collection Collections} that are equal to
 * {@code null}. In addition to implementing the {@link List} interface this
 * class provides protected methods for manipulating the size of the array that
 * is used internally to store the list.
 *
 * <p>
 * The {@link #get(int)} and {@link #iterator()} operations runs in constant
 * time. The {@link #add(Object)} operation runs in amortized constant time.
 * The {@link #resize(int)}, {@link #shiftLeft(int)} and
 * {@link #indexOf(Object)} contains operations runs in ~linear time.
 * The {@link #remove(int)} and {@link #remove(Object)} operations runs in
 * amortized ~linear time. The {@link #addAll(Collection)} operation runs in
 * linear time based on the size of the collection to be added.
 *
 * <p>
 * <b>Notice:</b> The {@link ArrayList} has an initial capacity of 2 unless an
 * entire {@link Collection} or a specific capacity is specified.
 *
 * @param <E> The type of elements stored within the list.
 */
public class ArrayList<E> implements List<E> {
  /**
   * UID for identifying serialized objects.
   */
  private static final long serialVersionUID = 47;

  /**
   * Default initial capacity of the internal storage of the collection.
   */
  private static final int DEFAULT_CAPACITY = 2;

  /**
   * The upper factor to use for resizing the internal storage of the
   * collection.
   *
   * <p>
   * When the number of entries in the collection reaches this factor of the
   * total capacity of the internal storage, the storage is resized.
   */
  private static final float UPPER_LOAD_FACTOR = 1.0f;

  /**
   * The factor with which to grow the internal storage of the collection when
   * the upper threshold has been reached.
   */
  private static final float UPPER_RESIZE_FACTOR = 2.0f;

  /**
   * The lower factor to use for resizing the internal storage of the
   * collection.
   *
   * <p>
   * When the number of entries in the collection reaches this factor of the
   * total capacity of the internal storage, the storage is resized.
   */
  private static final float LOWER_LOAD_FACTOR = 0.25f;

  /**
   * The factor with which to shrink the internal storage of the collection when
   * the lower threshold has been reached.
   */
  private static final float LOWER_RESIZE_FACTOR = 0.5f;

  /**
   * The size of the array list.
   */
  private int size;

  /**
   * Internal element storage.
   */
  private E[] elements;

  /**
   * Initialize an array list with the default initial capacity.
   */
  public ArrayList() {
    this(DEFAULT_CAPACITY);
  }

  /**
   * Initialize an array list with the specified initial capacity.
   *
   * @param capacity The initial capacity of the array list.
   */
  @SuppressWarnings("unchecked")
  public ArrayList(final int capacity) {
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
   * Get the size of the array list.
   *
   * @return The size of the array list.
   */
  public final int size() {
    return this.size;
  }

  /**
   * Check if the array list is empty.
   *
   * @return A boolean indicating whether or not the array list is empty.
   */
  public final boolean isEmpty() {
    return this.size == 0;
  }

  /**
   * Resize the internal storage of the list to the specified capacity.
   *
   * @param capacity The new capacity of the internal storage of the list.
   */
  @SuppressWarnings("unchecked")
  private void resize(final int capacity) {
    E[] elements = (E[]) new Object[capacity];

    for (int i = 0; i < this.size; i++) {
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
    int shifts = this.size - index - 1;

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
    int shifts = this.size - index;

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

    for (int i = 0; i < this.size; i++) {
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
    if (index < 0 || index >= this.size) {
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

    if (this.size == (int) (this.elements.length * UPPER_LOAD_FACTOR)) {
      this.resize((int) (this.elements.length * UPPER_RESIZE_FACTOR));
    }

    this.elements[this.size++] = element;

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
    if (index < 0 || index > this.size) {
      throw new ArrayIndexOutOfBoundsException();
    }

    if (element == null) {
      return false;
    }

    if (this.size == (int) (this.elements.length * UPPER_LOAD_FACTOR)) {
      this.resize((int) (this.elements.length * UPPER_RESIZE_FACTOR));
    }

    this.shiftRight(index);
    this.elements[index] = element;
    this.size++;

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
    if (index < 0 || index > this.size) {
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
    if (index < 0 || index >= this.size) {
      throw new ArrayIndexOutOfBoundsException();
    }

    E element = this.elements[index];

    this.shiftLeft(index);
    this.elements[--this.size] = null;

    if (
      this.size > 0
      && this.size == (int) (this.elements.length * LOWER_LOAD_FACTOR)
    ) {
      this.resize((int) (this.elements.length * LOWER_RESIZE_FACTOR));
    }

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
   * the internal storage of the list to its default capacity instead of simply
   * nullifying all elements.
   */
  @SuppressWarnings("unchecked")
  public final void clear() {
    this.elements = (E[]) new Object[DEFAULT_CAPACITY];
    this.size = 0;
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
    return Arrays.copyOf(this.elements, this.size);
  }

  /**
   * Trim the size of the internal storage to the actual size of the list.
   */
  public final void trimToSize() {
    // Bail out if we've already reached the capacity of the internal storage.
    // In this case there won't be anything to trim.
    if (this.elements.length >= this.size) {
      return;
    }

    this.elements = Arrays.copyOf(this.elements, this.size);
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
        return this.i < ArrayList.this.size;
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
