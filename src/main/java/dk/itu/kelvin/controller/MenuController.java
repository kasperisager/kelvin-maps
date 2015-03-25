/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.controller;

// I/O utilities
import java.io.File;

// JavaFX stage utilities
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

// Later
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;

// ControlsFX
import org.controlsfx.control.PopOver;

/**
 * MenuBar controller class.
 */
public class MenuController {
  /**
   * Current Version number.
   */
  private static final String CUR_VERSION = "0.4.0";

  /**
   * Location for default bin file.
   */
  private static final String DEFAULT_BIN = "";

  /**
   * Manual PopOver.
   */
  private PopOver manual;

  /**
   * About PopOver.
   */
  private PopOver about;

  /**
   * Main system MenuBar.
   */
  @FXML
  private MenuBar mainMenuBar;

  /**
   * Choose an .OSM file to be loaded.
   */
  public final void pickFile() {
    FileChooser filechooser = new FileChooser();
    filechooser.setTitle("Select file to load");
    filechooser.getExtensionFilters().add(
      new FileChooser.ExtensionFilter("All Files", "*.osm")
    );
    File file = filechooser.showOpenDialog(new Stage());

    if (file != null) {
      //do something with the file.
      System.out.println(file.getAbsolutePath());
    }
  }

  /**
   * Saves current OSM file as bin.
   */
  public void saveBin() {
    //do stuff.
  }

  /**
   * Loads the last used bin file.
   */
  public void loadBin() {
    //do stuff
  }

  /**
   * Loads the default bin file.
   */
  public void defaultBin() {
    //do stuff.
  }

  /**
   * Closes the application.
   */
  public final void close() {
    Stage stage = (Stage) this.mainMenuBar.getScene().getWindow();
    stage.close();
  }

  /**
   * Resets all application settings to default.
   */
  public void resetAll() {
    //do stuff.
  }

  /**
   * Manual shows information about general interaction with the software.
   */
  public final void showManual() {
    this.manual = new PopOver();
    VBox vbox = new VBox();

    vbox.setAlignment(Pos.CENTER);
    vbox.setPrefWidth(500);
    vbox.getStyleClass().add("aboutVBox");

    Label l1 = new Label("User Manual");
    l1.getStyleClass().add("header");

    vbox.getChildren().addAll(l1);
    this.manual.setContentNode(vbox);
    this.manual.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
    this.manual.setCornerRadius(2);
    this.manual.setArrowSize(6);
    this.manual.setAutoHide(true);
    this.manual.show(this.mainMenuBar);
  }

  /**
   * About menu showing information about the software and creators.
   */
  public final void showAbout() {
    this.about = new PopOver();
    VBox vbox = new VBox();
    vbox.getStyleClass().add("aboutVBox");
    Label l1 = new Label("About");
    l1.getStyleClass().add("header");
    Label l9 = new Label("Kelvin Maps");
    Label l8 = new Label("Version " + this.CUR_VERSION);
    Label l10 = new Label("");
    Label l2 = new Label("This software was made by:");
    Label l3 = new Label("Kasper Isager");
    Label l4 = new Label("Mathias Grundtvig Andreasen");
    Label l5 = new Label("Johan Hjalte á Trødni");
    Label l7 = new Label("Niklas Pelle Michelsen");
    Label l6 = new Label("Sebastian Molding Bork");
    vbox.getChildren().addAll(l1, l9, l8, l10, l2, l3, l4, l5, l7, l6);
    vbox.setAlignment(Pos.CENTER);
    vbox.setPrefWidth(500);

    this.about.setContentNode(vbox);
    this.about.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
    this.about.setCornerRadius(2);
    this.about.setArrowSize(6);
    this.about.setAutoHide(true);
    this.about.show(this.mainMenuBar);
  }
}
