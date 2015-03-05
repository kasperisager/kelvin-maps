/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.controller;

// JavaFX layout
import javafx.scene.layout.BorderPane;

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
   * Borderpane element.
   */
  @FXML
  private BorderPane borderPane;

  /**
   * JavaFX constructor for the ApplicationController.
   */
  public void initialize() {
    ApplicationController.instance(this);
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
}
