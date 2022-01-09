package com.d21mp.d21mediaplayer;

import javafx.scene.media.MediaPlayer;

public class MediaPlayerInfo {

    /**
     * This gets you the current time played of the specified mediaplayer
     * @param mp the mediaplayer
     * @return the current time played
     */
    static public String getCurrentTime (MediaPlayer mp){

        //Here the number is rounded and the offset of (0.5 secs) are removed
        String currentTime = ""+round(toSec(mp,getInfoType.currentTime)+0.49,2);

        //Here the current time string is returned
        return currentTime;
    }

    /**
     * This gets you the duration of the media in the specified mediaplayer
     * @param mp the mediaplayer
     * @return the duration
     */
    static public String getDuration (MediaPlayer mp){

        //Here the number is rounded
        String duration = ""+round(toSec(mp,getInfoType.duration),2);

        //Here the duration string is returned
        return duration;
    }


    /**
     * This is used for rounding the decimals and then choosing the amount of decimals you want to show (rounded)
     * @param value this is the double that you want to round
     * @param places this is the amount of decimals you want to round up to
     * @return the value rounded up till the amount of places behind the dot
     */
    private static double round(double value, int places) {

        //Her it checks if the places is above 0, else it will throw an exception
        if (places < 0) throw new IllegalArgumentException();

        //Here the factor number gets chosen. The bigger, the more digits are accurate
        long factor = (long) Math.pow(10, places);

        //Here the value is timed with the factor to get a larger number
        value = value * factor;

        //Here the value is rounded
        long tmp = Math.round(value);

        //Here the value gets devided with the factor to get the rounded number and then returned
        return (double) tmp / factor;
    }


    enum getInfoType{
        duration,
        currentTime;
    }

    /**
     * This takes the value from the mediaplayer and converts its string to a double and then takes it from millisec to sec
     * @param mp the mediaplayer
     * @param it the information you want in sec
     * @return the amount of sec
     */
    static private double toSec (MediaPlayer mp,getInfoType it){

        //Here the string infoString is initiated
        String infoString="";

        if (it.equals(it.duration)){
            //Here the duration of the media is found
            infoString = mp.getMedia().getDuration().toString();

        } else if (it.equals(it.currentTime)){
            //Here the value of the current time is found
            infoString = mp.getCurrentTime().toString();

        }

        //Here it splits the string so we dont get less then milliSeconds
        String[] milliSek = infoString.split("\\.");

        //Here number of Sec is returned
        return Double.parseDouble(milliSek[0])/1000;
    }

}
