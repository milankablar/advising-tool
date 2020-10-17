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

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.FileInputStream;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Advising.fxml"));
        primaryStage.setTitle("MSOE Advising Application");
        primaryStage.setScene(new Scene(root));
        Image image = new Image(new FileInputStream("./resources/Images/MSOE.png"));
        primaryStage.getIcons().add(image);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);

    }
}
