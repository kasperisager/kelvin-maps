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
 *      http://wiki.openstreetmap.org/wiki/Node</a>
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
   * Initialize a node.
   *
   * @param x The x-coordinate of the node.
   * @param y The y-coordinate of the node.
   */
  public Node(final double x, final double y) {
    this.x = (float) x;
    this.y = (float) y;
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
   * Get the JavaFX representation of the node.
   *
   * @return The JavaFX representation of the node.
   */
  public Label render() {
    Label label = new Label();

    return label;
  }
}
