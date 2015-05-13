/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.controller;

// General utilities
import java.util.List;
import java.util.Properties;

// I/O utilities
import java.io.File;

// JavaFX application utilities
import javafx.application.Platform;

// JavaFX layout
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;

// JavaFX shapes
import javafx.scene.shape.Polyline;

// JavaFX inputs
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;

// JavaFX text
import javafx.scene.text.Text;

// JavaFX controls
import javafx.scene.control.Label;

// FXML utilities
import javafx.fxml.FXML;

// Utilities
import dk.itu.kelvin.util.Graph;
import dk.itu.kelvin.util.SpatialIndex;
import dk.itu.kelvin.util.ShortestPath;

// Math
import dk.itu.kelvin.math.Geometry;

// Parser
import dk.itu.kelvin.parser.Parser;

// Layout
import dk.itu.kelvin.layout.Chart;

// Models
import dk.itu.kelvin.model.Way;
import dk.itu.kelvin.model.Address;
import dk.itu.kelvin.model.Relation;
import dk.itu.kelvin.model.Node;
import dk.itu.kelvin.model.BoundingBox;

// Stores
import dk.itu.kelvin.store.ElementStore;

/**
 * Chart controller class.
 */
public final class ChartController {
  /**
   * The ChartController instance.
   */
  private static ChartController instance;

  /**
   * Default zoom step factor.
   */
  private static final double ZOOM_STEP = 0.025;

  /**
   * Default zoom in factor.
   */
  private static final double ZOOM_IN = 1 + ZOOM_STEP;

  /**
   * Default zoom out factor.
   */
  private static final double ZOOM_OUT = 1 / ZOOM_IN;

  /**
   * Mouse X coordinate for dragging.
   */
  private double initialMouseDragX;

  /**
   * Mouse Y coordinate for dragging.
   */
  private double initialMouseDragY;

  /**
   * Mouse X coordinate for scrolling and zooming.
   */
  private double initialMouseScrollX;

  /**
   * Mouse Y coordinate for scrolling and zooming.
   */
  private double initialMouseScrollY;

  /**
   * Text(icon) representing the found address.
   */
  private Text locationPointer;

  /**
   * Text(icon) representing the destination address.
   */
  private Text destinationPointer;

  /**
   * Element store storing all elements.
   */
  private static ElementStore elementStore;

  /**
   * Polyline to represent the route.render().
   */
  private Polyline route;

  /**
   * The Canvas element to add all the Chart elements to.
   */
  @FXML
  private Chart chart;

  /**
   * The VBox surrounding the scale indicator.
   */
  @FXML
  private HBox scaleVBox;

  /**
   * Indicator for scale.
   */
  @FXML
  private Label scaleIndicatorLabel;

  /**
   * The main parent element.
   */
  @FXML
  private StackPane mainStackPane;

  /**
   * Initialize a new chart controller.
   *
   * <p>
   * <b>OBS:</b> This constructor can only ever be called once by JavaFX.
   */
  public ChartController() {
    super();

    if (ChartController.instance != null) {
      throw new RuntimeException("Only a single controller instance can exist");
    }
  }

  /**
   * Initialize the controller.
   *
   * @throws Exception In case of an error. Duh.
   */
  @FXML
  private void initialize() throws Exception {
    ChartController.instance = this;

    this.initLocationPointer();
    this.initDestinationPointer();
  }

  /**
   * Initializing properties for location pointer and adding to chart.
   */
  private void initLocationPointer() {
    this.locationPointer = new Text();
    this.locationPointer.getStyleClass().add("icon");
    this.locationPointer.getStyleClass().add("address-label");
    this.locationPointer.setText("\uf456");
    this.locationPointer.setVisible(false);
    this.chart.getChildren().add(this.locationPointer);
  }

  /**
   * Initializing properties for location destination pointer and adding to
   * chart.
   */
  private void initDestinationPointer() {
    this.destinationPointer = new Text();
    this.destinationPointer.getStyleClass().add("icon");
    this.destinationPointer.getStyleClass().add("address-label");
    this.destinationPointer.setText("\uf456");
    this.destinationPointer.setVisible(false);
    this.chart.getChildren().add(this.destinationPointer);
  }

  /**
   * Moves the scale VBox relative to BOTTOM_LEFT.
   *
   * @param x how much to move scale indicator along x-axis [px].
   */
  public static void moveScale(final double x) {
    ChartController.instance.scaleVBox.setTranslateX(x);
  }

