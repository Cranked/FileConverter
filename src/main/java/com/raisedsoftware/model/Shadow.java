package com.raisedsoftware.model;

import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Shadow {

    public Rectangle createShadowedBox(double width, double height, double shadowWidth, double shadowHeight, double strokeWidth, double spread, double radius) {
        Rectangle rectangle = new Rectangle();
        rectangle.setWidth(width);
        rectangle.setHeight(height);
        rectangle.setStrokeWidth(strokeWidth);
        rectangle.setFill(Color.WHITE);
        rectangle.setStroke(Color.BLACK);
        rectangle.setArcWidth(radius);
        rectangle.setArcHeight(radius);
        DropShadow shadow = new DropShadow();
        shadow.setWidth(shadowWidth);
        shadow.setRadius(radius);
        shadow.setSpread(spread);
        shadow.setHeight(shadowHeight);
        rectangle.setEffect(shadow);
        return rectangle;
    }

    public Rectangle createInnerShadowedBox(double width, double height, double shadowWidth, double shadowHeight, double strokeWidth, double spread, double radius) {
        Rectangle rectangle = new Rectangle();
        rectangle.setWidth(width);
        rectangle.setHeight(height);
        rectangle.setStrokeWidth(strokeWidth);
        rectangle.setFill(Color.WHITE);
        rectangle.setStroke(Color.BLACK);
        rectangle.setArcWidth(radius);
        rectangle.setArcHeight(radius);
        InnerShadow shadow = new InnerShadow();
        shadow.setWidth(shadowWidth);
        shadow.setRadius(radius);
        shadow.setHeight(shadowHeight);
        rectangle.setEffect(shadow);
        return rectangle;
    }

}
