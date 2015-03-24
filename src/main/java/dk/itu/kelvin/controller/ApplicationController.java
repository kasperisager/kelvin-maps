/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.controller;

// JavaFX utilities
import javafx.util.Duration;

// JavaFX layout
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

// JavaFX controls
import javafx.scene.control.Label;

// JavaFX animations
import javafx.animation.RotateTransition;

// FXML utilities
import javafx.fxml.FXML;

/**
 * Application controller class.
 *
 * @version 1.0.0
 */
public final class ApplicationController {
  /**
   * Field that holds only instance of the class.
   */
  private static ApplicationController instance;

  /**
   * The rotate animation.
   */
  RotateTransition rt;

  /**
   * Borderpane element.
   */
  @FXML
  private BorderPane borderPane;

  /**
   * StackPane element.
   */
  @FXML
  private StackPane stackPane;

  /**
   * The loading icon.
   */
  @FXML
  private Label loadIcon;

  /**
   * JavaFX constructor for the ApplicationController.
   */
  public void initialize() {
    ApplicationController.instance(this);
    rt = new RotateTransition(Duration.millis(1000), this.loadIcon);
    rotateIcon();
  }

  /**
   * Get the application controller instance.
   *
   * @return The application controller instance.
   */
  public static ApplicationController instance() {
    return ApplicationController.instance;
  }

  /**
   * Set the application controller instance.
   *
   * @param instance The application controller instance.
   */
  private static void instance(final ApplicationController instance) {
    ApplicationController.instance = instance;
  }

  /**
   * Add a styleclass to borderpane element.
   * This enables us to change colours of the map.
   */
  public static void highContrast() {
    if (!ApplicationController.instance().borderPane.getStyleClass().
      contains("high-contrast")) {
      ApplicationController.instance().borderPane.getStyleClass().
      add("high-contrast");
      System.out.println(ApplicationController.instance().borderPane.
      getStyleClass());
    } else {
      ApplicationController.instance().borderPane.getStyleClass().
      remove("high-contrast");
      System.out.println(ApplicationController.instance().borderPane.
      getStyleClass());
    }
  }

  /**
   * To rotate the load icon.
   */
  public void rotateIcon() {
    rt.setByAngle(360);
    rt.setCycleCount(RotateTransition.INDEFINITE);
    rt.play();
  }

  /**
   * Stops rotation and removes the icon.
   */
  public static void removeIcon() {
    ApplicationController.instance().rt.stop();
    ApplicationController.instance().stackPane.getChildren().remove(ApplicationController.instance().loadIcon);
  }

}