  /**
   * Find shortest path between 2 nodes.
   * @param n The from node.
   * @param m The to node.
   * @param type The type of graph initiate.
   */
  public static void findShortestPath(
    final Node n,
    final Node m,
    final String type
  ) {
    SpatialIndex.Point p1 = new SpatialIndex.Point(n.x(), n.y());
    SpatialIndex.Point p2 = new SpatialIndex.Point(m.x(), m.y());

    Way fromWay = elementStore.transportWaysTree().nearest(p1);
    Way toWay = elementStore.transportWaysTree().nearest(p2);

    double distanceFrom = 0;
    Node from = null;

    for (Node qu: fromWay.nodes()) {
      SpatialIndex.Point ptemp = new SpatialIndex.Point(qu.x(), qu.y());

      if (distanceFrom == 0 || Geometry.distance(ptemp, p1) < distanceFrom) {
        distanceFrom = Geometry.distance(ptemp, p1);
        from = new Node(qu.x(), qu.y());
      }
    }

    double distanceTo = 0;
    Node to = null;

    for (Node qu: toWay.nodes()) {
      SpatialIndex.Point ptemp = new SpatialIndex.Point(qu.x(), qu.y());

      if (distanceTo == 0 || Geometry.distance(ptemp, p2) < distanceTo) {
        distanceTo = Geometry.distance(ptemp, p2);
        to = new Node(qu.x(), qu.y());
      }
    }

    if (from == null || to == null) {
      return;
    }

    Graph<Node, Way> graph = null;
    Properties properties = new Properties();

    switch (type.toLowerCase()) {
      case "bicycle":
        graph = elementStore.bycicleGraph();
        properties.setProperty("bicycle", "yes");
        break;

      case "car":
      default:
        graph = elementStore.carGraph();
        properties.setProperty("bicycle", "no");
    }

    ShortestPath<Node, Way> shortestPath = new ShortestPath<Node, Way>(
      graph, from, to, properties
    );

    List<Node> path = shortestPath.path();

    float dist = 0.0f;

    if (ChartController.instance.route != null) {
      ChartController.instance.chart.getChildren().remove(
        ChartController.instance.route
      );
      ChartController.instance.route = null;
    }

    Way route = new Way();
    route.add(path);
    route.tag("meta", "direction");
    ChartController.instance.route = route.render();

    ChartController.instance.chart.getChildren().add(
      ChartController.instance.route
    );
  }

  /**
   * Store all POI nodes in ElementStore.
   * @param parser for parsing data.
   */
  private static void storePoi(final Parser parser) {
    for (Node n : parser.nodes()) {
      if (n.tag("amenity") != null) {
        ChartController.instance.elementStore.add(n);
      }

      if (n.tag("shop") != null) {
        ChartController.instance.elementStore.add(n);
      }
    }
  }

  /**
   * Sets the text of scaleIndicator.
   * @param text the text to be set in scale.
   */
  public static void setScaleText(final String text) {
    ChartController.instance.scaleIndicatorLabel.setText(text);
  }

  /**
   * Sets the length of scaleIndicator.
   * @param length the length in pixels.
   */
  public static void setScaleLength(final double length) {
    double minLength = 50;
    int count = 1;
    double temp = length;

    while (temp < minLength) {
      temp = length * ChartController.findScale(++count);
    }

    int scale = ChartController.findScale(count);

    if (scale >= 1000) {
      ChartController.instance.setScaleText(scale / 1000 + " km");
    }
    else {
      ChartController.instance.setScaleText(scale + " m");
    }

    ChartController.instance.scaleIndicatorLabel.setPrefWidth(temp);
  }

  /**
   * Finds a scale value in the cycle of 1, 2, 5, 10, 20,... that correlates to
   * the count value.
   * @param count the count to correlate to a specific scale.
   * @return the scale that correlates to the count.
   */
  private static int findScale(final int count) {
    int multiplier = (int) Math.ceil(count / 3) + 1;

    switch (count % 3) {
      case 0:   return 1 * (int) (Math.pow(10, multiplier));
      case 1:   return 2 * (int) (Math.pow(10, multiplier));
      case 2:   return 5 * (int) (Math.pow(10, multiplier));
      default:  return 0;
    }
  }

