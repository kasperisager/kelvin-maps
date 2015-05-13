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
import javafx.scene.Group;
import javafx.scene.Scene;

// JavaFX shape utilities
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;

// JavaFX geometry utilities
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;

// Fast utils
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

// Math
import dk.itu.kelvin.math.Haversine;
import dk.itu.kelvin.math.Mercator;
import dk.itu.kelvin.math.Projection;

// Controllers
import dk.itu.kelvin.controller.ChartController;

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
 * methods have been added to allow for adding our own data model.
 *
 * {@link Chart} constructor takes no parameters, adding initial layers elements
 * to the chart.
 */
public final class Chart extends Group {
  /**
   * Maximum zoom factor.
   */
  private static final double MAX_ZOOM_FACTOR = 4;

  /**
   * Minimum zoom factor.
   */
  private static final double MIN_ZOOM_FACTOR = 0.5;

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
  private Map<Anchor, Group> showing = new Object2ObjectOpenHashMap<>();

  /**
   * Keep track of the tiles of POI currently showing.
   */
  private Map<Anchor, Group> showingPOI = new Object2ObjectOpenHashMap<>();

  /**
   * HashSet representing the POI tags currently being shown on the map.
   */
  private Set<String> activeTags = new ObjectOpenHashSet<>();

  /**
   * HashSet representing the POI tags that is selected by the user.
   */
  private Set<String> currentTags = new ObjectOpenHashSet<>();

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
   * The bounds of the chart.
   */
  private BoundingBox bounds;

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

    this.bounds = bounds;

    this.mapWidth = Math.abs(bounds.maxX() - bounds.minX());
    this.mapHeight = Math.abs(bounds.maxY() - bounds.minY());

    double centerX = bounds.minX() + (this.mapWidth / 2);
    double centerY = bounds.minY() + (this.mapHeight / 2);

    Node centerNode = new Node(centerX, centerY);

    // The 10.000 is default padding to ensure it still works for really small
    // maps with coastlines.
    double paddingX = 10000 + this.mapWidth;
    double paddingY = 10000 + this.mapHeight;
    Rectangle wrapper = new Rectangle(
      bounds.minX() - paddingX,
      bounds.minY() - paddingY,
      this.mapWidth + paddingX * 2,
      this.mapHeight + paddingY * 2
    );
    wrapper.getStyleClass().add("wrapper");

    if (this.getChildren().contains(wrapper)) {
      this.getChildren().remove(wrapper);
    }


    this.getChildren().add(wrapper);
    this.landLayer.setClip(bounds.render());

    Projection mp = new Mercator();

    double mapLength = Haversine.distance(
      mp.yToLat(bounds.minY()),
      mp.xToLon(bounds.minX()),
      mp.yToLat(bounds.minY()),
      mp.xToLon(bounds.maxX())
    ) * 1000;
    double unitPrPx = mapLength / (this.mapWidth / 100);
    this.unitPrM = 100 / unitPrPx;

