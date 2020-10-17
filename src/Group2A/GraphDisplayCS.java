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

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Graph display is responsible for displaying CS curriculum and highlighting completed courses
 * @author Sara Alsudeer
 */
public class GraphDisplayCS {

    private Scene scene;
    private Stage stage;
    private ImageView done;

    private PDFUploader pdfUploader;
    private HashMap<String, Course> completed;


    public GraphDisplayCS(PDFUploader pdfUploader) throws Exception {
        // for testing purposes
        // remove later
        //pdfUploader = new PDFUploader();
        //pdfUploader.loadTranscriptPDF("./resources/transcript.pdf");
        completed = pdfUploader.getTranscriptMap();

        try {
            loadScene();
            for(String course: completed.keySet()) {
                if(!course.equals("")) markCompleted(course);
            }

        } catch (IOException e){
            e.printStackTrace();
            throw new IOException("CurriculumDisplay failed to load");
        }
    }

    /**
     * Loads the scene fxml file
     * @throws IOException if the fxml fails to load
     */
    private void loadScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CurriculumDisplayCS.fxml"));
        loader.setController(this);
        Parent root = loader.load();
        scene = new Scene(root);
    }

    /** Sets the scene
     * @param mainStage The stage to display the observer
     */
    public void setAsScene(Stage mainStage) throws FileNotFoundException {
        mainStage.setScene(scene);
        mainStage.show();
        this.stage = mainStage;
        Image image = new Image(new FileInputStream("./resources/Images/MSOE.png"));
        mainStage.getIcons().add(image);
        mainStage.setTitle("MSOE Advising Application");
    }

    /**
     * Highlight completed courses in the curriculum to show student's progress
     */
    public void markCompleted(String completedCourse){
        Pane paneCS = (Pane) scene.lookup("#paneCS");
        done = new ImageView(new Image("file:resources/Images/check.png"));
        done.setPreserveRatio(true);    // ensure ratio preserved when scaling the image
        done.setFitWidth(50.0);         // scale image to be a reasonable size
        if (completedCourse.startsWith("HU") || completedCourse.startsWith("SS")) {
            markHUSS();
        } else {
            Node rectangle = scene.lookup("#" + completedCourse);
            if (rectangle != null) {
                done.setLayoutX(rectangle.getLayoutX() - 10);
                done.setLayoutY(rectangle.getLayoutY() - 35);
                paneCS.getChildren().addAll(done);
            }
        }

    }

    /**
     * Handle highlighting completed HU/SS electives
     */
    private void markHUSS(){
        String courseID = "HUSS";
        Pane paneCS = (Pane) scene.lookup("#paneCS");
        done = new ImageView(new Image("file:resources/Images/check.png"));
        done.setPreserveRatio(true);    // ensure ratio preserved when scaling the image
        done.setFitWidth(50.0);         // scale image to be a reasonable size
        Node rectangle = scene.lookup("#" + courseID);
        done.setLayoutX(rectangle.getLayoutX() - 10);
        done.setLayoutY(rectangle.getLayoutY() - 35);
        paneCS.getChildren().add(done);

    }

}
