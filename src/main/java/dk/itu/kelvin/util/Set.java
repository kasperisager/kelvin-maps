/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

/**
 * <h2>Sightly simplified implementation of Set interface</h2>
 * <p>
 * A {@link Collection} that contains no duplicate elements. More formally,
 * sets contain no pair of elements {@code e1} and {@code e2} such that
 * {@code e1.equals(e2)}, and at most one {@code null}, even though there
 * isn't any implementing classes that allow {@code null elements}. As implied
 * by its name, this interface models the mathematical set abstraction.
 *
 * <p>
 * {@code Set} doesn't specify any additional methods that hasn't been
 * specified in the {@link Collection} interface.
 *
 * @param <E> The type of elements contained within the set.
 *
 * @version 1.0.0
 */
public interface Set<E> extends Collection<E> {
  // No extra methods required.
}
