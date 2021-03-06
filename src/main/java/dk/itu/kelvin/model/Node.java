/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.model;

// General utilities
import java.util.Map;

// JavaFX controls
import javafx.scene.control.Tooltip;
import javafx.scene.control.Label;

// Utilities
import dk.itu.kelvin.util.PointTree;
import dk.itu.kelvin.util.WeightedGraph;

/**
 * A node describes a 2-dimensional coordinate within a chart.
 *
 * @see <a href="http://wiki.openstreetmap.org/wiki/Node">
 *      http://wiki.openstreetmap.org/wiki/Node</a>
 */
public final class Node extends Element<Label>
  implements PointTree.Index, WeightedGraph.Node {
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

    label.setLayoutX(this.x);
    label.setLayoutY(this.y);

    label.getStyleClass().add("icon");

    String name = this.tag("name");

    if (name != null) {
      label.setTooltip(new Tooltip(name));
    }

    String icon = null;

    for (Map.Entry<String, String> tag: this.tags().entrySet()) {
      label.getStyleClass().add(tag.getKey());
      label.getStyleClass().add(tag.getKey() + "-" + tag.getValue());

      switch (tag.getValue()) {
        case "pub":             icon = "\uf26a"; break;
        case "cafe":            icon = "\uF272"; break;
        case "restaurant":      icon = "\uF3AA"; break;
        case "taxi":            icon = "\uf36f"; break;
        case "supermarket":     icon = "\uf3f8"; break;
        case "bank":            icon = "\uf316"; break;
        case "fast_food":       icon = "\uf2a8"; break;
        case "post_box":        icon = "\uf423"; break;
        case "telephone":       icon = "\uf2d2"; break;
        case "compressed_air":  icon = "\uf369"; break;
        case "solarium":        icon = "\uf4b7"; break;
        case "recycling":       icon = "\uf253"; break;
        case "toilets":         icon = "\uf25d|\uf202"; break;
        default:
          continue;
      }
    }

    if (icon != null) {
      label.setText(icon);
    }

    return label;
  }

  /**
   * Compute the hashcode of the current node.
   *
   * @return The computed hashcode of the current node.
   */
  @Override
  public int hashCode() {
    long bits = 7L;
    bits = 31L * bits + Float.hashCode(this.x);
    bits = 31L * bits + Float.hashCode(this.y);

    return (int) (bits ^ (bits >> 32));
  }

  /**
   * Check if the current node equals the specified object.
   *
   * @param object  The object to compare the current node to.
   * @return        A boolean indicating whether or not the current node
   *                equals the specified object.
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

    return (node.x() == this.x && node.y() == this.y);
  }

  @Override
  public String toString() {
    return "Node["
    + "x = " + this.x
    + ", y = " + this.y
    + "]";
  }
}
