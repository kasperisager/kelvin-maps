/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// I/O utilities
import java.io.Serializable;

/**
 * Abstract class for keeping array size and resize properties.
 *
 * <p>
 * {@link DynamicArray} is abstract class for keeping track of array capacity
 * and number of elements in extending classes, it also has values for when the
 * arrays should resize and to what capacity it should resize.
 *
 * <p>
 * {@link DynamicArray} has a single constructor with arguments {@code capacity}
 * that represents the size of the array, {@code upperLoadFactor} that specifies
 * a factor for when the array needs to resize to a bigger array,
 * {@code upperResizeFactor} that specifies a factor for how many times bigger
 * the new capacity should be, {@code lowerLoadFactor} that specifies a factor
 * for when the array needs to resize to a smaller array, and
 * {@code lowerResizeFactor} that specifies a factor for how many times smaller
 * the new capacity should be.
 *
 * <p>
 * {@link DynamicArray} keeps track of the number of elements in the array and
 * also provides methods {@link #size()} for returning current number of
 * elements in array, {@link #isEmpty()} for returning whether there is any
 * elements in the array, and {@link #capacity} for returning the current size
 * of the array.
 *
 * <p>
 * Additionally there is {@link #grow()} and {@link #shrink()} for growing and
 * shrinking the array which are called every time an element is added or
 * removed, respectively, from the array.
 *
 * <p>
 * {@link #resize(int)} is used by {@link #grow()} and {@link #shrink()} and are
 * therefore abstract so extending classes need to write their own
 * implementations.
 */
public abstract class DynamicArray implements Serializable {
  /**
   * Default initial capacity of the internal storage of the collection.
   */
  private int defaultCapacity;

  /**
   * The upper factor to use for resizing the internal storage of the
   * collection.
   *
   * When the number of entries in the collection reaches this factor of the
   * total capacity of the internal storage, the storage is resized.
   */
  private float upperLoadFactor;

  /**
   * The factor with which to grow the internal storage of the collection when
   * the upper threshold has been reached.
   */
  private float upperResizeFactor;

  /**
   * The lower factor to use for resizing the internal storage of the
   * collection.
   *
   * When the number of entries in the collection reaches this factor of the
   * total capacity of the internal storage, the storage is resized.
   */
  private float lowerLoadFactor;

  /**
   * The factor with which to shrink the internal storage of the collection when
   * the lower threshold has been reached.
   */
  private float lowerResizeFactor;

  /**
   * The initial capacity of the internal storage of the collection.
   *
   * <p>
   * This is used when resetting the internal storage of the collection.
   */
  private final int initialCapacity;

  /**
   * The capacity of the internal storage of the collection.
   */
  private int capacity;

  /**
   * The number of entries contained within the table.
   */
  private int size;

  /**
   * Initialize a dynamic array structure with the specified values.
   *
   * @param capacity          The initial capacity of the collection.
   * @param upperLoadFactor   The upper load factor of the collection.
   * @param upperResizeFactor The upper resize factor of the collection.
   * @param lowerLoadFactor   The lower load factor of the collection.
   * @param lowerResizeFactor The lower resize factor of the collection.
   */
  public DynamicArray(
    final int capacity,
    final float upperLoadFactor,
    final float upperResizeFactor,
    final float lowerLoadFactor,
    final float lowerResizeFactor
  ) {
    this.initialCapacity = capacity;
    this.capacity = capacity;
    this.upperLoadFactor = upperLoadFactor;
    this.upperResizeFactor = upperResizeFactor;
    this.lowerLoadFactor = lowerLoadFactor;
    this.lowerResizeFactor = lowerResizeFactor;
  }

  /**
   * Get the size of the collection.
   *
   * @return The size of the collection.
   */
  public final int size() {
    return this.size;
  }

  /**
   * Check if the collection is empty.
   *
   * @return A boolean indicating whether or not the collection is empty.
   */
  public final boolean isEmpty() {
    return this.size == 0;
  }

  /**
   * Get the initial capacity of the internal storage of the collection.
   *
   * @return The initial capacity of the internal storage of the collection.
   */
  protected final int initialCapacity() {
    return this.initialCapacity;
  }

  /**
   * Get the capacity of the internal storage of the collection.
   *
   * @return The capacity of the internal storage of the collection.
   */
  protected final int capacity() {
    return this.capacity;
  }

  /**
   * Grow the collection by one element and resize the internal storage if
   * the upper threshold has been reached.
   */
  protected final void grow() {
    this.size++;

    // Resize the internal storage if we've reached the upper threshold.
    if (this.size >= (int) (this.capacity * this.upperLoadFactor)) {
      int capacity = (int) (this.capacity * this.upperResizeFactor);

      this.resize(capacity);
      this.capacity = capacity;
    }
  }

  /**
   * Shrink the collection by one element and resize the internal storage if
   * the lower threshold has been reached.
   */
  protected final void shrink() {
    if (this.size <= 0) {
      return;
    }

    this.size--;

    // Resize the internal storage if we've reached the lower threshold.
    if (this.size <= (int) (this.capacity * this.lowerLoadFactor)) {
      int capacity = (int) (this.capacity * this.lowerResizeFactor);

      this.resize(capacity);
      this.capacity = capacity;
    }
  }

  /**
   * Reset the size of the collection to 0 and the capacity to the initial
   * capacity set when initializing the collection.
   */
  protected final void reset() {
    this.size = 0;
    this.capacity = this.initialCapacity;
  }

  /**
   * Resize the internal storage of the collection to the specified capacity.
   *
   * @param capacity The new capacity of the internal storage of the collection.
   */
  protected abstract void resize(final int capacity);
}
