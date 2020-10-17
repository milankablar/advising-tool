/*
 * Software Engineering Process I - SE 2800
 * Spring 2020
 * authors:
 *      Sara Alsudeer
 *      Hayden Klein
 */
package Group2A;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Course class is responsible for storing courses and their information (description, course code, ..etc.)
 */
public class Course {

    private String courseCode;
    private int year;
    private int quarter;
    private int credits;
    private String major;
    private String description;
    private HashMap<String, Integer> offerings = new HashMap<>();
    private ArrayList<String> prereqList = new ArrayList<>();

    public Course(String name){
        this.courseCode = name;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getQuarter() {
        return quarter;
    }

    public void setQuarter(int quarter) {
        this.quarter = quarter;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addOffering(String major, int quarter){
        offerings.put(major, quarter);
    }

    public HashMap<String, Integer> getOfferings(){
        return offerings;
    }

    public void addPrereq(String prereq) {
        this.prereqList.add(prereq);
    }

    public ArrayList<String> getPrereqList() {
        return prereqList;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }
}
