package com.d21mp.d21mediaplayer;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.*;

public class MediaPlayerController implements Initializable {

    @FXML
    private MediaView mediaView;
    @FXML
    private Label currentPlaylist;
    @FXML
    private TextField searchField;
    @FXML
    private VBox rootVBox, searchPlaylistView, mediaViewVBox;
    @FXML
    private Button playPauseBut, stopBut, skipForwardBut, skipBackwardBut, addToPlaylist, removeFromPlaylist;
    @FXML
    public Slider sliderTime, sliderVolume;
    @FXML
    RadioMenuItem darkmode, maximize;
    @FXML
    ListView listview;

    private MediaPlayer mp;
    private Media me;

    private double xOffset;
    private double yOffset;
    private String chosenPlaylist = "";

    private Image playImg, pauseImg, stopImg, skipForwardImg, skipBackwardImg;

    // Weather or not the search / playlist view is displayed in the UI
    // Initially the view is initialized as being displayed, so set to true
    private boolean showSearchPlaylistView = true;

    // Weather or not media is currently playing
    private boolean isPlaying = false;

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

        // Change isPlaying state when mp starts playing
        mp.setOnPlaying(new Runnable() {
            @Override
            public void run() {
                isPlaying = true;
            }
        });

        // Change isPlaying state on pause or stop
        mp.setOnPaused(new Runnable() {
            @Override
            public void run() {
                isPlaying = false;
            }
        });
        mp.setOnStopped(new Runnable() {
            @Override
            public void run() {
                isPlaying = false;
            }
        });

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

        // Change isPlaying state when mp starts playing
        mp.setOnPlaying(new Runnable() {
            @Override
            public void run() {
                isPlaying = true;
            }
        });

        // Change isPlaying state on pause or stop
        mp.setOnPaused(new Runnable() {
            @Override
            public void run() {
                isPlaying = false;
            }
        });
        mp.setOnStopped(new Runnable() {
            @Override
            public void run() {
                isPlaying = false;
            }
        });

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

    /**
     * Creates a new playlist and adds to database
     */
    @FXML
    public void createPlaylist() {
        // Create new object
        PlaylistHandler playlistCreator = new PlaylistHandler();

        // Values for dialog box
        TextInputDialog dialog = new TextInputDialog();
        //dialog.initStyle(StageStyle.UNDECORATED); // THIS DISABLES TOP BAR
        dialog.setHeaderText(null);
        dialog.setContentText("Please enter name of playlist:");
        // Add custom graphics to dialog box
        dialog.setGraphic(new ImageView(Objects.requireNonNull(this.getClass().getResource("addIcon32.png")).toString()));

        // Set to dark mord if activated
        if (darkmode.isSelected()) {
            dialog.getDialogPane().setStyle("-fx-background-color: darkgrey");
        }

        // Get the Stage
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();

        // Add a custom icon.
        stage.getIcons().add(new Image(Objects.requireNonNull(this.getClass().getResource("awesomeicon.png")).toString()));

        // Get the response value.
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(playlistCreator::createPlaylist);
    }

    /**
     * Opens a playlist and displays content in listview
     */
    @FXML
    public void openPlaylist() {
        // Create new object
        PlaylistHandler playlistOpener = new PlaylistHandler();

        List<String> choices = playlistOpener.loadPlaylistOverview();

        ChoiceDialog<String> dialog = new ChoiceDialog<>("", choices);
        dialog.setTitle("Select Playlist");
        dialog.setHeaderText(null);
        dialog.setContentText("Choose your playlist:");

        // Get result
        Optional<String> result = dialog.showAndWait();
        String resultAsString = String.valueOf(result);
        chosenPlaylist = resultAsString.substring(9, resultAsString.length()-1);

        currentPlaylist.setText(chosenPlaylist);
        result.ifPresent(s -> listview.setItems((FXCollections.observableArrayList(playlistOpener.loadPlaylistFromDB(s)))));
    }

    @FXML
    public void playSongFromPlaylist(MouseEvent event) {
        String pathToSelectedFile = (String) listview.getSelectionModel().getSelectedItem();

        me = new Media(new File(pathToSelectedFile).toURI().toString());
        mp = new MediaPlayer(me);

        // Resize window and set minimum window size when media has loaded
        Runnable sizeToSceneRun = () -> MainApplication.sizeToScene();
        mp.setOnReady(sizeToSceneRun);

        // Change isPlaying state when mp starts playing
        mp.setOnPlaying(new Runnable() {
            @Override
            public void run() {
                isPlaying = true;
            }
        });

        // Change isPlaying state on pause or stop
        mp.setOnPaused(new Runnable() {
            @Override
            public void run() {
                isPlaying = false;
            }
        });
        mp.setOnStopped(new Runnable() {
            @Override
            public void run() {
                isPlaying = false;
            }
        });

        mediaView.setMediaPlayer(mp);
        mp.setAutoPlay(false);
        mp.play();
    }

