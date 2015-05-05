/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.controller;

// I/O utilities
import java.io.*;

// JavaFX stage utilities
import com.sun.glass.ui.Menu;
import dk.itu.kelvin.model.BoundingBox;
import dk.itu.kelvin.store.AddressStore;
import dk.itu.kelvin.store.ElementStore;
import dk.itu.kelvin.util.function.Callback;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

// JavaFX Scene utilities
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.control.MenuBar;

// JavaFX Geometry
import javafx.geometry.Pos;

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
  private static final String CUR_VERSION = "0.4.0";

  private static final String CURRENT_BIN = "currentMap.bin";

  /**
   * Location for default bin file.
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
   * Getting MenuController instance.
   * @return MenuController instance.
   */
  public static MenuController instance() {
    return MenuController.instance;
  }

  /**
   * Initializing the MenuController instance.
   * @param instance the MenuController instance.
   */
  private static void instance(final MenuController instance) {
    MenuController.instance = instance;
  }

  /**
   * The initialize method.
   */
  @FXML
  private void initialize() {
    MenuController.instance(this);
  }
  /**
   * Choose an .OSM, .XML, .PBF file to be loaded.
   */
  @FXML
  private void pickFile() {
    FileChooser filechooser = new FileChooser();
    filechooser.setTitle("Select file to load");
    filechooser.getExtensionFilters().add(
      new FileChooser.ExtensionFilter("All Files", "*.osm", "*xml", "*.pbf")
    );
    File file = filechooser.showOpenDialog(new Stage());

    if (file != null) {
      //do something with the file.
      AddressController.instance().clearAddresses();
      ChartController.instance().clearMap();
      ChartController.instance().loadMap(file);
    }
  }

  /**
   * Saves current map file as bin.
   */
  @FXML
  private void saveBin() {
    //do stuff.
    File file = new File(CURRENT_BIN);

    try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
      out.writeObject(ChartController.getBounds());
      out.writeObject(ChartController.getElementStore());
      out.writeObject(AddressController.getAddressStore());
      out.close();
      System.out.println("Save complete");
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
    /*
    AddressController.instance().getAddressStore().load(file, () -> {
      System.out.println("Success with loading AddressStore");
    }); */
    ChartController.instance().clearMap();
    try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))){
      BoundingBox bounds = (BoundingBox) in.readObject();
      ElementStore elementStore = (ElementStore) in.readObject();
      AddressStore addressStore = (AddressStore) in.readObject();
      in.close();

      ChartController.instance().loadBinMap(elementStore, bounds);
      AddressController.instance().setAddressStore(addressStore);
      System.out.println("Load complete");
    }catch (Exception e){
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
  public static void loadDefault(){
    File file = new File(DEFAULT_BIN);

    ChartController.instance().clearMap();
    try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))){
      BoundingBox bounds = (BoundingBox) in.readObject();
      ElementStore elementStore = (ElementStore) in.readObject();
      AddressStore addressStore = (AddressStore) in.readObject();
      in.close();

      ChartController.instance().loadBinMap(elementStore, bounds);
      AddressController.instance().setAddressStore(addressStore);
      System.out.println("Load complete");
    }catch (Exception e){
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
