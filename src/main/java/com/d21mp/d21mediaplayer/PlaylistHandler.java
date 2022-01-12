package com.d21mp.d21mediaplayer;

import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlaylistHandler {

    /**
     * Get the current media playings position
     * @return the position (int)
     */
    public int getCurrentMediaPlayingPosition() {
        return currentMediaPlayingPosition;
    }

    private int currentMediaPlayingPosition;

    /**
     * Get the current playlists size
     * @return the size (int)
     */
    public int getPlaylistSize() {
        return playlistSize;
    }

    private int playlistSize;

    //These arraylists show the master, shuffle and current playlists
    private final ArrayList<String> masterPlaylist = new ArrayList<>();
    private final ArrayList<String> shufflePlaylist = new ArrayList<>();
    private final ArrayList<String> currentPlaylist = new ArrayList<>();

    public PlaylistHandler(){
        this.currentMediaPlayingPosition = 0;
        this.playlistSize = masterPlaylist.size();
    }

    //Here the different names for the fld's and tbl's are stored for easy access
    private String tblMedia = "Media";
    private String fldURL = "URL";
    private String fldCreator = "Creator";
    private String fldTitle = "Title";

    private String tblPlaylist = "PlaylistCollection";
    private String fldPlaylistName = "PlaylistName";

    private String tblRelation = "Relation";
    private String fldRelationURL = "R-URL";
    private String fldRelationPlaylistName = "R-PlaylistName";

    /**
     * Get the current playlist
     */
    public ArrayList<String> getCurrentPlaylist() {
        return currentPlaylist;
    }

    /**
     * Create the shuffled playlist
     */
    private void shuffleThePlaylist () {
        shufflePlaylist.clear();
        shufflePlaylist.addAll(masterPlaylist);
        Collections.shuffle(shufflePlaylist);
    }

    /**
     * Change current playlist to a shuffled playlist
     */
    public void changeToShufflePlaylist(){
        if (shufflePlaylist.size()==0){
            shuffleThePlaylist();
        }
        this.currentMediaPlayingPosition=0;
        currentPlaylist.clear();
        currentPlaylist.addAll(shufflePlaylist);

    }

    /**
     * Change current playlist to master playlist
     */
    public void changeToMasterPlaylist(){
        if (masterPlaylist.size()>0){
            this.currentMediaPlayingPosition=0;
            currentPlaylist.clear();
            currentPlaylist.addAll(masterPlaylist);
        }


    }

    /**
     * Clear the playlist
     */
    public void clearCurrentPlaylist() {
        masterPlaylist.clear();
        shufflePlaylist.clear();
        currentPlaylist.clear();
        this.playlistSize = masterPlaylist.size();
    }

    /**
     * Save the current playlist
     */
    public void saveCurrentPlaylist(String name){
        savePlaylistToDB(name);
    }

    /**
     * Load another playlist into the current playlist
     */
    public void loadPlaylist(String name){
        masterPlaylist.addAll(loadPlaylistFromDB(name));
        currentPlaylist.addAll(masterPlaylist);
        this.playlistSize = masterPlaylist.size();
    }

    /**
     * Remove from the current playlist via position (starts from 1 not 0)
     */
    public void removeUrlFromPlaylist(int position){
        masterPlaylist.remove(position-1);
        this.playlistSize = masterPlaylist.size();
    }

    /**
     * Get an url from the current playlist via position (starts from 1 not 0)
     */
    public String getUrlFromPlaylist(int position){
        this.currentMediaPlayingPosition=position-1;
        return currentPlaylist.get(position-1);
    }

    /**
     * Get the next url in the playlist
     */
    public String getNextUrlFromPlaylist(){
        if (playlistSize==currentMediaPlayingPosition+1){
            currentMediaPlayingPosition=0;
        } else {
            currentMediaPlayingPosition+=1;
        }
        return currentPlaylist.get(currentMediaPlayingPosition);
    }

    /**
     * Get the previous url in the playlist
     */
    public String getPreviousFromPlaylist(){
        if (0>currentMediaPlayingPosition-1){
            currentMediaPlayingPosition=playlistSize;
        } else {
            currentMediaPlayingPosition-=1;
        }
        return currentPlaylist.get(currentMediaPlayingPosition);
    }




    public ArrayList<String> loadPlaylistOverview() {
        ArrayList<String> loadedList = new ArrayList<>();
        DB.selectSQL("SELECT PlaylistName FROM PlaylistOverview");
        do{
            String data = DB.getData();
            if (data.equals(DB.NOMOREDATA)){
                break;
            } else {
                loadedList.add(data);
            }
        } while (true);

        return loadedList;
    }



    /**
     * Select the different songs in the loaded playlist and make them into an array
     * @param name this is the name of the selected playlist, which will now be loaded
     */
    public List<String> loadPlaylistFromDB(String name){

        List<String> loadedPlaylist = new ArrayList<>();

        DB.selectSQL("select "+fldURL+" from "+tblPlaylist+" where "+fldPlaylistName+" = '"+name+"';");

        do{
            String data = DB.getData();
            if (data.equals(DB.NOMOREDATA)){
                break;
            } else {
                loadedPlaylist.add(data);
            }
        } while (true);


        return loadedPlaylist;
    }

    /**
     * Select the different songs in the loaded playlist and make them into an array
     * @param name this is the name of the now saved playlist
     */
    private void savePlaylistToDB(String name){

        for (String url : currentPlaylist){

            //Get the url of the current media
            String mediaURL = url;

            //Make a new playlist
            DB.insertSQL("Insert into "+tblPlaylist+" values("+
                    name+");");

            //Insert the link between the playlist and the media
            DB.insertSQL("Insert into "+tblRelation+ " values("+
                    mediaURL+
                    name+");");

        }
    }

    /**
     * Adds a new playlist to the database
     */
    public void createPlaylist(String nameOfPlaylist) {

        // Add playlist to PlaylistOverview
        DB.insertSQL("INSERT INTO PlaylistOverview VALUES ('" + nameOfPlaylist + "');");
    }

    /**
     * Adds a media to this medias
     */
    public void addMediaToMedias(String URL) {

        // Add playlist to PlaylistOverview
        DB.insertSQL("INSERT INTO Media (URL) VALUES ('"  + URL + "' );");
    }

    /**
     * Adds a media to this playlist
     */
    public void addMediaToPlaylist(String PlaylistName, String URL) {

        // Add playlist to PlaylistOverview
        DB.insertSQL("INSERT INTO PlaylistCollection (PlaylistName, URL) VALUES ('" + PlaylistName + "','" + URL + "' );");
    }
}
