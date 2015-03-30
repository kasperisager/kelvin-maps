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
          // Get the intersection point between the bounding line and the line
          // that runs between the previous and current node.
          Point a = Geometry.intersection(bound, line);

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
      Polyline polyline = coastline.render();
      polyline.getStyleClass().add("land");

      group.getChildren().add(polyline);
    }

    return group;
  }
}
