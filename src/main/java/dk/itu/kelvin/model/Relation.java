/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.model;

// General utilities
import java.util.Iterator;

// JavaFX application utilities
import javafx.application.Platform;

// JavaFX scene utilities
import javafx.scene.Group;

// JavaFX shapes
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;

// JavaFX paint
import javafx.scene.paint.Color;

// Utilities
import dk.itu.kelvin.util.ArrayList;
import dk.itu.kelvin.util.HashTable;
import dk.itu.kelvin.util.List;
import dk.itu.kelvin.util.Map;

// Threading
import dk.itu.kelvin.thread.TaskQueue;

/**
 * A relation is an ordered list of one or more members (nodes, ways, or even
 * other relations) that may optionally be assigned a role.
 *
 * @see <a href="http://wiki.openstreetmap.org/wiki/Relation">
 * http://wiki.openstreetmap.org/wiki/Relation</a>
 */
public final class Relation extends Element<Group> {
  /**
   * UID for identifying serialized objects.
   */
  private static final long serialVersionUID = 48;

  /**
   * The JavaFX representation of the relation.
   *
   * This field is transient as it is simply used for caching the rendered
   * JavaFX scene graph node. We therefore don't want to store it when
   * serializing the element.
   */
  private transient Group fx;

  /**
   * The members of the relation mapped to their roles.
   *
   * The map is initialized on-demand when first accessed to avoid allocating
   * memory to empty maps.
   */
  private Map<Element, Relation.Role> elements;

  /**
   * The type of the relation.
   *
   * The type is initialized on-demand when first accessed to avoid allocating
   * memory to relations without a type.
   */
  private Type type;

  /**
   * Initialize a relation with an ID.
   *
   * @param id The ID of the relation.
   */
  public Relation(final long id) {
    super(id);
  }

  /**
   * Get the type of the relation.
   *
   * @return The type of the relation.
   */
  public Type type() {
    if (this.type == null) {
      return Type.NONE;
    }

    return this.type;
  }

  /**
   * Set the type of the relation.
   *
   * @param type The type of the relation.
   */
  public void type(final Type type) {
    if (type == null) {
      return;
    }

    this.type = type;
  }

  /**
   * Get the role of a member element of the relation.
   *
   * @param element The member whose role to get.
   * @return        The role of the member if found, otherwise null.
   */
  public Role role(final Element element) {
    if (element == null || this.elements == null) {
      return null;
    }

    return this.elements.get(element);
  }

  /**
   * Get a list of all members of the relation.
   *
   * @return A list of members of the relation.
   */
  public List<Element> members() {
    if (this.elements == null) {
      return new ArrayList<Element>();
    }

    return new ArrayList<Element>(this.elements.keySet());
  }

  /**
   * Add an element with a role to the relation.
   *
   * @param element The element to add to the relation.
   * @param role    The role of the element within the relation.
   */
  public void member(final Element element, final Role role) {
    if (element == null || role == null) {
      return;
    }

    if (this.elements == null) {
      this.elements = new HashTable<>();
    }

    this.elements.put(element, role);
  }

  /**
   * Shorthand for adding an element to the relation without a role.
   *
   * @param element The element to add to the relation.
   */
  public void member(final Element element) {
    if (element == null) {
      return;
    }

    this.member(element, Role.NONE);
  }

  /**
   * Get the JavaFX representation of the relation.
   *
   * @return The JavaFX representation of the relation.
   */
  public Group render() {
    if (this.fx != null) {
      return this.fx;
    }

    Group group = new Group();

    for (Map.Entry<String, String> tag: this.tags().entrySet()) {
      String key = tag.getKey();
      String value = tag.getValue();

      switch (key) {
        case "type":
          this.type(Type.fromString(value));

          switch (value) {
            case "multipolygon":
              TaskQueue.run(() -> {
                Shape shape = this.multipolygon();

                Platform.runLater(() -> {
                  group.getChildren().add(shape);
                });
              });
              break;
            default:
              // Do nothing.
          }
          break;

        case "building":
        case "area":
          group.getStyleClass().add(key);
          break;
        case "leisure":
        case "landuse":
        case "waterway":
          group.getStyleClass().add(key);
          group.getStyleClass().add(value);
          break;
        default:
          // Do nothing.
      }
    }

    this.fx = group;

    return this.fx;
  }

