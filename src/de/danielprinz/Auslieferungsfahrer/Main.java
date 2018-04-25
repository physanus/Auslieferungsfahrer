package de.danielprinz.Auslieferungsfahrer;

import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.Toolkit;
import de.danielprinz.Auslieferungsfahrer.enums.Progress;
import de.danielprinz.Auslieferungsfahrer.gui.AlertBox;
import de.danielprinz.Auslieferungsfahrer.handlers.SettingsHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Main extends Application {

    public static final boolean DEBUG = true; // TODO disable debug for release
    public static final SettingsHandler SETTINGS_HANDLER = new SettingsHandler(100, 0.9, 1.2, 100);
    private final static String WINDOW_TITLE = "Der Auslieferungsfahrer";

    private static Stage window;

    public static void main(String[] args) {
        launch(args);
    }



    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        window.setTitle(WINDOW_TITLE);
        window.getIcons().add(new Image(Main.class.getResourceAsStream("delivery-truck.png")));

        MenuBar menuBar = new MenuBar();

        //File menu
        Menu fileMenu = new Menu("Datei");
        MenuItem settings = new MenuItem("Einstellungen");
        settings.setOnAction(e -> {
            SettingsGUI.display();
        });
        fileMenu.getItems().add(settings);
        //fileMenu.getItems().add(new SeparatorMenuItem());
        menuBar.getMenus().addAll(fileMenu);


        GridPane mainPane = new GridPane();
        mainPane.add(menuBar, 0, 0);

        // Grid pane
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);
        mainPane.add(grid, 0, 1);

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
            if(DEBUG) {
                // Set predefined values
                textFields.get(0).setText("Aspersdorfer Str. 11b, 2020 Gemeinde Hollabrunn");
                textFields.get(1).setText("Brunnthalgasse 10, 2020 Hollabrunn");
                textFields.get(2).setText("Aspersdorfer Str. 34, 2020 Gemeinde Hollabrunn");
                textFields.get(3).setText("Im Dorf 63, 2020 Gemeinde Hollabrunn");
                textFields.get(4).setText("Sonnberger Str. 61, 2020 Gemeinde Hollabrunn");
                textFields.get(5).setText("Brunnengasse 46, 67454 Haßloch");
            }

            if(textFields.get(0).getText().equals("")) {
                AlertBox.display("Fehler: Startpunkt fehlt", "Bitte gib einen Startpunk an, bevor du eine Route planst!");
                return;
            }
            if(textFields.get(1).getText().equals("")) {
                AlertBox.display("Fehler: Zielort fehlt", "Bitte gib mindestens einen Zielort ein!");
                return;
            }

            search.setDisable(true);
            for(TextField textField : textFields) textField.setDisable(true);
            menuBar.setDisable(true);
            ArrayList<String> waypoints = new ArrayList<>(textFields.stream().map(TextInputControl::getText).filter(s -> !s.equals("")).collect(Collectors.toList()));

            new Thread(() -> {
                try {
                    GoogleAPI.analyze(waypoints);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                Platform.runLater(() -> {
                    search.setDisable(false);
                    for(TextField textField : textFields) textField.setDisable(false);
                    menuBar.setDisable(false);
                });
            }).start();


        });


        // Add everything to grid
        grid.getChildren().addAll(labels);
        grid.getChildren().addAll(textFields);
        grid.getChildren().addAll(search);

        Scene scene = new Scene(mainPane, 400, 430);
        window.setScene(scene);
        window.show();

    }


    public static void setProgress(Progress progress) {
        if(progress.equals(Progress.FINISHED)) {
            Platform.runLater(() -> window.setTitle(WINDOW_TITLE));
            return;
        }
        Platform.runLater(() -> window.setTitle(WINDOW_TITLE + " (" + progress.getProgressNumber() + "/" + (Progress.FINISHED.getProgressNumber() - 1) + ")"));
    }

}
