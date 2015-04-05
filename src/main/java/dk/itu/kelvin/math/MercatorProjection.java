/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.math;

/**
 * Mercator projection class.
 */
public final class MercatorProjection implements Projection {
  /**
   * Factor with which to scale the lat/lon projection coordinates.
   */
  private static final double PROJECTION_FACTOR = 30000;

  /**
   * Convert an x-coordinate into a spherical longitude.
   *
   * @see <a href="http://wiki.openstreetmap.org/wiki/Mercator#Java">
   *      http://wiki.openstreetmap.org/wiki/Mercator#Java</a>
   *
   * @param x The x-coordinate to convert.
   * @return  The corresponding spherical longitude.
   */
  public double xToLon(final double x) {
    return x / PROJECTION_FACTOR;
  }

  /**
   * Convert a spherical longitude into an x-coordinate.
   *
   * @see <a href="http://wiki.openstreetmap.org/wiki/Mercator#Java">
   *      http://wiki.openstreetmap.org/wiki/Mercator#Java</a>
   *
   * @param lon The spherical longitude to convert.
   * @return    The corresponding x-coordinate.
   */
  public double lonToX(final double lon) {
    return lon * PROJECTION_FACTOR;
  }

  /**
   * Convert a y-coordinate into a spherical latitude.
   *
   * @see <a href="http://wiki.openstreetmap.org/wiki/Mercator#Java">
   *      http://wiki.openstreetmap.org/wiki/Mercator#Java</a>
   *
   * @param y The y-coordinate to convert.
   * @return  The corresponding spherical latitude.
   */
  public double yToLat(final double y) {
    return (double) Math.toDegrees(
      2 * Math.atan(Math.exp(Math.toRadians(y))) - Math.PI / 2
    ) / -PROJECTION_FACTOR;
  }

  /**
   * Convert a spherical latitude into a y-coordinate.
   *
   * @see <a href="http://wiki.openstreetmap.org/wiki/Mercator#Java">
   *      http://wiki.openstreetmap.org/wiki/Mercator#Java</a>
   *
   * @param lat The spherical latitude to convert.
   * @return    The corresponding y-coordinate.
   */
  public double latToY(final double lat) {
    return (double) Math.toDegrees(
      Math.log(Math.tan(Math.PI / 4 + Math.toRadians(lat) / 2))
    ) * -PROJECTION_FACTOR;
  }
}
