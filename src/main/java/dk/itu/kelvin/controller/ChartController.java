/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.controller;

// General utilities
import java.util.Collections;
import java.util.List;

// I/O utilities
import java.io.BufferedReader;
import java.io.InputStreamReader;

// JavaFX scene utilities
import javafx.scene.CacheHint;
import javafx.scene.Group;

// JavaFX layout
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

// JavaFX shapes
import javafx.scene.shape.Path;

// JavaFX input
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.RotateEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;

// JavaFX transformations
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

// JavaFX collections
import javafx.collections.FXCollections;

// FXML utilities
import javafx.fxml.FXML;

import dk.itu.kelvin.ChartParser;

// Models
import dk.itu.kelvin.model.Chart;
import dk.itu.kelvin.model.Element;

/**
 * Chart controller class.
 *
 * @version 1.0.0
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
   * Maximum zoom factor.
   */
  private static final double MAX_ZOOM_FACTOR = 4;

  /**
   * Minimum zoom factor.
   */
  private static final double MIN_ZOOM_FACTOR = 0.5;

  /**
   * The current zoom factor.
   */
  private double currentZoomFactor = 1;

  /**
   * Affine transformation instance.
   */
  private Affine transform = new Affine();

  /**
   * Affine transformation for chart compass.
   */
  private Affine compassTransform = new Affine();

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
   * The Chart model instance.
   */
  private Chart chart = new Chart();

  /**
   * The Group element to add all the Chart elements to.
   */
  @FXML
  private Group canvas;

  /**
   * The compass arrow.
   */
  @FXML
  private Path compassArrow;

  /**
   * The address typed in.*
   */
  @FXML
  private TextField addressFrom;

  /**
   * The address to navigate to from addressFrom*
   */
  @FXML
  private TextField addressTo;

  /**
   * Initialize the controller.
   *
   * @throws Exception In case of an error. Duh.
   */
  public void initialize() throws Exception {
    this.canvas.setCache(true);
    this.canvas.setCacheHint(CacheHint.QUALITY);

    this.canvas.getTransforms().add(this.transform);

    this.compassArrow.getTransforms().add(this.compassTransform);

    ChartParser parser = new ChartParser(this.chart);
    parser.read(MAP_INPUT);

    // Collections.sort(this.chart.elements());

    // this.canvas.getChildren().addAll(this.chart.nodes());

    this.transform.prependTranslation(
      -this.chart.bounds().getX(),
      -this.chart.bounds().getY()
    );

    // try (
    //   BufferedReader br = new BufferedReader(new InputStreamReader(
    //     this.getClass().getResourceAsStream(MAP_INPUT)
    //   ));
    // ) {
    //   for (String line; (line = br.readLine()) != null;) {
    //     String[] coordinates = line.split(" ");

    //     // this.canvas.node(
    //     //   Double.parseDouble(coordinates[0]),
    //     //   Double.parseDouble(coordinates[1]),
    //     //   Double.parseDouble(coordinates[2]),
    //     //   Double.parseDouble(coordinates[3])
    //     // );
    //   }

    //   this.canvas.getChildren().addAll(this.canvas.nodes());
    // }
  }

  /**
   * Zoom the map.
   *
   * @param factor  The factor with which to zoom.
   * @param x       The x-coordinate of the pivot point.
   * @param y       The y-coordinate of the pivot point.
   */
  private void zoom(final double factor, final double x, final double y) {
    double newZoomFactor = currentZoomFactor * factor;

    if (factor > 1 && newZoomFactor >= MAX_ZOOM_FACTOR) {
      return;
    }

    if (factor < 1 && newZoomFactor <= MIN_ZOOM_FACTOR) {
      return;
    }

    currentZoomFactor *= factor;

    this.transform.prependScale(factor, factor, x, y);
  }

  /**
   * Zoom the map, using the center as the pivot point.
   *
   * @param factor The factor with which to zoom.
   */
  private void zoom(final double factor) {
    this.zoom(
      factor,
      this.canvas.getScene().getWidth() / 2,
      this.canvas.getScene().getHeight() / 2
    );
  }

  /**
   * Pan the map.
   *
   * @param x The amount to pan on the x-axis.
   * @param y The amount to pan on the y-axis.
   */
  private void pan(final double x, final double y) {
    this.transform.prependTranslation(x, y);
  }

  /**
   * Rotate the map.
   *
   * @param angle The angle of the rotation.
   * @param x     The x-coordinate of the pivot point.
   * @param y     The y-coordinate of the pivot point.
   */
  private void rotate(final double angle, final double x, final double y) {
    this.transform.prependRotation(angle, x, y);
    this.compassTransform.prependRotation(angle, 4, 40);
  }

  /**
   * Rotate the map, using the center as the pivot point.
   *
   * @param angle The angle of the rotation.
   */
  private void rotate(final double angle) {
    this.rotate(
      angle,
      this.canvas.getScene().getWidth() / 2,
      this.canvas.getScene().getHeight() / 2
    );
  }

  /**
   * Set the cache to speed-mode.
   */
  private void setCacheSpeed() {
    if (this.canvas.getCacheHint() == CacheHint.SCALE_AND_ROTATE) {
      return;
    }

    this.canvas.setCacheHint(CacheHint.SCALE_AND_ROTATE);
  }

  /**
   * Set the cache to quality-mode.
   */
  private void setCacheQuality() {
    if (this.canvas.getCacheHint() == CacheHint.QUALITY) {
      return;
    }

    this.canvas.setCacheHint(CacheHint.QUALITY);
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

  @FXML
  private void onMouseEntered(final MouseEvent e) {
    this.setInitialMouseScroll(e);
  }

  @FXML
  private void onMousePressed(final MouseEvent e) {
    this.setInitialMouseDrag(e);
    this.setInitialMouseScroll(e);

    this.setCacheSpeed();

    this.canvas.requestFocus();
  }

  @FXML
  private void onMouseReleased(final MouseEvent e) {
    this.setInitialMouseScroll(e);

    this.setCacheQuality();
  }

  @FXML
  private void onMouseMoved(final MouseEvent e) {
    this.setInitialMouseScroll(e);
  }

  @FXML
  private void onMouseClicked(final MouseEvent e) {
    this.setInitialMouseScroll(e);

    if (e.getClickCount() == 2) {
      this.zoom(Math.pow(ZOOM_IN, 15), e.getSceneX(), e.getSceneY());
    }
  }

  @FXML
  private void onMouseDragged(final MouseEvent e) {
    this.setCacheSpeed();

    double x = e.getSceneX();
    double y = e.getSceneY();

    this.pan(x - this.initialMouseDragX, y - this.initialMouseDragY);

    this.setInitialMouseScroll(e);
    this.setInitialMouseDrag(e);
  }

  @FXML
  private void onScrollStarted() {
    this.setCacheSpeed();
  }

  @FXML
  private void onScrollFinished() {
    this.setCacheQuality();
  }

  @FXML
  private void onScroll(final ScrollEvent e) {
    this.setCacheSpeed();

    double factor = (e.getDeltaY() < 0) ? ZOOM_IN : ZOOM_OUT;

    this.zoom(
      factor,
      this.initialMouseScrollX,
      this.initialMouseScrollY
    );
  }

  @FXML
  private void onZoomStarted() {
    this.setCacheSpeed();
  }

  @FXML
  private void onZoomFinished() {
    this.setCacheQuality();
  }

  @FXML
  private void onZoom(final ZoomEvent e) {
    this.setCacheSpeed();

    this.zoom(
      e.getZoomFactor(),
      this.initialMouseScrollX,
      this.initialMouseScrollY
    );
  }

  @FXML
  private void onRotationStarted() {
    this.setCacheSpeed();
  }

  @FXML
  private void onRotationFinished() {
    this.setCacheQuality();
  }

  @FXML
  private void onRotate(final RotateEvent e) {
    this.rotate(
      e.getAngle(),
      this.initialMouseScrollX,
      this.initialMouseScrollY
    );
  }

  @FXML
  private void onKeyPressed(final KeyEvent e) {
    switch (e.getCode()) {
      case UP:
      case K:
        this.pan(0, 15);
        e.consume();
        break;
      case DOWN:
      case J:
        this.pan(0, -15);
        e.consume();
        break;
      case RIGHT:
      case L:
        this.pan(-15, 0);
        e.consume();
        break;
      case LEFT:
      case H:
        this.pan(15, 0);
        e.consume();
        break;
      case PLUS:
      case EQUALS:
        this.zoom(Math.pow(ZOOM_IN, 8));
        e.consume();
        break;
      case MINUS:
      case UNDERSCORE:
        this.zoom(Math.pow(ZOOM_OUT, 8));
        e.consume();
        break;
      default:
        return;
    }
  }

  @FXML
  private void zoomIn() {
    this.zoom(Math.pow(ZOOM_IN, 8));
  }

  @FXML
  private void zoomOut() {
    this.zoom(Math.pow(ZOOM_OUT, 8));
  }

  /**
   * Is activated through input from the user looking for the address.
   * Found in textfield addressFrom.
   */
  @FXML
  private void findAddress() {
    System.out.println(addressFrom.getText());
  }

  /**
   * Takes the input from addressFrom and addressTo.
   */
  @FXML
  private void findRoute() {
    System.out.println(addressTo.getText());
  }
}
