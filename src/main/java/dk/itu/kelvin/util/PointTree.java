/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// General utilities
import java.util.Arrays;

// Math
import dk.itu.kelvin.math.Geometry;
import static dk.itu.kelvin.math.Geometry.Bounds;
import static dk.itu.kelvin.math.Geometry.Point;

/**
 * Point tree class.
 *
 * @param <E> The type of elements stored within the point tree.
 */
public class PointTree<E> implements SpatialIndex<E> {
  /**
   * The maximum size of point buckets.
   */
  private static final int BUCKET_SIZE = 1000;

  /**
   * The size of the point tree.
   */
  private int size;

  /**
   * The element descriptor.
   */
  private Element<E, Point> element;

  /**
   * The root node of the point tree.
   */
  private Node root;

  /**
   * Initialize a new point tree.
   *
   * @param elements  The elements to add to the tree.
   * @param element   The element descriptor to use.
   */
  @SuppressWarnings("unchecked")
  public PointTree(
    final Collection<E> elements,
    final Element<E, Point> element
  ) {
    this.element = element;

    this.root = this.treeify(
      (E[]) elements.toArray(), // Elements
      0,                        // Depth
      0,                        // Starting index
      elements.size() - 1       // Ending index
    );
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
    if (element == null) {
      return false;
    }

    return this.root.contains(0, element);
  }

  /**
   * Find all elements within the range of the specified bounds.
   *
   * @param <B>     The type of bounds to use.
   * @param bounds  The bounds to search for elements within.
   * @return        A list of elements contained within the range of the
   *                specified bounds.
   */
  public final <B extends Geometry.Bounds> List<E> range(final B bounds) {
    if (bounds == null) {
      return null;
    }

    return this.range(bounds, (element) -> {
      return true;
    });
  }

  /**
   * Find all elements within the range of the specified bounds.
   *
   * @param <B>     The type of bounds to use.
   * @param bounds  The bounds to search for elements within.
   * @param filter  The filter to apply to the range search.
   * @return        A list of elements contained within the range of the
   *                specified bounds.
   */
  public final <B extends Geometry.Bounds> List<E> range(
    final B bounds,
    final Filter<E> filter
  ) {
    if (bounds == null) {
      return null;
    }

    List<E> elements = new ArrayList<>();

    this.root.range(0, elements, bounds, filter);

    return elements;
  }

