/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.controller;

// General Utilities
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

// Kelvin Model
import dk.itu.kelvin.model.Address;

// Kelvin Store
import dk.itu.kelvin.store.AddressStore;

// JavaFX Beans
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

// JavaFX Geometry
import javafx.geometry.Bounds;
import javafx.geometry.Pos;

// JavaFX Control
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

// JavaFX Input
import javafx.scene.input.KeyEvent;

// JavaFX Layout
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

// JavaFX Utilities
import javafx.util.Duration;

// JavaFX FXML
import javafx.fxml.FXML;

// ControlsFX
import org.controlsfx.control.PopOver;

// Koloboke collections
import net.openhft.koloboke.collect.map.hash.HashObjObjMaps;

/**
 * AddressField controller.
 */
public final class AddressController {

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
   * Store for all address objects.
   */
  private static AddressStore addresses = new AddressStore();

  /**
   * The main FXML element for AddressController.
   */
  @FXML
  private VBox mainVBox;

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
  private static PopOver settingsPopOver;

  /**
   * PopOver for address suggestions.
   */
  private static PopOver autoCompletePopOver;

  /**
   * VBox for the content of autoCompletePopOver.
   */
  private static VBox autoCompletePopOverVBox;

  /**
   * Map for full suggestions.
   */
  private static Map<String, Address> autoCompleteSuggestions;

  /**
   * Pointer for which address suggestion to highlight.
   */
  private static int pointer = 0;

  /**
   * The location the user is at, found in findAddressTextField.
   */
  private static Address currentAddress;

  /**
   * The location the user tries to navigate to, found in findRouteTextField.
   */
  private static Address destinationAddress;

