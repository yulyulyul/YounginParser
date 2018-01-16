package com.my.project.younginParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.sqlite.SQLiteConfig;

public class planB 
{
	public final static String DATABASE = "Timetable_data_2017_1.db";
//	public final static String DATABASE = "Timetable_data_2017_2.db";
	
	private static Connection connection;
	private static String dbFileName;
	private static boolean isOpened = false;
	
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
	
	
	public static void main(String[] args) throws ParseException 
	{
		HashMap<String, ArrayList<TableData>> tableData = new HashMap<String, ArrayList<TableData>>();
		
		try 
		{
			String data = readFileAsString("timeSchedule2017_1.txt");
//			String data = readFileAsString("timeSchedule2017_2.txt");
			
			data = fun_del(data);
			String hakyear = "학년";
			StringBuilder sb = new StringBuilder();
			
			data = data.replaceAll("※.*\\r\\n", "");
			String[] dataStr = data.split("학년");
			for(int a=0; a<dataStr.length; a++)
			{
//				System.out.println("[" + a + "] = " + dataStr[a]);
				sb.append("======================"+a+"======================"+dataStr[a]+"\n");
			}
//			System.out.println("dataStr size : " + dataStr.length);

 			saveTxtFile(sb.toString(), "hakyearSPLIT_2017_1.txt");
// 			saveTxtFile(sb.toString(), "hakyearSPLIT_2017_2.txt");
 			
 			String nextMajor = "";
 			String nextReference = "";
 			boolean nextIndexIsClassification = false;
 			boolean nextIndexIsTarget = false;
 			boolean nextIndexIsLecNumber = false;
 			boolean nextIndexIsLecTitle = false;
 			boolean nextIndexIsScore = false;
 			boolean nextIndexIsProfessor = false;
 			boolean ScoreAfter = false;
 			boolean isReference = false;
 			
 			StringBuilder strb = new StringBuilder();
 			
 			for(int a=0; a<dataStr.length; a++)
			{
 				String[] sp = dataStr[a].split("\n");
				
//				System.out.println("== [" + a + "] ==");
				strb.append(";;\n");
				
				if(nextMajor.length() != 0)
				{
					strb.append(nextMajor+"\n");
					nextMajor = "";
					isReference = false;
				}
				if(nextReference.length() != 0)
				{
					strb.append(nextReference+"\n");
					nextReference = "";
					isReference = true;
				}
				
				boolean printOk = false;
				
				for (int i = 0; i < sp.length; i++) 
				{
					sp[i] = sp[i].replaceAll("(\r\n|\r|\n|\n\r)", "");
					
					/*
//					 * '['을 가지고 있으면 (여기서는 [~~~] 이것이 '비고'에 해당하는 것임.
//					 * 이것 바로 위에 다음 배열의 전공명이 위치해 있고
//					 * 만약에 '['이 없다면 비고가 없는 것, 따라서 해당 배열의 맨 마지막이 
//					 * 다음 배열의 전공명임.
//					 */
					if(sp[sp.length-1].contains("["))
					{
//						System.out.println(sp[sp.length-2]);
						nextMajor = sp[sp.length-2];
						nextReference = sp[sp.length-1];
					}
					else
					{
//						System.out.println(sp[sp.length-1]);
						nextMajor = sp[sp.length-1];
					}
					
					try
					{
						Integer Intgrade = Integer.parseInt(sp[i]);
						printOk = true;
						strb.append("*\n");
					}
					catch(NumberFormatException e)
					{
						String gradeReg = "\\d.\\d";
						Pattern pat_lec_num = Pattern.compile(gradeReg);
						Matcher lec = pat_lec_num.matcher(sp[i]);
						
						if(sp[i].contains("·") && lec.find())
						{
							printOk = true;
							strb.append("*\n");
						}
					}
					
					if(printOk)
					{
//						System.out.println(sp[i]);
						if(isReference || (nextReference.length() != 0))
						{
							if((sp.length-2)>i)
							{
								strb.append(sp[i]+"\n");
							}
						}
						else
						{
							if ((sp.length-1)>i) 
							{
								strb.append(sp[i]+"\n");
							}
						}
						/*
						 * if(printOk)의 문에서 sp의 마지막 인덱스 또는 마지막 전 인덱스의 텍스트는 다음 배열의 과
						 * strb에서 거ㅡ 전까지만 append 시켰는데
						 * 이렇게 되면 마지막 배열(뷰티케애ㅓ학과의 마지막 과목)의 장소가 짤리게된다.(testfiel.txt)
						 * 따라서 마지막 문장은 포함되도록 아래의 코드를 넣어준다.
						 */
						if((a==(dataStr.length-1)) && (i==(sp.length-1)))
						{
//							System.out.println(sp[i]);
							strb.append(sp[i]);
						}
					}
				}
			}
 				
// 			System.out.println(strb.toString());
 			saveTxtFile(strb.toString(), "checkFile2017_1.txt");
// 			saveTxtFile(strb.toString(), "checkFile2017_2.txt");
 			
			StringBuilder testfile2_strb = new StringBuilder();
			StringBuilder checkProfessor = new StringBuilder();
			StringBuilder checkTime = new StringBuilder();
			StringBuilder checkRoom = new StringBuilder();
			
			String[] splitA = strb.toString().split(";;");
			
			for (int j = 0; j < splitA.length; j++) 
			{
//				System.out.println("== [" + j + "] ==");
//				System.out.println(splitA[j]);
				
				String[] splitB = splitA[j].split("\\*\n");
				
				String Lec = "";
				String Target = "";
				ArrayList<TableData> tempList = new ArrayList<TableData>();
				
				for (int k = 0; k < splitB.length; k++) 
				{
//					System.out.println("[" + k + "]");
					testfile2_strb.append("[" + k + "]" + "\n");
//					System.out.println(splitB[k]);
					
					if(k!=0)
					{
						String[] splitC = splitB[k].split("\n");
						
						String grade = "";
						String classification = "";
						String target = "";
						String lec_number = "";
						String lec_title = "";
						String score = "";
						String professor = "";
						String classroom = "";
						
						JSONObject json_prof = new JSONObject();
						JSONObject json_time = new JSONObject();
						JSONObject json_classRoom = new JSONObject();
						int cnt_p = 0;
						int cnt_t = 0;
						int cnt_c = 0;
						
						ScoreAfter = false;
						for (int l = 0; l < splitC.length; l++) 
						{
//							System.out.println(l + " : " + splitC[l]);
							
							/*
							 * 
							 * nextIndexIsProfessor  if문부터
							 * ScoreAfter를 true로 바꿔서
							 * 학점 이후의 텍스트를 제어하는데 사용.
							 * 
							 * 따라서
							 * 밑에
							 * if(ScoreAfter)로 이어지게 되어서
							 * 여기서 학점 이후의 텍스트르 제어.
							 */
							if(nextIndexIsProfessor)
							{
//								professor = splitC[l];
								nextIndexIsProfessor = false;
								
								ScoreAfter = true;
							}
							
							if(nextIndexIsScore)
							{
								score = splitC[l];
								nextIndexIsScore = false;
								nextIndexIsProfessor = true;
							}
							
							if(nextIndexIsLecTitle)
							{
								lec_title = splitC[l];
								nextIndexIsLecTitle = false;
								nextIndexIsScore = true;
							}
							
							if(nextIndexIsLecNumber)
							{
								lec_number = splitC[l];
								nextIndexIsLecNumber = false;
								nextIndexIsLecTitle = true;
							}
							
							if(l == 0)
							{
								grade = splitC[l];
								
								/*
								 * ScoreAfter는 이 루프가 새로 시작하게 되면 false가 되어야 하는데
								 *  학년	: 2
								 *  구분	: 전공
								 *  학수번호: 470013-82
								 *  수강명	: 중국어회화(현지)
								 *  학점	:2(50)
								 *  과 같이 학점 이후로 데이터가 없는 과목일 경우 보장이 안되는 경우가 발생하였음.
								 *  이유는 귀찮아서 확인을 안했으나.. 여기 l이 0일때 ScoreAfter를 false로 보장해주면
								 *  내가 의도한 대로 잘 나오는 것을 학인할 수 있었다.
								 */
								ScoreAfter = false;
							}
							else if(l == 1)
							{
								classification = splitC[l];
							}
							else if(l ==2)
							{
								try
								{
									String strTar = splitC[l].charAt(0) + "";
									Integer isNumber =- Integer.parseInt(strTar);
									if(Target.length() == 0)
									{
										target = "";
									}
									else
									{
										target = Target;
									}
									lec_number = splitC[l];
									nextIndexIsLecTitle = true;
								}
								catch(NumberFormatException e)
								{
									target = splitC[l];
									nextIndexIsLecNumber = true;
								}
							}
							if(ScoreAfter)
							{
								String cLassRoom = "";
								String classRoom = "\\W-.*";
								Pattern pat_lec_num = Pattern.compile(classRoom);
								Matcher lec = pat_lec_num.matcher(splitC[l]);
								
								if(splitC[l].contains(":"))
								{
//									System.out.println("시간 : " + splitC[l]);
									json_time.put(cnt_t, splitC[l]);
									cnt_t += 1;
								}
								else if(lec.find())
								{
									cLassRoom = lec.group();
//									System.out.println("장소 : " + cLassRoom);
									json_classRoom.put(cnt_c, cLassRoom);
									cnt_c += 1;
								}
								else
								{
//									System.out.println("교수 : " + splitC[l]);
									json_prof.put(cnt_p, splitC[l]);
									cnt_p += 1;
								}
//								System.out.println(splitC[l]);
							}
							
						}
//						System.out.println("");
//						System.out.println("학    년 : " + grade);
//						System.out.println("구    분 : " + classification);
//						System.out.println("대    상 : " + target);
//						System.out.println("학수번호 : " + lec_number);
//						System.out.println("수 강 명 : " + lec_title);
//						System.out.println("학    점 : " + score);
//						System.out.println("교    수 : " + json_prof.toJSONString());
//						System.out.println("시    간 : " + json_time.toJSONString());
//						System.out.println("장    소 : " + json_classRoom.toJSONString());
//						System.out.println("");
						
						testfile2_strb.append("학년		: " + grade + "\n");
						testfile2_strb.append("구분		: " + classification + "\n");
						testfile2_strb.append("대상		: " + target + "\n");
						testfile2_strb.append("학수번호 : " + lec_number + "\n");
						testfile2_strb.append("수강명	: " + lec_title + "\n");
						testfile2_strb.append("학점		: " + score + "\n");
						testfile2_strb.append("교수		: " + json_prof.toJSONString() + "\n");
						testfile2_strb.append("시간		: " + json_time.toJSONString() + "\n");
						testfile2_strb.append("장소		: " + json_classRoom.toJSONString() + "\n");
						testfile2_strb.append("\n");
						
						checkProfessor.append("학수번호 : " + lec_number + "	교수 : " + json_prof.toJSONString() + "\n");
						checkTime.append("학수번호 : " + lec_number + "	시간 : " + json_time.toJSONString() + "\n");
						checkRoom.append("학수번호 : " + lec_number + "	장소 : " + json_classRoom.toJSONString() + "\n");
						
						/*
						 * 수업을 시간단위(시간 하나당 DB에 들어갈 row)로 나누기 위해서 Json으로 묶어놨던 데이터들을 나눈다.
						 */
						JSONParser jsonparser = new JSONParser();
						JSONObject timeJS = (JSONObject) jsonparser.parse(json_time.toJSONString());
						JSONObject ProfJS = (JSONObject) jsonparser.parse(json_prof.toJSONString());
						JSONObject classRoomJS = (JSONObject) jsonparser.parse(json_classRoom.toJSONString());
						
						String prof = null;
						String clasR = null;
						String tempP = null;
						String tempR = null;
						
						if(timeJS.size() == 1)
						{
							StringBuilder sbProf = new StringBuilder();
							StringBuilder sbClasR = new StringBuilder();
							String time = (String) timeJS.get(0+"");
							
							for (int i = 0; i < json_prof.size(); i++) 
							{
								sbProf.append((String)ProfJS.get(i+"") + ",");
							}
							for (int i = 0; i < json_classRoom.size(); i++) 
							{
								sbClasR.append((String)classRoomJS.get(i+"") + ",");
							}
							try
							{
								String stProf = sbProf.substring(0, sbProf.length()-1);
								String stClasR = sbClasR.substring(0, sbClasR.length()-1);
								String date = time.substring(0, 1);
								String startTime = time.substring(1, time.indexOf("-"));
								String endTime = time.substring(time.indexOf("-")+1, time.length());
								
//								System.out.println("");
//								System.out.println("학    년 : " + grade);
//								System.out.println("구    분 : " + classification);
//								System.out.println("대    상 : " + target);
//								System.out.println("학수번호 : " + lec_number);
//								System.out.println("수 강 명 : " + lec_title);
//								System.out.println("학    점 : " + score);
//								System.out.println("교    수 : " + stProf);
//								System.out.println("요    일 : " + date);
//								System.out.println("시작시간 : " + startTime);
//								System.out.println("끝 시 간 : " + endTime);
//								System.out.println("강 의 실 : " + stClasR);
//								System.out.println("");
								
								TableData td = new TableData(grade, classification, target, lec_number, lec_title, score, stProf, date, startTime, endTime, stClasR);
								tempList.add(td);
							}
							catch(StringIndexOutOfBoundsException e )
							{
//								e.printStackTrace();
								String date = time.substring(0, 1);
								String startTime = time.substring(1, time.indexOf("-"));
								String endTime = time.substring(time.indexOf("-")+1, time.length());
								
//								System.out.println("");
//								System.out.println("학    년 : " + grade);
//								System.out.println("구    분 : " + classification);
//								System.out.println("대    상 : " + target);
//								System.out.println("학수번호 : " + lec_number);
//								System.out.println("수 강 명 : " + lec_title);
//								System.out.println("학    점 : " + score);
//								System.out.println("교    수 : " + sbProf);
//								System.out.println("요    일 : " + date);
//								System.out.println("시작시간 : " + startTime);
//								System.out.println("끝 시 간 : " + endTime);
//								System.out.println("강 의 실 : " + sbClasR);
//								System.out.println("");
								
								TableData td = new TableData(grade, classification, target, lec_number, lec_title, score, sbProf.toString(), date, startTime, endTime, sbClasR.toString());
								tempList.add(td);
							}
							
							
						}
						else if(timeJS.size() == 0)
						{
							StringBuilder sbProf = new StringBuilder();
							StringBuilder sbClasR = new StringBuilder();
							String time = (String) timeJS.get(0 + "");
							
							for (int i = 0; i < json_prof.size(); i++) 
							{
								sbProf.append((String)ProfJS.get(i+"")+",");
							}
							for (int i = 0; i < json_classRoom.size(); i++) 
							{
								sbClasR.append((String)classRoomJS.get(i+"")+",");
							}
							try
							{
								String stProf = "";
								String stClasR = "";
								if(!sbProf.toString().equals(""))
								{
									stProf = sbProf.substring(0, sbProf.length()-1);
								}
								if(!sbClasR.toString().equals(""))
								{
									stClasR = sbClasR.substring(0, sbClasR.length()-1);
								}
//								System.out.println("");
//								System.out.println("학    년 : " + grade);
//								System.out.println("구    분 : " + classification);
//								System.out.println("대    상 : " + target);
//								System.out.println("학수번호 : " + lec_number);
//								System.out.println("수 강 명 : " + lec_title);
//								System.out.println("학    점 : " + score);
//								System.out.println("교    수 : " + stProf);
//								System.out.println("요    일 : " + "");
//								System.out.println("시작시간 : " + "");
//								System.out.println("끝 시 간 : " + "");
//								System.out.println("강 의 실 : " + stClasR);
//								System.out.println("");
								
								TableData td = new TableData(grade, classification, target, lec_number, lec_title, score, stProf, "", "", "", stClasR);
								tempList.add(td);
								
							} 
							catch (StringIndexOutOfBoundsException e) 
							{
//								System.out.println("");
//								System.out.println("학    년 : " + grade);
//								System.out.println("구    분 : " + classification);
//								System.out.println("대    상 : " + target);
//								System.out.println("학수번호 : " + lec_number);
//								System.out.println("수 강 명 : " + lec_title);
//								System.out.println("학    점 : " + score);
//								System.out.println("교    수 : " + sbProf);
//								System.out.println("요    일 : " + "");
//								System.out.println("시작시간 : " + "");
//								System.out.println("끝 시 간 : " + "");
//								System.out.println("강 의 실 : " + sbClasR);
//								System.out.println("");
								
								TableData td = new TableData(grade, classification, target, lec_number, lec_title, score, sbProf.toString(), "", "", "", sbClasR.toString());
								tempList.add(td);
								
							}
						}
						
						/*
						 * 강의시간이 2개 이상인 경우.
						 */
						else
						{
							for (int i = 0; i < timeJS.size(); i++) 
							{
								String time =(String) timeJS.get(i+"");
								
								tempP = (String)ProfJS.get(i+"");
								if(tempP != null)
								{
									prof = tempP;
								}
								
								tempR = (String)classRoomJS.get(i+"");
								if(tempP != null)
								{
									clasR = tempR;
								}
								
								if(!((prof == null) && (clasR == null)))
								{
									String date = time.substring(0, 1);
									String startTime = time.substring(1, time.indexOf("-"));
									String endTime = time.substring(time.indexOf("-")+1, time.length());
									
//									System.out.println("");
//									System.out.println("학    년 : " + grade);
//									System.out.println("구    분 : " + classification);
//									System.out.println("대    상 : " + target);
//									System.out.println("학수번호 : " + lec_number);
//									System.out.println("수 강 명 : " + lec_title);
//									System.out.println("학    점 : " + score);
//									System.out.println("교    수 : " + prof);
//									System.out.println("요    일 : " + date);
//									System.out.println("시작시간 : " + startTime);
//									System.out.println("끝 시 간 : " + endTime);
//									System.out.println("강 의 실 : " + clasR);
//									System.out.println("");
									
									TableData td = new TableData(grade, classification, target, lec_number, lec_title, score, prof, date, startTime, endTime, clasR);
									tempList.add(td);
								}
							}
						}
					}
					else // 과목명
					{
						Lec = splitB[k].replaceAll("(\r\n|\r|\n|\n\r)", "");
						if(Lec.contains("["))
						{
							String lec = "";
							String tarGet = "";
							tarGet = Lec.substring(Lec.indexOf("[")+1, Lec.indexOf("]"));
//							System.out.println(tarGet);
							lec = Lec.substring(0, Lec.indexOf("["));
//							System.out.println(lec);
							Lec = lec;
							Target = tarGet;
						}
//						System.out.println(Lec);
						testfile2_strb.append("**** " + Lec + " ****" + "\n");
					}
				}
				if(isSameKey(Lec, tableData))
				{
					if(tempList.size() > 0)
					{
						ArrayList<TableData> arrTemp = tableData.get(Lec);
						arrTemp.addAll(tempList);
						tableData.replace(Lec, arrTemp);
					}
				}
				else
				{
					if(tempList.size() > 0)
					{
						tableData.put(Lec, tempList);
					}
				}
			}
			StringBuilder ProfessorTimeRoom = new StringBuilder();
			ProfessorTimeRoom.append("==================== 교수 ====================" + "\n");
			ProfessorTimeRoom.append(checkProfessor.toString() + "\n");
			ProfessorTimeRoom.append("==================== 시간 ====================" + "\n");
			ProfessorTimeRoom.append(checkTime.toString() + "\n");
			ProfessorTimeRoom.append("==================== 장소 ====================" + "\n");
			ProfessorTimeRoom.append(checkRoom.toString() + "\n");
			
			saveTxtFile(testfile2_strb.toString(), "FinalCheckFile2017_1.txt");
//			saveTxtFile(testfile2_strb.toString(), "FinalCheckFile2017_2.txt");
			saveTxtFile(ProfessorTimeRoom.toString(), "CheckProfessorTimeRoom_2017_1.txt");
//			saveTxtFile(ProfessorTimeRoom.toString(), "CheckProfessorTimeRoom_2017_2.txt");
			
			System.out.println("HashMap Print");
			Set<String> keySet = tableData.keySet();
			for (String str : keySet) 
			{
				System.out.println("key : " + str);
//				ArrayList<TableData> tableD = tableData.get(str);
//				for (int i = 0; i < tableD.size(); i++) 
//				{
//					System.out.println(tableD.get(i).getLec_title());
//					System.out.println("");
//					System.out.println("학    년 : " + tableD.get(i).getGrade());
//					System.out.println("구    분 : " + tableD.get(i).getClassification());
//					System.out.println("대    상 : " + tableD.get(i).getTarget());
//					System.out.println("학수번호 : " + tableD.get(i).getLec_number());
//					System.out.println("수 강 명 : " + tableD.get(i).getLec_title());
//					System.out.println("학    점 : " + tableD.get(i).getScore());
//					System.out.println("교    수 : " + tableD.get(i).getProfessor());
//					System.out.println("요    일 : " + tableD.get(i).getDate());
//					System.out.println("시작시간 : " + tableD.get(i).getStartTime());
//					System.out.println("끝 시 간 : " + tableD.get(i).getEndTime());
//					System.out.println("강 의 실 : " + tableD.get(i).getClassroom());
//					System.out.println("");
//					
//				}
//				System.out.println("");
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		/*
		 * 이 위쪽까지가 모두 데이터를 추출해서 tableData에 정리하기까지의 과정임.
		 * 이 밑에 부분은 데이터를 sqlite에 저장하기 위한 과정.
		 */
		
		
		open();
		Set<String> keySet = tableData.keySet();
		for (String str : keySet) 
		{
			String tableName = str;
			String InsertQuery = "Insert into " + tableName + "(grade, classification, target, lec_number, lec_title, score, professor, date, startTime, endTime, classroom) "
								+ "Values(?,?,?,?,?,?,?,?,?,?,?);";
			String creatTableQuery = "CREATE TABLE " + tableName + " (seq INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
																	+"grade TEXT NOT NULL, "
																	+"classification TEXT NOT NULL, "
																	+"target TEXT NOT NULL, "
																	+"lec_number TEXT NOT NULL, "
																	+"lec_title TEXT NOT NULL, "
																	+"score TEXT NOT NULL, "
																	+"professor TEXT NOT NULL, "
																	+"date TEXT NOT NULL, "
																	+"startTime TEXT NOT NULL, "
																	+"endTime TEXT NOT NULL, "
																	+"classroom TEXT NOT NULL);";
			
			try 
			{
				PreparedStatement prep = connection.prepareStatement(creatTableQuery);
				prep.execute();
				
				System.out.println("key : " + str);
				ArrayList<TableData> tableD = tableData.get(str);
				
				for (int i = 0; i < tableD.size(); i++) 
				{
					System.out.println(tableD.get(i).getLec_title());
					PreparedStatement insertPrep = connection.prepareStatement(InsertQuery);
					insertPrep.setString(1,  tableD.get(i).getGrade());
					insertPrep.setString(2,  tableD.get(i).getClassification());
					insertPrep.setString(3,  tableD.get(i).getTarget());
					insertPrep.setString(4,  tableD.get(i).getLec_number());
					insertPrep.setString(5,  tableD.get(i).getLec_title());
					insertPrep.setString(6,  tableD.get(i).getScore());
					insertPrep.setString(7,  tableD.get(i).getProfessor());
					insertPrep.setString(8,  tableD.get(i).getDate());
					insertPrep.setString(9,  tableD.get(i).getStartTime());
					insertPrep.setString(10, tableD.get(i).getEndTime());
					insertPrep.setString(11, tableD.get(i).getClassroom());
					insertPrep.execute();
				}
				System.out.println("");
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
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
	
	
	public static boolean isSameKey(String LecTitle, HashMap<String, ArrayList<TableData>> hash)
	{
		Set<String> keySet = hash.keySet();
		for (String str : keySet) 
		{
			if(str.equals(LecTitle))
			{
				System.out.println("Same_Key Name : " + str);
				return true;
			}
		}
		
		return false;
	}
	
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
