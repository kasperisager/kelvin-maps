/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

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
public class HashTable<K, V> extends DynamicHashArray implements Map<K, V> {
  /**
   * UID for identifying serialized objects.
   */
  private static final long serialVersionUID = 55;

  /**
   * The hash collision resolver to use.
   */
  private static final HashResolver RESOLVER = new QuadraticProbe();

  /**
   * Internal key storage.
   */
  private K[] keys;

  /**
   * Internal value storage.
   */
  private V[] values;

  /**
   * Initialize a hash table with the default initial capacity.
   */
  public HashTable() {
    this(16);
  }

  /**
   * Initialize a table with the specified initial capacity.
   *
   * @param capacity The initial capacity of the table.
   */
  @SuppressWarnings("unchecked")
  public HashTable(final int capacity) {
    super(
      capacity,
      0.5f,   // Upper load factor
      2f,     // Upper resize factor
      0.125f, // Lower load factor
      0.5f    // Lower resize factor
    );

    this.keys = (K[]) new Object[this.capacity()];
    this.values = (V[]) new Object[this.capacity()];
  }

  /**
   * Resize the internal storage of the table to the specified capacity.
   *
   * @param capacity The new capacity of the internal storage of the table.
   */
  protected final void resize(final int capacity) {
    HashTable<K, V> temp = new HashTable<>(capacity);

    // For each of the entries in the current table, put them in the temporary
    // table, effectively recomputing their hashes.
    for (int i = 0; i < this.capacity(); i++) {
      if (this.keys[i] != null) {
        temp.put(this.keys[i], this.values[i]);
      }
    }

    // Point the entries of the current table to the entries of the temporary,
    // rehashed table.
    this.keys = temp.keys;
    this.values = temp.values;
  }

  /**
   * Return the index of the specified key.
   *
   * @param key The key to look up the index of.
   * @return    The index of the specified key.
   */
  private int indexOf(final Object key) {
    if (key == null) {
      return -1;
    }

    return RESOLVER.resolve(this.hash(key), key, this.keys);
  }

  /**
   * Get a value by key from the table.
   *
   * @param key The key of the value to get.
   * @return    The value if found.
   */
  public final V get(final Object key) {
    if (key == null) {
      return null;
    }

    return this.values[this.indexOf(key)];
  }

  /**
   * Check if the specified key exists within the table.
   *
   * @param key The key to check for.
   * @return    A boolean indicating whether or not the table contains the
   *            specified key.
   */
  public final boolean containsKey(final Object key) {
    if (key == null) {
      return false;
    }

    return this.get(key) != null;
  }

  /**
   * Check if the specified value exists within the table.
   *
   * @param value The value to check for.
   * @return      A boolean indicating whether or not the table contains the
   *              specified value.
   */
  public final boolean containsValue(final Object value) {
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
   * Put a key/value pair in the table.
   *
   * @param key   The key to put in the table.
   * @param value The value to put in the table.
   * @return      The previous value associated with the key if found.
   */
  public final V put(final K key, final V value) {
    if (key == null) {
      return null;
    }

    if (value == null) {
      this.remove(key);
      return null;
    }

    int i = this.indexOf(key);

    if (this.keys[i] != null && this.keys[i].equals(key)) {
      V old = this.values[i];
      this.values[i] = value;

      return old;
    }
    else {
      this.keys[i] = key;
      this.values[i] = value;
      this.grow();

      return null;
    }
  }

  /**
   * Remove a key/value pair from the table.
   *
   * @param key The key of the value to remove.
   * @return    The removed value if fonund.
   */
  public final V remove(final Object key) {
    if (!this.containsKey(key)) {
      return null;
    }

    int i = this.indexOf(key);

    V old = this.values[i];

    this.keys[i] = null;
    this.values[i] = null;
    this.shrink();

    return old;
  }

  /**
   * Get a set of the keys contained within the table.
   *
   * <p>
   * Unlike Java's Collections, the returned set is neither backed by the map
   * nor is the map backed by the set. The object references are however the
   * same, so changes to the elements of the set will propagate to the map and
   * vice-versa.
   *
   * @return A set of the keys contained within the table.
   */
  public final Set<K> keySet() {
    Set<K> keySet = new HashSet<>(this.size());

    for (int i = 0; i < this.capacity(); i++) {
      if (this.keys[i] != null) {
        keySet.add(this.keys[i]);
      }
    }

    return keySet;
  }

  /**
   * Get a collection of the values contained within the table.
   *
   * <p>
   * Unlike Java's Collections, the returned collection is neither backed by the
   * map nor is the map backed by the collection. The object references are
   * however the same, so changes to the elements of the collection will
   * propagate to the map and vice-versa.
   *
   * @return A collection of the values contained within the table.
   */
  public final Collection<V> values() {
    List<V> values = new ArrayList<>(this.size());

    for (int i = 0; i < this.capacity(); i++) {
      if (this.keys[i] != null) {
        values.add(this.values[i]);
      }
    }

    return values;
  }

  /**
   * Get a set of entries contained within the map.
   *
   * <p>
   * Unlike Java's Collections, the returned set is neither backed by the map
   * nor is the map backed by the set.
   *
   * @return A set of entries contained within the map.
   */
  public final Set<Map.Entry<K, V>> entrySet() {
    Set<Map.Entry<K, V>> entrySet = new HashSet<>(this.size());

    for (int i = 0; i < this.capacity(); i++) {
      if (this.keys[i] != null) {
        entrySet.add(new Entry<K, V>(this.keys[i], this.values[i]));
      }
    }

    return entrySet;
  }

  /**
   * The {@link Entry} class describes an entry within a hash table.
   */
  public static final class Entry<K, V> implements Map.Entry<K, V> {
    /**
     * The key of the entry.
     */
    private final K key;

    /**
     * The value of the entry.
     */
    private final V value;

    /**
     * Initialize a new entry with the specified key and value.
     *
     * @param key   The key of the entry.
     * @param value The value of the entry.
     */
    public Entry(final K key, final V value) {
      this.key = key;
      this.value = value;
    }

    /**
     * Get the key of the entry.
     *
     * @return The key of the entry.
     */
    public K getKey() {
      return this.key;
    }

    /**
     * Get the value of the entry.
     *
     * @return The value of the entry.
     */
    public V getValue() {
      return this.value;
    }
  }
}
