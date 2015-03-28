/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// Math
import dk.itu.kelvin.math.Geometry;
import static dk.itu.kelvin.math.Geometry.Bounds;
import static dk.itu.kelvin.math.Geometry.Shape;

/**
 * Spatial index interface.
 *
 * @param <E> The type of element stored within the spatial index.
 */
public interface SpatialIndex<E> {
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
   * @param <B>     The type of bounds to use.
   * @param bounds  The bounds to search for elements within.
   * @return        A list of elements contained within the range of the
   *                specified bounds.
   */
  <B extends Geometry.Bounds> List<E> range(final B bounds);

  /**
   * Find all elements included in the filter and within the range of the
   * specified bounds.
   *
   * @param <B>     The type of bounds to use.
   * @param bounds  The bounds to search for elements within.
   * @param filter  The filter to apply to the range search.
   * @return        A list of elements contained within the range of the
   *                specified bounds.
   */
  <B extends Bounds> List<E> range(final B bounds, final Filter<E> filter);

  /**
   * The {@link Element} interface describes an element within the spatial index
   * and is used for converting arbitrary elements to elements that can be used
   * with the data structure.
   *
   * @param <E> The original type of the element.
   * @param <S> The converted type of the element.
   */
  @FunctionalInterface
  public interface Element<E, S extends Shape> {
    /**
     * Convert an arbitrary element to an element that can be used within the
     * spatial index.
     *
     * @param element The element to convert.
     * @return        The converted element.
     */
    S toShape(final E element);
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
