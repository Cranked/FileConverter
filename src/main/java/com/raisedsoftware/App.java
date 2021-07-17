package com.raisedsoftware;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Dosya Dönüştürücü");
        FXMLLoader loader = loadFXML("sample");
        Scene scene = new Scene(loader.load(), 1300, 750);
        primaryStage.setScene(scene);
        primaryStage.show();
        Controller controller = loader.getController();
        controller.init();

    }

    private static FXMLLoader loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader;
    }

    public static void main(String[] args) {
        launch();
    }

}