/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.controller;

// General utilities
import java.util.Collections;

// JavaFX application utilities
import javafx.application.Platform;

// JavaFX concurrency utilities
import javafx.concurrent.Task;

// JavaFX scene utilities
import javafx.geometry.Pos;
import javafx.scene.CacheHint;

// JavaFX layout
import javafx.scene.layout.VBox;

// JavaFX shapes
import javafx.scene.shape.Path;

// JavaFX input
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.RotateEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;

// JavaFX transformations
import javafx.scene.transform.Affine;

// JavaFX controls
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TextField;

// Controls FX
import org.controlsfx.control.PopOver;

// FXML utilities
import javafx.fxml.FXML;

// Parser
import dk.itu.kelvin.parser.ChartParser;

// Components
import dk.itu.kelvin.component.Canvas;

// Models
import dk.itu.kelvin.model.Address;
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
   * The Chart model instance.
   */
  private Chart chart = new Chart();

  /**
   * PopOver for the config menu.
   */
  private PopOver popOver;

  /**
   * The Canvas element to add all the Chart elements to.
   */
  @FXML
  private Canvas canvas;

  /**
   * The compass arrow.
   */
  @FXML
  private Path compassArrow;

  /**
   * The address typed in.
   */
  @FXML
  private TextField addressFrom;

  /**
   * The address to navigate to from addressFrom.
   */
  @FXML
  private TextField addressTo;

  /**
   * The config button.
   */
  @FXML
  private ToggleButton toggleButton;

  /**
   * The checkbox VBox element.
   */
  @FXML
  private VBox checkboxVBox;

  /**
   * Initialize the controller.
   *
   * @throws Exception In case of an error. Duh.
   */
  public void initialize() throws Exception {
    this.checkboxVBox.setVisible(false);

    this.compassArrow.getTransforms().add(this.compassTransform);

    Canvas canvas = this.canvas;
    Chart chart = this.chart;

    Task task = new Task<Void>() {
      @Override
      public Void call() {
        ChartParser parser = new ChartParser(chart);

        try {
          parser.read(MAP_INPUT);
        }
        catch (Exception ex) {
          throw new RuntimeException(ex);
        }

        Collections.sort(chart.elements(), Element.Order.COMPARATOR);

        // Schedule rendering of the chart nodes.
        Platform.runLater(() -> {
          // canvas.add(chart.nodes());

          canvas.pan(
            -chart.bounds().getMinX(),
            -chart.bounds().getMaxY()
          );
        });

        return null;
      }
    };

    new Thread(task).start();

    this.createPopOver();
  }

  /**
   * Creates a PopOver object with buttons, eventhandlers and listeners.
   */
  private void createPopOver() {
    VBox vbox = new VBox(2);
    vbox.getStyleClass().add("config-vbox");

    Button blind = new Button("High Contrast");
    Button poi = new Button("Points of Interest");

    blind.setAlignment(Pos.BOTTOM_LEFT);
    poi.setAlignment(Pos.BOTTOM_LEFT);
    blind.getStyleClass().add("config-button");
    poi.getStyleClass().add("config-button");
    blind.setPrefWidth(120);
    poi.setPrefWidth(120);
    vbox.getChildren().addAll(blind, poi);

    blind.setOnAction((event) -> {
      ApplicationController.highContrast();
    });

    poi.setOnAction((event) -> {
      if (!this.checkboxVBox.isVisible()) {
        this.checkboxVBox.setVisible(true);
      }
      else {
        this.checkboxVBox.setVisible(false);
      }
      this.popOver.hide();
    });

    this.popOver = new PopOver();
    this.popOver.setContentNode(vbox);
    this.popOver.setCornerRadius(2);
    this.popOver.setArrowSize(6);
    this.popOver.setArrowLocation(PopOver.ArrowLocation.TOP_LEFT);
    this.popOver.setAutoHide(true);

    this.toggleButton.selectedProperty().addListener((ob, ov, nv) -> {
      if (nv) {
        this.popOver.show(this.toggleButton);
      }
      else {
        this.popOver.hide();
      }
    });

    this.popOver.showingProperty().addListener((ob, ov, nv) -> {
      if (!nv && this.toggleButton.isSelected()) {
        this.toggleButton.setSelected(false);
      }
    });
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

    this.setCacheSpeed();

    this.canvas.requestFocus();
  }

  /**
   * On mouse release event.
   *
   * @param e The mouse event.
   */
  @FXML
  private void onMouseReleased(final MouseEvent e) {
    this.setInitialMouseScroll(e);

    this.setCacheQuality();
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
      this.canvas.zoom(Math.pow(ZOOM_IN, 15), e.getSceneX(), e.getSceneY());
    }
  }

  /**
   * On mouse dragged event.
   *
   * @param e The mouse event.
   */
  @FXML
  private void onMouseDragged(final MouseEvent e) {
    this.setCacheSpeed();

    double x = e.getSceneX();
    double y = e.getSceneY();

    this.canvas.pan(x - this.initialMouseDragX, y - this.initialMouseDragY);

    this.setInitialMouseScroll(e);
    this.setInitialMouseDrag(e);
  }

  /**
   * On scroll started event.
   */
  @FXML
  private void onScrollStarted() {
    this.setCacheSpeed();
  }

  /**
   * On scroll finished event.
   */
  @FXML
  private void onScrollFinished() {
    this.setCacheQuality();
  }

  /**
   * On scroll event.
   *
   * @param e The scroll event.
   */
  @FXML
  private void onScroll(final ScrollEvent e) {
    this.setCacheSpeed();

    double factor = (e.getDeltaY() < 0) ? ZOOM_IN : ZOOM_OUT;

    this.canvas.zoom(
      factor,
      this.initialMouseScrollX,
      this.initialMouseScrollY
    );
  }

  /**
   * On zoom started event.
   */
  @FXML
  private void onZoomStarted() {
    this.setCacheSpeed();
  }

  /**
   * On zoom finished event.
   */
  @FXML
  private void onZoomFinished() {
    this.setCacheQuality();
  }

  /**
   * On zoom event.
   *
   * @param e The zoom event.
   */
  @FXML
  private void onZoom(final ZoomEvent e) {
    this.setCacheSpeed();

    this.canvas.zoom(
      e.getZoomFactor(),
      this.initialMouseScrollX,
      this.initialMouseScrollY
    );
  }

  /**
   * On rotation started event.
   */
  @FXML
  private void onRotationStarted() {
    this.setCacheSpeed();
  }

  /**
   * On rotation finished event.
   */
  @FXML
  private void onRotationFinished() {
    this.setCacheQuality();
  }

  /**
   * On rotate event.
   *
   * @param e The rotate event.
   */
  @FXML
  private void onRotate(final RotateEvent e) {
    this.canvas.rotate(
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
        this.canvas.pan(0, 15);
        e.consume();
        break;
      case DOWN:
      case J:
      case S:
        this.canvas.pan(0, -15);
        e.consume();
        break;
      case RIGHT:
      case L:
      case D:
        this.canvas.pan(-15, 0);
        e.consume();
        break;
      case LEFT:
      case H:
      case A:
        this.canvas.pan(15, 0);
        e.consume();
        break;
      case PLUS:
      case EQUALS:
        this.canvas.zoom(Math.pow(ZOOM_IN, 8));
        e.consume();
        break;
      case MINUS:
      case UNDERSCORE:
        this.canvas.zoom(Math.pow(ZOOM_OUT, 8));
        e.consume();
        break;
      case Q:
        this.canvas.rotate(-10);
        this.compassTransform.prependRotation(-10, 4, 40);
        e.consume();
        break;
      case E:
        this.canvas.rotate(10);
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
    this.canvas.zoom(Math.pow(ZOOM_IN, 8));
  }

  /**
   * Zoom out.
   */
  @FXML
  private void zoomOut() {
    this.canvas.zoom(Math.pow(ZOOM_OUT, 8));
  }

  /**
   * Is activated through input from the user looking for the address.
   * Found in textfield addressFrom.
   */
  @FXML
  private void findAddress() {
    Address startAddress = Address.parse(this.addressFrom.getText());
    System.out.println(startAddress);
  }

  /**
   * Takes the input from addressFrom and addressTo.
   */
  @FXML
  private void findRoute() {
    Address startAddress = Address.parse(this.addressFrom.getText());
    Address endAddress = Address.parse(this.addressTo.getText());
    System.out.println(startAddress);
    System.out.println(endAddress);
  }

  /**
   * Will reset the compass, so it points north.
   */
  @FXML
  private void compassReset() {
    //to be continued
  }

  /**
   * Swap the text of the from and to address inputs.
   */
  @FXML
  private void swapTextFields() {
    String from = this.addressFrom.getText();
    String to = this.addressTo.getText();
    this.addressFrom.setText(to);
    this.addressTo.setText(from);
  }

  /**
   * Shows the route between a and b by car.
   */
  @FXML
  private void routeByCar() {
    System.out.println("Route by car");
  }

  /**
   * Shows the rout between a and b by foot or bicycle.
   */
  @FXML
  private void routeByFoot() {
    System.out.println("Route by foot");
  }
}
