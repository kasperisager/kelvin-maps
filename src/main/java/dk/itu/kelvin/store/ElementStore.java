/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.store;

// General utilities
import java.util.Arrays;
import java.util.Iterator;

// Models
import dk.itu.kelvin.model.Element;

/**
 * Element store class.
 *
 * @see <a href="http://algs4.cs.princeton.edu/34hash/">
 *      http://algs4.cs.princeton.edu/34hash/</a>
 *
 * @param <T> The type of elements contained within the store.
 */
public final class ElementStore<T extends Element> implements Iterable<T> {
  /**
   * Default initial capacity of the internal storage of the store.
   */
  private static final int DEFAULT_CAPACITY = 16;

  /**
   * The upper factor to use for rehashing the internal storage of the store.
   *
   * When the number of entries in the store reaches this factor of the total
   * capacity of the internal storage, the storage is rehashed.
   */
  private static final float UPPER_LOAD_FACTOR = 0.5f;

  /**
   * The lower factor to use for rehashing the internal storage of the store.
   *
   * When the number of entries in the store reaches this factor of the total
   * capacity of the internal storage, the storage is rehashed.
   */
  private static final float LOWER_LOAD_FACTOR = 0.25f;

  /**
   * Primes to use for hashing IDs.
   */
  private static final int[] PRIMES = new int[] {
    31, 61, 127, 251, 509, 1021, 2039, 4093, 8191, 16381, 32749, 65521, 131071,
    262139, 524287, 1048573, 2097143, 4194301, 8388593, 16777213, 33554393,
    67108859, 134217689, 268435399, 536870909, 1073741789, 2147483647
  };

  /**
   * The capacity of the internal array storage of the store.
   */
  private int capacity;

  /**
   * The logarithm of the capacity.
   *
   * This is used for computing hashes and cached in a field to avoid overhead
   * associated with computing the logarithm.
   */
  private int logCapacity;

  /**
   * The upper threshold of the internal storage of the store.
   *
   * This is cached in a field to avoid computing it every time a new element
   * is added.
   */
  private int upperThreshold;

  /**
   * The lower threshold of the internal storage of the store.
   *
   * This is cached in a field to avoid computing it every time an element is
   * removed.
   */
  private int lowerThreshold;

  /**
   * The number of entries contained within the store.
   */
  private int size;

  /**
   * The IDs contained within the store.
   */
  private long[] ids;

  /**
   * The elements contained within the store.
   */
  private T[] elements;

  /**
   * Intialize a new store with the specified initial capacity.
   *
   * @param capacity The initial capacity of the store.
   */
  @SuppressWarnings("unchecked")
  public ElementStore(final int capacity) {
    // Set the initial capacity of the store.
    this.capacity(capacity);

    // Initialize the array storage.
    this.ids = new long[capacity];
    this.elements = (T[]) new Element[capacity];
  }

  /**
   * Initialize a new store with the default initial capacity.
   */
  public ElementStore() {
    this(DEFAULT_CAPACITY);
  }

  /**
   * Get the size of the element store.
   *
   * @return The size of the element store.
   */
  public int size() {
    return this.size;
  }

  /**
   * Check if the element store is empty.
   *
   * @return A boolean indicating whether or not the store is empty.
   */
  public boolean empty() {
    return this.size == 0;
  }

  /**
   * Set the capacity of the store.
   *
   * Note: This method does not rehash the internal storage; it simply sets
   * the `capacity` field and computes a bunch of stuff.
   *
   * @param capacity The capacity of the store.
   */
  private void capacity(final int capacity) {
    this.capacity = capacity;

    // Compute the logarithm of the capacity. This is used for computing hashes.
    this.logCapacity = (int) Math.log(this.capacity);

    // Compute the upper and lower thresholds of the internal storage capacity.
    this.upperThreshold = (int) (this.capacity * UPPER_LOAD_FACTOR);
    this.lowerThreshold = (int) (this.capacity * LOWER_LOAD_FACTOR);
  }

  /**
   * Compute the hash for the specified ID.
   *
   * @param id  The ID for which to compute a hash.
   * @return    The computed hash.
   */
  public int hash(final long id) {
    int t = Long.hashCode(id) & 0x7fffffff;

    if (this.logCapacity < 26) {
      t = t % PRIMES[this.logCapacity + 5];
    }

    return (int) (t % this.capacity);
  }

  /**
   * Return the index of the specified ID.
   *
   * @param id  The ID to look up the index of.
   * @return    The index of the specified ID.
   */
  private int index(final long id) {
    return this.quadraticProbe(id);
  }

