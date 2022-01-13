package com.d21mp.d21mediaplayer;

import javafx.animation.*;
import javafx.collections.FXCollections;
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
import java.net.URL;
import java.util.*;

public class MediaPlayerController implements Initializable {

    // region [FXML]
    @FXML
    private MediaView mediaView;
    @FXML
    private Label currentPlaylist;
    @FXML
    private TextField searchField;
    @FXML
    private VBox rootVBox, searchPlaylistView, mediaViewVBox;
    @FXML
    private Button playPauseBut, stopBut, skipForwardBut, skipBackwardBut, addToPlaylist, removeFromPlaylist,
            shuffleButton, loopButton, muteButton, minimizeButton, maximizeButton, exitButton;
    @FXML
    private Slider sliderTime, sliderVolume;
    @FXML
    RadioMenuItem darkmode;
    @FXML
    private ListView<String> playlistListView, searchListView;
	@FXML
    private ImageView imgPlay,imgPause;
    @FXML
    private CheckBox doYoutubeSearch;
    // endregion

    // region [Class variables]
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
    // Whether the search / playlist view is displayed in the UI with showing as default
    private boolean showSearchPlaylistView = true;
    // Whether media is currently playing
    private boolean isPlaying = false;

    // endregion

    // region [Class objects]

    // This is used for handling the animations
    AnimationHandler animation = new AnimationHandler();
    // This is used for handling the yt-search and results
    YoutubeHandler yt = new YoutubeHandler();

    // endregion

    // region [Initialize]

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
    // endregion

    // region [UI methods]

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
     * Minimize window
     */
    @FXML
    public void minimize() {
        Stage stage = (Stage) rootVBox.getScene().getWindow();
        stage.setIconified(true);
    }

