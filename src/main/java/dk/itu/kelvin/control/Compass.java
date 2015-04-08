package dk.itu.kelvin.control;

// JavaFX Controls
import javafx.scene.control.Button;
import javafx.scene.control.Control;

// JavaFX Layout
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

// JavaFX Shapes
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;

/**
 * Compass control
 */
public class Compass extends Control {
  private static final String DEFAULT_STYLE_CLASS = "";

  StackPane compass;
  VBox vbox;
  Path arrow;
  Circle circle;
  VBox vbox2;
  Button button;

  public Compass() {
    compass = new StackPane();
    vbox = new VBox(2);
    arrow = new Path();
    circle = new Circle();
    vbox2 = new VBox(1);
    button = new Button();
  }
  public Compass(String text){}
  public Compass(String text, int arrowSize, int circleSize){}


  public void reset(){

  }

  public void rotate(int degree){

  }

  public void setText(String text){

  }

  public void setArrowSize(int size){

  }

  public void setCircleSize(int size){

  }

  public void setButtonStyle(String styleClass){

  }
  public void setCircleStyle(String styleClass){

  }
  public void setArrowStyle(String styleClass){

  }
  public void setCompassStyle(String styleClass){

  }
  public void setTooltip(String tooltip){

  }


}
