package com.d21mp.d21mediaplayer;


import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.media.*;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.*;
import java.net.*;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    public Button playpause, stop, back, forward, openButton;
    @FXML
    public Slider sliderTime;
    @FXML
    private MediaView mediaV;
    @FXML
    private Text test;

    private MediaPlayer mp;
    private Media me;



    /**
     * This method is invoked automatically in the beginning. Used for initializing, loading data etc.
     *
     * @param location
     * @param resources
     */
    public void initialize(URL location, ResourceBundle resources) {
        // Start a video when application opens
        mediaSelection("Countdown");

    }

    public void mediaSelection(String title) {
        // Build the path to the location of the media file
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

        // Create new Media object (the actual media content)
        me = new Media(new File(path).toURI().toString());
        // Create new MediaPlayer and attach the media to be played
        mp = new MediaPlayer(me);

        mediaV.setMediaPlayer(mp);
        // If autoplay is turned off the method play(), stop(), pause() etc controls how/when medias are played
        mp.setAutoPlay(false);

        //Slider scaling
        //sliderTime.setMax();

        //Starts the mediaPlayer automatic after loading had been completed
        mp.play();
    }


    /**
     * Opens a file using Windows file explorer
     */
    @FXML
    public void openFile() {
        FileChooser fileChooser = new FileChooser();

        File selectedFile = fileChooser.showOpenDialog(null);
        String pathToSelectedFile = selectedFile.getAbsolutePath();

        me = new Media(new File(pathToSelectedFile).toURI().toString());
        // Create new MediaPlayer and attach the media to be played
        mp = new MediaPlayer(me);

        mediaV.setMediaPlayer(mp);
        // If autoplay is turned off the method play(), stop(), pause() etc controls how/when medias are played
        mp.setAutoPlay(false);

        //Slider scaling
        //sliderTime.setMax();

        //Starts the mediaPlayer automatic after loading had been completed
        mp.play();
    }

    @FXML
    /**
     * Handler for the play and pause button
     */
    private void handlePlayPause() {
        if (playpause.getText().equals("Play")){
            // Play the mediaPlayer with the attached media
            mpPlay();
        } else {
            // Pause the mediaPlayer
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
        playpause.setText("Pause");
    }

    @FXML
    /**
     * Pause the Media
     */
    private void mpPause(){
        // Pause the mediaPlayer
        mp.pause();
        playpause.setText("Play");
    }

    @FXML
    /**
     * Handler for the stop button
     */
    private void handleStop()
    {
        // Stop the mediaPlayer
        mp.stop();
        playpause.setText("Play");

        test.setText(MediaPlayerInfo.getCurrentTime(mp)+" / "+MediaPlayerInfo.getDuration(mp));
    }

    @FXML
    /**
     * Handler for the skipback button
     */
    private void handleSkipBack()
    {
        // Play the next media in mediaPlayer
    }

    @FXML
    /**
     * Handler for the skipforward button
     */
    private void handleSkipLForward()
    {
        // Play the next media in mediaPlayer
    }

    /**
     * Handler for the skipforward button
     */
    @FXML
    private void handleSliderTimeStartPoint()
    {
        //The video is paused
        mpPause();
    }

    /**
     * Handler for the skipforward button
     */
    @FXML
    private void handleSliderTimeEndPoint()
    {
        // The video is startet

    }

    /**
     * Handler for the skipforward button
     */
    @FXML
    private void handleSliderTimeSliding()
    {
        // Skip forwards and backwards in the media via the slider
        mp.setStartTime(Duration.seconds(sliderTime.getValue()));
    }

    @FXML
    /**
     * Handler for the search button
     */
    private void handleSearch()
    {
        DBHandling.main();
    }

    @FXML
    /**
     * Handler for the add to Playlist button
     */
    private void handleAddToPlaylist()
    {
        // Add a media to a playlist
    }

    @FXML
    /**
     * Handler for the removing from a Playlist button
     */
    private void handleRemoveFromPlaylist()
    {
        // remove a media from a playlist
    }

    @FXML
    /**
     * Handler for the removing of a Playlist button
     */
    private void handleRemovePlaylist()
    {
        // remove a playlist
    }

    @FXML
    /**
     * Handler for the removing of a Playlist button
     */
    private void handleAddPlaylist()
    {
        // add a new playlist
    }

    @FXML
    /**
     * Handler for the selection of a Playlist button
     */
    private void handleSelectPlaylist()
    {
        // select a playlist
    }

    @FXML
    /**
     * Handler for edit options for a Playlist button
     */
    private void handleEditPlaylist()
    {
        // select a playlist
    }



    @FXML
    /**
     * Handler for exiting the program
     */
    private void handleExit()
    {
        System.exit(0);
    }

    @FXML
    /**
     * Handler for info about the program
     */
    private void handleAbout()
    {
        // Show version and our names as creators
    }




}
