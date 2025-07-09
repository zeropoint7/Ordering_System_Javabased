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

public class userController {
    @FXML
    public MenuButton dateMenuButton;
    @FXML
    private Label balanceLabel;

    @FXML
    private TextField dishNameTextField;

    @FXML
    private TableView<Dish> dishesTableView; // 假设你有一个Dish类来表示菜品

    @FXML
    private TableColumn<Dish, Integer> idTableColumn;

    @FXML
    private TableColumn<Dish, String> nameTableColumn;

    @FXML
    private TableColumn<Dish, Double> priceTableColumn;

    @FXML
    private TableColumn<Dish, Integer> surplusTableColumn;

    @FXML
    private TableColumn<Dish, String> typeTableColumn;

    @FXML
    private Button payButton;

    @FXML
    private Button searchButton;

    @FXML
    private Button addToOrderButton;

    @FXML
    private Button myOrderButton;

    @FXML
    private Button exitButton;

    @FXML
    private MenuButton numberMenuButton;

    // 初始化函数
    @FXML
    public void initialize() throws Exception {
        if (idTableColumn == null || nameTableColumn == null || priceTableColumn == null || surplusTableColumn == null || typeTableColumn == null) {
            System.out.println("某些列未初始化");
            return; // 终止初始化以避免 NullPointerException
        }

        // 向表格列设置 cell value factory
        idTableColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        nameTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        priceTableColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());
        surplusTableColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSurplus()).asObject());
        typeTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType()));

        // 初始化份数菜单按钮
        for (int i = 1; i <= 5; i++) {
            MenuItem item = new MenuItem(String.valueOf(i));
            int finalI = i;
            item.setOnAction(event -> numberMenuButton.setText(finalI + " 份")); // 设置当前数量选择
            numberMenuButton.getItems().add(item);
        }

        // 初始化日期菜单按钮
        String[] days = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        for (String day : days) {
            MenuItem item = new MenuItem(day);
            item.setOnAction(event -> dateMenuButton.setText(day)); // 设置当前日期选择
            dateMenuButton.getItems().add(item);
        }

        // 设置余额显示
        balanceLabel.setText("" + MainOrdering.getBalance(MainOrdering.CurrentID, MainOrdering.CurrentType));
    }

    // 充值按钮事件处理
    @FXML
    private void handlePayButtonClicked(ActionEvent actionEvent) {
        try {
            // 加载 balance.fxml 文件
            FXMLLoader loader = new FXMLLoader(getClass().getResource("balance.fxml"));
            Parent balanceRoot = loader.load();

            // 获取当前窗口的场景
            Scene currentScene = balanceLabel.getScene(); // 可以用任意一个FXML组件
            Stage currentStage = (Stage) currentScene.getWindow();

            // 设置新的场景并显示充值页面
            currentStage.setScene(new Scene(balanceRoot));
            currentStage.setTitle("充值页面"); // 可选，设置窗口标题
            currentStage.show();
            MainOrdering.parentPageType = 0;
        } catch (IOException e) {
            e.printStackTrace(); // 处理异常，例如文件未找到等
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText("无法加载充值页面");
            alert.setContentText("请检查 balance.fxml 文件是否存在！");
            alert.showAndWait();
        }
    }

    // 搜索按钮事件处理
    @FXML
    private void handleSearchButtonClicked(ActionEvent actionEvent) throws Exception {
        String dishName = dishNameTextField.getText().trim();

        // 清空之前的菜品列表
        dishesTableView.getItems().clear();

        if (MainOrdering.isDishExist(dishName)) {
            Dish dish = new Dish(MainOrdering.getDishId(dishName), dishName, MainOrdering.getDishPrice(dishName), MainOrdering.getDishSurplus(dishName), MainOrdering.getDishType(dishName));

            // 添加菜品到表格
            dishesTableView.getItems().add(dish);
        } else {
            // 提示菜品不存在
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("提示");
            alert.setHeaderText(null);
            alert.setContentText("菜品不存在！");
            alert.showAndWait();
        }
    }

    // 添加到订单按钮事件处理
    @FXML
    private void handleAddButtonClicked(ActionEvent actionEvent) throws Exception {
        Dish selectedDish = dishesTableView.getSelectionModel().getSelectedItem();

        if (selectedDish != null) {
            // 获取选择的份数和日期
            String selectedQuantityText = numberMenuButton.getText(); // 假设这是选择的份数
            String selectedDate = dateMenuButton.getText(); // 假设这是选择的日期

            // 去掉文本中的" 份"前缀（假设将该文本首尾可能有空格）
            selectedQuantityText = selectedQuantityText.replace(" 份", "").trim(); // 去掉 " 份" 并做修整

            // 检查日期是否已选择
            if (selectedDate.isEmpty() || selectedDate.equals("日期")) { // 假设未选择时的文本为"请选择日期"
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("警告");
                alert.setHeaderText(null);
                alert.setContentText("请先选择一个日期！");
                alert.showAndWait();
                return; // 如果未选择日期，直接返回
            }

            try {
                int selectedQuantity = Integer.parseInt(selectedQuantityText);

                double totalPrice = MainOrdering.getTotalPrice(selectedDish.getName(), selectedQuantity);
                double currentBalance = MainOrdering.getBalance(MainOrdering.CurrentID, MainOrdering.CurrentType);
                if (currentBalance >= totalPrice) {
                    double newBalance = currentBalance - totalPrice;
                    MainOrdering.updateBalance(MainOrdering.CurrentID, newBalance);

                    // 调用主类的添加订单方法（这里需要确保方法用的是"orders"表）
                    MainOrdering.addOrder(MainOrdering.CurrentID, selectedDate, selectedDish.getId(), selectedQuantity);

                    // 更新菜品剩余数量
                    selectedDish.setSurplus(selectedDish.getSurplus() - selectedQuantity);
                    dishesTableView.refresh(); // 刷新表格
                    // 设置余额显示
                    balanceLabel.setText("" + MainOrdering.getBalance(MainOrdering.CurrentID, MainOrdering.CurrentType));
                } else {
                    // 提示余额不足
                    Alert insufficientFundsAlert = new Alert(Alert.AlertType.WARNING);
                    insufficientFundsAlert.setTitle("余额不足");
                    insufficientFundsAlert.setHeaderText(null);
                    insufficientFundsAlert.setContentText("您的余额不足，无法完成此购买！");
                    insufficientFundsAlert.showAndWait();
                }
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("错误");
                alert.setHeaderText(null);
                alert.setContentText("请选择有效的数量！");
                alert.showAndWait();
            }
        } else {
            // 提示未选择菜品
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("警告");
            alert.setHeaderText(null);
            alert.setContentText("请先选择一个菜品！");
            alert.showAndWait();
        }
    }


    // 我的订单按钮事件处理
    @FXML
    private void handleOrderButton(ActionEvent actionEvent) {
        try {
            // 加载 myOrder.fxml 文件
            FXMLLoader loader = new FXMLLoader(getClass().getResource("myOrder.fxml"));
            Parent myOrderRoot = loader.load();

            // 获取当前窗口的场景
            Scene currentScene = balanceLabel.getScene(); // 可以用任意一个FXML组件
            Stage currentStage = (Stage) currentScene.getWindow();

            // 设置新的场景并显示
            currentStage.setScene(new Scene(myOrderRoot));
            currentStage.setTitle("我的订单"); // 可选，设置窗口标题
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace(); // 处理异常，例如文件未找到等
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText("无法加载订单页面");
            alert.setContentText("请检查 myOrder.fxml 文件是否存在！");
            alert.showAndWait();
        }
    }

    // 退出按钮事件处理
    @FXML
    private void handleExitButtonClicked(ActionEvent actionEvent) {
        // 提示是否退出
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认退出");
        alert.setHeaderText("你确定要退出吗？");
        // 显示提示框并等待用户确认
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // 加载 login.fxml 文件
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("test_log_in.fxml"));
                    Parent loginRoot = loader.load();

                    // 获取当前窗口的场景
                    Scene currentScene = balanceLabel.getScene(); // 可以用任意一个FXML组件
                    Stage currentStage = (Stage) currentScene.getWindow();

                    // 设置新的场景并显示登录界面
                    currentStage.setScene(new Scene(loginRoot));
                    currentStage.setTitle("登录界面"); // 可选，设置窗口标题
                    currentStage.show();
                } catch (IOException e) {
                    e.printStackTrace(); // 处理异常，例如文件未找到等
                    Alert alertError = new Alert(Alert.AlertType.ERROR);
                    alertError.setTitle("错误");
                    alertError.setHeaderText("无法加载登录页面");
                    alertError.setContentText("请检查 login.fxml 文件是否存在！");
                    alertError.showAndWait();
                }
            }
        });
    }
}