/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.model;

// General utilities
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// JavaFX scene utilities
import javafx.scene.Group;

// JavaFX shapes
import javafx.scene.shape.FillRule;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

// Utilities
import dk.itu.kelvin.util.RectangleTree;

/**
 * A relation is an ordered list of one or more members (nodes, ways, or even
 * other relations) that may optionally be assigned a role.
 *
 * @see <a href="http://wiki.openstreetmap.org/wiki/Relation">
 *      http://wiki.openstreetmap.org/wiki/Relation</a>
 */
public final class Relation extends Element<Group>
  implements RectangleTree.Index {
  /**
   * UID for identifying serialized objects.
   */
  private static final long serialVersionUID = 48;

  /**
   * The members of the relation.
   */
  private List<Element> members;

  /**
   * The smallest x-coordinate of the relation.
   */
  private float minX;

  /**
   * The smallest y-coordinate of the relation.
   */
  private float minY;

  /**
   * The largest x-coordinate of the relation.
   */
  private float maxX;

  /**
   * The largest y-coordinate of the relation.
   */
  private float maxY;

  /**
   * Get the smallest x-coordinate of the relation.
   *
   * @return The smallest x-cooordinate of the relation.
   */
  public float minX() {
    return this.minX;
  }

  /**
   * Get the smallest y-coordinate of the relation.
   *
   * @return The smallest y-cooordinate of the relation.
   */
  public float minY() {
    return this.minY;
  }

  /**
   * Get the largest x-coordinate of the relation.
   *
   * @return The largest x-cooordinate of the relation.
   */
  public float maxX() {
    return this.maxX;
  }

  /**
   * Get the largest y-coordinate of the relation.
   *
   * @return The largest y-cooordinate of the relation.
   */
  public float maxY() {
    return this.maxY;
  }

  /**
   * Add an element to the relation.
   *
   * @param element The element to add to the relation.
   */
  public void add(final Element element) {
    if (element == null) {
      return;
    }

    if (this.members == null) {
      this.members = new ArrayList<>();
    }

    boolean empty = this.members.isEmpty();

    if (element instanceof Node) {
      Node node = (Node) element;

      this.minX = !empty ? Math.min(this.minX, node.x()) : node.x();
      this.minY = !empty ? Math.min(this.minY, node.y()) : node.y();
      this.maxX = !empty ? Math.max(this.maxX, node.x()) : node.x();
      this.maxY = !empty ? Math.max(this.maxY, node.y()) : node.y();
    }

    if (element instanceof Way) {
      Way way = (Way) element;

      this.minX = !empty ? Math.min(this.minX, way.minX()) : way.minX();
      this.minY = !empty ? Math.min(this.minY, way.minY()) : way.minY();
      this.maxX = !empty ? Math.max(this.maxX, way.maxX()) : way.maxX();
      this.maxY = !empty ? Math.max(this.maxY, way.maxY()) : way.maxY();
    }

    this.members.add(element);
  }

  /**
   * Get the members of the relation.
   *
   * @return The members of the relation.
   */
  public List<Element> members() {
    if (this.members == null) {
      this.members = new ArrayList<>();
    }

    return this.members;
  }

  /**
   * Get the JavaFX representation of the relation.
   *
   * @return The JavaFX representation of the relation.
   */
  public Group render() {
    Group group = new Group();

    group.getStyleClass().add("relation");

    for (Map.Entry<String, String> tag: this.tags().entrySet()) {
      group.getStyleClass().add(tag.getKey());
      group.getStyleClass().add(tag.getValue());
    }

    String type = this.tag("type");

    if (type != null) {
      switch (type) {
        case "multipolygon":
          group.getChildren().add(this.multipolygon());
          break;

        default:
          // Do nothing.
      }
    }

    return group;
  }

  /**
   * Construct a multipolygon from the ways in the relation.
   *
   * @return A multipolygon constructed from the ways of the relation.
   */
  private Path multipolygon() {
    Path path = new Path();

    path.setFillRule(FillRule.EVEN_ODD);

    // Don't use antialiasing for performance reasons.
    path.setSmooth(false);

    for (Element element: this.members) {
      if (!(element instanceof Way)) {
        continue;
      }

      Way way = (Way) element;

      for (int i = 0; i < way.nodes().size(); i++) {
        Node node = way.nodes().get(i);

        if (i == 0) {
          path.getElements().add(new MoveTo(node.x(), node.y()));
        }
        else {
          path.getElements().add(new LineTo(node.x(), node.y()));
        }
      }
    }

    path.getStyleClass().add("member");

    return path;
  }
}
