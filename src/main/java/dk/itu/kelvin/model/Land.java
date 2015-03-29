/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.model;

// General utilities
import java.util.Iterator;

// JavaFX scene utilities
import javafx.scene.Group;

// JavaFX shapes
import javafx.scene.shape.Polyline;

// Utilities
import dk.itu.kelvin.util.ArrayList;
import dk.itu.kelvin.util.List;

// Math
import dk.itu.kelvin.math.Geometry;
import static dk.itu.kelvin.math.Geometry.Line;
import static dk.itu.kelvin.math.Geometry.Point;

/**
 * Land class.
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
    return new Line(
      new Point(a.x(), a.y()),
      new Point(b.x(), b.y())
    );
  }

  /**
   * Get the intersection between the line segment specified by the two nodes
   * and the bounding box of the land mass.
   *
   * @param a The first node.
   * @param b The second node.
   * @return  The intersection between the line segment specified by the two
   *          nodes and the bounding box of the land mass or {@code null} is
   *          they don't intersect.
   */
  private Intersection findIntersection(final Node a, final Node b) {
    if (a == null || b == null) {
      return null;
    }

    Line line = this.constructLine(a, b);

    for (Line bound: this.constructBounds()) {
      Point point = Geometry.intersection(bound, line);

      if (point != null) {
        return new Intersection(point, bound);
      }
    }

    return null;
  }

  /**
   * Merge the specified way with the existing coastlines of the land mass.
   *
   * @param way The way to merge.
   */
  private void merge(final Way way) {
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

    this.coastlines.add(coastline);
  }

  /**
   * Close the specified coastline, merging it with the bounding box of the
   * land mass where possible.
   *
   * <p>
   * <b>NB:</b> This method may produce unexpected result if called prior to all
   * coastlines having been added to the land mass.
   *
   * @param coastline The coastline to close.
   */
  private void close(final Way coastline) {
    if (coastline.isClosed()) {
      return;
    }

    Node prev = null;

    List<Node> nodes = coastline.nodes();

    int i = 0;
    int n = nodes.size();

    while (i < n) {
      Node next = nodes.get(i++);

      boolean prevInside = this.bounds.contains(prev);
      boolean nextInside = this.bounds.contains(next);

      if (prevInside ^ nextInside) {
        Intersection intersection = this.findIntersection(prev, next);

        if (intersection != null) {
          Line bound = intersection.bound;

          if (nextInside) {
            nodes.add(0, new Node(bound.start().x(), bound.start().y()));
          }

          if (prevInside) {
            nodes.add(new Node(bound.end().x(), bound.end().y()));
          }

          i++;
          n++;
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
      this.close(coastline);

      Polyline polyline = coastline.render();
      polyline.getStyleClass().add("land");

      group.getChildren().add(polyline);
    }

    return group;
  }

  /**
   * The {@link Intersection} class describes an intersection consisting of an
   * intersection point and the bounding line on which the point lies.
   */
  private static class Intersection {
    /**
     * The intersection point.
     */
    private Point point;

    /**
     * The bounding line that the intersection point lies on.
     */
    private Line bound;

    /**
     * Initialize a new intersection.
     *
     * @param point The intersection point.
     * @param bound The bounding line that the intersection point lies on.
     */
    public Intersection(final Point point, final Line bound) {
      this.point = point;
      this.bound = bound;
    }
  }
}