    this.center(centerNode, this.getMapScale());
  }

  /**
   * Calculates the appropriate scale for the map and window size, with the
   * intent see the whole map.
   * @return the scale needed to see the whole map.
   */
  private double getMapScale() {
    double scaleX = this.mapWidth / this.getScene().getWidth();
    double scaleY = this.mapHeight / this.getScene().getHeight();
    double scaleMax = Math.max(scaleX, scaleY);
    return 1 / scaleMax;
  }

  /**
   * Gets the elementStore of all elements.
   * @return the elementStore.
   */
  public ElementStore getElementStore() {
    return this.elementStore;
  }

  /**
   * Gets the bounds BoundingBox of the map.
   * @return the bounds.
   */
  public BoundingBox getBounds() {
    return this.bounds;
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
   * Centering chart on two addresses and adjust the scale.
   * @param addr1 the from address.
   * @param addr2 the destination address.
   */
  public void center(final Address addr1, final Address addr2) {
    double deltaX = addr2.x() - addr1.x();
    double deltaY = addr2.y() - addr1.y();
    Node center = new Node(addr1.x() + (deltaX / 2), addr1.y() + (deltaY / 2));

    double xDist = 50 + Math.abs(addr1.x() - addr2.x());
    double yDist = 200 + Math.abs(addr1.y() - addr2.y());
    double scaleX = xDist / this.getScene().getWidth();
    double scaleY = yDist / this.getScene().getHeight();
    double scaleMax = Math.max(scaleX, scaleY);

    if (1 / scaleMax < MIN_ZOOM_FACTOR) {
      this.center(addr1, MIN_ZOOM_FACTOR);
    } else {
      this.center(center, 1 / scaleMax);
    }
  }

  /**
   * Zoom the chart.
   *
   * @param factor  The factor with which to zoom.
   * @param x       The x-coordinate of the pivot point.
   * @param y       The y-coordinate of the pivot point.
   */
  public void zoom(final double factor, final double x, final double y) {
    double oldScale = this.getScaleX();
    double newScale = oldScale * factor;

    if (factor > 1 && newScale >= MAX_ZOOM_FACTOR) {
      return;
    }

    if (factor < 1 && newScale < MIN_ZOOM_FACTOR) {
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
   * Sets a specific scale on both x and y.
   * @param scale the scale to set.
   */
  private void setScale(final double scale) {
    if (scale <= MIN_ZOOM_FACTOR) {
      this.setScaleX(MIN_ZOOM_FACTOR);
      this.setScaleY(MIN_ZOOM_FACTOR);
    } else if (scale >= MAX_ZOOM_FACTOR) {
      this.setScaleX(MAX_ZOOM_FACTOR);
      this.setScaleY(MAX_ZOOM_FACTOR);
    } else {
      this.setScaleX(scale);
      this.setScaleY(scale);
    }
    ChartController.setScaleLength(this.unitPrM * this.getScaleX());
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

    this.minX = minX;
    this.minY = minY;
    this.maxX = maxX;
    this.maxY = maxY;

    Set<Anchor> anchors = new ObjectOpenHashSet<>();

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

    it = this.showingPOI.keySet().iterator();

    while (it.hasNext()) {
      Anchor anchor = it.next();

      if (anchors.contains(anchor)) {
        if (
          this.activeTags.containsAll(this.currentTags)
          && this.activeTags.size() == this.currentTags.size()
        ) {
          continue;
        }
      }
      this.hidePOI(anchor);
      it.remove();
    }
    for (Anchor anchor: anchors) {
      if (this.showingPOI.containsKey(anchor)) {
        if (
          this.activeTags.containsAll(this.currentTags)
          && this.activeTags.size() == this.currentTags.size()
        ) {
          continue;
        }
      }
      this.showPOI(anchor);
    }
  }

  /**
   * Sets visibility for labels attached to a unique type of POI.
   *
   * @param tag unique type of POI.
   */
  public void showSelectedPoi(final String tag) {
    this.currentTags.add(tag);
    this.layoutTiles();
  }

  /**
   * Remove visibility for labels attached to a unique type of POI.
   *
   * @param tag unique key in POI.
   */
  public void hidePointsOfInterests(final String tag) {
    this.currentTags.remove(tag);
    this.layoutTiles();
  }

  /**
   * Shows all current POI on a specific anchor.
   * @param anchor the anchor to show.
   */
  private void showPOI(final Anchor anchor) {
    if (anchor == null) {
      return;
    }

    int x = anchor.x;
    int y = anchor.y;

    Group group = new Group();

    for (String tag: this.currentTags) {
      List<Element> elements = this.elementStore.find()
        .types("poi")
        .tag(tag)
        .bounds(this.minX, this.minY, this.maxX + this.tileSize,
          this.maxY + this.tileSize)
        .get();
      for (Element element: elements) {
        Node node = (Node) element;
        Label label = node.render();
        group.getChildren().add(label);
      }
    }
    group.setClip(new Rectangle(x, y, this.tileSize, this.tileSize));
    group.setCache(true);

    this.metaLayer.getChildren().add(group);
    this.activeTags = new ObjectOpenHashSet<>(this.currentTags);
    this.showingPOI.put(anchor, group);
  }

  /**
   * Hides a specific anchor of POI.
   * @param anchor The anchor to hide.
   */
  private void hidePOI(final Anchor anchor) {
    if (anchor == null) {
      return;
    }

    Group group = this.showingPOI.get(anchor);

    this.metaLayer.getChildren().remove(group);
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
    this.showing.clear();
    this.showingPOI.clear();
    this.elementStore = new ElementStore();
    this.currentTags.clear();
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
