package com.d21mp.d21mediaplayer;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MediaPlayerController implements Initializable {

    @FXML
    private MediaView mediaView;

    @FXML
    private TextField searchField;

    @FXML
    private VBox mediaViewVBox;

    @FXML
    private VBox rootVBox;

    private MediaPlayer mp;
    private Media me;

    public void initialize(URL url, ResourceBundle resourceBundle) {

        String path = "";
        DB.selectSQL("SELECT URL FROM Media WHERE Title = 'Countdown'");
        do {
            String data = DB.getData();
            if (data.equals(DB.NOMOREDATA)) {
                break;
            }
            else {
                path = data;
                System.out.printf("path: " + path);
            }
        } while (true);

        me = new Media(new File(path).toURI().toString());
        mp = new MediaPlayer(me);

        mediaViewVBox.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));

        mediaView.setPreserveRatio(true);

        Runnable sizeToSceneRun = () -> MainApplication.sizeToScene();
        mp.setOnReady(sizeToSceneRun);

        mediaView.setMediaPlayer(mp);
        mp.setAutoPlay(false);
        mp.play();
    }

    @FXML
    public void test() {

        me = new Media(new File("src/main/java/com/d21mp/d21mediaplayer/media/Meme.mp4").toURI().toString());
        mp = new MediaPlayer(me);
        Runnable sizeToSceneRun = () -> MainApplication.sizeToScene();
        mp.setOnReady(sizeToSceneRun);
        mediaView.setMediaPlayer(mp);
        mp.setAutoPlay(false);
        mp.play();
        //System.out.println(rootVBox.getHeight() + "|" + rootVBox.getWidth());
    }

    public void addSearchResult() {
        // add button

        // add label

    }

    public void clearSearchResults() {

    }

    /**
     * Search Media table in Media Player DB by keyword found in a textfield
     * @return All found results an Arraylist
     */
    public ArrayList<String> searchFunction() {
        DB.selectSQL("SELECT URL FROM Media WHERE Creator LIKE '%" + this.searchField.getText() + "%' OR Title LIKE '%" + this.searchField.getText() +"%'");
        ArrayList<String> collected = new ArrayList();
        do {
            String result = DB.getData();
            if (result.equals(DB.NOMOREDATA)) {
                break;
            }
            else {
                collected.add(result);
            }
        } while (true);
        return collected;
    }
}
