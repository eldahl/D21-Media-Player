package com.d21mp.d21mediaplayer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class YoutubeHandler {

    public String getVideoID(int getNumber) {
        lastPlayedVideoID=videoID.get(getNumber);
        return videoID.get(getNumber);
    }

    private ArrayList<String> videoID = new ArrayList<>();

    public String getLastPlayedVideoID() {
        return lastPlayedVideoID;
    }

    private String lastPlayedVideoID;

    /**
     * This downloads the video chosen from youtube into the right folder (youtube-dl/media/NAMEOFVIDEO)
     * @param searchFor this can be either the name of the video or the url
     */
    public void downloadVideo(String searchFor) {

        if (searchFor.length()>20){
            //Make the searchFor smaller as the yt-dlp can't take the string if it is to long
            searchFor=searchFor.substring(0,20);
        }

        try {
            // This starts the cmd with the yt-dlp function (downloads the video)
            Process download = Runtime.getRuntime().exec("cmd /c start cmd.exe /c \"" +
                    "cd "+System.getProperty("user.dir")+"\\src\\main\\java\\com\\d21mp\\d21mediaplayer\\media\\youtube-dl" +
                    "&& yt-dlp -f \"bv[ext=mp4]+ba[ext=m4a]/b[ext=mp4] / bv+ba/b\" -S \"filesize:25M\" -S \"codec:h264\" --no-playlist" +
                    " -o "+System.getProperty("user.dir")+"\\src\\main\\java\\com\\d21mp\\d21mediaplayer\\media\\%(title)s.%(ext)s \"ytsearch:"+searchFor
                    +"\""
                    +"\"");
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

        Document doc= new Document("");

        try { // There is a lot of try catch here, but that is only there because there's a limit of 100 videos pr. day pr. key, and we would like more
            // Here we send and receive the data from the Youtube API
            String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults="+amountOfResults+"&order=viewCount&q=" + keyword + "&key=AIzaSyAyTQiXk4idJBgwdIEAlLa_arRePZV0uyI";
            doc = Jsoup.connect(url).timeout(10 * 1000).ignoreContentType(true).get();
            System.out.println("Nr.1 API");
        } catch (Exception e1){
            try {
                // Here we send and receive the data from the Youtube API (if the first one doesn't work)
                String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults="+amountOfResults+"&order=viewCount&q=" + keyword + "&key=AIzaSyDqiSI8LWBwwd9NvoPozyYMuMS3CeZ5cW8";
                doc = Jsoup.connect(url).timeout(10 * 1000).ignoreContentType(true).get();
                System.out.println("Nr.2 API");
            } catch (Exception e2){
                try {
                    // Here we send and receive the data from the Youtube API (if the first one doesn't work)
                    String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults="+amountOfResults+"&order=viewCount&q=" + keyword + "&key=AIzaSyCYjh7_5h6Gc-qe-4DYyxzd4f_3B8tvM6Y";
                    doc = Jsoup.connect(url).timeout(10 * 1000).ignoreContentType(true).get();
                    System.out.println("Nr.3 API");
                } catch (Exception e3){
                    try {
                        // Here we send and receive the data from the Youtube API (if the first one doesn't work)
                        String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults="+amountOfResults+"&order=viewCount&q=" + keyword + "&key=AIzaSyB0WuqRNHXTcqyrNjUlOMx2cge_8eAtH6M";
                        doc = Jsoup.connect(url).timeout(10 * 1000).ignoreContentType(true).get();
                        System.out.println("Nr.4 API");
                    } catch (Exception e4){
                        try {
                            // Here we send and receive the data from the Youtube API (if the first one doesn't work)
                            String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults="+amountOfResults+"&order=viewCount&q=" + keyword + "&key=AIzaSyAiDXgbbaKrmZP1LiXkIX8vlt4zx-FCkJE";
                            doc = Jsoup.connect(url).timeout(10 * 1000).ignoreContentType(true).get();
                            System.out.println("Nr.5 API");
                        } catch (Exception e5){
                            try {
                                // Here we send and receive the data from the Youtube API (if the first one doesn't work)
                                String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults="+amountOfResults+"&order=viewCount&q=" + keyword + "&key=AIzaSyB-g0GdU14yP4R3yFunUGBtQeoIRJe817Y";
                                doc = Jsoup.connect(url).timeout(10 * 1000).ignoreContentType(true).get();
                                System.out.println("Nr.6 API");
                            } catch (Exception e6){
                                try {
                                    // Here we send and receive the data from the Youtube API (if the first one doesn't work)
                                    String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults="+amountOfResults+"&order=viewCount&q=" + keyword + "&key=AIzaSyBnvw_ifZIDtlpfbSIo1SEHLfVTytAoXfc";
                                    doc = Jsoup.connect(url).timeout(10 * 1000).ignoreContentType(true).get();
                                    System.out.println("Nr.7 API");
                                } catch (Exception e7){
                                    try {
                                        // Here we send and receive the data from the Youtube API (if the first one doesn't work)
                                        String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults="+amountOfResults+"&order=viewCount&q=" + keyword + "&key=AIzaSyCwumYwHIr0tDB9TSmR0IhM9eCVHLGIng4";
                                        doc = Jsoup.connect(url).timeout(10 * 1000).ignoreContentType(true).get();
                                        System.out.println("Nr.8 API");
                                    } catch (Exception e8){
                                        try {
                                            // Here we send and receive the data from the Youtube API (if the first one doesn't work)
                                            String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults="+amountOfResults+"&order=viewCount&q=" + keyword + "&key=AIzaSyCBK375jNCU7j6h77Yn2V-I7QjNwHpUk3U";
                                            doc = Jsoup.connect(url).timeout(10 * 1000).ignoreContentType(true).get();
                                            System.out.println("Nr.9 API");
                                        } catch (Exception e9){
                                            // Here we send and receive the data from the Youtube API (if the first one doesn't work)
                                            String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults="+amountOfResults+"&order=viewCount&q=" + keyword + "&key=AIzaSyAAWRrkCKCzzI3icLJRzLuKU9qeVKmRc9Y";
                                            doc = Jsoup.connect(url).timeout(10 * 1000).ignoreContentType(true).get();
                                            System.out.println("Nr.10 API");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        /*
        Key1:AIzaSyAyTQiXk4idJBgwdIEAlLa_arRePZV0uyI
        Key2:AIzaSyDqiSI8LWBwwd9NvoPozyYMuMS3CeZ5cW8
        Key3:AIzaSyCYjh7_5h6Gc-qe-4DYyxzd4f_3B8tvM6Y
        Key4:AIzaSyB0WuqRNHXTcqyrNjUlOMx2cge_8eAtH6M
        Key5:AIzaSyAiDXgbbaKrmZP1LiXkIX8vlt4zx-FCkJE
        Key6:AIzaSyB-g0GdU14yP4R3yFunUGBtQeoIRJe817Y
        Key7:AIzaSyBnvw_ifZIDtlpfbSIo1SEHLfVTytAoXfc
        Key8:AIzaSyCwumYwHIr0tDB9TSmR0IhM9eCVHLGIng4
        Key9:AIzaSyCBK375jNCU7j6h77Yn2V-I7QjNwHpUk3U
        Key10:AIzaSyAAWRrkCKCzzI3icLJRzLuKU9qeVKmRc9Y
         */

        if (doc == null){
            return titles;
        }

        // The data gets made into a string
        String getJson = doc.text();

        // The data gets split into videos
        String[] splitVideoData = getJson.split("youtube#searchResult");

        if (videoID.size()==0){
            // Updates the videoID arraylist
            setVideoID(splitVideoData);
        } else {
            // Clears the list first
            videoID.clear();
            //  Updates the videoID arraylist
            setVideoID(splitVideoData);
        }

        // This gets the title from the videos one by one and adds them to the array
        for (int i = 1; i<splitVideoData.length;i++){
            titles.add(cutString(splitVideoData[i],"\"title\": \"","\", \""));
        }

        // This returns the titels via array
        return titles;
    }

    /**
     * This is used for updating the relevant videoIDs in the arraylist
     * @param splitVideoData the data splittet up to single video data
     * @return the new videoIDs
     */
    private ArrayList<String> setVideoID(String[] splitVideoData) {

        // This gets the title from the videos one by one and adds them to the array
        for (int i = 1; i<splitVideoData.length;i++){
            videoID.add(cutString(splitVideoData[i],"\"videoId\": \"","\" },"));
        }

        return videoID;
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

    /**
     * This is used for cleaning the title of a youtube video, removing special characters
     * @param title the title of the video
     * @return the clean version of the title
     */
    public String cleanTitle(String title){

        // These are the characters which gets filtered out
        String[] specialCharacters = {"/","\\|"};

        // This loops through all the special characters and replaces them with '_'
        for (String s: specialCharacters){
            title = title.replaceAll(s,"_");
        }

        //The filtered string gets send back
        return title;
    }

}
