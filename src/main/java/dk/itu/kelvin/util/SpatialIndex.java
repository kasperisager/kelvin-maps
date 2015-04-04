/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// I/O utilities
import java.io.Serializable;

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
   * The {@link Bounds} class describes a set of 2-dimensional, rectangular
   * bounds that can be used in range queries in spatial indexes.
   */
  public static final class Bounds {
    /**
     * The smallest x-coordinate of the bounds.
     */
    private final float minX;

    /**
     * The smallest y-coordinate of the bounds.
     */
    private final float minY;

    /**
     * The largest x-coordinate of the bounds.
     */
    private final float maxX;

    /**
     * The largest y-coordinate of the bounds.
     */
    private final float maxY;

    /**
     * Initialize a new set of bounds.
     *
     * @param minX The smallest x-coordinate of the bounds.
     * @param minY The smallest y-coordinate of the bounds.
     * @param maxX The largest x-coordinate of the bounds.
     * @param maxY The largest y-coordinate of the bounds.
     */
    public Bounds(
      final float minX,
      final float minY,
      final float maxX,
      final float maxY
    ) {
      this.minX = minX;
      this.minY = minY;
      this.maxX = maxX;
      this.maxY = maxY;
    }

    /**
     * Get the smallest x-coordinate of the bounds.
     *
     * @return The smallest x-coordinate of the bounds.
     */
    public float minX() {
      return this.minX;
    }

    /**
     * Get the smallest y-coordinate of the bounds.
     *
     * @return The smallest y-coordinate of the bounds.
     */
    public float minY() {
      return this.minY;
    }

    /**
     * Get the largest x-coordinate of the bounds.
     *
     * @return The largest x-coordinate of the bounds.
     */
    public float maxX() {
      return this.maxX;
    }

    /**
     * Get the largest y-coordinate of the bounds.
     *
     * @return The largest y-coordinate of the bounds.
     */
    public float maxY() {
      return this.maxY;
    }

    /**
     * Check if the current bounds contain the specified point.
     *
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @return  A boolean indicating whether or not the current bounds contain
     *          the specified point.
     */
    public boolean contains(final float x, final float y) {
      return (
        this.minX <= x
        && this.maxX >= x
        && this.minY <= y
        && this.maxY >= y
      );
    }

    /**
     * Check if the current bounds intersect the specified bounding coordinates.
     *
     * @param minX  The smallest x-coordinate to check intersection of.
     * @param minY  The smallest y-coordinate to check intersection of.
     * @param maxX  The largest x-coordinate to check intersection of.
     * @param maxY  The largest y-coordinate to check intersection of.
     * @return      A boolean indicating whether or not the current bounds
     *              intersect the specified bounding coordinates.
     */
    public boolean intersects(
      final float minX,
      final float minY,
      final float maxX,
      final float maxY
    ) {
      return (
        this.minX() <= maxX
        && this.maxX() >= minX
        && this.minY() <= maxY
        && this.maxY() >= minY
      );
    }

    /**
     * Check if the current bounds intersect the specified bounds.
     *
     * @param bounds  The bounds to check intersection of.
     * @return        A boolean indicating whether or not the current bounds
     *                intersect the specified bounds.
     */
    public boolean intersects(final Bounds bounds) {
      return this.intersects(
        bounds.minX(), bounds.minY(), bounds.maxX(), bounds.maxY()
      );
    }
  }

  /**
   * The {@link Filter} interface describes a filter that can be applied to all
   * range queries.
   *
   * @param <E> The type of element to apply the filter to.
   */
  @FunctionalInterface
  public interface Filter<E> {
    /**
     * Check if the specified element should be included in the filter.
     *
     * @param element The element to check.
     * @return        A boolean indicating whether or not the specified element
     *                should be included in the filter.
     */
    boolean include(final E element);
  }
}
