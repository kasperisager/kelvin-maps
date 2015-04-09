/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.controller;

// General utilities
import java.util.List;

import dk.itu.kelvin.control.Compass;
import dk.itu.kelvin.util.HashTable;

// I/O utilities
import java.io.File;

// JavaFX utilities
import javafx.util.Duration;

// JavaFX application utilities
import javafx.application.Platform;

// JavaFX scene utilities
import javafx.geometry.Pos;

// JavaFX layout
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.CheckBox;

// JavaFX geometry
import javafx.geometry.Bounds;

// FXML utilities
import javafx.fxml.FXML;

// Controls FX
import org.controlsfx.control.PopOver;

// Parser
import dk.itu.kelvin.parser.Parser;

// Layout
import dk.itu.kelvin.layout.Chart;

// Models
import dk.itu.kelvin.model.Address;

// Stores
import dk.itu.kelvin.store.AddressStore;

/**
 * Chart controller class.
 */
public final class ChartController {
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
   * Don't show auto completion when the entered input contains less that this
   * number of characters.
   */
  private static final int AUTOCOMPLETE_CUTOFF = 2;

  /**
   * Max number of items to show in the auto completion menu.
   */
  private static final int AUTOCOMPLETE_MAX_ITEMS = 5;

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
   * The map from the parser.
   */

  /**
   * PopOver for the config menu.
   */

  /**
   * Auto-complete popover for textfields.
   */

  /**
   * The dynamic autocomplete results.
   */


  private AddressStore addresses = new AddressStore();


  /**
   * Label representing the found address.
   */
  private Text locationPointer;

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
   * The parent element.
   */
  @FXML
  private StackPane stackPane;

  /**
   * Tags for cartographic elements.
   */


  /**
   * Initialize the controller.
   * @throws Exception In case of an error. Duh.
   */
  public void initialize() throws Exception {

    //Compass compass = new Compass();

    //this.stackPane.getChildren().addAll(compass);

    // Sets the parent element inactive until loaded.
    this.stackPane.setDisable(true);


    this.compassArrow.getTransforms().add(this.compassTransform);

    this.locationPointer = new Text();
    this.locationPointer.getStyleClass().add("icon");
    this.locationPointer.getStyleClass().add("address-label");
    this.locationPointer.setText("\uf456");
    this.locationPointer.setVisible(false);
    this.chart.getChildren().add(this.locationPointer);

    File file = new File(Parser.class.getResource(MAP_INPUT).toURI());

    Parser parser = Parser.probe(file);

    parser.read(file, () -> {
      // Get all addresses from parser.
      for (Address address: parser.addresses()) {
        this.addresses.add(address);
      }

      // Schedule rendering of the chart nodes.
      Platform.runLater(() -> {
        this.chart.land(parser.land());
        this.chart.ways(parser.ways());
        this.chart.relations(parser.relations());
        this.chart.bounds(parser.bounds());

        // Sets the chart active after load.
        this.stackPane.setDisable(false);
        ApplicationController.removeIcon();
      });
    });
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
   * Centers the screen on a specific point and set a location marker.
   * @param a the Address to center the screen around and set pointer at.
   */
  private void findAddress(final Address a) {
    this.chart.center(a, 2.5);
    this.setPointer(a);
    /*else {
      // Dialog "The address does not exist."
    }*/
  }

  /**
   * Moves the compass VBox relative to BOTTOM_LEFT.
   * @param x how much to move compass along x-axis [px].
   */
  public void moveCompass(final double x) {
    this.compassVBox.setTranslateX(x);
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
   * Sets the length of the scaleIndicator.
   * @param length how wide the scale is [px].
   */
  private void setScaleLenght(final double length) {
    this.scaleIndicatorLabel.setPrefWidth(length);
  }

  /**
   * Sets a pointer at the address found.
   * @param x Address with the coordinates for the pointer.
   * @param y Address with the coordinates for the pointer.
   */
  public void setPointer(final double x, final double y) {
    this.locationPointer.setLayoutX(x);
    this.locationPointer.setLayoutY(y);
    this.locationPointer.setVisible(true);
  }
}
