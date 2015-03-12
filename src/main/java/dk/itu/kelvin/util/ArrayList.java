/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

/**
 * Array list class.
 *
 * @param <E> The type of elements stored within the list.
 *
 * @version 1.0.0
 */
public class ArrayList<E> extends AbstractList<E> {
  /**
   * UID for identifying serialized objects.
   */
  private static final long serialVersionUID = 47;

  /**
   * Internal element storage.
   */
  private E[] elements;

  /**
   * Initialize an array list with the specified initial capacity.
   *
   * @param capacity The initial capacity of the array list.
   */
  @SuppressWarnings("unchecked")
  public ArrayList(final int capacity) {
    super(capacity);

    this.elements = (E[]) new Object[this.capacity()];
  }

  /**
   * Initialize an array list with the default initial capacity.
   */
  public ArrayList() {
    this(2);
  }

  /**
   * Resize the internal storage of the list to the specified capacity.
   *
   * @param capacity The new capacity of the internal storage of the list.
   */
  @SuppressWarnings("unchecked")
  protected final void resize(final int capacity) {
    E[] temp = (E[]) new Object[capacity];

    for (int i = 0; i < this.size(); i++) {
      temp[i] = this.elements[i];
    }

    this.elements = temp;
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
      return null;
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
    return this.indexOf(element) != -1;
  }

  /**
   * Add an element to the list.
   *
   * @param element The element to add to the list.
   * @return        {@code true}
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
   * Remove an element from the list.
   *
   * @param index The index of the element to remove.
   * @return      The removed element.
   */
  public final E remove(final int index) {
    if (index < 0 || index >= this.size()) {
      return null;
    }

    E element = this.elements[index];

    int moved = this.size() - index - 1;

    if (moved > 0) {
      this.shiftLeft(this.elements, index, moved);
    }

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
    return this.remove(this.indexOf(element)) != null;
  }
}
