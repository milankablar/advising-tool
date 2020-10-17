package Group2A;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PDFUploader {

    private String[] coursePrefixes = new String[] {"AC","AE","AF","AR","BA","BE","BI","CE","CH","CM",
            "CS","CV","EB","EE","EN","GE","GS","HU","IE","IG","IW","LS","MA","ME","NS","NU",
            "OR","PH","RP","RW","SC","SE","SM","SP","SS","TC","TR","UR","UX","VC"};

    public static void main(String[] args) throws Exception {
        PDFUploader u = new PDFUploader();
        u.loadTranscriptPDF("resources/transcript.pdf");
        for(Map.Entry<String, Course> course : u.getTranscriptMap().entrySet()) {
            System.out.println(course.getValue().getCourseCode() + "  " + course.getValue().getQuarter());
        }
    }

    HashMap<String, Course> transcriptMap = new HashMap<>();
    private int currentQuarter;

    public void loadTranscriptPDF(String pathname) throws Exception {
        try {
            PDDocument document = PDDocument.load(new File(pathname));
            if (!document.isEncrypted()) {
                PDFTextStripperByArea areaStripper = new PDFTextStripperByArea();
                areaStripper.setSortByPosition(true);
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(document);
                Scanner scanner = new Scanner(text);
                ArrayList<String> quarters = new ArrayList<>();

                StringBuilder quarterString = new StringBuilder();
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.contains("Fall") || line.contains("Winter") || line.contains("Spring")
                            || line.contains("Summer") || line.contains("Advanced Placement")) {
                        quarters.add(quarterString.toString());
                        quarterString = new StringBuilder();
                    }
                    quarterString.append("\n").append(line);
                }

                String APCourses = quarters.get(1);
                String[] APLines = APCourses.split("\\r?\\n");
                for (String line : APLines) {
                    if (line.endsWith("CR")) {
                        Course course = new Course(getCourse(line));
                        course.setYear(0);
                        course.setQuarter(0);
                        transcriptMap.put(course.getCourseCode(), course);
                    }
                }

                int startingYear = Integer.parseInt(quarters.get(2).substring(1, 5));
                for (int i = 2; i < quarters.size(); i++) {
                    int year = Integer.parseInt(quarters.get(i).substring(1, 5));
                    int quarterNum = getQuarterNum(quarters.get(i));

                    currentQuarter = quarterNum;
                    String[] lines = quarters.get(i).split("\\r?\\n");
                    for (String line : lines) {
                        if (!line.contains("SSN")) {
                            Course course = new Course(getCourse(line));
                            course.setQuarter(quarterNum);
                            course.setYear(year - startingYear + 1);
                            transcriptMap.put(course.getCourseCode(), course);
                        }
                    }
                }
            }
            document.close();
        } catch (Exception e) {
            throw new Exception("PDF selected has an invalid format");
        }

    }

    public HashMap<String, Course> getTranscriptMap() {
        return transcriptMap;
    }
    public int getCurrentQuarter() {
        return currentQuarter;
    }

    /**
     * Helper method
     * @param line
     * @return
     */
    private String getCourse(String line) {
        for(String prefix: coursePrefixes) {
            if(line.startsWith(prefix)) {
                return line.split(" ")[0];
            }
        }
        return "";
    }

    /**
     * Helper method
     * @param quarter
     * @return
     */
    private int getQuarterNum(String quarter) {
        int quarterNum = 0;
        if(quarter.contains("Fall")) {
            quarterNum = 1;
        } else if(quarter.contains("Winter")) {
            quarterNum = 2;
        } else if(quarter.contains("Spring")) {
            quarterNum = 3;
        } else if(quarter.contains("Summer")) {
            quarterNum = 4;
        }
        return quarterNum;
    }
}