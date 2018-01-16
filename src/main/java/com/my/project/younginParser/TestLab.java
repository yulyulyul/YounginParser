package com.my.project.younginParser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.sqlite.SQLiteConfig;

public class TestLab 
{
	public final static String DATABASE = "Timetable_data_2017_1.db";
//	public final static String DATABASE = "Timetable_data_2017_2.db";
	private static Connection connection;
	private static String dbFileName;
	private static boolean isOpened = false;
	public static int count = 0;
	
	static
	{
		try
		{
			Class.forName("org.sqlite.JDBC");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) 
	{

	}

	public static boolean open()
	{
		try 
		{
			SQLiteConfig config = new SQLiteConfig();
//			config.setReadOnly(true);
			connection = DriverManager.getConnection("jdbc:sqlite:" + DATABASE);
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			return false;
		}
		
		isOpened = true;
		return true;
	}
	
	public static boolean close()
	{
		if(isOpened == false)
		{
			return true;
		}
		try
		{
			connection.close();
		} catch (SQLException e) 
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
}
