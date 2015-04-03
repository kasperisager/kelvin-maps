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
   * Add a coastline to the land.
   *
   * @param way The coastline way to add to the land.
   */
  public void add(final Way way) {
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

    this.coastlines.add(coastline);
  }

  /**
   * Construct geometric line segments that represent the bounds of the land
   * mass.
   *
   * @return Geometric line segments that represent the bounds of the land mass.
   */
  private Geometry.Line[] constructBounds() {
    return new Geometry.Line[] {
      // Top bounds segment.
      new Geometry.Line(
        new Geometry.Point(this.bounds.maxX(), this.bounds.minY()),
        new Geometry.Point(this.bounds.minX(), this.bounds.minY())
      ),

      // Right bounds segment.
      new Geometry.Line(
        new Geometry.Point(this.bounds.maxX(), this.bounds.maxY()),
        new Geometry.Point(this.bounds.maxX(), this.bounds.minY())
      ),

      // Bottom bounds segment.
      new Geometry.Line(
        new Geometry.Point(this.bounds.minX(), this.bounds.maxY()),
        new Geometry.Point(this.bounds.maxX(), this.bounds.maxY())
      ),

      // Left bounds segment.
      new Geometry.Line(
        new Geometry.Point(this.bounds.minX(), this.bounds.minY()),
        new Geometry.Point(this.bounds.minX(), this.bounds.maxY())
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
  private Geometry.Line constructLine(final Node a, final Node b) {
    return new Geometry.Line(
      new Geometry.Point(a.x(), a.y()),
      new Geometry.Point(b.x(), b.y())
    );
  }

  /**
   * Get the JavaFX representation of the land.
   *
   * @return The JavaFX representation of the land.
   */
  public Group render() {
    Group group = new Group();

    Geometry.Line[] bounds = this.constructBounds();

    for (Way coastline: this.coastlines) {
      if (coastline.open()) {
        Node previous1 = null;
        Node previous2 = null;

        boolean inside1 = false;
        boolean inside2 = false;

        List<Node> nodes = coastline.nodes();

        int n = nodes.size();
        int i = 0;

        while (i < n) {
          Node next = nodes.get(i++);

          if (this.bounds.contains(next)) {
            if (inside1 || previous1 == null) {
              continue;
            }

            Geometry.Line line = this.constructLine(previous1, next);

            for (Geometry.Line bound: bounds) {
              Geometry.Point p = Geometry.intersection(bound, line);

              if (p != null) {
                Node node = new Node(p.x(), p.y());

                nodes.add(i - 1, node);
                i++;
                n++;

                nodes.add(0, new Node(bound.start().x(), bound.start().y()));

                break;
              }
            }

            inside1 = true;
          }
          else {
            if (!inside1) {
              nodes.remove(i - 1);
              i--;
              n--;
            }

            inside1 = false;
          }

          previous1 = next;
        }

        int j = n - 1;

        while (j >= 0) {
          Node next = nodes.get(j--);

          if (this.bounds.contains(next)) {
            if (inside2 || previous2 == null) {
              continue;
            }

            Geometry.Line line = this.constructLine(previous2, next);

            for (Geometry.Line bound: bounds) {
              Geometry.Point p = Geometry.intersection(bound, line);

              if (p != null) {
                Node node = new Node(p.x(), p.y());

                nodes.add(j + 3, node);
                j++;

                nodes.add(new Node(bound.end().x(), bound.end().y()));

                break;
              }
            }

            inside2 = true;
          }
          else {
            if (!inside2) {
              nodes.remove(j + 1);
              j--;
            }

            inside2 = false;
          }

          previous2 = next;
        }
      }

      Polyline polyline = coastline.render();
      polyline.getStyleClass().add("land");

      group.getChildren().add(polyline);
    }

    return group;
  }
}