    /**
     * Add media to a playlist and refresh listview
     */
    @FXML
    public void addSongToPlaylistViaButton() {
        PlaylistHandler playlistAdd = new PlaylistHandler();
        FileChooser fileChooser = new FileChooser();

        File selectedFile = fileChooser.showOpenDialog(null);
        String pathToSelectedFile = selectedFile.getAbsolutePath();
        System.out.println(pathToSelectedFile);

        // Add to database
        playlistAdd.addMediaToMedias(pathToSelectedFile);
        // Add to database
        playlistAdd.addMediaToPlaylist(chosenPlaylist, pathToSelectedFile);
        // Refresh listview
        listview.setItems((FXCollections.observableArrayList(playlistAdd.loadPlaylistFromDB(chosenPlaylist))));
    }

    @FXML
    public void removeSongToPlaylistViaButton() {

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
        if (!isPlaying)
            mpPlay();
        else
            mpPause();
    }

    @FXML
    /**
     * Plays the Media
     */
    private void mpPlay(){
        // Play the mediaPlayer with the attached media
        mp.play();
        setButtonUIImage(playPauseBut, pauseImg);
        isPlaying = true;
    }

    @FXML
    /**
     * Pause the Media
     */
    private void mpPause(){
        // Pause the mediaPlayer
        mp.pause();
        setButtonUIImage(playPauseBut, playImg);
        isPlaying = false;
    }

    @FXML
    /**
     * Handler for the stop button
     */
    private void buttonStop()
    {
        // Stop the mediaPlayer
        mp.stop();
        setButtonUIImage(playPauseBut, playImg);
        isPlaying = false;
    }

    @FXML
    /**
     * Handler for the skipback button
     */
    private void buttonSkipBack()
    {
        if(playlist.getPlaylistSize()>0) {
			// Play the next media in mediaPlayer
			getURLFromPlaylist(nextMedia.previous);
		}
    }

    @FXML
    /**
     * Handler for the skipforward button
     */
    private void buttonSkipForward()
    {
        if(playlist.getPlaylistSize()>0) {
			// Play the next media in mediaPlayer
			getURLFromPlaylist(nextMedia.next);
		}
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
     * Create a shuffle button
     */
    @FXML
   private void buttonShuffle(){
        playlist.changeToShufflePlaylist();
        createMediaPlayer(playlist,playlist.getUrlFromPlaylist(1));
   }

    /**
     * Create an unshuffle button
     */
    @FXML
    private void buttonUnShuffle(){
        playlist.changeToMasterPlaylist();
        createMediaPlayer(playlist,playlist.getUrlFromPlaylist(1));
    }


    /**
     * Handler for the Time slider
     */
    @FXML
    private void sliderTimeDragging()
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
        Runnable timeSliderUpdater = () -> sliderTimeDragging();
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

    /**
     * Toggles light/dark mode
     */
    @FXML
    public void toggleDarkMode() {

        if (darkmode.isSelected()) {
            // Main theme
            rootVBox.setStyle("-fx-base:black");
            // Buttons
            playPauseBut.setStyle("-fx-base:darkgrey");
            stopBut.setStyle("-fx-base:darkgrey");
            skipForwardBut.setStyle("-fx-base:darkgrey");
            skipBackwardBut.setStyle("-fx-base:darkgrey");
        }
        else {
            // Main theme
            rootVBox.setStyle("");
            // Buttons
            playPauseBut.setStyle("");
            stopBut.setStyle("");
            skipForwardBut.setStyle("");
            skipBackwardBut.setStyle("");
        }
    }

     @FXML
     public void maximize() {
        Stage stage = (Stage) rootVBox.getScene().getWindow();
        if ( maximize.isSelected()) {
            stage.setMaximized(true);
        }
        else
            stage.setMaximized(false);
     }

    @FXML
    private void drag(MouseEvent event) {
        Stage stage = (Stage) rootVBox.getScene().getWindow();
        stage.setY(event.getScreenY() - yOffset);
        stage.setX(event.getScreenX() - xOffset);
    }

    @FXML
    private void presDrag(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    /**
     * Allows the user to exit the application after confirming an alert
     */
    @FXML
    public void exit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initStyle(StageStyle.UNDECORATED);
        alert.setTitle(null);
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to exit?");

        // Set to dark mord if activated
        if (darkmode.isSelected()) {
            alert.getDialogPane().setStyle("-fx-background-color: darkgrey");
        }

        // Get result
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            // ... user chose OK
            System.exit(0);
        } else {
            // ... user chose CANCEL or closed the dialog
        }
    }
}