  /**
   * Sets a pointer at the address found.
   * @param x Address with the coordinates for the pointer.
   * @param y Address with the coordinates for the pointer.
   */
  public static void setPointer(final double x, final double y) {
    ChartController.instance.locationPointer.setLayoutX(x);
    ChartController.instance.locationPointer.setLayoutY(y);
    ChartController.instance.locationPointer.setVisible(true);
  }

  /**
   * Sets a pointer at the destination address.
   * @param x Destination address with the coordinates for the pointer.
   * @param y Destination address with the coordinates for the pointer.
   */
  public static void setDistinationPointer(final double x, final double y) {
    ChartController.instance.destinationPointer.setLayoutX(x);
    ChartController.instance.destinationPointer.setLayoutY(y);
    ChartController.instance.destinationPointer.setVisible(true);
  }

  /**
   * Centering chart around and x, y coordinate.
   * @param a the address to center around.
   * @param scale the new scale for the map.
   */
  public static void centerChart(final Address a, final double scale) {
    ChartController.instance.chart.center(a, scale);
  }

  /**
   * Centers chart on two addresses and adjust the scale.
   * @param addr1 the first address.
   * @param addr2 the second address.
   */
  public static void centerChart(final Address addr1, final Address addr2) {
    ChartController.instance.chart.center(addr1, addr2);
  }

  /**
   * Shows points of interest.
   *
   * @param tag tag to show on map.
   */
  public static void showPoi(final String tag) {
    if (ChartController.elementStore != null) {
      ChartController.instance.chart.showSelectedPoi(tag);
    }
  }

  /**
   * Hides points of interest.
   *
   * @param tag to hide on map.
   */
  public static void hidePoi(final String tag) {
    if (ChartController.elementStore != null) {
      ChartController.instance.chart.hidePointsOfInterests(tag);
    }
  }

  /**
   * The the initial mouse coordinates for scrolling.
   *
   * @param e Mouse event for capturing mouse location.
   */
  private void setInitialMouseScroll(final MouseEvent e) {
    this.initialMouseScrollX = e.getSceneX();
    this.initialMouseScrollY = e.getSceneY();
  }

  /**
   * The the initial mouse coordinates for dragging.
   *
   * @param e Mouse event for capturing mouse location.
   */
  private void setInitialMouseDrag(final MouseEvent e) {
    this.initialMouseDragX = e.getSceneX();
    this.initialMouseDragY = e.getSceneY();
  }

  /**
   * On mouse entered event.
   *
   * @param e The mouse event.
   */
  @FXML
  private void onMouseEntered(final MouseEvent e) {
    this.setInitialMouseScroll(e);
  }

  /**
   * On mouse pressed event.
   *
   * @param e The mouse event.
   */
  @FXML
  private void onMousePressed(final MouseEvent e) {
    this.setInitialMouseDrag(e);
    this.setInitialMouseScroll(e);

    this.chart.requestFocus();
  }

  /**
   * On mouse release event.
   *
   * @param e The mouse event.
   */
  @FXML
  private void onMouseReleased(final MouseEvent e) {
    this.setInitialMouseScroll(e);
  }

  /**
   * On mouse moved event.
   *
   * @param e The mouse event.
   */
  @FXML
  private void onMouseMoved(final MouseEvent e) {
    this.setInitialMouseScroll(e);
  }

  /**
   * On mouse clicked event.
   *
   * @param e The mouse event.
   */
  @FXML
  private void onMouseClicked(final MouseEvent e) {
    this.setInitialMouseScroll(e);

    if (e.getClickCount() == 2) {
      this.chart.zoom(Math.pow(ZOOM_IN, 15), e.getSceneX(), e.getSceneY());
    }
  }

  /**
   * On mouse dragged event.
   *
   * @param e The mouse event.
   */
  @FXML
  private void onMouseDragged(final MouseEvent e) {
    double x = e.getSceneX();
    double y = e.getSceneY();

    this.chart.pan(x - this.initialMouseDragX, y - this.initialMouseDragY);

    this.setInitialMouseScroll(e);
    this.setInitialMouseDrag(e);
  }

  /**
   * On scroll event.
   *
   * @param e The scroll event.
   */
  @FXML
  private void onScroll(final ScrollEvent e) {
    double factor = (e.getDeltaY() < 0) ? ZOOM_IN : ZOOM_OUT;

    this.chart.zoom(
      factor,
      this.initialMouseScrollX,
      this.initialMouseScrollY
    );
  }

