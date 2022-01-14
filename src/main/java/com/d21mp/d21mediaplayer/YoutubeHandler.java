package com.d21mp.d21mediaplayer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

public class YoutubeHandler {

    /**
     * This downloads the video chosen from youtube into the right folder (youtube-dl/media/NAMEOFVIDEO)
     * @param searchFor this can be either the name of the video or the url
     */
    public void downloadVideo(String searchFor) {

        try {
            // This starts the cmd with the yt-dlp function (downloads the video)
            Process download = Runtime.getRuntime().exec("cmd /c start cmd.exe /c \"" +
                    "cd "+System.getProperty("user.dir")+"\\src\\main\\java\\com\\d21mp\\d21mediaplayer\\media\\youtube-dl" +
                    "&& yt-dlp -f \"bv*[ext=mp4]+ba[ext=m4a]/b[ext=mp4] / bv*+ba/b\" -S \"filesize:100M\" --no-playlist " +
                    "-o "+System.getProperty("user.dir")+"\\src\\main\\java\\com\\d21mp\\d21mediaplayer\\media\\%(title)s.%(ext)s \"ytsearch:"+searchFor
                    +"\"\"");
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * This is used for searching for videos on youtube and then getting an array of titels
     * @param searchTerm the name that is searched for
     * @return an array of titels
     * @throws IOException if there is no HTML to get
     */
    public ArrayList<String> searchYoutube(String searchTerm,int amountOfResults) throws IOException {

        ArrayList<String> titles = new ArrayList<>();

        // This formats the url, so it can be received properly by the YouTube API
        String keyword = searchTerm;
        keyword = keyword.replace(" ", "+");

        // Here we send and receive the data from the Youtube API
        String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults="+amountOfResults+"&order=rating&q=" + keyword + "&key=AIzaSyAyTQiXk4idJBgwdIEAlLa_arRePZV0uyI";
        Document doc = Jsoup.connect(url).timeout(10 * 1000).ignoreContentType(true).get();

        // The data gets made into a string
        String getJson = doc.text();

        // The data gets split into videos
        String[] splitVideoData = getJson.split("youtube#searchResult");

        // This gets the title from the videos one by one and adds them to the array
        for (int i = 1; i<splitVideoData.length;i++){
            titles.add(cutString(splitVideoData[i],"\"title\": \"","\", \""));
        }

        // This returns the titels via array
        return titles;
    }

    /**
     * This is used for cutting in a string
     * @param cutThis the string that is going to be cut
     * @param from the point cut from
     * @param to the point cut to
     * @return the string in between
     */
    private String cutString (String cutThis,String from, String to){

        //This holds the string as it changes
        String middlePart = cutThis;

        // Here the first part gets cut of
        String[] fromContainer = middlePart.split(from);
        middlePart=fromContainer[1];

        //Here the last part gets cut of
        String[] toContainer = middlePart.split(to);
        middlePart=toContainer[0];

        //The middelpart gets returned
        return middlePart;
    }

}
