/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.model;

// General utilities
import java.util.Comparator;

// JavaFX shapes
import javafx.scene.shape.Polyline;

// JavaFX paint
import javafx.scene.paint.Color;

// Utilities
import dk.itu.kelvin.util.ArrayList;
import dk.itu.kelvin.util.List;
import dk.itu.kelvin.util.Map;

/**
 * A way is an ordered list of 2 to 2,000 nodes.
 *
 * @see <a href="http://wiki.openstreetmap.org/wiki/Way">
 *      http://wiki.openstreetmap.org/wiki/Way</a>
 */
public final class Way extends Element<Polyline> {
  /**
   * UID for identifying serialized objects.
   */
  private static final long serialVersionUID = 67;

  /**
   * Comparator for comparing the drawing layer and order of two ways.
   */
  public static final Comparator<Way> COMPARATOR =
    new Comparator<Way>() {
    /**
     * Compare two ways taking into account their drawing layer and drawing
     * order.
     *
     * @param a The first way.
     * @param b The second way.
     * @return  A negative integer, zero, or a positive integer as the first way
     *          is less than, equal to, or greater than the second way.
     */
    @Override
    public int compare(final Way a, final Way b) {
      return Way.compare(a, b);
    }
  };

  /**
   * Drawing order of the way.
   *
   * <p>
   * The order is initialized on-demand when first accessed to avoid allocating
   * memory to never-used orders.
   */
  private Order order;

  /**
   * Drawing layer of the way.
   */
  private int layer;

  /**
   * List of nodes contained within the way.
   *
   * <p>
   * The list is initialized on-demand when first accessed to avoid allocating
   * memory to empty lists.
   */
  private List<Node> nodes;

  /**
   * Get the order of the element.
   *
   * @return The order of the element.
   */
  public Order order() {
    if (this.order == null) {
      return Order.DEFAULT;
    }

    return this.order;
  }

  /**
   * Set the order of the element.
   *
   * @param order The order of the element.
   */
  public void order(final Order order) {
    if (order == null) {
      return;
    }

    this.order = order;
  }

  /**
   * Get the layer of the element.
   *
   * @return The layer of the element.
   */
  public int layer() {
    return this.layer;
  }

  /**
   * Set the layer of the element.
   *
   * @param layer The layer of the element.
   */
  public void layer(final int layer) {
    this.layer = layer;
  }

  /**
   * Get the initial node of the way.
   *
   * @return The initial node of the way.
   */
  public Node start() {
    if (this.nodes == null) {
      return null;
    }

    return this.nodes.get(0);
  }

  /**
   * Get the last node of the way.
   *
   * @return The last node of the way.
   */
  public Node end() {
    if (this.nodes == null) {
      return null;
    }

    return this.nodes.get(this.nodes.size() - 1);
  }

  /**
   * Is the way closed?
   *
   * @return Boolean indicating whether or not the way is closed.
   */
  public boolean closed() {
    Node start = this.start();
    Node end = this.end();

    if (start == null || end == null) {
      return false;
    }

    return start.equals(end);
  }

  /**
   * Check if the way is open (unclosed).
   *
   * @return Boolean indicating whether or not the way is open.
   */
  public boolean open() {
    return !this.closed();
  }

  /**
   * Check if the current way starts in the same node as another way either ends
   * or starts.
   *
   * @param way The way to check against.
   * @return    Boolean indicating whether or not the current way starts in
   *            either the end or start node of the specified way.
   */
  public boolean startsIn(final Way way) {
    if (way == null) {
      return false;
    }

    Node start = this.start();
    Node end = this.end();

    if (start == null || end == null) {
      return false;
    }

    return (this.start().equals(way.start()) || this.start().equals(way.end()));
  }

  /**
   * Check if the current way end in the same node as another way either ends or
   * starts.
   *
   * @param way The way to check against.
   * @return    Boolean indicating whether or not the current way ends in either
   *            the end or start node of the specified way.
   */
  public boolean endsIn(final Way way) {
    if (way == null) {
      return false;
    }

    return (this.end().equals(way.start()) || this.end().equals(way.end()));
  }

  /**
   * Get the nodes contained within the way.
   *
   * @return The nodes contained within the way.
   */
  public List<Node> nodes() {
    if (this.nodes == null) {
      this.nodes = new ArrayList<>();
    }

    return this.nodes;
  }

  /**
   * Add a node to the way.
   *
   * @param node The node to add to the way.
   */
  public void add(final Node node) {
    if (node == null) {
      return;
    }

    if (this.nodes == null) {
      this.nodes = new ArrayList<>();
    }

    this.nodes.add(node);
  }

