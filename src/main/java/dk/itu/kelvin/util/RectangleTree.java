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
import static dk.itu.kelvin.math.Geometry.Rectangle;

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
public class RectangleTree<E> implements SpatialIndex<E> {
  /**
   * The maximum size of rectangle pages.
   */
  private static final int PAGE_MAXIMUM = 10;

  /**
   * The minimum size of rectangle pages.
   */
  private static final int PAGE_MINIMUM = PAGE_MAXIMUM / 2;

  /**
   * The maximum size of rectangle buckets.
   */
  private static final int BUCKET_MAXIMUM = 1000;

  /**
   * The minimum size of rectangle buckets.
   */
  private static final int BUCKET_MINIMUM = BUCKET_MAXIMUM / 2;

  /**
   * The size of the rectangle tree.
   */
  private int size;

  /**
   * The element descriptor.
   */
  private Descriptor<E, Rectangle> descriptor;

  /**
   * The root node of the rectangle tree.
   */
  private Node root;

  /**
   * Initialize a new rectangle tree bulk-loaded with the specified list of
   * elements.
   *
   * @param elements    The elements to add to the tree.
   * @param descriptor  The element descriptor to use.
   */
  @SuppressWarnings("unchecked")
  public RectangleTree(
    final Collection<E> elements,
    final Descriptor<E, Rectangle> descriptor
  ) {
    this.descriptor = descriptor;

    this.root = this.partition(
      (E[]) elements.toArray(), // Elements
      0,                        // Starting index
      elements.size()           // Ending index
    );
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
  @SuppressWarnings("unchecked")
  private Node partition(final E[] elements, final int start, final int end) {
    if (elements == null) {
      return null;
    }

    int length = end - start;

    if (length < 0) {
      return null;
    }

    if (length == 0) {
      this.size++;
      return new Leaf(elements[start]);
    }

    if (length <= BUCKET_MAXIMUM) {
      this.size += length;
      return new Bucket(Arrays.copyOfRange(elements, start, end));
    }

    Arrays.sort(elements, start, end, (a, b) -> {
      Bounds ab = this.descriptor.describe(a).bounds();
      Bounds bb = this.descriptor.describe(b).bounds();

      return Double.compare(
        ab.min().x() - ((ab.max().x() - ab.min().x()) / 2),
        bb.min().x() - ((bb.max().x() - bb.min().x()) / 2)
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
        Bounds ab = this.descriptor.describe(a).bounds();
        Bounds bb = this.descriptor.describe(b).bounds();

        return Double.compare(
          ab.min().y() - ((ab.max().y() - ab.min().y()) / 2),
          bb.min().y() - ((bb.max().y() - bb.min().y()) / 2)
        );
      });
    }

    Bucket[] buckets = new RectangleTree.Bucket[l];

    for (int i = 0; i < l; i++) {
      int bucketStart = start + i * BUCKET_MAXIMUM;
      int bucketEnd = bucketStart + BUCKET_MAXIMUM;

      if (bucketStart > end) {
        break;
      }

      if (bucketEnd > end) {
        bucketEnd = end;
      }

      buckets[i] = new Bucket(
        Arrays.copyOfRange(elements, bucketStart, bucketEnd)
      );
    }

    return new Page(buckets);
  }

  /**
   * The {@link Node} class describes a node within a rectangle tree.
   */
  private abstract class Node {
    /**
     * The smallest x-coordinate of the nodes or elements contained within this
     * node.
     */
    private double minX;

    /**
     * The smallest y-coordinate of the nodes or elements contained within this
     * node.
     */
    private double minY;

    /**
     * The largest x-coordinate of the nodes or elements contained within this
     * node.
     */
    private double maxX;

    /**
     * The largest y-coordinate of the nodes or elements contained within this
     * node.
     */
    private double maxY;

    /**
     * Check if the node intersects the specified bounds.
     *
     * @param bounds  The bounds to check intersection of.
     * @return        A boolean indicating whether or not the node intersects
     *                the specified bounds.
     */
    public boolean intersects(final Bounds bounds) {
      return Geometry.intersects(
        bounds,
        new Bounds(
          new Point(this.minX, this.minY),
          new Point(this.maxX, this.maxY)
        )
      );
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
      Bounds bounds = RectangleTree.this.descriptor.describe(element).bounds();

      this.minX = Math.min(this.minX, bounds.min().x());
      this.minY = Math.min(this.minY, bounds.min().y());
      this.maxX = Math.max(this.maxX, bounds.max().x());
      this.maxY = Math.max(this.maxY, bounds.max().y());
    }

    /**
     * Union the bounds of the current node with the bounds of the specified
     * node.
     *
     * @param node  The node whose bounds to union with the bounds of the
     *              current node.
     */
    protected void union(final Node node) {
      this.minX = Math.min(this.minX, node.minX);
      this.minY = Math.min(this.minY, node.minY);
      this.maxX = Math.max(this.maxX, node.maxX);
      this.maxY = Math.max(this.maxY, node.maxY);
    }
  }

  /**
   * A {@link Page} is a {@link Node} that contains references to other
   * {@link Node Nodes}.
   */
  private final class Page extends Node {
    /**
     * The nodes associated with the branch.
     */
    private Node[] nodes;

    /**
     * Initialize a new page.
     *
     * @param nodes The nodes associated with the page.
     */
    public Page(final Node[] nodes) {
      this.nodes = nodes;

      for (Node node: nodes) {
        this.union(node);
      }
    }

    /**
     * Check if the page contains the specified element.
     *
     * @param element The element to look for.
     * @return        A boolean indicating whether or not the page contains the
     *                specified element.
     */
    public boolean contains(final E element) {
      if (element == null) {
        return false;
      }

      if (!this.intersects(
        RectangleTree.this.descriptor.describe(element).bounds()
      )) {
        return false;
      }

      for (Node node: this.nodes) {
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
      if (elements == null || bounds == null || filter == null) {
        return;
      }

      if (!this.intersects(bounds)) {
        return;
      }

      for (Node node: this.nodes) {
        node.range(elements, bounds, filter);
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
    private E element;

    /**
     * Initialize a new leaf.
     *
     * @param element The element associated with the leaf.
     */
    public Leaf(final E element) {
      this.element = element;
      this.union(element);
    }

    /**
     * Check if the leaf contains the specified element.
     *
     * @param element The element to look for.
     * @return        A boolean indicating whether or not the leaf contains the
     *                specified element.
     */
    public boolean contains(final E element) {
      if (this.element == null || element == null) {
        return false;
      }

      return this.element.equals(element);
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
        this.element == null
        || elements == null
        || bounds == null
        || filter == null
      ) {
        return;
      }

      if (filter.include(this.element) && this.intersects(bounds)) {
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
    private E[] elements;

    /**
     * Initialize a new bucket.
     *
     * @param elements The elements associated with the bucket.
     */
    public Bucket(final E[] elements) {
      this.elements = elements;

      for (E element: this.elements) {
        this.union(element);
      }
    }

    /**
     * Check if the bucket contains the specified element.
     *
     * @param element The element to look for.
     * @return        A boolean indicating whether or not the bucket contains
     *                the specified element.
     */
    public boolean contains(final E element) {
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
        this.elements == null
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

        Bounds found = RectangleTree.this.descriptor.describe(element).bounds();

        if (Geometry.intersects(found, bounds)) {
          elements.add(element);
        }
      }
    }
  }
}
