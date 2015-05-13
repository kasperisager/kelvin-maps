/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.controller;

// I/O utilities
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

// JavaFX stage utilities
import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

// JavaFX Scene utilities
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.control.MenuBar;
import javafx.scene.text.TextAlignment;

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

// Main
import dk.itu.kelvin.Main;

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
  private static final String CUR_VERSION = "1.0.0";

  /**
   * Current binary file that the user can overwrite by saving or load.
   */
  private static final String CURRENT_BIN =  "currentMap.bin";

  /**
   * Location for default bin file. Default bin can't be changed.
   */
  private static final String DEFAULT_BIN =  "defaultMap.bin";

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

    if (file != null && file.exists()) {
      MenuController.clearMap();
      ChartController.loadMap(file);
    }
  }

  /**
   * Saves current map file as bin.
   */
  @FXML
  private void saveBin() {
    File file = new File(CURRENT_BIN);
    ApplicationController.addIcon();

    Platform.runLater(() -> {
      try (ObjectOutputStream out = new ObjectOutputStream(
        new FileOutputStream(file))
      ) {
        out.writeObject(ChartController.getBounds());
        out.writeObject(ChartController.getElementStore());
        out.writeObject(AddressController.getAddressStore());
        out.close();

        ApplicationController.removeIcon();
      } catch (Exception e) {
        ApplicationController.removeIcon();
        throw new RuntimeException(e);
      }
    });
  }

  /**
   * Loads the last saved bin map.
   */
  @FXML
  private void loadBin() {
    this.loadBin(CURRENT_BIN);
  }

  /**
   * Loads the default bin map.
   */
  @FXML
  private void defaultBin() {
    MenuController.loadDefault();
  }

  /**
   * Static method for loading the default map that can't be changed.
   */
  public static void loadDefault() {
    ApplicationController.addIcon();

    MenuController.clearMap();

    Platform.runLater(() -> {
      try (
        ObjectInputStream in = new ObjectInputStream(
          Main.class.getResourceAsStream(DEFAULT_BIN)
        );
      ) {
        BoundingBox bounds = (BoundingBox) in.readObject();
        ElementStore elementStore = (ElementStore) in.readObject();
        AddressStore addressStore = (AddressStore) in.readObject();
        in.close();

        ChartController.loadBinMap(elementStore, bounds);
        AddressController.setAddressStore(addressStore);
        ApplicationController.removeIcon();
      } catch (Exception e) {
        ApplicationController.removeIcon();
        throw new RuntimeException(e);
      }
    });
  }

  /**
   * Loads a map from binary file based on filename.
   * @param filename a String for representing the file directory.
   */
  private static void loadBin(final String filename) {
    File file = new File(filename);
    if (!file.exists()) {
      ApplicationController.removeIcon();
      return;
    }
    ApplicationController.addIcon();

    MenuController.clearMap();
    Platform.runLater(() -> {
      try (ObjectInputStream in = new ObjectInputStream(
        new FileInputStream(file))
      ) {
        BoundingBox bounds = (BoundingBox) in.readObject();
        ElementStore elementStore = (ElementStore) in.readObject();
        AddressStore addressStore = (AddressStore) in.readObject();
        in.close();

        ChartController.loadBinMap(elementStore, bounds);
        AddressController.setAddressStore(addressStore);
        ApplicationController.removeIcon();
      } catch (Exception e) {
        ApplicationController.removeIcon();
        throw new RuntimeException(e);
      }
    });
  }

  /**
   * Calls methods for resetting all relevant data when loading new map file.
   */
  private static void clearMap() {
    AddressController.resetUI();

    AddressController.clearAddresses();
    ChartController.clearMap();
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
    version.setTextAlignment(TextAlignment.CENTER);
    credits.setTextAlignment(TextAlignment.CENTER);

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
