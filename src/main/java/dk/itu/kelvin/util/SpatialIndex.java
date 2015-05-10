/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// General utilities
import java.util.List;

// I/O utilities
import java.io.Serializable;

// Math
import dk.itu.kelvin.math.Geometry;

// Functional utilities
import dk.itu.kelvin.util.function.Filter;

/**
 * Spatial index interface.
 *
 * @param <E> The type of element stored within the spatial index.
 */
public interface SpatialIndex<E> extends Serializable {
  /**
   * Get the size of the spatial index.
   *
   * @return The size of the spatial index.
   */
  int size();

  /**
   * Check if the spatial index is empty.
   *
   * @return A boolean indicating whether or not the spatial index is empty.
   */
  boolean isEmpty();

  /**
   * Check if the spatial index contains the specified element.
   *
   * @param element The element to search for.
   * @return        A boolean indicating whether or not the spatial index
   *                contains the specified element.
   */
  boolean contains(final E element);

  /**
   * Find all elements within the range of the specified bounds.
   *
   * @param bounds  The bounds to search for elements within.
   * @return        A list of elements contained within the range of the
   *                specified bounds.
   */
  List<E> range(final Bounds bounds);

  /**
   * Find all elements included in the filter and within the range of the
   * specified bounds.
   *
   * @param bounds  The bounds to search for elements within.
   * @param filter  The filter to apply to the range search.
   * @return        A list of elements contained within the range of the
   *                specified bounds.
   */
  List<E> range(final Bounds bounds, final Filter<E> filter);

  /**
   * The {@link Point} class describes a 2-dimensional point in a spatial index.
   */
  public static final class Point extends Geometry.Point {
    /**
     * Initialize a new point.
     *
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     */
    public Point(final double x, final double y) {
      super(x, y);
    }
  }

  /**
   * The {@link Bounds} class describes a set of 2-dimensional, rectangular
   * bounds that can be used in range queries in spatial indexes.
   */
  public static final class Bounds extends Geometry.Bounds {
    /**
     * Initialize a new set of bounds.
     *
     * @param minX The smallest x-coordinate of the bounds.
     * @param minY The smallest y-coordinate of the bounds.
     * @param maxX The largest x-coordinate of the bounds.
     * @param maxY The largest y-coordinate of the bounds.
     */
    public Bounds(
      final double minX,
      final double minY,
      final double maxX,
      final double maxY
    ) {
      super(new Geometry.Point(minX, minY), new Geometry.Point(maxX, maxY));
    }
  }
}
