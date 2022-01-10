package com.d21mp.d21mediaplayer;

import java.util.ArrayList;

public class DBHandling {

    public DBHandling() {
    }

    public void createPlaylist(String nameOfPlaylist) {

        // Add playlist to PlaylistOverview
        DB.insertSQL("INSERT INTO PlaylistOverview VALUES ('" + nameOfPlaylist + "');");
    }
}
