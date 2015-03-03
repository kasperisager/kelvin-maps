package dk.itu.kelvin.controller;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
/**
 * Copyright (C) 2015 The Authors.
 */


public class MenuController {
  /**
   * Choose an .OSM file to be loaded.
   */
  public void pickFile() {
    FileChooser filechooser= new FileChooser();
    filechooser.setTitle("Select file to load");
    filechooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("Text Files", "*.osm"));
    File file = filechooser.showOpenDialog(new Stage());
    if(file != null){
      //do something with the file
      System.out.println(file.getAbsolutePath());
    }
  }
}
