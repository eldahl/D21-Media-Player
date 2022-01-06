package com.d21mp.d21mediaplayer;

import javafx.scene.media.MediaPlayer;

public class MediaPlayerInfo {

    static public String getCurrentTime (MediaPlayer mp){

        String timeOfCurrentMedia = mp.getCurrentTime().toString();

        String currentTime = ""+round(toSek(timeOfCurrentMedia),2);

        return currentTime;
    }

    static public String getDuration (MediaPlayer mp){

        String durationOfCurrentMedia = mp.getMedia().getDuration().toString();

        String duration = ""+round(toSek(durationOfCurrentMedia),2);

        return duration;
    }



    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    static private double toSek (String s){

        String[] milliSek = s.split("\\.");

        double numSek = Double.parseDouble(milliSek[0])/1000;

        return numSek;
    }

}
