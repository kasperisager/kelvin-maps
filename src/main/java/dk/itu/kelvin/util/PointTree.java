/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// General utilities
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

// I/O utilities
import java.io.Serializable;

// Functional utilities
import dk.itu.kelvin.util.function.Filter;

/**
 * Point tree class.
 *
 * @see <a href="http://en.wikipedia.org/wiki/K-d_tree">
 *      http://en.wikipedia.org/wiki/K-d_tree</a>
 *
 * @param <E> The type of elements stored within the point tree.
 */
public class PointTree<E extends PointTree.Index> implements SpatialIndex<E> {
  /**
   * UID for identifying serialized objects.
   */
  private static final long serialVersionUID = 730;

  /**
   * The maximum size of point buckets.
   */
  private static final int BUCKET_MAXIMUM = 2048;

  /**
   * The minimum size of points bucket.
   */
  private static final int BUCKET_MINIMUM = BUCKET_MAXIMUM / 2;

  /**
   * The size of the point tree.
   */
  private int size;

  /**
   * The root node of the point tree.
   */
  private Node<E> root;

  /**
   * Initialize a new point tree bulk-loaded with the specified collection of
   * elements.
   *
   * @param elements The elements to add to the tree.
   */
  public PointTree(final Collection<E> elements) {
    @SuppressWarnings("unchecked")
    E[] array = (E[]) new Index[elements.size()];

    int i = 0;

    for (E element: elements) {
      array[i++] = element;
    }

    this.root = this.partition(array, 0, 0, array.length);
    this.size = array.length;
  }

  /**
   * Get the size of the point tree.
   *
   * @return The size of the point tree.
   */
  public final int size() {
    return this.size;
  }

  /**
   * Check if the point tree is empty.
   *
   * @return A boolean indicating whether or not the point tree is empty.
   */
  public final boolean isEmpty() {
    return this.size == 0;
  }

  /**
   * Check if the tree contains the specified element.
   *
   * @param element The element to search for.
   * @return        A boolean indicating whether or not the tree contains the
   *                specified element.
   */
  public final boolean contains(final E element) {
    if (this.root == null || element == null) {
      return false;
    }

    return this.root.contains(0, element);
  }

  /**
   * Find all elements within the range of the specified bounds.
   *
   * @param bounds  The bounds to search for elements within.
   * @return        A list of elements contained within the range of the
   *                specified bounds.
   */
  public final List<E> range(final Bounds bounds) {
    if (this.root == null || bounds == null) {
      return null;
    }

    return this.range(bounds, (element) -> {
      return true;
    });
  }

  /**
   * Find all elements within the range of the specified bounds.
   *
   * @param bounds  The bounds to search for elements within.
   * @param filter  The filter to apply to the range search.
   * @return        A list of elements contained within the range of the
   *                specified bounds.
   */
  public final List<E> range(final Bounds bounds, final Filter<E> filter) {
    if (this.root == null || bounds == null || filter == null) {
      return null;
    }

    return this.root.range(0, bounds, filter);
  }

  /**
   * Find the element closest to the specified point.
   *
   * @param point The point to look for elements near.
   * @return      The element closest to the specified point.
   */
  public final E nearest(final Point point) {
    throw new UnsupportedOperationException();
  }

  /**
   * Find the element included in the filter closest to the specified point.
   *
   * @param point   The point to look for elements near.
   * @param filter  The filter to apply to the search.
   * @return        The element closest to the specified point.
   */
  public final E nearest(final Point point, final Filter<E> filter) {
    throw new UnsupportedOperationException();
  }

  /**
   * Partition the given elements at the specified depth between the given
   * indices.
   *
   * @param elements  The elements to partition.
   * @param depth     The current depth of the tree.
   * @param start     The starting index of the operation.
   * @param end       The ending index of the operation.
   * @return          A partitioned {@link Node} instance.
   */
  private Node<E> partition(
    final E[] elements,
    final int depth,
    final int start,
    final int end
  ) {
    if (elements == null) {
      return null;
    }

    int length = end - start;

    // Bail out if there are no elements left to partition.
    if (length < 0) {
      return null;
    }

    // If we're within the cufoff length, store all the remaining elements in
    // a bucket.
    if (length <= BUCKET_MAXIMUM) {
      return new Bucket<E>(Arrays.copyOfRange(elements, start, end));
    }

    // Sort the element between the specified indices using the comparator
    // associated with the current axis.
    Arrays.sort(elements, start, end, (a, b) -> {
      return PointTree.compare(depth, a, b);
    });

    // Compute the median of the elements.
    int median = start + length / 2;

    return new Branch<E>(
      elements[median],

      // Recursively partition all elements before the median.
      this.partition(elements, depth + 1, start, median),

      // Recursively partition all elements after the median.
      this.partition(elements, depth + 1, median + 1, end)
    );
  }