    /**
     * Maximize window
     */
    @FXML
    public void maximize() {
        Stage stage = (Stage) rootVBox.getScene().getWindow();
        stage.setMaximized(!stage.isMaximized());
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

    /**
     * Displays the about alert box
     */
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
     * Shuffles the playlist by click on button and refreshes lower listview
     */
    @FXML
    public void shufflePlaylist() {
        PlaylistHandler playlistShuffler = new PlaylistHandler();
        List<String> items = new ArrayList<>(playlistShuffler.loadPlaylistFromDB(getComputerName(), chosenPlaylist));
        Collections.shuffle(items);
        playlistListView.getItems().setAll(items);
    }

    public void animationForLater() {
        Timeline animation = new Timeline(
                new KeyFrame(Duration.millis(500), e -> playlistListView.getSelectionModel().selectNext()));
        playlistListView.getSelectionModel().select(0);
        animation.setCycleCount(playlistListView.getItems().size()-1);
        animation.play();
    } //TODO IM ON IT //NIKOLAI

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
            minimizeButton.setStyle("-fx-base:darkgrey");
            maximizeButton.setStyle("-fx-base:darkgrey");
            exitButton.setStyle("-fx-base:darkgrey");
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
            minimizeButton.setStyle("");
            maximizeButton.setStyle("");
            exitButton.setStyle("");
        }
    }
    // endregion

    // region [Media methods]
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
     * Create the mediaplayer with its attributes
     */
    private void createMediaPlayer(String URL){

        // Stops the current media if there is some playing
        buttonStop();

        me = new Media(new File(URL).toURI().toString());
        mp = new MediaPlayer(me);

        // Resize window and set minimum window size when media has loaded
        Runnable sizeToSceneRun = MainApplication::sizeToScene;
        mp.setOnReady(sizeToSceneRun);

        // Resize the slider
        Runnable sliderResize = this::sliderTimeScaling;
        mp.setOnReady(sliderResize);

        // While playing this changes the UI live
        Runnable timeSliderUpdater = this::sliderTimeDragging;
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

        // Set the media player to be the one in the media view
        mediaView.setMediaPlayer(mp);
        mp.setAutoPlay(false);
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
     * Plays the Media
     */
    @FXML
    private void mpPlay(){
        // Play the mediaPlayer with the attached media
        mp.play();
        if (imgPause.getOpacity()==0 && imgPlay.getOpacity()==0){
            animation.fadeOut(imgPlay,0.5);
        }

        setButtonUIImage(playPauseBut, pauseImg);
        isPlaying = true;
    }

    /**
     * Pause the Media
     */
    @FXML
    private void mpPause(){
        // Pause the mediaPlayer
        mp.pause();
        if (imgPause.getOpacity()==0 && imgPlay.getOpacity()==0){
            animation.fadeOut(imgPause,0.5);
        }
        setButtonUIImage(playPauseBut, playImg);
        isPlaying = false;
    }

    /**
     * Handler for the play and pause button
     */
    @FXML
    private void buttonPlayPause() {
        if (!isPlaying)
            mpPlay();
        else
            mpPause();
    }

    /**
     * Handler for the stop button
     */
    @FXML
    private void buttonStop() {
        if (mp != null){
            // Stop the mediaPlayer
            mp.stop();
            setButtonUIImage(playPauseBut, playImg);
            isPlaying = false;
        }
    }

    /**
     * Handler for the mute button
     */
    @FXML
    private void mute() {
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
    private void sliderTimeDragging() {
        // Skip forwards and backwards in the media via the slider
        mp.setStartTime(Duration.seconds(sliderTime.getValue()));

        //Update the time
        //timeChangeShow();
    }

    private void timeChangeShow(){
        //The time
    } //TODO WHAT IS THIS?? //NIKOLAI

    /**
     * void for the Time sliders scaling
     */
    private void sliderTimeScaling() {
        //Slider scaling
        sliderTime.setMax(Double.parseDouble(MediaPlayerInfo.getDuration(mp)));
    }

    /**
     * Handler for the sound slider
     */
    @FXML
    private void sliderVolume() {
        // Skip forwards and backwards in the media via the slider
        mp.setVolume(Math.pow(sliderVolume.getValue()/100,2));
    }

    // endregion

    //region [Search methods]

    /**
     * Called when performing a search from the search view
     */
    @FXML
    public void search() {

        // Make sure we have something to search for
        if(!searchField.getText().isEmpty()) {
            // Search DB
            if(!doYoutubeSearch.isSelected()) {
                ArrayList<String> result = searchCreatorOrTitle(searchField.getText());
                searchListView.setItems(FXCollections.observableArrayList(result));
            }
            else { // Search YouTube

            }
        }
    }

    /**
     * Search Media table in Media Player DB by keyword found in a text field
     * @return All found results an Arraylist
     */
    public ArrayList<String> searchCreatorOrTitle(String searchString) {
        DB.selectSQL("SELECT URL FROM Media WHERE Creator LIKE '%" + searchString + "%' OR Title LIKE '%" + searchString +"%'");
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
    //endregion

    // region [Playlist methods]
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
        result.ifPresent(s -> playlistListView.setItems((FXCollections.observableArrayList(playlistCreator.loadPlaylistFromDB(getComputerName(), s)))));

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
        result.ifPresent(s -> playlistListView.setItems((FXCollections.observableArrayList(playlistOpener.loadPlaylistFromDB(getComputerName(), s)))));

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
     * Deletes a playlist
     */
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
        playlistListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2) {
                    // Use ListView's getSelected Item
                    String listviewItem = (String) playlistListView.getSelectionModel().getSelectedItem();

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
        playlistListView.setItems((FXCollections.observableArrayList(playlistAdd.loadPlaylistFromDB(getComputerName(), chosenPlaylist))));
    }

    /**
     * Remove a media from a given playlist
     */
    @FXML
    public void removeSongFromPlaylistViaButton() {
        // Create new object
        PlaylistHandler playlistHandler = new PlaylistHandler();

        // Delete media from database and thus listview
        playlistHandler.deleteMediaFromPlaylist(getComputerName(), chosenPlaylist, (String) playlistListView.getSelectionModel().getSelectedItem());

        // Refresh listview
        playlistListView.setItems((FXCollections.observableArrayList(playlistHandler.loadPlaylistFromDB(getComputerName(), chosenPlaylist))));
    }

    /**
     * Plays the next media in listview when clicking button
     */
    @FXML
    public void playNext() {
        // Current listview item
        String listviewItem = (String) playlistListView.getSelectionModel().getSelectedItem();
        // Select next item in listview
        playlistListView.getSelectionModel().selectNext();

        // Cast this item to a string to fetch title
        String nextListviewItem = (String) playlistListView.getSelectionModel().getSelectedItem();

        // Play media!
        mediaSelection(nextListviewItem);
    }

    /**
     * Plays the previous media in listview when clicking button
     */
    @FXML
    public void playPrevious() {

        // Select next item in listview
        playlistListView.getSelectionModel().selectPrevious();

        // Cast this item to a string to fetch title
        String listviewItem = playlistListView.getSelectionModel().getSelectedItem();

        // Play media!
        mediaSelection(listviewItem);
    }

    // endregion

    // region [Telemetry]
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

    // endregiong

    // region [in progress]

    /**
     * Loop playlist if button is activated
     */
    @FXML
    public void loop() {
    // TODO BY NIKOLAI
    }

    /**
     * Add search results to upper listview
     */
    public void addSearchResult() {
        // add button
        // add label
    }

    /**
     * Clear search results from upper listview
     */
    public void clearSearchResults() {

    }

    // endregion

} // End of class
