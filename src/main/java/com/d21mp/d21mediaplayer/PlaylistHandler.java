package com.d21mp.d21mediaplayer;

import java.util.ArrayList;

public class PlaylistHandler {

    //The playlist that is the master/current playlist
    private ArrayList<String> Playlist = new ArrayList<>();

    //Here the different names for the fld's and tbl's are stored for easy access
    String tblMedia = "Media";
    String fldURL = "URL";
    String fldCreator = "Creator";
    String fldTitle = "Title";

    String tblPlaylist = "Playlist";
    String fldPlaylistName = "PlaylistName";

    String tblRelation = "Relation";
    String fldRelationURL = "R-URL";
    String fldRelationPlaylistName = "R-PlaylistName";


    /**
     * Get the current playlist
     */
    public ArrayList<String> getCurrentPlaylist() {
        return Playlist;
    }

    /**
     * Clear the playlist
     */
    public void clearCurrentPlaylist() {
        Playlist.clear();
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
        for (int i = 0; loadPlaylistFromDB(name).size()>i; i++){
            Playlist.add(loadPlaylistFromDB(name).get(i));
        }
    }

    /**
     * Remove from the current playlist via position
     */
    public void removeUrlFromPlaylist(int position){
        Playlist.remove(position);
    }

    /**
     * Get an url from the current playlist via position
     */
    public String getUrlFromPlaylist(int position){
        return Playlist.get(position);
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

        for (int i = 0; Playlist.size()>i; i++){

            //Get the url of the current media
            String mediaURL = Playlist.get(i);

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
