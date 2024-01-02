package org.fasnow.mutiple.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.fasnow.mutiple.model.nacos.Nacos;
import org.fasnow.mutiple.model.redis.Redis;
import org.fasnow.mutiple.ui.controller.MainController;
import org.fasnow.mutiple.ui.controller.NacosController;
import org.fasnow.mutiple.ui.controller.RedisController;

import java.io.IOException;
import java.util.Objects;

public class Instance {
    private static final Stage mainInstance = new Stage();
    private static final MainController mainController;
    static {
        FXMLLoader loader = new FXMLLoader(Application.class.getResource("/ui/main.fxml"));
        try {
            Scene scene=new Scene(loader.load(),800, 600);
            scene.getStylesheets().add(Objects.requireNonNull(Application.class.getResource("/ui/button.css")).toExternalForm());
            mainInstance.setScene(scene);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mainInstance.setTitle("模块选择");
        mainInstance.initModality(Modality.NONE);//不阻塞主窗口
        mainController =loader.getController();
    }
    private Instance() {
    }

    public static Stage getMainInstance(){
        mainInstance.toFront();
        return mainInstance;
    }

    public static Stage getNacosStage() throws IOException {
        FXMLLoader loader = new FXMLLoader(Application.class.getResource("/ui/nacos.fxml"));
        Stage stage= new Stage();
        stage.setScene(new Scene(loader.load(), 1100, 600));
        stage.setTitle("Nacos");
        stage.initModality(Modality.NONE);//不阻塞主窗口
        NacosController nacosController = loader.getController();
        nacosController.setMainController(mainController);
        Nacos.setMainController(mainController);
        return stage;
    }

    public static Stage getRedisStage() throws IOException {
        FXMLLoader loader = new FXMLLoader(Application.class.getResource("/ui/redis.fxml"));
        Stage stage= new Stage();
        stage.setScene(new Scene(loader.load(), 1100, 600));
        stage.setTitle("Redis");
        stage.initModality(Modality.NONE);//不阻塞主窗口
        RedisController redisController = loader.getController();
        redisController.setMainController(mainController);
        Redis.setMainController(mainController);
        return stage;
    }

    public static MainController getMainController() {
        return mainController;
    }
}
