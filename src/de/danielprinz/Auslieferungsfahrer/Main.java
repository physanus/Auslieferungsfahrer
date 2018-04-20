package de.danielprinz.Auslieferungsfahrer;

import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.Toolkit;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Main extends Application {

    public static final boolean DEBUG = true;

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        Stage window = primaryStage;
        window.setTitle("Der Auslieferungsfahrer");

        // Grid pane
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);


        // Create all labels and buttons
        FontLoader fontLoader = Toolkit.getToolkit().getFontLoader();
        ArrayList<Label> labels = new ArrayList<>();
        ArrayList<TextField> textFields = new ArrayList<>();
        for(int i = 0; i <= 10; i++) {
            String title = i == 0 ? "Startpunkt: " : "Zwischenstopp " + i + ":";

            Label label = new Label(title);
            label.setMinWidth(fontLoader.computeStringWidth(label.getText(), label.getFont()));
            GridPane.setConstraints(label, 0, i);
            labels.add(label);

            TextField textField = new TextField();
            textField.setPrefColumnCount(24);
            GridPane.setConstraints(textField, 1, i);
            textFields.add(textField);
        }

        Button search = new Button("Suche");
        GridPane.setConstraints(search, 1, 11);
        search.setOnAction(e -> {
            if(!DEBUG) {
                // Set predefined values
                textFields.get(0).setText("Aspersdorfer Str. 11b, 2020 Gemeinde Hollabrunn");
                textFields.get(1).setText("Brunnthalgasse 10, 2020 Hollabrunn");
                textFields.get(2).setText("Aspersdorfer Str. 34, 2020 Gemeinde Hollabrunn");
                textFields.get(3).setText("Im Dorf 63, 2020 Gemeinde Hollabrunn");
                textFields.get(4).setText("Sonnberger Str. 61, 2020 Gemeinde Hollabrunn");
            }

            if(textFields.get(0).getText().equals("")) {
                AlertBox.display("Fehler: Startpunkt fehlt", "Bitte gib einen Startpunk an, bevor du eine Route planst!");
                return;
            }
            if(textFields.get(1).getText().equals("")) {
                AlertBox.display("Fehler: Zielort fehlt", "Bitte gib mindestens einen Zielort ein!");
                return;
            }

            // start navigation stuff


        });


        //Add everything to grid
        grid.getChildren().addAll(labels);
        grid.getChildren().addAll(textFields);
        grid.getChildren().addAll(search);

        Scene scene = new Scene(grid, 400, 410);
        window.setScene(scene);
        window.show();

    }
}
