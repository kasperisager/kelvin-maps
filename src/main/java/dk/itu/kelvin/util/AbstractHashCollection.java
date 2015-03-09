/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

/**
 * Hash table class.
 *
 * @version 1.0.0
 */
public abstract class AbstractHashCollection extends AbstractCollection {
  /**
   * Primes to use for hashing keys.
   */
  private static final int[] PRIMES = new int[] {
    31, 61, 127, 251, 509, 1021, 2039, 4093, 8191, 16381, 32749, 65521, 131071,
    262139, 524287, 1048573, 2097143, 4194301, 8388593, 16777213, 33554393,
    67108859, 134217689, 268435399, 536870909, 1073741789, 2147483647
  };

  /**
   * Initialize a new hash collection with the specified initial capacity.
   *
   * @param capacity The initial capacity of the hash collection.
   */
  protected AbstractHashCollection(final int capacity) {
    super(
      capacity,
      0.5f,   // Upper load factor
      2f,     // Upper resize factor
      0.125f, // Lower load factor
      0.5f    // Lower resize factor
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
