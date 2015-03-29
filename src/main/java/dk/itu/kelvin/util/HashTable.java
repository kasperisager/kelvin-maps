/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

/**
 * Modified minimal hash table implementation.
 *
 * <p>
 * This class implements a hash table which maps keys to values. Any non-null
 * objects or variables can be used as a key or as a value.
 *
 * <p>
 * To successfully store and retrieve objects from a hashtable, the objects
 * used as keys will call respective {@link #hash} methods from
 * {@link HashResolver}. For HashTable an instance of {@link QuadraticProbe}
 * is create which implements HashResolver.
 *
 * <p>
 * HashTable has two constructors, one that takes a single argument {@code int}
 * specifies the initial capacity, and one that takes no arguments and sets
 * the initial capacity to 16.
 *
 * The default {@code load capacity factor} is 0.5 for resizing to a bigger
 * table, and 0.125 for resizing to a smaller array, respective with a
 * {@code resize factor} of 2 and 0.5.
 *
 * <p>
 * The initial capacity controls a tradeoff between wasted space and the need
 * for {@link #resize(int)} operations, which are time-consuming. No resize
 * operations will ever occur if the initial capacity is greater than the
 * maximum number of entries the HashTable will contain divided by its load
 * factor. However, setting the initial capacity too high can waste space.
 *
 * <p>
 * Methods {@link #resize(int)}, {@link #containsValue(Object)},
 * {@link #keySet()}, {@link #values()} and {@link #entrySet()} takes linear
 * time complexity, methods {@link #indexOf(Object)}, {@link #get(Object)}
 * and {@link #containsKey(Object)} takes constant time complexity methods
 * {@link #put(Object, Object)} and {@link #remove(Object)} takes amortized
 * constant time.
 *
 * @see <a href="http://algs4.cs.princeton.edu/34hash/">
 *      http://algs4.cs.princeton.edu/34hash/</a>
 *
 * @param <K> The type of keys stored within the table.
 * @param <V> The type of values stored within the table.
 */
public class HashTable<K, V> implements Map<K, V> {
  /**
   * UID for identifying serialized objects.
   */
  private static final long serialVersionUID = 55;

  /**
   * Default initial capacity of the internal storage of the table.
   */
  private static final int DEFAULT_CAPACITY = 16;

  /**
   * The upper factor to use for resizing the internal storage of the table.
   *
   * <p>
   * When the number of entries in the table reaches this factor of the total
   * capacity of the internal storage, the storage is resized.
   */
  private static final float UPPER_LOAD_FACTOR = 0.5f;

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
  private static final float LOWER_LOAD_FACTOR = 0.125f;

  /**
   * The factor with which to shrink the internal storage of the collection when
   * the lower threshold has been reached.
   */
  private static final float LOWER_RESIZE_FACTOR = 0.5f;

  /**
   * The hash collision resolver to use.
   */
  private static final HashResolver RESOLVER = new LinearProbe();

  /**
   * The size of the hash table.
   */
  private int size;

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
    this(DEFAULT_CAPACITY);
  }

  /**
   * Initialize a table with the specified initial capacity.
   *
   * @param capacity The initial capacity of the table.
   */
  @SuppressWarnings("unchecked")
  public HashTable(final int capacity) {
    this.keys = (K[]) new Object[capacity];
    this.values = (V[]) new Object[capacity];
  }

  /**
   * Get the size of the hash table.
   *
   * @return The size of the hash table.
   */
  public final int size() {
    return this.size;
  }

  /**
   * Check if the hash table is empty.
   *
   * @return A boolean indicating whether or not the hash table is empty.
   */
  public final boolean isEmpty() {
    return this.size == 0;
  }

  /**
   * Resize the internal storage of the table to the specified capacity.
   *
   * @param capacity The new capacity of the internal storage of the table.
   */
  private void resize(final int capacity) {
    HashTable<K, V> temp = new HashTable<>(capacity);

    // For each of the entries in the current table, put them in the temporary
    // table, effectively recomputing their hashes.
    for (int i = 0; i < this.keys.length; i++) {
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

    return RESOLVER.resolve(key, this.keys);
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
    if (key == null || value == null) {
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
      this.size++;

      if (this.size == (int) (this.keys.length * UPPER_LOAD_FACTOR)) {
        this.resize((int) (this.keys.length * UPPER_RESIZE_FACTOR));
      }

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
    this.size--;

    if (
      this.size > 0
      && this.size == (int) (this.keys.length * LOWER_LOAD_FACTOR)
    ) {
      this.resize((int) (this.keys.length * LOWER_RESIZE_FACTOR));
    }

    return old;
  }

  /**
   * Remove all elements from the table.
   *
   * <p>
   * <b>NB:</b> Unlike Java's Collections, this operation will actually reset
   * the internal storage of the table to its default capacity instead of simply
   * nullifying all elements.
   */
  @SuppressWarnings("unchecked")
  public final void clear() {
    this.keys = (K[]) new Object[DEFAULT_CAPACITY];
    this.values = (V[]) new Object[DEFAULT_CAPACITY];
    this.size = 0;
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
    Set<K> keySet = new HashSet<>(this.size);

    for (int i = 0; i < this.keys.length; i++) {
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
    List<V> values = new ArrayList<>(this.size);

    for (int i = 0; i < this.keys.length; i++) {
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
    Set<Map.Entry<K, V>> entrySet = new HashSet<>(this.size);

    for (int i = 0; i < this.keys.length; i++) {
      if (this.keys[i] != null) {
        entrySet.add(new Entry<K, V>(this.keys[i], this.values[i]));
      }
    }

    return entrySet;
  }

  /**
   * Simple Entry implementation.
   *
   * <p>
   * The {@link Entry} class describes an entry within a hash table.
   *
   * <p>
   * Methods {@link #getKey()} and {@link #getValue()} takes constant time
   * complexity.
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
