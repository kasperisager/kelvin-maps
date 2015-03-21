/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.model;

// JavaFX controls
import javafx.scene.control.Label;

/**
 * A node describes a 2-dimensional coordinate within a chart.
 *
 * @see <a href="http://wiki.openstreetmap.org/wiki/Node">
 * http://wiki.openstreetmap.org/wiki/Node</a>
 */
public final class Node extends Element<Label> {
  /**
   * UID for identifying serialized objects.
   */
  private static final long serialVersionUID = 19;

  /**
   * X-coordinate of the node.
   */
  private float x;

  /**
   * Y-coordinate of the node.
   */
  private float y;

  /**
   * Initialize a node.
   *
   * @param x The x-coordinate of the node.
   * @param y The y-coordinate of the node.
   */
  public Node(final float x, final float y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Get the x-coordinate of the node.
   *
   * @return The x-coordinate of the node.
   */
  public float x() {
    return this.x;
  }

  /**
   * Get the y-coordinate of the node.
   *
   * @return The y-coordinate of the node.
   */
  public float y() {
    return this.y;
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
    if (object == null || !(object instanceof Node)) {
      return false;
    }

    if (this == object) {
      return true;
    }

    Node node = (Node) object;

    return (
      this.order() == node.order()
      &&
      this.layer() == node.layer()
      &&
      this.x == node.x()
      &&
      this.y == node.y()
    );
  }

  /**
   * Compute the hashcode of the Node.
   *
   * @return The computed hashcode of the Node.
   */
  @Override
  public int hashCode() {
    long bits = 7L;
    bits = 31L * bits + (long) this.order().ordinal();
    bits = 31L * bits + (long) this.layer();
    bits = 31L * bits + Double.doubleToLongBits(this.x);
    bits = 31L * bits + Double.doubleToLongBits(this.y);

    return (int) (bits ^ (bits >> 32));
  }

  /**
   * Get the JavaFX representation of the node.
   *
   * @return The JavaFX representation of the node.
   */
  public Label render() {
    Label label = new Label();

    return label;
  }

}
