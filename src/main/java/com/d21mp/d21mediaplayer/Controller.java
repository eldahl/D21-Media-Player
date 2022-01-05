package com.d21mp.d21mediaplayer;


import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.media.*;
import javafx.scene.control.Button;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.io.*;
import java.net.*;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private MediaView mediaV;

    @FXML
    private Button playpause;

    @FXML
    private TextField test1;
    @FXML
    private TextField test2;


    private MediaPlayer mp;
    private Media me;


    /**
     * This method is invoked automatically in the beginning. Used for initializing, loading data etc.
     *
     * @param location
     * @param resources
     */
    public void initialize(URL location, ResourceBundle resources){
        // Build the path to the location of the media file
        ////String path = new File("src/main/java/com/d21mp/d21mediaplayer/media/file_example_MP4_640_3MG.mp4").getAbsolutePath();


        DB.selectSQL("select URL from Media where Creator = Someone");
        System.out.println(DB.getData());
        // Create new Media object (the actual media content)
        me = new Media(new File(DB.getData()).toURI().toString());
        // Create new MediaPlayer and attach the media to be played
        mp = new MediaPlayer(me);
        //
        mediaV.setMediaPlayer(mp);
        // mp.setAutoPlay(true);
        // If autoplay is turned of the method play(), stop(), pause() etc controls how/when medias are played
        mp.setAutoPlay(false);

        //Starts the mediaPlayer automatic after loading had been completed
        mp.play();
    }



    @FXML
    /**
     * Handler for the play button
     */
    private void handlePlayPause()
    {
        // Play the mediaPlayer with the attached media
        mp.play();

        int durationOfCurrentMedia = Integer.parseInt(mp.getMedia().getDuration().toString());

        test1.setText(mp.getCurrentTime().toString());


    }

    @FXML
    /**
     * Handler for the pause button
     */
    private void handlePause()
    {
        // Pause the mediaPlayer
        mp.pause();
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
     *
     * Nice to have, not a need to have
     */
    private void handleAbout()
    {
        // Show version and our names as creators
    }




}
