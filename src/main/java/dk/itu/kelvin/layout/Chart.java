/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.layout;

// General utilities
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

// JavaFX scene utilities
import dk.itu.kelvin.controller.ChartController;
import dk.itu.kelvin.math.Haversine;
import dk.itu.kelvin.math.MercatorProjection;
import javafx.scene.Group;
import javafx.scene.Scene;

// JavaFX shape utilities
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;

// JavaFX geometry utilities
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;

// Koloboke collections
import net.openhft.koloboke.collect.set.hash.HashObjSets;
import net.openhft.koloboke.collect.map.hash.HashObjObjMaps;

// Models
import dk.itu.kelvin.model.Address;
import dk.itu.kelvin.model.BoundingBox;
import dk.itu.kelvin.model.Element;
import dk.itu.kelvin.model.Node;

// Stores
import dk.itu.kelvin.store.ElementStore;

/**
 * Chart class for handling which elements to display and where.
 *
 * <p>
 * This class functions as the canvas for all map specific elements, while the
 * extension to {@link Group} allows for addition and removal of elements.
 *
 * <p>
 * Aside from being able to add regular {@code JavaFX} elements, additional
 * methods have been added to allow for adding our own data model, respectively
 * {@link #add(Element)}, {@link #add(Node)}, {@link #add(BoundingBox)},
 * {@link #add(Land)} and implementation for adding a collection of elements
 * {@link #add(Collection)}.
 *
 * <p>
 * General map interactions are handled with methods, respectively
 * {@link #zoom(double)}, {@link #pan(double, double)}, {@link #rotate(double)},
 * additional implementations for zoom, pan and rotate are usually not called
 * directly, but kept {@code public} as it gives more options.
 * This class also has implementations for specialised interactions with the
 * {@link Chart}, respectively {@link #center(double, double, double)},
 * {@link #center(Node, double)} and {@link #setPointer(Node)}.
 *
 * {@link Chart} constructor takes no parameters, adding initial elements to
 * chart.
 */
public final class Chart extends Group {
  /**
   * Maximum zoom factor.
   */
  private static final double MAX_ZOOM_FACTOR = 4;

  /**
   * Minimum zoom factor.
   */
  private static double minZoomFactor = 0.0000001;

  /**
   * The size of each tile in the chart.
   */
  private static int tileSize = 256;

  /**
   * Stores all elements.
   */
  private ElementStore elementStore;

  /**
   * Keep track of the tiles currently showing.
   */
  private Map<Anchor, Group> showing = HashObjObjMaps.newMutableMap();

  /**
   * Current smallest x-coordinate of the chart viewport.
   */
  private int minX;

  /**
   * Current smallest y-coordinate of the chart viewport.
   */
  private int minY;

  /**
   * Current largest x-coordinate of the chart viewport.
   */
  private int maxX;

  /**
   * Current largest y-coordinate of the chart viewport.
   */
  private int maxY;

  /**
   * Layer of land elements.
   */
  private Group landLayer = new Group();

  /**
   * Layer of meta elements.
   */
  private Group metaLayer = new Group();

  /**
   * Map of points currently being shown.
   */
  private Map<Node, Label> points = HashObjObjMaps.newMutableMap();

  /**
   * The width of the map.
   */
  private double mapWidth;

  /**
   * The height of the map.
   */
  private double mapHeight;

  /**
   * A unit for how many pixel it takes to stretch 1 meter.
   */
  private double unitPrM;

  /**
   * Initialize the chart.
   */
  public Chart() {
    this.getChildren().addAll(this.landLayer, this.metaLayer);
  }

  /**
   * Setter for the element store field.
   * @param elementStore the element store object.
   */
  public void elementStore(final ElementStore elementStore) {
    this.elementStore = elementStore;
  }

