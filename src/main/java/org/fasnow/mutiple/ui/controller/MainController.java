package org.fasnow.mutiple.ui.controller;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import okhttp3.*;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;
import javafx.scene.layout.GridPane;
import org.fasnow.mutiple.HttpClient;
import org.fasnow.mutiple.Log;
import org.fasnow.mutiple.Utils;
import org.fasnow.mutiple.ui.Instance;

public class MainController {
    @FXML
    private TextField usernameField;

    @FXML
    private TextArea log;

    @FXML
    private Button vcenterMode;

    @FXML
    private Button confluenceMode;

    @FXML
    private TextField hostField;

    @FXML
    private ComboBox<String> proxyTypeComboBox;

    @FXML
    private TextField portField;

    @FXML
    private MenuItem proxyMenu;

    @FXML
    private Button nacosMode;

    @FXML
    private Label updateProxyBtn;

    @FXML
    private TextField timeoutField;

    @FXML
    private TextField passwordField;

    @FXML
    private CheckBox enableCheckbox;

    @FXML
    private Button updateTimeoutBtn;

    @FXML
    private GridPane basePanel;

    @FXML
    private Button redisMode;

    private static OkHttpClient client = HttpClient
            .newHttpClientBuilder()
            .connectTimeout(HttpClient.getDefaultTimout(),TimeUnit.SECONDS)
            .build();

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        updateProxyBtn.getStyleClass().add("button-label");
        String[] nameList = {"HTTP","SOCKS"};
        ObservableList<String> observableNames = FXCollections.observableArrayList(nameList);
        proxyTypeComboBox.setItems(observableNames);
        proxyTypeComboBox.getSelectionModel().selectFirst();
        proxyTypeComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(javafx.beans.value.ObservableValue<? extends String> observable, String oldValue, String newValue) {
                enableCheckboxAction(null);
            }
        });

        Utils.setGlobalFocusTraversal(false,basePanel);
    }

    @FXML
    void nacosModeAction(ActionEvent event) {
        try {
            Instance.getNacosStage().show();
        } catch (Exception err) {
            log.appendText(Log.formatStderr(err));
        }
    }

    @FXML
    void redisModeAction(ActionEvent event) {
        try {
            Instance.getRedisStage().show();
        } catch (Exception err) {
            log.appendText(Log.formatStderr(err));
        }
    }

    @FXML
    void enableCheckboxAction(ActionEvent event) {
        if(enableCheckbox.isSelected()){
            String type = proxyTypeComboBox.getSelectionModel().getSelectedItem();
            Proxy.Type t = Proxy.Type.DIRECT;
            switch (type){
                case "HTTP":
                    t=Proxy.Type.HTTP;
                    break;
                case "SOCKS":
                    t=Proxy.Type.SOCKS;
                    break;
            }
            String host = hostField.getText().trim();
            int port = Integer.parseInt(portField.getText().trim());
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            if("".equals(host)||0>port||65535<port){
                log.appendText(Log.formatStderr(new Exception("输入内容有误")));
                return;
            }
            setProxy(t,host, (port),username,password);
            return;
        }
        setProxy(Proxy.Type.DIRECT,"",0,"","");
    }

    @FXML
    void updateProxyAction(MouseEvent event) {
        enableCheckboxAction(null);
    }

    @FXML
    void updateTimeoutBtnAction(ActionEvent event) {
        setTimeout(Integer.parseInt(timeoutField.getText()),TimeUnit.SECONDS);
    }

    public TextArea getLogTextArea(){
        return log;
    }

    public Response httpCall(String pluginName,Request request) throws IOException {
        log.appendText(Log.formatStdoutWithPlugin(pluginName,String.format("%s %s",request.method(),request.url())));
        return client.newCall(request).execute();
    }

    public void setProxy(Proxy.Type type,String host,int port,String username,String password){
        OkHttpClient.Builder builder=client.newBuilder();
        if(type== Proxy.Type.DIRECT){
            client=builder.proxy(Proxy.NO_PROXY).build();
            return;
        }
        builder.proxy(new Proxy(type, new InetSocketAddress(host, port)));
        if(!"".equals(username)){
            Authenticator proxyAuthenticator = (route, response) -> {
                String credential = Credentials.basic(username, password);
                return response.request().newBuilder()
                        .header("Proxy-Authorization", credential)
                        .build();
            };
            builder.proxyAuthenticator(proxyAuthenticator);
        }
        client=builder.build();
    }

    public synchronized void setTimeout(int timeout, TimeUnit unit){
        HttpClient.setDefaultTimeout(timeout);
        client= client.newBuilder().connectTimeout(timeout,unit).build();
    }
}
