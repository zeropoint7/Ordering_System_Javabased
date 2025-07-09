import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {  // 修改类名为 LoginController

    @FXML
    private RadioButton userRadioButton;  // 用户身份单选框
    @FXML
    private RadioButton administerRadioButton;  // 管理员身份单选框
    @FXML
    private TextField userNameTextField;  // 用户名文本框
    @FXML
    private PasswordField passwordField;  // 密码文本框
    @FXML
    private TextField visiblePasswordField;  // 可见密码文本框
    @FXML
    private Button passwordShowButton;  // 显示/隐藏密码按钮
    @FXML
    private Button loginButton;  // 登录按钮 (原注册按钮)
    @FXML
    private Label registerLabel;  // 跳转到注册界面的标签
    @FXML
    private Label exitLabel;  // 退出系统标签
    @FXML
    private TextField phoneTextField;  // 电话号码文本框

    private ToggleGroup roleToggleGroup;

    // 初始化方法，用于设置 ToggleGroup
    public void initialize() {
        // 创建一个 ToggleGroup
        roleToggleGroup = new ToggleGroup();

        // 将两个 RadioButton 添加到 ToggleGroup 中
        userRadioButton.setToggleGroup(roleToggleGroup);
        administerRadioButton.setToggleGroup(roleToggleGroup);

        // 可选：设置默认选中的 RadioButton
        userRadioButton.setSelected(true);
    }

    @FXML
    private void handleRegisterClick() {
        try {
            // 加载注册界面的FXML文件
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Enroll.fxml"));
            Scene registerScene = new Scene(loader.load());

            // 获取当前窗口
            Stage stage = (Stage) registerLabel.getScene().getWindow();
            stage.setScene(registerScene);
            stage.setTitle("注册界面");
            stage.show();
        } catch (IOException e) {
            MainOrdering.showAlert("错误", "注册界面加载失败: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleExitClick() {
        // 弹出确认对话框
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "确定要退出系统吗？");
        alert.setTitle("退出确认");
        alert.setHeaderText(null);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // 关闭应用程序
                Platform.exit();
            }
        });
    }

    // 显示/隐藏密码功能
    @FXML
    private void handlePasswordVisibility(ActionEvent event) {
        String password = passwordField.getText();
        if (visiblePasswordField.isVisible()) {
            // 如果 visiblePasswordField 是可见的，则隐藏它，并显示 passwordField
            visiblePasswordField.setVisible(false);
            passwordField.setVisible(true);
            passwordShowButton.setText("显示密码");
        } else {
            // 如果 visiblePasswordField 是不可见的，则显示它，并隐藏 passwordField
            visiblePasswordField.setVisible(true);
            passwordField.setVisible(false);
            passwordShowButton.setText("隐藏密码");
            visiblePasswordField.setText(password); // 将密码内容同步到 visiblePasswordField
        }
    }

    // 登录按钮点击事件 (原注册按钮点击事件)
    @FXML
    private void handleLogin(ActionEvent event) {
        try {
            // 获取输入的用户名、密码和电话号码
            String username = userNameTextField.getText();
            String password = passwordField.isVisible() ? passwordField.getText() : visiblePasswordField.getText();
            String phoneNumber = phoneTextField.getText();  // 获取电话号码

            // 校验输入
            if (username.isEmpty() || password.isEmpty() || phoneNumber.isEmpty()) {
                MainOrdering.showAlert("错误", "用户名、密码或电话号码不能为空", Alert.AlertType.ERROR);
                return;
            }

            // 判断选择的身份
            int userKind = userRadioButton.isSelected() ? 0 : 1; // 0 为用户，1 为管理员

            // 调用 LoginInfo 方法进行登录验证
            int judge = MainOrdering.LoginInfo(username, phoneNumber, password, userKind);
            if (judge == -1) {  // 登录失败
                return; // 直接返回
            }

            // 登录成功
            // 设置当前用户信息
            MainOrdering.CurrentName = username;
            MainOrdering.CurrentType = userKind == 0 ? "User" : "Administrator"; // 设置类型
            MainOrdering.CurrentID = MainOrdering.getUserIdByUsername(MainOrdering.CurrentName, MainOrdering.CurrentType); // 假设 judge 返回用户 ID


            try {
                String fxmlFile = userKind == 0 ? "user.fxml" : "administrator.fxml"; // 根据身份选择加载的 FXML 文件
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
                Parent newScene = loader.load();

                // 获取当前窗口
                Stage stage = (Stage) loginButton.getScene().getWindow();

                // 切换场景
                Scene scene = new Scene(newScene);
                stage.setScene(scene);
                stage.setTitle(userKind == 0 ? "用户页面" : "管理员页面"); // 设置窗口标题

                stage.show(); // 显示新场景
            } catch (IOException e) {
                e.printStackTrace();
                MainOrdering.showAlert("错误", "页面加载失败: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            MainOrdering.showAlert("错误", "登录失败：" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}



