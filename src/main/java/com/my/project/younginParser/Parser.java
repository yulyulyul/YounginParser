package com.my.project.younginParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Parser 
{
	static String regType1 = "[^·][1-4{1}]\\r\\n\\W{2}\\r\\n\\d{6}-\\d{2}\\r\\n.*\\r\\n\\d\\(.*\\)\\r\\n.*\\r\\n\\W\\d{2}:.*\\r\\n\\W-.*";
	/*
	 * 교수 1, 장소 1
	 */
	static String regType2 = "([1-4]{1}·[1-4]{1})\\r\\n\\W{2}\\r\\n\\d{6}-\\d{2}\\r\\n.*\\r\\n\\d\\(.*\\)\\r\\n.*\\r\\n\\W\\d{2}:.*\\r\\n\\W-.*";
	/*
	 * (3,4학년) 교수 1, 장소 1
	 */
	static String regType3 = "[^·][1-4{1}]\\r\\n\\W{2}\\r\\n.*\\r\\n\\d{6}-\\d{2}\\r\\n.*\\r\\n\\d\\(.*\\r\\n.*\\r\\n\\W\\d{2}:.*\\r\\n\\W-.*\\r\\n.*\\r\\n\\W\\d{2}:.*\\r\\n\\W-.*";
	/*
	 * (훈련단) 교수 2, 장소 2
	 */	
	static String regType4 = "([1-4]{1}·[1-4]{1})\\r\\n\\W{2}\\r\\n.*\\r\\n\\d{6}-\\d{2}\\r\\n.*\\r\\n\\d\\(.*\\r\\n.*\\r\\n\\W\\d{2}:.*\\r\\n\\W-.*\\r\\n.*\\r\\n\\W\\d{2}:.*\\r\\n\\W-.*";
	/*
	 * (3,4학년 // 훈련단) 교수 2, 장소 2
	 */
	static String regType5 = "[^·][1-4{1}]\\r\\n\\W{2}\\r\\n.*\\r\\n\\d{6}-\\d{2}\\r\\n.*\\r\\n\\d\\(.*\\r\\n.*\\r\\n\\W\\d{2}:.*\\r\\n\\W-.*\\r\\n\\W\\d{2}:.*\\r\\n\\W-.*";
	/*
	 * (훈련단) 교수 1, 장소 2
	 */
	static String regType6 = "([1-4]{1}·[1-4]{1})\\r\\n\\W{2}\\r\\n.*\\r\\n\\d{6}-\\d{2}\\r\\n.*\\r\\n\\d\\(.*\\r\\n.*\\r\\n\\W\\d{2}:.*\\r\\n\\W-.*\\r\\n\\W\\d{2}:.*\\r\\n\\W-.*";
	/*
	 * (3,4학년 // 훈련단) 교수 1, 장소 2
	 */
	static String regType7 = "[^·][1-4{1}]\\r\\n\\W{2}\\r\\n\\d{6}-\\d{2}\\r\\n.*\\r\\n\\d\\(.*\\r\\n.*\\r\\n\\W\\d{2}:.*\\r\\n\\W-.*\\r\\n\\W\\d{2}:.*\\r\\n\\W-.*";
	/*
	 * 교수 1, 장소 2
	 */
	static String regType8 = "([1-4]{1}·[1-4]{1})\\r\\n\\W{2}\\r\\n\\d{6}-\\d{2}\\r\\n.*\\r\\n\\d\\(.*\\r\\n.*\\r\\n\\W\\d{2}:.*\\r\\n\\W-.*\\r\\n\\W\\d{2}:.*\\r\\n\\W-.*";
	
	static String regType9 = "[^·][1-4{1}]\\r\\n\\W{2}\\r\\n\\d{6}-\\d{2}\\r\\n.*\\r\\n.*\\r\\n\\d\\(.*\\)\\r\\n.*\\r\\n\\W\\d{2}:.*\\r\\n\\W-.*";
	/*
	 *  1
		교선
		920165-01
		한국문화와한국어이해1
		(외국인대상강좌)
		3(30)
		김미옥
		수09:25-12:10
		무-12304(12304)
		
		외국인대상강좌 <- 처럼 강의명에 개행문자가 들어가서 나누어진것.
	 */
	static String regType10 = "[^·][1-4{1}]\\r\\n\\W{2}\\r\\n\\d{6}-\\d{2}\\r\\n.*\\r\\n\\d\\(.*\\)\\r\\n.*\\r\\n\\W\\W.*\\r\\n\\W-.*";
	/*
	 *  2
		전공
		200081-05
		야외교육
		3(50)
		윤익선
		윤익선
		무-유도실기장1(12422)
		
		장소가 없고 교수1 다만 위의 경우 장소가 비어서 교수가 텍스트상 2명이 되었음..
	 */
	
	static String regType11 = "[^·][1-4{1}]\\r\\n\\W{2}\\r\\n\\d{6}-\\d{2}\\r\\n중국.*\\(현지\\)\\r\\n\\d\\(.*\\)";
	/*
	 * 중국어학과 예외처리..
	 * 
	 *  2
		전공
		470089-82
		중국어종합(현지)
		2(50)
	 */
	
	static String regType12 = "([1-4]{1}·[1-4]{1})\\r\\n\\W{2}\\r\\n\\d{6}-\\d{2}\\r\\n.*\\r\\n\\d\\(.*\\)\\r\\n.*\\r\\n\\W\\W.*\\r\\n\\W-.*";
	/*
	 *  3·4
		전공
		240025-13
		승마
		2(50)
		전형상
		전형상
		무-무도세미나실(12101)
		
		장소가 없고 교수1 다만 위의 경우 장소가 비어서 교수가 텍스트상 2명이 되었음.. -- 3,4학년 전용
	 */
	
	
	public static void main(String[] args) throws Exception 
	{
		String data = readFileAsString("timeSchedule.txt");
//		String data = readFileAsString("timetable.txt");
		StringBuilder FILE_sb = new StringBuilder();
		
		data = fun_del(data); // *로 시작하는 문장 제거.
		/*
		 * 최종적으로 파싱에 사용할 timeSchedule.txt의 형태
		 */
		System.out.println(data);
		saveTxtFile(data,"timeScheduleParsingData.txt");
		
		String testReg = "([^-][1-4]{1}\\r\\n\\W{2}\\r\\n\\d{6}-\\d{2})";
		
		ArrayList<String> numPat1 = new ArrayList<String>();
		ArrayList<String> numPat2 = new ArrayList<String>();
		ArrayList<String> numPat3 = new ArrayList<String>();
		ArrayList<String> numPat4 = new ArrayList<String>();
		ArrayList<String> numPat5 = new ArrayList<String>();
		ArrayList<String> numPat6 = new ArrayList<String>();
		ArrayList<String> numPat7 = new ArrayList<String>();
		ArrayList<String> numPat8 = new ArrayList<String>();
		ArrayList<String> numPat9 = new ArrayList<String>();
		ArrayList<String> numPat10 = new ArrayList<String>();
		ArrayList<String> numPat11 = new ArrayList<String>();
		ArrayList<String> numPat12 = new ArrayList<String>();
		
		
		
		ArrayList<TimeTable> Pat1 = new ArrayList<TimeTable>();
		ArrayList<TimeTable> Pat2 = new ArrayList<TimeTable>();
		ArrayList<TimeTable> Pat3 = new ArrayList<TimeTable>();
		ArrayList<TimeTable> Pat4 = new ArrayList<TimeTable>();
		ArrayList<TimeTable> Pat5 = new ArrayList<TimeTable>();
		ArrayList<TimeTable> Pat6 = new ArrayList<TimeTable>();
		ArrayList<TimeTable> Pat7 = new ArrayList<TimeTable>();
		ArrayList<TimeTable> Pat8 = new ArrayList<TimeTable>();
		ArrayList<TimeTable> Pat9 = new ArrayList<TimeTable>();
		ArrayList<TimeTable> Pat10 = new ArrayList<TimeTable>();
		ArrayList<TimeTable> Pat11 = new ArrayList<TimeTable>();
		ArrayList<TimeTable> Pat12 = new ArrayList<TimeTable>();
		
		
		
		
		HashMap<Integer, ArrayList<TimeTable>> checkLoopMap = new HashMap<Integer, ArrayList<TimeTable>>();
		
		Pattern pattern1 = Pattern.compile(regType1);
		Pattern pattern2 = Pattern.compile(regType2);
		Pattern pattern3 = Pattern.compile(regType3);
		Pattern pattern4 = Pattern.compile(regType4);
		Pattern pattern5 = Pattern.compile(regType5);
		Pattern pattern6 = Pattern.compile(regType6);
		Pattern pattern7 = Pattern.compile(regType7);
		Pattern pattern8 = Pattern.compile(regType8);
		Pattern pattern9 = Pattern.compile(regType9);
		Pattern pattern10 = Pattern.compile(regType10);
		Pattern pattern11 = Pattern.compile(regType11);
		Pattern pattern12 = Pattern.compile(regType12);
		
		
		
		
		
		Matcher m1 = pattern1.matcher(data); //840개 ==> 중복된 데이터 75개를 지우면 ==> 765개
		Matcher m2 = pattern2.matcher(data); //401개 ==> 중복된 데이터 31개를 지우면 ==> 370개
		Matcher m3 = pattern3.matcher(data); //96개
		Matcher m4 = pattern4.matcher(data); //0개
		Matcher m5 = pattern5.matcher(data); //48개
		Matcher m6 = pattern6.matcher(data); //0개
		Matcher m7 = pattern7.matcher(data); //75개
		Matcher m8 = pattern8.matcher(data); //31개
		Matcher m9 = pattern9.matcher(data); //2개
		Matcher m10 = pattern10.matcher(data); //25개 
		Matcher m11 = pattern11.matcher(data); //5개
		Matcher m12 = pattern12.matcher(data); //
		
		
		
		
		/*
		 * 총 강의수 : 1452	=> 1448개 [중복된 값(4개) 제거]		지금까지 파싱한 데이터의 총 개수 : 1408개			남은 데이터의 수 : 40개
		 */
		
//		numPat1 = fun_regType_num(m1);
//		numPat2 = fun_regType_num(m2);
//		numPat3 = fun_regType_num(m3);
//		numPat4 = fun_regType_num(m4);
//		numPat5 = fun_regType_num(m5);
//		numPat6 = fun_regType_num(m6);
//		numPat7 = fun_regType_num(m7);
//		numPat8 = fun_regType_num(m8);
//		numPat9 = fun_regType_num(m9);
//		numPat10 = fun_regType_num(m10);
//		numPat11 = fun_regType_num(m11);
//		numPat12 = fun_regType_num(m12);
		
		
		
		
//		Pat1  = fun_regType(m1,  Pat1,1);
//		Pat2  = fun_regType(m2,  Pat2,2);
//		Pat3  = fun_regType(m3,  Pat3,3);
//		Pat4  = fun_regType(m4,  Pat4,4);
//		Pat5  = fun_regType(m5,  Pat5,5);
//		Pat6  = fun_regType(m6,  Pat6,6);
//		Pat7  = fun_regType(m7,  Pat7,7);
//		Pat8  = fun_regType(m8,  Pat8,8);
//		Pat9  = fun_regType(m9,  Pat9,9);
//		Pat10 = fun_regType(m10, Pat10,10);
//		Pat11 = fun_regType(m11, Pat11,11);
//		Pat12 = fun_regType(m12, Pat12,12);
//		
//		
//		
//		Pat1 = delDoubleValueInArrayList(Pat1, "Pat1");
//		Pat2 = delDoubleValueInArrayList(Pat2, "Pat2");
//		Pat3 = delDoubleValueInArrayList(Pat3, "Pat3");
//		Pat4 = delDoubleValueInArrayList(Pat4, "Pat4");
//		Pat5 = delDoubleValueInArrayList(Pat5, "Pat5");
//		Pat6 = delDoubleValueInArrayList(Pat6, "Pat6");
//		Pat7 = delDoubleValueInArrayList(Pat7, "Pat6");
//		Pat8 = delDoubleValueInArrayList(Pat8, "Pat8");
//		Pat9 = delDoubleValueInArrayList(Pat9, "Pat9");
//		Pat10 = delDoubleValueInArrayList(Pat10, "Pat10");
//		Pat11 = delDoubleValueInArrayList(Pat11, "Pat11");
//		Pat12 = delDoubleValueInArrayList(Pat12, "Pat12");
		
		
		
		/*
		 * 중복 값이 있는지 검사하기 위하여 checkLoopMap에 모든 데이터를 넣는다.
		 */
		checkLoopMap.put(0, Pat1);
		checkLoopMap.put(1, Pat2);
		checkLoopMap.put(2, Pat3);
		checkLoopMap.put(3, Pat4);
		checkLoopMap.put(4, Pat5);
		checkLoopMap.put(5, Pat6);
		checkLoopMap.put(6, Pat7);
		checkLoopMap.put(7, Pat8);
		checkLoopMap.put(8, Pat9);
		checkLoopMap.put(9, Pat10);
		checkLoopMap.put(10, Pat11);
		checkLoopMap.put(11, Pat12);
		
		
		
		/*
		 * 파싱한 데이터를 시각화하고 텍스트 파일로 저장함.
		 */
//		FILE_sb.append(printTimeTable(Pat1));
//		FILE_sb.append(printTimeTable(Pat2));
//		FILE_sb.append(printTimeTable(Pat3));
//		FILE_sb.append(printTimeTable(Pat4));
//		FILE_sb.append(printTimeTable(Pat5));
//		FILE_sb.append(printTimeTable(Pat6));
//		FILE_sb.append(printTimeTable(Pat7));
//		FILE_sb.append(printTimeTable(Pat8));
//		FILE_sb.append(printTimeTable(Pat9));
//		FILE_sb.append(printTimeTable(Pat10));
//		FILE_sb.append(printTimeTable(Pat11));
//		FILE_sb.append(printTimeTable(Pat12));
//		saveTxtFile(FILE_sb.toString(),"lecList.txt");
//		
//		/*
//		 * checkLoopMap에 모든 데이터를 때려넣고 
//		 * 중복 값이 있는지 검사.
//		 */
//		System.out.println("==================== 1 ====================");
//		checkLoopMap = checkHashMapDoubledata(checkLoopMap);
//		/*
//		 * 이중 체크!
//		 */
//		System.out.println("==================== 2 ====================");
//		checkLoopMap = checkHashMapDoubledata(checkLoopMap);
//		
//		
//		/*
//		 * 데이터의 학수번호 정보를 하나의 ArrayList에 저장하여 check_miss_data에 넘겨주어 
//		 * 무엇이 빠졌나 대조를 시작함.
//		 * 
//		 * 파싱하지 못한 데이터 출력 성공! (오로지 출력용임.)
//		 */
//		ArrayList<String> lec_List = new ArrayList<String>();
//		lec_List = putLecNumber(checkLoopMap);
//		check_miss_data(data, lec_List);
		
		
	}
	
	/*
	 * 리스트 자체에 중복된 값이 있는지 검사하고 제거.
	 */
	public static ArrayList<TimeTable> delDoubleValueInArrayList(ArrayList<TimeTable> arr,String str)
	{
		System.out.println(str + " : " + arr.size());
		
		boolean Flag = false;
		boolean Flag2 = false;
		
		for (int i = 0; i < arr.size(); i++) 
        {
			if(Flag == true)
			{
//				System.out.println("Flag = true");
				i-=1;
				Flag = false;
			}
			if(Flag2 == true)
			{
//				System.out.println("Flag2 = true");
				i=0;
				Flag2 =false;
			}
			
			for(int j = 0; j< arr.size(); j++)
			{
				if ((i!=j) && (arr.get(i).getLec_number().equals(arr.get(j).getLec_number()))) 
	            {
					if(i != 0)
					{
//						System.out.println("i != 0");
						arr.remove(j);
						j = arr.size();
						Flag = true;
					}
					else if(i == 0)
					{
//						System.out.println("i == 0");
						Flag2 = true;
					}
	            }
			}
        }
		
		System.out.println("결과 : " + arr.size()+"\n");
		
		return arr;
	}
	
	/*
	 * Main에서 만든 lec_List에 checkLoopMap에 있는 모든 학수번호를 넣기 위해!!  
	 */
	public static ArrayList<String> putLecNumber(HashMap checkLoopMap)
	{	
		ArrayList<String> result = new  ArrayList<String>();
		for(int i = 0; i < checkLoopMap.size(); i++)
		{
			
			for(int j = 0; j< ((ArrayList)checkLoopMap.get(i)).size(); j++)
			{
				result.add(((ArrayList<TimeTable>)checkLoopMap.get(i)).get(j).getLec_number());
			}
		}
		
		return result;
	}
	
	/*
	 * 전체 총 강의의 수를 구한다.
	 * 여태까지 찾았던 데이터랑 비교하여 무엇이 빠졌는지를 체크한다.
	 */
	public static void check_miss_data(String data, ArrayList<String> existed)
	{
		int cnt = 0;
		int iIsZero = 0;
		int iIsNotZero = 0;
		
		ArrayList<String> lec_list = new ArrayList<String>();
		ArrayList<String> finalLecList = new ArrayList<String>();
		
		String lec_num = "(\\d{6}-\\d{2})";
		Pattern pat_lec_num = Pattern.compile(lec_num);
		Matcher lec = pat_lec_num.matcher(data);
		
		while(lec.find())
		{
			lec_list.add(lec.group());
		}
		
		 for (int i = 0; i < lec_list.size(); i++) 
         {
             if (!finalLecList.contains(lec_list.get(i))) 
             {
            	 finalLecList.add(lec_list.get(i));
             }
         }
		 
//		System.out.println("Total lecture : " + lec_list.size());
		System.out.println("Total lecture : " + finalLecList.size());
		
		lec_list = finalLecList;
		final int totalLec = finalLecList.size();
		/*
		 * 전체 학수번호 출력
		 */
//		for (int i = 0; i < lec_list.size(); i++) 
//		{
//			System.out.println(lec_list.get(i));
//		}
		/*
		 * 전체 강의에서 파싱한 데이터를 뺀다.
		 * 
		 * 진행 도중 데이터가 학수번호임에도 불구하고 같은 데이터가 존재함에 충격을 받음...
		 * 따라서 일단 하던거 중지하고 같은 arraylist에 있는 데이터라도 겹치는 데이터가 존재하는지 다시 한번 확인해야하는 루틴을 만들어야겠음 => 해결함
		 * 약 12개가 중복됨... => 조사해보니 4개였음.
		 * 
		 * 
		 * 
		 * 아래의 연산 설명
		 * 1. 첫번째로 lec_list(전체 강의)에 있는 모든 데이터와 existed(파싱한 데이터)에 존재하는 데이터를 비교한 후
		 *	  같은 데이터라면 지워주는 연산임.
		 *
		 * 2. flag는 i가 0일 경우 겹치는 데이터가 존재해서 i-=1을 해주면 i가 -1이 되어버리므로 ArrayIndexOutOfBoundsException이 발생함.
		 *    따라서 일단 0일때는 existed을 한바퀴 돌려주고 flag가 true면 다시 i를 0으로 만들어줘서 다시한번 확인하게 만드는 용도임.
		 *    
		 * 3. 겹치는 데이터를 제거하면 남아있는 강의의 수는 67개가 나와야하는데 68개가 나와버려서 점검하다가 
		 *    lec_list의 i가 동일함에도 existed에 여러 겹치는 데이터가 있다는걸 발견함.
		 *    
		 * 4. 위와 같은 데이터를 확인하려고 flag2를 만들어서 처음 lec_list와 existed 사이에 중복되는 데이터가 발생하면 flag2에 true 값을 주고
		 *    또 다시 발생한다면 그 데이터를 캐치하려고 만든 변수임.
		 *    
		 * 5. 중복되는 데이터를 모두 처리릃 함.
		 * 
		 * 6. 중복되는 데이터를 모두 처리했으니 flag2 관련 부분은 전부 주석으로 처리.
		 * 
		 * 7. [결론!] 파싱되지 않은 데이터 67개 뽑아내는데 성공!! 
		 */
	
		
		boolean flag = false;
//		boolean flag2 = false;
		
		for (int i = 0; i < lec_list.size(); i++) 
		{
			if(flag == true)
			{
				i = 0;
				flag = false;
			}
			for (int j = 0; j < existed.size(); j++) 
			{
				try
				{
					if(lec_list.get(i).equals(existed.get(j)))
					{
//						if(flag2 == true)
//						{
//							System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//							System.out.println("lec_list[" + i + "] : " + lec_list.get(i));
//							System.out.println("existed[" + j + "] : " + existed.get(j));
//							System.out.println("");
//						}
//						flag2 = true;
						cnt+=1;
//						System.out.println("lec_list[" + i + "] : " + lec_list.get(i));
//						System.out.println("existed[" + j + "] : " + existed.get(j));
//						System.out.println("cnt : " + cnt);
//						System.out.println("");
						
						if(i==0)
						{
							iIsZero+=1;
							lec_list.remove(i);
							j = existed.size();
							flag = true;
						}
						
						if(i!=0)
						{
							iIsNotZero+=1;
							lec_list.remove(i);
							j = existed.size();
							i-=1;
						}
						
					}					
				}
				catch(ArrayIndexOutOfBoundsException arrayIndexOutOEx)
				{
					System.out.println("!!	arrayIndexOutOEx	!!");
					System.out.println("i : " + i);
					System.out.println("j : " + j);
				}
			}
//			flag2 = false;
		}
		System.out.println("\n\n========= 데이터 제거 후.. =========");
		System.out.println("전체   데이터 개수: " + totalLec);
		System.out.println("파싱한 데이터 개수: " + cnt);
		System.out.println("파싱해야할 데이터 : " + lec_list.size());
		System.out.println("====================================\n");
		
		System.out.println("====== 파싱해야할 데이터 =======");
		for (int i = 0; i < lec_list.size(); i++) 
		{
			System.out.println("[" + i + "] : " + lec_list.get(i));
		}
		
//		System.out.println("cnt : " + cnt);
//		System.out.println("iIsZero : " + iIsZero);
//		System.out.println("iIsNotZero : " + iIsNotZero);
	}
	
	
	public static String printTimeTable(ArrayList<TimeTable> a)
	{
		StringBuilder info = new StringBuilder();
		System.out.println("a의 크기 : " + a.size());
		
		for (int i = 0; i < a.size() ; i++) 
		{ 	System.out.println("======================= [" + i +"] =======================");
			System.out.println("학년 : " + a.get(i).getGrade());
			System.out.println("이수구분 : " + a.get(i).getClassification());
			System.out.println("대상 : " + a.get(i).getTarget());
			System.out.println("학수번호 : " + a.get(i).getLec_number());
			System.out.println("교과목명 : " + a.get(i).getLec_title());
			System.out.println("학점 : " + a.get(i).getScore());
			System.out.println("교수명 : " + a.get(i).getProfessor());
			System.out.println("강의시간 : " + a.get(i).getLec_time());
			System.out.println("강의실 : " + a.get(i).getClassroom());
			
			info.append("======================= [" + i +"] =======================\n");
			info.append("학년 : " + a.get(i).getGrade()+"\n");
			info.append("이수구분 : " + a.get(i).getClassification()+"\n");
			info.append("대상 : " + a.get(i).getTarget()+"\n");
			info.append("학수번호 : " + a.get(i).getLec_number()+"\n");
			info.append("교과목명 : " + a.get(i).getLec_title()+"\n");
			info.append("학점 : " + a.get(i).getScore()+"\n");
			info.append("교수명 : " + a.get(i).getProfessor()+"\n");
			info.append("강의시간 : " + a.get(i).getLec_time()+"\n");
			info.append("강의실 : " + a.get(i).getClassroom()+"\n");
		}
		
		return info.toString();
	}
	
	public static void printArrayList(ArrayList arr)
	{
		for(int a=0; a < arr.size(); a++)
		{
			System.out.println("arr[" + a + "] = " + arr.get(a));
		}
	}
	
	public static HashMap checkHashMapDoubledata(HashMap checkLoopMap)
	{
		ArrayList<ArrayList<TimeTable>> arr = new ArrayList<ArrayList<TimeTable>>();
		
		System.out.println("checkLookMap size : " + checkLoopMap.size());
		for(int i = 0; i < checkLoopMap.size(); i++)
		{
			for(int j = i+1; j< checkLoopMap.size(); j++)
			{
//				System.out.println("[" + i + "][" + j + "] // a.size() = " + ((ArrayList)checkLoopMap.get(i)).size() + " // b.size() = " + ((ArrayList)checkLoopMap.get(j)).size());
				arr = checkList((ArrayList<TimeTable>)checkLoopMap.get(i), (ArrayList<TimeTable>)checkLoopMap.get(j));
				checkLoopMap.replace(i,  arr.get(0));
				checkLoopMap.replace(j,  arr.get(1));
//				System.out.println("arr.get(0).size() = " + arr.get(0).size() + "			 arr.get(1).size() = " + arr.get(1).size());
//				System.out.println("after [" + i + "]["+ j + "] // a.size() = " + ((ArrayList)checkLoopMap.get(i)).size() + " // b.size() = " + ((ArrayList)checkLoopMap.get(j)).size());
//				System.out.println("");
			}
		}
		return checkLoopMap;
	}
	
	public static ArrayList<ArrayList<TimeTable>> checkList(ArrayList<TimeTable> a, ArrayList<TimeTable> b)
	{
		ArrayList<ArrayList<TimeTable>> arr = new ArrayList<ArrayList<TimeTable>>();
		
		int checkSum = 0;
		if(a == null)
		{
			System.out.println("!! a is null !!");
		}
		
		if(b == null)
		{
			System.out.println("!! b is null !!");
		}
		
		for(int i = 0 ; i < a.size(); i++)
		{
			for (int j = 0; j < b.size(); j++) 
			{
				if(a.get(i).getLec_number().equals(b.get(j).getLec_number()))
				{
//					System.out.println("!!!!!!! 중복 데이터 !!!!!!!");
//					System.out.println("a[" + i + "] : " + a.get(i).getLec_number().toString());
//					System.out.println("b[" + j + "] : " + b.get(j).getLec_number().toString());
					//여기가 잘못됨.. i,j 값의 수정이 필요함.. 위에서의 i,j값이 여기서의 i,j 값이 아님..
					/*
					 * 사실 길이(size가 작은 게 쓸데없이 중복된 값이긴해.. 그런데 여기서는 간편하게 .size()를 사용할 수 없어
					 * 왜냐? 다 TimeTable 객체로 만들어버려서 arrayList로 넣어버렸거든..
					 * 그러면 어떻게 해야하지?
					 * stringbuilder로 모든 객체를 다 붙여버린다음에 길이로?
					 * 아니면 애초에 이 함수에 진입할때 flag를 주어서 0과 6일때는 a가 삭제되도록?
					 * 정규식으로 아예 (교수1, 강의1)을 잘라내기.. 전방탐색으로 숫자나 문자 앞까지를 탐색하느걸 시도했는데... 결과는.. 
					 * 내가 못해서인지 뭐인지.. 자꾸 처음 숫자보다 한 없이 작게 나옴.
					 * 
					 * 결론 : json 객체를 조사해서 교수의 수나 강의의 수를 비교하여 큰거를 살리고 작은것을 죽이기로함.
					 */
					
					JSONParser jsonParser = new JSONParser();
					
					try 
					{
						String a_json_professor = a.get(i).getProfessor();
						String b_json_professor = b.get(j).getProfessor();
						JSONObject professor_a = (JSONObject) jsonParser.parse(a_json_professor);
						JSONObject professor_b = (JSONObject) jsonParser.parse(b_json_professor);
//
						String a_json_time = a.get(i).getLec_time();
						String b_json_time = b.get(j).getLec_time();
						JSONObject time_a = (JSONObject) jsonParser.parse(a_json_time);
						JSONObject time_b = (JSONObject) jsonParser.parse(b_json_time);
//						
						String a_json_classroom = a.get(i).getClassroom();
						String b_json_classroom = b.get(j).getClassroom();
						JSONObject classroom_a = (JSONObject) jsonParser.parse(a_json_classroom);
						JSONObject classroom_b = (JSONObject) jsonParser.parse(b_json_classroom);
						
//						System.out.println("professor_a.size() : " + professor_a.size());
//						System.out.println("professor_b.size() : " + professor_b.size());
//						System.out.println("time_a.size() : " + time_a.size());
//						System.out.println("time_b.size() : " + time_b.size());
//						System.out.println("classroom_a.size() : " + classroom_a.size());
//						System.out.println("classroom_b.size() : " + classroom_b.size());
						
						int compareA = professor_a.size() + time_a.size() + classroom_a.size();
						int compareB = professor_b.size() + time_b.size() + classroom_b.size();
						
//						System.out.println("compareA : " + compareA);
//						System.out.println("compareB : " + compareB);
						
						if(compareA > compareB)
						{
//							System.out.println("delte data");
							b.remove(j);
							j-=1;
						}
						if(compareA < compareB)
						{
//							System.out.println("delte data");
							a.remove(i);
							i-=1;
						}
					}
					catch (Exception e) 
					{
						e.printStackTrace();
					}
					
//					if((i==0) && (j==6))
//					{
//						System.out.println("delte data");
//						a.remove(i);
//						j-=1;
//					}
					checkSum+=1;
				}
			}
		}
		
		arr.add(a);
		arr.add(b);
		
		System.out.println("중복된 데이터의 개수 : " + checkSum);
		
		return arr;
	}
	
	/*
	 *  *로 시작하는 문장을 제거
	 *  sp_data로 공백문자를 기준으로 split
	 *  sp_data에서 공백문자 전부 제거
	 *  sp_data에서 길이가 0인 문장 제거
	 *  sp_data에 전부 공백문자를 삽입
	 *  StringBuilder로 전부 append
	 */
	public static String fun_del(String str)
	{
		String result = "";
		String data = str;
		ArrayList<String> sp_data_list = new ArrayList<String>();
		StringBuilder sp_data_list_toSB = new StringBuilder();
		
		data = data.replaceAll("\\*.*\\r\\n", "");
		String[] sp_data = data.split("\\r\\n");
		
		for (int i = 0; i < sp_data.length; i++) 
		{
			sp_data[i] = sp_data[i].replaceAll("^\\s", "");
			sp_data_list.add(sp_data[i]);
		}

//		System.out.println("sp_data_list.size() : " + sp_data_list.size());
		
		int cnt = 0;
		for (int i = 0; i < sp_data_list.size(); i++) 
		{
//			System.out.println(sp_data_list.get(i));
			if(sp_data_list.get(i).length() == 0)
			{
				sp_data_list.remove(i);
				i-=1;
				cnt+=1;
			}
			else
			{
				sp_data_list_toSB.append(sp_data_list.get(i)+"\r\n");
			}
		}
//		System.out.println("sp_data_list.size() : " + sp_data_list.size());
//		System.out.println("cnt : " + cnt);
//		System.out.println("==== result ====");
//		for (int i = 0; i < sp_data_list.size(); i++) 
//		{
//			System.out.println(sp_data_list.get(i) + "				length = " + sp_data_list.get(i).length());
//		}
//		System.out.println("==== end ====");
		
		
		result = sp_data_list_toSB.toString();
//		System.out.println(sp_data_list_toSB.toString());
		
		return result;
	}
	
	public static ArrayList<TimeTable> fun_regType(Matcher m, ArrayList pat, int p)
	{
		int cnt = 0;
		String data;
		while(m.find())
		{
			data = m.group();
			
			String[] toObject = data.split("\\n");
//			System.out.println("toObject.length = " + toObject.length);
			for (int i = 0; i < toObject.length; i++) // //n, //r 등 특수문자 제거
			{
				toObject[i] = toObject[i].replaceAll("\\s", "");
			}
//			System.out.println("size : " + toObject.length);
			
			JSONObject professor = new JSONObject();
			JSONObject time = new JSONObject();
			JSONObject classroom = new JSONObject();
			
			switch (p) 
			{
			
			case 1: //type1
//				System.out.println("case 9");
				professor.put(1, toObject[6]);
				time.put(1, toObject[7]);
				classroom.put(1, toObject[8]);
				TimeTable tableData1 = new TimeTable(toObject[1], toObject[2],"", toObject[3], toObject[4], toObject[5], professor.toJSONString(), time.toJSONString(), classroom.toJSONString(),1);
				pat.add(tableData1);
				break;
				
			case 2: //type2
//				System.out.println("case 8");
				professor.put(1, toObject[5]);
				time.put(1, toObject[6]);
				classroom.put(1, toObject[7]);
				TimeTable tableData2 = new TimeTable(toObject[0], toObject[1],"", toObject[2], toObject[3], toObject[4], professor.toJSONString(), time.toJSONString(), classroom.toJSONString(),2);
				pat.add(tableData2);
				break;
				
			case 3://type3
//				System.out.println("case 13");
				professor.put(1, toObject[7]);
				professor.put(2, toObject[10]);
				time.put(1, toObject[8]);
				time.put(2, toObject[11]);
				classroom.put(1, toObject[9]);
				classroom.put(2, toObject[12]);
				TimeTable tableData3 = new TimeTable(toObject[1], toObject[2], toObject[3], toObject[4], toObject[5], toObject[6], professor.toJSONString(), time.toJSONString(), classroom.toJSONString(),3);
				pat.add(tableData3);
				break;
				
			case 5://type4
//				System.out.println("case 12");
				professor.put(1, toObject[7]);
				time.put(1, toObject[8]);
				time.put(2, toObject[10]);
				classroom.put(1, toObject[9]);
				classroom.put(2, toObject[11]);
				TimeTable tableData4 = new TimeTable(toObject[1], toObject[2], toObject[3], toObject[4], toObject[5], toObject[6], professor.toJSONString(), time.toJSONString(), classroom.toJSONString(),5);
				pat.add(tableData4);
				break;
				
			case 7: //type5
//				System.out.println("case 11");
				
				professor.put(1, toObject[6]);
				time.put(1, toObject[7]);
				time.put(2, toObject[9]);
				classroom.put(1, toObject[8]);
				classroom.put(2, toObject[10]);
				TimeTable tableData5 = new TimeTable(toObject[1], toObject[2], "",toObject[3], toObject[4], toObject[5], professor.toJSONString(), time.toJSONString(), classroom.toJSONString(),7);
				pat.add(tableData5);
				break;
				
			case 8: //type6
//				System.out.println("case 10");
				professor.put(1, toObject[5]);
				time.put(1, toObject[6]);
				time.put(2, toObject[8]);
				classroom.put(1, toObject[7]);
				classroom.put(2, toObject[9]);
				TimeTable tableData6 = new TimeTable(toObject[0], toObject[1],"", toObject[2], toObject[3], toObject[4], professor.toJSONString(), time.toJSONString(), classroom.toJSONString(),8);
				pat.add(tableData6);
				break;
				
			case 9:
				professor.put(1, toObject[7]);
				time.put(1, toObject[8]);
				classroom.put(1, toObject[9]);
				TimeTable tableData7 = new TimeTable(toObject[1], toObject[2],"", toObject[3], toObject[4]+toObject[5], toObject[6], professor.toJSONString(), time.toJSONString(), classroom.toJSONString(),9);
				pat.add(tableData7);
				break;
				
			case 10:
				professor.put(1, toObject[6]);
				time.put(1, "");
				classroom.put(1, toObject[8]);
				TimeTable tableData8 = new TimeTable(toObject[1], toObject[2],"", toObject[3], toObject[4], toObject[5], professor.toJSONString(), time.toJSONString(), classroom.toJSONString(),10);
				pat.add(tableData8);
				break;
				
			case 11:
				professor.put(1, "");
				time.put(1, "");
				classroom.put(1, "");
				TimeTable tableData9 = new TimeTable(toObject[1], toObject[2],"", toObject[3], toObject[4], toObject[5], professor.toJSONString(), time.toJSONString(), classroom.toJSONString(),11);
				pat.add(tableData9);
				break;
				
			default:
				break;
			}
		}
		return pat;
	}
	
	public static ArrayList<String> fun_regType_num(Matcher m)
	{
		ArrayList<String> numList = new ArrayList<String>();
		int cnt = 0;
		String data;
		while(m.find())
		{
			System.out.println("== [" + cnt + "] ==");
			data = m.group();
			System.out.println(data);
			
			String[] toObject = data.split("\\n");
			System.out.println("size : " + toObject.length);
			
			/*
			 * 데이터가 겹쳤는지 앟ㄹ아보기 위하여 학수번호를 뽑아서 ArrayList에 넣는다.
			 */
			String lec_num = "(\\d{6}-\\d{2})";
			Pattern pat_lec_num = Pattern.compile(lec_num);
			Matcher lec = pat_lec_num.matcher(data);
			
			String num = ""; // 학수번호 변수
			while(lec.find())
			{
				num = lec.group();
//				System.out.println("Extracted Data : " + num);
				numList.add(num);
			}
			
			cnt++;
		}
		
		return numList;
	}
	
	public static void saveTxtFile(String txt, String fileName)
	{
		File file = new File(fileName);
        
        if( file.exists() ){
            if(file.delete())
            {
                System.out.println("파일삭제 성공");
            }
            else
            {
                System.out.println("파일삭제 실패");
                return;
            }
        }
        else
        {
            System.out.println("파일이 존재하지 않으므로 파일을 생성합니다.");
        }
        
        try
        {
            // BufferedWriter 와 FileWriter를 조합하여 사용 (속도 향상)
            BufferedWriter fw = new BufferedWriter(new FileWriter(fileName, true));
            // 파일안에 문자열 쓰기
            fw.write(txt);
            fw.flush();
            // 객체 닫기
            fw.close();
        }
        catch(Exception e)
        { 
            e.printStackTrace();
        }
	}
	public static String readFileAsString(String filePath) throws java.io.IOException
	{
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
//            System.out.println("읽어온 데이터 : " + readData);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        return fileData.toString();
	}
	
}