  /**
   * Add bounds to the chart.
   *
   * @param bounds The bounds to add to the chart.
   */
  public void bounds(final BoundingBox bounds) {
    if (bounds == null) {
      return;
    }

    this.mapWidth = Math.abs(bounds.maxX() - bounds.minX());
    this.mapHeight = Math.abs(bounds.maxY() - bounds.minY());

    System.out.println(mapWidth);

    double centerX = bounds.minX() + (this.mapWidth / 2);
    double centerY = bounds.minY() + (this.mapHeight / 2);

    Node centerNode = new Node(centerX, centerY);
    /**
     * The 10.000 is default padding to ensure it still works for really small
     * maps with coastlines.
     */
    double paddingX = 10000 + this.mapWidth;
    double paddingY = 10000 + this.mapHeight;

    Rectangle wrapper = new Rectangle(
      bounds.minX() - paddingX,
      bounds.minY() - paddingY,
      mapWidth + paddingX*2,
      mapHeight + paddingY*2
    );
    wrapper.getStyleClass().add("boundingBox");

    this.getChildren().add(wrapper);
    this.landLayer.setClip(bounds.render());

    MercatorProjection mp = new MercatorProjection();

    double mapLength = Haversine.distance(
      (float)mp.yToLat(bounds.minY()),
      (float)mp.xToLon(bounds.minX()),
      (float)mp.yToLat(bounds.minY()),
      (float)mp.xToLon(bounds.maxX())
    )*1000;
    double unitPrPx = mapLength / (mapWidth / 100);
    unitPrM = 100/unitPrPx;
    System.out.println("map length: "+mapLength+" map width: "+mapWidth);
    System.out.println("unit pr. 100px: "+unitPrPx);
    System.out.println("pixel pr. 100m: "+unitPrM);


    this.calcMinZoomFactor();
    ChartController.instance().setScaleLength(unitPrM);
    //this.center(centerNode, 1);
    this.center(centerNode, this.minZoomFactor);
  }

  /**
   * Recalculates the min zoom factor, in case the user changes the window size.
   */
  private void calcMinZoomFactor() {
    double scaleX = this.mapWidth / this.getScene().getWidth();
    double scaleY = this.mapHeight / this.getScene().getHeight();
    double scaleMax = Math.max(scaleX, scaleY);
    this.minZoomFactor = 1 / scaleMax;
  }

  /**
   * Pan the chart.
   *
   * @param x The amount to pan on the x-axis.
   * @param y The amount to pan on the y-axis.
   */
  public void pan(final double x, final double y) {
    this.setTranslateX(this.getTranslateX() + x);
    this.setTranslateY(this.getTranslateY() + y);
    this.layoutTiles();
  }

  /**
   * Center the chart on the specified scene coordinate.
   *
   * @param x The x-coordinate to center on.
   * @param y The y-coordinate to center on.
   */
  public void center(final double x, final double y) {
    this.pan(
      -(x - this.getScene().getWidth() / 2),
      -(y - this.getScene().getHeight() / 2)
    );
  }

  /**
   * Center the chart on the specified scene coordinate.
   *
   * @param x     The x-coordinate to center on.
   * @param y     The y-coordinate to center on.
   * @param scale The scale to set after centering.
   */
  public void center(final double x, final double y, final double scale) {
    this.setScale(scale);
    this.center(x, y);
  }

  /**
   * Center the chart on the specified node.
   *
   * @param node The node to center on.
   */
  public void center(final Node node) {
    this.center(
      this.localToScene(node.x(), node.y()).getX(),
      this.localToScene(node.x(), node.y()).getY()
    );
  }

  /**
   * Center the chart on the specified node.
   *
   * @param node  The node to center on.
   * @param scale The scale to set after centering.
   */
  public void center(final Node node, final double scale) {
    this.setScale(scale);
    this.center(node);
  }

  /**
   * Center the chart on the specified address.
   *
   * @param address The address to center on.
   */
  public void center(final Address address) {
    this.center(
      this.localToScene(address.x(), address.y()).getX(),
      this.localToScene(address.x(), address.y()).getY()
    );
  }

  /**
   * Center the chart on the specified address.
   *
   * @param address The address to center on.
   * @param scale   The scale to set after centering.
   */
  public void center(final Address address, final double scale) {
    this.setScale(scale);
    this.center(address);
  }

