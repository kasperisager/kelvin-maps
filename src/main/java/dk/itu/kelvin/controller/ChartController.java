/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.controller;

// General utilities
import java.util.List;

// Utilities
import dk.itu.kelvin.math.Geometry;
import dk.itu.kelvin.math.Haversine;
import dk.itu.kelvin.math.MercatorProjection;
import dk.itu.kelvin.util.SpatialIndex;
import dk.itu.kelvin.util.WeightedGraph;
import dk.itu.kelvin.util.ShortestPath;

// I/O utilities
import java.io.File;
import java.util.Set;

// JavaFX application utilities
import javafx.application.Platform;

// JavaFX layout
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;

// JavaFX shapes
import javafx.scene.shape.Path;
import javafx.scene.shape.Polyline;

// JavaFX inputs
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.RotateEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;

// JavaFX transformations
import javafx.scene.transform.Affine;

// JavaFX text
import javafx.scene.text.Text;

// JavaFX controls
import javafx.scene.control.Label;

// FXML utilities
import javafx.fxml.FXML;

// Parser
import dk.itu.kelvin.parser.Parser;

// Layout
import dk.itu.kelvin.layout.Chart;

// Models
import dk.itu.kelvin.model.Way;
import dk.itu.kelvin.model.Address;
import dk.itu.kelvin.model.Relation;
import dk.itu.kelvin.model.Node;

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
   * The input file to show in the map viewer.
   */
  private static final String MAP_INPUT = "small.osm";

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
   * Affine transformation for chart compass.
   */
  private Affine compassTransform = new Affine();

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
  private static ElementStore elementStore = new ElementStore();

  /**
   * Way element to represent a route between to addresses.
   */
  private static Way route = null;

  /**
   * Polyline to represent the route.render().
   */
  private static Polyline routeRender = null;

  /**
   * The Canvas element to add all the Chart elements to.
   */
  @FXML
  private Chart chart;

  /**
   * The compass arrow.
   */
  @FXML
  private Path compassArrow;

  /**
   * The VBox surrounding all compass elements.
   */
  @FXML
  private HBox compassVBox;

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
   * Getting ChartsController instance.
   *
   * @return ChartController instance.
   */
  public static ChartController instance() {
    return ChartController.instance;
  }

  /**
   * Initializing the ChartController instance.
   *
   * @param instance the ChartController instance.
   */
  private static void instance(final ChartController instance) {
    ChartController.instance = instance;
  }

  /**
   * Initialize the controller.
   *
   * @throws Exception In case of an error. Duh.
   */
  @FXML
  private void initialize() throws Exception {
    ChartController.instance(this);

    // Sets the parent element inactive until done loading.
    this.mainStackPane.setDisable(true);

    this.compassArrow.getTransforms().add(this.compassTransform);

    this.initLocationPointer();
    this.initDestinationPointer();
    File file = new File(Parser.class.getResource(MAP_INPUT).toURI());

    Parser parser = Parser.probe(file);

    parser.read(file, () -> {
      // Get all addresses from parser.
      for (Address address : parser.addresses()) {
        AddressController.instance().addAddress(address);
      }

      // Sets all POI from initialized nodes.
      this.storePoi(parser);

      // Schedule rendering of the chart nodes.
      Platform.runLater(() -> {
        for (Way l : parser.land()) {
          this.elementStore.addLand(l);
        }

        for (Way w : parser.ways()) {
          this.elementStore.add(w);
        }

        for (Relation r : parser.relations()) {
          this.elementStore.add(r);
        }

        this.elementStore.add(parser.bounds());

        this.chart.elementStore(this.elementStore);
        this.chart.bounds(parser.bounds());

        // Sets the chart active after load.
        this.mainStackPane.setDisable(false);
        ApplicationController.removeIcon();
      });
    });
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
   * Initializing properties for location destination pointer and adding to chart.
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
   * Moves the compass VBox relative to BOTTOM_LEFT.
   *
   * @param x how much to move compass along x-axis [px].
   */
  public static void moveCompass(final double x) {
    ChartController.instance().compassVBox.setTranslateX(x);
  }

  /**
   * Find shortest path between 2 nodes.
   * @param n The from node.
   * @param m The to node.
   * @param type The type of graph initiate.
   */
  public static void findShortestPath(
    final WeightedGraph.Node n,
    final WeightedGraph.Node m,
    final String type) {

    SpatialIndex.Point p1 = new SpatialIndex.Point(n.x(), n.y());
    SpatialIndex.Point p2 = new SpatialIndex.Point(m.x(), m.y());

    Way fromWay =
      elementStore.transportWaysTree().nearest(p1);
    Way toWay =
      elementStore.transportWaysTree().nearest(p2);

    double distanceFrom = 0;
    WeightedGraph.Node from = null;
    for (Node qu : fromWay.nodes()) {
      SpatialIndex.Point ptemp = new SpatialIndex.Point(qu.x(), qu.y());

      if (distanceFrom == 0 || Geometry.distance(ptemp, p1) < distanceFrom) {
        distanceFrom = Geometry.distance(ptemp, p1);
        from = new WeightedGraph.Node(qu.x(), qu.y());
      }
    }

    double distanceTo = 0;
    WeightedGraph.Node to = null;
    for (Node qu : toWay.nodes()) {
      SpatialIndex.Point ptemp = new SpatialIndex.Point(qu.x(), qu.y());

      if (distanceTo == 0 || Geometry.distance(ptemp, p2) < distanceTo) {
        distanceTo = Geometry.distance(ptemp, p2);
        to = new WeightedGraph.Node(qu.x(), qu.y());
      }
    }

    //ChartController.instance().chart.center(new Node(n.x(), n.y()));


    if (from != null && to != null) {
      ShortestPath shortestPath = null;

      if (type.equals("car")) {
        shortestPath = new ShortestPath(elementStore.carGraph(), from);
      } else if (type.equals("bicycle")) {
        shortestPath = new ShortestPath(elementStore.bycicleGraph(), from);
      }

      List<WeightedGraph.Edge> path = shortestPath.path(to);

      float dist = 0.0f;


      WeightedGraph.Edge e1 = null;

      WeightedGraph.Edge e2 = null;


      for (WeightedGraph.Edge e : path) {

        if (e1 != null) {
          e2 = e1;
        }

        e1 = e;

        if (e2 != null) {
/*
          if(//sidste knude er lig destination){
            // print "fortsæt ad vejens forløb til destination" , afstand  = dist
          }

          // kald geometry-klassen og find vinklen mellem 3 punkter:


        if(70 <= angle <= 100){

          // print "fortsæt ad vejens forløb" , afstand  = dist
          // print "drej til højre";
          // dist = 0.0;
        }
        else if(250 <= angle <= 290){
          // drej til venstre
        }
        else{
          // dist = dist +
          //
        }*/


        }

        if (routeRender != null) {
          ChartController.instance().chart.getChildren().remove(routeRender);
          routeRender = null;
        }

        route = new Way();

        for (int i = 0; i < path.size(); i++) {
          if (i == 0) {
            // Add the address from node.
            route.add(new Node(n.x(), n.y()));

          }
          Node n1 = new Node(path.get(i).from().x(), path.get(i).from().y());
          route.add(n1);

          if (i == path.size() - 1) {
            Node n2 = new Node(path.get(i).to().x(), path.get(i).to().y());
            route.add(n2);
            // Add the address to node.
            route.add(new Node(m.x(), m.y()));
          }
        }
        route.tag("meta", "direction");

        routeRender = route.render();


        ChartController.instance().chart.getChildren().add(routeRender);

      }
    }
  }


  public static float edgeLength(WeightedGraph.Node n1, WeightedGraph.Node n2){
    float dist = 0.0f;

    MercatorProjection mer = new MercatorProjection();

    float lat1 = (float) mer.yToLat(n1.y()) * 1000;
    float lon1 = (float) mer.xToLon(n1.x()) * 1000;

    float lat2 = (float) mer.yToLat(n2.y()) * 1000;
    float lon2 = (float) mer.xToLon(n2.x()) * 1000;

    dist = Haversine.distance(lat1, lon1, lat2, lon2);

    return dist;
  }

    /**
   * Store all POI nodes in ElementStore.
   * @param parser for parsing data.
   */
  private static void storePoi(final Parser parser) {
    for (Node n : parser.nodes()) {

      if (n.tag("amenity") != null) {
        ChartController.instance().elementStore.add(n);

      }
      if (n.tag("shop") != null) {
        ChartController.instance().elementStore.add(n);
      }
    }
  }

  /**
   * Will reset the compass, so it points north.
   */
  @FXML
  private void compassReset() {
    //to be continued
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
      temp = length * ChartController.instance.findScale(++count);
    }
    if (ChartController.instance.findScale(count) >= 1000) {
      ChartController.instance.setScaleText(
        ChartController.instance.findScale(count) / 1000 + " km"
      );
    } else {
      ChartController.instance.setScaleText(
        ChartController.instance.findScale(count) + " m"
      );
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
    int scaleCase = count % 3;
    int multiplier = (int) Math.ceil(count / 3) + 1;
    switch (scaleCase) {
      case 0:
        return 1 * (int) (Math.pow(10, multiplier));
      case 1:
        return 2 * (int) (Math.pow(10, multiplier));
      case 2:
        return 5 * (int) (Math.pow(10, multiplier));
      default:
        return 0;
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
    ChartController.instance().chart.showSelectedPoi(tag);
  }

  /**
   * Hides points of interest.
   *
   * @param tag to hide on map.
   */
  public static void hidePoi(final String tag) {
    ChartController.instance().chart.hidePointsOfInterests(tag);
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
   * On rotate event.
   *
   * @param e The rotate event.
   */
  @FXML
  private void onRotate(final RotateEvent e) {
    this.chart.rotate(
      e.getAngle(),
      this.initialMouseScrollX,
      this.initialMouseScrollY
    );
    this.compassTransform.prependRotation(e.getAngle(), 4, 40);
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
      case Q:
        this.chart.rotate(-10);
        this.compassTransform.prependRotation(-10, 4, 40);
        e.consume();
        break;
      case E:
        this.chart.rotate(10);
        this.compassTransform.prependRotation(10, 4, 40);
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
  }

  /**
   * Loads a new map, shows loading icon and parse all map elements.
   * @param file the map file to load.
   */
  public static void loadMap(final File file) {
    ApplicationController.instance().addIcon();
    //ApplicationController.instance().rotateIcon();
    ChartController.instance.mainStackPane.setDisable(true);

    Parser parser = Parser.probe(file);

    parser.read(file, () -> {
      // Get all addresses from parser.
      for (Address address : parser.addresses()) {
        AddressController.instance().addAddress(address);
      }
      // Sets all POI from initialized nodes.
      ChartController.instance().storePoi(parser);

      Platform.runLater(() -> {
        for (Way l : parser.land()) {
          ChartController.instance().elementStore.addLand(l);
        }

        for (Way w : parser.ways()) {
          ChartController.instance().elementStore.add(w);
        }

        for (Relation r : parser.relations()) {
          ChartController.instance().elementStore.add(r);
        }

        ChartController.instance().elementStore.add(parser.bounds());

        ChartController.instance().chart.elementStore(
          ChartController.instance().elementStore);
        ChartController.instance().chart.bounds(parser.bounds());

        // Sets the chart active after load.
        ChartController.instance().mainStackPane.setDisable(false);
        ApplicationController.removeIcon();
      });
    });
  }
}

