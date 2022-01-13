package com.d21mp.d21mediaplayer;

public class YoutubeHandler {

    public void downloadYoutubeVideo(String URL) {

        try {
            Process download = Runtime.getRuntime().exec("cmd /c start cmd.exe /c \"" +
                    "cd "+System.getProperty("user.dir")+"\\src\\main\\java\\com\\d21mp\\d21mediaplayer\\media\\youtube-dl" +
                    "&& yt-dlp -f \"bv*[ext=mp4]+ba[ext=m4a]/b[ext=mp4] / bv*+ba/b\" -S \"filesize:100M\" --no-playlist -o /media/%(title)s.%(ext)s \"ytsearch:"+URL
                    +"\"\"");
        } catch (Exception e){
            e.printStackTrace();
        }

    }

}
