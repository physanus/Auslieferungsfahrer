package de.danielprinz.Auslieferungsfahrer.gui;

import com.google.maps.model.DirectionsStep;
import com.google.maps.model.Distance;
import com.google.maps.model.Duration;
import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.Toolkit;
import de.danielprinz.Auslieferungsfahrer.Main;
import de.danielprinz.Auslieferungsfahrer.containers.RelationContainer;
import de.danielprinz.Auslieferungsfahrer.containers.RouteContainer;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;

public class RouteViewer {

    public static void display(ArrayList<RouteContainer> cheapestRoutes) {

        Stage window = new Stage();
        window.setTitle("Route");
        window.getIcons().add(new Image(Main.class.getResourceAsStream("delivery-truck.png")));
        window.initModality(Modality.APPLICATION_MODAL);

        FontLoader fontLoader = Toolkit.getToolkit().getFontLoader();

        // Grid pane
        GridPane grid = new GridPane();
        //grid.setPadding(new Insets(10, 10, 10, 10));
        //grid.setVgap(8);
        //grid.setHgap(10);



        RouteContainer routeContainer = cheapestRoutes.get(0);
        ImageView imageView = new ImageView(SwingFXUtils.toFXImage(routeContainer.getMap(), null));
        HBox hBox = new HBox();
        hBox.getChildren().add(imageView);
        GridPane.setConstraints(hBox, 0, 0);


        GridPane subgrid = new GridPane();
        subgrid.setPadding(new Insets(10, 10, 10, 10));
        subgrid.setVgap(8);
        subgrid.setHgap(10);

        ScrollPane scrollPane = new ScrollPane();
        Text text = new Text();
        text.setWrappingWidth(600);
        scrollPane.setMinWidth(620);
        //scrollPane.setFitToWidth(true);
        scrollPane.setContent(text);
        subgrid.add(scrollPane, 0, 0);
        GridPane.setConstraints(subgrid, 1, 0);

        StringBuilder sb = new StringBuilder();
        for(RelationContainer relationContainer : routeContainer.getRelations()) {
            Duration duration = relationContainer.getDuration();
            Distance distance = relationContainer.getDistance();

            for(DirectionsStep directionsStep : relationContainer.getDirectionsSteps()) {
                sb.append(directionsStep.htmlInstructions.replaceAll("", "").replaceAll("<.*?>", ""));
                sb.append("\n");
                if(duration != null && distance != null) {
                    sb.append("\tGesamtentfernung: " + distance.humanReadable);
                    sb.append("\n");
                    sb.append("\tGesamtzeit: " + duration.humanReadable);
                    sb.append("\n");
                    duration = null;
                    distance = null;
                }
                sb.append("\tEntfernung: " + directionsStep.distance.humanReadable);
                sb.append("\n");
                sb.append("\tZeit: " + directionsStep.duration.humanReadable);
                sb.append("\n");
            }
            sb.append("\n");
        }
        text.setText(sb.toString());


        grid.getChildren().addAll(hBox, subgrid);

        Scene scene = new Scene(grid, 1280, 640);
        window.setScene(scene);
        window.showAndWait();

    }

}
