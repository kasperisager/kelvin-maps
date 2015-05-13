/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.model;

// General utilities
// JUnit annotations
// JUnit assertions
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * {@link Land} test suite.
 */
public final class LandTest {
  /**
   *
   */
  @Test
  public void testAddWay() {
    Land land = new Land(new BoundingBox(4, 6, 2, 1));
    Way way = new Way();

    // test add way = null
    Way way1 = null;
    land.add(way1);
    assertTrue(0 == land.coastlines().size());

    // test size of coastlines when added 1 one
    land.add(way);
    assertTrue(1 == land.coastlines().size());
  }
}
