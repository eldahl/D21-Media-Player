package com.d21mp.d21mediaplayer;

import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlaylistHandler {

    private int currentMediaPlayingPosition;

    private int playlistSize;

    //These arraylists show the master, shuffle and current playlists
    private final ArrayList<String> masterPlaylist = new ArrayList<>();
    private final ArrayList<String> shufflePlaylist = new ArrayList<>();
    private final ArrayList<String> currentPlaylist = new ArrayList<>();

    public PlaylistHandler(){
        this.currentMediaPlayingPosition = 0;
        this.playlistSize = masterPlaylist.size();
    }

    private String PlaylistCollection = "PlaylistCollection";


    private String Relation = "Relation";

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
     * Adds a new playlist to the database
     */
    public void createPlaylist(String HostName, String PlaylistName) {
        DB.insertSQL("INSERT INTO PlaylistOverview (HostName, PlaylistName) VALUES ('" + HostName + "','" + PlaylistName + "' );");
    }

    public void deletePlaylist(String HostName, String PlaylistName) {
        DB.insertSQL("DELETE FROM PlaylistCollection WHERE HostName = '" + HostName + "' AND PlaylistName = '" + PlaylistName + "';");
        DB.insertSQL("DELETE FROM PlaylistOverview WHERE HostName = '" + HostName + "' AND PlaylistName = '" + PlaylistName + "';");
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

    /**
     * Get Title from Playlist
     */
    public String getTitleFromPlaylist(String HostName, String PlaylistName) {
        DB.selectSQL("SELECT Title from PlaylistCollection WHERE HostName = '" + HostName + "' AND PlaylistName = '" + PlaylistName + "'");
        do{
            String data = DB.getData();
            if (data.equals(DB.NOMOREDATA)){
                break;
            } else {
                return data;
            }
        } while (true);
        return null;
    }

    /**
     * Get size of chosen playlist
     */
    public int getListCount(String HostName, String PlaylistName) {
        DB.selectSQL("SELECT COUNT (Title) from PlaylistCollection WHERE HostName = '" + HostName + "' AND PlaylistName = '" + PlaylistName + "'");
        do{
            String data = DB.getData();
            if (data.equals(DB.NOMOREDATA)){
                break;
            } else {
                return Integer.parseInt(data);
            }
        } while (true);
        return 0;
    }
}

