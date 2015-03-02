/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.model;

// General utilities
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The "Chart" data model describes a chart with elements defined by the OSM
 * data model.
 *
 * @see <a href="http://wiki.openstreetmap.org/wiki/Elements">
 *      http://wiki.openstreetmap.org/wiki/Elements</a>
 *
 * @version 1.0.0
 */
public final class Chart {
  /**
   * Factor with which to scale the lat/lon projection coordinates.
   */
  private static final float PROJECTION_FACTOR = 30000;

  /**
   * List of elements contained within the chart.
   */
  private List<Element> elements = new ArrayList<>();

  /**
   * The bounds of the chart.
   */
  private BoundingBox bounds;

  /**
   * Get the shape describing the chart bounds.
   *
   * @return The shape describing the chart bounds.
   */
  public BoundingBox bounds() {
    return this.bounds;
  }

  /**
   * Set the bounds of the chart.
   *
   * @param bounds The shape describing the chart bounds.
   */
  public void bounds(final BoundingBox bounds) {
    this.bounds = bounds;
  }

  /**
   * Get all elements of the chart.
   *
   * @return A list of all elements of the chart.
   */
  public List<Element> elements() {
    return this.elements;
  }

  /**
   * Add an element to the chart.
   *
   * @param element The element to add to the chart.
   */
  public void element(final Element element) {
    if (element == null) {
      return;
    }

    this.elements.add(element);
  }

  /**
   * Add a list of elements to the chart.
   *
   * @param elements The list of elements to add to the chart.
   */
  public void elements(final Collection<? extends Element> elements) {
    if (elements == null) {
      return;
    }

    this.elements.addAll(elements);
  }

  /**
   * Return the elements of the chart as a list of JavaFX nodes.
   *
   * Java unfortunately lacks multiple inheritance and JavaFX specifies Node as
   * an abstract class. Since elements of the chart extend different base
   * classes depending on their purpose (some shapes, others groups of shapes),
   * it's not possible to also extend the abstract Node class.
   *
   * We therefore assume that all elements extend some sort of Node-based class,
   * which allows us to simply cast the elements to JavaFX nodes.
   *
   * @return A list of JavaFX nodes created from the elements of the chart.
   */
  public List<javafx.scene.Node> nodes() {
    List<javafx.scene.Node> nodes = new ArrayList<>();

    for (Element element: this.elements) {
      nodes.add((javafx.scene.Node) element);
    }

    return nodes;
  }

  /**
   * Convert an x coordinate into a spherical longitude.
   *
   * @see <a href="http://wiki.openstreetmap.org/wiki/Mercator#Java">
   *      http://wiki.openstreetmap.org/wiki/Mercator#Java</a>
   *
   * @param x The x coordinate to convert.
   * @return  The corresponding spherical longitude.
   */
  public static float xToLon(final float x) {
    return x / PROJECTION_FACTOR;
  }

  /**
   * Convert a spherical longitude into an x coordinate.
   *
   * @see <a href="http://wiki.openstreetmap.org/wiki/Mercator#Java">
   *      http://wiki.openstreetmap.org/wiki/Mercator#Java</a>
   *
   * @param lon The spherical longitude to convert.
   * @return    The corresponding x coordinate.
   */
  public static float lonToX(final float lon) {
    return lon * PROJECTION_FACTOR;
  }

  /**
   * Convert a y coordinate into a spherical latitude.
   *
   * @see <a href="http://wiki.openstreetmap.org/wiki/Mercator#Java">
   *      http://wiki.openstreetmap.org/wiki/Mercator#Java</a>
   *
   * @param y The y coordinate to convert.
   * @return  The corresponding spherical latitude.
   */
  public static float yToLat(final float y) {
    return (float) Math.toDegrees(
      2 * Math.atan(Math.exp(Math.toRadians(y))) - Math.PI / 2
    ) / -PROJECTION_FACTOR;
  }

  /**
   * Convert a spherical latitude into a y coordinate.
   *
   * @see <a href="http://wiki.openstreetmap.org/wiki/Mercator#Java">
   *      http://wiki.openstreetmap.org/wiki/Mercator#Java</a>
   *
   * @param lat The spherical latitude to convert.
   * @return    The corresponding y coordinate.
   */
  public static float latToY(final float lat) {
    return (float) Math.toDegrees(
      Math.log(Math.tan(Math.PI / 4 + Math.toRadians(lat) / 2))
    ) * -PROJECTION_FACTOR;
  }
}
