/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.model;

// General utilities
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// JavaFX scene utilities
import javafx.scene.Group;

// Math
import dk.itu.kelvin.math.Geometry;
import dk.itu.kelvin.math.Geometry.Line;
import dk.itu.kelvin.math.Geometry.Point;

/**
 * Calculation and merging of coastlines to land polyLines.
 * <p>
 * Initially all coastlines are put in a {@link List}.
 * Then {@link Geometry.Line} line segments are created from the bounds of
 * {@link BoundingBox}. Coastlines are iterated through, each coastline is
 * check if it's already closed(starting point = ending point). If it's open we
 * iterate though the nodes until we until we find a note that's outside the
 * BoundingBox then make a line segment from the two points. Then we check if
 * the line segments intersect with any of the bounds that we made to line
 * segments, if the intersect we add the intersection point to the nodes list.
 * We then render the points to polyLines representing the Land segments.
 *
 * Time complexity for finding intersection points on bounds is worst case: ~8N
 * (goes though the coastlines 2 times, and each coastline checks for each of
 * the 4 bounds in BondingBox for intersections).
 * Average is significantly lower since first iteration of coastlines only...
 *
 */
public final class Land extends Element<Group> {
  /**
   * UID for identifying serialized objects.
   */
  private static final long serialVersionUID = 81;

  /**
   * Bounds of the land element.
   */
  private BoundingBox bounds;

  /**
   * Coastlines contained within the land element.
   */
  private List<Way> coastlines = new ArrayList<>();

  /**
   * Initialize a land instance with initial bounds.
   *
   * @param bounds The bounds of the land.
   */
  public Land(final BoundingBox bounds) {
    this.bounds = bounds;
  }

  /**
   * Get a list of merged coastlines.
   *
   * @return A list of merged coastlines.
   */
  public List<Way> coastlines() {
    return this.coastlines;
  }

  /**
   * Add a coastline to the land.
   *
   * @param way The coastline way to add to the land.
   */
  public void add(final Way way) {
    if (way == null) {
      return;
    }

    this.merge(way);
  }

  /**
   * Construct geometric line segments that represent the bounds of the land
   * mass.
   *
   * @return Geometric line segments that represent the bounds of the land mass.
   */
  private Line[] constructBounds() {
    if (this.bounds == null) {
      return null;
    }

    return new Line[] {
      // Top bounds segment.
      new Line(
        new Point(this.bounds.maxX(), this.bounds.minY()),
        new Point(this.bounds.minX(), this.bounds.minY())
      ),

      // Right bounds segment.
      new Line(
        new Point(this.bounds.maxX(), this.bounds.maxY()),
        new Point(this.bounds.maxX(), this.bounds.minY())
      ),

      // Bottom bounds segment.
      new Line(
        new Point(this.bounds.minX(), this.bounds.maxY()),
        new Point(this.bounds.maxX(), this.bounds.maxY())
      ),

      // Left bounds segment.
      new Line(
        new Point(this.bounds.minX(), this.bounds.minY()),
        new Point(this.bounds.minX(), this.bounds.maxY())
      )
    };
  }

  /**
   * Construct a geometric line segment between the specified nodes.
   *
   * @param a The first node.
   * @param b The second node.
   * @return  A geometric line segment between the specified nodes.
   */
  private Line constructLine(final Node a, final Node b) {
    if (a == null || b == null) {
      return null;
    }

    return new Line(
      new Point(a.x(), a.y()),
      new Point(b.x(), b.y())
    );
  }

  /**
   * Merge the specified way with the existing coastlines of the land mass.
   *
   * @param way The way to merge.
   */
  private void merge(final Way way) {
    if (way == null) {
      return;
    }

    Way coastline = new Way();
    coastline.append(way);

    Iterator<Way> i = this.coastlines.iterator();

    while (i.hasNext()) {
      Way next = i.next();

      if (coastline.endsIn(next)) {
        coastline.append(next);
        i.remove();
      }
      else if (coastline.startsIn(next)) {
        next.append(coastline);
        coastline = next;
        i.remove();
      }
    }

    this.close(coastline);

    coastline.tag("land", "yes");
    coastline.tag("layer", "-9999");

    this.coastlines.add(coastline);
  }

  /**
   * Close the specified coastline, merging it with the bounding box of the
   * land mass where possible.
   *
   * @param coastline The coastline to close.
   */
  private void close(final Way coastline) {
    if (coastline == null || coastline.isClosed()) {
      return;
    }

    List<Node> nodes = coastline.nodes();

    // Keep track of the previously visited node.
    Node prev = null;

    // Construct an array of bounding lines.
    Line[] bounds = this.constructBounds();

    int i = 0;
    int n = nodes.size();

    while (i < n) {
      Node next = nodes.get(i++);

      // Check if either the current node or the previous node are inside
      // the bounds.
      boolean prevInside = this.bounds.contains(prev);
      boolean nextInside = this.bounds.contains(next);

      // Remove the current node from the coastline if it's outside the bounds.
      if (!nextInside && next != null) {
        nodes.remove(i - 1);
        i--;
        n--;
      }

      if ((prevInside ^ nextInside) && prev != null && next != null) {
        Line line = this.constructLine(prev, next);

        for (Line bound: bounds) {
          // Check if the rectangular bounds of the lines intersect before
          // doing any further computations.
          if (!Geometry.intersects(bound, line)) {
            continue;
          }

          // Get the intersection point between the bounding line and the line
          // that runs between the previous and current node.
          Point a = Geometry.intersection(bound, line);

          // Check if both lines actually contain the intersection point. If
          // this is not the case the lines do not intersect within their
          // individual segments.
          if (!bound.contains(a) || !line.contains(a)) {
            continue;
          }

          // Figure out which point on the bounding line to potentially use for
          // closing the coastline.
          Point b = nextInside ? bound.start() : bound.end();

          if (a == null || b == null) {
            continue;
          }

          nodes.add(nextInside ? i - 1 : i, new Node(
            (nextInside ? a : b).x(),
            (nextInside ? a : b).y()
          ));

          nodes.add(nextInside ? i - 1 : i, new Node(
            (nextInside ? b : a).x(),
            (nextInside ? b : a).y()
          ));

          i += 2;
          n += 2;
        }
      }

      prev = next;
    }
  }

  /**
   * Get the JavaFX representation of the land.
   *
   * @return The JavaFX representation of the land.
   */
  public Group render() {
    Group group = new Group();

    for (Way coastline: this.coastlines) {
      group.getChildren().add(coastline.render());
    }

    return group;
  }
}
