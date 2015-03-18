/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.math;

/**
 * Haversine class.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Haversine_formula">
 *      http://en.wikipedia.org/wiki/Haversine_formula</a>
 *
 * @see <a href="http://rosettacode.org/wiki/Haversine_formula#Java">
 *      http://rosettacode.org/wiki/Haversine_formula#Java</a>
 *
 * @version 1.0.0
 */
public final class Haversine {
  /**
   * Approximation of the radius of the Earth in kilometers.
   *
   * @see <a href="http://en.wikipedia.org/wiki/Earth_radius">
   *      http://en.wikipedia.org/wiki/Earth_radius</a>
   */
  public static final float R = 6372.8f;

  /**
   * Calculate the distance between the specified coordinates.
   *
   * @param lat1  The latitude of the first coordinate.
   * @param lon1  The longitude of the first coordinate.
   * @param lat2  The latitude of the second coordinate.
   * @param lon2  The longitude of the seoncd coordinate.
   * @return      The distance between the two coordinates in kilometers.
   */
  public static float distance(
    final float lat1,
    final float lon1,
    final float lat2,
    final float lon2
  ) {
    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);

    double a = (
      Math.sin(dLat / 2) * Math.sin(dLat / 2) +
      Math.sin(dLon / 2) * Math.sin(dLon / 2) *
      Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
    );

    double c = 2 * Math.asin(Math.sqrt(a));

    return (float) (R * c);
  }
}
