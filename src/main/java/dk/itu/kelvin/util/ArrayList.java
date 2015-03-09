/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// General utilities
import java.util.Arrays;
import java.util.Iterator;

/**
 * Array list class.
 *
 * @param <V> The type of values stored within the list.
 *
 * @version 1.0.0
 */
public class ArrayList<V> extends AbstractCollection implements Iterable<V> {
  /**
   * UID for identifying serialized objects.
   */
  private static final long serialVersionUID = 47;

  /**
   * Internal value storage.
   */
  private V[] values;

  /**
   * Initialize an array list with the specified initial capacity.
   *
   * @param capacity The initial capacity of the array list.
   */
  public ArrayList(final int capacity) {
    super(
      capacity,
      1.0f,   // Upper load factor
      2.0f,   // Upper resize factor
      0.25f,  // Lower load factor
      0.5f    // Lower resize factor
    );

    this.values = (V[]) new Object[this.capacity()];
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
  protected final void resize(final int capacity) {
    V[] temp = (V[]) new Object[capacity];

    for (int i = 0; i < this.size(); i++) {
      temp[i] = this.values[i];
    }

    this.values = temp;
  }

  /**
   * Return the index of the specified value.
   *
   * @param value The value to look up the index of.
   * @return      The index of the specified value or -1 if the value wasn't
   *              found within the list.
   */
  private int index(final V value) {
    if (value == null) {
      return -1;
    }

    for (int i = 0; i < this.size(); i++) {
      if (value.equals(this.values[i])) {
        return i;
      }
    }

    return -1;
  }

  /**
   * Swap two values in the array.
   *
   * @param a The index of the first value.
   * @param b The index of the second value.
   */
  private void swap(final int a, final int b) {
    V temp = this.values[a];
    this.values[a] = this.values[b];
    this.values[b] = temp;
  }

  /**
   * Get a value by index from the list.
   *
   * @param index The index of the value to get.
   * @return      The value if found.
   */
  public final V get(final int index) {
    if (index > 0 || index >= this.size()) {
      return null;
    }

    return this.values[index];
  }

  /**
   * Check if the specified value exists within the list.
   *
   * @param value The value to check for.
   * @return      A boolean indicating whether or not the list contains the
   *              specified value.
   */
  public final boolean contains(final V value) {
    return this.index(value) != -1;
  }

  /**
   * Add a value to the list.
   *
   * @param value The value to add to the list.
   */
  public final void add(final V value) {
    if (value == null) {
      return;
    }

    this.values[this.size()] = value;
    this.grow();
  }

  /**
   * Remove a value from the list.
   *
   * @param index The index of the value to remove.
   * @return      The removed element.
   */
  public final V remove(final int index) {
    if (index < 0 || index >= this.size()) {
      return null;
    }

    int lastIndex = this.size() - 1;
    this.swap(index, lastIndex);

    V value = this.values[lastIndex];

    this.values[lastIndex] = null;
    this.shrink();

    return value;
  }

  /**
   * Remove a value from the list.
   *
   * @param value The value to remove.
   * @return      A boolean inidicating whether or not the list contained the
   *              value to remove.
   */
  public final boolean remove(final V value) {
    return this.remove(this.index(value)) != null;
  }

  /**
   * Iterate over the values of the list.
   *
   * @return An iterator over the values of the list.
   */
  public final Iterator<V> iterator() {
    return Arrays.asList(this.values).iterator();
  }
}
