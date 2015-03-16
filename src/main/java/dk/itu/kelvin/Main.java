/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin;

// JavaFX utilities
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;

// JavaFX text utilities
import javafx.scene.text.Font;

// FXML utilities
import javafx.fxml.FXMLLoader;

/**
 * Main class.
 *
 * @version 1.0.0
 */
public final class Main extends Application {
  /**
   * The title of the application.
   *
   * This will be shown in the title bar of the application window.
   */
  private static final String TITLE = "Kelvin Maps";

  /**
   * The minimum width of the application window.
   */
  private static final int MIN_WIDTH = 500;

  /**
   * The minimum height of the application window.
   */
  private static final int MIN_HEIGHT = 300;

  /**
   * The main view of the application.
   *
   * This view be loaded and used as the main entry point to the application.
   */
  private static final String MAIN_VIEW = "view/Application.fxml";

  /**
   * Stylesheets to load in the application.
   */
  private static final String[] STYLESHEETS = new String[] {
    "stylesheet/Main.css"
  };

  /**
   * Fonts to load in the application.
   */
  private static final String[] FONTS = new String[] {
    "font/ionicons.ttf"
  };

  /**
   * The scene of the application.
   */
  private Scene scene;

  /**
   * Load the main scene of the application.
   */
  private void loadScene() {
    // Bail out if the scene has already been loaded.
    if (this.scene != null) {
      return;
    }

    try {
      this.scene = new Scene(
        (new FXMLLoader(this.getClass().getResource(MAIN_VIEW))).load()
      );
    }
    catch (Exception ex) {
      // This should never happen so simply propagate the exception as a
      // runtime exception.
      throw new RuntimeException(ex);
    }
  }

  /**
   * Load all application stylesheets.
   */
  private void loadStylesheets() {
    // Attempt loading the scene if it hasn't already been loaded.
    if (this.scene == null) {
      this.loadScene();
    }

    for (String stylesheet: STYLESHEETS) {
      this.scene.getStylesheets().add(
        this.getClass().getResource(stylesheet).toExternalForm()
      );
    }
  }

  /**
   * Load all application fonts.
   */
  private void loadFonts() {
    for (String font: FONTS) {
      Font.loadFont(this.getClass().getResourceAsStream(font), 14);
    }
  }

  /**
   * Start the JavaFX thread and hand off control to the main view.
   *
   * @param stage The primary stage of the application.
   */
  @Override
  public void start(final Stage stage) {
    // 1. Set the application title.
    stage.setTitle(TITLE);

    // 2. Set the minimum dimensions of the application window.
    stage.setMinWidth(MIN_WIDTH);
    stage.setMinHeight(MIN_HEIGHT);

    // 3. Load the main scene of the application.
    this.loadScene();

    // 4. Load the application fonts.
    this.loadFonts();

    // 5. Load the application stylesheets.
    this.loadStylesheets();

    // 6. Set the scene of the primary stage.
    stage.setScene(this.scene);

    // 7. Show the primary stage. Eureka!
    stage.show();
  }

  /**
   * Launch the application.
   *
   * @param args Runtime arguments.
   */
  public static void main(final String[] args) {
    Main.launch(args);
  }
}
