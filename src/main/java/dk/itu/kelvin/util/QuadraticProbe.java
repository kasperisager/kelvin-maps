/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

/**
 * Quadratic probing algorithm for resolving hash collisions.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Quadratic_probing">
 *      http://en.wikipedia.org/wiki/Quadratic_probing</a>
 *
 * @version 1.0.0
 */
public final class QuadraticProbe implements HashResolver {
  /**
   * Calculate the next index to step through.
   *
   * @param hash    The hash to step over.
   * @param length  The length of the keys
   * @param step    The previous step.
   * @return        The next index to step through.
   */
  private int step(final int hash, final int length, final int step) {
    return (hash + step * step) % length;
  }

  /**
   * Resolve hash collisions for primitive {@code int} keys.
   *
   * @param hash  The hash of the specified key.
   * @param key   The key to look up the index of.
   * @param keys  The list of keys to look through.
   * @return      The index of the specified key.
   */
  public int resolve(final int hash, final int key, final int[] keys) {
    int i = hash;
    int step = 1;

    while (keys[i] != 0 && keys[i] != key) {
      i = this.step(i, keys.length, step++);
    }

    return i;
  }

  /**
   * Resolve hash collisions for primitive {@code long} keys.
   *
   * @param hash  The hash of the specified key.
   * @param key   The key to look up the index of.
   * @param keys  The list of keys to look through.
   * @return      The index of the specified key.
   */
  public int resolve(final int hash, final long key, final long[] keys) {
    int i = hash;
    int step = 1;

    while (keys[i] != 0L && keys[i] != key) {
      i = this.step(i, keys.length, step++);
    }

    return i;
  }

  /**
   * Resolve hash collisions for primitive {@code float} keys.
   *
   * @param hash  The hash of the specified key.
   * @param key   The key to look up the index of.
   * @param keys  The list of keys to look through.
   * @return      The index of the specified key.
   */
  public int resolve(final int hash, final float key, final float[] keys) {
    int i = hash;
    int step = 1;

    while (keys[i] != 0.0f && keys[i] != key) {
      i = this.step(i, keys.length, step++);
    }

    return i;
  }

  /**
   * Resolve hash collisions for primitive {@code double} keys.
   *
   * @param hash  The hash of the specified key.
   * @param key   The key to look up the index of.
   * @param keys  The list of keys to look through.
   * @return      The index of the specified key.
   */
  public int resolve(final int hash, final double key, final double[] keys) {
    int i = hash;
    int step = 1;

    while (keys[i] != 0.0d && keys[i] != key) {
      i = this.step(i, keys.length, step++);
    }

    return i;
  }

  /**
   * Resolve hash collisions for {@code Object} keys.
   *
   * @param hash  The hash of the specified key.
   * @param key   The key to look up the index of.
   * @param keys  The list of keys to look through.
   * @return      The index of the specified key.
   */
  public int resolve(final int hash, final Object key, final Object[] keys) {
    int i = hash;
    int step = 1;

    while (keys[i] != null && !keys[i].equals(key)) {
      i = this.step(i, keys.length, step++);
    }

    return i;
  }
}
