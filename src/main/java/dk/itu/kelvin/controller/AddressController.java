package dk.itu.kelvin.controller;

import dk.itu.kelvin.model.Address;

import dk.itu.kelvin.util.HashTable;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;

import java.util.List;

/**
 * AddressField controller.
 */
public class AddressController {

  /**
   * The AddressController instance.
   */
  private static AddressController instance;

  /**
   * The number of characters needed before address fields will show
   * autocomplete suggestions.
   */
  private static final int AUTOCOMPLETE_CUTOFF = 2;

  /**
   * The maximum number of addresses to suggest in address fields.
   */
  private static final int AUTOCOMPLETE_MAX_ITEMS = 5;

  /**
   * Tags to show in Points of interest.
   */
  private String[] tags = {"Parking", "Cafe", "Restaurant", "Fast Food",
    "Toilets", "Pub", "Recycling", "Bar", "Compressed Air", "Post Box",
    "Taxi", "BBQ", "Solarium", "Telephone"};

  /**
   * The main FXML element for AddressController.
   */
  @FXML
  private VBox mainAddressFieldVBox;

  /**
   * First text field for the address to find or start location for finding
   * route.
   */
  @FXML
  private TextField findAddressTextField;

  /**
   * Second text field for the address to navigate to.
   */
  @FXML
  private TextField findRouteTextField;

  /**
   * The toggle button for showing more settings.
   */
  @FXML
  private ToggleButton settingsToggleButton;

  /**
   * The grid pane that contains boxes for additional settings.
   * Pane stretches from text fields to bottom of screen.
   */
  @FXML
  private GridPane propertiesGridPane;

  /**
   * VBox container for Points Of Interest.
   */
  @FXML
  private VBox poiContainer;

  /**
   * VBox for the content of Points Of Interest.
   */
  @FXML
  private VBox poiContentVBox;

  /**
   * VBox container for route description.
   */
  @FXML
  private VBox directionsContainer;

  /**
   * VBox for the content of route description.
   */
  @FXML
  private VBox directionsContentVBox;

  /**
   * PopOver for toggle button settings.
   */
  private PopOver settingsPopOver;

  /**
   * PopOver for address suggestions.
   */
  private PopOver autoCompletePopOver;

  /**
   * VBox for the content of autoCompletePopOver.
   */
  private VBox autoCompletePopOverVBox;

  /**
   * HashTable for suggestions.
   */
  private HashTable<String, Address> autoCompleteSuggestions;

  /**
   * Pointer for which address suggestion to highlight.
   */
  private int pointer = 0;

  /**
   * Getting AddressController instance.
   * @return AddressController instance.
   */
  public static AddressController instance() {
    return AddressController.instance;
  }

  /**
   * Initializing the AddressController instance.
   * @param instance the AddressController instance.
   */
  private static void instance(final AddressController instance) {
    AddressController.instance = instance;
  }

  /**
   * Initialize the address controller.
   */
  public void initialize() {
    AddressController.instance(this);

    this.autoCompleteSuggestions = new HashTable<>(AUTOCOMPLETE_MAX_ITEMS);

    this.propertiesGridPane.getChildren().remove(this.poiContainer);
    this.propertiesGridPane.getChildren().remove(this.directionsContainer);

    this.createSettingsPopOver();
    this.initPoiBox();

    this.initAutoCompletePopOver();
    this.setAutoComplete(this.findAddressTextField);
    this.setAutoComplete(this.findRouteTextField);
  }

  /**
   * Initializing of event handler for settingsToggleButton and content of
   * settingsPopOver.
   */
  private void createSettingsPopOver() {
    VBox vbox = new VBox(2);

    Button hContrast = new Button("High Contrast");
    Button poi = new Button("Points of Interest");

    hContrast.setPrefWidth(140);
    poi.setPrefWidth(140);
    vbox.getChildren().addAll(hContrast, poi);

    hContrast.setOnAction((event) -> {
      ApplicationController.highContrast();
    });

    poi.setOnAction((event) -> {
      if (!this.propertiesGridPane.getChildren().contains(this.poiContainer)) {
        this.propertiesGridPane.getChildren().add(this.poiContainer);
        //this.moveCompass(200);
      } else {
        this.propertiesGridPane.getChildren().remove(this.poiContainer);
        //this.moveCompass(0);
      }
      this.settingsPopOver.hide();
    });

    this.settingsPopOver = new PopOver();
    this.settingsPopOver.setContentNode(vbox);
    this.settingsPopOver.setCornerRadius(2);
    this.settingsPopOver.setArrowSize(6);
    this.settingsPopOver.setArrowLocation(PopOver.ArrowLocation.TOP_LEFT);
    this.settingsPopOver.setAutoHide(true);

    this.settingsToggleButton.selectedProperty().addListener((ob, ov, nv) -> {
      if (nv) {
        this.settingsPopOver.show(this.settingsToggleButton);
      }
      else {
        this.settingsPopOver.hide();
      }
    });

    this.settingsPopOver.showingProperty().addListener((ob, ov, nv) -> {
      if (!nv && this.settingsToggleButton.isSelected()) {
        this.settingsToggleButton.setSelected(false);
      }
    });
  }