  /**
   * Treeify the given elements at the specified depth between the given
   * indices.
   *
   * @param elements  The elements to treeify.
   * @param depth     The current depth of the tree.
   * @param start     The starting index of the operation.
   * @param end       The ending index of the operation.
   * @return          A treeified node if any non-treeified elements were left
   *                  in the array of elements.
   */
  private Node treeify(
    final E[] elements,
    final int depth,
    final int start,
    final int end
  ) {
    int length = end - start;

    // Bail out if there are no elements left to treeify.
    if (length < 0) {
      return null;
    }

    // Return a leaf node if we've reached a single element.
    if (length == 0) {
      this.size++;
      return new Leaf(elements[start]);
    }

    // If we're within the cufoff length, store all the remaining elements in
    // a bucket.
    if (length <= BUCKET_SIZE) {
      this.size += length;
      return new Bucket(Arrays.copyOfRange(elements, start, end + 1));
    }

    // Sort the element between the specified indices using the comparator
    // associated with the current axis.
    Arrays.sort(elements, start, end + 1, (a, b) -> {
      return this.compare(depth, a, b);
    });

    // Compute the median of the elements.
    int median = start + length / 2;

    this.size++;

    return new Branch(
      elements[median],

      // Recursively treeify all elements before the median.
      this.treeify(elements, depth + 1, start, median - 1),

      // Recursively treeify all elements after the median.
      this.treeify(elements, depth + 1, median + 1, end)
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
    Point ap = this.element.toShape(a);
    Point bp = this.element.toShape(b);

    if (depth % 2 == 0) {
      return Double.compare(ap.x(), bp.x());
    }
    else {
      return Double.compare(ap.y(), bp.y());
    }
  }

  /**
   * Compare an element to a set of bounds at the specified depth.
   *
   * @param <B>     The type of bounds to use.
   * @param depth   The tree depth.
   * @param element The element.
   * @param bounds  The bounds.
   * @return        A negative integer, zero, or a positive integer as the
   *                element is smaller, within, or larger than the bounds.
   */
  private <B extends Geometry.Bounds> int compare(
    final int depth,
    final E element,
    final B bounds
  ) {
    Point ep = this.element.toShape(element);

    if (depth % 2 == 0) {
      if (ep.x() > bounds.min().x()) {
        return -1;
      }

      if (ep.x() < bounds.max().x()) {
        return 1;
      }
    }
    else {
      if (ep.y() > bounds.min().y()) {
        return -1;
      }

      if (ep.y() < bounds.max().y()) {
        return 1;
      }
    }

    return 0;
  }

  /**
   * Check if an element intersects the specified bounds.
   *
   * @param <B>     The type of bounds to use.
   * @param element The element.
   * @param bounds  The bounds.
   * @return        A boolean indicating whether or not the element intersects
   *                the specified bounds.
   */
  private <B extends Geometry.Bounds> boolean intersects(
    final E element,
    final B bounds
  ) {
    return bounds.contains(this.element.toShape(element));
  }

  /**
   * The {@link Point} class describes a point within the point tree.
   *
   * <p>
   * It has been subclassed from {@link Geometry.Point} for ease of access and
   * to allow easy refactoring should this be needed.
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
   * The {@link Bounds} class describes the bounds of range queries with the
   * point tree.
   *
   * <p>
   * It has been subclassed from {@link Geometry.Bounds} for ease of access and
   * to allow easy refactoring should this be needed.
   */
  public static final class Bounds extends Geometry.Bounds {
    /**
     * Initialize a new set of bounds.
     *
     * @param min The minimum point of the bounds.
     * @param max The maximum point of the bounds.
     */
    public Bounds(final Point min, final Point max) {
      super(min, max);
    }
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
     * @param <B>       The type of bounds to use.
     * @param depth     The current tree depth.
     * @param elements  The collection to add the found elements to.
     * @param bounds    The bounds to search for elements within.
     * @param filter    The filter to apply to the range search.
     */
    public abstract <B extends Geometry.Bounds> void range(
      final int depth,
      final Collection<E> elements,
      final B bounds,
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
      if (element == null) {
        return false;
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

      // Otherwise, check if we've found a match.
      return this.element.equals(element);
    }

    /**
     * Find all elements within the range of the specified bounds.
     *
     * @param <B>       The type of bounds to use.
     * @param depth     The current tree depth.
     * @param elements  The collection to add the found elements to.
     * @param bounds    The bounds to search for elements within.
     * @param filter    The filter to apply to the range search.
     */
    public <B extends Geometry.Bounds> void range(
      final int depth,
      final Collection<E> elements,
      final B bounds,
      final Filter<E> filter
    ) {
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
        // Is the element included in the filter?
        filter.include(this.element)
        &&
        // Does the element intersect with the search bounds?
        PointTree.this.intersects(this.element, bounds)
      ) {
        elements.add(this.element);
      }
    }
  }

  /**
   * A {@link Leaf} is a {@link Node} that contains an element and nothing else.
   */
  private final class Leaf extends Node {
    /**
     * The element associated with the leaf.
     */
    private final E element;

    /**
     * Initialize a new leaf.
     *
     * @param element The element associated with the leaf.
     */
    public Leaf(final E element) {
      this.element = element;
    }

    /**
     * Check if the leaf contains the specified element.
     *
     * @param depth   The current tree depth.
     * @param element The element to look for.
     * @return        A boolean indicating whether or not the leaf contains the
     *                specified element.
     */
    public boolean contains(final int depth, final E element) {
      if (this.element == null || element == null) {
        return false;
      }

      return this.element.equals(element);
    }

    /**
     * Find all elements within the range of the specified bounds.
     *
     * @param <B>       The type of bounds to use.
     * @param depth     The current tree depth.
     * @param elements  The collection to add the found elements to.
     * @param bounds    The bounds to search for elements within.
     * @param filter    The filter to apply to the range search.
     */
    public <B extends Geometry.Bounds> void range(
      final int depth,
      final Collection<E> elements,
      final B bounds,
      final Filter<E> filter
    ) {
      if (
        // Is the element included in the filter?
        filter.include(this.element)
        &&
        // Does the element intersect with the search bounds?
        PointTree.this.intersects(this.element, bounds)
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
      if (element == null) {
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
     * @param <B>       The type of bounds to use.
     * @param depth     The current tree depth.
     * @param elements  The collection to add the found elements to.
     * @param bounds    The bounds to search for elements within.
     * @param filter    The filter to apply to the range search.
     */
    public <B extends Geometry.Bounds> void range(
      final int depth,
      final Collection<E> elements,
      final B bounds,
      final Filter<E> filter
    ) {
      for (E found: this.elements) {
        if (
          // Is the element included in the filter?
          filter.include(found)
          &&
          // Does the element intersect with the search bounds?
          PointTree.this.intersects(found, bounds)
        ) {
          elements.add(found);
        }
      }
    }
  }
}
