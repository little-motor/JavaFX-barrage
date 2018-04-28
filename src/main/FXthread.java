package main;

import db.DBmanager;

import java.io.File;
import java.sql.ResultSet;

import javafx.animation.PathTransition;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.util.Duration;

/* FX异步编程，轮询数据库将访问得到的数据加入label主线程
 * littlemotor
 * 创建日期：18.4.21
 * */
public class FXthread {
  private Pane pane;
  private int cycleIndex,num = 0,numLab = 0;
  private String[] content = new String[6];
  private Label[] label = new Label[100];
  private double screenX,endX;//创建轨迹
  Rectangle2D screen = Screen.getPrimary().getBounds();
  private Line[] fourLine = new Line[4];//4条弹幕轨迹
  private PathTransition[] pt = new PathTransition[100]; 
  
  private double[] changedY = new double[4];
  //初始化
  FXthread(Pane pane) {
    this.pane = pane;
     
    for(int i = 0;i < 6;i++) {
      content[i] = null;
      //System.out.println(content[i]);
    }
    //初始化轨迹
    //检查屏幕边界
    
    screenX = screen.getMaxX() + 200;
    double screenY = screen.getMaxY();
    endX = -200;
    //分别初始化四条 四分之一轨迹
    double section1 = ((screenY / 4 * 3) / 5);
    for(int i = 0; i < 4; i++) {
      //Y的值每次都会变
      changedY[i] = (screenY / 8 + (i + 1) * section1);
      //注意初始化啊！
      fourLine[i] = new Line();      
      //System.out.println(oneOfFourY[i]);
      fourLine[i].setStartX(screenX);
      fourLine[i].setStartY(changedY[i]);
      fourLine[i].setEndX(endX);
      fourLine[i].setEndY(changedY[i]);
      //System.out.println("轨迹初始化成功");
    }
    //测试线
//    pane.getChildren().add(fourLine[0]);
//    pane.getChildren().add(fourLine[1]);
//    pane.getChildren().add(fourLine[2]);
//    pane.getChildren().add(fourLine[3]);  
    
  }
  
  ScheduledService<Void> service = new ScheduledService<Void>() {
    
    @Override
    protected Task<Void> createTask() {
      //返回Task<Void> 执行
      return new Task<Void>(){
        @Override
        protected Void call() throws Exception {
          cycleIndex = 0 ;//实际循环次数
          File file = new File("c:\\Users\\littlemotor\\Apply\\test\\development.sqlite3");
          if(file.exists()) {
          //建立数据库连接
          DBmanager db = new DBmanager();
          ResultSet result = db.getContent();
          int i = 0;
          //每次轮询数据库得到四个元素
          while(result.next()) {        
              content[i] = result.getString(1);
              System.out.println("content:" + content[i]);
              i++;
            }
          cycleIndex = db.readnum();
          System.out.println("readnum:" + cycleIndex);
          db.close(); 
          }
            //Thread.sleep(500);
          //if(!result.next()) {
            //数据库检索完毕关闭弹幕
            //System.out.println(content = "输出完毕");
            //restart();
            //System.out.println("系统结束：" + cancel());
          //}
     return null;
        }   
      };
    }
    
    protected void canceled() {
      super.cancelled();
      System.out.println("结束");
    }
    
//    @Override
//    protected void failed() {
//      super.failed();
//      System.out.println("失败");
//    }
    

    //每次Task执行完后输出一条弹幕
    @Override
    protected void succeeded() {
      super.succeeded();
      System.out.println("succeed");
      int i = 0; 
      while((content[i] != null) && (i < cycleIndex)) {
        
        //给标签添加文字，第一轮进行初始化
        if(numLab < 99) {
          label[numLab] = new Label();
          //字体名称，粗细，字形，大小
          label[numLab].setFont(Font.font("黑体",FontWeight.BOLD,FontPosture.REGULAR,25));
          //标签透明
          //label[i].setStyle("-fx-background: transparent;");
          label[numLab].setTextFill(Color.BLACK);
          numLab++;
        }
        
          if(content[i] != null) {
            label[num].setText(content[i]);
            pane.getChildren().add(label[num]);  
            
            //创建动画(持续时间，轨迹，node)
            switch(num%4) {
            case 0:{
              //System.out.println("0");
              //增加随机距离
              double length = Math.random() * 250;
              //着重是使每个标签的速率相等
              pt[num] = new PathTransition(new Duration((length+screen.getMaxX()+400)*5000/screen.getMaxX()),
                  new Line(screenX + length,changedY[0],
                  endX,changedY[0]),label[num]);      
              pt[num].play();
              break;
            }
            case 1:{
              //System.out.println("1");
              double length = Math.random() * 250;
              pt[num] = new PathTransition(new Duration((length+screen.getMaxX()+400)*5000/screen.getMaxX()),
                  new Line(screenX + length,changedY[1],
                  endX,changedY[1]),label[num]);      
              pt[num].play();
              break;
            }
            case 2:{
              //System.out.println("2");
              double length = Math.random() * 250;
              pt[num] = new PathTransition(new Duration((length+screen.getMaxX()+400)*5000/screen.getMaxX()),
                  new Line(screenX + length,changedY[2],
                  endX,changedY[2]),label[num]);      
              pt[num].play();
              break;
            }
            case 3:{
              //System.out.println("3");
              double length = Math.random() * 250;
              pt[num] = new PathTransition(new Duration((length+screen.getMaxX()+400)*5000/screen.getMaxX()),
                  new Line(screenX + length,changedY[3],
                  endX,changedY[3]),label[num]);      
              pt[num].play();
              break;
            }
            }
            
            if((++num) == 101)num = 0;
            content[i] = null;
          }
       i++; 
      }
    }
    
  };
  
  
  
}
