import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import java.io.IOException;

public class BalanceController {

    @FXML
    private Label balanceLabel; // 显示当前余额的标签

    @FXML
    private TextField payTextField; // 输入充值金额的文本框

    @FXML
    private Button payButton; // 充值按钮

    @FXML
    private Button exitButton; // 返回按钮

    @FXML
    public void initialize() {
        try {
            // 加载用户余额
            double balance = MainOrdering.getBalance(MainOrdering.CurrentID, MainOrdering.CurrentType);
            balanceLabel.setText(balance + " 元");

        } catch (Exception e) {
            MainOrdering.showAlert("错误", "加载余额失败: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // 处理充值按钮点击事件
    @FXML
    private void handlePayButtonClicked(ActionEvent event) {
        String inputText = payTextField.getText();

        try {
            double input = Double.parseDouble(inputText);
            if (input <= 0) {
                MainOrdering.showAlert("错误", "请输入有效的充值金额！", Alert.AlertType.ERROR);
                return;
            }
            MainOrdering.Recharge(input, MainOrdering.CurrentID, MainOrdering.CurrentType);
            double newBalance = MainOrdering.getBalance(MainOrdering.CurrentID, MainOrdering.CurrentType); // 更新余额
            balanceLabel.setText(newBalance + " 元");
            payTextField.clear(); // 清空输入框
        } catch (NumberFormatException e) {
            MainOrdering.showAlert("错误", "充值金额必须为数字！", Alert.AlertType.ERROR);
        } catch (Exception e) {
            MainOrdering.showAlert("错误", "充值失败: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // 处理返回按钮点击事件
    @FXML
    private void handleExitButtonCLicked(ActionEvent event) {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        try {
            FXMLLoader loader;
            Parent newScene;

            // 根据 parentPageType 决定跳转目标
            if (MainOrdering.parentPageType == 0) {
                // 跳转到用户页面
                loader = new FXMLLoader(getClass().getResource("user.fxml"));
                newScene = loader.load();
                Scene scene = new Scene(newScene);
                stage.setScene(scene);
                stage.setTitle("用户页面");
            } else {
                // 跳转到我的订单页面
                loader = new FXMLLoader(getClass().getResource("myOrder.fxml"));
                newScene = loader.load();
                Scene scene = new Scene(newScene);
                stage.setScene(scene);
                stage.setTitle("订单页面");
            }

            stage.show(); // 显示新场景
        } catch (IOException e) {
            MainOrdering.showAlert("错误", "页面加载失败: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}


