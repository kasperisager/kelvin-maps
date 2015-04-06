/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util.function;

/**
 * The {@link Filter} interface describes a filter that can be applied to an
 * element.
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
