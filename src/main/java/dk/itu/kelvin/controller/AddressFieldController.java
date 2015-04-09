package dk.itu.kelvin.controller;

import dk.itu.kelvin.model.Address;
import dk.itu.kelvin.store.AddressStore;
import dk.itu.kelvin.util.HashTable;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;

import java.util.List;

/**
 * AddressField controller
 */
public class AddressFieldController {

  private static final int AUTOCOMPLETE_CUTOFF = 2;

  private static final int AUTOCOMPLETE_MAX_ITEMS = 5;

  private String[] tags = {"Parking", "Cafe", "Restaurant", "Fast Food",
    "Toilets", "Pub", "Recycling", "Bar", "Compressed Air", "Post Box",
    "Taxi", "BBQ", "Solarium", "Telephone"};

  @FXML
  private VBox mainAddressFieldVBox;

  @FXML
  private TextField findAddressTextField;

  @FXML
  private TextField findRouteTextField;

  @FXML
  private ToggleButton settingsToggleButton;

  @FXML
  private GridPane propertiesGridPane;

  @FXML
  private VBox poiContainer;

  @FXML
  private VBox poiContentVBox;

  @FXML
  private VBox directionsContainer;

  @FXML
  private VBox directionsContentVBox;

  private PopOver settingsPopOver;



  private PopOver autoCompletePopOver;

  private HashTable <String, Address> autoCompleteSuggestions;


  private VBox autoCompletePopOverVBox;

  private int pointer = 0;


  public void initialize(){
    autoCompleteSuggestions = new HashTable<>(AUTOCOMPLETE_MAX_ITEMS);

    this.propertiesGridPane.getChildren().remove(this.poiContainer);
    this.propertiesGridPane.getChildren().remove(this.directionsContainer);

    createSettingsPopOver();
    initPoiBox();

    initAutoCompletePopOver();
    setAutoComplete(this.findAddressTextField);
    setAutoComplete(this.findRouteTextField);
  }

  private void createSettingsPopOver(){
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

  private void initPoiBox(){
    for (String s : this.tags) {
      CheckBox cb = new CheckBox(s);
      cb.setPrefWidth(200);

      //add CheckBox event listener and update shown labels

      this.poiContentVBox.getChildren().add(cb);
    }

  }
  private void initAutoCompletePopOver(){
    this.autoCompletePopOver = new PopOver();
    this.autoCompletePopOver.setArrowLocation(PopOver.ArrowLocation.TOP_LEFT);
    this.autoCompletePopOver.setCornerRadius(2);
    this.autoCompletePopOver.setArrowSize(0);
    this.autoCompletePopOver.setAutoHide(true);
    this.autoCompletePopOver.setDetachable(false);
  }

  private void setAutoComplete(final TextField tf) {
    tf.textProperty().addListener((e) -> {
      this.autoCompleteSuggestions.clear();

      // If the input in the textfield is above
      // The autocomplete_cutoff then add strings to the suggestions arraylist.
      if (tf.getLength() > AUTOCOMPLETE_CUTOFF) {
        List<Address> results = this.addresses.search(tf.getText());

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

      this.autoCompletePopOverVBox = new VBox(this.autoCompleteSuggestions.size());

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
  private void addHighlightStyle(){
    Button b = (Button) this.autoCompletePopOverVBox.getChildren().get(this.pointer);
    b.getStyleClass().add("highlight");
  }
  private void removeHighlightStyle(){
    Button b = (Button) this.autoCompletePopOverVBox.getChildren().get(this.pointer);
    b.getStyleClass().remove("highlight");
  }
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

  @FXML
  public void findAddress(){
    if (this.autoCompletePopOver.isShowing()) {
      Button b = (Button) this.autoCompletePopOverVBox.getChildren().get(this.pointer);
      this.findAddress(this.autoCompleteSuggestions.get(b.getText()));

      this.findAddressTextField.setText(b.getText());
      this.autoCompletePopOver.hide();
    }

  }
  public void findAddress(final Address address){

  }
  @FXML
  public void findRoute(){
    if (this.autoCompletePopOver.isShowing()) {
      Button b = (Button) this.autoCompletePopOverVBox.getChildren().get(this.pointer);
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

    if (!this.propertiesGridPane.getChildren().contains(this.directionsContainer)) {
      this.propertiesGridPane.getChildren().add(this.directionsContainer);
    }

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

  @FXML
  public void swapTextFields(){
    String from = this.findAddressTextField.getText();
    this.findAddressTextField.setText(this.findAddressTextField.getText());
    this.findRouteTextField.setText(from);
    if (this.autoCompletePopOver.isShowing()) {
      this.autoCompletePopOver.hide(new Duration(0));
    }

  }

  @FXML
  public void routeByCar(){

  }
  @FXML
  public void routeByFoot(){

  }

  @FXML
  public void hidePOI(){
    propertiesGridPane.getChildren().remove(poiContainer);
  }
  @FXML
  public void hideDirections(){
    propertiesGridPane.getChildren().remove(directionsContainer);
  }
}
