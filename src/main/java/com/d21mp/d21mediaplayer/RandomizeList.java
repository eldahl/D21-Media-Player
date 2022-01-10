package com.d21mp.d21mediaplayer;

import javafx.scene.media.Media;

import java.io.File;
import java.util.*;

public class RandomizeList {

    public static void main(String[] args) {

        String Filer =  "C:/Users/benja/Downloads/mp4 filer";
        File[]ListOfFiles = new File(Filer).listFiles();

        List<File> FilePath = Arrays.asList(ListOfFiles);


        System.out.println("Medialist before shuffling : " + FilePath);

        // the Collections method is used for shuffling the arraylist
        Collections.shuffle(FilePath);

        System.out.println("Medialist after shuffling : " +FilePath);

        Collections.shuffle(FilePath, new Random(System.nanoTime()));

        System.out.println("Medialist after being shuffled for a second time :" + FilePath);

        Media Video = new Media(FilePath.get(0).toURI().toString());

    }

}
