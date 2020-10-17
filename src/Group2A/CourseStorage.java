/*
 * Software Engineering Process I - SE 2800
 * Spring 2020
 * authors:
 *      Sara Alsudeer
 *      Daniel Anderson
 *      Milan Kablar
 *      Hayden Klein
 *      Mohammad Sheikh
 */
package Group2A;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

/**
 * CSVParser is responsible for parsing csv files and storing them in a data structure
 */
public class CourseStorage {

    private static final String DEFAULT_SEPARATOR = ",";
    private static final String SPACE_SEPARATOR = " ";
    private String[] coursePrefixes = new String[] {"AC","AE","AF","AR","BA","BE","BI","CE","CH","CM",
            "CS","CV","EB","EE","EN","GE","GS","HU","IE","IG","IW","LS","MA","ME","NS","NU",
            "OR","PH","RP","RW","SC","SE","SM","SP","SS","TC","TR","UR","UX","VC"};


    private HashMap<String, HashMap<String, Course>> curriculumsMap = new HashMap<>();
    private HashMap<String, Course> courses = new HashMap<>();
    private HashMap<String, Course> currentTranscript = new HashMap<>();

    /**
     * Parses offerings csv and loads it into a hash based table
     * @throws FileNotFoundException
     * @author Hayden Klein
     */
    public void loadOfferingCSV(String offeringsPath) throws FileNotFoundException {
        clearOfferings();
        Scanner scanner = new Scanner(new File(offeringsPath));
        String tempLine = scanner.nextLine();
        String[] titleArray = tempLine.split(DEFAULT_SEPARATOR,-1);
        List<String> titleList = new ArrayList<>(Arrays.asList(titleArray));
        titleList.remove(0);
        while(scanner.hasNextLine()){
            String nextLine = scanner.nextLine();
            String[] tempArray = nextLine.split(DEFAULT_SEPARATOR,-1);
            List<String> lineList = new ArrayList<>(Arrays.asList(tempArray));
            String courseTitle = lineList.get(0);
            lineList.remove(0);
            for(int i = 0;i<lineList.size();i++){
                if (!courseTitle.equals("") && !titleList.get(i).equals("") && !lineList.get(i).equals("")) {
                    if(titleList.get(i).equals("SE")||titleList.get(i).equals("CS")) {
                        Course course = getOrCreateCourse(courseTitle);
                        int quarter = Integer.parseInt(lineList.get(i));
                        course.addOffering(titleList.get(i), quarter);
                    }
                }
            }
        }
    }

    /**
     * Clears the offerings from every course in the courses list
     */
    private void clearOfferings(){
        for(Course course : courses.values()){
            course.getOfferings().clear();
        }
    }

    /**
     * Parses prerequisite csv and loads it into a hash map
     * @throws FileNotFoundException - throws an error when the file is not found
     * @author Sara Alsudeer
     */
    public void loadPrerequisiteCSV(String filePath) throws FileNotFoundException {
        clearPrereqs();
        Scanner scanner = new Scanner(new File(filePath));
        // discarded
        String titleLine = scanner.nextLine();

        while (scanner.hasNextLine()){
            // get the next line
            String nextLine = scanner.nextLine();
            // store the title line
            String[] tempArray = nextLine.split(DEFAULT_SEPARATOR,-1);

            Course course = getOrCreateCourse(tempArray[0]);
            course.setDescription(tempArray[3]);
            course.setCredits(Integer.parseInt(tempArray[1]));
            // if course has prerequisite
            String[] prerequisite = tempArray[2].split(SPACE_SEPARATOR, -1);
            if(!prerequisite[0].equals("")){
                for(String prereq: prerequisite) course.addPrereq(prereq);
            }
        }
    }

    /**
     * Parses curriculum csv and loads it into an array list
     * Note: This method assumes Curriculum.csv contains only two majors (SE & CS)
     * @throws FileNotFoundException - throws an error when the file is not found
     */
    public void loadCurriculumCSV(String filePath) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filePath));
        HashMap<Integer, String> temp = new HashMap<>();

        String[] prefixLine = scanner.nextLine().split(DEFAULT_SEPARATOR);
        for(int i = 0; i < prefixLine.length; i++) {
            curriculumsMap.put(prefixLine[i], new HashMap<>());
            temp.put(i, prefixLine[i]);
        }

        while (scanner.hasNextLine()){
            String line = scanner.nextLine();
            String[] splitLine = line.split(",");
            for(int i = 0; i < splitLine.length; i++) {
                HashMap<String, Course> tempList = curriculumsMap.get(temp.get(i));
                String courseCode = splitLine[i];
                if(!courseCode.equals("")) {
                    Course course = getOrCreateCourse(courseCode);
                    tempList.put(courseCode, course);
                }
            }
        }
    }

    /**
     * Clears the list of prereqs for each course in courses
     */
    private void clearPrereqs(){
        for(Course course : courses.values()){
            course.getPrereqList().clear();
        }
    }

    private Course getOrCreateCourse(String courseName){
        Course toReturn = courses.get(courseName);
        if(toReturn == null){
            toReturn = new Course(courseName);
            courses.put(courseName, toReturn);
        }
        return toReturn;
    }

    /**
     * Gets the offerings for a specific course and major
     * @param courseTitle The course to search for
     * @param major the major to check offerings for
     * @return -1 if the offering does not exist. Otherwise return the quarter number
     */
    public int getOfferings(String courseTitle, String major){
        Course course = courses.get(courseTitle);
        if(course == null){
            return -1;
        }
        Integer offering = course.getOfferings().get(major);
        if(offering == null){
            return -1;
        }
        return offering;
    }

    /**
     * Gets a course from the course map
     * @param courseCode The code for the requested course
     * @return
     */
    public Course getCourse(String courseCode){
        return courses.get(courseCode);
    }

    public HashMap<String, Course> getCurriculum(String majorPrefix) {
        return curriculumsMap.get(majorPrefix);
    }


    public HashMap<String, Course> getCourses(){
        return courses;
    }

    public List<Course> getSortedCourses() {
        List<Course> sortedCourses = new ArrayList<>(courses.values());
        Collections.sort(sortedCourses, new Comparator<Course>() {
            @Override
            public int compare(Course c1, Course c2) {
                return c1.getCourseCode().compareTo(c2.getCourseCode());
            }
        });

        return sortedCourses;
    }
}
