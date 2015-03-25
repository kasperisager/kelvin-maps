/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// General utilities
import java.util.Arrays;
import java.util.Comparator;

/**
 * Dimensional tree class.
 *
 * @see <a href="http://en.wikipedia.org/wiki/K-d_tree">
 *      http://en.wikipedia.org/wiki/K-d_tree</a>
 *
 * @param <E> The type of elements contained within the tree.
 */
public final class DimensionalTree<E> {
  /**
   * Array of comparators for each of the dimensions in the tree.
   */
  private final Comparator<E>[] comparators;

  /**
   * Number of dimensions in the tree.
   */
  private final int dimensions;

  /**
   * The point at which to stop subdividing the tree.
   */
  private final int cutoff;

  /**
   * The root node of the dimensional tree.
   */
  private final Node root;

  /**
   * Initialize a dimensional tree.
   *
   * @param elements    The elements to store in the tree.
   * @param cutoff      The point at which to stop subdividing the tree.
   * @param comparators The comparators for each of the dimensions in the tree.
   */
  @SafeVarargs
  @SuppressWarnings("unchecked")
  public DimensionalTree(
    final Collection<E> elements,
    final int cutoff,
    final Comparator<E>... comparators
  ) {
    this.cutoff = cutoff;
    this.comparators = comparators;
    this.dimensions = this.comparators.length;

    this.root = this.treeify(
      (E[]) elements.toArray(), // Elements
      0,                        // Depth
      0,                        // Starting index
      elements.size() - 1       // Ending index
    );
  }

  /**
   * Initialize a dimensional tree without a cutoff.
   *
   * @param elements    The elements to store in the tree.
   * @param comparators The comparators for each of the dimensions in the tree.
   */
  @SafeVarargs
  @SuppressWarnings("unchecked")
  public DimensionalTree(
    final Collection<E> elements,
    final Comparator<E>... comparators
  ) {
    this(elements, 0, comparators);
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
  @SuppressWarnings("unchecked")
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
      return new Leaf(elements[start]);
    }

    // If we're within the cufoff length, store all the remaining elements in
    // a bucket.
    if (length <= this.cutoff) {
      return new Bucket(Arrays.copyOfRange(elements, start, end + 1));
    }

    // Get the axis to use for the current depth.
    int axis = depth % this.dimensions;

    // Sort the element between the specified indices using the comparator
    // associated with the current axis.
    Arrays.sort(elements, start, end, this.comparators[axis]);

    // Compute the median of the elements.
    int median = start + length / 2;

    return new Branch(
      elements[median],

      // Recursively treeify all elements before the median.
      this.treeify(elements, depth + 1, start, median - 1),

      // Recursively treeify all elements after the median.
      this.treeify(elements, depth + 1, median + 1, end)
    );
  }


  /**
   * Check if the tree contains the specified element.
   *
   * @param element The element to search for.
   * @return        A boolean indicating whether or not the tree contains the
   *                specified element.
   */
  public boolean contains(final E element) {
    if (element == null) {
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
  @SuppressWarnings("unchecked")
  public Collection<E> range(final Bounds<E>... bounds) {
    if (bounds == null) {
      return null;
    }

    if (bounds.length != this.dimensions) {
      throw new RuntimeException(
        "The number of bounds (" + bounds.length + ") must match the number of"
      + " dimensions in the tree (" + this.dimensions + ")"
      );
    }

    Collection<E> elements = new ArrayList<>();

    this.root.range(0, elements, bounds);

    return elements;
  }

  /**
   * The {@link Bounds} interface describes the bounds of a range search within
   * the tree.
   */
  @FunctionalInterface
  public interface Bounds<E> {
    /**
     * Check if the bounds contain the specified element.
     *
     * @param element The element to check containment of.
     * @return        A negative integer, zero, or a positive integer as the
     *                element is smaller, within, or larger than the bounds.
     */
    int contains(final E element);
  }

  /**
   * The {@link Node} class describes a node within a dimensional tree.
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
     */
    public abstract void range(
      final int depth,
      final Collection<E> elements,
      final Bounds<E>[] bounds
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

      // Get the axis to use for the current depth.
      int axis = depth % DimensionalTree.this.dimensions;

      // Check if the element is "contained" within the element associated with
      // the current node.
      int contains = DimensionalTree.this.comparators[axis].compare(
        this.element, element
      );

      // Look in the left child of the node if the element we're looking for
      // lies to the left of the element we're currently looking at.
      if (contains > 0) {
        if (this.left == null) {
          return false;
        }

        return this.left.contains(depth + 1, element);
      }

      // Look in the right child of the node if the element we're looking for
      // lies to the right of the element we're currently looking at.
      if (contains < 0) {
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
     * @param depth     The current tree depth.
     * @param elements  The collection to add the found elements to.
     * @param bounds    The bounds to search for elements within.
     */
    public void range(
      final int depth,
      final Collection<E> elements,
      final Bounds<E>[] bounds
    ) {
      int axis = depth % DimensionalTree.this.dimensions;

      int contains = bounds[axis].contains(this.element);

      if (contains < 0 || contains == 0) {
        if (this.left != null) {
          this.left.range(depth + 1, elements, bounds);
        }
      }

      if (contains > 0 || contains == 0) {
        if (this.right != null) {
          this.right.range(depth + 1, elements, bounds);
        }
      }

      if (contains == 0) {
        for (Bounds<E> b: bounds) {
          if (b.contains(this.element) != 0) {
            return;
          }
        }

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
      if (element == null) {
        return false;
      }

      return this.element.equals(element);
    }

    /**
     * Find all elements within the range of the specified bounds.
     *
     * @param depth     The current tree depth.
     * @param elements  The collection to add the found elements to.
     * @param bounds    The bounds to search for elements within.
     */
    public void range(
      final int depth,
      final Collection<E> elements,
      final Bounds<E>[] bounds
    ) {
      for (Bounds<E> b: bounds) {
        if (b.contains(this.element) != 0) {
          return;
        }
      }

      elements.add(this.element);
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
     * @param depth     The current tree depth.
     * @param elements  The collection to add the found elements to.
     * @param bounds    The bounds to search for elements within.
     */
    public void range(
      final int depth,
      final Collection<E> elements,
      final Bounds<E>[] bounds
    ) {
      outer:
      for (E found: this.elements) {
        for (Bounds<E> b: bounds) {
          if (b.contains(found) != 0) {
            continue outer;
          }
        }

        elements.add(found);
      }
    }
  }
}
