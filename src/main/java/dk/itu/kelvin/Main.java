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
public final class Main extends Application {
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

    Scene scene = new Scene(root);

    Font.loadFont(
      this.getClass().getResourceAsStream("font/ionicons.ttf"), 14
    );

    scene.getStylesheets().add(
      this.getClass().getResource("stylesheet/Main.css").toExternalForm()
    );

    primaryStage.setTitle("Kelvin Maps");
    primaryStage.setScene(scene);
    primaryStage.setMinHeight(500);
    primaryStage.setMinWidth(300);
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
