/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

/**
 * <h2>Hashing class.</h2>
 * <p>
 * DynamicHashArray provides method {@link #hash(int)} for computing a
 * {@code hash} from a specific key and returning it as an {@code integer}.
 * The hash methods allow for different types of key, both primitive and Object
 * respectively {@code int, long, float, double and Object}.
 * If key is not an integer, the {@link #hash(int)} is called with
 * {@code Type.hashcode(key)} as the parameter.
 *
 *
 * @version 1.0.0
 */
public abstract class DynamicHashArray extends DynamicArray {
  /**
   * Primes to use for hashing keys.
   */
  private static final int[] PRIMES = new int[] {
    31, 61, 127, 251, 509, 1021, 2039, 4093, 8191, 16381, 32749, 65521, 131071,
    262139, 524287, 1048573, 2097143, 4194301, 8388593, 16777213, 33554393,
    67108859, 134217689, 268435399, 536870909, 1073741789, 2147483647
  };

  /**
   * Initialize a hashing array structure with the specified values.
   *
   * @param capacity          The initial capacity of the internal storage.
   * @param upperLoadFactor   The upper load factor of the internal storage.
   * @param upperResizeFactor The upper resize factor of the internal storage.
   * @param lowerLoadFactor   The lower load factor of the internal storage.
   * @param lowerResizeFactor The lower resize factor of the internal storage.
   */
  public DynamicHashArray(
    final int capacity,
    final float upperLoadFactor,
    final float upperResizeFactor,
    final float lowerLoadFactor,
    final float lowerResizeFactor
  ) {
    super(
      capacity,
      upperLoadFactor,
      upperResizeFactor,
      lowerLoadFactor,
      lowerResizeFactor
    );
  }

  /**
   * Compute the hash for the specified key.
   *
   * @param key The key for which to compute a hash.
   * @return    The computed hash.
   */
  protected final int hash(final int key) {
    int t = key & 0x7fffffff;
    int log = (int) Math.log(this.capacity());

    if (log < 26) {
      t = t % PRIMES[log + 5];
    }

    return t % this.capacity();
  }

  /**
   * Compute the hash for the specified key.
   *
   * @param key The key for which to compute a hash.
   * @return    The computed hash.
   */
  protected final int hash(final long key) {
    return this.hash(Long.hashCode(key));
  }

  /**
   * Compute the hash for the specified key.
   *
   * @param key The key for which to compute a hash.
   * @return    The computed hash.
   */
  protected final int hash(final float key) {
    return this.hash(Float.floatToIntBits(key));
  }

  /**
   * Compute the hash for the specified key.
   *
   * @param key The key for which to compute a hash.
   * @return    The computed hash.
   */
  protected final int hash(final double key) {
    return this.hash(Double.doubleToLongBits(key));
  }

  /**
   * Compute the hash for the specified key.
   *
   * @param key The key for which to compute a hash.
   * @return    The computed hash.
   */
  protected final int hash(final Object key) {
    return this.hash(key.hashCode());
  }
}
