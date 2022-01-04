module com.d21mp.d21mediaplayer {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.d21mp.d21mediaplayer to javafx.fxml;
    exports com.d21mp.d21mediaplayer;
}