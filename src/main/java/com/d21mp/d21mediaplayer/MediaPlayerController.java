package com.d21mp.d21mediaplayer;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MediaPlayerController implements Initializable {

    @FXML
    private MediaView mediaView;

    @FXML
    private Label labelTime;

    @FXML
    private TextField searchField;

    @FXML
    private VBox searchPlaylistView, mediaViewVBox;

    @FXML
    private Button playPauseBut, stopBut, skipForwardBut, skipBackwardBut;

    @FXML
    public Slider sliderTime, sliderVolume;

    private MediaPlayer mp;
    private Media me;

    private Image playImg, pauseImg, stopImg, skipForwardImg, skipBackwardImg;

    // Weather or not the search / playlist view is displayed in the UI
    // Initially the view is initialized as being displayed, so set to true
    private boolean showSearchPlaylistView = true;

    //This is used for handling the playlist
    PlaylistHandler playlist = new PlaylistHandler();


    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Hide Search/Playlist view
        toggleSearchPlaylistView();

        // Load icon images for video controls
        playImg = new Image(new File("src/main/resources/com/d21mp/d21mediaplayer/play.png").toURI().toString());
        pauseImg = new Image(new File("src/main/resources/com/d21mp/d21mediaplayer/pause.png").toURI().toString());
        stopImg = new Image(new File("src/main/resources/com/d21mp/d21mediaplayer/stop.png").toURI().toString());
        skipForwardImg = new Image(new File("src/main/resources/com/d21mp/d21mediaplayer/skip-forward.png").toURI().toString());
        skipBackwardImg = new Image(new File("src/main/resources/com/d21mp/d21mediaplayer/skip-backward.png").toURI().toString());

        // Check for successful loading of images and add to buttons
        if(playImg != null && pauseImg != null && stopImg != null && skipForwardImg != null && skipBackwardImg != null) {
            setButtonUIImage(playPauseBut, pauseImg);
            setButtonUIImage(stopBut, stopImg);
            setButtonUIImage(skipForwardBut, skipForwardImg);
            setButtonUIImage(skipBackwardBut, skipBackwardImg);
        }
        else
            System.out.println("Error: Failed to load UI icons");

        // Paint it black and preserve aspect ratio of video
        mediaViewVBox.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        mediaView.setPreserveRatio(true);

        // Plays this media when application launches
        mediaSelection("Countdown");

    }

    /**
     * Show or hides the search / playlist view, depending on its state
     */
    public void toggleSearchPlaylistView() {
        if(!showSearchPlaylistView) {
            // Show view
            searchPlaylistView.setVisible(true);
            searchPlaylistView.setManaged(true);
            showSearchPlaylistView = true;
            // Resize scene and set minimum size to content size
            MainApplication.sizeToScene();
        }
        else {
            // Hide view
            searchPlaylistView.setVisible(false);
            searchPlaylistView.setManaged(false);
            showSearchPlaylistView = false;
            // Resize scene and set minimum size to content size
            MainApplication.sizeToScene();
        }
    }

    /**
     * Puts the supplied image onto supplied button
     * @param button Button to put image onto
     * @param img Image to put on button
     */
    private void setButtonUIImage(Button button, Image img) {
        ImageView view = new ImageView(img);
        view.setFitHeight(20);
        view.setPreserveRatio(true);
        button.setGraphic(view);
    }

    /**
     * Method for selecting a media to be played in media viewer
     * @param title
     */
    public void mediaSelection(String title) {
        String path = "";
        DB.selectSQL("SELECT URL FROM Media WHERE Title = '" + title + "'");
        do {
            String data = DB.getData();
            if (data.equals(DB.NOMOREDATA)) {
                break;
            }
            else {
                path = data;
            }
        } while (true);

        me = new Media(new File(path).toURI().toString());
        mp = new MediaPlayer(me);

        // Resize window and set minimum window size when media has loaded
        Runnable sizeToSceneRun = () -> MainApplication.sizeToScene();
        mp.setOnReady(sizeToSceneRun);

        mediaView.setMediaPlayer(mp);
        mp.setAutoPlay(false);
        mp.play();
    }

    /**
     * Opens a file via Windows file explorer which plays instantly
     */
    @FXML
    public void openFile() {
        FileChooser fileChooser = new FileChooser();

        File selectedFile = fileChooser.showOpenDialog(null);
        String pathToSelectedFile = selectedFile.getAbsolutePath();

        me = new Media(new File(pathToSelectedFile).toURI().toString());
        mp = new MediaPlayer(me);

        // Resize window and set minimum window size when media has loaded
        Runnable sizeToSceneRun = () -> MainApplication.sizeToScene();
        mp.setOnReady(sizeToSceneRun);

        mediaView.setMediaPlayer(mp);
        mp.setAutoPlay(false);
        mp.play();
    }


    /**
     * Closes current playing media
     */
    @FXML
    public void close() {

        me = new Media(new File("src/main/java/com/d21mp/d21mediaplayer/media/Meme.mp4").toURI().toString());
        mp = new MediaPlayer(me);
        Runnable sizeToSceneRun = () -> MainApplication.sizeToScene();
        mp.setOnReady(sizeToSceneRun);
        mediaView.setMediaPlayer(mp);
        mp.setAutoPlay(false);
        mp.play();
    }

    public void addSearchResult() {
        // add button
        // add label
    }

    public void clearSearchResults() {

    }

    /**
     * Search Media table in Media Player DB by keyword found in a text field
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

    @FXML
    /**
     * Handler for the play and pause button
     */
    private void buttonPlayPause() {
        if (playPauseBut.getText().equals("Play")){
            // Play the mediaPlayer with the attached media
            setButtonUIImage(playPauseBut, pauseImg);
            mpPlay();
        } else {
            // Pause the mediaPlayer
            setButtonUIImage(playPauseBut, playImg);
            mpPause();
        }
    }

    @FXML
    /**
     * Plays the Media
     */
    private void mpPlay(){
        // Play the mediaPlayer with the attached media
        mp.play();
        playPauseBut.setText("Pause");
    }

    @FXML
    /**
     * Pause the Media
     */
    private void mpPause(){
        // Pause the mediaPlayer
        mp.pause();
        playPauseBut.setText("Play");
    }

    @FXML
    /**
     * Handler for the stop button
     */
    private void buttonStop()
    {
        // Stop the mediaPlayer
        mp.stop();
        playPauseBut.setText("Play");
    }

    @FXML
    /**
     * Handler for the skipback button
     */
    private void buttonSkipBack()
    {
        // Play the next media in mediaPlayer
        getURLFromPlaylist(nextMedia.previous);
    }

    @FXML
    /**
     * Handler for the skipforward button
     */
    private void buttonSkipForward()
    {
        // Play the next media in mediaPlayer
        getURLFromPlaylist(nextMedia.next);
    }

    @FXML
    /**
     * Handler for the mute button
     */
    private void buttonMute()
    {
        if (mp.getVolume()==0){
            //Set the volume to the sliders value
            sliderVolume();
        } else {
            //Mute the sound
            mp.setVolume(0);
        }
    }




    /**
     * Handler for the Time slider
     */
    @FXML
    private void sliderTimeSliding()
    {
        // Skip forwards and backwards in the media via the slider
        mp.setStartTime(Duration.seconds(sliderTime.getValue()));

        //Update the time
        //timeChangeShow();
    }

    private void timeChangeShow(){
        //The time
    }

    /**
     * void for the Time sliders scaling
     */
    private void sliderTimeScaling()
    {
        //Slider scaling
        sliderTime.setMax(Double.parseDouble(MediaPlayerInfo.getDuration(mp)));
    }

    /**
     * Handler for the sound slider
     */
    @FXML
    private void sliderVolume()
    {
        // Skip forwards and backwards in the media via the slider
        mp.setVolume(sliderVolume.getValue()/100);
    }

    @FXML
    /**
     * Handler for info about the program
     */
    private void buttonAbout()
    {
        // Show version and our names as creators
    }

    enum nextMedia {
        previous,
        next;
    }

    private void getURLFromPlaylist(nextMedia p){

        //Creates a new mediaplayer with the next media to play
        if (p.equals(nextMedia.next)){
            createMediaPlayer(playlist, playlist.getNextUrlFromPlaylist());
        } else {
            createMediaPlayer(playlist, playlist.getPreviousFromPlaylist());
        }

        //Plays the new media
        mpPlay();
    }

    private MediaPlayer createMediaPlayer(PlaylistHandler playlistHandler, String URL){

        //Stops the current media if there is some playing
        buttonStop();

        me = new Media(new File(URL).toURI().toString());
        mp = new MediaPlayer(me);

        // Resize window and set minimum window size when media has loaded
        Runnable sizeToSceneRun = () -> MainApplication.sizeToScene();
        mp.setOnReady(sizeToSceneRun);

        //Resize the slider
        Runnable sliderResize = () -> sliderTimeScaling();
        mp.setOnReady(sliderResize);

        //While playing this changes the UI live
        Runnable timeSliderUpdater = () -> sliderTimeSliding();
        mp.setOnPlaying(timeSliderUpdater);

        //Gets the next media in the playlist
        Runnable nextUrlFromPlaylist = () -> getURLFromPlaylist(nextMedia.next);
        mp.setOnEndOfMedia(nextUrlFromPlaylist);

        //Set the volume to the same as last media
        sliderVolume();

        mediaView.setMediaPlayer(mp);
        mp.setAutoPlay(false);

        return mp;
    }
}
