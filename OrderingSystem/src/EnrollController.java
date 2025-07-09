import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.control.ToggleGroup;

import java.io.IOException;

public class EnrollController {

    @FXML
    private TextField userNameTextField; // 用户名文本框

    @FXML
    private TextField phoneTextField; // 电话号码文本框

    @FXML
    private PasswordField passwordField; // 密码字段

    @FXML
    private PasswordField rePasswordField; // 确认密码字段

    @FXML
    private Button showPasswordButton; // 显示密码按钮

    @FXML
    private Button showRePasswordButton; // 显示确认密码按钮

    @FXML
    private Button registerButton; // 注册按钮

    @FXML
    private RadioButton userRadioButton; // 用户单选框

    @FXML
    private RadioButton administerRadioButton; // 管理员单选框

    private ToggleGroup roleToggleGroup; // 用户角色的 ToggleGroup

    @FXML
    private Button exitButton; // 返回按钮

    @FXML
    public void initialize() {
        // 创建 ToggleGroup 实例
        roleToggleGroup = new ToggleGroup();

        // 将 RadioButton 绑定到 ToggleGroup
        userRadioButton.setToggleGroup(roleToggleGroup);
        administerRadioButton.setToggleGroup(roleToggleGroup);
    }

    @FXML
    public void handleEnroll(javafx.event.ActionEvent actionEvent) {
        // 获取输入的内容
        String userName = userNameTextField.getText();
        String phoneNumber = phoneTextField.getText();
        String password = passwordField.getText();
        String rePassword = rePasswordField.getText();

        // 判断两次密码是否一致
        if (!password.equals(rePassword)) {
            MainOrdering.showAlert("错误", "密码不一致", AlertType.ERROR);
            return;
        }

        // 获取用户类型（0:普通用户, 1:管理员）
        int userKind = userRadioButton.isSelected() ? 0 : (administerRadioButton.isSelected() ? 1 : -1);

        if (userKind == -1) {
            MainOrdering.showAlert("错误", "请选择用户类型", AlertType.ERROR);
            return;
        }

        // 调用 Enrollment 方法
        try {
            MainOrdering.EnrollInfo(userName, phoneNumber, password, userKind);
            try {
                Stage stage = (Stage) registerButton.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("test_log_in.fxml"));
                Parent newScene = loader.load();

                Scene scene = new Scene(newScene);
                stage.setScene(scene);
                stage.setTitle("登录页面"); // 设置窗口标题

                stage.show(); // 显示新场景
            } catch (IOException e) {
                MainOrdering.showAlert("错误", "页面加载失败: " + e.getMessage(), AlertType.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MainOrdering.showAlert("错误", "注册失败，请稍后再试", AlertType.ERROR);
        }
    }

    @FXML
    public void handlePasswordVisibility() {
        if (passwordField.isVisible()) {
            // 隐藏密码框，显示明文密码框
            passwordField.setVisible(false);
            showPasswordButton.setText("显示密码");

            // 创建一个 TextField 来显示密码
            TextField visiblePasswordField = new TextField(passwordField.getText());
            visiblePasswordField.setLayoutX(passwordField.getLayoutX());
            visiblePasswordField.setLayoutY(passwordField.getLayoutY());
            visiblePasswordField.setPrefWidth(passwordField.getWidth());

            // 将可见密码框添加到 Pane 中
            ((Pane) passwordField.getParent()).getChildren().add(visiblePasswordField);

            // 保存可见密码框的引用到按钮，方便以后切换
            showPasswordButton.setUserData(visiblePasswordField);
        } else {
            // 隐藏明文密码框，恢复显示 PasswordField
            TextField visiblePasswordField = (TextField) showPasswordButton.getUserData();
            if (visiblePasswordField != null) {
                ((Pane) passwordField.getParent()).getChildren().remove(visiblePasswordField);
            }

            passwordField.setVisible(true);
            showPasswordButton.setText("隐藏密码");
        }
    }

    @FXML
    public void handleRePasswordVisibility() {
        if (rePasswordField.isVisible()) {
            // 隐藏确认密码框，显示明文确认密码框
            rePasswordField.setVisible(false);
            showRePasswordButton.setText("显示密码");

            // 创建一个 TextField 来显示确认密码
            TextField visibleRePasswordField = new TextField(rePasswordField.getText());
            visibleRePasswordField.setLayoutX(rePasswordField.getLayoutX());
            visibleRePasswordField.setLayoutY(rePasswordField.getLayoutY());
            visibleRePasswordField.setPrefWidth(rePasswordField.getWidth());

            // 将可见确认密码框添加到 Pane 中
            ((Pane) rePasswordField.getParent()).getChildren().add(visibleRePasswordField);

            // 保存可见确认密码框的引用到按钮，方便以后切换
            showRePasswordButton.setUserData(visibleRePasswordField);
        } else {
            // 隐藏明文确认密码框，恢复显示 PasswordField
            TextField visibleRePasswordField = (TextField) showRePasswordButton.getUserData();
            if (visibleRePasswordField != null) {
                ((Pane) rePasswordField.getParent()).getChildren().remove(visibleRePasswordField);
            }

            rePasswordField.setVisible(true);
            showRePasswordButton.setText("隐藏密码");
        }

    }
    @FXML
    public void handleExit() {
        try {
            // 加载 test_log_in.fxml 文件
            FXMLLoader loader = new FXMLLoader(getClass().getResource("test_log_in.fxml"));
            Parent newScene = loader.load();

            // 获取当前的 Stage
            Stage currentStage = (Stage) exitButton.getScene().getWindow();

            // 设置新的场景
            Scene scene = new Scene(newScene);
            currentStage.setScene(scene);
            currentStage.setTitle("登录界面"); // 设置窗口标题

            currentStage.show(); // 显示新的场景
        } catch (IOException e) {
            e.printStackTrace();
            MainOrdering.showAlert("错误", "页面加载失败: " + e.getMessage(), AlertType.ERROR);
        }
    }

}



