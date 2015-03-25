/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

/**
 * Double hashing algorithm for resolving hash collisions.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Double_hashing">
 *      http://en.wikipedia.org/wiki/Double_hashing</a>
 */
public final class DoubleHash implements HashResolver {
  /**
   * Calculate the next index to step through.
   *
   * @param hash        The hash to step over.
   * @param secondHash  The second hash to use.
   * @param length      The length of the keys
   * @param step        The previous step.
   * @return            The next index to step through.
   */
  private int step(
    final int hash,
    final int secondHash,
    final int length,
    final int step
  ) {
    return (hash + step * secondHash) % length;
  }

  /**
   * Compute a second hash used for the skipping step when probing.
   *
   * @param key The key to hash.
   * @return    A second hash.
   */
  private int secondHash(final int key) {
    return (key % 31) + 1;
  }

  /**
   * Compute a second hash used for the skipping step when probing.
   *
   * @param key The key to hash.
   * @return    A second hash.
   */
  private int secondHash(final long key) {
    return this.secondHash(Long.hashCode(key));
  }

  /**
   * Compute a second hash used for the skipping step when probing.
   *
   * @param key The key to hash.
   * @return    A second hash.
   */
  private int secondHash(final float key) {
    return this.secondHash(Float.floatToIntBits(key));
  }

  /**
   * Compute a second hash used for the skipping step when probing.
   *
   * @param key The key to hash.
   * @return    A second hash.
   */
  private int secondHash(final double key) {
    return this.secondHash(Double.doubleToLongBits(key));
  }

  /**
   * Compute a second hash used for the skipping step when probing.
   *
   * @param key The key to hash.
   * @return    A second hash.
   */
  private int secondHash(final Object key) {
    return this.secondHash(key.hashCode());
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
    int secondHash = this.secondHash(key);
    int step = 1;

    while (keys[i] != 0) {
      if (keys[i] == key) {
        break;
      }

      i = this.step(i, secondHash, keys.length, step++);
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
    int secondHash = this.secondHash(key);
    int step = 1;

    while (keys[i] != 0L) {
      if (keys[i] == key) {
        break;
      }

      i = this.step(i, secondHash, keys.length, step++);
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
    int secondHash = this.secondHash(key);
    int step = 1;

    while (keys[i] != 0.0f) {
      if (keys[i] == key) {
        break;
      }

      i = this.step(i, secondHash, keys.length, step++);
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
    int secondHash = this.secondHash(key);
    int step = 1;

    while (keys[i] != 0.0d) {
      if (keys[i] == key) {
        break;
      }

      i = this.step(i, secondHash, keys.length, step++);
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
    int secondHash = this.secondHash(key);
    int step = 1;

    while (keys[i] != null) {
      if (keys[i].equals(key)) {
        break;
      }

      i = this.step(i, secondHash, keys.length, step++);
    }

    return i;
  }
}
