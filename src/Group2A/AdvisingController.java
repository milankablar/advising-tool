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


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.Map;
import java.util.List;


/**
 * AdvisingController is responsible for updating the GUI
 */
public class AdvisingController {
    private static final String[] MAJORS = {"CS", "SE"};

    @FXML
    private AnchorPane studentPrereqPane;
    @FXML
    private AnchorPane studentRecommendPane;
    @FXML
    private AnchorPane advisorRecommendPane;
    @FXML
    private TabPane studentTabPane;
    @FXML
    private TabPane advisorTabPane;
    @FXML
    private HBox graduationPlanPane;
    @FXML
    public ChoiceBox<String> majorsBox = new ChoiceBox<>();
    @FXML
    private Canvas prereqCanvas = new Canvas();
    @FXML
    private Stage curriculumWindow = new Stage();

    public ListView<String> coursePrerequisites;
    public ListView<String> courseList;
    public ListView<String> courseQuarter;
    public ListView<String> courseCodeList;
    public ListView<String> courseCredit;
    public VBox moveSection;
    public TextField searchBar;
    public TextArea status;

    private CourseStorage courseStorage = new CourseStorage();
    private PDFUploader pdfUploader = new PDFUploader();
    private GraduationPlanCreator gradCreator = new GraduationPlanCreator(courseStorage, pdfUploader);
    private GraduationPlan gradPlan;
    GraduationPlanDisplay gradDisplay;
    GraphDisplayCS graphDisplayCS;
    GraphDisplaySE graphDisplaySE;

    private static final String offeringsPath = "./resources/CSVs/offerings.csv";
    private static final String prerequisitePath = "./resources/CSVs/prerequisites.csv";
    private static final String curriculumPath = "./resources/CSVs/curriculum.csv";
    private static final String transcriptPath = "./resources/transcript.pdf";

    public static void main(String[] args) throws Exception {
        AdvisingController controller = new AdvisingController();
        controller.initialize();
    }


