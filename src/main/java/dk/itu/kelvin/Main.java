/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin;

// JavaFX utilities
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;

// JavaFX text utilities
import javafx.scene.text.Font;

// FXML utilities
import javafx.fxml.FXMLLoader;

/**
 * Main class.
 *
 * @version 1.0.0
 */
public class Main extends Application {
  /**
   * Start the JavaFX thread and hand off control to the primary controller.
   *
   * @param primaryStage The primary stage of the application.
   *
   * @throws Exception In case of an error.
   */
  @Override
  public void start(final Stage primaryStage) throws Exception {
    FXMLLoader fxmlLoader = new FXMLLoader();

    Parent root = fxmlLoader.load(
      this.getClass().getResource("view/Application.fxml")
    );

    Font.loadFont(
      this.getClass().getResource("font/ionicons.ttf").toExternalForm(), 14
    );

    Font.loadFont(
      this.getClass().getResource("font/ionicons.otf").toExternalForm(), 14
    );

    Scene scene = new Scene(root);
    primaryStage.setTitle("Map Viewer");
    primaryStage.setScene(scene);
    // primaryStage.setResizable(false);
    primaryStage.show();
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