  /**
   * Initializing Points Of Interest container and content of poiContentVBox.
   */
  private void initPoiBox() {
    for (String s : this.tags) {
      CheckBox cb = new CheckBox(s);
      cb.setPrefWidth(200);

      //add CheckBox event listener and update shown labels

      this.poiContentVBox.getChildren().add(cb);
    }
  }

  /**
   * Initializing address suggestions PopOver.
   */
  private void initAutoCompletePopOver() {
    this.autoCompletePopOver = new PopOver();
    this.autoCompletePopOver.setArrowLocation(PopOver.ArrowLocation.TOP_LEFT);
    this.autoCompletePopOver.setCornerRadius(2);
    this.autoCompletePopOver.setArrowSize(0);
    this.autoCompletePopOver.setAutoHide(true);
    this.autoCompletePopOver.setDetachable(false);
  }

  /**
   * Initializing event handler for text fields and content of
   * autoCompletePopOver.
   * @param tf the text field get autoCompletePopOver.
   */
  private void setAutoComplete(final TextField tf) {
    tf.textProperty().addListener((e) -> {
      this.autoCompleteSuggestions.clear();

      // If the input in the textfield is above
      // The autocomplete_cutoff then add strings to the suggestions arraylist.
      if (tf.getLength() > AUTOCOMPLETE_CUTOFF) {
        List<Address> results =
          ChartController.instance().addresses.search(tf.getText());

        for (Address a : results) {
          this.autoCompleteSuggestions.put(
            a.street()
              + " " + a.number()
              + ", " + a.postcode()
              + " " + a.city(), a
          );

          // End the foreach loop
          // if AutoComplete_max_items limit has been reached.
          if (this.autoCompleteSuggestions.size() > AUTOCOMPLETE_MAX_ITEMS) {
            break;
          }
        }
      }

      // Hide the popover if there are no suggestions.
      if (this.autoCompleteSuggestions.size() <= 0) {
        this.autoCompletePopOver.hide(Duration.ONE);
        return;
      }

      Bounds bounds = tf.localToScreen(tf.getBoundsInParent());

      this.autoCompletePopOverVBox =
        new VBox(this.autoCompleteSuggestions.size());

      this.autoCompletePopOverVBox.setPrefWidth(bounds.getWidth() + 27);

      // Creates and adds buttons to the VBox.
      for (String suggestion : this.autoCompleteSuggestions.keySet()) {
        Button b = new Button(suggestion);
        b.setPrefWidth(bounds.getWidth() + 27);

        b.setOnMouseClicked((e2 -> {
          tf.setText(b.getText());

          this.autoCompletePopOver.hide(Duration.ONE);

          if (tf.getId().equals("addressFrom")) {
            this.findAddress(this.autoCompleteSuggestions.get(suggestion));
          }
          else if (tf.getId().equals("addressTo")) {
            this.findRoute();
          }
        }));

        this.autoCompletePopOverVBox.getChildren().add(b);
      }

      // The suggestion highlight pointer.
      this.pointer = 0;

      // Highlights the first suggestion as default.
      this.addHighlightStyle();

      // Removes the current highlight on mouse enter.
      this.autoCompletePopOverVBox.setOnMouseEntered((e4 -> {
        this.removeHighlightStyle();
      }));

      // Adds highlight again on mouse exit.
      this.autoCompletePopOverVBox.setOnMouseExited((e4 -> {
        this.addHighlightStyle();
      }));

      // Adds the VBox to the popover.
      this.autoCompletePopOver.setContentNode(this.autoCompletePopOverVBox);

      // Makes the popover visible.
      if (!this.autoCompletePopOver.isShowing()) {
        this.autoCompletePopOver.show(
          tf,
          bounds.getMinX() + 14, // 14 = font size
          bounds.getMinY() + bounds.getHeight(),
          Duration.ONE
        );
      }
    });

    // Updating the highlight.
    tf.setOnKeyReleased((event -> {
      if (this.autoCompleteSuggestions.size() > 0) {
        this.autoCompletePopOverVBox.setOnKeyPressed((e2) -> {
          // Removes the current highlight.
          this.removeHighlightStyle();

          // Moves the pointer and thereby the highlight.
          this.movePointer(e2);

          // Adds a new highlight.
          this.addHighlightStyle();
        });
      }
    }));
  }

