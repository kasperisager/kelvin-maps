/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.math;

/**
 * Projection interface.
 */
public interface Projection {
  /**
   * Convert an x-coordinate into a projected longitude.
   *
   * @param x The x-coordinate to convert.
   * @return  The corresponding projected longitude.
   */
  double xToLon(final double x);

  /**
   * Convert a projected longitude into an x-coordinate.
   *
   * @param lon The projected longitude to convert.
   * @return    The corresponding x-coordinate.
   */
  double lonToX(final double lon);

  /**
   * Convert a y-coordinate into a projected latitude.
   *
   * @param y The y-coordinate to convert.
   * @return  The corresponding projected latitude.
   */
  double yToLat(final double y);

  /**
   * Convert a projected latitude into a y-coordinate.
   *
   * @param lat The projected latitude to convert.
   * @return    The corresponding y-coordinate.
   */
  double latToY(final double lat);
}
