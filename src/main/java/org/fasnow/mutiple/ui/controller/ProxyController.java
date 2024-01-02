package org.fasnow.mutiple.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.fasnow.mutiple.Config;
import org.fasnow.mutiple.entity.Proxy;


public class ProxyController {
    @FXML // fx:id="portFiled"
    private TextField portFiled; // Value injected by FXMLLoader

    @FXML // fx:id="enableBtn"
    private CheckBox enableBtn; // Value injected by FXMLLoader

    @FXML // fx:id="proxyComboBox"
    private ComboBox<String> proxyComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="editBtn"
    private Button editBtn; // Value injected by FXMLLoader

    @FXML // fx:id="usernameFiled"
    private TextField usernameFiled; // Value injected by FXMLLoader

    @FXML // fx:id="passwordFiled"
    private TextField passwordFiled; // Value injected by FXMLLoader

    @FXML // fx:id="hostFiled"
    private TextField hostFiled; // Value injected by FXMLLoader

    @FXML // fx:id="saveBtn"
    private Button saveBtn; // Value injected by FXMLLoader

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        proxyComboBox.getItems().setAll("HTTP","SOCKS");
        proxyComboBox.getSelectionModel().selectFirst();
        assert portFiled != null : "fx:id=\"portFiled\" was not injected: check your FXML file 'proxy.fxml'.";
        assert enableBtn != null : "fx:id=\"enableBtn\" was not injected: check your FXML file 'proxy.fxml'.";
        assert proxyComboBox != null : "fx:id=\"proxyComboBox\" was not injected: check your FXML file 'proxy.fxml'.";
        assert editBtn != null : "fx:id=\"editBtn\" was not injected: check your FXML file 'proxy.fxml'.";
        assert usernameFiled != null : "fx:id=\"usernameFiled\" was not injected: check your FXML file 'proxy.fxml'.";
        assert passwordFiled != null : "fx:id=\"passwordFiled\" was not injected: check your FXML file 'proxy.fxml'.";
        assert hostFiled != null : "fx:id=\"hostFiled\" was not injected: check your FXML file 'proxy.fxml'.";
        assert saveBtn != null : "fx:id=\"saveBtn\" was not injected: check your FXML file 'proxy.fxml'.";

    }

    @FXML
    public void enableBtnAction(ActionEvent event) {
        if (enableBtn.isSelected()) {
            editBtn.setDisable(true);
            saveBtn.setDisable(true);
            Config.getConfig().setProxy(
                    new Proxy(
                            hostFiled.getText(),
                            portFiled.getText(),
                            usernameFiled.getText(),
                            passwordFiled.getText(),
                            enableBtn.isSelected(),
                            proxyComboBox.getSelectionModel().getSelectedItem()
                    )
            );
        } else {
            editBtn.setDisable(false);
            saveBtn.setDisable(true);
        }
    }
    @FXML
    public void editBtnAction(ActionEvent event) {
        hostFiled.setDisable(false);
        portFiled.setDisable(false);
        usernameFiled.setDisable(false);
        passwordFiled.setDisable(false);
        proxyComboBox.setDisable(false);
        editBtn.setDisable(true);
        saveBtn.setDisable(false);
        enableBtn.setDisable(true);
    }
    @FXML
    public void saveBtnBtnAction(ActionEvent event) {
        hostFiled.setDisable(true);
        portFiled.setDisable(true);
        usernameFiled.setDisable(true);
        passwordFiled.setDisable(true);
        proxyComboBox.setDisable(true);
        editBtn.setDisable(false);
        saveBtn.setDisable(true);
        enableBtn.setDisable(false);
    }


    public void setProxy(Proxy proxy){
        hostFiled.setText(proxy.getHost());
        portFiled.setText(proxy.getPort());
        usernameFiled.setText(proxy.getUsername());
        passwordFiled.setText(proxy.getPassword());
        enableBtn.setDisable(proxy.isEnable());
    }
}
