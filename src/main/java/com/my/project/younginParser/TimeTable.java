package com.my.project.younginParser;

public class TimeTable 
{
	String grade;
	String classification;
	String target;
	String lec_number;
	String lec_title;
	String score;
	String professor;
	String lec_time;
	String classroom;
	int type;
	
	
	
	public TimeTable(String grade, String classification, String target,
			String lec_number, String lec_title, String score,
			String professor, String lec_time, String classroom, int type) {
		super();
		this.grade = grade;
		this.classification = classification;
		this.target = target;
		this.lec_number = lec_number;
		this.lec_title = lec_title;
		this.score = score;
		this.professor = professor;
		this.lec_time = lec_time;
		this.classroom = classroom;
		this.type = type;
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
	public String getLec_time() {
		return lec_time;
	}
	public void setLec_time(String lec_time) {
		this.lec_time = lec_time;
	}
	public String getClassroom() {
		return classroom;
	}
	public void setClassroom(String classroom) {
		this.classroom = classroom;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	

}
