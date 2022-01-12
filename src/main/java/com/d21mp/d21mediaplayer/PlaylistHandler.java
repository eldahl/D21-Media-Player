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
    private String Media = "Media";
    private String URL = "URL";
    private String Creator = "Creator";
    private String Title = "Title";

    private String PlaylistCollection = "PlaylistCollection";
    private String PlaylistName = "PlaylistName";

    private String Relation = "Relation";
    private String RelationURL = "R-URL";
    private String RelationPlaylistName = "R-PlaylistName";

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


    public ArrayList<String> loadPlaylistOverview(String HostName) {
        ArrayList<String> loadedList = new ArrayList<>();
        DB.selectSQL("SELECT PlaylistName FROM PlaylistOverview WHERE HostName = '" + HostName + "'");
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
     * @param PlaylistName this is the name of the selected playlist, which will now be loaded
     */
    public List<String> loadPlaylistFromDB(String HostName, String PlaylistName){

        List<String> loadedPlaylist = new ArrayList<>();

        DB.selectSQL("SELECT Title from PlaylistCollection WHERE HostName = '" + HostName + "' AND PlaylistName = '" + PlaylistName + "'");

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
            DB.insertSQL("Insert into "+PlaylistCollection+" values("+
                    name+");");

            //Insert the link between the playlist and the media
            DB.insertSQL("Insert into "+Relation+ " values("+
                    mediaURL+
                    name+");");

        }
    }

    /**
     * Adds a new playlist to the database
     */
    public void createPlaylist(String HostName, String PlaylistName) {

        // Add playlist to PlaylistOverview
        DB.insertSQL("INSERT INTO PlaylistOverview (HostName, PlaylistName) VALUES ('" + HostName + "','" + PlaylistName + "' );");
    }

    /**
     * Deletes media from chosen playlist
     */
    public void deleteMediaFromPlaylist(String HostName, String PlaylistName, String Title) {
        DB.insertSQL("DELETE FROM PlaylistCollection WHERE HostName = '" + HostName + "' AND PlaylistName = '" + PlaylistName + "' AND Title = '" + Title + "'");
    }

    /**
     * If not exist, add media to media
     */
    public void ifNotExistAddToMediaTable(String HostName, String Title, String URL) {
        DB.insertSQL("INSERT INTO Media (HostName, Title, URL) SELECT '" + HostName  + "','" + Title + "','" + URL + "' WHERE NOT EXISTS (SELECT HostName, Title, URL FROM Media WHERE HostName = '" + HostName + "' AND Title = '" + Title + "' AND URL = '" + URL + "');");
    }

    /**
     * Adds a media to this playlist
     */
    public void addMediaToPlaylist(String HostName, String PlaylistName, String Title, String URL) {
        DB.insertSQL("INSERT INTO PlaylistCollection (HostName, PlaylistName, Title, URL) VALUES ('" + HostName + "','" + PlaylistName + "','" +  Title + "','" + URL + "' );");
    }
}
