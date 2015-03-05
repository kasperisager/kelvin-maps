/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.controller;

// JavaFX layout
import javafx.scene.layout.BorderPane;

// JavaFX shapes

// JavaFX input

// JavaFX transformations

// Java FX event

// Controls FX

// FXML utilities
import javafx.fxml.FXML;

// Components

// Models

/**
 * Application controller class.
 *
 * @version 1.0.0
 */
public class ApplicationController {
  /**
   * Field that holds only instance of the class.
   */
  private static ApplicationController applicationController;

  /**
   * Borderpane element.
   */
  @FXML
  private BorderPane borderPane;

  /**
   * JavaFX constructor for the ApplicationController.
   */
  public final void initialize() {
    ApplicationController.applicationController = this;
  }

  /**
   *  Method to point to this instance from a static context.
   * @return this instance of the class.
   */
  public static ApplicationController instance() {
    return ApplicationController.applicationController;
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


