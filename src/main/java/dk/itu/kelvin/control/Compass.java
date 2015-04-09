package dk.itu.kelvin.control;

// JavaFX Controls
import javafx.scene.control.Button;
import javafx.scene.control.Control;

// JavaFX Layout
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

// JavaFX Shapes
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

// JavaFX Transforms
import javafx.scene.transform.Affine;

/**
 * Compass control
 */
public class Compass extends Control {
  private static final String DEFAULT_STYLE_CLASS = "";
  private static final String DEFAULT_BUTTON_TEXT = "";
  private static final int DEFAULT_ARROW_SIZE = 0;
  private static final int DEFAULT_CIRCLE_SIZE = 0;

  private int arrowSize = 0;
  private int circleSize = 0;

  StackPane compass;
  VBox vbox;
  Path arrow;
  Circle circle;
  VBox vbox2;
  Button button;

  private Affine compassTransform = new Affine();

  public Compass() {



  }
  public Compass(String text){}
  public Compass(String text, int arrowSize, int circleSize){
    compass = new StackPane();
    vbox = new VBox(2);
    arrow = new Path();
    circle = new Circle();
    vbox2 = new VBox(1);
    button = new Button();

    createPath();
  }

  private void createPath(){
    this.arrow.getElements().add(new MoveTo());
    this.arrow.getElements().add(new LineTo());
    this.arrow.getElements().add(new LineTo());
    this.arrow.getElements().add(new LineTo());
  }

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
