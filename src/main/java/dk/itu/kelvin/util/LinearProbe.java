/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

/**
 * Linear probing algorithm for resolving hash collisions.
 *
 * <p>
 * Linear steps through the {@code keys[] array} one by one until it finds and
 * empty location, if it reaches the end of the array it goes to the beginning
 * of the array and steps from there.
 *
 * <p>
 * This class implements {@link HashResolver}, which contains methods for
 * solving hash collisions. LinearProbe implements these methods for the
 * different primitive types and Objects of {@code key} and {@code keys[]},
 * respectively {@code int}, {@link long}, {@link float}, {@link double}, and
 * {@link Object}.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Linear_probing">
 *      http://en.wikipedia.org/wiki/Linear_probing</a>
 */
public final class LinearProbe extends HashResolver {
  /**
   * UID for identifying serialized objects.
   */
  private static final long serialVersionUID = 309;

  /**
   * Calculate the next index to step through.
   *
   * @param hash    The hash to step over.
   * @param length  The length of the keys
   * @return        The next index to step through.
   */
  private int step(final int hash, final int length) {
    return (hash + 1) % length;
  }

  /**
   * Resolve hash collisions for primitive {@code int} keys.
   *
   * @param key   The key to look up the index of.
   * @param keys  The list of keys to look through.
   * @return      The index of the specified key.
   */
  public int resolve(final int key, final int[] keys) {
    int i = HashResolver.hash(key, keys.length);

    while (keys[i] != 0) {
      if (keys[i] == key) {
        break;
      }

      i = this.step(i, keys.length);
    }

    return i;
  }

  /**
   * Resolve hash collisions for primitive {@code long} keys.
   *
   * @param key   The key to look up the index of.
   * @param keys  The list of keys to look through.
   * @return      The index of the specified key.
   */
  public int resolve(final long key, final long[] keys) {
    int i = HashResolver.hash(key, keys.length);

    while (keys[i] != 0L) {
      if (keys[i] == key) {
        break;
      }

      i = this.step(i, keys.length);
    }

    return i;
  }

  /**
   * Resolve hash collisions for primitive {@code float} keys.
   *
   * @param key   The key to look up the index of.
   * @param keys  The list of keys to look through.
   * @return      The index of the specified key.
   */
  public int resolve(final float key, final float[] keys) {
    int i = HashResolver.hash(key, keys.length);

    while (keys[i] != 0.0f) {
      if (keys[i] == key) {
        break;
      }

      i = this.step(i, keys.length);
    }

    return i;
  }

  /**
   * Resolve hash collisions for primitive {@code double} keys.
   *
   * @param key   The key to look up the index of.
   * @param keys  The list of keys to look through.
   * @return      The index of the specified key.
   */
  public int resolve(final double key, final double[] keys) {
    int i = HashResolver.hash(key, keys.length);

    while (keys[i] != 0.0d) {
      if (keys[i] == key) {
        break;
      }

      i = this.step(i, keys.length);
    }

    return i;
  }

  /**
   * Resolve hash collisions for {@code Object} keys.
   *
   * @param key   The key to look up the index of.
   * @param keys  The list of keys to look through.
   * @return      The index of the specified key.
   */
  public int resolve(final Object key, final Object[] keys) {
    int i = HashResolver.hash(key, keys.length);

    while (keys[i] != null) {
      if (keys[i].equals(key)) {
        break;
      }

      i = this.step(i, keys.length);
    }

    return i;
  }
}
