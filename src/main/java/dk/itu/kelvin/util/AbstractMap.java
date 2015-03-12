/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

/**
 * Abstract map class.
 *
 * @param <K> The type of keys stored within the table.
 * @param <V> The type of values stored within the table.
 *
 * @version 1.0.0
 */
public abstract class AbstractMap<K, V> extends HashingArray
  implements Map<K, V> {
  /**
   * Initialize a map with the specified initial capacity.
   *
   * @param capacity The initial capacity of the map.
   */
  public AbstractMap(final int capacity) {
    super(
      capacity,
      0.5f,   // Upper load factor
      2f,     // Upper resize factor
      0.125f, // Lower load factor
      0.5f    // Lower resize factor
    );
  }
}
