/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

/**
 * Prefix tree interface.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Trie">
 *      http://en.wikipedia.org/wiki/Trie</a>
 *
 * @param <V> The type of values stored within the prefix tree.
 */
public interface PrefixTree<V> {
  /**
   * Get the size of the tree.
   *
   * @return The size of the tree.
   */
  int size();

  /**
   * Check if the tree is empty.
   *
   * @return A boolean indicating whether or not the tree is empty.
   */
  boolean isEmpty();

  /**
   * Get a value by key from the tree.
   *
   * @param key The key of the value to get.
   * @return    The value if found.
   */
  V get(final Object key);

  /**
   * Check if the specified key exists within the tree.
   *
   * @param key The key to check for.
   * @return    A boolean indicating whether or not the tree contains the
   *            specified key.
   */
  boolean contains(final Object key);

  /**
   * Put a key/value pair in the tree.
   *
   * @param key   The key to put in the tree.
   * @param value The value to put in the tree.
   */
  void put(final String key, final V value);

  /**
   * Remove a key/value pair from the tree.
   *
   * @param key The key of the value to remove.
   */
  void remove(final Object key);

  /**
   * Remove all key/value pairs from the tree.
   */
  void clear();

  /**
   * Given a prefix, find all keys within the prefix tree that contain the
   * specified prefix.
   *
   * @param prefix  The prefix to search for.
   * @return        A map of matching keys and their associated values.
   */
  Map<String, V> search(final String prefix);
}
