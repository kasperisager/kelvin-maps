/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.controller;

// JavaFX layout

import javafx.scene.Scene;
import javafx.scene.Node;
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
  private static final ApplicationController applicationController;

  @FXML
  private BorderPane borderPane;

  public void initialize() {
    ApplicationController.applicationController = this;
  }

  /**
   * Add style class to borderpane.
   */
  public static void setHighContrast() {
    this.borderPane.getStyleClass().add("high-contrast");
  }
}


