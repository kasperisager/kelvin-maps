/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.controller;

// I/O utilities
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

// JavaFX stage utilities
import javafx.stage.FileChooser;
import javafx.stage.Stage;

// JavaFX Scene utilities
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.control.MenuBar;

// JavaFX Geometry
import javafx.geometry.Pos;

// Kelvin Models
import dk.itu.kelvin.model.BoundingBox;

// Kelvin Stores
import dk.itu.kelvin.store.AddressStore;
import dk.itu.kelvin.store.ElementStore;

// JavaFX FXML
import javafx.fxml.FXML;

// ControlsFX
import org.controlsfx.control.PopOver;

/**
 * MenuBar controller class.
 */
public final class MenuController {
  /**
   * The menu controller instance.
   */
  private static MenuController instance;

  /**
   * Current Version number.
   */
  private static final String CUR_VERSION = "RC1";

  /**
   * Current binary file that the user can overwrite by saving or load.
   */
  private static final String CURRENT_BIN = "currentMap.bin";

  /**
   * Location for default bin file. Default bin can't be changed.
   */
  private static final String DEFAULT_BIN = "defaultMap.bin";

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
   * Initialize a new menu controller.
   *
   * <p>
   * <b>OBS:</b> This constructor can only ever be called once by JavaFX.
   */
  public MenuController() {
    super();

    if (MenuController.instance != null) {
      throw new RuntimeException("Only a single controller instance can exist");
    }
  }

  /**
   * The initialize method.
   */
  @FXML
  private void initialize() {
    MenuController.instance = this;
  }
  /**
   * Choose an .OSM, .XML, .PBF file to be loaded.
   */
  @FXML
  private void pickFile() {
    FileChooser filechooser = new FileChooser();
    filechooser.setTitle("Select file to load");
    filechooser.getExtensionFilters().add(
      new FileChooser.ExtensionFilter("All Files", "*.osm", "*.xml", "*.pbf")
    );
    File file = filechooser.showOpenDialog(new Stage());

    if (file != null) {
      AddressController.resetPOI();
      AddressController.clearAddresses();
      ChartController.clearMap();
      ChartController.loadMap(file);
    }
  }

  /**
   * Saves current map file as bin.
   */
  @FXML
  private void saveBin() {
    File file = new File(CURRENT_BIN);

    try (ObjectOutputStream out = new ObjectOutputStream(
      new FileOutputStream(file))
    ) {
      out.writeObject(ChartController.getBounds());
      out.writeObject(ChartController.getElementStore());
      out.writeObject(AddressController.getAddressStore());
      out.close();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Loads the last saved bin map.
   */
  @FXML
  private void loadBin() {
    File file = new File(CURRENT_BIN);

    AddressController.resetPOI();
    ChartController.clearMap();

    try (ObjectInputStream in = new ObjectInputStream(
      new FileInputStream(file))
    ) {
      BoundingBox bounds = (BoundingBox) in.readObject();
      ElementStore elementStore = (ElementStore) in.readObject();
      AddressStore addressStore = (AddressStore) in.readObject();
      in.close();

      ChartController.loadBinMap(elementStore, bounds);
      AddressController.setAddressStore(addressStore);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Loads the default bin map.
   */
  @FXML
  private void defaultBin() {
    MenuController.instance.loadDefault();
  }

  /**
   * Static method for loading the default map that can't be changed.
   */
  public static void loadDefault() {
    File file = new File(DEFAULT_BIN);
    if (!file.exists()) {
      return;
    }
    AddressController.resetPOI();
    ChartController.clearMap();

    try (ObjectInputStream in = new ObjectInputStream(
      new FileInputStream(file))
    ) {
      BoundingBox bounds = (BoundingBox) in.readObject();
      ElementStore elementStore = (ElementStore) in.readObject();
      AddressStore addressStore = (AddressStore) in.readObject();
      in.close();

      ChartController.loadBinMap(elementStore, bounds);
      AddressController.setAddressStore(addressStore);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Closes the application.
   */
  @FXML
  private void close() {
    Stage stage = (Stage) this.mainMenuBar.getScene().getWindow();
    stage.close();
  }

  /**
   * Resets all application settings to default.
   */
  @FXML
  private void resetAll() {
    //do stuff.
  }

  /**
   * Manual shows information about general interaction with the software.
   */
  @FXML
  private void showManual() {
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
  @FXML
  private void showAbout() {
    this.about = new PopOver();
    VBox vbox = new VBox();
    vbox.getStyleClass().add("aboutVBox");

    Label header = new Label("About");
    header.getStyleClass().add("header");

    Label version = new Label(
      "Kelvin Maps"
    + "\nVersion: " + this.CUR_VERSION
    );

    Label credits = new Label(
      "This software was made by:"
    + "\n"
    + "\nJohan Hjalte á Trødni"
    + "\nKasper Kronborg Isager"
    + "\nMathias Grundtvig Andreasen"
    + "\nNiklas Pelle Michelsen"
    + "\nSebastian Molding Bork"
    + "\n"
    );

    vbox.getChildren().addAll(header, version, credits);
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
