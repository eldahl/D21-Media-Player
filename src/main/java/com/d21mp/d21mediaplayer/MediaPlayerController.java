package com.d21mp.d21mediaplayer;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MediaPlayerController implements Initializable {

    // region [FXML]
    @FXML
    private MediaView mediaView;
    @FXML
    private Label currentPlaylist, timelineCounter;
    @FXML
    private TextField searchField;
    @FXML
    private MenuBar menubarLeft, menubarRight;
    @FXML
    private VBox rootVBox, searchPlaylistView, mediaViewVBox;
    @FXML
    private Button playPauseBut, stopBut, skipForwardBut, skipBackwardBut, addToPlaylist, removeFromPlaylist,
            shuffleButton, loopButton, muteButton, minimizeButton, maximizeButton, exitButton, loopPlaylistButton, searchButton;
    @FXML
    private Slider progressSlider, sliderVolume;
    @FXML
    RadioMenuItem darkmode;
    @FXML
    private ListView<String> playlistListView, searchListView;
    @FXML
    private ImageView imgPlay, imgPause;
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
    private Image playImg, pauseImg, stopImg, skipForwardImg, skipBackwardImg, addButton, removeButton, shuffleImg, loopImg, muteImg, unmuteImg;
    // Whether the search / playlist view is displayed in the UI with showing as default
    private boolean showSearchPlaylistView = true;
    // Whether media is currently playing
    private boolean isPlaying = false;
    // Whether to loop media
    private boolean loopMedia = false;
    // Whether to loop playlist
    private boolean loopPlaylist = false;
    // Whether the Mediaplayer is muted or not
    private boolean isMuted = false;

    // endregion

    // region [Class objects]

    // This is used for handling the animations
    AnimationHandler animation = new AnimationHandler();
    // This is used for handling the yt-search and results
    YoutubeHandler youtubeHandler = new YoutubeHandler();
    // Create new object
    PlaylistHandler playlistHandler = new PlaylistHandler();
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
        unmuteImg = new Image(new File("src/main/resources/com/d21mp/d21mediaplayer/unmute.png").toURI().toString());

        // Check for successful loading of images and add to buttons
        if (playImg != null && pauseImg != null && stopImg != null && skipForwardImg != null &&
                skipBackwardImg != null && addButton != null && removeButton != null && shuffleImg != null && muteImg != null) {
            setButtonUIImage(playPauseBut, pauseImg);
            setButtonUIImage(stopBut, stopImg);
            setButtonUIImage(skipForwardBut, skipForwardImg);
            setButtonUIImage(skipBackwardBut, skipBackwardImg);
            setButtonUIImage(addToPlaylist, addButton);
            setButtonUIImage(removeFromPlaylist, removeButton);
            setButtonUIImage(shuffleButton, shuffleImg);
            setButtonUIImage(loopButton, loopImg);
            setButtonUIImage(muteButton, unmuteImg);
            setButtonUIImage(loopPlaylistButton, loopImg);
        } else
            System.out.println("Error: Failed to load UI icons");

        // Paint it black and preserve aspect ratio of video
        mediaViewVBox.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        mediaView.setPreserveRatio(true);

        animation.labelAnimation(currentPlaylist, 2);

        // Scan media directory
        new Thread(checkDirectory).start();
    }
    // endregion

    //region [Threading]

    String titleToLookFor="";
    String lastTitle;
    boolean exit = false;

    /**
     * This is the thread used for checking after the downloaded YouTube video
     */
    private final Thread checkDownload = new Thread(() -> {

        // The list with all the filenames
        ArrayList<String> fileNames = new ArrayList<>();

        // Reset the boolean exit so it doesn't close the thread, before the file has been found
        exit = false;

        // Max amount of times the thread cycles before closing (so it doesn't look forever, if an error accrues)
        int amountOfCycles = 20;

        // This is the path for the folder that is looked in
        File lookIn = new File(System.getProperty("user.dir")+"\\src\\main\\java\\com\\d21mp\\d21mediaplayer\\media");

        // This while loop ends when the file has been found
        while (!exit){
            try {
                //Next cycle get registered and then checks if it should end the thread
                amountOfCycles-=1;
                if (amountOfCycles==0){
                    System.out.println("Can't find it");
                    exit=true;
                    return;
                }

                // Here it gets/updates an array of the files in the targeted folder
                fileNames.clear();

                // Update the file list
                String[] files = lookIn.list();

                // Add all the file names to the fileNames array
                for (String s : files){
                    fileNames.add(s);
                }

                //If the fileNames array contains the title that was downloaded, then the media gets played
                if (fileNames.contains(titleToLookFor)){
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            //Create a new mediaplayer with the now finished downloaded video
                            createMediaPlayer(lookIn+"\\"+titleToLookFor);
                            //Plays the media
                            mpPlay();
                        }
                    });

                    //Ends the thread as it is no longer needed
                    exit=true;
                    return;
                } else {
                    Thread.sleep(2000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });

    /**
     * This is used for searching týoutube
     */
    private final Thread searchOnYoutube = new Thread(new Runnable() {
        @Override
        public void run() {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    ArrayList<String> ytResult = null;
                    try {
                        // Search YouTube and download the title of the amount of video results
                        ytResult = youtubeHandler.searchYoutube(searchField.getText(), 10);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // Shows the search results in the listview
                    searchListView.setItems(FXCollections.observableArrayList(ytResult));
                }
            });
        }
    });

    private final Thread checkDirectory = new Thread(new Runnable() {
        @Override
        public void run() {
            scanDirectory();
        }
    });

    //endregion

    //region [Utility]
    /**
     * Scan media directory on launch
     */
    private void scanDirectory() {

        PlaylistHandler playlistHandler = new PlaylistHandler();

        // Scan media directory
        File directory = new File("./src/main/java/com/d21mp/d21mediaplayer/media/");
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".mp4"));

        // Delete all preexisting entries from this HostName
        DB.deleteSQL("DELETE FROM Media WHERE HostName='"+ getComputerName() + "'");

        // Add all entries together for one insert query
        ArrayList<String> insertStrings = new ArrayList<String>();
        for (int i = 0; i < Objects.requireNonNull(files).length; i++) {
            // Get URI
            String URI = String.valueOf(files[i]);

            // Split to get title
            List<String> titlesAsList = Arrays.asList(URI.split("\\\\"));
            String title = titlesAsList.get(titlesAsList.size() - 1);
            title = title.split("\\.mp4")[0];

            // Add to DB INSERT query
            insertStrings.add("INSERT INTO Media (HostName, Title, URI) VALUES('"+getComputerName()+"', '" + title + "', '" + URI + "')");
        }
        // Add inserts together for better performance
        String dbInsertStr = String.join("\n", insertStrings);
        // Insert into DB
        DB.insertSQL(dbInsertStr);

    }
    //endregion

    // region [UI methods]

    /**
     * Show or hides the search / playlist view, depending on its state
     */
    public void toggleSearchPlaylistView() {
        if (!showSearchPlaylistView) {
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
     *
     * @param button Button to put image onto
     * @param img    Image to put on button
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
        if (result.get() == ButtonType.OK) {
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
            rootVBox.setStyle("-fx-base:#212120");
            // Buttons
            playPauseBut.setStyle("-fx-base:#233038");
            stopBut.setStyle("-fx-base:#233038");
            skipForwardBut.setStyle("-fx-base:#233038");
            skipBackwardBut.setStyle("-fx-base:#233038");
            searchButton.setStyle("-fx-base:#233038");
            muteButton.setStyle("-fx-base:#233038");
            shuffleButton.setStyle("-fx-base:#233038");
            //loopButton.setStyle("");
            //loopPlaylistButton.setStyle("");
            if(loopMedia)
                loopButton.setStyle("-fx-base:#233038");
            else
                loopButton.setStyle("");

            if(loopPlaylist)
                loopPlaylistButton.setStyle("-fx-base:#233038");
            else
                loopPlaylistButton.setStyle("");

        } else {
            // Main theme
            rootVBox.setStyle("");
            // Buttons
            playPauseBut.setStyle("");
            stopBut.setStyle("");
            skipForwardBut.setStyle("");
            skipBackwardBut.setStyle("");
            searchButton.setStyle("");
            muteButton.setStyle("");
            shuffleButton.setStyle("");
            if(loopMedia)
                loopButton.setStyle("-fx-base:#dec7af");
            else
                loopButton.setStyle("");

            if(loopPlaylist)
                loopPlaylistButton.setStyle("-fx-base:#dec7af");
            else
                loopPlaylistButton.setStyle("");
        }
    }
    // endregion

    // region [Media methods]

    /**
     * Method for selecting a media to be played in media viewer
     */
    public void mediaSelection(String title) {
        String path = "";
        DB.selectSQL("SELECT URI FROM Media WHERE HostName = '" + getComputerName() + "' AND Title = '" + title + "'");
        System.out.println("Playing: " + title+".mp4");
        do {
            String data = DB.getData();

            if (data.equals(DB.NOMOREDATA)) {
                break;
            }
            else {
                path = data;
                System.out.println("Path from DB:\n"+data+"\n");
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
    private void createMediaPlayer(String URI) {

        if (mp != null){
            // Stops the current media if there is some playing
            buttonStop();
        }

        me = new Media(new File(URI).toURI().toString());
        mp = new MediaPlayer(me);

        // Resize window and set minimum window size when media has loaded
        Runnable sizeToSceneRun = MainApplication::sizeToScene;
        mp.setOnReady(sizeToSceneRun);

        //Checks if the media was muted or not
        if (isMuted){
            // Set the volume to the same as last media
            mp.setVolume(0);
        } else {
            // Set the volume to the same as last media
            sliderVolume();
        }

        mp.setOnReady(new Runnable() {
            @Override
            public void run() {
                progressSlider.setMax(me.getDuration().toSeconds());
                mp.currentTimeProperty().addListener(new ChangeListener<Duration>() {
                    @Override
                    public void changed(ObservableValue<? extends Duration> observableValue, Duration oldValue, Duration newValue) {
                        progressSlider.setValue(newValue.toSeconds());
                        if(mp != null)
                            timelineCounter.setText(DurationFormatUtils.formatDuration((long)newValue.toMillis(), "HH:mm:ss") + " / " + DurationFormatUtils.formatDuration((long)mp.getTotalDuration().toMillis(), "HH:mm:ss"));
                    }
                });
            }
        });

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

        // When ended, autoplay next media
        mp.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                mp.seek(Duration.ZERO);

                // Loop media if button is activated
                if (loopMedia) {
                    mp.play();
                }
                // Loop playlist if button is activated
                else {
                    playNext();
                }
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

        //Set the default start of the filechooser to the media folder
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")+"\\src\\main\\java\\com\\d21mp\\d21mediaplayer\\media"));

        // Open classic file explorer window
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null){
            // Get URI for file
            String pathToSelectedFile = selectedFile.getAbsolutePath();

            createMediaPlayer(pathToSelectedFile);

            //play the media
            mpPlay();
        }
    }

    /**
     * Plays the Media
     */
    @FXML
    private void mpPlay() {
        if (mp != null){
            // Play the mediaPlayer with the attached media
            mp.play();
            setButtonUIImage(playPauseBut, pauseImg);
            isPlaying = true;
        }
    }

    /**
     * Pause the Media
     */
    @FXML
    private void mpPause(){
        if (mp != null){
            // Pause the mediaPlayer
            mp.pause();
            setButtonUIImage(playPauseBut, playImg);
            isPlaying = false;
        }
    }

    /**
     * Animation for play/pause when pressing anywhere in media view
     */
    @FXML
    private void playPauseOverlayAnimation() {
        if (mp != null){
            if (!isPlaying) {
                mp.play();
                if (imgPause.getOpacity() == 0 && imgPlay.getOpacity() == 0) {
                    animation.fadeOut(imgPlay, 0.5);
                    setButtonUIImage(playPauseBut, pauseImg);
                }
            } else {
                mp.pause();
                if (imgPause.getOpacity() == 0 && imgPlay.getOpacity() == 0) {
                    animation.fadeOut(imgPause, 0.5);
                    setButtonUIImage(playPauseBut, playImg);
                }
            }
        }
    }

    /**
     * Handler for the play and pause button
     */
    @FXML
    private void buttonPlayPause() {
        // Make sure we don't get an exception when trying to play/pause
        if(mp != null) {
            if (!isPlaying)
                mpPlay();
            else
                mpPause();
        }
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
        if (mp != null){
            if (mp.getVolume()==0){
                //Set the volume to the sliders value
                isMuted=false;
                sliderVolume();
                setButtonUIImage(muteButton, unmuteImg);
            } else {
                //Mute the sound
                isMuted=true;
                mp.setVolume(0);
                setButtonUIImage(muteButton, muteImg);
            }
        }
    }

    /**
     *  Seeks to the point in the video supplied by the progress slider
     */
    @FXML
    public void sliderSeek() {
        if (mp != null){
            mp.seek(Duration.seconds(progressSlider.valueProperty().doubleValue()));
        }
    }

    /**
     * Handler for the sound slider
     */
    @FXML
    private void sliderVolume() {
        if (mp != null && !isMuted){
            // Skip forwards and backwards in the media via the slider
            mp.setVolume(Math.pow(sliderVolume.getValue()/100,2));
        }
    }

    // endregion

    //region [Search methods]

    /**
     * Called when performing a search from the search view
     */
    @FXML
    public void search() throws IOException {

        // Make sure we have something to search for
        if(!searchField.getText().isEmpty()) {
            // Search DB
            if (!doYoutubeSearch.isSelected()) {
                ArrayList<String> result = searchCreatorOrTitle(searchField.getText());
                ListIterator<String> i = result.listIterator();
                while (i.hasNext()) {
                    String next = i.next();
                    List<String> path = Arrays.asList(next.split("\\\\"));
                    i.set(path.get(path.size() - 1).split("\\.")[0]);
                }
                searchListView.setItems(FXCollections.observableArrayList(result));
            } else {
                new Thread(searchOnYoutube).start();
            }
        }
    }

    /**
     * Search Media table in Media Player DB by keyword found in a text field
     * @return All found results an Arraylist
     */
    public ArrayList<String> searchCreatorOrTitle(String searchString) {
        DB.selectSQL("SELECT URI FROM Media WHERE Creator LIKE '%" + searchString + "%' OR Title LIKE '%" + searchString +"%' AND HostName = '" + getComputerName() + "' AND HostName IS NOT NULL");
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

    /**
     * This clears the search list
     */
    public void clearSearchResults(){
        searchListView.getItems().clear();
    }

    //endregion

    // region [Playlist methods]
    /**
     * Creates a new playlist and adds to database
     */
    @FXML
    public void createPlaylist() {
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
        chosenPlaylist = resultAsString.substring(9, resultAsString.length() - 1);

        // Set the current playlist label
        currentPlaylist.setText(chosenPlaylist);

        // Create playlist!
        result.ifPresent(s -> playlistHandler.createPlaylist(getComputerName(), s));

        // Refresh listview
        result.ifPresent(s -> playlistListView.setItems((FXCollections.observableArrayList(playlistHandler.loadPlaylistFromDB(getComputerName(), s)))));

        // Show Search and Playlist to the right
        if (!showSearchPlaylistView) {
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
        // Fetches PlaylistName from PlaylistOverview and collects in a List
        List<String> choices = playlistHandler.loadPlaylistOverview(getComputerName());

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
        chosenPlaylist = resultAsString.substring(9, resultAsString.length() - 1);

        // Open playlist!
        currentPlaylist.setText(chosenPlaylist);

        // Refresh listview
        result.ifPresent(s -> playlistListView.setItems((FXCollections.observableArrayList(playlistHandler.loadPlaylistFromDB(getComputerName(), s)))));

        // Show Search and Playlist to the right
        if (!showSearchPlaylistView) {
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
        // Fetches PlaylistName from PlaylistOverview and collects in a List
        List<String> choices = playlistHandler.loadPlaylistOverview(getComputerName());

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
        resultAsString = resultAsString.substring(9, resultAsString.length() - 1);

        // Delete playlist!
        playlistHandler.deletePlaylist(getComputerName(), resultAsString);
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
                }
            }
        });
    }

    /**
     * Allows the user to play media from listview by using a double click
     */
    @FXML
    public void getItemFromSearchList(MouseEvent event) {
        searchListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2) {
                    // Use ListView's getSelected Item

                    if (!doYoutubeSearch.isSelected()) {
                        String listviewItem = (String) searchListView.getSelectionModel().getSelectedItem();
                        System.out.println(listviewItem);
                        // Play media!
                        mediaSelection(listviewItem);

                        // When ended, autoplay next media
                        mp.setOnEndOfMedia(new Runnable() {
                            @Override
                            public void run() {
                                mp.seek(Duration.ZERO);

                                // Loop media if button is activated
                                if (loopMedia) {
                                    mp.play();
                                }
                                // Loop playlist if button is activated
                                // TODO NIKOLAI
                                // Go to next media
                                else {
                                    playNext();
                                }
                            }
                        });
                    }
                    else {
                        // Here the title is saved for searching later
                        String listviewItemTitle = searchListView.getSelectionModel().getSelectedItem();

                        // Here it takes the position of the selected item
                        int listviewItemVideoID = searchListView.getSelectionModel().getSelectedIndex();
                        // Here the youtube video gets downloaded via the videoID of the selected item
                        youtubeHandler.downloadVideo(youtubeHandler.getVideoID(listviewItemVideoID));

                        // This clears the search
                        clearSearchResults();
                        searchField.setText("");

                        // Here the thread for checking the download gets prepared and started
                        titleToLookFor=youtubeHandler.cleanTitle(listviewItemTitle)+".mp4";
                        lastTitle=titleToLookFor;
                        new Thread(checkDownload).start();
                    }
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
        FileChooser fileChooser = new FileChooser();

        //Set the default start of the filechooser to the media folder
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")+"\\src\\main\\java\\com\\d21mp\\d21mediaplayer\\media"));

        // Values for fileChooser
        File selectedFile = fileChooser.showOpenDialog(null);

        //Checks if there is a file
        if (selectedFile != null){
            // Get URI to file
            String pathToSelectedFile = selectedFile.getAbsolutePath();

            // Separate path by "\" to get filename
            List<String> pathDivided = new ArrayList<>(Arrays.asList(pathToSelectedFile.split("\\\\")));

            // Get file name
            String mediaTitle = pathDivided.get(pathDivided.size() - 1);

            // Remove ".mp4" from filename and play file
            mediaTitle = mediaTitle.substring(0, mediaTitle.length() - 4);

            // Add to database in table Media
            playlistHandler.ifNotExistAddToMediaTable(getComputerName(), mediaTitle, pathToSelectedFile);
            // Add to database in table PlaylistCollection
            playlistHandler.addMediaToPlaylist(getComputerName(), chosenPlaylist, mediaTitle, pathToSelectedFile);
            // Refresh listview
            playlistListView.setItems((FXCollections.observableArrayList(playlistHandler.loadPlaylistFromDB(getComputerName(), chosenPlaylist))));
        }
    }

    /**
     * Remove a media from a given playlist
     */
    @FXML
    public void removeSongFromPlaylistViaButton() {
        if (!playlistListView.getSelectionModel().isEmpty()){
            // Delete media from database and thus listview
            playlistHandler.deleteMediaFromPlaylist(getComputerName(), chosenPlaylist, (String) playlistListView.getSelectionModel().getSelectedItem());

            // Refresh listview
            playlistListView.setItems((FXCollections.observableArrayList(playlistHandler.loadPlaylistFromDB(getComputerName(), chosenPlaylist))));
        }
    }

    /**
     * Plays the next media in listview when clicking button
     */
    @FXML
    public void playNext() {
        if (!playlistListView.getItems().isEmpty()){
            // Current listview item
            String listviewItem = (String) playlistListView.getSelectionModel().getSelectedItem();

            //The next media
            String nextListviewItem;

            if (loopPlaylist && playlistListView.getSelectionModel().getSelectedIndex() == playlistListView.getItems().size()-1){
                // Cast this item to a string to fetch title
                nextListviewItem = (String) playlistListView.getItems().get(0);
                // Select next item in listview
                playlistListView.getSelectionModel().select(0);
            } else {
                // Select next item in listview
                playlistListView.getSelectionModel().selectNext();
                // Cast this item to a string to fetch title
                nextListviewItem = (String) playlistListView.getSelectionModel().getSelectedItem();
            }

            // Play media!
            mediaSelection(nextListviewItem);
        }
    }

    /**
     * Plays the previous media in listview when clicking button
     */
    @FXML
    public void playPrevious() {
        if (!playlistListView.getItems().isEmpty()){

            //The next media
            String listviewItem;

            if (loopPlaylist && playlistListView.getSelectionModel().getSelectedIndex() == 0){
                // Select next item in listview
                playlistListView.getSelectionModel().select(playlistListView.getItems().size()-1);
                // Cast this item to a string to fetch title
                listviewItem = playlistListView.getItems().get(playlistListView.getItems().size()-1);
            } else {
                // Select next item in listview
                playlistListView.getSelectionModel().selectPrevious();

                // Cast this item to a string to fetch title
                listviewItem = playlistListView.getSelectionModel().getSelectedItem();
            }

            // Play media!
            mediaSelection(listviewItem);
        }
    }

    // endregion

    // region [Telemetry]
    /**
     * Get the name of the host PC
     *
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
     * Loop media if button is activated
     */
    @FXML
    public void loop() {
        if (!loopMedia) {
            loopMedia = true;
            if(darkmode.isSelected())
                loopButton.setStyle("-fx-base:#233038");
            else
                loopButton.setStyle("-fx-base:#dec7af");
        } else {
            loopMedia = false;
            loopButton.setStyle("");
        }
    }

    /**
     * Loop playlist if button is activated
     */
    @FXML
    private void loopPlaylist() {

        if (!loopPlaylist) {
            loopPlaylist = true;

            if(darkmode.isSelected())
                loopPlaylistButton.setStyle("-fx-base:#233038");
            else
                loopPlaylistButton.setStyle("-fx-base:#dec7af");
        } else {
            loopPlaylist = false;
            loopPlaylistButton.setStyle("");
        }
    }

    // endregion

} // End of class
