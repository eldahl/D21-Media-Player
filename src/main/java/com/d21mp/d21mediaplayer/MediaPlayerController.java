package com.d21mp.d21mediaplayer;

import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
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
    private Button playPauseBut, stopBut, skipForwardBut, skipBackwardBut, addToPlaylist, removeFromPlaylist, shuffleButton, loopButton, muteButton;
    @FXML
    private Slider sliderTime, sliderVolume;
    @FXML
    RadioMenuItem darkmode, maximize;
    @FXML
    private ListView<String> listview;
	@FXML
    private ImageView imgPlay,imgPause;

    // For media player
    private MediaPlayer mp;
    private Media me;
    // For dragging
    private double xOffset;
    private double yOffset;
    // To log current playlist at all time
    private String chosenPlaylist = "";
    // For icons
    private Image playImg, pauseImg, stopImg, skipForwardImg, skipBackwardImg, addButton, removeButton, shuffleImg, loopImg, muteImg;
    // Weather or not the search / playlist view is displayed in the UI
    // Initially the view is initialized as being displayed, so set to true
    private boolean showSearchPlaylistView = true;
    // Weather or not media is currently playing
    private boolean isPlaying = false;
    // This is used for handling the playlist
    PlaylistHandler playlist = new PlaylistHandler();

     // This is used for handling the animations
    AnimationHandler animation = new AnimationHandler();
    //This is used for handling the yt-search and results
    YoutubeHandler yt = new YoutubeHandler();

    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Hide Search/Playlist view
        toggleSearchPlaylistView();

        // Load icon images for video controls
        playImg = new Image(new File("src/main/resources/com/d21mp/d21mediaplayer/play.png").toURI().toString());
        pauseImg = new Image(new File("src/main/resources/com/d21mp/d21mediaplayer/pause.png").toURI().toString());
        stopImg = new Image(new File("src/main/resources/com/d21mp/d21mediaplayer/stop.png").toURI().toString());
        skipForwardImg = new Image(new File("src/main/resources/com/d21mp/d21mediaplayer/skip-forward.png").toURI().toString());
        skipBackwardImg = new Image(new File("src/main/resources/com/d21mp/d21mediaplayer/skip-backward.png").toURI().toString());
        addButton = new Image(new File("src/main/resources/com/d21mp/d21mediaplayer/add32.png").toURI().toString());
        removeButton = new Image(new File("src/main/resources/com/d21mp/d21mediaplayer/remove32.png").toURI().toString());
        shuffleImg = new Image(new File("src/main/resources/com/d21mp/d21mediaplayer/shuffle.png").toURI().toString());
        loopImg = new Image(new File("src/main/resources/com/d21mp/d21mediaplayer/loop.png").toURI().toString());
        muteImg = new Image(new File("src/main/resources/com/d21mp/d21mediaplayer/mute.png").toURI().toString());

        // Check for successful loading of images and add to buttons
        if(playImg != null && pauseImg != null && stopImg != null && skipForwardImg != null &&
                skipBackwardImg != null && addButton != null && removeButton != null && shuffleImg != null && muteImg != null) {
            setButtonUIImage(playPauseBut, pauseImg);
            setButtonUIImage(stopBut, stopImg);
            setButtonUIImage(skipForwardBut, skipForwardImg);
            setButtonUIImage(skipBackwardBut, skipBackwardImg);
            setButtonUIImage(addToPlaylist, addButton);
            setButtonUIImage(removeFromPlaylist, removeButton);
            setButtonUIImage(shuffleButton, shuffleImg);
            setButtonUIImage(loopButton, loopImg);
            setButtonUIImage(muteButton, muteImg);
        }
        else
            System.out.println("Error: Failed to load UI icons");

        // Paint it black and preserve aspect ratio of video
        mediaViewVBox.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        mediaView.setPreserveRatio(true);

        animation.labelAnimation(currentPlaylist, 2);
        // WE DON'T WANT AN MEDIA TO AUTOPLAY THEREFOR DELETED
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

        //create media player
        createMediaPlayer(path);

        //play media player
        mpPlay();
    }

    /**
     * Opens a file via Windows file explorer which plays instantly
     */
    @FXML
    public void openFile() {
        FileChooser fileChooser = new FileChooser();

        // Open classic file explorer window
        File selectedFile = fileChooser.showOpenDialog(null);

        // Get URL for file
        String pathToSelectedFile = selectedFile.getAbsolutePath();

        // Separate path by "\" to get filename
        List<String> pathDivided = new ArrayList<>(Arrays.asList(pathToSelectedFile.split("\\\\")));

        // Get file name
        String mediaTitle = pathDivided.get(pathDivided.size()-1);

        // Remove ".mp4" from filename and play file
        mediaSelection(mediaTitle.substring(0, mediaTitle.length()-4));

        //play the media
        mpPlay();
    }


    /**
     * Closes current playing media
     * // WHY DOES THIS THEN PLAY ANOTHER MEDIA?????
     * // NIKOLAI
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

        // Add a custom icon
        stage.getIcons().add(new Image(Objects.requireNonNull(this.getClass().getResource("awesomeicon.png")).toString()));

        // Get the response value
        Optional<String> result = dialog.showAndWait();

        // Convert to string and remove "Optional[" and "]" from string
        String resultAsString = String.valueOf(result);
        chosenPlaylist = resultAsString.substring(9, resultAsString.length()-1);

        // Set the current playlist label
        currentPlaylist.setText(chosenPlaylist);

        // Create playlist!
        result.ifPresent(s -> playlistCreator.createPlaylist(getComputerName(), s));

        // Refresh listview
        result.ifPresent(s -> listview.setItems((FXCollections.observableArrayList(playlistCreator.loadPlaylistFromDB(getComputerName(), s)))));

        // Show Search and Playlist to the right
        if(!showSearchPlaylistView) {
            // Show view
            searchPlaylistView.setVisible(true);
            searchPlaylistView.setManaged(true);
            showSearchPlaylistView = true;
            // Resize scene and set minimum size to content size
            MainApplication.sizeToScene();
        }
    }

    /**
     * Opens a playlist and displays content in listview
     */
    @FXML
    public void openPlaylist() {
        // Create new object
        PlaylistHandler playlistOpener = new PlaylistHandler();

        // Fetches PlaylistName from PlaylistOverview and collects in a List
        List<String> choices = playlistOpener.loadPlaylistOverview(getComputerName());

        // Values for dialog box
        ChoiceDialog<String> dialog = new ChoiceDialog<>("", choices);
        dialog.setTitle("Select Playlist");
        dialog.setHeaderText(null);
        dialog.setContentText("Choose your playlist:");

        // Set to dark mord if activated
        if (darkmode.isSelected()) {
            dialog.getDialogPane().setStyle("-fx-background-color: darkgrey");
        }

        // Get the Stage
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();

        // Add a custom icon
        stage.getIcons().add(new Image(Objects.requireNonNull(this.getClass().getResource("awesomeicon.png")).toString()));

        // Get result
        Optional<String> result = dialog.showAndWait();

        // Convert to string and remove "Optional[" and "]" from string
        String resultAsString = String.valueOf(result);
        chosenPlaylist = resultAsString.substring(9, resultAsString.length()-1);

        // Open playlist!
        currentPlaylist.setText(chosenPlaylist);

        // Refresh listview
        result.ifPresent(s -> listview.setItems((FXCollections.observableArrayList(playlistOpener.loadPlaylistFromDB(getComputerName(), s)))));

        // Show Search and Playlist to the right
        if(!showSearchPlaylistView) {
            // Show view
            searchPlaylistView.setVisible(true);
            searchPlaylistView.setManaged(true);
            showSearchPlaylistView = true;
            // Resize scene and set minimum size to content size
            MainApplication.sizeToScene();
        }
    }

    @FXML
    public void deletePlaylist() {
        // Create new object
        PlaylistHandler playlistDeleter = new PlaylistHandler();

        // Fetches PlaylistName from PlaylistOverview and collects in a List
        List<String> choices = playlistDeleter.loadPlaylistOverview(getComputerName());

        // Values for dialog box
        ChoiceDialog<String> dialog = new ChoiceDialog<>("", choices);
        dialog.setTitle("Delete Playlist");
        dialog.setHeaderText(null);
        dialog.setContentText("Choose your playlist:");

        // Add custom graphics to dialog box
        dialog.setGraphic(new ImageView(Objects.requireNonNull(this.getClass().getResource("delete.png")).toString()));

        // Set to dark mord if activated
        if (darkmode.isSelected()) {
            dialog.getDialogPane().setStyle("-fx-background-color: darkgrey");
        }

        // Get the Stage
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();

        // Add a custom icon
        stage.getIcons().add(new Image(Objects.requireNonNull(this.getClass().getResource("awesomeicon.png")).toString()));

        // Get result
        Optional<String> result = dialog.showAndWait();

        // Convert to string and remove "Optional[" and "]" from string
        String resultAsString = String.valueOf(result);
        resultAsString = resultAsString.substring(9, resultAsString.length()-1);

        // Delete playlist!
        playlistDeleter.deletePlaylist(getComputerName(), resultAsString);
    }

    /**
     * Allows the user to play media from listview by using a double click
     */
    @FXML
    public void playSongFromPlaylist(MouseEvent event) {
        listview.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2) {
                    // Use ListView's getSelected Item
                    String listviewItem = (String) listview.getSelectionModel().getSelectedItem();

                    // Play media!
                    mediaSelection(listviewItem);

                    // When ended, autoplay next media
                    mp.setOnEndOfMedia(new Runnable() {
                        @Override
                        public void run() {
                            mp.seek(Duration.ZERO);
                            playNext();
                        }
                    });
                }
            }
        });
    }

    /**
     * Add media to a playlist and refresh listview
     */
    @FXML
    public void addSongToPlaylistViaButton() {
        // Create needed objects
        PlaylistHandler playlistAdd = new PlaylistHandler();
        FileChooser fileChooser = new FileChooser();

        // Values for fileChooser
        File selectedFile = fileChooser.showOpenDialog(null);

        // Get URL to file
        String pathToSelectedFile = selectedFile.getAbsolutePath();

        // Separate path by "\" to get filename
        List<String> pathDivided = new ArrayList<>(Arrays.asList(pathToSelectedFile.split("\\\\")));

        // Get file name
        String mediaTitle = pathDivided.get(pathDivided.size()-1);

        // Remove ".mp4" from filename and play file
        mediaTitle = mediaTitle.substring(0, mediaTitle.length()-4);

        // Add to database in table Media
        playlistAdd.ifNotExistAddToMediaTable(getComputerName(), mediaTitle, pathToSelectedFile);
        // Add to database in table PlaylistCollection
        playlistAdd.addMediaToPlaylist(getComputerName(), chosenPlaylist, mediaTitle, pathToSelectedFile);
        // Refresh listview
        listview.setItems((FXCollections.observableArrayList(playlistAdd.loadPlaylistFromDB(getComputerName(), chosenPlaylist))));
    }

    /**
     * Remove a media from a given playlist
     */
    @FXML
    public void removeSongFromPlaylistViaButton() {
        // Create new object
        PlaylistHandler playlistHandler = new PlaylistHandler();

        // Delete media from database and thus listview
        playlistHandler.deleteMediaFromPlaylist(getComputerName(), chosenPlaylist, (String) listview.getSelectionModel().getSelectedItem());

        // Refresh listview
        listview.setItems((FXCollections.observableArrayList(playlistHandler.loadPlaylistFromDB(getComputerName(), chosenPlaylist))));
    }

    /**
     * Plays the next media in listview when clicking button
     */
    @FXML
    public void playNext() {
        // Current listview item
        String listviewItem = (String) listview.getSelectionModel().getSelectedItem();
        // Select next item in listview
        listview.getSelectionModel().selectNext();

        // Cast this item to a string to fetch title
        String nextListviewItem = (String) listview.getSelectionModel().getSelectedItem();

        // Play media!
        mediaSelection(nextListviewItem);
    }

    /**
     * Plays the previous media in listview when clicking button
     */
    @FXML
    public void playPrevious() {

        // Select next item in listview
        listview.getSelectionModel().selectPrevious();

        // Cast this item to a string to fetch title
        String listviewItem = listview.getSelectionModel().getSelectedItem();

        // Play media!
        mediaSelection(listviewItem);
    }

    @FXML
    public void loop() {
    // TODO BY NIKOLAI
    }

    @FXML
    public void shufflePlaylist() {
        PlaylistHandler playlistShuffler = new PlaylistHandler();
        List<String> items = new ArrayList<>(playlistShuffler.loadPlaylistFromDB(getComputerName(), chosenPlaylist));
        Collections.shuffle(items);
        listview.getItems().setAll(items);
    }

    public void animationForLater() {
        Timeline animation = new Timeline(
                new KeyFrame(Duration.millis(500), e -> listview.getSelectionModel().selectNext()));
        listview.getSelectionModel().select(0);
        animation.setCycleCount(listview.getItems().size()-1);
        animation.play();
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

    public void addSearchResult() {
        // add button
        // add label
    }

    public void clearSearchResults() {

    }

    @FXML
    /**
     * Plays the Media
     */
    private void mpPlay(){
        // Play the mediaPlayer with the attached media
        mp.play();
        if (imgPause.getOpacity()==0 && imgPlay.getOpacity()==0){
            animation.fadeOut(imgPlay,0.5);
        }

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
        if (imgPause.getOpacity()==0 && imgPlay.getOpacity()==0){
            animation.fadeOut(imgPause,0.5);
        }
        setButtonUIImage(playPauseBut, playImg);
        isPlaying = false;
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
     * Handler for the stop button
     */
    private void buttonStop()
    {
        if (mp != null){
            // Stop the mediaPlayer
            mp.stop();
            setButtonUIImage(playPauseBut, playImg);
            isPlaying = false;
        }
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
    private void buttonSkipForward() {

        if(playlist.getPlaylistSize()>0) {
			// Play the next media in mediaPlayer
			getURLFromPlaylist(nextMedia.next);
		}

    }

    @FXML
    /**
     * Handler for the mute button
     */
    private void mute()
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
        mp.setVolume(Math.pow(sliderVolume.getValue()/100,2));
    }

    enum nextMedia {
        previous,
        next;
    }

    private void getURLFromPlaylist(nextMedia p){
        if (playlist.getPlaylistSize()>0) {
            //Creates a new mediaplayer with the next media to play
            if (p.equals(nextMedia.next)){
                createMediaPlayer(playlist.getNextUrlFromPlaylist());
            } else {
                createMediaPlayer(playlist.getPreviousFromPlaylist());
            }

            //Plays the new media
            mpPlay();
        }
    }

    private MediaPlayer createMediaPlayer(String URL){

        // Stops the current media if there is some playing
        buttonStop();

        me = new Media(new File(URL).toURI().toString());
        mp = new MediaPlayer(me);

        // Resize window and set minimum window size when media has loaded
        Runnable sizeToSceneRun = () -> MainApplication.sizeToScene();
        mp.setOnReady(sizeToSceneRun);

        // Resize the slider
        Runnable sliderResize = () -> sliderTimeScaling();
        mp.setOnReady(sliderResize);

        // While playing this changes the UI live
        Runnable timeSliderUpdater = () -> sliderTimeDragging();
        mp.setOnPlaying(timeSliderUpdater);

        // Set the volume to the same as last media
        sliderVolume();

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

        // Set the media player to be the one in the mediaview
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
            loopButton.setStyle("-fx-base:darkgrey");
            shuffleButton.setStyle("-fx-base:darkgrey");
        }
        else {
            // Main theme
            rootVBox.setStyle("");
            // Buttons
            playPauseBut.setStyle("");
            stopBut.setStyle("");
            skipForwardBut.setStyle("");
            skipBackwardBut.setStyle("");
            loopButton.setStyle("");
            shuffleButton.setStyle("");
        }
    }

    /**
     * Toggles maximize window
     */
     @FXML
     public void maximize() {
        Stage stage = (Stage) rootVBox.getScene().getWindow();
         stage.setMaximized(maximize.isSelected());
     }

    /**
     * Allows the user to drag movie by menu bar
     */
    @FXML
    private void drag(MouseEvent event) {
        Stage stage = (Stage) rootVBox.getScene().getWindow();
        stage.setY(event.getScreenY() - yOffset);
        stage.setX(event.getScreenX() - xOffset);
    }

    /**
     * Allows the user to drag movie by menu bar
     */
    @FXML
    private void presDrag(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    @FXML
    public void about() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText(null);
        alert.setContentText("Version: 0.5 \n\nSupported files: mp3, mp4 \n\nAuthors: Group 4\nBug report: someone@mail.com");
        // Add custom graphics to dialog box
        alert.setGraphic(new ImageView(Objects.requireNonNull(this.getClass().getResource("awesomeicon.png")).toString()));

        // Set to dark mord if activated
        if (darkmode.isSelected()) {
            alert.getDialogPane().setStyle("-fx-background-color: darkgrey");
        }

        // Get the Stage
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();

        // Add a custom icon
        stage.getIcons().add(new Image(Objects.requireNonNull(this.getClass().getResource("awesomeicon.png")).toString()));

        alert.showAndWait();
    }


    /**
     * Allows the user to exit the application after confirming an alert
     */
    @FXML
    public void exit() {
        // Values for alert box
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Exist");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to exit?");

        // Get the Stage
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();

        // Add a custom icon
        stage.getIcons().add(new Image(Objects.requireNonNull(this.getClass().getResource("awesomeicon.png")).toString()));

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

    /**
     * Get the name of the host PC
     * @return host name
     */
    private String getComputerName() {
        Map<String, String> env = System.getenv();
        if (env.containsKey("COMPUTERNAME"))
            return env.get("COMPUTERNAME");
        else if (env.containsKey("HOSTNAME"))
            return env.get("HOSTNAME");
        else
            return "Unknown Computer";
    }
}
