/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// General utilities
import java.util.Random;

/**
 * Hash collision resolver class.
 *
 * <p>
 * {@link HashResolver} provides the method {@link #resolve} for resolving hash
 * collisions of different key types, primitives and {@link Object Objects},
 * respectively {@code int}, {@link long}, {@link float}, {@link double}, and
 * {@link Object}.
 *
 * <p>
 * The resolve methods will solve hash collisions of different types of key and
 * key arrays.
 *
 * @see <a href="http://opendatastructures.org/ods-java/5_2_LinearHashTable
 * _Linear_.html">http://opendatastructures.org/ods-java/5_2_LinearHashTable
 * _Linear_.html</a>
 *
 * @see <a href="http://en.wikipedia.org/wiki/Tabulation_hashing">
 *      http://en.wikipedia.org/wiki/Tabulation_hashing</a>
 */
public abstract class HashResolver {
  /**
   * Number of bytes per random integer in the table.
   */
  private static final int W = 32;

  /**
   * Some magical number that does awesome stuff.
   */
  private static final int R = 4;

  /**
   * Table of random integers.
   */
  private static final int[][] TAB;

  /**
   * Initialize the table of random integers.
   */
  static {
    Random r = new Random();
    int l = (int) Math.pow(2, W / R);

    TAB = new int[4][l];

    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < l; j++) {
        TAB[i][j] = r.nextInt();
      }
    }
  }

  /**
   * Resolve hash collisions for primitive {@code int} keys.
   *
   * @param key   The key to look up the index of.
   * @param keys  The list of keys to look through.
   * @return      The index of the specified key.
   */
  public abstract int resolve(final int key, final int[] keys);

  /**
   * Resolve hash collisions for primitive {@code long} keys.
   *
   * @param key   The key to look up the index of.
   * @param keys  The list of keys to look through.
   * @return      The index of the specified key.
   */
  public abstract int resolve(final long key, final long[] keys);

  /**
   * Resolve hash collisions for primitive {@code float} keys.
   *
   * @param key   The key to look up the index of.
   * @param keys  The list of keys to look through.
   * @return      The index of the specified key.
   */
  public abstract int resolve(final float key, final float[] keys);

  /**
   * Resolve hash collisions for primitive {@code double} keys.
   *
   * @param key   The key to look up the index of.
   * @param keys  The list of keys to look through.
   * @return      The index of the specified key.
   */
  public abstract int resolve(final double key, final double[] keys);

  /**
   * Resolve hash collisions for {@code Object} keys.
   *
   * @param key   The key to look up the index of.
   * @param keys  The list of keys to look through.
   * @return      The index of the specified key.
   */
  public abstract int resolve(final Object key, final Object[] keys);

  /**
   * Compute the hash for the specified key.
   *
   * @see <a href="http://stackoverflow.com/a/13831166">
   *      http://stackoverflow.com/a/13831166</a>
   *
   * @param key     The key for which to compute a hash.
   * @param length  The length of the array.
   * @return        The computed hash.
   */
  public static int hash(final int key, final int length) {
    int d = (int) (Math.log(length) / Math.log(2));

    return (
      (
        TAB[0][key & 0xff]
        ^ TAB[1][(key >>> 8) & 0xff]
        ^ TAB[2][(key >>> 16) & 0xff]
        ^ TAB[3][(key >>> 24) & 0xff]
      ) >>> (W - d)
    ) % length;
  }

  /**
   * Compute the hash for the specified key.
   *
   * @param key     The key for which to compute a hash.
   * @param length  The length of the array.
   * @return        The computed hash.
   */
  public static int hash(final long key, final int length) {
    return HashResolver.hash(Long.hashCode(key), length);
  }

  /**
   * Compute the hash for the specified key.
   *
   * @param key     The key for which to compute a hash.
   * @param length  The length of the array.
   * @return        The computed hash.
   */
  public static int hash(final float key, final int length) {
    return HashResolver.hash(Float.floatToIntBits(key), length);
  }

  /**
   * Compute the hash for the specified key.
   *
   * @param key     The key for which to compute a hash.
   * @param length  The length of the array.
   * @return        The computed hash.
   */
  public static int hash(final double key, final int length) {
    return HashResolver.hash(Double.doubleToLongBits(key), length);
  }

  /**
   * Compute the hash for the specified key.
   *
   * @param key     The key for which to compute a hash.
   * @param length  The length of the array.
   * @return        The computed hash.
   */
  public static int hash(final Object key, final int length) {
    return HashResolver.hash(key.hashCode(), length);
  }
}