  /**
   * Zoom the chart.
   *
   * @param factor  The factor with which to zoom.
   * @param x       The x-coordinate of the pivot point.
   * @param y       The y-coordinate of the pivot point.
   */
  public void zoom(final double factor, final double x, final double y) {
    this.calcMinZoomFactor();
    double oldScale = this.getScaleX();
    double newScale = oldScale * factor;

    if (factor > 1 && newScale >= MAX_ZOOM_FACTOR) {
      return;
    }

    if (factor < 1 && newScale < minZoomFactor) {
      return;
    }

    this.setScale(newScale);

    // Calculate the difference between the new and the old scale.
    double f = (newScale / oldScale) - 1;

    // Get the layout bounds of the chart in local coordinates.
    Bounds bounds = this.localToScene(this.getLayoutBounds());

    double dx = x - (bounds.getMinX() + bounds.getWidth() / 2);
    double dy = y - (bounds.getMinY() + bounds.getHeight() / 2);

    this.setTranslateX(this.getTranslateX() - f * dx);
    this.setTranslateY(this.getTranslateY() - f * dy);

    this.layoutTiles();
  }

  /**
   * Zoom the chart, using the center of the scene as the pivot point.
   *
   * @param factor The factor with which to zoom.
   */
  public void zoom(final double factor) {
    this.zoom(
      factor,
      this.getScene().getWidth() / 2,
      this.getScene().getHeight() / 2
    );
  }

  /**
   * Rotate the chart.
   *
   * @param angle The angle of the rotation.
   * @param x     The x-coordinate of the pivot point.
   * @param y     The y-coordinate of the pivot point.
   */
  public void rotate(final double angle, final double x, final double y) {
    double oldAngle = this.getRotate();
    double newAngle = oldAngle + angle;

    this.setRotate(newAngle);

    // Get the layout bounds of the chart in local coordinates.
    Bounds bounds = this.localToScene(this.getLayoutBounds());

    double dx = x - (bounds.getMinX() + bounds.getWidth() / 2);
    double dy = y - (bounds.getMinY() + bounds.getHeight() / 2);
    double dt = Math.toRadians(newAngle - oldAngle);

    this.setTranslateX(
      this.getTranslateX()
    + (dx - dx * Math.cos(dt) + dy * Math.sin(dt))
    );

    this.setTranslateY(
      this.getTranslateY()
    + (dy - dx * Math.sin(dt) - dy * Math.cos(dt))
    );

    this.layoutTiles();
  }

  /**
   * Rotate the chart, using the center of the scene as the pivot point.
   *
   * @param angle The angle of the rotation.
   */
  public void rotate(final double angle) {
    this.rotate(
      angle,
      this.getScene().getWidth() / 2,
      this.getScene().getHeight() / 2
    );
  }

  private void setScale(double scale){
    System.out.println("new length: " + unitPrM*(scale));
    ChartController.instance().setScaleLength(unitPrM*(scale));
    this.setScaleX(scale);
    this.setScaleY(scale);
  }

  /**
   * Layout the tiles of the chart.
   */
  private void layoutTiles() {
    Scene scene = this.getScene();

    if (scene == null) {
      return;
    }

    Point2D min = this.sceneToLocal(0, 0);
    Point2D max = this.sceneToLocal(scene.getWidth(), scene.getHeight());

    int minX = (int) (this.tileSize * Math.floor(min.getX() / this.tileSize));
    int minY = (int) (this.tileSize * Math.floor(min.getY() / this.tileSize));
    int maxX = (int) (this.tileSize * Math.floor(max.getX() / this.tileSize));
    int maxY = (int) (this.tileSize * Math.floor(max.getY() / this.tileSize));

    if (
      minX == this.minX
      && minY == this.minY
      && maxX == this.maxX
      && maxY == this.maxY
    ) {
      return;
    }

    this.minX = minX;
    this.minY = minY;
    this.maxX = maxX;
    this.maxY = maxY;

    Set<Anchor> anchors = HashObjSets.newMutableSet();

    for (int x = minX; x <= maxX; x += this.tileSize) {
      for (int y = minY; y <= maxY; y += this.tileSize) {
        anchors.add(new Anchor(x, y));
      }
    }

    Iterator<Anchor> it = this.showing.keySet().iterator();

    while (it.hasNext()) {
      Anchor anchor = it.next();

      if (anchors.contains(anchor)) {
        continue;
      }

      this.hide(anchor);
      it.remove();
    }

    for (Anchor anchor: anchors) {
      if (this.showing.containsKey(anchor)) {
        continue;
      }

      this.show(anchor);
    }
  }

