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
  float xToLon(final float x);

  /**
   * Convert a projected longitude into an x-coordinate.
   *
   * @param lon The projected longitude to convert.
   * @return    The corresponding x-coordinate.
   */
  float lonToX(final float lon);

  /**
   * Convert a y-coordinate into a projected latitude.
   *
   * @param y The y-coordinate to convert.
   * @return  The corresponding projected latitude.
   */
  float yToLat(final float y);

  /**
   * Convert a projected latitude into a y-coordinate.
   *
   * @param lat The projected latitude to convert.
   * @return    The corresponding y-coordinate.
   */
  float latToY(final float lat);
}
