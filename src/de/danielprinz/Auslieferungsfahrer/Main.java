package de.danielprinz.Auslieferungsfahrer;

import com.google.maps.errors.ApiException;
import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.Toolkit;
import de.danielprinz.Auslieferungsfahrer.containers.RouteContainer;
import de.danielprinz.Auslieferungsfahrer.enums.Progress;
import de.danielprinz.Auslieferungsfahrer.gui.AlertBox;
import de.danielprinz.Auslieferungsfahrer.gui.RouteViewer;
import de.danielprinz.Auslieferungsfahrer.gui.SettingsGUI;
import de.danielprinz.Auslieferungsfahrer.handlers.SettingsHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
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
    public static final SettingsHandler SETTINGS_HANDLER = new SettingsHandler(100, 0.9, 1.2);
    private final static String WINDOW_TITLE = "Der Auslieferungsfahrer";

    private static Stage window;
    private static MenuBar menuBar;
    private static Button search;
    private static ArrayList<TextField> textFields;
    private static ArrayList<Label> labels;

    public static void main(String[] args) {
        launch(args);
    }



    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        window.setTitle(WINDOW_TITLE);
        window.getIcons().add(new Image(Main.class.getResourceAsStream("delivery-truck.png")));

        menuBar = new MenuBar();

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
        labels = new ArrayList<Label>();
        textFields = new ArrayList<>();
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

        search = new Button("Suche");
        GridPane.setConstraints(search, 1, 11);
        search.setOnAction((ActionEvent e) -> {
            if(DEBUG) {
                // Set predefined values
                textFields.get(0).setText("Aspersdorfer Str. 11b, 2020 Gemeinde Hollabrunn");
                textFields.get(1).setText("Brunnthalgasse 10, 2020 Hollabrunn");
                textFields.get(2).setText("Aspersdorfer Str. 34, 2020 Gemeinde Hollabrunn");
                textFields.get(3).setText("Im Dorf 63, 2020 Gemeinde Hollabrunn");
                //textFields.get(4).setText("Sonnberger Str. 61, 2020 Gemeinde Hollabrunn");

                /*textFields.get(0).setText("Gerichtsberg Kellerpl. 1918/4, 2020 Gemeinde Hollabrunn");
                textFields.get(1).setText("Ernstbrunner Str. 40, 2032 Enzersdorf im Thale");
                textFields.get(2).setText("Stronsdorf 106, 2153 Stronsdorf");
                textFields.get(3).setText("Gaubitsch 150, 2154 Gaubitsch");
                textFields.get(4).setText("Kirchengasse 5, 2151 Asparn an der Zaya");
                textFields.get(5).setText("Laaer Str. 10, 2054 Haugsdorf");
                textFields.get(6).setText("Nappersdorf 192, 2023 Nappersdorf");
                //textFields.get(7).setText("Weyerburg, Hl. Kunigunde, 2031 Weyerburg");
                textFields.get(7).setText("Im Dorf 63, 2020 Gemeinde Hollabrunn");
                textFields.get(8).setText("Hauptpl. 20, 3714 Sitzendorf an der Schmida");
                textFields.get(9).setText("Mittergrabern 39, 2020 Grabern");
                textFields.get(10).setText("Hauptpl. 19-25, 2013 Göllersdorf");*/
                /*textFields.get(0).setText("Brunnthalgasse 10, 2020 Hollabrunn");
                textFields.get(1).setText("Großharras 268, 2034 Großharras");
                textFields.get(2).setText("Brunnthalgasse 10, 2020 Hollabrunn");*/
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

                    ArrayList<RouteContainer> cheapestRoutes = GoogleAPI.analyze(waypoints);
                    Platform.runLater(() -> {
                        RouteViewer.display(cheapestRoutes);
                        releaseUILocks();
                    });

                } catch (IOException | InterruptedException | ApiException e1) {
                    Platform.runLater(() -> AlertBox.display("Fehler: Verbindung", "Es konnte keine Verbindung hergestellt werden!"));
                    e1.printStackTrace();
                    Platform.runLater(() -> {
                        releaseUILocks();
                    });
                    return;
                }

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

    public static void releaseUILocks() {
        search.setDisable(false);
        for(TextField textField : textFields) textField.setDisable(false);
        menuBar.setDisable(false);
    }

}
