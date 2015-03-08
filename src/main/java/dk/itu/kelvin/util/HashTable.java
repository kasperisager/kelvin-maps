/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// General utilities
import java.util.Arrays;
import java.util.Iterator;

/**
 * Hash table class.
 *
 * @see <a href="http://algs4.cs.princeton.edu/34hash/">
 *      http://algs4.cs.princeton.edu/34hash/</a>
 *
 * @param <K> The type of keys stored within the table.
 * @param <V> The type of values stored within the table.
 *
 * @version 1.0.0
 */
public class HashTable<K, V> extends AbstractHashCollection<K, V> {
  /**
   * UID for identifying serialized objects.
   */
  private static final long serialVersionUID = 55;

  /**
   * Internal key storage.
   */
  private K[] keys;

  /**
   * Internal value storage.
   */
  private V[] values;

  /**
   * The hash collision resolver to use.
   */
  private transient HashCollision<K> resolver = new QuadraticProbe<>();

  /**
   * Initialize a hash table with the specified initial capacity.
   *
   * @param capacity The initial capacity of the hash table.
   */
  public HashTable(final int capacity) {
    super(capacity);

    this.keys = (K[]) new Object[this.capacity()];
    this.values = (V[]) new Object[this.capacity()];
  }

  /**
   * Initialize a hash table with the default initial capacity.
   */
  public HashTable() {
    this(16);
  }

  /**
   * Resize the internal storage of the table to the specified capacity.
   *
   * @param capacity The new capacity of the internal storage of the table.
   */
  protected final void resize(final int capacity) {
    HashTable<K, V> temp = new HashTable<>(capacity);

    // For each of the entries in the current store, put them in the temporary
    // store, effectively recomputing their hashes.
    for (int i = 0; i < this.capacity(); i++) {
      if (this.keys[i] != null) {
        temp.put(this.keys[i], this.values[i]);
      }
    }

    // Point the entries of the current store to the entries of the temporary,
    // rehashed store.
    this.keys = temp.keys;
    this.values = temp.values;
  }

  /**
   * Return the index of the specified key.
   *
   * @param key The key to look up the index of.
   * @return    The index of the specified key.
   */
  private int index(final K key) {
    return this.resolver.resolve(this.hash(key), key, this.keys);
  }

  /**
   * Get a value by key from the table.
   *
   * @param key The key of the value to get.
   * @return    The value if found.
   */
  public final V get(final K key) {
    return this.values[this.index(key)];
  }

  /**
   * Check if the specified key exists within the table.
   *
   * @param key The key to check for.
   * @return    A boolean indicating whether or not the table contains the
   *            specified key.
   */
  public final boolean containsKey(final K key) {
    return this.get(key) != null;
  }

  /**
   * Check if the specified value exists within the table.
   *
   * @param value The value to check for.
   * @return      A boolean indicating whether or not the table contains the
   *              specified value.
   */
  public final boolean containsValue(final V value) {
    for (V found: this.values) {
      if (value.equals(found)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Put a key/value pair in the table.
   *
   * @param key   The key to put in the table.
   * @param value The value to put in the table.
   */
  public final void put(final K key, final V value) {
    if (value == null) {
      this.remove(key);
      return;
    }

    int i = this.index(key);

    if (this.keys[i] != null && this.keys[i].equals(key)) {
      this.values[i] = value;
    }
    else {
      this.keys[i] = key;
      this.values[i] = value;
      this.grow();
    }
  }

  /**
   * Remove a key/value pair from the table.
   *
   * @param key The key of the value to remove.
   */
  public final void remove(final K key) {
    if (!this.containsKey(key)) {
      return;
    }

    int i = this.index(key);

    this.keys[i] = null;
    this.values[i] = null;
    this.shrink();
  }

  /**
   * Iterate over the values of the table.
   *
   * @return An iterator over the values of the table.
   */
  public final Iterator<V> iterator() {
    return Arrays.asList(this.values).iterator();
  }
}
