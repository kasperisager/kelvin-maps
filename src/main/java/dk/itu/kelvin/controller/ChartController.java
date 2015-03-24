/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.controller;

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
import javafx.scene.control.CheckBox;

// JavaFX geometry
import javafx.geometry.Bounds;

// FXML utilities
import javafx.fxml.FXML;

// Controls FX
import org.controlsfx.control.PopOver;

// Utilities
import java.util.ArrayList;
import java.util.List;

// Parser
import dk.itu.kelvin.parser.ChartParser;

// Threading
import dk.itu.kelvin.thread.TaskQueue;

// Layout
import dk.itu.kelvin.layout.Chart;

// Models
import dk.itu.kelvin.model.Address;
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
   * The addresses map from the parser.
   */
  private AddressStore addresses;

  /**
   * PopOver for the config menu.
   */
  private PopOver popOver;

  /**
   * Auto-complete popover for textfields.
   */
  private PopOver autocPopOver;

  /**
   * The dynamic autocomplete results.
   */
  private List<String> suggestions = new ArrayList<>();

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
   * The poi elements VBox.
   */
  @FXML
  private VBox poiVBox;

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
   * Indicator for scale.
   */
  @FXML
  private Label scaleIndicatorLabel;

  /**
   * GridPane surrounding the big properties boxes
   * containing route description and point of interest.
   */
  @FXML
  private GridPane propertiesGridPane;

  /**
   * The parent element.
   */
  @FXML
  private StackPane stackPane;

  /**
   * Tags for cartographic elements.
   */
  private String[] tags = {"Parking", "Cafe", "Restaurant", "Fast Food",
    "Toilets", "Pub", "Recycling", "Bar", "Compressed Air", "Post Box",
    "Taxi", "BBQ", "Solarium", "Telephone"};

  /**
   * Initialize the controller.
   * @throws Exception In case of an error. Duh.
   */
  public void initialize() throws Exception {
    this.stackPane.setDisable(true);
    this.propertiesGridPane.getChildren().remove(this.checkboxVBox);
    this.propertiesGridPane.getChildren().remove(this.directionsScrollPane);

    this.compassArrow.getTransforms().add(this.compassTransform);

    TaskQueue.run(() -> {
      ChartParser parser = new ChartParser();

      try {
        parser.read(MAP_INPUT);
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }

      /**
       * Results doesn't get sorted
       */
      //Collections.sort(this.chart.elements(), Element.COMPARATOR);

      //Get map of all addresses from parser.
      this.addresses = parser.addresses();

      // Schedule rendering of the chart nodes.
      Platform.runLater(() -> {
        this.chart.add(parser.ways().values());
        this.chart.add(parser.relations().values());

        this.chart.add(parser.land());
        this.chart.add(parser.bounds());
      });

      //Get map of all addresses from parser.
      this.addresses = parser.addresses();
      System.out.println("Ready!");
    });

    this.createPOI();
    this.createPopOver();

    Platform.runLater(() -> this.addressFrom.requestFocus());

    this.autocPopOver = new PopOver();
    this.autocPopOver.setArrowLocation(PopOver.ArrowLocation.TOP_LEFT);
    this.autocPopOver.setCornerRadius(2);
    this.autocPopOver.setArrowSize(0);
    this.autocPopOver.setAutoHide(true);
    this.autocPopOver.setDetachable(false);

    this.setAutoComplete(this.addressFrom);
    this.setAutoComplete(this.addressTo);
  }

  /**
   * sets autocomplete for textfields.
   * @param tf textfield.
   */
  private void setAutoComplete(final TextField tf) {
    tf.setOnKeyReleased((event) -> {
      this.suggestions.clear();

      if (tf.getLength() > AUTOCOMPLETE_CUTOFF) {
        for (Address a: this.addresses.keySet()) {
          if (a.toString().toLowerCase().contains(tf.getText().toLowerCase())) {
            this.suggestions.add(a.toString());
          }

          if (this.suggestions.size() > AUTOCOMPLETE_MAX_ITEMS) {
            break;
          }
        }
      }

      if (this.suggestions.size() <= 0) {
        this.autocPopOver.hide(Duration.ONE);
        return;
      }

      Bounds bounds = tf.localToScreen(tf.getBoundsInParent());

      VBox suggestionsVBox = new VBox(this.suggestions.size());
      suggestionsVBox.setPrefWidth(bounds.getWidth() + 27);

      for (String suggestion: this.suggestions) {
        Button l = new Button(suggestion);

        l.setPrefWidth(bounds.getWidth() + 27);
        l.setOnMouseClicked((event2 -> {
          tf.setText(l.getText());
          this.autocPopOver.hide(Duration.ONE);
        }));

        suggestionsVBox.getChildren().add(l);
      }

      this.autocPopOver.setContentNode(suggestionsVBox);

      if (!this.autocPopOver.isShowing()) {
        this.autocPopOver.show(
          tf,
          bounds.getMinX() + 14, // 14 = font size
          bounds.getMinY() + bounds.getHeight(),
          Duration.ONE
        );
      }
    });

  }
  /**
   * Initialises the checkboxes of for Points Of Interest.
   */
  private void createPOI() {

    for (String s : this.tags) {
      CheckBox cb = new CheckBox(s);
      cb.setPrefWidth(200);

      //add CheckBox event listener and update shown labels

      this.poiVBox.getChildren().add(cb);
    }

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
        this.propertiesGridPane.getChildren().add(this.checkboxVBox);
        this.moveCompass(200);
      } else {
        this.checkboxVBox.setVisible(false);
        this.propertiesGridPane.getChildren().remove(this.checkboxVBox);
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
   * Is activated through input from the user looking for the address.
   * Found in textfield addressFrom.
   */
  @FXML
  private void findAddress() {
    String input = this.addressFrom.getText();

    if (input == null) {
      return;
    }

    input = input.trim();

    if (input.isEmpty()) {
      return;
    }

    Address startAddress = Address.parse(this.addressFrom.getText());

    if (startAddress == null) {
      return;
    }

    Node node = this.addresses.find(startAddress);

    if (node != null) {
      this.chart.center(node, 2.5);
      this.chart.setPointer(node);
    }
  }

  /**
   * Takes the input from addressFrom and addressTo.
   */
  @FXML
  private void findRoute() {
    if (!this.addressFrom.getText().trim().equals("")
      && !this.addressTo.getText().trim().equals("")) {
      Address startAddress = Address.parse(this.addressFrom.getText());
      Address endAddress = Address.parse(this.addressTo.getText());
      Node startPosition = this.addresses.find(startAddress);
      Node endPosition = this.addresses.find(endAddress);

      System.out.println("X: " + startPosition.x() + " " + "Y: "
        + startPosition.y());
      System.out.println("X: " + endPosition.x() + " " + "Y: "
        + endPosition.y());

    }

    this.propertiesGridPane.getChildren().add(this.directionsScrollPane);
    this.moveCompass(400);

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
    this.propertiesGridPane.getChildren().remove(this.directionsScrollPane);
    this.moveCompass(0);
  }

  /**
   * Hides the Points of Interest VBox.
   */
  public void hidePOI() {
    this.checkboxVBox.setVisible(false);
    this.propertiesGridPane.getChildren().remove(this.checkboxVBox);
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
  }

  /**
   * Shows the rout between a and b by foot or bicycle.
   */
  @FXML
  private void routeByFoot() {
    System.out.println("Route by foot");
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
}
