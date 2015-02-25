/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.model;

// General utilities
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

// JavaFX scene utilities
import javafx.scene.Group;

// JavaFX shapes
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;

/**
 * A relation is an ordered list of one or more members (nodes, ways, or even
 * other relations) that may optionally be assigned a role.
 *
 * @see <a href="http://wiki.openstreetmap.org/wiki/Relation">
 * http://wiki.openstreetmap.org/wiki/Relation</a>
 */
public final class Relation extends Group implements Element {
  /**
   * The ID of the relation.
   */
  private long id;

  /**
   * A map of tags associated with the relation.
   */
  private Map<String, String> tags = new HashMap<>();

  /**
   * Drawing order of the node.
   */
  private Element.Order order = Element.Order.DEFAULT;

  /**
   * Drawing layer of the relation.
   */
  private int layer;

  /**
   * The members of the relation mapped to their roles.
   */
  private Map<Element, Relation.Role> elements = new HashMap<>();

  /**
   * The type of the relation.
   */
  private Type type = Type.NONE;

  /**
   * Initialize a relation.
   */
  public Relation() {
    this.getStyleClass().add("relation");
  }

  /**
   * Initialize a relation with an ID.
   *
   * @param id The ID of the relation.
   */
  public Relation(final long id) {
    this();
    this.id = id;
  }

  /**
   * Get the ID of the relation.
   *
   * @return The id of the relation.
   */
  public long id() {
    return this.id;
  }

  /**
   * Add a tag to the relation.
   *
   * @param key   The key of the tag.
   * @param value The value of the tag.
   * @return      The previous value of the key, if any.
   */
  public String tag(final String key, final String value) {
    switch (key) {
      case "type":
        this.type(Type.fromString(value));

        switch (value) {
          case "multipolygon":
            this.getChildren().add(this.multipolygon());
            break;
          default:
            // Do nothing.
        }
        break;

      case "building":
      case "area":
        this.getStyleClass().add(key);
        break;
      case "leisure":
      case "landuse":
      case "waterway":
        this.getStyleClass().add(key);
        this.getStyleClass().add(value);
        break;
      default:
        // Do nothing.
    }

    return this.tags.put(key, value);
  }

  /**
   * Get a map of tags for the relation.
   *
   * @return A map of tags for the relation.
   */
  public Map<String, String> tags() {
    return this.tags;
  }

  /**
   * Get the drawing order of the relation.
   *
   * @return The drawing order of the relation.
   */
  public Element.Order order() {
    return this.order;
  }

  /**
   * Set the drawing order of the relation.
   *
   * @param order The drawing order of the relation.
   */
  public void order(final Element.Order order) {
    if (order == null) {
      return;
    }

    this.order = order;
  }

  /**
   * Compare the drawing order of this relation with the drawing order of
   * another element.
   *
   * @param element The element to compare the current node to.
   * @return        A negative integer, zero, or a positive integer as this
   *                relation is less than, equal to, or greater than the
   *                specified element.
   */
  public int compareTo(final Element element) {
    return Element.Order.compare(this, element);
  }

  /**
   * Get the drawing layer of the relation.
   *
   * @return The drawing layer of the relation.
   */
  public int layer() {
    return this.layer;
  }

  /**
   * Set the drawing layer of the relation.
   *
   * @param layer The drawing layer of the relation.
   */
  public void layer(final int layer) {
    this.layer = layer;
  }

  /**
   * Get the type of the relation.
   *
   * @return The type of the relation.
   */
  public Type type() {
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
    return this.elements.get(element);
  }

  /**
   * Get a list of all members of the relation.
   *
   * @return A list of members of the relation.
   */
  public List<Element> members() {
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

    if (Shape.class.isAssignableFrom(element.getClass())) {
      ((Shape) element).getStyleClass().add("member");
    }

    this.elements.put(element, role);
  }

  /**
   * Shorthand for adding a element to the relation without a role.
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

      Shape polygon = outer;

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
            polygon = Shape.subtract(polygon, inner);
          }
        }
      }

      p.add(polygon);
    }

    // MC-1 has been skipped as it's assumed that no polygons intersect. We have
    // faith in OpenStreetMap contributors.

    // MC-2: Construct a multipolygon from all polygons assembled in the ring
    // grouping step.
    Shape polygon = new Path();

    for (Shape shape: p) {
      polygon = Shape.union(polygon, shape);
    }

    polygon.getStyleClass().add("member");

    return polygon;
  }

  /**
   * Available roles of members within the relation.
   *
   * @see <a href="http://wiki.openstreetmap.org/wiki/Relation#Roles">
   * http://wiki.openstreetmap.org/wiki/Relation#Roles</a>
   */
  public enum Role {
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
  public enum Type {
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
