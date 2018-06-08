package com.jacek;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    private static Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main_layout.fxml"));
        Parent root = fxmlLoader.load();

        controller = fxmlLoader.getController();

        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Transmission error detection simulator");

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

}