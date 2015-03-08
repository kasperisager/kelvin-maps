/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// I/O utilities
import java.io.Serializable;

/**
 * Abstract collection class.
 *
 * @param <V> The type of values stored within the collection.
 *
 * @version 1.0.0
 */
public abstract class AbstractCollection<V>
  implements Iterable<V>, Serializable {
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
   * The capacity of the internal storage of the collection.
   */
  private int capacity;

  /**
   * The number of entries contained within the table.
   */
  private int size;

  /**
   * Initialize a new collection with the specified values.
   *
   * @param capacity          The initial capacity of the collection.
   * @param upperLoadFactor   The upper load factor of the collection.
   * @param upperResizeFactor The upper resize factor of the collection.
   * @param lowerLoadFactor   The lower load factor of the collection.
   * @param lowerResizeFactor The lower resize factor of the collection.
   */
  public AbstractCollection(
    final int capacity,
    final float upperLoadFactor,
    final float upperResizeFactor,
    final float lowerLoadFactor,
    final float lowerResizeFactor
  ) {
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
   * Resize the internal storage of the collection to the specified capacity.
   *
   * @param capacity The new capacity of the internal storage of the collection.
   */
  protected abstract void resize(final int capacity);
}