  /**
   * Provided that the relation describes a multipolygon, assemble polygons
   * from all the members of the relation.
   *
   * <p>
   * The algorithm is based on that described in the Open Street Map wiki. It
   * consists of the following steps (quoted from the wiki):
   *
   * <ul>
   * <li><b>Ring Assingment:</b> The purpose of the ring assignment step is to
   * make a number of closed rings out of all members of the relation. The
   * ordering of members in the relation does not matter.</li>
   * <li><b>Ring Grouping:</b> The purpose of the ring grouping step is to find
   * out which rings are nested into which other rings, and build polygons from
   * them.</li>
   * <li><b>Multipolygon Creation</b>
   * </ul>
   *
   * @see <a href="http://wiki.openstreetmap.org/wiki/Relation:multipolygon/
   * Algorithm">http://wiki.openstreetmap.org/wiki/Relation:multipolygon/
   * Algorithm</a>
   *
   * @return A shape created from all the members of the relation.
   */
  private Shape multipolygon() {
    // RA-1: Assemble all ways that are members of the relation. Mark them as
    // "unassigned", and reset the current ring count to 0.
    List<Way> u = new ArrayList<>();
    List<Way> a = new ArrayList<>();

    for (Element element: this.members()) {
      u.add((Way) element);
    }

    while (u.size() > 0) {
      Way next = u.remove(0);

      if (next == null) {
        continue;
      }

      // RA-2: Take one unassigned way and mark it assigned to the current ring.
      a.add(next);

      // RA-3: If the current ring is closed, increase ring counter and go to
      // RA-2.
      if (next.closed()) {
        continue;
      }

      // RA-4: If the current ring is not closed, take current ring's end node
      // and look for an unassigned way that starts or ends with this node.
      Iterator<Way> i = u.iterator();

      while (i.hasNext()) {
        Way inner = i.next();

        if (inner == null) {
          continue;
        }

        // RA-4, cont.: If such a way is found, add this way to the ring and go
        // to RA-3.
        if (next.endsIn(inner)) {
          next.append(inner);
          i.remove();

          // We've found a match. Bail out.
          break;
        }

        if (next.startsIn(inner)) {
          inner.append(next);
          next = inner;
          i.remove();

          // We've found a match. Bail out.
          break;
        }
      }
    }

    // We're done with u.
    u = null;

    // RG-2: Reset the polygon counter to 0.
    List<Shape> p = new ArrayList<>();

    while (a.size() > 0) {
      // RG-3: Find one unused ring that is not contained by any other ring.
      // Mark it as being the outer ring of the current polygon.
      Way outer = null;

      for (Way inner: a) {
        if (outer == null || inner.contains(outer)) {
          outer = inner;
        }
      }

      a.remove(outer);

      // RG-3, cont.: Optionally, check the ways making up this ring and
      // verify that they carry the role "outer".
      if (this.role(outer) != Role.OUTER) {
        continue;
      }

      Shape polygon = (Shape) outer.render();

      // RG-4: Find all unused rings that are contained by the ring found in
      // RG-3, but not contained by any other unused ring. Mark these rings as
      // being the holes of the current polygon.
      for (Way inner: a) {
        if (outer.contains(inner)) {
          boolean contained = true;

          for (Way way: a) {
            if (!way.equals(inner) && way.contains(inner)) {
              contained = false;

              // We've found a match. Bail out.
              break;
            }
          }

          // RG-5 and RG-6 are optional and have been skipped.

          // RG-7: Construct a polygon from the outer ring and the holes.
          if (contained) {
            polygon = Shape.subtract(polygon, (Shape) inner.render());
          }
        }
      }

      p.add(polygon);
    }

    // We're done with a.
    a = null;

    // MC-1 has been skipped as it's assumed that no polygons intersect. We have
    // faith in OpenStreetMap contributors.

    // MC-2: Construct a multipolygon from all polygons assembled in the ring
    // grouping step.
    Shape polygon = new Path();

    for (Shape shape: p) {
      polygon = Shape.union(polygon, shape);
    }

    // We're done with p.
    p = null;

    polygon.setFill(Color.TRANSPARENT);
    polygon.setStroke(Color.TRANSPARENT);
    polygon.getStyleClass().add("member");

    return polygon;
  }

  /**
   * Available roles of members within the relation.
   *
   * @see <a href="http://wiki.openstreetmap.org/wiki/Relation#Roles">
   * http://wiki.openstreetmap.org/wiki/Relation#Roles</a>
   */
  public static enum Role {
    /** No role. */
    NONE,

    /** The member is an inner part of a multipolygon. */
    INNER,

    /** The member is an outer part of a multipolygon. */
    OUTER;

    /**
     * Convert a string value to an enumerator element.
     *
     * @param value The string representation of an enumerator element.
     * @return      The enumerator element if found, otherwise NONE;
     */
    public static Role fromString(final String value) {
      try {
        return Role.valueOf(value.toUpperCase());
      }
      catch (Exception ex) {
        return NONE;
      }
    }
  }

  /**
   * Available types of relations.
   *
   * @see <a href="http://wiki.openstreetmap.org/wiki/Types_of_relation">
   * http://wiki.openstreetmap.org/wiki/Types_of_relation</a>
   */
  public static enum Type {
    /** No type. */
    NONE,

    /**
     * For areas where the outline consists of multiple ways, or that have
     * holes; also used for boundaries.
     */
    MULTIPOLYGON,

    /** Like bus routes, cycle routes and numbered highways. */
    ROUTE,

    /**
     * Bind all parts of a street together and everything else that belongs to
     * it.
     */
    STREET,

    /** For grouping boundaries and marking enclaves/exclaves. */
    BOUNDARY,

    /** Relation to group elements of a waterway=*. */
    WATERWAY,

    /**
     * Traffic enforcement devices; speed cameras, redlight cameras, weight
     * checks, etc.
     */
    ENFORCEMENT,

    /** Any kind of turn restriction. */
    RESTRICTION;

    /**
     * Convert a string value to an enumerator element.
     *
     * @param value The string representation of an enumerator element.
     * @return      The enumerator element if found, otherwise NONE;
     */
    public static Type fromString(final String value) {
      try {
        return Type.valueOf(value.toUpperCase());
      }
      catch (Exception ex) {
        return NONE;
      }
    }
  }
}
