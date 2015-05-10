/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.math;

// JUnit annotations
import org.junit.Test;

import static org.junit.Assert.assertTrue;

// JUnit assertions

/**
 * {@link Haversine} test suite.
 *
 * <p>
 * Test the distance between two specified coordinates. The results are calculated
 * on http://andrew.hedges.name/experiments/haversine/.
 *
 * We set a a deviation on 2 %.
 * <p>
 *
 */
public final class HaversineTest {

  /**
   * Test the distance between two specified coordinates.
   */
  @Test
  public void testDistance() {
    // from ITU to Rådhuspladsen 1, København.
    final double lat1 = 55.659890;
    final double lon1 = 12.591188;
    final double lat2 = 55.675637;
    final double lon2 = 12.569544;


    // we set a deviation on 2 %.
    double realDist1 = 2.216 * 1.01;
    double realDist2 = 2.216 * 0.99;
    double ourDist = Haversine.distance(lat1, lon1, lat2, lon2) * 0.99;

    assertTrue( ourDist < realDist1);
    assertTrue( ourDist > realDist2);

    // from ITU to Bahamas.
    final double lat3 = 55.659890;
    final double lon3 = 12.591188;
    final double lat4 = 24.15;
    final double lon4 = 76.0;

    // we set a deviation on 2 %.
    double realDist3 = 6159.197 * 1.02;
    double realDist4 = 6159.197 * 0.98;
    double ourDist1 = Haversine.distance(lat3, lon3, lat4, lon4) * 0.99;

    assertTrue( ourDist1 < realDist3);
    assertTrue( ourDist1 > realDist4);
  }




}
