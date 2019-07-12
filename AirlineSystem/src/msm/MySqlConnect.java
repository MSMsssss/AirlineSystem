package msm;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class MySqlConnect {
	//驱动名与数据库URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
    static final String DB_URL = "jdbc:mysql://localhost:3306/airlineinfo?useUnicode=true&"
    		+ "characterEncoding=GBK";
    
    // 数据库的用户名与密码，需要根据自己的设置
    static final String USER = "root";
    static final String PASS = "";
    
	public Connection connect = null;
    private Statement state = null;
    
    public MySqlConnect()
    {
        try{
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);
        
            // 打开链接
            connect = DriverManager.getConnection(DB_URL,USER,PASS);
        
            // 执行查询
            state = connect.createStatement();
            state.executeUpdate("set global max_write_lock_count = 1;");//设置读写优先权
        }catch(SQLException se){
            // 处理 JDBC 错误
            se.printStackTrace();
        }catch(Exception e){
            // 处理 Class.forName 错误
            e.printStackTrace();
        }
    }
    
    protected void finalize()
    {
        try{
            if(state!=null) state.close();
        }catch(SQLException se2){
        }
        try{
            if(connect!=null) connect.close();
        }catch(SQLException se){
            se.printStackTrace();
        }
    }
    
    public int executeUpdate(String command)
    {
    	try {
			return state.executeUpdate(command);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
    }
    
    public ResultSet executeQuery(String command)
    {
    	try {
			return state.executeQuery(command);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}    
    }

    
    public static void main(String[] args) {
    	ArrayList<Integer> list = new ArrayList<Integer>();
    	list.add(new Integer(100));
    	
    	System.out.print(list.contains(100));
    }
}

