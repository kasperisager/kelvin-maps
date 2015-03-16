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

/**
 * Land class.
 *
 * @version 1.0.0
 */
public final class Land extends Element<Group> {
  /**
   * UID for identifying serialized objects.
   */
  private static final long serialVersionUID = 81;

  /**
   * The JavaFX representation of the land.
   *
   * This field is transient as it is simply used for caching the rendered
   * JavaFX scene graph node. We therefore don't want to store it when
   * serializing the element.
   */
  private transient Group fx;

  /**
   * Bounds of the land element.
   */
  private BoundingBox bounds;

  /**
   * Coastlines contained within the land element.
   */
  private List<Way> coastlines = new ArrayList<>();

  /**
   * Intersections between the coastlines and the bounds of the land.
   */
  // private Map<Node, Intersection> intersections = new HashMap<>();

  public Land() {
    super(Long.MAX_VALUE);
  }

  /**
   * Initialize a land instance with initial bounds.
   *
   * @param bounds The bounds of the land.
   */
  public Land(final BoundingBox bounds) {
    this();
    this.bounds = bounds;
    this.layer(-999);
  }

  /**
   * Get the bounds of the element.
   *
   * @return The bounds of the element.
   */
  public BoundingBox bounds() {
    return this.bounds;
  }

  /**
   * Add a coastline to the land.
   *
   * @param way The coastline way to add to the land.
   */
  public void coastline(final Way way) {
    if (way == null) {
      return;
    }

    Way coastline = way;

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
   * Get the coastlines contained within the land.
   *
   * @return The coastlines contained within the land.
   */
  public List<Way> coastlines() {
    return this.coastlines;
  }

  /**
   * Get the JavaFX representation of the land.
   *
   * @return The JavaFX representation of the land.
   */
  public Group render() {
    if (this.fx != null) {
      return this.fx;
    }

    Group group = new Group();

    this.fx = group;

    return this.fx;
  }

  /**
   * Enumerator describing the intersections between coastlines and the land
   * bounds.
   */
  private static enum Intersection {
    /** Intersection of bounds and start node. */
    START,

    /** Intersection of bounds and end node. */
    END;
  }
}
