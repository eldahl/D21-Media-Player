package com.d21mp.d21mediaplayer;


import javafx.application.Platform;
import javafx.scene.control.Slider;
import javafx.scene.media.*;
import javafx.scene.control.Button;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.*;
import java.net.*;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    public Button playpause;
    @FXML
    public Button stop;
    @FXML
    public Button back;
    @FXML
    public Button forward;

    @FXML
    public Button exit;

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


    //the Thread to update UI
    Thread taskThread = new Thread(new Runnable() {
        @Override
        public void run() {
            for(int i=0; true; i++){

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //Resizes the max value of the time slider, so it matches the media
                            if (sliderTime.getMax() != Double.parseDouble(MediaPlayerInfo.getDuration(mp))) {
                                sliderTime.setMax(Double.parseDouble(MediaPlayerInfo.getDuration(mp)));
                            }

                            //Here the value for the time slider gets updated as the media plays (there is a 0.5 Sec delay)
                            if (sliderTime.getValue()+0.49 != Double.parseDouble(MediaPlayerInfo.getCurrentTime(mp))+0.49) {
                                //Updates the sliders value
                                sliderTime.setValue(Double.parseDouble(MediaPlayerInfo.getCurrentTime(mp))+0.49);
                                //Update the time text
                                timeChangeShow();
                            }
                        } catch (NumberFormatException nfe){
                            System.out.println("The Program hasn't started yet, it will soon...");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    });

    /**
     * This method is invoked automatically in the beginning. Used for initializing, loading data etc.
     *
     * @param location
     * @param resources
     */
    public void initialize(URL location, ResourceBundle resources){
        // Build the path to the location of the media file
        String path = "";
        DB.selectSQL("SELECT URL FROM Media WHERE Title = 'Countdown'");
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

        //Here the UI update thread will start
        taskThread.start();
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
        playlist.getLastUrlFromPlaylist();
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





    private static double round(double value, int places) {

        //Her it checks if the places is above 0, else it will throw an exception
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
