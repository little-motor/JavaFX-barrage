package main;
/*
   创建时间：18.4.14d
   功能：主函数，图形界面的设计，各个函数和包的调用
*/
import main.FXthread;
import javafx.util.Duration;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class FXmain extends Application{
  //创建全局变量
  private double screenX,screenY;
  private Pane pane;
  private final Duration duration = new Duration(5000);//创建持续时间
  
  //初始化参数
  public FXmain() {
  //创建画布
  pane = new Pane();  
  Rectangle2D screen = Screen.getPrimary().getBounds();//检查屏幕边界
  screenX = screen.getMaxX();
  screenY = screen.getMaxY(); 
  }
  
  @Override
  public void start(Stage primaryStage) throws Exception { 
    pane.setStyle("-fx-background: transparent;");
    //创建场景
    Scene scene = new Scene(pane,screenX,screenY);
    
    //scene设置为透明
    scene.setFill(null);
    //场景设置为透明
    primaryStage.initStyle(StageStyle.TRANSPARENT);
    primaryStage.setAlwaysOnTop(true);
    primaryStage.setScene(scene);
    primaryStage.setTitle("test");
    primaryStage.show();

    //javafx异步编程
    FXthread FX = new FXthread(pane);
    //周期5s
    FX.service.setPeriod(duration);
    FX.service.start();
    
    
    
    
  }
  public static void main(String[] args) throws Exception {
    Application.launch(args);
  }
}