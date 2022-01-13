package com.d21mp.d21mediaplayer;

import java.io.File;
import java.util.ArrayList;

public class YoutubeHandler {

    /**
     * This downloads the video chosen from youtube into the right folder (youtube-dl/media/NAMEOFVIDEO)
     * @param searchFor this can be either the name of the video or the url
     */
    public void downloadVideo(String searchFor) {

        try {
            Process download = Runtime.getRuntime().exec("cmd /c start cmd.exe /c \"" +
                    "cd "+System.getProperty("user.dir")+"\\src\\main\\java\\com\\d21mp\\d21mediaplayer\\media\\youtube-dl" +
                    "&& yt-dlp -f \"bv*[ext=mp4]+ba[ext=m4a]/b[ext=mp4] / bv*+ba/b\" -S \"filesize:100M\" --no-playlist -o /media/%(title)s.%(ext)s \"ytsearch:"+searchFor
                    +"\"\"");
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public void get5Previews(String searchFor){

        resetPreview(new File(("user.dir") + "\\src\\main\\java\\com\\d21mp\\d21mediaplayer\\media\\youtube-dl\\media\\preview"));

        try {
            Process download = Runtime.getRuntime().exec("cmd /c start cmd.exe /c \"" +
                    "cd "+System.getProperty("user.dir")+"\\src\\main\\java\\com\\d21mp\\d21mediaplayer\\media\\youtube-dl" +
                    "&& yt-dlp --write-thumbnail --convert-thumbnail jpg -o /media/preview/%(title)s.%(ext)s --skip-download \"ytsearch5:"+searchFor+
                    "\"\"");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void resetPreview(File folder){
        final File[] files = folder.listFiles();

        try {
            System.out.println(files[0].toString());
        } catch (Exception e){

        }

        if (files!=null){
            for (File f: files){
                System.out.println("going");
                if (f.isDirectory()){
                    System.out.println("yes");
                } else {
                    System.out.println("deleted");
                    f.delete();
                }
            }
        }
        //folder.delete();
    }

    public void getPreview(){

        File folder = new File(System.getProperty("user.dir"));

        for (File fileEntry : folder.listFiles()){
            if (fileEntry.isDirectory()){
                previewLocation.add(fileEntry.getPath());
            }
        }
    }

    private ArrayList<String> previewLocation = new ArrayList<>();



}
