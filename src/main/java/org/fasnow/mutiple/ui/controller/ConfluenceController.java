package org.fasnow.mutiple.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ConfluenceController {
    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML
    private ChoiceBox<String> vulId;

    private final String[] ID = { "生活管家", "后勤管家", "管理员" };

    @FXML
    public void initialize() {
        System.out.println(111);
        vulId.getItems().addAll(ID);
    }
}