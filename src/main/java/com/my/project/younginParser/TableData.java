package com.my.project.younginParser;

public class TableData 
{
	String grade;
	String classification;
	String target;
	String lec_number;
	String lec_title;
	String score;
	String professor;
	String date;
	String startTime;
	String endTime;
	String classroom;
	
	
	
	public TableData(String grade, String classification, String target,
			String lec_number, String lec_title, String score,
			String professor, String date, String startTime, String endTime,
			String classroom) {
		super();
		this.grade = grade;
		this.classification = classification;
		this.target = target;
		this.lec_number = lec_number;
		this.lec_title = lec_title;
		this.score = score;
		this.professor = professor;
		this.date = date;
		this.startTime = startTime;
		this.endTime = endTime;
		this.classroom = classroom;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public String getClassification() {
		return classification;
	}
	public void setClassification(String classification) {
		this.classification = classification;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getLec_number() {
		return lec_number;
	}
	public void setLec_number(String lec_number) {
		this.lec_number = lec_number;
	}
	public String getLec_title() {
		return lec_title;
	}
	public void setLec_title(String lec_title) {
		this.lec_title = lec_title;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getProfessor() {
		return professor;
	}
	public void setProfessor(String professor) {
		this.professor = professor;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getClassroom() {
		return classroom;
	}
	public void setClassroom(String classroom) {
		this.classroom = classroom;
	}
	
	
}
