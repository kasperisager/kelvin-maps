/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

/**
 * Double hashing algorithm for resolving hash collisions.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Double_hashing">
 *      http://en.wikipedia.org/wiki/Double_hashing</a>
 *
 * @param <K> The type of keys to resolve collisions of.
 *
 * @version 1.0.0
 */
public final class DoubleHash<K> implements HashCollision<K> {
  /**
   * Compute a second hash used for the skipping step when probing.
   *
   * @param hash  The original hash.
   * @return      A second hash.
   */
  private int step(final int hash) {
    return (hash % 7) + 1;
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
    int step = this.step(i);

    while (keys[i] != 0) {
      i += step;
      i %= keys.length;
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
    int step = this.step(i);

    while (keys[i] != 0L) {
      i += step;
      i %= keys.length;
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
    int step = this.step(i);

    while (keys[i] != 0.0f) {
      i += step;
      i %= keys.length;
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
    int step = this.step(i);

    while (keys[i] != 0.0d) {
      i += step;
      i %= keys.length;
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
  public int resolve(final int hash, final K key, final K[] keys) {
    int i = hash;
    int step = this.step(i);

    while (keys[i] != null) {
      i += step;
      i %= keys.length;
    }

    return i;
  }
}
