package com.d21mp.d21mediaplayer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;



public class MainApplication extends Application {

    // Fxml stage instance used for later access to stage
    public static Stage iStage;

    /**
     * Resize scene to fit fxml content size and set minimum size to content size
     */
    public static void sizeToScene() {
        iStage.sizeToScene();
        iStage.setMinWidth(iStage.getWidth());
        iStage.setMinHeight(iStage.getHeight());
    }

    @Override
    public void start(Stage stage) throws IOException {
        iStage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("media-player.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("awesomeicon.png"))));
        stage.initStyle(StageStyle.UNDECORATED); //THIS ONE REMOVES THE MENU BAR
        stage.setTitle("Awesome-O Media Player");
        stage.setScene(scene);

        // Finally
        stage.show();

    }




    public static void main(String[] args) {
        launch();
    }
}