  /**
   * On zoom event.
   *
   * @param e The zoom event.
   */
  @FXML
  private void onZoom(final ZoomEvent e) {
    this.chart.zoom(
      e.getZoomFactor(),
      this.initialMouseScrollX,
      this.initialMouseScrollY
    );
  }

  /**
   * On key pressed event.
   *
   * @param e The key event.
   */
  @FXML
  private void onKeyPressed(final KeyEvent e) {
    switch (e.getCode()) {
      case UP:
      case K:
      case W:
        this.chart.pan(0, 15);
        e.consume();
        break;
      case DOWN:
      case J:
      case S:
        this.chart.pan(0, -15);
        e.consume();
        break;
      case RIGHT:
      case L:
      case D:
        this.chart.pan(-15, 0);
        e.consume();
        break;
      case LEFT:
      case H:
      case A:
        this.chart.pan(15, 0);
        e.consume();
        break;
      case PLUS:
      case EQUALS:
        this.chart.zoom(Math.pow(ZOOM_IN, 8));
        e.consume();
        break;
      case MINUS:
      case UNDERSCORE:
        this.chart.zoom(Math.pow(ZOOM_OUT, 8));
        e.consume();
        break;
      default:
        return;
    }
  }

  /**
   * Zoom in.
   */
  @FXML
  private void zoomIn() {
    this.chart.zoom(Math.pow(ZOOM_IN, 8));
  }

  /**
   * Zoom out.
   */
  @FXML
  private void zoomOut() {
    this.chart.zoom(Math.pow(ZOOM_OUT, 8));
  }

  /**
   * Clears map by removing all children from layers.
   */
  public static void clearMap() {
    ChartController.instance.chart.clear();
    ChartController.elementStore = null;
  }

  /**
   * Loads a new map, shows loading icon and parse all map elements.
   * @param file the map file to load.
   */
  public static void loadMap(final File file) {
    ApplicationController.addIcon();

    Parser parser = Parser.probe(file);
    ChartController.elementStore = new ElementStore();
    parser.read(file, () -> {
      // Get all addresses from parser.
      for (Address address: parser.addresses()) {
        AddressController.addAddress(address);
      }

      // Sets all POI from initialized nodes.
      ChartController.instance.storePoi(parser);

      Platform.runLater(() -> {
        for (Way l: parser.land()) {
          ChartController.instance.elementStore.addLand(l);
        }
        // Adds land around entire bounds if parser doesn't have any coastlines.
        if (parser.land().isEmpty()) {
          BoundingBox tempBounds = parser.bounds();
          Way defaultLand = new Way();
          defaultLand.add(new Node(tempBounds.minX(), tempBounds.minY()));
          defaultLand.add(new Node(tempBounds.maxX(), tempBounds.minY()));
          defaultLand.add(new Node(tempBounds.maxX(), tempBounds.maxY()));
          defaultLand.add(new Node(tempBounds.minX(), tempBounds.maxY()));
          defaultLand.add(new Node(tempBounds.minX(), tempBounds.minY()));
          defaultLand.tag("land", "yes");
          defaultLand.tag("layer", "-9999");
          ChartController.instance.elementStore.addLand(defaultLand);
        }

        for (Way w: parser.ways()) {
          ChartController.instance.elementStore.add(w);
        }

        for (Relation r: parser.relations()) {
          ChartController.instance.elementStore.add(r);
        }

        ChartController.instance.elementStore.add(parser.bounds());

        ChartController.instance.chart.elementStore(
          ChartController.elementStore
        );
        ChartController.instance.chart.bounds(parser.bounds());

        // Sets the chart active after load.
        ApplicationController.removeIcon();
      });
    });
  }

  /**
   * Gets the element store and returns it.
   * @return the element store.
   */
  public static ElementStore getElementStore() {
    return ChartController.instance.chart.getElementStore();
  }

  /**
   * Gets the current BoundingBox of the chart map.
   * @return BoundingBox of the chart.
   */
  public static BoundingBox getBounds() {
    return ChartController.instance.chart.getBounds();
  }

  /**
   * Sets the elementStore with all elements and sets the bounds.
   * @param elementStore the elementStore to set.
   * @param bounds the BoundingBox to set.
   */
  public static void loadBinMap(
    final ElementStore elementStore,
    final BoundingBox bounds
  ) {
    ChartController.elementStore = elementStore;
    ChartController.instance.chart.elementStore(ChartController.elementStore);
    ChartController.instance.chart.bounds(bounds);
  }
}
