import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class AddDishesController {

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField priceTextField;

    @FXML
    private TextField surplusTextField;

    @FXML
    private ComboBox<String> typeMenuButton;

    @FXML
    private Button checkButton;

    // 初始化ComboBox的选项
    @FXML
    public void initialize() {
        typeMenuButton.getItems().addAll("Staple", "Cuisine", "Drink");
    }

    // "确认"按钮点击事件
    @FXML
    private void handleCheckButtonClick() {
        try {
            String name = nameTextField.getText().trim();
            String priceText = priceTextField.getText().trim();
            String surplusText = surplusTextField.getText().trim();
            String typeText = typeMenuButton.getValue();

            // 输入验证
            if (name.isEmpty() || priceText.isEmpty() || surplusText.isEmpty() || typeText == null) {
                showErrorMessage("所有字段都必须填写！");
                return;
            }

            double price = Double.parseDouble(priceText);
            int surplus = Integer.parseInt(surplusText);
            int type = typeMenuButton.getSelectionModel().getSelectedIndex();  // 0, 1, 2 对应 Staple, Cuisine, Drink

            // 弹窗显示输入信息
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("确认添加菜品");
            alert.setHeaderText("菜品信息");
            alert.setContentText(String.format("菜名: %s\n价格: %.2f\n剩余份数: %d\n类型: %s",
                    name, price, surplus, typeText));
            alert.showAndWait();

            // 调用EnrollDishes方法
            MainOrdering.EnrollDishes(name, price, surplus, type);
        } catch (NumberFormatException e) {
            showErrorMessage("价格和剩余份数必须是有效的数字！");
        } catch (Exception e) {
            showErrorMessage("发生了错误: " + e.getMessage());
        }
    }
    @FXML
    private void handleReturnButtonClick() {
        Stage stage = (Stage) checkButton.getScene().getWindow();
        try {
            // 加载administrator.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("administrator.fxml"));
            Parent root = loader.load();

            // 设置新场景
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("管理员页面"); // 设置标题
            stage.show(); // 显示新场景
        } catch (IOException e) {
            showErrorMessage("页面加载失败: " + e.getMessage());
        }
    }
    private void showErrorMessage(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("错误");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
