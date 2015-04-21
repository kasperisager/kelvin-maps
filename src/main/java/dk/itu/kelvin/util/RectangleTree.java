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
 * Rectangle tree class.
 *
 * @see <a href="http://en.wikipedia.org/wiki/R-tree">
 *      http://en.wikipedia.org/wiki/R-tree</a>
 *
 * @see <a href="http://www-db.deis.unibo.it/courses/SI-LS/papers/Gut84.pdf">
 *      http://www-db.deis.unibo.it/courses/SI-LS/papers/Gut84.pdf</a>
 *
 * @param <E> The type of elements stored within the rectangle tree.
 */
public class RectangleTree<E extends RectangleTree.Index>
  implements SpatialIndex<E> {
  /**
   * UID for identifying serialized objects.
   */
  private static final long serialVersionUID = 848;

  /**
   * The maximum size of rectangle pages.
   */
  private static final int PAGE_MAXIMUM = 4096;

  /**
   * The minimum size of rectangle pages.
   */
  private static final int PAGE_MINIMUM = PAGE_MAXIMUM / 2;

  /**
   * The maximum size of rectangle buckets.
   */
  private static final int BUCKET_MAXIMUM = 512;

  /**
   * The minimum size of rectangle buckets.
   */
  private static final int BUCKET_MINIMUM = BUCKET_MAXIMUM / 2;

  /**
   * The size of the rectangle tree.
   */
  private int size;

  /**
   * The root node of the rectangle tree.
   */
  private Node<E> root;

  /**
   * Initialize a new rectangle tree bulk-loaded with the specified list of
   * elements.
   *
   * @param elements The elements to add to the tree.
   */
  public RectangleTree(final Collection<E> elements) {
    @SuppressWarnings("unchecked")
    E[] array = (E[]) new Index[elements.size()];

    int i = 0;

    for (E element: elements) {
      array[i++] = element;
    }

    this.root = this.partition(array, 0, array.length);
    this.size = array.length;
  }

  /**
   * Get the size of the rectangle tree.
   *
   * @return The size of the rectangle tree.
   */
  public final int size() {
    return this.size;
  }

  /**
   * Check if the rectangle tree is empty.
   *
   * @return A boolean indicating whether or not the rectangle tree is empty.
   */
  public final boolean isEmpty() {
    return this.size == 0;
  }

  /**
   * Check if the rectangle tree contains the specified element.
   *
   * @param element The element to search for.
   * @return        A boolean indicating whether or not the rectangle tree
   *                contains the specified element.
   */
  public final boolean contains(final E element) {
    throw new UnsupportedOperationException();
  }

  /**
   * Find all elements within the range of the specified bounds.
   *
   * @param bounds  The bounds to search for elements within.
   * @return        A list of elements contained within the range of the
   *                specified bounds.
   */
  public final List<E> range(final Bounds bounds) {
    if (bounds == null) {
      return null;
    }

    return this.range(bounds, (element) -> {
      return true;
    });
  }

  /**
   * Find all elements included in the filter and within the range of the
   * specified bounds.
   *
   * @param bounds  The bounds to search for elements within.
   * @param filter  The filter to apply to the range search.
   * @return        A list of elements contained within the range of the
   *                specified bounds.
   */
  public final List<E> range(final Bounds bounds, final Filter<E> filter) {
    if (bounds == null || filter == null) {
      return null;
    }

    List<E> elements = new ArrayList<>();

    this.root.range(elements, bounds, filter);

    return elements;
  }

  /**
   * Partition the specified array of elements between the given indices using
   * the Sort-Tile-Recursive (STR) algorithm.
   *
   * @see <a href="http://www.dtic.mil/dtic/tr/fulltext/u2/a324493.pdf">
   *      http://www.dtic.mil/dtic/tr/fulltext/u2/a324493.pdf</a>
   *
   * @param elements  The elements to partition.
   * @param start     The starting index of the operation.
   * @param end       The ending index of the operation.
   * @return          A partitioned {@link Node} instance.
   */
  private Node<E> partition(
    final E[] elements,
    final int start,
    final int end
  ) {
    if (elements == null) {
      return null;
    }

    int length = end - start;

    if (length < 0) {
      return null;
    }

    if (length <= BUCKET_MAXIMUM) {
      return new Bucket<E>(Arrays.copyOfRange(elements, start, end));
    }

    Arrays.sort(elements, start, end, (a, b) -> {
      return Double.compare(
        a.minX() - ((a.maxX() - a.minX()) / 2),
        b.minX() - ((b.maxX() - b.minX()) / 2)
      );
    });

    // Compute the number of leaves.
    int l = (int) Math.ceil(length / (double) BUCKET_MAXIMUM);

    // Compute the number of slices.
    int s = (int) Math.ceil(Math.sqrt(l));

    for (int i = 0; i < s; i++) {
      int slice = s * BUCKET_MAXIMUM;
      int sliceStart = start + i * slice;
      int sliceEnd = sliceStart + slice;

      if (sliceStart > end) {
        break;
      }

      if (sliceEnd > end) {
        sliceEnd = end;
      }

      Arrays.sort(elements, sliceStart, sliceEnd, (a, b) -> {
        return Double.compare(
          a.minY() - ((a.maxY() - a.minY()) / 2),
          b.minY() - ((b.maxY() - b.minY()) / 2)
        );
      });
    }

    if (l <= PAGE_MAXIMUM) {
      @SuppressWarnings("unchecked")
      Bucket<E>[] buckets = new Bucket[l];

      for (int i = 0; i < l; i++) {
        int bucketStart = start + i * BUCKET_MAXIMUM;
        int bucketEnd = bucketStart + BUCKET_MAXIMUM;

        if (bucketStart > end) {
          break;
        }

        if (bucketEnd > end) {
          bucketEnd = end;
        }

        buckets[i] = new Bucket<E>(
          Arrays.copyOfRange(elements, bucketStart, bucketEnd)
        );
      }

      return new Page<E>(buckets);
    }
    else {
      int p = (int) Math.ceil(l / (double) PAGE_MAXIMUM);

      @SuppressWarnings("unchecked")
      Node<E>[] children = new Node[p];

      for (int i = 0; i < p; i++) {
        int pageStart = start + i * BUCKET_MAXIMUM * PAGE_MAXIMUM;
        int pageEnd = pageStart + i * BUCKET_MAXIMUM * PAGE_MAXIMUM;

        if (pageStart > end) {
          break;
        }

        if (pageEnd > end) {
          pageEnd = end;
        }

        children[i] = this.partition(elements, pageStart, pageEnd);
      }

      return new Page<E>(children);
    }
  }

  /**
   * The {@link Index} interface describes an object that is indexable by the
   * rectangle tree.
   */
  public interface Index extends Serializable {
    /**
     * Get the smallest x-coordinate of the object.
     *
     * @return The smallest x-coordinate of the object.
     */
    float minX();

    /**
     * Get the smallest y-coordinate of the object.
     *
     * @return The smallest y-coordinate of the object.
     */
    float minY();

    /**
     * Get the largest x-coordinate of the object.
     *
     * @return The largest x-coordinate of the object.
     */
    float maxX();

    /**
     * Ger the largest y-coordinate of the object.
     *
     * @return The largest y-coordinate of the object.
     */
    float maxY();
  }

  /**
   * The {@link Node} class describes a node within a rectangle tree.
   *
   * @param <E> The type of elements stored within the node.
   */
  private abstract static class Node<E extends Index> implements Serializable {
    /**
     * UID for identifying serialized objects.
     */
    private static final long serialVersionUID = 849;

    /**
     * The smallest x-coordinate of the nodes or elements contained within this
     * node.
     */
    private float minX;

    /**
     * The smallest y-coordinate of the nodes or elements contained within this
     * node.
     */
    private float minY;

    /**
     * The largest x-coordinate of the nodes or elements contained within this
     * node.
     */
    private float maxX;

    /**
     * The largest y-coordinate of the nodes or elements contained within this
     * node.
     */
    private float maxY;

    /**
     * Check if the node intersects the specified bounds.
     *
     * @param bounds  The bounds to check intersection of.
     * @return        A boolean indicating whether or not the node intersects
     *                the specified bounds.
     */
    public final boolean intersects(final Bounds bounds) {
      return bounds.intersects(this.minX, this.minY, this.maxX, this.maxY);
    }

    /**
     * Check if the node intersects the specified element.
     *
     * @param element The element to check intersection of.
     * @return        A boolean indicating whether or not the node intersects
     *                the specified element.
     */
    public final boolean intersects(final E element) {
      return (
        this.minX <= element.maxX()
        && this.maxX >= element.minX()
        && this.minY <= element.maxY()
        && this.maxY >= element.maxY()
      );
    }

    /**
     * Get the size of the node.
     *
     * @return The size of the node.
     */
    public abstract int size();

    /**
     * Check if the node is empty.
     *
     * @return A boolean indicating whether or not the node is empty.
     */
    public final boolean isEmpty() {
      return this.size() == 0;
    }

    /**
     * Check if the node contains the specified element.
     *
     * @param element The element to look for.
     * @return        A boolean indicating whether or not the node contains the
     *                specified element.
     */
    public abstract boolean contains(final E element);

    /**
     * Find all elements within the range of the specified bounds.
     *
     * @param elements  The collection to add the found elements to.
     * @param bounds    The bounds to search for elements within.
     * @param filter    The filter to apply to the range search.
     */
    public abstract void range(
      final Collection<E> elements,
      final Bounds bounds,
      final Filter<E> filter
    );

    /**
     * Union the bounds of the current node with the bounds of the specified
     * element.
     *
     * @param element The element whose bounds to union with the bounds of the
     *                current node.
     */
    protected void union(final E element) {
      if (element == null) {
        return;
      }

      boolean empty = this.isEmpty();

      this.minX = !empty ? Math.min(this.minX, element.minX()) : element.minX();
      this.minY = !empty ? Math.min(this.minY, element.minY()) : element.minY();
      this.maxX = !empty ? Math.max(this.maxX, element.maxX()) : element.maxX();
      this.maxY = !empty ? Math.max(this.maxY, element.maxY()) : element.maxY();
    }

    /**
     * Union the bounds of the current node with the bounds of the specified
     * node.
     *
     * @param node  The node whose bounds to union with the bounds of the
     *              current node.
     */
    protected void union(final Node node) {
      if (node == null) {
        return;
      }

      boolean empty = this.isEmpty();

      this.minX = !empty ? Math.min(this.minX, node.minX) : node.minX;
      this.minY = !empty ? Math.min(this.minY, node.minY) : node.minY;
      this.maxX = !empty ? Math.max(this.maxX, node.maxX) : node.maxX;
      this.maxY = !empty ? Math.max(this.maxY, node.maxY) : node.maxY;
    }
  }

  /**
   * A {@link Page} is a {@link Node} that contains references to other
   * {@link Node Nodes}.
   *
   * @param <E> The type of elements stored within the page.
   */
  private static final class Page<E extends Index> extends Node<E> {
    /**
     * UID for identifying serialized objects.
     */
    private static final long serialVersionUID = 850;

    /**
     * The nodes associated with the branch.
     */
    private Node<E>[] nodes;

    /**
     * The size of the page.
     */
    private int size;

    /**
     * Initialize a new page.
     *
     * @param nodes The nodes associated with the page.
     */
    public Page(final Node<E>[] nodes) {
      this.nodes = nodes;

      for (Node<E> node: nodes) {
        if (node == null) {
          break;
        }

        this.union(node);
        this.size++;
      }
    }

    /**
     * Get the size of the page.
     *
     * @return The size of the page.
     */
    public int size() {
      return this.size;
    }

    /**
     * Check if the page contains the specified element.
     *
     * @param element The element to look for.
     * @return        A boolean indicating whether or not the page contains the
     *                specified element.
     */
    public boolean contains(final E element) {
      if (element == null || this.size == 0) {
        return false;
      }

      if (!this.intersects(element)) {
        return false;
      }

      for (Node<E> node: this.nodes) {
        if (node.contains(element)) {
          return true;
        }
      }

      return false;
    }

    /**
     * Find all elements within the range of the specified bounds.
     *
     * @param elements  The collection to add the found elements to.
     * @param bounds    The bounds to search for elements within.
     * @param filter    The filter to apply to the range search.
     */
    public void range(
      final Collection<E> elements,
      final Bounds bounds,
      final Filter<E> filter
    ) {
      if (
        this.size == 0
        || elements == null
        || bounds == null
        || filter == null
        || !this.intersects(bounds)
      ) {
        return;
      }

      for (Node<E> node: this.nodes) {
        if (node == null) {
          break;
        }

        node.range(elements, bounds, filter);
      }
    }
  }

  /**
   * A {@link Bucket} is a {@link Node} that contains a list of elements rather
   * than just a single element.
   *
   * @param <E> The type of elements stored within the bucket.
   */
  private final class Bucket<E extends Index> extends Node<E> {
    /**
     * UID for identifying serialized objects.
     */
    private static final long serialVersionUID = 851;

    /**
     * The elements associated with the bucket.
     */
    private E[] elements;

    /**
     * The size of the bucket.
     */
    private int size;

    /**
     * Initialize a new bucket.
     *
     * @param elements The elements associated with the bucket.
     */
    public Bucket(final E[] elements) {
      this.elements = elements;

      for (E element: this.elements) {
        if (element == null) {
          continue;
        }

        this.union(element);
        this.size++;
      }
    }

    /**
     * Get the size of the bucket.
     *
     * @return The size of the bucket.
     */
    public int size() {
      return this.size;
    }

    /**
     * Check if the bucket contains the specified element.
     *
     * @param element The element to look for.
     * @return        A boolean indicating whether or not the bucket contains
     *                the specified element.
     */
    public boolean contains(final E element) {
      if (element == null || this.size == 0) {
        return false;
      }

      for (E found: this.elements) {
        if (found == null) {
          continue;
        }

        if (element.equals(found)) {
          return true;
        }
      }

      return false;
    }

    /**
     * Find all elements within the range of the specified bounds.
     *
     * @param elements  The collection to add the found elements to.
     * @param bounds    The bounds to search for elements within.
     * @param filter    The filter to apply to the range search.
     */
    public void range(
      final Collection<E> elements,
      final Bounds bounds,
      final Filter<E> filter
    ) {
      if (
        this.size == 0
        || elements == null
        || bounds == null
        || filter == null
        || !this.intersects(bounds)
      ) {
        return;
      }

      for (E element: this.elements) {
        if (!filter.include(element)) {
          continue;
        }

        if (bounds.intersects(
          element.minX(), element.minY(), element.maxX(), element.maxY()
        )) {
          elements.add(element);
        }
      }
    }
  }
}
