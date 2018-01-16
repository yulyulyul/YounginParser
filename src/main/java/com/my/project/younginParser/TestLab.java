package com.my.project.younginParser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
		open();
		String title = "격기지도학과,경영정보학과,경영학과,경영학과_야,경찰행정학과,경호학과,골프학과,교양1영역,교양2영역,교양3영역,교양4영역,교양5영역,교양6영역,교양7영역,교양필수,교직,국악과,군사학,군사학과,동양무예학과,무도대학_기초전공,무용과,문화관광학과,문화관광학과_야,문화재학과,문화콘텐츠학과,물류통계정보학과,물리치료학과,미디어디자인학과,뷰티케어학과,뷰티헬스케어학과,사회복지학과,산업환경보건학과,생명과학과,스포츠레저학과,식품영양학과,실용음악과,연극학과,영어과,영화영상학과,유도경기지도학과_Ⅰ,유도경기지도학과_Ⅱ,유도학과_Ⅰ,유도학과_Ⅱ,중국학과,체육과학대학_기초전공,체육학과,체육학과_야,컴퓨터과학과,태권도경기지도학과,태권도학과_Ⅰ,태권도학과_Ⅱ,특수체육교육과,학군사관_후보생_전용강좌,환경학과,회화학과";
		StringBuilder sb = new StringBuilder();
		String[] aa = title.split(",");
		
		for (String str : aa) 
		{
			
		}
	}

	public static PreparedStatement CompletePrep(PreparedStatement prep, int title_arr_length, String time, String date, String Timeterm)
	{
		for (int i = 1; i <= title_arr_length*4; i++) 
		{
			int switchInt = i%4;
			
			try 
			{
				switch(switchInt)
				{
				case 1:
					prep.setString(i, time);
					break;
				case 2:
					prep.setString(i, time);
					break;
				case 3:
					prep.setString(i, Timeterm);
					break;
				case 0:
					prep.setString(i, date);
					break;
				}
			} 
			catch (Exception e) 
			{
				try 
				{
					prep.close();
				}
				catch (SQLException e2) 
				{
					e2.printStackTrace();
				}
				e.printStackTrace();
			}
		}
		return prep;
	}
	
	public static String getQuery(String[] titleArr)
	{
		StringBuilder sb = new StringBuilder();
		
		for (String str : titleArr) 
		{
			sb.append("SELECT * FROM " + str +" WHERE (startTime Between time(?) and time(?,?)) AND (date=>) UNION ALL ");
		}
//		System.out.println(sb.toString());
		String sb_to_query = sb.toString();
		String query = sb_to_query.substring(0, sb_to_query.lastIndexOf(" UNION ALL"));
		query = query + ";";
//		System.out.println(query);
		System.out.println("");
		System.out.println("");
		
		return query;
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
