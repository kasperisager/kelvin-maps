/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.model;

// General utilities
import java.util.HashMap;
import java.util.Map;

// I/O utilities
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

// JavaFX geometry
import javafx.geometry.Point2D;

/**
 * A node describes a 2-dimensional coordinate within a chart.
 *
 * @see <a href="http://wiki.openstreetmap.org/wiki/Node">
 *      http://wiki.openstreetmap.org/wiki/Node</a>
 */
public final class Node implements Element {
  /**
   * UID for identifying serialized objects.
   */
  private static final long serialVersionUID = 42;

  /**
   * Cache the hash of the node.
   */
  private int hash = 0;

  /**
   * The ID of the node.
   */
  private long id;

  /**
   * A map of tags associated with the node.
   */
  private Map<String, String> tags = new HashMap<>();

  /**
   * Drawing order of the node.
   */
  private Element.Order order = Element.Order.DEFAULT;

  /**
   * Drawing layer of the node.
   */
  private int layer;

  /**
   * The point representation of the node.
   *
   * Since none of the JavaFX Geometry classes are serializable, we cannot
   * directly extend any of them if we want our models to also be serializable.
   */
  private transient Point2D point;

  /**
   * Initialize a node.
   *
   * @param id  The ID of the node.
   * @param x   The x-coordinate of the node.
   * @param y   The y-coordinate of the node.
   */
  public Node(final long id, final float x, final float y) {
    this.id = id;
    this.point = new Point2D(x, y);
  }

  /**
   * Get the ID of the node.
   *
   * @return The ID of the node.
   */
  public long id() {
    return this.id;
  }

  /**
   * Add a tag to the node.
   *
   * @param key   The key of the tag.
   * @param value The value of the tag.
   * @return      The previous value of the key, if any.
   */
  public String tag(final String key, final String value) {
    return this.tags.put(key, value);
  }

  /**
   * Get a map of tags for the node.
   *
   * @return A map of tags for the node.
   */
  public Map<String, String> tags() {
    return this.tags;
  }

  /**
   * Get the drawing order of the node.
   *
   * @return The drawing order of the node.
   */
  public Element.Order order() {
    return this.order;
  }

  /**
   * Set the drawing order of the node.
   *
   * @param order The drawing order of the node.
   */
  public void order(final Element.Order order) {
    if (order == null) {
      return;
    }

    this.order = order;
  }

  /**
   * Get the drawing layer of the node.
   *
   * @return The drawing layer of the node.
   */
  public int layer() {
    return this.layer;
  }

  /**
   * Set the drawing layer of the node.
   *
   * @param layer The drawing layer of the node.
   */
  public void layer(final int layer) {
    this.layer = layer;
  }

  /**
   * Get the Point2D representation of the node.
   *
   * @return The Point2D representation of the node.
   */
  public Point2D point() {
    return this.point;
  }

  /**
   * Get the x-coordinate of the node.
   *
   * @return The x-coordinate of the node.
   */
  public double getX() {
    return this.point.getX();
  }

  /**
   * Get the y-coordinate of the node.
   *
   * @return The y-coordinate of the node.
   */
  public double getY() {
    return this.point.getY();
  }

  /**
   * Check if the current Node equals the specified object.
   *
   * @param object  The reference object with which to compare.
   * @return        Boolean indicating whether or not the Node is equal to the
   *                specified object.
   */
  @Override
  public boolean equals(final Object object) {
    if (object == null) {
      return false;
    }

    if (this == object) {
      return true;
    }

    if (!(object instanceof Node)) {
      return false;
    }

    Node node = (Node) object;

    return (
      this.id() == node.id()
      && this.order() == node.order()
      && this.layer() == node.layer()
      && this.point.equals(node.point())
    );
  }

  /**
   * Compute the hashcode of the Node.
   *
   * @return The computed hashcode of the Node.
   */
  @Override
  public int hashCode() {
    if (this.hash == 0) {
      long bits = 7L;
      bits = 31L * bits + this.id();
      bits = 31L * bits + (long) this.order().ordinal();
      bits = 31L * bits + (long) this.layer();
      this.hash = this.point.hashCode() + (int) (bits ^ (bits >> 32));
    }

    return this.hash;
  }

  /**
   * Write the node to an object output stream.
   *
   * @see <a href="https://books.google.dk/books?id=cfJzBgAAQBAJ">
   *      https://books.google.dk/books?id=cfJzBgAAQBAJ</a>
   *
   * @param out The object output stream to write the node to.
   *
   * @throws IOException In case of an I/O error.
   */
  private void writeObject(final ObjectOutputStream out) throws IOException {
    out.defaultWriteObject();

    // Write the Point2D coordinates to the stream.
    out.writeDouble(this.point.getX());
    out.writeDouble(this.point.getY());
  }

  /**
   * Read the node from an object input stream.
   *
   * @see <a href="https://books.google.dk/books?id=cfJzBgAAQBAJ">
   *      https://books.google.dk/books?id=cfJzBgAAQBAJ</a>
   *
   * @param in The object input stream to read the node from.
   *
   * @throws IOException            In case of an I/O error.
   * @throws ClassNotFoundException Class of a serialized object cannot be
   *                                found.
   */
  private void readObject(final ObjectInputStream in)
    throws IOException, ClassNotFoundException {
    in.defaultReadObject();

    // Read the Point2D coordinates from the stream.
    double x = in.readDouble();
    double y = in.readDouble();

    // Initialize the Point2D representation of the node.
    this.point = new Point2D(x, y);
  }
}
