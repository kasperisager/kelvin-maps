/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// JUnit annotations
import org.junit.Test;

// JUnit assertions
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * {@link StringPool} test suite.
 */
public class StringPoolTest {
  /**
   * Testing get method with string, empty string and null string.
   */
  @Test
  public void testGet() {
    StringPool sp = new StringPool();

    assertEquals(sp.get("James"), "James");
    assertEquals(sp.get(""), "");
    assertNull(sp.get(null));
  }

}