  /**
   * Address object.
   */
  private static Address address;

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
  @FXML
  private void initialize() {
    AddressController.instance(this);

    this.autoCompleteSuggestions = new LinkedHashMap<>(
      AUTOCOMPLETE_MAX_ITEMS
    );

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
        this.showPOI();
      } else {
        this.hidePOI();
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
    Map<String, String> filter = HashObjObjMaps.newMutableMap();
    filter.put("bank", "Bank");
    filter.put("toilets", "Toilets");
    filter.put("cafe", "Cafe");
    filter.put("pub", "Pub");
    filter.put("supermarket", "Supermarket");
    filter.put("compressed_air", "Compressed Air");
    filter.put("post_box", "Post Box");
    filter.put("taxi", "Taxi");
    filter.put("fast_food", "Fast Food");
    filter.put("telephone", "Telephone");
    filter.put("solarium", "Solarium");
    filter.put("recycling", "Recycling");
    filter.put("restaurant", "Restaurant");

    for (String s : filter.keySet()) {
      CheckBox cb = new CheckBox(filter.get(s));
      cb.setPrefWidth(200);

      cb.selectedProperty().addListener((ob, ov, nv) -> {
        if (nv) {
          ChartController.instance().showPoi(s);
          //this.showPointsOfInterests(s);
        } else {
          ChartController.instance().hidePoi(s);
          //this.hidePointsOfInterests(s);
        }

      });
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
    tf.textProperty().addListener(this.addressFieldListener);

    // Updating the highlight.
    tf.setOnKeyReleased((event -> {
      if (autoCompleteSuggestions.size() > 0) {
        autoCompletePopOverVBox.setOnKeyPressed((e2) -> {
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
  private void findAddress() {
    if (this.autoCompletePopOver.isShowing()) {
      Button b = (Button) this.autoCompletePopOverVBox.getChildren().
        get(this.pointer);
      AddressController.address =
        Address.parse(this.findAddressTextField.getText());

      if (AddressController.address.number() == null) {
        insertStreet(this.findAddressTextField, b.getText());
      } else {
        insertAddress(this.findAddressTextField, b.getText());
        this.currentAddress = this.autoCompleteSuggestions.get(b.getText());
        this.findAddress(this.currentAddress);
      }
    }
  }

  /**
   * Centers chart around specific point and sets location pointer.
   * @param address the address to find.
   */
  private void findAddress(final Address address) {
    //calling center 3 times to ensure correct centering, until bug is fixed.
    ChartController.centerChart(address, 2.5);
    ChartController.centerChart(address, 2.5);
    ChartController.setPointer(address.x(), address.y());
  }

  /**
   * Finds the destination address from the autoCompletePopOver.
   */
  @FXML
  private void findRoute() {
    if (this.autoCompletePopOver.isShowing()) {
      Button b = (Button) this.autoCompletePopOverVBox.getChildren().
        get(this.pointer);
      AddressController.address =
        Address.parse(this.findRouteTextField.getText());

      if (AddressController.address.number() == null) {
        insertStreet(this.findRouteTextField, b.getText());
      } else {
        insertAddress(this.findRouteTextField, b.getText());
        this.destinationAddress = this.autoCompleteSuggestions.get(b.getText());
        this.findRoute(this.destinationAddress);
      }
    }
  }

  /**
   * Finds the route between two addresses.
   * @param address the destination address.
   */
  private void findRoute(final Address address) {
    String endInput = this.findRouteTextField.getText();
    String startInput = this.findAddressTextField.getText();

    if (endInput == null || startInput == null) {
      return;
    }
    if (this.currentAddress == null || this.destinationAddress == null) {
      return;
    }

    endInput = endInput.trim();
    startInput = startInput.trim();

    if (endInput.isEmpty() || startInput.isEmpty()) {
      return;
    }

    System.out.println("X: " + this.currentAddress.x() + " " + "Y: "
      + this.currentAddress.y());
    System.out.println("X: " + this.destinationAddress.x() + " " + "Y: "
      + this.destinationAddress.y());

    this.autoCompletePopOver.hide();

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
  private void swapTextFields() {
    Address temp = this.currentAddress;
    this.currentAddress = this.destinationAddress;
    this.destinationAddress = temp;
    String from = this.findAddressTextField.getText();
    this.findAddressTextField.setText(this.findRouteTextField.getText());
    this.findRouteTextField.setText(from);
    if (this.autoCompletePopOver.isShowing()) {
      this.autoCompletePopOver.hide(new Duration(0));
    }
  }

  /**
   * Finds route specific only to cars.
   */
  @FXML
  private void routeByCar() {
  }

  /**
   * Find route specific only to bikes.
   */
  @FXML
  private void routeByFoot() {
  }

  /**
   * Removes the Points Of Interest container from the window.
   */
  @FXML
  private void hidePOI() {
    this.propertiesGridPane.getChildren().remove(this.poiContainer);
    ChartController.moveCompass(0);
  }

  /**
   * Removes the route description container from the window.
   */
  @FXML
  private void hideDirections() {
    this.propertiesGridPane.getChildren().remove(this.directionsContainer);
    ChartController.moveCompass(0);
  }

  /**
   * Shows the Points Of Interest container in the window.
   */
  private void showPOI() {
    if (!this.propertiesGridPane.getChildren().contains(this.poiContainer)) {
      this.propertiesGridPane.getChildren().add(this.poiContainer);
      ChartController.moveCompass(200);
    }
  }

  /**
   * Shows the route description container in the window.
   */
  private void showDirections() {
    if (!this.propertiesGridPane.getChildren().
      contains(this.directionsContainer)) {
      this.propertiesGridPane.getChildren().add(this.directionsContainer);
      ChartController.moveCompass(400);
    }
  }

  /**
   * Adds address to addressStore.
   * @param address the address to add.
   */
  public static void addAddress(final Address address) {
    AddressController.instance.addresses.add(address);
  }

  /**
   * Resets the AddressStore and clears all addresses.
   */
  public static void clearAddresses() {
    AddressController.instance.addresses = new AddressStore();
  }

  /**
   * Listener for input TextFields, controlling auto-complete suggestions.
   */
  private ChangeListener<String> addressFieldListener =
    new ChangeListener<String>() {
    @Override
    public void changed(
      final ObservableValue<? extends String> observable,
      final String oldValue,
      final String newValue
    ) {
      StringProperty textProperty = (StringProperty) observable;
      TextField tf = (TextField) textProperty.getBean();

      AddressController.autoCompleteSuggestions.clear();

      Bounds bounds = tf.localToScreen(tf.getBoundsInParent());

      // If the input in the textfield is above
      // The autocomplete_cutoff then add strings to the suggestions arraylist.
      if (tf.getLength() > AddressController.AUTOCOMPLETE_CUTOFF) {
        List<Address> results =
          AddressController.addresses.search(tf.getText());

        AddressController.address = Address.parse(tf.getText());

        AddressController.autoCompletePopOverVBox =
          new VBox(AddressController.autoCompleteSuggestions.size());

        AddressController.autoCompletePopOverVBox.
          setPrefWidth(bounds.getWidth() + 27);

        if (AddressController.address.number() == null) {
          // Create suggestions without street numbers.
          for (Address a: results) {
            if (a.street() != null && a.postcode() != null && a.city() != null) {
              autoCompleteSuggestions.put(
                a.street()
                  + ", " + a.postcode()
                  + " " + a.city(),
                a
              );
            }

            // End the foreach loop
            // if AutoComplete_max_items limit has been reached.
            if (
              AddressController.autoCompleteSuggestions.size()
                > AddressController.AUTOCOMPLETE_MAX_ITEMS
              ) {
              break;
            }
          }
          for (String suggestion: autoCompleteSuggestions.keySet()) {
            Button b = new Button(suggestion);
            b.setPrefWidth(bounds.getWidth() + 27);

            b.setOnMouseClicked((e2 -> {
              insertStreet(tf, b.getText());
            }));
            AddressController.autoCompletePopOverVBox.getChildren()
              .add(b);
          }
        } else {
          // Create suggestions for full addresses
          for (Address a: results) {
            AddressController.autoCompleteSuggestions.put(
              a.street()
              + " " + a.number()
              + ", " + a.postcode()
              + " " + a.city(),
              a
            );

            // End the foreach loop
            // if AutoComplete_max_items limit has been reached.
            if (
                AddressController.autoCompleteSuggestions.size()
                > AddressController.AUTOCOMPLETE_MAX_ITEMS
              ) {
              break;
            }
          }
          for (
            String suggestion
            : AddressController.autoCompleteSuggestions.keySet()
            ) {
            Button b = new Button(suggestion);
            b.setPrefWidth(bounds.getWidth() + 27);

            b.setOnMouseClicked((e2 -> {
              insertAddress(tf, b.getText());

              if (tf.getId().equals("findAddressTextField")) {
                AddressController.currentAddress = AddressController
                  .autoCompleteSuggestions.get(suggestion);
                AddressController.instance.findAddress(
                  AddressController.currentAddress);
              } else if (tf.getId().equals("findRouteTextField")) {
                AddressController.destinationAddress = AddressController
                  .autoCompleteSuggestions.get(suggestion);
                AddressController.instance.findRoute(
                  AddressController.destinationAddress);
              }
            }));

            AddressController.autoCompletePopOverVBox.getChildren().add(b);
          }
        }
      }

      // Hide the popover if there are no suggestions.
      if (AddressController.autoCompleteSuggestions.size() <= 0) {
        AddressController.autoCompletePopOver.hide(Duration.ONE);
        return;
      }

      handleHighlight();

      // Adds the VBox to the popover.
      AddressController.autoCompletePopOver.setContentNode(
        AddressController.autoCompletePopOverVBox);

      // Makes the popover visible.
      if (!AddressController.autoCompletePopOver.isShowing()) {
        AddressController.autoCompletePopOver.show(
          tf,
          bounds.getMinX() + 14, // 14 = font size
          bounds.getMinY() + bounds.getHeight(),
          Duration.ONE
        );
      }
    }
  };

  /**
   * Inserts a String into a specific text field, and hides PopOver.
   * @param tf the text field to set text.
   * @param text the String to put in text field.
   */
  private static void insertAddress(final TextField tf, final String text) {
    tf.textProperty().removeListener(
      AddressController.instance.addressFieldListener);
    tf.setText(text);
    tf.textProperty().addListener(
      AddressController.instance.addressFieldListener);
    AddressController.autoCompletePopOver.hide(Duration.ONE);
  }

  /**
   * Inserts a String into a specific text field, and hides PopOver.
   * Also changes the cursor position to between street and postcode for easy
   * entering of street number.
   * @param tf the text field to set text.
   * @param text the String to put in text field.
   */
  private static void insertStreet(final TextField tf, final String text) {
    tf.textProperty().removeListener(
      AddressController.instance.addressFieldListener);

    int comma = text.indexOf(",");
    StringBuilder builder = new StringBuilder(text);
    builder.insert(comma, " ");
    tf.setText(builder.toString());

    tf.textProperty().addListener(
      AddressController.instance.addressFieldListener);
    AddressController.autoCompletePopOver.hide(Duration.ONE);
    tf.positionCaret(comma + 1);
  }

  /**
   * Handles default behavior for highlight on auto-complete suggestions.
   */
  private static void handleHighlight() {
    // The suggestion highlight pointer.
    AddressController.pointer = 0;

    // Highlights suggestion based on pointer value.
    AddressController.instance.addHighlightStyle();

    // Removes the current highlight on mouse enter.
    AddressController.autoCompletePopOverVBox.setOnMouseEntered((e4 -> {
      AddressController.instance.removeHighlightStyle();
    }));

    // Adds highlight again on mouse exit.
    AddressController.autoCompletePopOverVBox.setOnMouseExited((e4 -> {
      AddressController.instance.addHighlightStyle();
    }));
  }
}
