package de.danielprinz.Auslieferungsfahrer.gui;

import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.Toolkit;
import de.danielprinz.Auslieferungsfahrer.Main;
import de.danielprinz.Auslieferungsfahrer.containers.RouteContainer;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);


        RouteContainer routeContainer = cheapestRoutes.get(0);
        final ImageView imv = new ImageView();

        final Image image2 = SwingFXUtils.toFXImage(routeContainer.getMap(), null);
        imv.setImage(image2);

        final HBox pictureRegion = new HBox();

        pictureRegion.getChildren().add(imv);
        grid.add(pictureRegion, 1, 1);


/*
        RouteContainer routeContainer = cheapestRoutes.get(0);
        ImageView imageView = new ImageView(SwingFXUtils.toFXImage(routeContainer.getMap(), null));
        GridPane.setConstraints(imageView, 0, 1);

        Label label = new Label("Hello World");
        GridPane.setConstraints(label, 0, 0);*/


        // Add everything to grid
        //grid.getChildren().addAll(imageView, label);

        Scene scene = new Scene(grid, 680, 680);
        window.setScene(scene);
        window.showAndWait();

    }

}
