package Group2A;

import javafx.scene.control.TableView;

import java.io.IOException;
import java.util.HashMap;

/**
 * A class for creating the graduation plan for a user
 */
public class GraduationPlanCreator {
    //Parser to get the prerequisites and offerings
    private CourseStorage fileInfo;
    private PDFUploader transcriptInfo;

    /**
     * Creates the new Graduation Plan Creator object passing in the csv parser object
     * @param fileInfo The csv parser that is used to get information about offerings and prerequisites
     */
    public GraduationPlanCreator(CourseStorage fileInfo, PDFUploader transcriptInfo){
        this.fileInfo = fileInfo;
        this.transcriptInfo = transcriptInfo;
    }

    /**
     * Creates the graduation plan for the current user
     * @return A list of semesters that each contain a list of courses
     */
    public GraduationPlan makeGraduationPlan(String major){
        GraduationPlan graduationPlan = new GraduationPlan();
        HashMap<String, Course> requirements = getRequiredCourses(major);
        HashMap<String, Course> transcript = transcriptInfo.getTranscriptMap();
        for(Course takenCourse : transcript.values()){
            graduationPlan.addTakenCourse(takenCourse, takenCourse.getQuarter(), takenCourse.getYear());
        }

        for(Course requiredCourse : requirements.values()){
            Course takenCourse = transcript.get(requiredCourse.getCourseCode());

            if(takenCourse == null){
                Integer checkSemester = requiredCourse.getOfferings().get(major);
                if(checkSemester == null){
                    continue;
                }
                int semester = checkSemester;
                graduationPlan.addRequiredCourse(requiredCourse, semester);
            }
        }
        return graduationPlan;
    }

    /**
     * Gets all of the courses that are required for the user
     * @return A list of courses that are required by this major
     */
    private HashMap<String, Course> getRequiredCourses(String major){
        return fileInfo.getCurriculum(major);
    }

    private HashMap<String, Course> getTakenCourses(String path){
        try{
            transcriptInfo.loadTranscriptPDF(path);
            //TODO: Do not load file here
            return transcriptInfo.getTranscriptMap();
        }catch(Exception e){
            return null;
        }
    }
}
