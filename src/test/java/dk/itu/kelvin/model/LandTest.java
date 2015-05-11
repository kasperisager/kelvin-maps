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
public class LandTest {
  /**
   *
   */
  @Test
  public void addWay() {
    Land land = new Land(new BoundingBox(4, 6, 2, 1));
    Way way = new Way();

    land.add(way);
    assertTrue(1 == land.coastlines().size());

    Way way1 = null;
    land.add(way1);

  }
}
