/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// General utilities
import java.util.Map;

// I/O utilities
import java.io.Serializable;

// Koloboke collections
import net.openhft.koloboke.collect.map.hash.HashObjObjMaps;

/**
 * String pool class.
 *
 * @see <a href="http://en.wikipedia.org/wiki/String_interning">
 *      http://en.wikipedia.org/wiki/String_interning</a>
 *
 * @see <a href="http://stackoverflow.com/a/10628759">
 *      http://stackoverflow.com/a/10628759</a>
 */
public final class StringPool implements Serializable {
  /**
   * Internal map of strings.
   */
  private Map<String, String> pool = HashObjObjMaps.newMutableMap();

  /**
   * Get the interned version of the specified string, interning it if it's not
   * already in the pool.
   *
   * @param string  The string to get from the string pool.
   * @return        The interned string.
   */
  public String get(final String string) {
    if (string == null || string.isEmpty()) {
      return string;
    }

    if (!this.pool.containsKey(string)) {
      this.pool.put(string, string);
    }

    return this.pool.get(string);
  }
}
