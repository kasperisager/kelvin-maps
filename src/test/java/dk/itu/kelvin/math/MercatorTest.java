/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.math;

// JUnit annotations
import org.junit.Test;

import static org.junit.Assert.assertTrue;

// JUnit assertions

/**
 * {@link Mercator} test suite.
 *
 * <p>
 * Test convert a x- and y-coordinate into a spherical latitude and longitude.
 * The results are calculated on .
 *
 * We set a a deviation on 2 %.
 * <p>
 *
 */
public class MercatorTest {
  /**
   * Test convert an x-coordinate into a spherical longitude
   */
  @Test
  public void testXToLon() {
    Mercator m = new Mercator();
    double lon = m.xToLon(20.03);



    assertTrue(20.03 == m.lonToX(lon));
  }

  /**
   * Test convert an y-coordinate into a spherical latitude
   */
  @Test
  public void testYToLon() {
    Mercator m = new Mercator();
    double lat = m.yToLat(200.03);
    assertTrue(200.03 == m.latToY(lat));
  }

}
