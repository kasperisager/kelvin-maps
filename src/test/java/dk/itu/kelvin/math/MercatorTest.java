/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.math;

// JUnit annotations
import org.junit.Test;

// JUnit assertions
import static org.junit.Assert.assertTrue;

/**
 * {@link Mercator} test suite.
 *
 * <p>
 * Test convert a x- and y-coordinate into a spherical latitude and longitude.
 *
 * We take into account that we compare doubles and set a deviation at 0,1 %.
 * <p>
 *
 */
public final class MercatorTest {
  /**
   * Test convert an x-coordinate into a spherical longitude.
   */
  @Test
  public void testXToLon() {
    Mercator m = new Mercator();
    double lon = m.xToLon(20.03);

    assertTrue(20.03 == m.lonToX(lon));
  }

  /**
   * Test convert an y-coordinate into a spherical latitude.
   */
  @Test
  public void testYToLon() {
    Mercator m = new Mercator();
    double lat = m.yToLat(200.03);

    // deviation accept a deviation of 0,1 %.
    double devLat1 = m.latToY(lat) * 1.001;
    double devLat2 = m.latToY(lat) * 0.099;
    double yCoord = 200.03;

    assertTrue(yCoord < devLat1);
    assertTrue(yCoord > devLat2);
  }
}
