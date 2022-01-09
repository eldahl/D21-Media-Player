package com.d21mp.d21mediaplayer;

import java.util.ArrayList;

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

    //The playlist that is the master/current playlist
    private ArrayList<String> playlist = new ArrayList<>();

    public PlaylistHandler(){
        this.currentMediaPlayingPosition = 0;
        this.playlistSize = playlist.size();
    }

    //Here the different names for the fld's and tbl's are stored for easy access
    private String tblMedia = "Media";
    private String fldURL = "URL";
    private String fldCreator = "Creator";
    private String fldTitle = "Title";

    private String tblPlaylist = "Playlist";
    private String fldPlaylistName = "PlaylistName";

    private String tblRelation = "Relation";
    private String fldRelationURL = "R-URL";
    private String fldRelationPlaylistName = "R-PlaylistName";

    /**
     * Get the current playlist
     */
    public ArrayList<String> getCurrentPlaylist() {
        return playlist;
    }

    /**
     * Clear the playlist
     */
    public void clearCurrentPlaylist() {
        playlist.clear();
        this.playlistSize = playlist.size();
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
        playlist.addAll(loadPlaylistFromDB(name));
        this.playlistSize = playlist.size();
    }

    /**
     * Remove from the current playlist via position (starts from 1 not 0)
     */
    public void removeUrlFromPlaylist(int position){
        playlist.remove(position-1);
        this.playlistSize = playlist.size();
    }

    /**
     * Get an url from the current playlist via position (starts from 1 not 0)
     */
    public String getUrlFromPlaylist(int position){
        this.currentMediaPlayingPosition=position-1;
        return playlist.get(position-1);
    }

    /**
     * Get the next url in the playlist
     */
    public String getNextUrlFromPlaylist(){
        return playlist.get(currentMediaPlayingPosition+1);
    }

    /**
     * Get the previous url in the playlist
     */
    public String getPreviousFromPlaylist(){
        return playlist.get(currentMediaPlayingPosition-1);
    }







    /**
     * Select the different songs in the loaded playlist and make them into an array
     * @param name this is the name of the selected playlist, which will now be loaded
     */
    private ArrayList<String> loadPlaylistFromDB(String name){

        ArrayList<String> loadedPlaylist = new ArrayList<>();

        DB.selectSQL("select "+fldURL+" from "+tblMedia+" where "+fldPlaylistName+" = "+name+";");

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

        for (String url : playlist){

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

}
