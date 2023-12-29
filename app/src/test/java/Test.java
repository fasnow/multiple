
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;


/**
 * @author th
 * @description: TODO
 * @projectName hashdog
 * @date 2020/2/1620:48
 */
public class Test extends Application {

    public static void main(String[] args) {
        Application.launch(Test.class,args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Student s1 = new Student("s1",18,90);
        Student s2 = new Student("s2",18,90);
        Student s3 = new Student("s3",18,90);
        Student s4 = new Student("s4",18,90);
        Student s5 = new Student("s5",18,90);

        AnchorPane an = new AnchorPane();
        //下拉列表
        ChoiceBox<Student> cb = new ChoiceBox<Student>();
        cb.getItems().addAll(s1,s2,s3,s4,s5);

        //数据转换
        cb.setConverter(new StringConverter<Student>() {
            //只显示名字
            @Override
            public String toString(Student object) {
                String value = object.getName();
                return value;
            }

            @Override
            public Student fromString(String string) {
                return null;
            }
        });


        ComboBox<String> cbb = new ComboBox<String>();
        cbb.getItems().addAll("str1","str2","str3");
        //运行编辑
        cbb.setEditable(true);

        AnchorPane.setLeftAnchor(cbb,60.0);
        an.getChildren().addAll(cb,cbb);
        an.setStyle("-fx-background-color: deepskyblue");
        Scene s= new Scene(an);
        primaryStage.setScene(s);
        primaryStage.setTitle("hashdog");
        primaryStage.setWidth(500);
        primaryStage.setHeight(300);
        //设置窗口不可拉伸
        primaryStage.setResizable(false);
        primaryStage.show();






    }

}


class Student{
    private  String name;
    private  int age;
    private  double score;

    public Student(String name, int age, double score) {
        this.name = name;
        this.age = age;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Student(){

    }

}
 