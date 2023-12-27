package org.fasnow.mutiple.app.ui;

import javafx.stage.Stage;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage){
       Instance.getMainInstance().show();
    }

    public static void main(String[] args) {
        launch();
    }
}