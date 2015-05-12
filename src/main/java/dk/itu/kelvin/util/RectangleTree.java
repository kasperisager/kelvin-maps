/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// General utilities
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

// I/O utilities
import java.io.Serializable;

// Math
import dk.itu.kelvin.math.Geometry;
import dk.itu.kelvin.math.Epsilon;

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
    this.root = this.partition(new ArrayList<>(elements));
    this.size = elements.size();
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

    return this.root.range(bounds, filter);
  }

  /**
   * Find the element closest to the specified point.
   *
   * @param point The point to look for elements near.
   * @return      The element closest to the specified point.
   */
  public final E nearest(final Point point) {
    if (point == null) {
      return null;
    }

    return this.nearest(point, (element) -> {
      return true;
    });
  }

  /**
   * Find the element included in the filter closest to the specified point.
   *
   * @param point   The point to look for elements near.
   * @param filter  The filter to apply to the search.
   * @return        The element closest to the specified point.
   */
  public final E nearest(final Point point, final Filter<E> filter) {
    if (point == null || filter == null) {
      return null;
    }

    return this.root.nearest(point, filter);
  }

  /**
   * Partition the specified list of elements using the Sort-Tile-Recursive
   * (STR) algorithm.
   *
   * @see <a href="http://www.dtic.mil/dtic/tr/fulltext/u2/a324493.pdf">
   *      http://www.dtic.mil/dtic/tr/fulltext/u2/a324493.pdf</a>
   *
   * @param elements  The elements to partition.
   * @return          A partitioned {@link Node} instance.
   */
  private Node<E> partition(final List<E> elements) {
    if (elements == null || elements.isEmpty()) {
      return null;
    }

    if (elements.size() <= BUCKET_MAXIMUM) {
      return new Bucket<E>(elements);
    }

    Collections.sort(elements, (a, b) -> {
      return Double.compare(
        a.minX() - ((a.maxX() - a.minX()) / 2),
        b.minX() - ((b.maxX() - b.minX()) / 2)
      );
    });

    // Compute the number of leaves.
    int l = (int) Math.ceil(elements.size() / (double) BUCKET_MAXIMUM);

    // Compute the number of slices.
    int s = (int) Math.ceil(Math.sqrt(l));

    for (int i = 0; i < s; i++) {
      int slice = s * BUCKET_MAXIMUM;
      int start = i * slice;
      int end = start + slice;

      if (start > elements.size()) {
        break;
      }

      if (end > elements.size()) {
        end = elements.size();
      }

      Collections.sort(elements.subList(start, end), (a, b) -> {
        return Double.compare(
          a.minY() - ((a.maxY() - a.minY()) / 2),
          b.minY() - ((b.maxY() - b.minY()) / 2)
        );
      });
    }

    // Can the elements fit on a single page?
    boolean singlePage = l <= PAGE_MAXIMUM;

    // Compute the number of elements per page.
    int n = (singlePage) ? l : (int) Math.ceil(l / (double) PAGE_MAXIMUM);

    List<Node<E>> nodes = new ArrayList<>();

    for (int i = 0; i < n; i++) {
      int start;
      int end;

      if (singlePage) {
        start = i * BUCKET_MAXIMUM;
        end = start + BUCKET_MAXIMUM;
      }
      else {
        start = i * BUCKET_MAXIMUM * PAGE_MAXIMUM;
        end = start + i * BUCKET_MAXIMUM * PAGE_MAXIMUM;
      }

      if (start > elements.size()) {
        break;
      }

      if (end > elements.size()) {
        end = elements.size();
      }

      // If the elements can fit on a single page, create a bucket.
      if (singlePage) {
        nodes.add(new Bucket<E>(elements.subList(start, end)));
      }
      // Otherwise, continue recursively partioning the elements.
      else {
        nodes.add(this.partition(new ArrayList<>(elements.subList(
          start, end
        ))));
      }
    }

    return new Page<E>(nodes);
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

    return bounds.intersects(element.bounds());
  }

  /**
   * @see <a href="http://www.cs.umd.edu/~nick/papers/nnpaper.pdf">
   *      http://www.cs.umd.edu/~nick/papers/nnpaper.pdf</a>
   *
   * @param point   The point to calculate the minimum distance to.
   * @param bounds  The bounds to calculate the minimum distance from.
   * @return        The minimum distance between the specified bounds and the
   *                given point.
   */
  private static double minimumDistance(
    final Point point,
    final Bounds bounds
  ) {
    if (point == null || bounds == null) {
      return Double.POSITIVE_INFINITY;
    }

    double r1 = point.x();

    if (Epsilon.less(point.x(), bounds.min().x())) {
      r1 = bounds.min().x();
    }
    else if (Epsilon.greater(point.x(), bounds.max().x())) {
      r1 = bounds.max().x();
    }

    double r2 = point.y();

    if (Epsilon.less(point.y(), bounds.min().y())) {
      r2 = bounds.min().y();
    }
    else if (Epsilon.greater(point.y(), bounds.max().y())) {
      r2 = bounds.max().y();
    }

    return Geometry.distance(point, new Point(r1, r2));
  }

  /**
   * @see <a href="http://www.cs.umd.edu/~nick/papers/nnpaper.pdf">
   *      http://www.cs.umd.edu/~nick/papers/nnpaper.pdf</a>
   *
   * @param point   The point to calculate the minimax distance to.
   * @param bounds  The bounds to calculate the minimax distance from.
   * @return        The minimax distance between the specified bounds and the
   *                given point.
   */
  private static double minimaxDistance(
    final Point point,
    final Bounds bounds
  ) {
    if (point == null || bounds == null) {
      return Double.POSITIVE_INFINITY;
    }

    double rm1;
    double rm2;

    if (Epsilon.lessOrEqual(
      point.x(), (bounds.min().x() + bounds.max().x()) / 2.0
    )) {
      rm1 = bounds.min().x();
    }
    else {
      rm1 = bounds.max().x();
    }

    if (Epsilon.lessOrEqual(
      point.y(), (bounds.min().y() + bounds.max().y()) / 2.0
    )) {
      rm2 = bounds.min().y();
    }
    else {
      rm2 = bounds.max().y();
    }

    double rM1;
    double rM2;

    if (Epsilon.greaterOrEqual(
      point.x(), (bounds.min().x() + bounds.max().x()) / 2.0
    )) {
      rM1 = bounds.min().x();
    }
    else {
      rM1 = bounds.max().x();
    }

    if (Epsilon.greaterOrEqual(
      point.y(), (bounds.min().y() + bounds.max().y()) / 2.0
    )) {
      rM2 = bounds.min().y();
    }
    else {
      rM2 = bounds.max().x();
    }

    double s = Geometry.distance(point, new Point(rM1, rM2));

    double distance = Math.min(
      Math.pow(Math.abs(point.x() - rm1), 2) + s,
      Math.pow(Math.abs(point.y() - rm2), 2) + s
    );

    return Math.sqrt(distance);
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

    /**
     * Get the actual distance to the specified point from the index.
     *
     * @param point The point to find the distance to.
     * @return      The distance to the specified point from the index.
     */
    double distance(final Point point);

    /**
     * Get the bounds of the object.
     *
     * @return The bounds of the object.
     */
    default Bounds bounds() {
      return new Bounds(this.minX(), this.minY(), this.maxX(), this.maxY());
    }
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
     * Get the bounds of the node.
     *
     * @return The bounds of the node.
     */
    public final Bounds bounds() {
      return new Bounds(this.minX, this.minY, this.maxX, this.maxY);
    }

    /**
     * Check if the node intersects the specified bounds.
     *
     * @param bounds  The bounds to check intersection of.
     * @return        A boolean indicating whether or not the node intersects
     *                the specified bounds.
     */
    public final boolean intersects(final Bounds bounds) {
      return bounds.intersects(this.bounds());
    }

    /**
     * Check if the node intersects the specified element.
     *
     * @param element The element to check intersection of.
     * @return        A boolean indicating whether or not the node intersects
     *                the specified element.
     */
    public final boolean intersects(final E element) {
      return RectangleTree.intersects(element, this.bounds());
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
     * @param bounds  The bounds to search for elements within.
     * @param filter  The filter to apply to the range search.
     * @return        All elements within the range of the specified bounds.
     */
    public abstract List<E> range(final Bounds bounds, final Filter<E> filter);

    /**
     * Find the element in the node closest to the specified point.
     *
     * @param point   The point to look for elements near.
     * @param filter  The filter to apply to the search.
     * @return        The element closest to the specified point.
     */
    public abstract E nearest(final Point point, final Filter<E> filter);

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
    private List<Node<E>> nodes;

    /**
     * Initialize a new page.
     *
     * @param nodes The nodes associated with the page.
     */
    public Page(final List<Node<E>> nodes) {
      this.nodes = nodes;

      for (Node<E> node: nodes) {
        if (node == null) {
          continue;
        }

        this.union(node);
      }
    }

    /**
     * Get the size of the page.
     *
     * @return The size of the page.
     */
    public int size() {
      return this.nodes.size();
    }

    /**
     * Check if the page contains the specified element.
     *
     * @param element The element to look for.
     * @return        A boolean indicating whether or not the page contains the
     *                specified element.
     */
    public boolean contains(final E element) {
      if (element == null || this.size() == 0) {
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
     * @param bounds  The bounds to search for elements within.
     * @param filter  The filter to apply to the range search.
     * @return        All elements with range of the specified bounds.
     */
    public List<E> range(final Bounds bounds, final Filter<E> filter) {
      List<E> elements = new ArrayList<>();

      if (
        this.size() == 0
        || bounds == null
        || filter == null
        || !this.intersects(bounds)
      ) {
        return elements;
      }

      for (Node<E> node: this.nodes) {
        if (node == null) {
          continue;
        }

        elements.addAll(node.range(bounds, filter));
      }

      return elements;
    }

    /**
     * Find the element in the page closest to the specified point.
     *
     * @param point   The point to look for elements near.
     * @param filter  The filter to apply to the search.
     * @return        The element closest to the specified point.
     */
    public E nearest(final Point point, final Filter<E> filter) {
      if (point == null || filter == null) {
        return null;
      }

      // "During the descending phase, at each newly visited nonleaf node, the
      // algorithm computes the ordering metric bounds (e.g. MINDIST, Definition
      // 2) for all its MBRs and sorts them (associated with their corresponding
      // node) into an Active Branch List (ABL).
      List<Node<E>> abl = new ArrayList<>(this.nodes);

      Collections.sort(abl, (a, b) -> {
        if (a == b) {
          return 0;
        }

        if (a == null) {
          return -1;
        }

        if (b == null) {
          return 1;
        }

        return Double.compare(
          RectangleTree.minimumDistance(point, a.bounds()),
          RectangleTree.minimumDistance(point, b.bounds())
        );
      });

      // Keep track of the smallest minimax distance.
      double minimumMinimaxDistance = Double.POSITIVE_INFINITY;

      for (Node<E> node: abl) {
        double minimaxDistance = RectangleTree.minimaxDistance(
          point, node.bounds()
        );

        if (minimumMinimaxDistance > minimaxDistance) {
          minimumMinimaxDistance = minimaxDistance;
        }
      }

      // Search pruning, strategy 1: "an MBR M with MINDIST(P,M) greater than
      // the MINMAXDIST(P,M') of another MBR M' is discarded because it cannot
      // contain the NN (theorems 1 and 2). We use this in downward pruning."
      for (int i = 0; i < abl.size(); i++) {
        double minimumDistance = RectangleTree.minimumDistance(
          point, abl.get(i).bounds()
        );

        if (minimumDistance > minimumMinimaxDistance) {
          abl.remove(i--);
        }
      }

      E nearest = null;

      while (abl.size() > 0) {
        Node<E> next = abl.remove(0);

        if (next == null) {
          continue;
        }

        E estimate = next.nearest(point, filter);

        if (estimate == null) {
          continue;
        }

        if (nearest == null) {
          nearest = estimate;
        }
        else {
          double distNearest = nearest.distance(point);
          double distEstimate = estimate.distance(point);

          if (distNearest > distEstimate) {
            nearest = estimate;
          }
        }
      }

      return nearest;
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
    private List<E> elements;

    /**
     * Initialize a new bucket.
     *
     * @param elements The elements associated with the bucket.
     */
    public Bucket(final List<E> elements) {
      this.elements = new ArrayList<>(elements);

      for (E element: this.elements) {
        if (element == null) {
          continue;
        }

        this.union(element);
      }
    }

    /**
     * Get the size of the bucket.
     *
     * @return The size of the bucket.
     */
    public int size() {
      return this.elements.size();
    }

    /**
     * Check if the bucket contains the specified element.
     *
     * @param element The element to look for.
     * @return        A boolean indicating whether or not the bucket contains
     *                the specified element.
     */
    public boolean contains(final E element) {
      if (element == null || this.size() == 0) {
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
     * @param bounds  The bounds to search for elements within.
     * @param filter  The filter to apply to the range search.
     * @return        All elements within range of the specified bounds.
     */
    public List<E> range(final Bounds bounds, final Filter<E> filter) {
      List<E> elements = new ArrayList<>();

      if (
        this.size() == 0
        || bounds == null
        || filter == null
        || !this.intersects(bounds)
      ) {
        return elements;
      }

      for (E element: this.elements) {
        if (!filter.include(element)) {
          continue;
        }

        if (RectangleTree.intersects(element, bounds)) {
          elements.add(element);
        }
      }

      return elements;
    }

    /**
     * Find the element in the bucket closest to the specified point.
     *
     * @param point   The point to look for elements near.
     * @param filter  The filter to apply to the search.
     * @return        The element closest to the specified point.
     */
    public E nearest(final Point point, final Filter<E> filter) {
      if (this.size() == 0 || point == null || filter == null) {
        return null;
      }

      E nearest = null;

      for (E element: this.elements) {
        if (element == null || !filter.include(element)) {
          continue;
        }

        if (nearest == null) {
          nearest = element;
        }
        else {
          double distNearest = nearest.distance(point);
          double distElement = element.distance(point);

          if (distNearest > distElement) {
            nearest = element;
          }
        }
      }

      return nearest;
    }
  }
}
