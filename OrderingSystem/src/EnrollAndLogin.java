import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.FileInputStream;
import java.util.Objects;
import java.lang.Exception;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class EnrollAndLogin {
    /* 馒头 米饭  炒饭
    豆浆 0.2 200 2
    叉烧 5 100 1
     */
    /*
    麦理浩 19700000001 123$456$789$
    卫奕信 19823451234 asdfg
    常凯申 19800001111 QKSKMT
    斯大林 19889890001 cccpussr 1
    丘吉尔 19710102020 UK 1
    罗斯福 19191945194 1945usa 1
    戴高乐 17819441945 VivaLaFrench 0
    隆美尔 12345652343 DestchLand 0
    朴卡卡 12392319233 ZhongCheng 0
    特朗普 12312331323 MAGA 0
    拿破仑 13312331233 1233313 0
    俾斯麦 19424244244 1919191919 0
    全斗焕 19832323441 ZhongCheng 0
     */
    private static TextField nameField;
    private static TextField phoneField;
    private static PasswordField passwordField;
    private static ToggleGroup toggleGroup;
    private static Alert alert;

    public static void Login(Stage stage) {
        /*Label titleLabel = new Label("登录系统");
        titleLabel.setFont(new javafx.scene.text.Font("Arial", 24));

        nameField = new TextField();
        nameField.setPromptText("姓名");
        nameField.setMaxWidth(200);
        nameField.setMinWidth(100);

        phoneField = new TextField();
        phoneField.setPromptText("电话");
        phoneField.setMaxWidth(200);
        phoneField.setMinWidth(100);

        passwordField = new PasswordField();
        passwordField.setPromptText("密码");
        passwordField.setMaxWidth(200);
        passwordField.setMinWidth(100);

        toggleGroup = new ToggleGroup();
        RadioButton userRadioButton = new RadioButton("用户");
        RadioButton adminRadioButton = new RadioButton("管理员");
        userRadioButton.setToggleGroup(toggleGroup);
        adminRadioButton.setToggleGroup(toggleGroup);
        HBox type = new HBox(10, userRadioButton, adminRadioButton);
        type.setAlignment(Pos.CENTER);

        Button EnrollButton = new Button("注册");
        Button LoginButton = new Button("登录");

        HBox buttonBox = new HBox(10, EnrollButton, LoginButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox vbox = new VBox(20, titleLabel, nameField, phoneField, passwordField, type, buttonBox);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));

        Scene scene = new Scene(vbox, 1000, 750);
        stage.setTitle("登录系统");
        stage.setScene(scene);
        stage.show();

        EnrollButton.setOnAction(actionEvent -> {
            output(true);
        });
        LoginButton.setOnAction(actionEvent -> {
            output(false);
        });*/
    }

    public static void output(boolean isEnroll) {
        RadioButton radioButton = (RadioButton) toggleGroup.getSelectedToggle();
        String selectedType = radioButton != null ? radioButton.getText() : null;

        String name = nameField.getText();
        String phoneNumber = phoneField.getText();
        String password = passwordField.getText();

        if (Objects.isNull(name) || Objects.isNull(phoneNumber) || Objects.isNull(password) || Objects.isNull(selectedType)) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("警告！");
            alert.setHeaderText("输入错误！");
            alert.setContentText("请输入完整信息！");
            alert.showAndWait();
        }
        else if (phoneNumber.length() != 11) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("警告！");
            alert.setHeaderText("输入错误！");
            alert.setContentText("请输入正确形式的电话号码！");
            alert.showAndWait();
        }
        else {
            int userKind = selectedType.equals("用户") ? 0 : 1;
            try {
                if (isEnroll) {
                    MainOrdering.EnrollInfo(name, phoneNumber, password, userKind);
                }
                else {
                    MainOrdering.LoginInfo(name, phoneNumber, password, userKind);
                }
            }
            catch (Exception e) {
                System.out.println("Error！ Cause: " + e.getMessage());
            }
        }
    }
}
