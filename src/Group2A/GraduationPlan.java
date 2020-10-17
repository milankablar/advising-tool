package Group2A;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A class that represents the graduation plan for a user
 * Contains the courses for each semester
 */
public class GraduationPlan {
    private ArrayList<Node> plan;
    private int minYear;
    private int minQuarter;

    /**
     * Creates a new graduation plan object
     */
    public GraduationPlan(){
        plan = new ArrayList<>();
        minYear = 0;
        minQuarter = 0;
    }

    public void addTakenCourse(Course course, int quarter, int year){
        minYear = Math.max(year, minYear);
        minQuarter = Math.max(quarter, minQuarter);
        updateAllFromMinimums();
        addCourse(course, quarter, year, true);
    }

    public void addRequiredCourse(Course course, int quarter){
        addCourse(course, quarter, 1, false);
    }

    private void addCourse(Course course, int quarter, int year, boolean taken){
        Node newNode = new Node(course, year, quarter, taken);
        for(Node n : plan){
            if(newNode.hasPrereq(n)){
                newNode.addChild(n);
                n.addParent(newNode);
            } else if(n.hasPrereq(newNode)){
                n.addChild(newNode);
                newNode.addParent(n);
            }
        }
        plan.add(newNode);
        newNode.refreshNode();
        updateAllFromMinimums();
    }

    public void modifyCourse(String code, int quarter, int year) {
        for(Node n : plan) {
            if(n.getCourse().getCourseCode().equals(code)) {
                n.setQuarter(quarter);
                n.setYear(year);
            }
        }
    }

    private void updateAllFromMinimums(){
        for(Node node : plan){
            node.updateMinimums(minYear, minQuarter);
        }
    }

    public List<Node> getAllCourses(){
        return plan;
    }
}

class Node {
    private List<Node> parents = new LinkedList<>();
    private List<Node> children = new LinkedList<>();
    private Course course;
    private int year;
    private int quarter;
    //Note that taken courses will never have their position edited
    private boolean taken;

    public Node(Course course, int startYear, int quarter, boolean taken){
        this.course = course;
        this.quarter = quarter;
        this.year = startYear;
        this.taken = taken;
        if(!taken){
            findStandingRequirements();
        }
    }

    private void findStandingRequirements(){
        for(String req : course.getPrereqList()){
            switch (req){
                case "FR":
                    year = 1;
                    break;
                case "SO":
                    year = 2;
                    break;
                case "JR":
                    year = 3;
                    break;
                case "SR":
                    year = 4;
                    break;
            }
        }
    }

    public void refreshNode(){
        for(Node child : children){
            updateFromChild(child);
        }
    }

    public void updateFromChild(Node child){
        if(!taken){
            int minYear = child.year;
            if(quarter <= child.quarter){
                minYear++;
            }
            year = Math.max(minYear, year);

        }
        updateParents();
    }

    private void updateParents(){
        for(Node parent : parents){
            parent.updateFromChild(this);
        }
    }

    public void updateMinimums(int minYear, int minQuarter){
        if(!taken){
            if(quarter <= minQuarter){
                minYear++;
            }
            year = Math.max(year, minYear);
            updateParents();
        }
    }

    public void addChild(Node n){
        children.add(n);
    }

    public void addParent(Node n){
        parents.add(n);
    }

    public boolean hasPrereq(Node other){
        List<String> prereqs = course.getPrereqList();
        String courseCode = other.course.getCourseCode();
        for(String prereq : prereqs){
            if(prereq.equals(courseCode)){
                return true;
            }
            if(prereq.contains("|")){
                String[] subreqs = prereq.split("\\|");
                for(String subreq : subreqs){
                    if(subreq.equals(courseCode)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Course getCourse(){
        return course;
    }

    public int getYear(){
        return year;
    }

    public int getQuarter(){
        return quarter;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setQuarter(int quarter) {
        this.quarter = quarter;
    }
}
