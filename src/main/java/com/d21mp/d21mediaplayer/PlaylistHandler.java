package com.d21mp.d21mediaplayer;

import java.util.ArrayList;
import java.util.List;

public class PlaylistHandler {

    /**
     * Fetches PlaylistName from PlaylistOverview
     */
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

    /**
     * Deletes a playlist from playlistOverview and rows with playlistName from PlaylistCollection
     */
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
    public void ifNotExistAddToMediaTable(String HostName, String Title, String URI) {
        DB.insertSQL("INSERT INTO Media (HostName, Title, URI) SELECT '" + HostName  + "','" + Title + "','" + URI + "' WHERE NOT EXISTS (SELECT HostName, Title, URI FROM Media WHERE HostName = '" + HostName + "' AND Title = '" + Title + "' AND URI = '" + URI + "');");
    }

    /**
     * Add multiple values
     */
    public void addMultipleValuesToMediaTable(String HostName, String URI, String Title) {
        DB.insertSQL("INSERT INTO Media (HostName, Title, URI) VALUES ('" + HostName + "','" + URI + "','" + Title + "');");
        // String HostName = GetComputerName(), GetComputerName(),
        // String URI = LKJASLDKJASD + ALSKDLKAJSLDKJS + ALKSJDLKAJSDLKJASD + ALKSJDLASKJDLKASJD


    }

    /**
     * Delete from media
     */
    public void deleteFromMediaTable(String HostName) {
        DB.insertSQL("DELETE FROM Media WHERE HostName = '" + HostName + "'");
    }

    /**
     * Adds a media to this playlist
     */
    public void addMediaToPlaylist(String HostName, String PlaylistName, String Title, String URI) {
        DB.insertSQL("INSERT INTO PlaylistCollection (HostName, PlaylistName, Title, URI) VALUES ('" + HostName + "','" + PlaylistName + "','" +  Title + "','" + URI + "' );");
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

