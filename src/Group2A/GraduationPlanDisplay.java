package Group2A;

import com.google.common.collect.HashBasedTable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.List;

public class GraduationPlanDisplay {
    public String chosenMoveCourseCode;

    public void displayPlan(GraduationPlan plan, HBox mainContainer, VBox moveSection){
        mainContainer.getChildren().clear();
        makeDisplay(mainContainer, moveSection, plan);
    }

    private void makeDisplay(HBox container, VBox moveSection, GraduationPlan plan){
        //Formatted Year, Quarter
        HashBasedTable<Integer, Integer, ObservableList<String>> lists = HashBasedTable.create();
        lists.put(0, 0, FXCollections.observableArrayList());

        List<Node> allCourses = plan.getAllCourses();
        for(Node node : allCourses){
            checkAndCreateYear(node.getYear(), container, lists);
            lists.get(node.getYear(), node.getQuarter()).add(node.getCourse().getCourseCode());
        }

        createNonScheduledCourseSection(container, moveSection, lists.get(0,0));
    }

    private void createNonScheduledCourseSection(HBox container, VBox moveSection, ObservableList<String> courses){
        VBox section = new VBox();
        VBox preCollegeSection = new VBox();
        Label listLabel = new Label("Pre-College Courses");
        ListView<String> list = new ListView<>(courses);
        list.setMaxHeight(250);
        preCollegeSection.getChildren().add(listLabel);
        preCollegeSection.getChildren().add(list);
        preCollegeSection.setPadding(new Insets(20));

        section.getChildren().addAll(preCollegeSection, moveSection);
        container.getChildren().add(section);
    }

    private void checkAndCreateYear(int year, HBox container, HashBasedTable<Integer, Integer, ObservableList<String>> lists){
        ObservableList<javafx.scene.Node> subPanes = container.getChildren();
        for(int i = subPanes.size(); i < year; i++){
            VBox subPane = new VBox();
            Label yearLabel = new Label("Year " + (i+1));
            subPane.getChildren().add(yearLabel);
            for(int c = 0; c < 3; c++){
                VBox quarterContainer = new VBox();
                quarterContainer.setPadding(new Insets(5, 5, 5, 5));
                //Make Header
                Label header = new Label(translateQuarter(c) + " Quarter");
                header.setPadding(new Insets(5, 5, 5, 5));

                ObservableList<String> listContents = FXCollections.observableArrayList();
                ListView<String> list = new ListView<>(listContents);
                list.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        chosenMoveCourseCode = list.getSelectionModel().getSelectedItem();
                    }
                });
                quarterContainer.getChildren().add(header);
                quarterContainer.getChildren().add(list);
                subPane.getChildren().add(quarterContainer);
                lists.put(i+1, c+1, listContents);
            }
            container.getChildren().add(subPane);
        }
    }

    private String translateQuarter(int quarter){
        switch(quarter){
            case 0:
                return "Fall";
            case 1:
                return "Winter";
            case 2:
                return "Spring";
        }
        return "Invalid Quarter";
    }
}
