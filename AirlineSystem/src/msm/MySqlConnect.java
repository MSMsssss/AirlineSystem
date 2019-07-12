package msm;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class MySqlConnect {
	//�����������ݿ�URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
    static final String DB_URL = "jdbc:mysql://localhost:3306/airlineinfo?useUnicode=true&"
    		+ "characterEncoding=GBK";
    
    // ���ݿ���û��������룬��Ҫ�����Լ�������
    static final String USER = "root";
    static final String PASS = "";
    
	public Connection connect = null;
    private Statement state = null;
    
    public MySqlConnect()
    {
        try{
            // ע�� JDBC ����
            Class.forName(JDBC_DRIVER);
        
            // ������
            connect = DriverManager.getConnection(DB_URL,USER,PASS);
        
            // ִ�в�ѯ
            state = connect.createStatement();
            state.executeUpdate("set global max_write_lock_count = 1;");//���ö�д����Ȩ
        }catch(SQLException se){
            // ���� JDBC ����
            se.printStackTrace();
        }catch(Exception e){
            // ���� Class.forName ����
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

