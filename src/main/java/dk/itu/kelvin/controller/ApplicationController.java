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

// JavaFX application
import javafx.application.Platform;

// FXML utilities
import javafx.fxml.FXML;

/**
 * Application controller class.
 */
public final class ApplicationController {
  /**
   * Field that holds only instance of the class.
   */
  private static ApplicationController instance;

  /**
   * The rotate animation.
   */
  private RotateTransition rt;

  /**
   * The main parent element.
   */
  @FXML
  private BorderPane mainBorderPane;

  /**
   * StackPane element.
   */
  @FXML
  private StackPane stackPane;

  /**
   * Main parent StackPane for ChartController.
   */
  @FXML
  private StackPane chart;

  /**
   * The loading icon.
   */
  @FXML
  private Label loadIcon;

  /**
   * Initialize a new application controller.
   *
   * <p>
   * <b>OBS:</b> This constructor can only ever be called once by JavaFX.
   */
  public ApplicationController() {
    super();

    if (ApplicationController.instance != null) {
      throw new RuntimeException("Only a single controller instance can exist");
    }
  }

  /**
   * JavaFX constructor for the ApplicationController.
   */
  @FXML
  private void initialize() {
    ApplicationController.instance = this;

    ApplicationController.instance.rt = new RotateTransition(
      Duration.millis(10000), this.loadIcon
    );
    ApplicationController.instance.rotateIcon();
    ApplicationController.instance.stackPane.getChildren().remove(
      this.loadIcon
    );

    ApplicationController.addIcon();
    Platform.runLater(() -> {
      MenuController.loadDefault();
    });
  }

  /**
   * To rotate the load icon.
   */
  public static void rotateIcon() {
    ApplicationController.instance.rt.setByAngle(360);
    ApplicationController.instance.rt.setCycleCount(
      RotateTransition.INDEFINITE
    );
    ApplicationController.instance.rt.play();
  }

  /**
   * Stops rotation and removes the icon.
   */
  public static void removeIcon() {
    ApplicationController.instance.rt.stop();
    if (ApplicationController.instance.stackPane.getChildren().contains(
      ApplicationController.instance.loadIcon
    )) {
      ApplicationController.instance.stackPane.getChildren().remove(
        ApplicationController.instance.loadIcon
      );
    }
    ApplicationController.instance.chart.setDisable(false);
  }

  /**
   * Adds the loading icon to the stack pane.
   */
  public static void addIcon() {
    if (!ApplicationController.instance.stackPane.getChildren().contains(
      ApplicationController.instance.loadIcon
    )) {
      ApplicationController.instance.stackPane.getChildren().add(
        ApplicationController.instance.loadIcon
      );
    }
    ApplicationController.instance.rotateIcon();
    ApplicationController.instance.chart.setDisable(true);
  }
}