  /**
   * Linear probing algorithm for resolving hash collisions.
   *
   * @see <a href="http://en.wikipedia.org/wiki/Linear_probing">
   *      http://en.wikipedia.org/wiki/Linear_probing</a>
   *
   * @param id  The ID to look up the index of.
   * @return    The index of the specified ID.
   */
  private int linearProbe(final long id) {
    int i = this.hash(id);

    while (this.elements[i] != null) {
      // If this is the element we're looking for, bail out.
      if (this.ids[i] == id) {
        break;
      }

      // Otherwise, check the next element.
      i = (i + 1) % this.capacity;
    }

    return i;
  }

  /**
   * Quadratic probing algorithm for resolving hash collisions.
   *
   * @see <a href="http://en.wikipedia.org/wiki/Quadratic_probing">
   *      http://en.wikipedia.org/wiki/Quadratic_probing</a>
   *
   * @param id  The ID to look ip the index of.
   * @return    The index of the specified ID.
   */
  private int quadraticProbe(final long id) {
    int i = this.hash(id);
    int step = 0;

    while (this.elements[i] != null) {
      // If this is the element we're looking for, bail out.
      if (this.ids[i] == id) {
        break;
      }

      // Otherwise, check the next element.
      i = (i + step * step++) % this.capacity;
    }

    return i;
  }

  /**
   * Double hashing algorithm for resolving hash collisions.
   *
   * @see <a href="http://en.wikipedia.org/wiki/Double_hashing">
   *      http://en.wikipedia.org/wiki/Double_hashing</a>
   *
   * @param id  The ID to look ip the index of.
   * @return    The index of the specified ID.
   */
  private int doubleHash(final long id) {
    int i = this.hash(id);
    int skip = 1 + (Long.hashCode(id) % 31);

    while (this.elements[i] != null) {
      i += skip;
      i %= this.capacity;
    }

    return i;
  }

  /**
   * Rehash the internal storage of the store.
   *
   * @param capacity The new capacity of the internal storage.
   */
  public void rehash(final int capacity) {
    ElementStore<T> temp = new ElementStore<>(capacity);

    // For each of the entries in the current store, put them in the temporary
    // store, effectively recomputing their hashes.
    for (int i = 0; i < this.capacity; i++) {
      if (this.elements[i] != null) {
        temp.put(this.ids[i], this.elements[i]);
      }
    }

    // Point the entries of the current store to the entries of the temporary,
    // rehashed store.
    this.ids = temp.ids;
    this.elements = temp.elements;

    this.capacity(temp.capacity);
  }

  /**
   * Get an element by ID from the store.
   *
   * @param id  The ID of the element to get.
   * @return    The element if found, otherwise null.
   */
  public T get(final long id) {
    return this.elements[this.index(id)];
  }

  /**
   * Check if an element with the given ID exists within the store.
   *
   * @param id  The ID of the element to check for.
   * @return    A boolean indicating whether or not the element store contains
   *            the element with the specified ID.
   */
  public boolean contains(final long id) {
    return this.get(id) != null;
  }

  /**
   * Check if the given element exists within the store.
   *
   * @param element The element to check for.
   * @return        A boolean indicating whether or not the element store
   *                contains the specified element.
   */
  public boolean contains(final T element) {
    for (T found: this.elements) {
      if (element.equals(found)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Put an element in the store.
   *
   * @param id      The ID of the element.
   * @param element The element.
   */
  public void put(final long id, final T element) {
    if (element == null) {
      this.remove(id);
      return;
    }

    // Rehash the internal storage if we've reached the upper threshold.
    if (this.size >= this.upperThreshold) {
      this.rehash(this.capacity * 2);
    }

    int i = this.index(id);

    this.ids[i] = id;
    this.elements[i] = element;

    this.size++;
  }

  /**
   * Remove an element by ID from the store.
   *
   * @param id The ID of the element to remove.
   */
  public void remove(final long id) {
    if (!this.contains(id)) {
      return;
    }

    int i = this.index(id);

    this.ids[i] = 0L;
    this.elements[i] = null;

    this.size--;

    // Rehash the internal storage if we've reached the lower threshold.
    if (this.size > 0 && this.size <= this.lowerThreshold) {
      this.rehash(this.capacity / 2);
    }
  }

  /**
   * Iterate over the elements of the store.
   *
   * @return An iterator over the elements of the store.
   */
  public Iterator<T> iterator() {
    return Arrays.asList(this.elements).iterator();
  }
}
