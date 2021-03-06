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
 */
public final class Haversine {
  /**
   * Approximation of the radius of the Earth in kilometers.
   *
   * @see <a href="http://en.wikipedia.org/wiki/Earth_radius">
   *      http://en.wikipedia.org/wiki/Earth_radius</a>
   */
  public static final double R = 6372.8;

  /**
   * Don't allow instantiation of the class.
   *
   * <p>
   * Since the class only contains static fields and methods, we never want to
   * instantiate the class. We therefore define a private constructor so that
   * noone can create instances of the class other than the class itself.
   *
   * <p>
   * NB: This does not make the class a singleton. In fact, there never exists
   * an instance of the class since not even the class instantiates itself.
   */
  private Haversine() {
    super();
  }

  /**
   * Calculate the distance between the specified coordinates.
   *
   * @param lat1  The latitude of the first coordinate.
   * @param lon1  The longitude of the first coordinate.
   * @param lat2  The latitude of the second coordinate.
   * @param lon2  The longitude of the second coordinate.
   * @return      The distance between the two coordinates in kilometers.
   */
  public static double distance(
    final double lat1,
    final double lon1,
    final double lat2,
    final double lon2
  ) {
    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);

    double a = (
      Math.sin(dLat / 2) * Math.sin(dLat / 2)
    + Math.sin(dLon / 2) * Math.sin(dLon / 2)
    * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
    );

    double c = 2 * Math.asin(Math.sqrt(a));

    return R * c;
  }
}
