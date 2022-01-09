package com.d21mp.d21mediaplayer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;



public class MainApplication extends Application {

    public static Stage iStage;

    public static void sizeToScene() {
        iStage.sizeToScene();
    }

    @Override
    public void start(Stage stage) throws IOException {

        iStage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("media-player.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("awesomeicon.png"))));
        stage.setTitle("D21 MediaPlayer");
        stage.setScene(scene);
        stage.show();
        stage.setMinWidth(425);
        stage.setMinHeight(153);
    }

    public static void main(String[] args) {
        launch();
    }
}
