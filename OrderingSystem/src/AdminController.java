import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class AdminController {

    @FXML
    private TableView<Dish> dishesTableView; // 显示菜色的总体信息
    @FXML
    private TableColumn<Dish, Integer> idTableColumn; // 菜色ID
    @FXML
    private TableColumn<Dish, String> nameTableColumn; // 菜色名字
    @FXML
    private TableColumn<Dish, Double> priceTableColumn; // 菜色价格
    @FXML
    private TableColumn<Dish, Integer> surplusTableColumn; // 菜色剩余份数
    @FXML
    private TableColumn<Dish, String> typeTableColumn; // 菜色类型
    @FXML
    private TextField dishesNameTextField; // 输入菜名
    @FXML
    private Button checkDishesButton; // 查找菜色按钮
    @FXML
    private Button enrollDishesButton; // 进入添加菜色界面按钮
    @FXML
    private Button updateDishesButton; // 更新菜色信息按钮（留空）
    @FXML
    private Button deleteDishesButton; // 删除菜色按钮
    @FXML
    private Button exitButton; // 返回登录界面按钮
    @FXML
    private Slider controlSlider; // 控制表格移动的滑动条（若需要）

    // 初始化函数
    @FXML
    public void initialize() throws Exception {
        // 设置表格列的 cell value factory
        idTableColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        nameTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        priceTableColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());
        surplusTableColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSurplus()).asObject());
        typeTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType()));

        // 加载所有菜品信息到表格
        loadAllDishes();
    }

    private void loadAllDishes() throws Exception {
        // 清空表格
        dishesTableView.getItems().clear();
        // 查询所有菜品并添加到表格
        for (Dish dish : MainOrdering.getAllDishes()) {
            dishesTableView.getItems().add(dish);
        }
    }

    // 查找菜色按钮事件处理
    @FXML
    private void handleCheckDishesButton(ActionEvent event) throws Exception {
        String dishName = dishesNameTextField.getText().trim();
        if (MainOrdering.isDishExist(dishName)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("提示");
            alert.setHeaderText(null);
            alert.setContentText("菜品 \"" + dishName + "\" 存在！");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("提示");
            alert.setHeaderText(null);
            alert.setContentText("菜品 \"" + dishName + "\" 不存在！");
            alert.showAndWait();
        }
    }

    // 进入添加菜色界面按钮事件处理
    @FXML
    private void handleEnrollDishesButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addDishes.fxml"));
            Parent newScene = loader.load();
            Stage currentStage = (Stage) enrollDishesButton.getScene().getWindow();
            currentStage.setScene(new Scene(newScene));
            currentStage.setTitle("添加菜色");
            currentStage.show();
        } catch (IOException e) {
            MainOrdering.showAlert("错误", "加载添加菜色页面失败！", Alert.AlertType.ERROR);
        }
    }

    // 删除菜色按钮事件处理
    @FXML
    private void handleDeleteDishesButton(ActionEvent event) {
        Dish selectedDish = dishesTableView.getSelectionModel().getSelectedItem();
        if (selectedDish == null) {
            MainOrdering.showAlert("警告", "请先选择一个菜品进行删除！", Alert.AlertType.WARNING);
            return;
        }

        // 弹出确认对话框
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("确认删除");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("确认要删除菜品 \"" + selectedDish.getName() + "\" 吗？");
        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // 调用方法删除菜品
                MainOrdering.deleteDishById(selectedDish.getId());
                // 更新表格
                loadAllDishes();
                MainOrdering.showAlert("成功", "菜品已成功删除！", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                MainOrdering.showAlert("错误", "删除菜品失败: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    // 返回按钮事件处理
    @FXML
    private void handleExitButton(ActionEvent event) {
        // 弹出确认对话框
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("确认退出");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("您确定要退出登录吗？");

        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            // 用户确认退出，加载登录界面
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("test_log_in.fxml"));
                Parent loginRoot = loader.load();
                Stage currentStage = (Stage) exitButton.getScene().getWindow();
                currentStage.setScene(new Scene(loginRoot));
                currentStage.setTitle("登录界面");
                currentStage.show();
            } catch (IOException e) {
                MainOrdering.showAlert("错误", "加载登录页面失败！", Alert.AlertType.ERROR);
            }
        }
    }


}


