package main;
import db.DBmanager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.naming.InterruptedNamingException;

import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
/*littlemotor
 * 18.4.17
 * 按照生产者消费者模式开发个多线程应用，
 * 由于javafx的ui只能运行于主线程，所以之后用异步编程重新修改*/
public class ProducerConsumer {
  static Resources resourse;
  
  //有参构造函数，初始化参数
  ProducerConsumer(Pane pane,ResultSet result,int rowNum) {
    resourse = new Resources(pane,result,rowNum);
    ExecutorService executor = Executors.newFixedThreadPool(5);
    executor.execute(new Producer());
    executor.execute(new Consumer());
    
    
  }
  
  
  //生产者线程
  private static class Producer implements Runnable {

    @Override
    public void run() {
          
            
            int i = 0;
            while(true) {
            resourse.Push(i);
            try {
              Thread.sleep(1000);
            } catch (InterruptedException e) {
              // TODO 自动生成的 catch 块
              e.printStackTrace();
            }
            if((++i) == 4)i = 0;
            }
            
              //更新JavaFX的主线程的代码放在此处
          }
     
        //线程
        
      }
    
    
 
  //消费者线程
  private static class Consumer implements Runnable {

  @Override
  public void run() {
    while(true) {
      //线程
      resourse.Pull();
    }   
  }  
 }   
}

//对资源的具体操作
class Resources {
  private Lock lock = new ReentrantLock();
  private Condition nullCondition = lock.newCondition();
  private Condition fullCondition = lock.newCondition();
  
  private Pane pane;
  private ResultSet result;
  private int rowNum;
  private String content = null;
  
  private Label[] label = new Label[100];
  
  private final Duration duration = new Duration(5000);//创建持续时间
  Timer timer;
  
  //创建轨迹,后期处理，按照当前数据的大小改变行距
  private double startX,endX;
  private double[] oneOfFourY = new double[4];//new double[4];//存储起始边界
  private Line[] fourLine = new Line[4];
  private PathTransition[] pt = new PathTransition[100]; 
  
  //构造函数，初始化参数
  Resources(Pane pane,ResultSet result,int rowNum) {
    this.pane = pane;
    this.result = result;
    this.rowNum = rowNum;
    //初始化轨迹
    //检查屏幕边界
    Rectangle2D screen = Screen.getPrimary().getBounds();
    startX = screen.getMaxX();
    endX = 0;
    double maxY = screen.getMaxY();
    //分别初始化四条 四分之一轨迹
    double section1 = ((maxY / 4 * 3) / 5);
    for(int i = 0; i < 4; i++) {
      oneOfFourY[i] = (maxY / 8 + (i + 1) * section1);
      //注意初始化啊！
      fourLine[i] = new Line();      
      //System.out.println(oneOfFourY[i]);
      fourLine[i].setStartX(startX);
      fourLine[i].setStartY(oneOfFourY[i]);
      fourLine[i].setEndX(endX);
      fourLine[i].setEndY(oneOfFourY[i]);
      System.out.println("轨迹初始化成功");
    }
    //测试线
    pane.getChildren().add(fourLine[0]);
    pane.getChildren().add(fourLine[1]);
    pane.getChildren().add(fourLine[2]);
    pane.getChildren().add(fourLine[3]);
  }
  
  //发送弹幕
  public void Push(int i) {
    //加锁
    lock.lock();
    try {
      //判断
      while(content == null) {
        nullCondition.await();
      }
      
      //执行任务,释放信号量
         
      //给标签添加文字
      label[i] = new Label();
      //字体名称，粗细，字形，大小
      label[i].setFont(Font.font("黑体",FontWeight.BOLD,FontPosture.REGULAR,25));
      //标签透明
      //label[i].setStyle("-fx-background: transparent;");
      label[i].setTextFill(Color.WHITE);
      label[i].setText(content); 
      pane.getChildren().add(label[i]);  
      //创建动画(持续时间，轨迹，node)
      pt[i] = new PathTransition(duration,fourLine[i%4],label[i]);       
      pt[i].play();
      
      content = null;
      fullCondition.signal();//让Pull解除nullCondition等待
    }catch(InterruptedException e) {e.printStackTrace();}   
    //解锁
    finally {
      
      lock.unlock();
    }
  }
  
  
  
  
  //存放数据
  public void Pull() {
    //加锁
    lock.lock();
    try { 
      //判断
      while(content != null) {
        fullCondition.await();
      }
      
      //执行任务 释放信号量(执行任务放在了finally)
      if(result.next())content = result.getString(1);
      nullCondition.signal();//这个是为了让Push的接触nullCondition等待
      
    }catch(InterruptedException | SQLException e) {e.printStackTrace();} 
    
    //解锁
    finally {
      lock.unlock();
      
    }
  }
}