  /**
   * Adds the highlight style to selected address suggestion.
   */
  private void addHighlightStyle() {
    Button b = (Button) this.autoCompletePopOverVBox.getChildren().
      get(this.pointer);
    b.getStyleClass().add("highlight");
  }

  /**
   * Remove the highlight style to selected address suggestion.
   */
  private void removeHighlightStyle() {
    Button b = (Button) this.autoCompletePopOverVBox.getChildren().
      get(this.pointer);
    b.getStyleClass().remove("highlight");
  }

  /**
   * Moves the pointer based on input from arrow keys.
   * @param e the key event.
   */
  private void movePointer(final KeyEvent e) {
    switch (e.getCode()) {
      case UP:
        if (this.pointer > 0) {
          this.pointer--;
        }
        e.consume();
        break;
      case DOWN:
        if (this.pointer < this.autoCompleteSuggestions.size() - 1) {
          this.pointer++;
        }
        e.consume();
        break;
      default:
        break;
    }
  }

  /**
   * Gets a address from the address suggestions PopOver and sets text.
   */
  @FXML
  public void findAddress() {
    if (this.autoCompletePopOver.isShowing()) {
      Button b = (Button) this.autoCompletePopOverVBox.getChildren().
        get(this.pointer);
      this.findAddress(this.autoCompleteSuggestions.get(b.getText()));

      this.findAddressTextField.setText(b.getText());
      this.autoCompletePopOver.hide();
    }
  }

  /**
   * Centers chart around specific point and sets location pointer.
   * @param address the address to find.
   */
  public void findAddress(final Address address) {
    ChartController.centerChart(address.x(), address.y(), 2.5);
    ChartController.setPointer(address.x(), address.y());
  }

  /**
   * Finds the route between two specific addresses from the text fields.
   */
  @FXML
  public void findRoute() {
    if (this.autoCompletePopOver.isShowing()) {
      Button b = (Button) this.autoCompletePopOverVBox.getChildren().
        get(this.pointer);
      String endInput = b.getText();
      this.findRouteTextField.setText(b.getText());

      String startInput = this.findAddressTextField.getText();

      if (endInput == null || startInput == null) {
        return;
      }

      endInput = endInput.trim();
      startInput = startInput.trim();

      if (endInput.isEmpty() || startInput.isEmpty()) {
        return;
      }

      Address startAddress = Address.parse(startInput);
      Address endAddress = Address.parse(endInput);

      if (endAddress == null || startAddress == null) {
        return;
      }

      // showRouteOnMap(startAddress, endAddress);

      System.out.println("X: " + startAddress.x() + " " + "Y: "
        + startAddress.y());
      System.out.println("X: " + endAddress.x() + " " + "Y: "
        + endAddress.y());

      this.autoCompletePopOver.hide();
    }
    /*else {
      // Dialog "The address does not exist."
    }*/

    this.showDirections();

    // Initialize placeholder text
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
      this.directionsContentVBox.getChildren().add(hbox);
    }
  }

  /**
   * Swaps the two address fields around.
   */
  @FXML
  public void swapTextFields() {
    String from = this.findAddressTextField.getText();
    this.findAddressTextField.setText(this.findAddressTextField.getText());
    this.findRouteTextField.setText(from);
    if (this.autoCompletePopOver.isShowing()) {
      this.autoCompletePopOver.hide(new Duration(0));
    }
  }

  /**
   * Finds route specific only to cars.
   */
  @FXML
  public void routeByCar() {

  }

  /**
   * Find route specific only to bikes.
   */
  @FXML
  public void routeByFoot() {

  }

  /**
   * Removes the Points Of Interest container from the window.
   */
  @FXML
  public void hidePOI() {
    this.propertiesGridPane.getChildren().remove(this.poiContainer);
  }

  /**
   * Removes the route description container from the window.
   */
  @FXML
  public void hideDirections() {
    this.propertiesGridPane.getChildren().remove(this.directionsContainer);
  }

  /**
   * Shows the Points Of Interest container in the window.
   */
  public void showPOI() {
    if (!this.propertiesGridPane.getChildren().contains(this.poiContainer)) {
      this.propertiesGridPane.getChildren().add(this.poiContainer);
    }
  }

  /**
   * Shows the route description container in the window.
   */
  public void showDirections() {
    if (!this.propertiesGridPane.getChildren().
      contains(this.directionsContainer)) {
      this.propertiesGridPane.getChildren().add(this.directionsContainer);
    }
  }
}