    /**
     * Set up the GUI
     *
     * @throws IOException - Throw an exception when failure occurs
     */
    @FXML
    private void initialize() throws Exception {
        // set up the choice boxes
        courseStorage.loadOfferingCSV(offeringsPath);
        courseStorage.loadPrerequisiteCSV(prerequisitePath);
        courseStorage.loadCurriculumCSV(curriculumPath);

        // display all courses
        for (Course c : courseStorage.getSortedCourses()) courseCodeList.getItems().add(c.getCourseCode());

        // display majors in a list view
        for (String s : MAJORS) majorsBox.getItems().add(s);

        majorsBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                try {
                    updateCourseAttributes();
                } catch (IOException e) { }
            }
        });
    }

    @FXML
    private void loadMultipleFiles() throws FileNotFoundException {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

            /* Show open file dialog to select multiple files. */
            List<File> fileList = fileChooser.showOpenMultipleDialog(null);
            if (fileList != null && fileList.size() > 0) {
                for (File file : fileList) {
                    if (file.getName().equals("curriculum.csv")) {
                        courseStorage.loadCurriculumCSV(file.getAbsolutePath());
                    } else if (file.getName().equals("prerequisites.csv")) {
                        courseStorage.loadPrerequisiteCSV(file.getAbsolutePath());
                    } else if (file.getName().equals("offerings.csv")) {
                        courseStorage.loadOfferingCSV(file.getAbsolutePath());
                    } else {
                        throw new NullPointerException();
                    }
                }
            } else {
                throw new NullPointerException();
            }
        } catch (NullPointerException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Load proper files: prerequisites.csv, curriculum.csv, offerings.csv");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    /**
     * Loads offerings.csv file
     *
     * @throws FileNotFoundException - Throw an exception when the file cannot be found
     */
    @FXML
    private void loadOfferingFile() throws FileNotFoundException {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                String path = file.getAbsolutePath();
                courseStorage.loadOfferingCSV(path);
            } else {
                throw new NullPointerException();
            }

        } catch (NullPointerException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Load a proper file offerings.csv.");
            alert.setHeaderText("Error");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    /**
     * Loads Prerequisite.csv file
     *
     * @throws FileNotFoundException - Throw an exception when the file cannot be found
     */
    @FXML
    private void loadPrerequisiteFile() throws FileNotFoundException {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                String path = file.getAbsolutePath();
                courseStorage.loadPrerequisiteCSV(path);
            } else {
                throw new NullPointerException();
            }
        } catch (NullPointerException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Load a proper file prerequisites.csv.");
            alert.setHeaderText("Error");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    /**
     * Loads Curriculum.csv file
     *
     * @throws FileNotFoundException - Throw an exception when the file cannot be found
     */
    @FXML
    private void loadCurriculumFile() throws FileNotFoundException {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                String path = file.getAbsolutePath();
                courseStorage.loadCurriculumCSV(path);
            } else {
                throw new NullPointerException();
            }
        } catch (NullPointerException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Load a proper file curriculum.csv.");
            alert.setHeaderText("Error");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    /**
     * Upload an official transcript as PDF
     */
    @FXML
    public void loadTranscript() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            File selectedFile = fileChooser.showOpenDialog(null);
            pdfUploader.loadTranscriptPDF(selectedFile.getCanonicalPath());
            studentTabPane.setDisable(false);
            advisorTabPane.setDisable(false);

            gradPlan = gradCreator.makeGraduationPlan("SE");
            showGraduationPlan();
            graphDisplayCS = new GraphDisplayCS(pdfUploader);
            graphDisplaySE = new GraphDisplaySE(pdfUploader);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("No File Selected");
            alert.setContentText("Open unofficial transcript by selecting the pdf file");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Invalid PDF Selected");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }


    /**
     * Search method allows user to type a prefix to update the list view of courses
     *
     * @author Sara Alsudeer
     */
    @FXML
    public void search() {
        String prefix = searchBar.getText().toUpperCase();
        ArrayList<Course> coursesList= (ArrayList<Course>) courseStorage.getSortedCourses();
        ArrayList<Course> updatedList = new ArrayList<>();

        for (Course code : coursesList) {
            if (code.getCourseCode().contains(prefix)) updatedList.add(code);
        }

        courseCodeList.getItems().clear();
        for (Course course : updatedList) {
            courseCodeList.getItems().add(course.getCourseCode());
        }
    }

    @FXML
    private void updateCourseAttributes() throws IOException {
        if(courseCodeList.getSelectionModel().getSelectedItem() != null) {
            quarterOffering();
            coursePrerequisite();
        }
        if(!studentTabPane.isDisabled() && !advisorTabPane.isDisabled()) {
            createStudentRecommendationsTab();
            createAdvisorRecommendationsTab();
        }
    }

    @FXML
    private void updateTabs() throws IOException {
        createPrerequisiteTab();
        createStudentRecommendationsTab();
        createAdvisorRecommendationsTab();
    }


    private void quarterOffering() {
        courseQuarter.getItems().clear();
        courseList.getItems().clear();
        courseCredit.getItems().clear();
        String courseCode = courseCodeList.getSelectionModel().getSelectedItem();

        if (courseCode != null) {
            Course course = courseStorage.getCourse(courseCode);
            courseList.getItems().add(course.getDescription());
            courseCredit.getItems().add(String.valueOf(course.getCredits()));
            String quarter = "";

            String majorChoice = majorsBox.getSelectionModel().getSelectedItem();
            int quarterInt = (courseStorage.getOfferings(courseCode, majorChoice));

            if (quarterInt != -1) {
                if (quarterInt == 1) {
                    quarter = "Fall";
                } else if (quarterInt == 2) {
                    quarter = "Winter";
                } else if (quarterInt == 3) {
                    quarter = "Spring";
                }
            } else {
                quarter = "Not Offered";
            }
            courseQuarter.getItems().add(quarter);
        } else {
            courseList.getItems().add("No Course Selected.");
        }
    }

    /**
     * Display course prerequisites in FXML list view
     */
    private void coursePrerequisite() {
        coursePrerequisites.getItems().clear();
        String prerequisite;
        String courseCode = courseCodeList.getSelectionModel().getSelectedItem();
        Course course = courseStorage.getCourse(courseCode);

        // get the prerequisites of the selected course
        ArrayList<String> prereqList = course.getPrereqList();

        if (prereqList.isEmpty()) {
            coursePrerequisites.getItems().add("No prerequisites");
        } else {
            for (String p : prereqList) {
                // for display purposes
                switch (p) {
                    case "SR":
                        prerequisite = "Senior Standing";
                        break;
                    case "JR":
                        prerequisite = "Junior Standing";
                        break;
                    case "SO":
                        prerequisite = "Sophomore Standing";
                        break;
                    default:
                        prerequisite = p;
                        break;
                }
                coursePrerequisites.getItems().add(prerequisite);
            }
        }
    }

    public ArrayList<String> recommendCourses() throws IOException {
        String majorPrefix = majorsBox.getSelectionModel().getSelectedItem();
        ArrayList<String> recommendations = new ArrayList<>();
        if(majorPrefix == null) {
            System.out.println("No major prefix selected");
        } else {
            int numRecommendations = 5;
            int nextQuarter = pdfUploader.getCurrentQuarter() + 1;
            if (nextQuarter == 4) {
                nextQuarter = 1;
            }
            HashMap<String, Course> transcript = pdfUploader.getTranscriptMap();
            HashMap<String, Course> curriculum = courseStorage.getCurriculum(majorPrefix);

            for (Map.Entry<String, Course> curriculumCourse : curriculum.entrySet()) {
                String curriculumCourseCode = curriculumCourse.getKey();
                if (courseStorage.getOfferings(curriculumCourseCode, majorPrefix) == nextQuarter) {
                    if (!transcript.containsKey(curriculumCourseCode)) {
                        recommendations.add(curriculumCourseCode);
                    }
                }

                if (recommendations.size() == numRecommendations) {
                    return recommendations;
                }
            }
        }
        return recommendations;
    }

    private void graphPrerequisite(List<String> prereqList, String course, Canvas prerequisiteCanvas) {
        GraphicsContext graphicsContext = prerequisiteCanvas.getGraphicsContext2D();
        double middle = prerequisiteCanvas.getHeight() / 2;
        graphicsContext.setStroke(Color.BLACK);


        if (prereqList.isEmpty() || prereqList.get(0).equals("")) {
            if (course != null) {
                graphicsContext.fillText("No Prerequisites", (middle) - 50, (middle) - 50);
                graphicsContext.strokeRoundRect((middle) - 55, (middle) - 75, 95, 55, 20, 20);
            } else {
                graphicsContext.fillText("No Course Selected", (middle) - 50, (middle) - 50);
                graphicsContext.strokeRoundRect((middle) - 55, (middle) - 75, 120, 55, 20, 20);

            }
        } else {
            if (prereqList.size() == 1 && !prereqList.get(0).contains("|")) {

                graphicsContext.strokeRoundRect(50, (middle) - 75, 95, 55, 20, 20);
                graphicsContext.fillText(prereqList.get(0), 55, (middle) - 50);
                graphicsContext.strokeLine(145, (middle) - 50, 195, (middle) - 50);
                graphicsContext.strokeRoundRect(195, (middle) - 75, 95, 55, 20, 20);
                graphicsContext.fillText(course, 200, (middle) - 50);

            } else if (prereqList.size() == 1) {

                String[] strings = prereqList.get(0).split("\\|");

                if (strings.length == 2) {
                    graphicsContext.strokeRoundRect(50, middle - 75, 95, 95, 20, 20);
                    graphicsContext.fillText(strings[0], 55, middle - 50);
                    graphicsContext.fillText("OR", 65, middle - 25);
                    graphicsContext.fillText(strings[1], 55, middle);
                    graphicsContext.strokeLine(145, (middle) - 50, 195, (middle) - 50);
                    graphicsContext.strokeRoundRect(195, (middle) - 75, 95, 55, 20, 20);
                    graphicsContext.fillText(course, 200, (middle) - 50);
                } else if (strings.length == 3) {
                    graphicsContext.strokeRoundRect(50, middle - 75, 95, 135, 20, 20);
                    graphicsContext.fillText(strings[0], 55, middle - 50);
                    graphicsContext.fillText("OR", 65, middle - 25);
                    graphicsContext.fillText(strings[1], 55, middle);
                    graphicsContext.fillText("OR", 65, middle + 25);
                    graphicsContext.fillText(strings[2], 55, middle + 50);
                    graphicsContext.strokeLine(145, (middle) - 50, 195, (middle) - 50);
                    graphicsContext.strokeRoundRect(195, (middle) - 75, 95, 55, 20, 20);
                    graphicsContext.fillText(course, 200, (middle) - 50);

                } else if (strings.length == 4) {
                    graphicsContext.strokeRoundRect(50, middle - 75, 95, 185, 20, 20);
                    graphicsContext.fillText(strings[0], 55, middle - 50);
                    graphicsContext.fillText("OR", 65, middle - 25);
                    graphicsContext.fillText(strings[1], 55, middle);
                    graphicsContext.fillText("OR", 65, middle + 25);
                    graphicsContext.fillText(strings[2], 55, middle + 50);
                    graphicsContext.fillText("OR", 65, middle + 75);
                    graphicsContext.fillText(strings[3], 55, middle + 100);
                    graphicsContext.strokeLine(145, (middle) - 50, 195, (middle) - 50);
                    graphicsContext.strokeRoundRect(195, (middle) - 75, 95, 55, 20, 20);
                    graphicsContext.fillText(course, 200, (middle) - 50);
                }

            }
            if (prereqList.size() == 2 && !prereqList.get(0).contains("|") && !prereqList.get(1).contains("|")) {
                graphicsContext.strokeRoundRect(50, (middle) - 150, 95, 55, 20, 20);
                graphicsContext.fillText(prereqList.get(0), 55, (middle) - 125);
                graphicsContext.strokeRoundRect(50, (middle) + 50, 95, 55, 20, 20);
                graphicsContext.fillText(prereqList.get(1), 55, (middle) + 75);
                graphicsContext.strokeLine(145, (middle) - 120, 195, (middle) - 20);
                graphicsContext.strokeLine(145, (middle) + 70, 195, (middle) - 20);
                graphicsContext.strokeRoundRect(195, (middle) - 50, 95, 55, 20, 20);
                graphicsContext.fillText(course, 200, (middle) - 25);
            } else if (prereqList.size() == 2) {
                String[] strings1 = prereqList.get(0).split("\\|");
                String[] strings2 = prereqList.get(1).split("\\|");

                if (strings1.length == 2 && strings2.length == 2) {
                    graphicsContext.strokeRoundRect(50, middle - 175, 95, 95, 20, 20);
                    graphicsContext.fillText(strings1[0], 55, middle - 150);
                    graphicsContext.fillText("OR", 65, middle - 125);
                    graphicsContext.fillText(strings1[1], 55, middle - 100);
                    graphicsContext.strokeLine(145, (middle) - 150, 195, (middle) - 50);

                    graphicsContext.strokeRoundRect(50, middle - 75, 95, 95, 20, 20);
                    graphicsContext.fillText(strings2[0], 55, middle - 50);
                    graphicsContext.fillText("OR", 65, middle - 25);
                    graphicsContext.fillText(strings2[1], 55, middle);
                    graphicsContext.strokeLine(145, (middle) - 50, 195, (middle) - 50);

                    graphicsContext.strokeRoundRect(195, (middle) - 75, 95, 55, 20, 20);
                    graphicsContext.fillText(course, 200, (middle) - 50);

                }

                if (strings1.length == 2 && strings2.length == 1) {
                    graphicsContext.strokeRoundRect(50, middle - 175, 95, 95, 20, 20);
                    graphicsContext.fillText(strings1[0], 55, middle - 150);
                    graphicsContext.fillText("OR", 65, middle - 125);
                    graphicsContext.fillText(strings1[1], 55, middle - 100);
                    graphicsContext.strokeLine(145, (middle) - 150, 195, (middle) - 50);

                    graphicsContext.strokeRoundRect(50, (middle) + 50, 95, 55, 20, 20);
                    graphicsContext.fillText(prereqList.get(1), 55, (middle) + 75);
                    graphicsContext.strokeLine(145, (middle) + 70, 195, (middle) - 20);

                    graphicsContext.strokeRoundRect(195, (middle) - 75, 95, 55, 20, 20);
                    graphicsContext.fillText(course, 200, (middle) - 50);


                } else if (strings1.length == 1 && strings2.length == 2) {

                    graphicsContext.strokeRoundRect(50, middle - 75, 95, 95, 20, 20);
                    graphicsContext.fillText(strings2[0], 55, middle - 50);
                    graphicsContext.fillText("OR", 65, middle - 25);
                    graphicsContext.fillText(strings2[1], 55, middle);
                    graphicsContext.strokeLine(145, (middle) - 50, 195, (middle) - 50);

                    graphicsContext.strokeRoundRect(195, (middle) - 75, 95, 55, 20, 20);
                    graphicsContext.fillText(course, 200, (middle) - 50);
                    graphicsContext.strokeRoundRect(50, (middle) - 150, 95, 55, 20, 20);
                    graphicsContext.fillText(prereqList.get(0), 55, (middle) - 125);
                    graphicsContext.strokeLine(145, (middle) - 120, 195, (middle) - 20);


                }
            }
            if (prereqList.size() == 3 && !prereqList.get(0).contains("|") && !prereqList.get(1).contains("|") && !prereqList.get(2).contains("|")) {
                graphicsContext.strokeRoundRect(50, (middle) - 150, 95, 55, 20, 20);
                graphicsContext.fillText(prereqList.get(0), 55, (middle) - 125);
                graphicsContext.strokeRoundRect(50, (middle) - 40, 95, 55, 20, 20);
                graphicsContext.fillText(prereqList.get(1), 55, (middle) - 15);
                graphicsContext.strokeRoundRect(50, (middle) + 50, 95, 55, 20, 20);
                graphicsContext.fillText(prereqList.get(2), 55, (middle) + 75);
                graphicsContext.strokeLine(145, (middle) - 120, 195, (middle) - 20);
                graphicsContext.strokeLine(145, (middle) - 20, 195, (middle) - 20);
                graphicsContext.strokeLine(145, (middle) + 70, 195, (middle) - 20);
                graphicsContext.strokeRoundRect(195, (middle) - 50, 95, 55, 20, 20);
                graphicsContext.fillText(course, 200, (middle) - 25);

            } else if (prereqList.size() == 3) {
                String[] strings1 = prereqList.get(0).split("\\|");
                String[] strings2 = prereqList.get(1).split("\\|");
                String[] strings3 = prereqList.get(2).split("\\|");

                if (strings1.length == 2 && strings2.length == 2 && strings3.length == 2) {

                } else if (strings1.length == 3 && strings2.length == 3 && strings3.length == 3) {

                } else if (strings1.length == 4 && strings2.length == 4 && strings3.length == 4) {

                }

            }
            if (prereqList.size() == 4 && !prereqList.get(0).contains("|") && !prereqList.get(1).contains("|")
                    && !prereqList.get(2).contains("|") && !prereqList.get(3).contains("|")) {

                graphicsContext.strokeRoundRect(50, (middle) - 150, 95, 55, 20, 20);
                graphicsContext.fillText(prereqList.get(0), 55, (middle) - 125);

                graphicsContext.strokeRoundRect(50, middle - 90, 95, 55, 20, 20);
                graphicsContext.fillText(prereqList.get(1), 55, middle - 75);

                graphicsContext.strokeRoundRect(50, middle - 10, 95, 55, 20, 20);
                graphicsContext.fillText(prereqList.get(2), 55, middle + 10);

                graphicsContext.strokeRoundRect(50, (middle) + 50, 95, 55, 20, 20);
                graphicsContext.fillText(prereqList.get(3), 55, (middle) + 75);

                graphicsContext.strokeLine(145, (middle) - 120, 195, (middle) - 20);
                graphicsContext.strokeLine(145, middle - 70, 195, middle - 20);
                graphicsContext.strokeLine(145, middle + 10, 195, middle - 20);
                graphicsContext.strokeLine(145, (middle) + 70, 195, (middle) - 20);

                graphicsContext.strokeRoundRect(195, (middle) - 50, 95, 55, 20, 20);
                graphicsContext.fillText(course, 200, (middle) - 25);
            }
        }
    }

    @FXML
    public void createPrerequisiteTab() {
        VBox pane = new VBox();
        prereqCanvas.setHeight(500);
        prereqCanvas.setWidth(500);
        studentPrereqPane.setStyle("-fx-background-color: #FFFFFF");
        pane.getChildren().add(prereqCanvas);
        studentPrereqPane.getChildren().add(pane);

        String courseCode = courseCodeList.getSelectionModel().getSelectedItem();
        List<String> list = coursePrerequisites.getItems();
        GraphicsContext graphicsContext = prereqCanvas.getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, prereqCanvas.getHeight(), prereqCanvas.getWidth());
        graphPrerequisite(list, courseCode, prereqCanvas);
    }

    @FXML
    public void savePrerequisites() {
        Stage saveWindow = new Stage();
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(saveWindow);

        if (file != null) {
            try {
                WritableImage writableImage = new WritableImage((int) prereqCanvas.getWidth(), (int) prereqCanvas.getHeight());
                prereqCanvas.snapshot(null, writableImage);
                RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                ImageIO.write(renderedImage, "png", file);
            } catch (IOException ignored) {
            }
        }
    }

    @FXML
    public void createStudentRecommendationsTab() throws IOException {
        TextFlow pane = new TextFlow();
        pane.setStyle("  -fx-background-color: #FFFFFF");
        pane.getChildren().add(new Text("\n\t"));
        ArrayList<String> recommendedCourses = recommendCourses();

        String comma = "";
        for(int i = 0; i < recommendedCourses.size(); i++) {
            if(i != 0) {
                comma = ", ";
            }
            Text text = new Text(comma + recommendedCourses.get(i));
            text.setFont(new Font("Calisto MT", 20));
            text.setFill(Color.web("#b21616"));
            pane.getChildren().add(text);
        }
        studentRecommendPane.getChildren().add(pane);
    }


    public void createAdvisorRecommendationsTab() throws IOException {
        TextFlow pane = new TextFlow();
        pane.setStyle("  -fx-background-color: #FFFFFF");
        pane.getChildren().add(new Text("\n\t"));
        ArrayList<String> recommendedCourses = recommendCourses();

        String comma = "";
        for(int i = 0; i < recommendedCourses.size(); i++) {
            if(i != 0) {
                comma = ", ";
            }
            Text text = new Text(comma + recommendedCourses.get(i));
            text.setFont(new Font("Calisto MT", 20));
            text.setFill(Color.web("#b21616"));
            pane.getChildren().add(text);
        }
        advisorRecommendPane.getChildren().add(pane);
    }

    @FXML
    private void displayCurriculumSE() throws Exception {
        try {
            graphDisplaySE.setAsScene(curriculumWindow);
        } catch(NullPointerException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Transcript Not Uploaded");
            alert.setContentText("Upload a transcript PDF in order to see the SE curriculum");
            alert.showAndWait();
        }
    }

    @FXML
    private void displayCurriculumCS() throws Exception {
        try {
            graphDisplayCS.setAsScene(curriculumWindow);
        } catch(NullPointerException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Transcript Not Uploaded");
            alert.setContentText("Upload a transcript PDF in order to see the CS curriculum");
            alert.showAndWait();
        }
    }

    private void showGraduationPlan(){
        gradDisplay = new GraduationPlanDisplay();
        moveCourseGradPlan();
        gradDisplay.displayPlan(gradPlan, graduationPlanPane, moveSection);
    }

    private void moveCourseGradPlan() {
        moveSection = new VBox();
        Label moveLabel = new Label("Move Course");
        TextField quarterField = new TextField();
        quarterField.setMinWidth(20);
        quarterField.setPromptText("Quarter");
        TextField yearField = new TextField();
        yearField.setMinWidth(20);
        yearField.setPromptText("Year");
        Button moveButton = new Button();
        moveButton.setMinWidth(20);
        moveButton.setText("Move");
        moveButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    int moveQuarter = Integer.parseInt(quarterField.getText());
                    int moveYear = Integer.parseInt(yearField.getText());
                    String courseCode = gradDisplay.chosenMoveCourseCode;
                    if (courseStorage.getOfferings(courseCode, "SE") == moveQuarter
                            && moveYear >= 0 && moveYear <= 6) {
                        gradPlan.modifyCourse(courseCode, moveQuarter, moveYear);
                        showGraduationPlan();
                    }
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error Dialog");
                    alert.setHeaderText("Invalid Move Input");
                    alert.setContentText("Enter a valid year and quarter to move to");
                    alert.showAndWait();
                }

            }
        });
        moveSection.getChildren().addAll(moveLabel, quarterField, yearField, moveButton);
        moveSection.setPadding(new Insets(20));
    }
}
