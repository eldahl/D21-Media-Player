package com.d21mp.d21mediaplayer;



import javafx.application.Platform;
import javafx.scene.control.Slider;
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
    public Slider sliderVolume;
    @FXML
    private MediaView mediaV;
    @FXML
    private Text textTime;
    @FXML
    private Text textVolume;

    private MediaPlayer mp;
    private Media me;
    PlaylistHandler playlist = new PlaylistHandler();




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

        //Starts the mediaPlayer automatic after loading have been completed
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
    }

    @FXML
    /**
     * Handler for the skipback button
     */
    private void handleSkipBack()
    {
        // Play the next media in mediaPlayer
        playlist.getPreviousFromPlaylist();
    }

    @FXML
    /**
     * Handler for the skipforward button
     */
    private void handleSkipLForward()
    {
        // Play the next media in mediaPlayer
        playlist.getNextUrlFromPlaylist();
    }

    /**
     * Handler for when you want to use the slider
     */
    @FXML
    private void handleSliderTimeStartPoint()
    {
        //The video is paused
        mpPause();
    }

    /**
     * Handler for when you have decided your skip to point
     */
    @FXML
    private void handleSliderTimeEndPoint()
    {
        // The video is startet

    }

    /**
     * Handler for the Time slider
     */
    @FXML
    private void handleSliderTimeSliding()
    {
        // Skip forwards and backwards in the media via the slider
        mp.setStartTime(Duration.seconds(sliderTime.getValue()));

        //Update the time
        timeChangeShow();
    }

    private void timeChangeShow(){
        //The time gets rounded
        String currentTime = ""+round(Double.parseDouble(MediaPlayerInfo.getCurrentTime(mp)),0);
        String duration = ""+round(Double.parseDouble(MediaPlayerInfo.getDuration(mp)),0);

        //Changes the time text
        textTime.setText(currentTime+" / "+duration);
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
    private void handleSliderVolume()
    {
        // Skip forwards and backwards in the media via the slider
        mp.setVolume(sliderVolume.getValue()/100);

        //Changes the volume text
        textVolume.setText(round(sliderVolume.getValue(),0)+"%");
    }

    @FXML
    /**
     * Handler for the select media button
     */
    private void handleSelectMedia()
    {
        //AutoScales the time slider
        sliderTimeScaling();
    }

    @FXML
    /**
     * Handler for the search button
     */
    private void handleSearch()
    {

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
        //This closes the system
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


    /**
     * This is used for rounding the decimals and then choosing the amount of decimals you want to show (rounded)
     * @param value this is the double that you want to round
     * @param places this is the amount of decimals you want to round up to
     * @return the value rounded up till the amount of places behind the dot
     */
    private static double round(double value, int places) {

        //Here it checks if the places is above 0, else it will throw an exception
        if (places < 0) throw new IllegalArgumentException();

        //Here the factor number gets chosen. The bigger, the more digits are accurate
        long factor = (long) Math.pow(10, places);

        //Here the value is timed with the factor to get a larger number
        value = value * factor;

        //Here the value is rounded
        long tmp = Math.round(value);

        //Here the value gets devided with the factor to get the rounded number and then returned
        return (double) tmp / factor;
    }
}
