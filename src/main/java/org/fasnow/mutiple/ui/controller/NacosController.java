package org.fasnow.mutiple.ui.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.fasnow.mutiple.Jwt;
import org.fasnow.mutiple.Log;
import org.fasnow.mutiple.Utils;
import org.fasnow.mutiple.entity.Result;
import org.fasnow.mutiple.model.nacos.Nacos;
import org.fasnow.mutiple.model.nacos.Vuls;
import com.alibaba.nacos.v2.config.server.model.ConfigInfo;
import com.alibaba.nacos.v2.console.model.Namespace;
import org.fasnow.mutiple.vul.Vul;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NacosController {

    @FXML
    private TextArea detectLog;

    @FXML
    private Button batchSubmitBtn;

    @FXML
    private Button exportBtn;

    @FXML
    private TabPane tabs;

    @FXML
    private TextArea description;

    @FXML
    private Tab descTab;

    @FXML
    private TextField passwordField1;

    @FXML
    private TextField versionField;

    @FXML
    private TextField useAuthPasswordField;

    @FXML
    private TextField useTokenTokenField;

    @FXML
    private TextField threadField;

    @FXML
    private Button submitBtn;

    @FXML
    private Button importBtn;
    @FXML
    private Button addUserBtn;

    @FXML
    private Button dumpConfigBtn;
    @FXML
    private Button deleteUserBtn;
    @FXML
    private Button updateUserBtn;
    @FXML
    private TextArea batchDetectLog;

    @FXML
    private RadioButton useAuth;

    @FXML
    private RadioButton useVul;

    @FXML
    private TextArea dumpLog;

    @FXML
    private TextField usernameField1;

    @FXML
    private TextField url;

    @FXML
    private RadioButton useToken;

    @FXML
    private TextField useAuthUsernameField;

    @FXML
    private TextField fileField;

    @FXML
    private ComboBox<String> vulCB1;


    @FXML
    private TextArea logArea1;

    Nacos nacosInstance = new Nacos();

    @FXML
    private GridPane basePanel;

    private static final Map<String,String> vulNameMethodMap = new HashMap<>();

//    private MainController mainController;

    private List<String> targets = new ArrayList<>();

    @FXML
    void initialize() {
        List<String> nameList = new ArrayList<>();
        List<String> descList = new ArrayList<>();
        for (Map.Entry<String, Vul> entry : Nacos.vuls.entrySet()) {
            Vul vul = entry.getValue();
            String vulName= vul.getName();
            vulNameMethodMap.put(vulName,entry.getKey());
            nameList.add(0,vulName);
            descList.add(
                    "漏洞名称: " + vulName + "\n\n" +
                    "影响版本: " + vul.getAffectedVersion() + "\n\n" +
                    "参考链接: " + vul.getRefUrl()
            );
        }
        if(nameList.size()>1){
            nameList.add(0,"All");
        }
        ObservableList<String> observableNames = FXCollections.observableArrayList(nameList);
        vulCB1.setItems(observableNames);
        vulCB1.getSelectionModel().selectFirst();
        description.setText(String.join("\n\n\n",descList));

        ToggleGroup toggleGroup = new ToggleGroup();
        useAuth.setToggleGroup(toggleGroup);
        useVul.setToggleGroup(toggleGroup);
        useToken.setToggleGroup(toggleGroup);

        exportBtn.setVisible(false);
        Utils.setGlobalFocusTraversal(false,basePanel);
    }

    public void submitBtnAction(Event event){
        String targetUrl = url.getText();
        if("".equals(targetUrl.trim())){
            return;
        }
        try {
            nacosInstance.setTarget(targetUrl);
        } catch (Exception e) {
            detectLog.appendText(Log.formatStderr(e));
            return;
        }
        tabs.getSelectionModel().select(1);
        String vulName = vulCB1.getValue();
        String methodName = vulNameMethodMap.get(vulName);
        Class<?> clazz = Vuls.class;
        Method[] methods = clazz.getDeclaredMethods();
        CompletableFuture.supplyAsync(() -> {
            Platform.runLater(()->submitBtn.setDisable(true));
            try {
                String version = nacosInstance.getVersion();
                Platform.runLater(()->versionField.setText(version));
            } catch (Exception e) {
                Platform.runLater(()->detectLog.appendText(Log.formatStderr(e)));
                return null;
            }
            for (Method method:methods) {
                try {
                    Result result;
                    if (method.getName().equals(methodName) || vulName.equals("All")) {
                        if (method.getParameterCount() == 0) {
                            result = (Result) method.invoke(nacosInstance);
                        } else {
                            result = (Result) method.invoke(nacosInstance, false);
                        }
                        Platform.runLater(()->detectLog.appendText(Log.formatStdout(result.isPassed(),targetUrl,Utils.getKeyByValue(vulNameMethodMap,method.getName()),result.getFeatureField())));
                    }
                }
                catch (Exception e) {
                    Platform.runLater(()->detectLog.appendText(Log.formatStderr(e)));
                }
            }
            return null;
        }).thenAccept(result -> {
            Platform.runLater(()->submitBtn.setDisable(false));
        });
    }
    public void batchSubmitBtnAction(Event event) {
        int threadNum =  Integer.parseInt(threadField.getText());
        if(threadNum<=0){
            batchDetectLog.appendText(Log.formatStderr(new Exception("错误的线程数")));
            return;
        }
        if(targets.size()==0){
            batchDetectLog.appendText(Log.formatStdout("目标为空,请先导入目标"));
            return;
        }
        String vulName = vulCB1.getValue();
        String methodName = vulNameMethodMap.get(vulName);
        Class<?> clazz = Vuls.class;
        Method[] methods = clazz.getDeclaredMethods();
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        CompletableFuture.supplyAsync(() -> {
            Platform.runLater(()->batchSubmitBtn.setDisable(true));

            for (String targetUrl : targets) {
                for (Method method:methods) {
                    if (method.getName().equals(methodName) || vulName.equals("All")) {
                        executorService.execute(() -> {
                            try {
                                Result result;
                                Nacos nacos = new Nacos();
                                nacos.setTarget(targetUrl);
                                if (method.getParameterCount() == 0) {
                                    result = (Result) method.invoke(nacos);
                                } else {
                                    result = (Result) method.invoke(nacos, true);
                                }
                                Platform.runLater(()->batchDetectLog.appendText(Log.formatStdout(result.isPassed(),targetUrl,Utils.getKeyByValue(vulNameMethodMap,method.getName()),result.getFeatureField())));
                            }
                            catch (Exception e) {
                                Platform.runLater(()->batchDetectLog.appendText(Log.formatStderr(e)));
                            }
                        });
                    }
                }
            }
            return null;
        }).thenAccept(result->{
            Platform.runLater(()->batchSubmitBtn.setDisable(false));
            executorService.shutdown();
        });
    }

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
        if(targets.size()==0){
            batchDetectLog.appendText(Log.formatStdout("导入的目标不能为空"));
        }
    }

    public TextArea getLogTextarea(){
        return detectLog ;
    }

    @FXML
    void addUserBtn1Action(Event event) {
        String username = usernameField1.getText();
        String password = passwordField1.getText();
        CompletableFuture.supplyAsync(() -> {
            Platform.runLater(()->addUserBtn.setDisable(true));
            try {
                nacosInstance.setTarget(url.getText());
                Result r = nacosInstance.addUser(username,password);
                Platform.runLater(()->logArea1.appendText(Log.formatStdout(r.getFeatureField())));
            } catch (Exception e) {
                Platform.runLater(()->logArea1.appendText(Log.formatStderr(e)));
            }
            return null;
        }).thenAccept(result->{
            Platform.runLater(()->addUserBtn.setDisable(false));
        });

    }

    @FXML
    void deleteUserBtn1Action(Event event) {
        String username = usernameField1.getText();
        CompletableFuture.supplyAsync(() -> {
            Platform.runLater(()->deleteUserBtn.setDisable(true));
            try {
                nacosInstance.setTarget(url.getText());
                Result r = nacosInstance.deleteUser(username);
                Platform.runLater(()->logArea1.appendText(Log.formatStdout(r.getFeatureField())));
            } catch (Exception e) {
                Platform.runLater(()->logArea1.appendText(Log.formatStderr(e)));
            }
            return null;
        }).thenAccept(result->{
            Platform.runLater(()->deleteUserBtn.setDisable(false));
        });
    }

    @FXML
    void updateBtn1Action(Event event) {
        String username = usernameField1.getText();
        String password = passwordField1.getText();
        CompletableFuture.supplyAsync(() -> {
            Platform.runLater(()->updateUserBtn.setDisable(true));
            try {
                nacosInstance.setTarget(url.getText());
                Result r = nacosInstance.resetPassword(username,password);
                Platform.runLater(()->logArea1.appendText(Log.formatStdout(r.getFeatureField())));
            } catch (Exception e) {
                Platform.runLater(()->logArea1.appendText(Log.formatStderr(e)));
            }
            return null;
        }).thenAccept(result->{
            Platform.runLater(()->updateUserBtn.setDisable(false));
        });
    }

    @FXML
    void useTokenAction(Event event) {

    }

    @FXML
    public void useVulAction(Event event) {

    }

    @FXML
    public void useAuthAction(Event event) {

    }

    @FXML
    void dumpConfigBtnAction(Event event) {
        String token = useTokenTokenField.getText();
        try {
            nacosInstance.setTarget(url.getText());
        } catch (Exception e) {
            dumpLog.appendText(Log.formatStdout(e.toString()));
            return;
        }
        CompletableFuture.supplyAsync(() -> {
            Platform.runLater(()->dumpConfigBtn.setDisable(true));
            if(useAuth.isSelected()){
                String username = useAuthUsernameField.getText();
                String password = useAuthPasswordField.getText();
                if ("".equals(username)||"".equals(password)){
                    dumpLog.appendText(Log.formatStdout("用户名或密码为空"));
                    return null;
                }
                try {
                    String authToken = nacosInstance.login(username,password);
                    Platform.runLater(()->dumpLog.appendText(Log.formatStdout("Authorization:"+authToken)));
                    List<Namespace> namespaces = nacosInstance.getNamespacesWithAuthToken(authToken);
                    Platform.runLater(()->dumpLog.appendText(Log.formatStdout("命名空间如下\n"+Nacos.NamespaceListToString(namespaces))));
                    for (Namespace namespace : namespaces) {
                        int configCount = namespace.getConfigCount();
                        if(configCount==0){
                            continue;
                        }
                        List<ConfigInfo> configs = nacosInstance.getConfigsWithAuthToken(authToken,namespace.getNamespace(),configCount);
                        Platform.runLater(()->dumpLog.appendText(Log.formatStdout(String.format("================%s================\n%s",namespace.getNamespaceShowName(),Nacos.ConfigListToString(configs)))));
                    }
                } catch (Exception e) {
                    Platform.runLater(()->dumpLog.appendText(Log.formatStdout(e.toString())));
                }
            }else if(useToken.isSelected()){
                if("".equals(token)){
                    Platform.runLater(()->dumpLog.appendText(Log.formatStdout("token不能为空")));
                }
                try {
                    if(!nacosInstance.validAuthToken(token)){
                        Platform.runLater(()->dumpLog.appendText(Log.formatStdout("token无效")));
                        return null;
                    }
                    List<Namespace> namespaces = nacosInstance.getNamespacesWithAuthToken(token);
                    Platform.runLater(()->dumpLog.appendText(Log.formatStdout(Nacos.NamespaceListToString(namespaces))));
                    for (Namespace namespace : namespaces) {
                        int configCount = namespace.getConfigCount();
                        if(configCount==0){
                            continue;
                        }
                        List<ConfigInfo> configs = nacosInstance.getConfigsWithAuthToken(token,namespace.getNamespace(),configCount);
                        Platform.runLater(()->dumpLog.appendText(Log.formatStdout(String.format("================%s================\n%s",namespace.getNamespaceShowName(),Nacos.ConfigListToString(configs)))));
                    }

                } catch (Exception e) {
                    Platform.runLater(()->dumpLog.appendText(Log.formatStdout(e.toString())));
                }

            }else if(useVul.isSelected()){
                List<String> a = new ArrayList<>();
                a.add("权限认证绕过漏洞(CVE-2021-29441)");
                a.add("默认token.secret.key配置(QVD-2023-6271)");
                if(!a.contains(vulCB1.getValue())){
                    Platform.runLater(()->dumpLog.appendText(Log.formatStdout("请右上方选择【权限认证绕过漏洞(CVE-2021-29441)】或者【默认token.secret.key配置(QVD-2023-6271)】")));
                    return null;
                }
                if(vulCB1.getValue().equals(a.get(0))){
                    try {
                        List<Namespace> namespaces = nacosInstance.getNamespacesWithMisconfig();
                        Platform.runLater(()->dumpLog.appendText(Log.formatStdout(Nacos.NamespaceListToString(namespaces))));
                        for (Namespace namespace : namespaces) {
                            int configCount = namespace.getConfigCount();
                            if(configCount==0){
                                continue;
                            }
                            List<ConfigInfo> configs = nacosInstance.getConfigsWithMisconfig(namespace.getNamespace(),configCount);
                            Platform.runLater(()->dumpLog.appendText(Log.formatStdout(String.format("================%s================\n%s",namespace.getNamespaceShowName(),Nacos.ConfigListToString(configs)))));
                        }
                    } catch (Exception e) {
                        Platform.runLater(()->dumpLog.appendText(Log.formatStdout(e.toString())));
                    }
                }else if(vulCB1.getValue().equals(a.get(1))){
                    String t = Jwt.generateJwt(Utils.getRandomString(8));
                    Platform.runLater(()->dumpLog.appendText(Log.formatStdout("本地生成生成token: "+t)));
                    try {
                        if(!nacosInstance.validAuthToken(t)){//抛出异常，不用return
                            Platform.runLater(()->dumpLog.appendText(Log.formatStdout("非默认token.secret.key")));
                        }
                        List<Namespace> namespaces = nacosInstance.getNamespacesWithAuthToken(t);
                        Platform.runLater(()->dumpLog.appendText(Log.formatStdout(Nacos.NamespaceListToString(namespaces))));
                        for (Namespace namespace : namespaces) {
                            int configCount = namespace.getConfigCount();
                            if(configCount==0){
                                continue;
                            }
                            List<ConfigInfo> configs = nacosInstance.getConfigsWithAuthToken(t,namespace.getNamespace(),configCount);
                            Platform.runLater(()->dumpLog.appendText(Log.formatStdout(String.format("================%s================\n%s",namespace.getNamespaceShowName(),Nacos.ConfigListToString(configs)))));
                        }
                    } catch (Exception e) {
                        Platform.runLater(()->dumpLog.appendText(Log.formatStdout(e.toString())));
                    }
                }

            }

            return null;
        }).thenAccept(result->{
            Platform.runLater(()->dumpConfigBtn.setDisable(false));
        });


    }

    @FXML
    void exportBtnAction(Event event) {

    }
}
