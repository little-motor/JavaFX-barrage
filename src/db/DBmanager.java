package db;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
/*
  创建日期：18.4.14
  功能：数据库管理 
*/
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class DBmanager {
  public int rowNum = 0,readNum = 0;
  File file;
  private Connection connect;
  private PreparedStatement contentStatement,testStatement;
  private ResultSet content,test;
  
  //sql语言
  private String sqlSearchContent = "select content " +  //查询未读内容;?不加引号
      " from microposts " +
      " where id >= ? and id <= ?";
  
  
  //初始化，根据状态数据库上次读取的结束位置起，向后读取四个数据
  public DBmanager() throws ClassNotFoundException {	  
	  try {
	    //加载驱动
  		Class.forName("org.sqlite.JDBC");
  		
  		//System.out.println("本地数据库连接成功");
  		connect = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\littlemotor"
          + "\\Apply\\test\\development.sqlite3");
  		
  		file = new File("c:\\Users\\littlemotor\\Desktop\\rsync\\num.txt");
  		try(
  		    Scanner input = new Scanner(new FileInputStream(file));)
  		{ 		  
  		  rowNum = Integer.parseInt(input.next());
  		} catch (FileNotFoundException e) {
        // TODO 自动生成的 catch 块
        e.printStackTrace();
      } catch (IOException e) {
        // TODO 自动生成的 catch 块
        e.printStackTrace();
      } finally {
        //file.delete();
      }
  	  
      System.out.println("本地读取行数：" + rowNum);
      
	} catch (SQLException e) {
		
		e.printStackTrace();
	}
  }
  
  //返回搜索结果
  public ResultSet getContent(){
    try {
      
      //检索新的数据
      contentStatement = connect.prepareStatement(sqlSearchContent);
      contentStatement.setInt(1,rowNum + 1);
      contentStatement.setInt(2,rowNum + 4);
      content = contentStatement.executeQuery();
      
      //计算实际读取数
      testStatement = connect.prepareStatement(sqlSearchContent);
      testStatement.setInt(1,rowNum + 1);
      testStatement.setInt(2,rowNum + 4);
      test = testStatement.executeQuery();
      
      //更新本地行号
      while(test.next()) {
        readNum++;
      }
      
      try(PrintWriter output = new PrintWriter(new FileOutputStream(file)))
      {
        output.println(rowNum + readNum);
      } catch (FileNotFoundException e) {
        // TODO 自动生成的 catch 块
        e.printStackTrace();
      } catch (IOException e) {
        // TODO 自动生成的 catch 块
        e.printStackTrace();
      }
      
    } catch (SQLException e) {
      close();
      File sql = new File("c:\\Users\\littlemotor\\Apply\\test\\development.sqlite3");
      sql.delete();
      e.printStackTrace();
    }
    return content;
  }
   
  //关闭数据库的正确方式
  public void close() {
    try {
      if(content != null) {
        content.close();
        content = null;
      }
      if(test != null) {
        test.close();
        test = null;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if(contentStatement != null)
          {
          contentStatement.close();
          contentStatement = null;
          }
        if(testStatement != null)
        {
          testStatement.close();
          testStatement = null;
        }
      } catch (SQLException e) {
        e.printStackTrace();
      } finally {
        try {
          if(connect != null) {
            connect.close();
            connect = null;
          }
        } catch (SQLException e) {
          e.printStackTrace();
        } finally {
          System.out.println("数据库关闭成功");
        }
      }
     
    }
  }
  
  public int readnum() {
    return readNum;
  }
  //主函数用于测试
//  public static void main(String[] args) {
//    try {
//      DBmanager db = new DBmanager();
//      ResultSet result = db.getContent();
//      while(result.next()) {
//        
//        System.out.print(result.getString(1));
//        System.out.println(db.rowNum);
//      } 
//      //判断是否为最后一行，修改状态
//      //if(result.isAfterLast())db.setState();
//      
//    } catch (ClassNotFoundException | SQLException e) {
//      
//      e.printStackTrace();
//    }
//  }
}
