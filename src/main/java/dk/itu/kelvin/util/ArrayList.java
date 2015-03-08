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
public class ArrayList<V> extends AbstractCollection<V> {
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
      2.0f    // Lower resize factor
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
    if (value == null) {
      return false;
    }

    for (V found: this.values) {
      if (value.equals(found)) {
        return true;
      }
    }

    return false;
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
   */
  public final void remove(final int index) {
    if (index < 0 || index >= this.size()) {
      return;
    }

    this.values[index] = null;
    this.shrink();
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