  /**
   * Compare two elements at the specified tree depth.
   *
   * @param <E>   The type of elements to compare.
   * @param depth The tree depth.
   * @param a     The first element.
   * @param b     The second element.
   * @return      A negative integer, zero, or a positive integer as the first
   *              element is smaller, equal to, or larger than the second
   *              element.
   */
  private static <E extends Index> int compare(
    final int depth,
    final E a,
    final E b
  ) {
    if (a == null && b == null) {
      return 0;
    }

    if (a == null) {
      return -1;
    }

    if (b == null) {
      return 1;
    }

    if (depth % 2 == 0) {
      return Double.compare(a.x(), b.x());
    }
    else {
      return Double.compare(a.y(), b.y());
    }
  }

  /**
   * Compare an element to a set of bounds at the specified depth.
   *
   * @param <E>     The type of elements to compare.
   * @param depth   The tree depth.
   * @param element The element.
   * @param bounds  The bounds.
   * @return        A negative integer, zero, or a positive integer as the
   *                element is smaller, within, or larger than the bounds.
   */
  private static <E extends Index> int compare(
    final int depth,
    final E element,
    final Bounds bounds
  ) {
    if (element == null && bounds == null) {
      return 0;
    }

    if (element == null) {
      return -1;
    }

    if (bounds == null) {
      return 1;
    }

    if (depth % 2 == 0) {
      if (element.x() > bounds.min().x()) {
        return -1;
      }

      if (element.x() < bounds.max().x()) {
        return 1;
      }
    }
    else {
      if (element.y() > bounds.min().y()) {
        return -1;
      }

      if (element.y() < bounds.max().y()) {
        return 1;
      }
    }

    return 0;
  }

  /**
   * Check if an element intersects the specified bounds.
   *
   * @param <E>     The type of elements to check intersection of.
   * @param element The element.
   * @param bounds  The bounds.
   * @return        A boolean indicating whether or not the element intersects
   *                the specified bounds.
   */
  private static <E extends Index> boolean intersects(
    final E element,
    final Bounds bounds
  ) {
    if (element == null || bounds == null) {
      return false;
    }

    return bounds.contains(new Point(element.x(), element.y()));
  }

  /**
   * The {@link Index} interface describes an object that is indexable by the
   * point tree.
   */
  public interface Index extends Serializable {
    /**
     * Get the x-coordinate of the object.
     *
     * @return The x-coordinate of the object.
     */
    float x();

    /**
     * Get the y-coordinate of the object.
     *
     * @return The y-coordinate of the object.
     */
    float y();
  }

  /**
   * The {@link Node} class describes a node within a point tree.
   *
   * @param <E> The type of elements stored within the node.
   */
  private abstract static class Node<E extends Index> implements Serializable {
    /**
     * UID for identifying serialized objects.
     */
    private static final long serialVersionUID = 731;

    /**
     * Check if the node contains the specified element.
     *
     * @param depth   The current tree depth.
     * @param element The element to look for.
     * @return        A boolean indicating whether or not the node contains the
     *                specified element.
     */
    public abstract boolean contains(final int depth, final E element);

    /**
     * Find all elements within the range of the specified bounds.
     *
     * @param depth   The current tree depth.
     * @param bounds  The bounds to search for elements within.
     * @param filter  The filter to apply to the range search.
     * @return        All elements within range of the specified bounds.
     */
    public abstract List<E> range(
      final int depth,
      final Bounds bounds,
      final Filter<E> filter
    );
  }

  /**
   * A {@link Branch} is a {@link Node} that contains an element and either one
   * or two children {@link Node Nodes}.
   *
   * @param <E> The type of elements stored within the branch.
   */
  private static final class Branch<E extends Index> extends Node<E> {
    /**
     * UID for identifying serialized objects.
     */
    private static final long serialVersionUID = 732;

    /**
     * The element associated with the branch.
     */
    private final E element;

