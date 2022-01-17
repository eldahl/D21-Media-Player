module com.d21mp.d21mediaplayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.media;
    requires org.jsoup;
    requires commons.lang3;


    opens com.d21mp.d21mediaplayer to javafx.fxml;
    exports com.d21mp.d21mediaplayer;
}