/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// General utilities
import java.util.Arrays;
import java.util.Comparator;

/**
 * Collections class.
 */
public final class Collections {
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
  private Collections() {
    super();
  }

  /**
   * Sort a list of comparable elements.
   *
   * <p>
   * The list will be sorted in-place.
   *
   * @param <T>   The type of elements to sort.
   * @param list  The list of comparable elements to sort.
   */
  @SuppressWarnings("unchecked")
  public static <T extends Comparable<? super T>> void sort(
    final List<T> list
  ) {
    T[] elements = (T[]) list.toArray();

    Arrays.sort(elements);

    for (int i = 0; i < elements.length; i++) {
      list.set(i, elements[i]);
    }
  }

  /**
   * Sort a list of elements using the specified comparator.
   *
   * <p>
   * The list will be sorted in-place.
   *
   * @param <T>         The type of elements to sort.
   * @param list        The list of elements to sort.
   * @param comparator  The comparator to use for sorting the list.
   */
  @SuppressWarnings("unchecked")
  public static <T> void sort(
    final List<T> list,
    final Comparator<? super T> comparator
  ) {
    T[] elements = (T[]) list.toArray();

    Arrays.sort(elements, comparator);

    for (int i = 0; i < elements.length; i++) {
      list.set(i, elements[i]);
    }
  }
}