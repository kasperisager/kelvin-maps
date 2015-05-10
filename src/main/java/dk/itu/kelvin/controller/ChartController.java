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

// Parser
import dk.itu.kelvin.parser.Parser;

// Layout
import dk.itu.kelvin.layout.Chart;

// Models
import dk.itu.kelvin.model.Address;
import dk.itu.kelvin.model.Way;
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
   * Element store storing all elements.
   */
  private static ElementStore elementStore = new ElementStore();

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
   * @throws Exception In case of an error. Duh.
   */
  @FXML
  private void initialize() throws Exception {
    ChartController.instance = this;

    this.initLocationPointer();
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
   * Moves the scale VBox relative to BOTTOM_LEFT.
   * @param x how much to move scale indicator along x-axis [px].
   */
  public static void moveScale(final double x) {
    ChartController.instance.scaleVBox.setTranslateX(x);
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
    ChartController.instance.chart.showSelectedPoi(tag);
  }

  /**
   * Hides points of interest.
   *
   * @param tag to hide on map.
   */
  public static void hidePoi(final String tag) {
    ChartController.instance.chart.hidePointsOfInterests(tag);
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
    ChartController.elementStore = new ElementStore();
  }

  /**
   * Loads a new map, shows loading icon and parse all map elements.
   * @param file the map file to load.
   */
  public static void loadMap(final File file) {
    ApplicationController.addIcon();

    Parser parser = Parser.probe(file);

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

        for (Way w: parser.ways()) {
          ChartController.instance.elementStore.add(w);
        }

        for (Relation r: parser.relations()) {
          ChartController.instance.elementStore.add(r);
        }

        ChartController.instance.elementStore.add(parser.bounds());

        ChartController.instance.chart.elementStore(
          ChartController.elementStore);
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
    ChartController.instance.chart.elementStore(elementStore);
    ChartController.instance.chart.bounds(bounds);
  }

}