  /**
   * Add a list of nodes to the way.
   *
   * @param nodes The nodes to add to the way.
   */
  public void add(final List<Node> nodes) {
    if (nodes == null || nodes.isEmpty()) {
      return;
    }

    if (this.nodes == null) {
      this.nodes = new ArrayList<>();
    }

    this.nodes.addAll(nodes);
  }

  /**
   * Append the nodes of another way to the current way.
   *
   * @param way The way whose nodes to append to the current way.
   */
  public void append(final Way way) {
    if (way == null) {
      return;
    }

    this.add(way.nodes());
  }

  /**
   * Get the JavaFX representation of the way.
   *
   * @return The JavaFX representation of the way.
   */
  public Polyline render() {
    Polyline polyline = new Polyline();

    // Don't use antialiasing for performance reasons.
    polyline.setSmooth(false);

    // Ensure that the bounds of the way are calculated correctly. This is only
    // the case if both a stroke and a fill is set, otherwise calculation of
    // bounds will be off.
    polyline.setStroke(Color.TRANSPARENT);
    polyline.setFill(Color.TRANSPARENT);

    for (Map.Entry<String, String> tag: this.tags().entrySet()) {
      String key = tag.getKey();
      String value = tag.getValue();

      switch (key) {
        case "building":
        case "area":
          polyline.getStyleClass().add(key);
          break;
        case "highway":
        case "leisure":
        case "landuse":
        case "natural":
        case "waterway":
          polyline.getStyleClass().add(key);
          polyline.getStyleClass().add(value);
          break;
        default:
          // Do nothing.
      }
    }

    for (Node node: this.nodes) {
      polyline.getPoints().add((double) node.x());
      polyline.getPoints().add((double) node.y());
    }

    return polyline;
  }

  /**
   * Does the current way contain the specified way?.
   *
   * @param way The way to check containment of.
   * @return    Boolean indicating whether or not the current way contains the
   *            specified way.
   */
  public boolean contains(final Way way) {
    if (way == null) {
      return false;
    }

    Polyline polyline = this.render();

    return (
      polyline.contains(polyline.parentToLocal(
        way.start().x(), way.start().y())
      )
      &&
      polyline.contains(polyline.parentToLocal(
        way.end().x(), way.end().y())
      )
    );
  }

  /**
   * Enumerator describing the drawing order of different ways.
   *
   * <p>
   * The declaration order of the enumerator elements is what determines the
   * drawing order. Elements declared first are thus drawn first and appear
   * below elements declared after.
   */
  public static enum Order {
    /** Water draw order. */
    NATURAL_WATER,

    /** Default draw order. */
    DEFAULT,

    /** Default natural draw order. */
    NATURAL,

    /** Default landuse draw order. */
    LANDUSE,

    /** Default waterway draw order. */
    WATERWAY,

    /** Islands. */
    PLACE_ISLAND,

    /** Default leisure draw order. */
    LEISURE,

    /** Default highway draw order. */
    HIGHWAY,

    /** Building draw order. */
    BUILDING,

    /** Service highway draw order. */
    HIGHWAY_SERVICE,

    /** Residential highway draw order. */
    HIGHWAY_RESIDENTIAL,

    /** Tertiary highway draw order. */
    HIGHWAY_TERTIARY,

    /** Secondary highway draw order. */
    HIGHWAY_SECONDARY,

    /** Primary highway draw order. */
    HIGHWAY_PRIMARY,

    /** Trunk highway draw order. */
    HIGHWAY_TRUNK,

    /** Motorway highway draw order. */
    HIGHWAY_MOTORWAY;

    /**
     * Number of enumerator elements.
     */
    public static final int SIZE = Order.values().length;

    /**
     * Convert a key and value to an enumerator element.
     *
     * @param key   The "key" of the enumerator element.
     * @param value The "value" of the enumerator element.
     * @return      The enumerator element if found, otherwise null;
     */
    public static Order fromString(final String key, final String value) {
      if (key == null || value == null) {
        return null;
      }

      for (Order order: Order.values()) {
        if (
          order.toString().equalsIgnoreCase(String.format("%s_%s", key, value))
          ||
          order.toString().equalsIgnoreCase(key)
        ) {
          return order;
        }
      }

      return null;
    }
  }

  /**
   * Compare two ways taking into account their drawing layer and drawing order.
   *
   * @param a The first way.
   * @param b The second way.
   * @return  A negative integer, zero, or a positive integer as the first way
   *          is less than, equal to, or greater than the second way.
   */
  public static int compare(final Way a, final Way b) {
    if (a == null && b == null) {
      return 0;
    }
    else if (a == null) {
      return -1;
    }
    else if (b == null) {
      return 1;
    }

    return Integer.compare(
      a.order().ordinal() + (Order.SIZE * a.layer()),
      b.order().ordinal() + (Order.SIZE * b.layer())
    );
  }
}
