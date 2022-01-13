package com.d21mp.d21mediaplayer;

import javafx.animation.*;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class AnimationHandler {

    /**
     * fadeIn is used to make the image fade into view with a transition
     * @param img is the targeted imageView
     * @param duration is the amount of time set for one animation cycle
     */
   public void fadeIn(ImageView img, double duration)
    {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(duration),img);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1.0);
        fadeTransition.play();
    }

    /**
     * fadeOut is used to make the image fade out of view with a transition
     * @param img is the targeted imageView
     * @param duration is the amount of time set for one animation cycle
     */
    public void fadeOut(ImageView img, double duration)
    {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(duration),img);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0);
        fadeTransition.play();
    }

    /**
     * Animation for current playlist. I want this to keep scrolling from the right if the text is too wide. Like your radio.
     * @param lbl is the targeted Label
     * @param duration is the amount of time set for one animation cycle
     */
    public void labelAnimation(Label lbl, double duration) {

        KeyFrame kfP = new KeyFrame(Duration.seconds(2), new KeyValue(currentPlaylist.textFillProperty(), Color.GREY));
        Timeline TIMER = new Timeline();
        TIMER.getKeyFrames().add(kfP);
        TIMER.setCycleCount(Animation.INDEFINITE);
        TIMER.setAutoReverse(true);
        TIMER.play();

    }

}
