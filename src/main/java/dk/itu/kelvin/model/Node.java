/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.model;

// JavaFX controls
import javafx.scene.control.Tooltip;
import javafx.scene.control.Label;

// Utilities
import dk.itu.kelvin.util.PointTree;
import javafx.scene.paint.Color;

import java.util.Map;

/**
 * A node describes a 2-dimensional coordinate within a chart.
 *
 * @see <a href="http://wiki.openstreetmap.org/wiki/Node">
 *      http://wiki.openstreetmap.org/wiki/Node</a>
 */
public final class Node extends Element<Label> implements PointTree.Index {
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
    for (Map.Entry<String, String> tag: this.tags().entrySet()) {
      //String key = tag.getKey();
      String value = tag.getValue();

      switch (value) {
        case "pub":
          return this.createLabel("\uf26a", value, Color.YELLOW);
        case "cafe":
          return this.createLabel("\uF272", value, Color.BLACK);
        case "restaurant":
          return this.createLabel("\uF3AA", value, Color.BLACK);
        case "taxi":
          return this.createLabel("\uf36f", value, Color.YELLOW);
        case "supermarket":
          return this.createLabel("\uf3f8", value, Color.BLACK);
        case "bank":
          return this.createLabel("\uf316", value, Color.YELLOW);
        case "fast_food":
          return this.createLabel("\uf2a8", value, Color.YELLOW);
        case "toilets":
          return this.createLabel("\uf3e4", value, Color.YELLOW);
        case "post_box":
          return this.createLabel("\uf423", value, Color.YELLOW);
        case "telephone":
          return this.createLabel("\uf2d2", value, Color.YELLOW);
        case "compressed_air":
          return this.createLabel("\uf369", value, Color.YELLOW);
        case "solarium":
          return this.createLabel("\uf4b7", value, Color.YELLOW);
        case "recycling":
          return this.createLabel("\uf253", value, Color.YELLOW);
        default:
          // Do nothing.
      }
    }

    return null;
  }

  /**
   * Create a label for a unique POI.
   *
   * @param icon defines a unique icon for each key.
   * @param value defines the key.
   * @param color defines the color of the icon.
   * @return label.
   */
  private Label createLabel(
    final String icon,
    final String value,
    final Color color
  ) {
    Label l = new Label();
    l.setText(icon);
    l.getStyleClass().add("icon");

    l.setLayoutX(this.x());
    l.setLayoutY(this.y());

    if (this.tags().get("name") != null) {
      String name = this.tags().get("name");
      l.setTooltip(new Tooltip(name));
    }
    return l;
  }
}
