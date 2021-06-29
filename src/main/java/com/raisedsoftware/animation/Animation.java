package com.raisedsoftware.animation;

import javafx.scene.Node;

public class Animation<T extends Node> {

    public void hoverAnimationPulse(T t) {
        t.setStyle("-fx-border-color: #e4cb58;-fx-animated: pulse 1s;-fx-background-color:  #FF3399;-fx-text-fill:   #FFE4C4;-fx-border-radius: 5px;");
    }

    public void exitAnimationPulse(T t) {
        t.setStyle("-fx-background-color: #FFE4C4; -fx-border-color: #FF3333; -fx-text-fill: #FF3399; -fx-border-radius: 0.5em;");
    }
}
