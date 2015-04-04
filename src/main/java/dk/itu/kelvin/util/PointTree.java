/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// General utilities
import java.util.Arrays;

// I/O utilities
import java.io.Serializable;

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
  private Node root;

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

    List<E> elements = new ArrayList<>();

    this.root.range(0, elements, bounds, filter);

    return elements;
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
  private Node partition(
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
      return new Bucket(Arrays.copyOfRange(elements, start, end));
    }

    // Sort the element between the specified indices using the comparator
    // associated with the current axis.
    Arrays.sort(elements, start, end, (a, b) -> {
      return this.compare(depth, a, b);
    });

    // Compute the median of the elements.
    int median = start + length / 2;

    return new Branch(
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
   * @param depth The tree depth.
   * @param a     The first element.
   * @param b     The second element.
   * @return      A negative integer, zero, or a positive integer as the first
   *              element is smaller, equal to, or larger than the second
   *              element.
   */
  private int compare(final int depth, final E a, final E b) {
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
   * @param depth   The tree depth.
   * @param element The element.
   * @param bounds  The bounds.
   * @return        A negative integer, zero, or a positive integer as the
   *                element is smaller, within, or larger than the bounds.
   */
  private int compare(final int depth, final E element, final Bounds bounds) {
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
      if (element.x() > bounds.minX()) {
        return -1;
      }

      if (element.x() < bounds.maxX()) {
        return 1;
      }
    }
    else {
      if (element.y() > bounds.minY()) {
        return -1;
      }

      if (element.y() < bounds.maxY()) {
        return 1;
      }
    }

    return 0;
  }

  /**
   * Check if an element intersects the specified bounds.
   *
   * @param element The element.
   * @param bounds  The bounds.
   * @return        A boolean indicating whether or not the element intersects
   *                the specified bounds.
   */
  private boolean intersects(final E element, final Bounds bounds) {
    if (element == null || bounds == null) {
      return false;
    }

    return bounds.contains(element.x(), element.y());
  }

  /**
   * The {@link Node} class describes a node within a point tree.
   */
  private abstract class Node {
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
     * @param depth     The current tree depth.
     * @param elements  The collection to add the found elements to.
     * @param bounds    The bounds to search for elements within.
     * @param filter    The filter to apply to the range search.
     */
    public abstract void range(
      final int depth,
      final Collection<E> elements,
      final Bounds bounds,
      final Filter<E> filter
    );
  }

  /**
   * A {@link Branch} is a {@link Node} that contains an element and either one
   * or two children {@link Node Nodes}.
   */
  private final class Branch extends Node {
    /**
     * The element associated with the branch.
     */
    private final E element;

    /**
     * The left neighbouring node.
     */
    private final Node left;

    /**
     * The right neighbouring node.
     */
    private final Node right;

    /**
     * Initialize a new branch.
     *
     * @param element The element associated with the branch.
     * @param left    The left neighbouring node.
     * @param right   The right neighbouring node.
     */
    public Branch(final E element, final Node left, final Node right) {
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
      int contains = PointTree.this.compare(depth, this.element, element);

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
     * @param depth     The current tree depth.
     * @param elements  The collection to add the found elements to.
     * @param bounds    The bounds to search for elements within.
     * @param filter    The filter to apply to the range search.
     */
    public void range(
      final int depth,
      final Collection<E> elements,
      final Bounds bounds,
      final Filter<E> filter
    ) {
      if (
        this.element == null
        || elements == null
        || bounds == null
        || filter == null
      ) {
        return;
      }

      int contains = PointTree.this.compare(depth, this.element, bounds);

      if (contains < 0 || contains == 0) {
        if (this.left != null) {
          this.left.range(depth + 1, elements, bounds, filter);
        }
      }

      if (contains > 0 || contains == 0) {
        if (this.right != null) {
          this.right.range(depth + 1, elements, bounds, filter);
        }
      }

      if (
        contains == 0
        // Is the element included in the filter?
        && filter.include(this.element)
        // Does the element intersect with the search bounds?
        && PointTree.this.intersects(this.element, bounds)
      ) {
        elements.add(this.element);
      }
    }
  }

  /**
   * A {@link Bucket} is a {@link Node} that contains a list of elements rather
   * than just a single element.
   */
  private final class Bucket extends Node {
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
     * @param depth     The current tree depth.
     * @param elements  The collection to add the found elements to.
     * @param bounds    The bounds to search for elements within.
     * @param filter    The filter to apply to the range search.
     */
    public void range(
      final int depth,
      final Collection<E> elements,
      final Bounds bounds,
      final Filter<E> filter
    ) {
      if (
        this.elements == null
        || elements == null
        || bounds == null
        || filter == null
      ) {
        return;
      }

      for (E found: this.elements) {
        if (
          // Is the element included in the filter?
          filter.include(found)
          // Does the element intersect with the search bounds?
          && PointTree.this.intersects(found, bounds)
        ) {
          elements.add(found);
        }
      }
    }
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
}
