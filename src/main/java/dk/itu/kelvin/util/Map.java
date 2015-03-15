/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

/**
 * Map interface.
 *
 * @param <K> The type of keys stored within the map.
 * @param <V> The type of values stored within the map.
 *
 * @version 1.0.0
 */
public interface Map<K, V> {
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
   * Get a collection of the values contained within the map.
   *
   * @return A collection of the values contained within the map.
   */
  Collection<V> values();
}
