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
   * The root node of the dimensional tree.
   */
  private final Node root;

  /**
   * Initialize a dimensional tree.
   *
   * @param elements    The elements to store in the tree.
   * @param comparators The comparators for each of the dimensions in the tree.
   */
  @SuppressWarnings("unchecked")
  public DimensionalTree(
    final Collection<E> elements,
    final Comparator<E>... comparators
  ) {
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
      return new Node(elements[start + length]);
    }

    // Get the axis to use for the current depth.
    int axis = depth % this.dimensions;

    // Sort the element between the specified indices using the comparator
    // associated with the current axis.
    Arrays.sort(elements, start, end, this.comparators[axis]);

    // Compute the median of the elements.
    int median = start + length / 2;

    return new Node(
      elements[median],
      this.treeify(elements, depth + 1, start, median - 1),
      this.treeify(elements, depth + 1, median + 1, end)
    );
  }

  /**
   * Check if the given node contains the specified element.
   *
   * @param depth   The current tree depth.
   * @param node    The node to search through.
   * @param element The element to search for.
   * @return        A boolean indicating whether or not the node contains the
   *                specified element.
   */
  private boolean contains(final int depth, final Node node, final E element) {
    if (node == null || element == null) {
      return false;
    }

    // Get the axis to use for the current depth.
    int axis = depth % this.dimensions;

    // Check if the element is "contained" within the element associated with
    // the current node.
    int contains = this.comparators[axis].compare(node.element, element);

    // Look in the left child of the node if the element we're looking for lies
    // to the left of the element we're currently looking at.
    if (contains > 0) {
      return this.contains(depth + 1, node.left, element);
    }

    // Look in the right child of the node if the element we're looking for lies
    // to the right of the element we're currently looking at.
    if (contains < 0) {
      return this.contains(depth + 1, node.right, element);
    }

    // Otherwise, we've found the element we're looking for.
    return true;
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

    return this.contains(0, this.root, element);
  }

  /**
   * Look for elements at the specified depth within the given bounds of the
   * specified node.
   *
   * @param depth     The current tree depth.
   * @param bounds    The bounds to search within.
   * @param elements  The list of found elements.
   * @param node      The node to search through.
   */
  private void range(
    final int depth,
    final Bounds<E>[] bounds,
    final List<E> elements,
    final Node node
  ) {
    if (node == null) {
      return;
    }

    int axis = depth % this.dimensions;

    int contains = bounds[axis].contains(node.element);

    if (contains == 0) {
      elements.add(node.element);
    }

    if (contains < 0 || contains == 0) {
      this.range(depth + 1, bounds, elements, node.left);
    }

    if (contains > 0 || contains == 0) {
      this.range(depth + 1, bounds, elements, node.right);
    }
  }

  /**
   * Find all elements within the range of the specified bounds.
   *
   * @param bounds  The bounds to search for elements within.
   * @return        A list of elements contained within the range of the
   *                specified bounds.
   */
  @SuppressWarnings("unchecked")
  public List<E> range(final Bounds<E>... bounds) {
    if (bounds.length != this.dimensions) {
      throw new RuntimeException(
        "The number of bounds (" + bounds.length + ") must match the number of"
      + " dimensions in the tree (" + this.dimensions + ")"
      );
    }

    // Create a list to hold the elements contained within the bounds.
    List<E> elements = new ArrayList<>();

    // Look for elements contained within the range starting at the root
    // element.
    this.range(0, bounds, elements, this.root);

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
  private final class Node {
    /**
     * The element associated with the node.
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
     * Initialize a new node.
     *
     * @param element The element associated with the node.
     * @param left    The left neighbouring node.
     * @param right   The right neighbouring node.
     */
    public Node(final E element, final Node left, final Node right) {
      this.element = element;
      this.left = left;
      this.right = right;
    }

    /**
     * Initialize a new leaf node.
     *
     * @param element The element associated with the node.
     */
    public Node(final E element) {
      this(element, null, null);
    }

    /**
     * Check if the node is a leaf.
     *
     * @return A boolean indicating whether or not the node is a leaf.
     */
    public boolean isLeaf() {
      return this.left == null && this.right == null;
    }
  }
}
