/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// I/O utilities
import java.io.Serializable;

/**
 * Minimal implementation of Map interface.
 *
 * <p>
 * An object that maps keys to values. A map cannot contain duplicate keys and
 * each key can map to at most one value.
 *
 * <p>
 * The {@code Map} interface provides three collection views, which allow a
 * map's contents to be viewed as a {@code set of keys},
 * {@code collection of values}, or {@code set of key-value mappings}.
 *
 * All general-purpose map implementation classes should provide two "standard"
 * constructors: a void (no arguments) constructor which creates an empty map,
 * and a constructor with a single argument of type {@code Map}, which creates
 * a new map with the same key-value mappings as its argument.
 * In effect, the latter constructor allows the user to copy any map, producing
 * an equivalent map of the desired class.
 *
 * <p>
 * Some map implementations have restrictions on the keys and values that they
 * may contain. No implementing classes allow {@code null} keys or
 * {@code null} values. Adding a null null value, with remove the associated
 * key.
 *
 * @param <K> The type of keys stored within the map.
 * @param <V> The type of values stored within the map.
 */
public interface Map<K, V> extends Serializable {
  /**
   * Get the size of the map.
   *
   * @return The size of the map.
   */
  int size();

  /**
   * Check if the map is empty.
   *
   * @return A boolean indicating whether or not the map is empty.
   */
  boolean isEmpty();

  /**
   * Get a value by key from the map.
   *
   * @param key The key of the value to get.
   * @return    The value if found.
   */
  V get(final Object key);

  /**
   * Check if the specified key exists within the map.
   *
   * @param key The key to check for.
   * @return    A boolean indicating whether or not the map contains the
   *            specified key.
   */
  boolean containsKey(final Object key);

  /**
   * Check if the specified value exists within the map.
   *
   * @param value The value to check for.
   * @return      A boolean indicating whether or not the map contains the
   *              specified value.
   */
  boolean containsValue(final Object value);

  /**
   * Put a key/value pair in the map.
   *
   * @param key   The key to put in the map.
   * @param value The value to put in the map.
   * @return      The previous value associated with the key if found.
   */
  V put(final K key, final V value);

  /**
   * Remove a key/value pair from the map.
   *
   * @param key The key of the value to remove.
   * @return    The removed value if fonund.
   */
  V remove(final Object key);

  /**
   * Remove all key/value pairs from the map.
   */
  void clear();

  /**
   * Get a set of the keys contained within the map.
   *
   * @return A set of the keys contained within the map.
   */
  Set<K> keySet();

  /**
   * Get a collection of the values contained within the map.
   *
   * @return A collection of the values contained within the map.
   */
  Collection<V> values();

  /**
   * Get a set of entries contained within the map.
   *
   * @return A set of entries contained within the map.
   */
  Set<Map.Entry<K, V>> entrySet();

  /**
   * The {@link Entry} class describes an entry within a map.
   */
  public interface Entry<K, V> {
    /**
     * Get the key of the entry.
     *
     * @return The key of the entry.
     */
    K getKey();

    /**
     * Get the value of the entry.
     *
     * @return The value of the entry.
     */
    V getValue();
  }
}
