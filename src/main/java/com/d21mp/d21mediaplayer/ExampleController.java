package com.d21mp.d21mediaplayer;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ExampleController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}