  /**
   * Sets visibility for labels attached to a unique type of POI.
   *
   * @param tag unique type of POI.
   */
  public void showSelectedPoi(final String tag) {
    List<Element> elements = this.elementStore.find()
      .types("poi")
      .tag(tag)
      .bounds(this.minX, this.minY, this.maxX + this.tileSize,
        this.maxY + this.tileSize)
      .get();

      for (Element element: elements) {
        Node node = (Node) element;
        Label label = node.render();
        this.points.put(node, label);
        this.metaLayer.getChildren().add(label);
      }
  }

  /**
   * Remove visibility for labels attached to a unique type of POI.
   *
   * @param tag unique key in POI.
   */
  public void hidePointsOfInterests(final String tag) {
    List<Element> elements = this.elementStore.find()
      .types("poi")
      .tag(tag)
      .bounds(this.minX, this.minY, this.maxX + this.tileSize,
        this.maxY + this.tileSize)
      .get();

    for (Element element : elements) {
      Node node = (Node) element;
      Label label = this.points.remove(node);
      this.metaLayer.getChildren().remove(label);
    }
  }

  /**
   * Show the specified anchor.
   *
   * @param anchor The anchor to show.
   */
  private void show(final Anchor anchor) {
    if (anchor == null) {
      return;
    }

    int x = anchor.x;
    int y = anchor.y;

    List<Element> elements = this.elementStore.find()
      .types("land", "way", "relation", "transportWay")
      .bounds(x, y, x + this.tileSize, y + this.tileSize)
      .get();

    if (elements.isEmpty()) {
      return;
    }

    Collections.sort(elements, Element.COMPARATOR);

    Group group = new Group();
    group.setClip(new Rectangle(x, y, this.tileSize, this.tileSize));
    group.setCache(true);

    for (Element element: elements) {
      group.getChildren().add(element.render());
    }

    this.landLayer.getChildren().add(group);

    this.showing.put(anchor, group);
  }

  /**
   * Hide the specified anchor.
   *
   * @param anchor The anchor to hide.
   */
  private void hide(final Anchor anchor) {
    if (anchor == null) {
      return;
    }

    Group group = this.showing.get(anchor);

    this.landLayer.getChildren().remove(group);
  }

  /**
   * Removes children from layers and sets collections to null.
   */
  public void clear() {
    this.landLayer.getChildren().clear();
    this.metaLayer.getChildren().clear();
    this.elementStore = new ElementStore();
  }

  /**
   * The {@link Anchor} class describes an anchor point for a group of elements
   * within the chart.
   */
  private static class Anchor {
    /**
     * The x-coordinate of the anchor.
     */
    private int x;

    /**
     * The y-coordinate of the anchor.
     */
    private int y;

    /**
     * Initialize a new anchor.
     *
     * @param x The x-coordinate of the anchor.
     * @param y The y-coordinate of the anchor.
     */
    public Anchor(final int x, final int y) {
      this.x = x;
      this.y = y;
    }

    /**
     * Check if the anchor equals the specified object.
     *
     * @param object  The object to compare the anchor to.
     * @return        A boolean indicating whether or not the anchor is equal to
     *                The specified object.
     */
    public boolean equals(final Object object) {
      if (object == null || !(object instanceof Anchor)) {
        return false;
      }

      if (object == this) {
        return true;
      }

      Anchor anchor = (Anchor) object;

      return anchor.x == this.x && anchor.y == this.y;
    }

    /**
     * Compute the hash code of the anchor.
     *
     * @return The hash code of the anchor.
     */
    public int hashCode() {
      long bits = 7L;
      bits = 31L * bits + this.x;
      bits = 31L * bits + this.y;

      return (int) (bits ^ (bits >> 32));
    }

  }
}
