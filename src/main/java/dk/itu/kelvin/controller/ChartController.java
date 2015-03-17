/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.controller;

// JavaFX application utilities
import javafx.application.Platform;

// JavaFX scene utilities
import javafx.geometry.Pos;

// JavaFX layout
import javafx.scene.layout.HBox;
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
import javafx.scene.control.Label;

// Controls FX
import org.controlsfx.control.PopOver;

// FXML utilities
import javafx.fxml.FXML;

// Parser
import dk.itu.kelvin.parser.ChartParser;

// Threading
import dk.itu.kelvin.thread.TaskQueue;

// Layout
import dk.itu.kelvin.layout.Canvas;

// Models
import dk.itu.kelvin.model.Address;
import dk.itu.kelvin.model.Chart;
import dk.itu.kelvin.model.Node;

// Stores
import dk.itu.kelvin.store.AddressStore;

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
   * The addresses map from the parser.
   */
  private AddressStore addresses;

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
   * The VBox containing route description.
   */
  @FXML
  private VBox directionsVBox;

  /**
   * The VBox containing a ScrollPane.
   */
  @FXML
  private VBox directionsScrollPane;

  /**
   * The VBox surrounding all compass elements.
   */
  @FXML
  private HBox compassVBox;

  /**
   * Indicator for scale
   */
  @FXML
  private Label scaleIndicatorLabel;

  /**
   * Initialize the controller.
   *
   * @throws Exception In case of an error. Duh.
   */
  public void initialize() throws Exception {
    this.checkboxVBox.setVisible(false);

    this.compassArrow.getTransforms().add(this.compassTransform);

    TaskQueue.run(() -> {
      ChartParser parser = new ChartParser(this.chart);

      try {
        parser.read(MAP_INPUT);
      }
      catch (Exception ex) {
        throw new RuntimeException(ex);
      }

      // Collections.sort(this.chart.elements(), Element.COMPARATOR);

      //Get map of all addresses from parser.
      this.addresses = parser.addresses();

      // Schedule rendering of the chart nodes.
      Platform.runLater(() -> {
        this.canvas.add(this.chart.elements());

        this.canvas.pan(
          -this.chart.bounds().getMinX(),
          -this.chart.bounds().getMinY()
        );
      });
    });

    this.createPopOver();

    Platform.runLater(() -> this.addressFrom.requestFocus());
  }

  /**
   * Creates a PopOver object with buttons, eventhandlers and listeners.
   */
  private void createPopOver() {
    VBox vbox = new VBox(2);

    Button blind = new Button("High Contrast");
    Button poi = new Button("Points of Interest");

    blind.setPrefWidth(140);
    poi.setPrefWidth(140);
    vbox.getChildren().addAll(blind, poi);

    blind.setOnAction((event) -> {
      ApplicationController.highContrast();
    });

    poi.setOnAction((event) -> {
      if (!this.checkboxVBox.isVisible()) {
        this.checkboxVBox.setVisible(true);
        this.moveCompass(200);
      }
      else {
        this.checkboxVBox.setVisible(false);
        this.moveCompass(0);
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
    double x = e.getSceneX();
    double y = e.getSceneY();

    this.canvas.pan(x - this.initialMouseDragX, y - this.initialMouseDragY);

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

    this.canvas.zoom(
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
    this.canvas.zoom(
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
    Node position = this.addresses.find(startAddress);
    // centerView(position.x(), position.y());

    System.out.println("X: " + position.x() + " " + "Y: " + position.y());
  }

  /**
   * Takes the input from addressFrom and addressTo.
   */
  @FXML
  private void findRoute() {
    /*
    Address startAddress = Address.parse(this.addressFrom.getText());
    Address endAddress = Address.parse(this.addressTo.getText());
    Node startPosition = this.addresses.find(startAddress);
    Node endPosition = this.addresses.find(endAddress);

    System.out.println("X: " + startPosition.x() + " " + "Y: "
      + startPosition.y());
    System.out.println("X: " + endPosition.x() + " " + "Y: "
      + endPosition.y());
    */
    this.moveCompass(400);
    this.directionsScrollPane.setVisible(true);
    int stack = 30;
    for (int i = 0; i < stack; i++) {
      HBox hbox = new HBox(2);
      hbox.getStyleClass().add("bottomBorder");
      hbox.setPrefWidth(500);
      Label icon = new Label("\uf10c");
      icon.getStyleClass().add("icon");
      icon.setPrefWidth(40);
      icon.setAlignment(Pos.CENTER);

      Label label = new Label("Turn right at next left");

      hbox.getChildren().addAll(icon, label);
      this.directionsVBox.getChildren().add(hbox);

    }
  }

  /**
   * Hides the route description VBox.
   */
  public void hideDirections() {
    this.directionsScrollPane.setVisible(false);
    this.moveCompass(0);
  }

  /**
   * Hides the Points of Interest VBox.
   */
  public void hidePOI() {
    this.checkboxVBox.setVisible(false);
    this.moveCompass(0);
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
    setScaleText("100 km");
    setScaleLenght(300);
  }

  /**
   * Shows the rout between a and b by foot or bicycle.
   */
  @FXML
  private void routeByFoot() {
    System.out.println("Route by foot");
  }

  private void setScaleText(String text) {
    this.scaleIndicatorLabel.setText(text);
  }
  private void setScaleLenght(double lenght) {
    this.scaleIndicatorLabel.setPrefWidth(lenght);
  }
}
