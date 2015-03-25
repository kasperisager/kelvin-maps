/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.math;

/**
 * Projection interface.
 */
public interface Projection {
  /**
   * Convert an x-coordinate into a spherical longitude.
   *
   * @param x The x-coordinate to convert.
   * @return  The corresponding spherical longitude.
   */
  float xToLon(final float x);

  /**
   * Convert a spherical longitude into an x-coordinate.
   *
   * @param lon The spherical longitude to convert.
   * @return    The corresponding x-coordinate.
   */
  float lonToX(final float lon);

  /**
   * Convert a y-coordinate into a spherical latitude.
   *
   * @param y The y-coordinate to convert.
   * @return  The corresponding spherical latitude.
   */
  float yToLat(final float y);

  /**
   * Convert a spherical latitude into a y-coordinate.
   *
   * @param lat The spherical latitude to convert.
   * @return    The corresponding y-coordinate.
   */
  float latToY(final float lat);
}
