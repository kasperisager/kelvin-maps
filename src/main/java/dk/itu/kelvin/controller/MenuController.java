/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.controller;

// I/O utilities
import java.io.File;

// JavaFX stage utilities
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MenuController {
  /**
   * Choose an .OSM file to be loaded.
   */
  public void pickFile() {
    FileChooser filechooser= new FileChooser();
    filechooser.setTitle("Select file to load");
    filechooser.getExtensionFilters().add(
      new FileChooser.ExtensionFilter("Text Files", "*.osm")
    );
    File file = filechooser.showOpenDialog(new Stage());
    
    if(file != null){
      //do something with the file
      System.out.println(file.getAbsolutePath());
    }
  }
}