    /**
     * The left neighbouring node.
     */
    private final Node<E> left;

    /**
     * The right neighbouring node.
     */
    private final Node<E> right;

    /**
     * Initialize a new branch.
     *
     * @param element The element associated with the branch.
     * @param left    The left neighbouring node.
     * @param right   The right neighbouring node.
     */
    public Branch(final E element, final Node<E> left, final Node<E> right) {
      this.element = element;
      this.left = left;
      this.right = right;
    }

    /**
     * Check if the branch contains the specified element.
     *
     * @param depth   The current tree depth.
     * @param element The element to look for.
     * @return        A boolean indicating whether or not the branch contains
     *                the specified element.
     */
    public boolean contains(final int depth, final E element) {
      if (this.element == null || element == null) {
        return false;
      }

      if (this.element.equals(element)) {
        return true;
      }

      // Check if the element is "contained" within the element associated with
      // the current node.
      int contains = PointTree.compare(depth, this.element, element);

      // Look in the left child of the node if the element we're looking for
      // lies to the left of the element we're currently looking at.
      if (contains < 0) {
        if (this.left == null) {
          return false;
        }

        return this.left.contains(depth + 1, element);
      }

      // Look in the right child of the node if the element we're looking for
      // lies to the right of the element we're currently looking at.
      if (contains > 0) {
        if (this.right == null) {
          return false;
        }

        return this.right.contains(depth + 1, element);
      }

      return false;
    }

    /**
     * Find all elements within the range of the specified bounds.
     *
     * @param depth   The current tree depth.
     * @param bounds  The bounds to search for elements within.
     * @param filter  The filter to apply to the range search.
     * @return        All elements within range of the specified bounds.
     */
    public List<E> range(
      final int depth,
      final Bounds bounds,
      final Filter<E> filter
    ) {
      List<E> elements = new ArrayList<>();

      if (
        this.element == null
        || bounds == null
        || filter == null
      ) {
        return elements;
      }

      int contains = PointTree.compare(depth, this.element, bounds);

      if (contains < 0 || contains == 0) {
        if (this.left != null) {
          elements.addAll(this.left.range(depth + 1, bounds, filter));
        }
      }

      if (contains > 0 || contains == 0) {
        if (this.right != null) {
          elements.addAll(this.right.range(depth + 1, bounds, filter));
        }
      }

      if (
        contains == 0
        // Is the element included in the filter?
        && filter.include(this.element)
        // Does the element intersect with the search bounds?
        && PointTree.intersects(this.element, bounds)
      ) {
        elements.add(this.element);
      }

      return elements;
    }
  }

  /**
   * A {@link Bucket} is a {@link Node} that contains a list of elements rather
   * than just a single element.
   *
   * @param <E> The type of elements stored within the bucket.
   */
  private static final class Bucket<E extends Index> extends Node<E> {
    /**
     * UID for identifying serialized objects.
     */
    private static final long serialVersionUID = 733;

    /**
     * The elements associated with the bucket.
     */
    private final E[] elements;

    /**
     * Initialize a new bucket.
     *
     * @param elements The elements associated with the bucket.
     */
    public Bucket(final E[] elements) {
      this.elements = elements;
    }

    /**
     * Check if the bucket contains the specified element.
     *
     * @param depth   The current tree depth.
     * @param element The element to look for.
     * @return        A boolean indicating whether or not the bucket contains
     *                the specified element.
     */
    public boolean contains(final int depth, final E element) {
      if (this.elements == null || element == null) {
        return false;
      }

      for (E found: this.elements) {
        if (element.equals(found)) {
          return true;
        }
      }

      return false;
    }

    /**
     * Find all elements within the range of the specified bounds.
     *
     * @param depth   The current tree depth.
     * @param bounds  The bounds to search for elements within.
     * @param filter  The filter to apply to the range search.
     * @return        All elements within range of the specified bounds.
     */
    public List<E> range(
      final int depth,
      final Bounds bounds,
      final Filter<E> filter
    ) {
      List<E> elements = new ArrayList<>();

      if (
        this.elements == null
        || bounds == null
        || filter == null
      ) {
        return elements;
      }

      for (E found: this.elements) {
        if (
          // Is the element included in the filter?
          filter.include(found)
          // Does the element intersect with the search bounds?
          && PointTree.intersects(found, bounds)
        ) {
          elements.add(found);
        }
      }

      return elements;
    }
  }
}
