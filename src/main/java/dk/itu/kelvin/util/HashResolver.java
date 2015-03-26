/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

/**
 * <h2>Hash collision resolver interface.</h2>
 * <p>
 * HashResolver provides method {@link #resolve} for resolving hash collisions
 * of different key types primitive and Object, respectively
 * {@code int, long, float, double and Object}.
 *
 * <p>
 * The resolve methods will solve hash collisions of different types of key and
 * keys array.
 *
 * @version 1.0.0
 */
public interface HashResolver {
  /**
   * Resolve hash collisions for primitive {@code int} keys.
   *
   * @param hash  The hash of the specified key.
   * @param key   The key to look up the index of.
   * @param keys  The list of keys to look through.
   * @return      The index of the specified key.
   */
  int resolve(final int hash, final int key, final int[] keys);

  /**
   * Resolve hash collisions for primitive {@code long} keys.
   *
   * @param hash  The hash of the specified key.
   * @param key   The key to look up the index of.
   * @param keys  The list of keys to look through.
   * @return      The index of the specified key.
   */
  int resolve(final int hash, final long key, final long[] keys);

  /**
   * Resolve hash collisions for primitive {@code float} keys.
   *
   * @param hash  The hash of the specified key.
   * @param key   The key to look up the index of.
   * @param keys  The list of keys to look through.
   * @return      The index of the specified key.
   */
  int resolve(final int hash, final float key, final float[] keys);

  /**
   * Resolve hash collisions for primitive {@code double} keys.
   *
   * @param hash  The hash of the specified key.
   * @param key   The key to look up the index of.
   * @param keys  The list of keys to look through.
   * @return      The index of the specified key.
   */
  int resolve(final int hash, final double key, final double[] keys);

  /**
   * Resolve hash collisions for {@code Object} keys.
   *
   * @param hash  The hash of the specified key.
   * @param key   The key to look up the index of.
   * @param keys  The list of keys to look through.
   * @return      The index of the specified key.
   */
  int resolve(final int hash, final Object key, final Object[] keys);
}
