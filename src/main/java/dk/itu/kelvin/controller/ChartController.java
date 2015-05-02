/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.controller;

// I/O utilities
import java.io.File;

// JavaFX application utilities
import javafx.application.Platform;

// JavaFX layout
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;

// JavaFX shapes
import javafx.scene.shape.Path;

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
import dk.itu.kelvin.model.Address;
import dk.itu.kelvin.model.Way;
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
   * Element store storing all elements.
   */
  private static ElementStore elementStore = new ElementStore();

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
   * @return ChartController instance.
   */
  public static ChartController instance() {
    return ChartController.instance;
  }

  /**
   * Initializing the ChartController instance.
   * @param instance the ChartController instance.
   */
  private static void instance(final ChartController instance) {
    ChartController.instance = instance;
  }

  /**
   * Initialize the controller.
   * @throws Exception In case of an error. Duh.
   */
  @FXML
  private void initialize() throws Exception {
    ChartController.instance(this);

    // Sets the parent element inactive until done loading.
    this.mainStackPane.setDisable(true);

    this.compassArrow.getTransforms().add(this.compassTransform);

    this.initLocationPointer();
    File file = new File(Parser.class.getResource(MAP_INPUT).toURI());

    Parser parser = Parser.probe(file);

    parser.read(file, () -> {
      // Get all addresses from parser.
      for (Address address: parser.addresses()) {
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
   * Moves the compass VBox relative to BOTTOM_LEFT.
   * @param x how much to move compass along x-axis [px].
   */
  public static void moveCompass(final double x) {
    ChartController.instance().compassVBox.setTranslateX(x);
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
  private void setScaleText(final String text) {
    this.scaleIndicatorLabel.setText(text);
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
   * Centering chart around and x, y coordinate.
   * @param a the address to center around.
   * @param scale the new scale for the map.
   */
  public static void centerChart(final Address a, final double scale) {
    ChartController.instance.chart.center(a, scale);
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
      for (Address address: parser.addresses()) {
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

  /**
   * Gets the element store and returns it.
   * @return the element store.
   */
  public static ElementStore getElementStore(){
    return ChartController.instance.elementStore;
  }

  public static void setElementStore(ElementStore elementStore){
    ChartController.instance.elementStore = elementStore;
  }
}

