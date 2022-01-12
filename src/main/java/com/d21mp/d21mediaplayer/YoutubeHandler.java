package com.d21mp.d21mediaplayer;

import java.io.IOException;

public class YoutubeHandler {

    public void downloadYoutubeVideo(String URL) {

        System.out.println("Download started");

        try {
            Process p = Runtime.getRuntime().exec("cmd /c start cmd.exe /c \"" +
                    "cd C:\\Users\\Patrick\\IdeaProjects\\d-21-media-player-patrick\\src\\main\\java\\com\\d21mp\\d21mediaplayer\\media\\youtube-dl" +
                    "&& yt-dlp -f \"bv*[ext=mp4]+ba[ext=m4a]/b[ext=mp4] / bv*+ba/b\" -S \"filesize:100M\" -o /media/%(title)s.%(ext)s "+URL
                    +"\"");
        } catch (Exception e){
            e.printStackTrace();
        }



    }

}
