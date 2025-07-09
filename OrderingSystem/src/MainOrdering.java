import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.*;

public class MainOrdering extends Application {
    public static String className = "com.mysql.cj.jdbc.Driver";
    public static String url = "jdbc:mysql://localhost:3306/javaproject?serverTimezone=UTC";
    public static Scanner scanner = new Scanner(System.in);
    public static Connection connection;
    public static Statement statement;
    public static ResultSet resultSet;
    public static PreparedStatement preparedStatement;
    // 当前使用的账户信息
    public static String CurrentName = null;
    public static int CurrentID = 0;
    public static String CurrentType = null; // CurrentType = "User"/"Administrator";
    // 星期映射的哈希表
    public static Map<Integer, String> weekmap = new HashMap<>();
    public static int parentPageType = -1; // 0为用户页面，1为我的订单页面
    // 界面显示函数
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("test_log_in.fxml"));

        // 创建场景
        Scene scene = new Scene(loader.load());

        // 设置Stage（窗口）
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    // 终端显示函数（可以弃置）
    public static void show() {
        System.out.println(" ");
        System.out.println("*--------------------------*");
        System.out.println("1:注册个人信息   2:登录个人信息");
        System.out.println("3:查询个人信息   4:充值");
        System.out.println("5:更新个人信息   6:删除个人信息");
        System.out.println(" ");
        System.out.println("7:注册菜品信息   9:查询菜品信息");
        System.out.println("9:更新菜品信息   10:删除菜品信息");
        System.out.println(" ");
        System.out.println("11:订餐         12:查询订单信息");
        System.out.println("0:退出系统");
        System.out.println("*--------------------------*");
        System.out.println(" ");
    }

    public static void main(String[] args) {
        weekmap.put(1, "Monday");
        weekmap.put(2, "Tuesday");
        weekmap.put(3, "Wednesday");
        weekmap.put(4, "Thursday");
        weekmap.put(5, "Friday");
        /*
        try {
            Class.forName(className);
            connection = DriverManager.getConnection(url, "root", "SYSU");
            statement = connection.createStatement();
            while (true) {
                show();
                int index = scanner.nextInt();
                if (index == 0) {
                    break;
                }
                switch (index) {
                    case 1:
                        System.out.println("请输入：姓名 电话 密码 人员类别(0-User/1-Administrator)：");
                        EnrollInfo();
                        break;
                    case 2:
                        System.out.println("请输入：姓名 电话 密码 人员类别(0-User/1-Administrator)：");
                        LoginInfo();
                        break;
                    case 3:
                        CheckInfo();
                        break;
                    case 4:
                        Recharge();
                        break;
                    case 5:
                        UpdateInfo();
                        break;
                    case 6:
                        DeleteInfo();
                        break;
                    case 7:
                        EnrollDishes();
                        break;
                    case 8:
                        CheckDishes();
                        break;
                    case 9:
                        UpdateDishes();
                        break;
                    case 10:
                        DeleteDishes();
                        break;
                    case 11:
                        ChangeOrdering();
                        break;
                    case 12:
                        CheckOrdering();
                        break;
                    default:
                        System.out.println("Input Error!");
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error！ Cause: " + e.getMessage());
        }
         */
        try {
            Class.forName(className);
            connection = DriverManager.getConnection(url, "root", "SYSU");
            statement = connection.createStatement();
        }
        catch (Exception e) {
            System.out.println("Error！ Cause: " + e.getMessage());
        }
        launch(args);
    }
    // 弹窗显示函数
    public static void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    // 用户/管理员注册信息
    public static void EnrollInfo(String name, String phoneNumber, String password, int userKind) throws Exception {
        // 人员类别分类考虑
        if (userKind == 0) {
            /*
            preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM users");
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                // 记录目前数据表中的数据数量(ID = 10000 + 数据数量)
                ID += resultSet.getInt(1);
            }
             */
            preparedStatement = connection.prepareStatement("SELECT COALESCE(MAX(ID), 0) AS max_id FROM users");
            resultSet = preparedStatement.executeQuery();
            int max_id = 0;
            if (resultSet.next()) {
                max_id = resultSet.getInt("max_id");
            }
            int ID = max_id + 1;
            if (ID < 10000) {
                ID = 10001;
            }

            String insertSQL = "INSERT INTO users (ID, Name, PhoneNumber, Password, Balance) VALUES (?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(insertSQL);
            preparedStatement.setInt(1, ID);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, phoneNumber);
            preparedStatement.setString(4, password);
            preparedStatement.setDouble(5, 0.00);
            preparedStatement.executeUpdate();
            CurrentID = ID;
            CurrentName = name;
            CurrentType = "User";
        }
        else if (userKind == 1) {
            preparedStatement = connection.prepareStatement("SELECT COALESCE(MAX(ID), 0) AS max_id FROM administrators");
            resultSet = preparedStatement.executeQuery();
            int max_id = 0;
            if (resultSet.next()) {
                max_id = resultSet.getInt("max_id");
            }
            int ID = max_id + 1;
            if (ID < 10000) {
                ID = 10001;
            }

            String insertSQL = "INSERT INTO administrators (ID, Name, PhoneNumber, Password) VALUES (?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(insertSQL);
            preparedStatement.setInt(1, ID);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, phoneNumber);
            preparedStatement.setString(4, password);
            preparedStatement.executeUpdate();
            CurrentID = ID;
            CurrentName = name;
            CurrentType = "Administrator";
        }
        else {
            System.out.println("User Kind Error!");
            return;
        }
        showAlert("注册成功", "欢迎您" + name, Alert.AlertType.INFORMATION);
    }
    // 用户/管理员登录信息
    public static int LoginInfo(String name, String phoneNumber, String password, int userKind) throws Exception {
        // 人员类别分类考虑
        if (userKind == 0) { // 普通用户
            String sql = "SELECT * FROM users WHERE Name = ? AND PhoneNumber = ? AND Password = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, phoneNumber);
            preparedStatement.setString(3, password);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // 登录成功
                CurrentName = name;
                CurrentID = resultSet.getInt("ID");
                CurrentType = "User";
                showAlert("登录成功", "欢迎回来，" + name, Alert.AlertType.INFORMATION);
                return 0;
            } else {
                // 登录失败
                showAlert("登录失败", "用户名或密码错误，请重新尝试", Alert.AlertType.ERROR);
                return -1;
            }

        } else if (userKind == 1) { // 管理员
            String sql = "SELECT * FROM administrators WHERE Name = ? AND PhoneNumber = ? AND Password = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, phoneNumber);
            preparedStatement.setString(3, password);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // 登录成功
                CurrentName = name;
                CurrentID = resultSet.getInt("ID");
                CurrentType = "Administrator";
                showAlert("登录成功", "欢迎管理员 " + name, Alert.AlertType.INFORMATION);
                return 1;
            } else {
                // 登录失败
                showAlert("登录失败", "用户名或密码错误，请重新尝试", Alert.AlertType.ERROR);
                return -1;
            }

        } else {
            // 如果用户类型无效
            System.out.println("User Kind Error!");
            return -1;
        }
    }
    //余额查询函数
    public static double getBalance(int CurrentID, String CurrentType) throws Exception {
        // 查询信息要求：必须已经登录
        if (CurrentID == 0 || CurrentType == null) {
            showAlert("错误", "Invalid Account!", Alert.AlertType.ERROR);
            return -1; // 返回-1表示无效账户
        }

        double balance = -1; // 默认余额为-1，表示未找到余额

        // 人员类别分类考虑
        if (CurrentType.equals("User")) {
            String sql = "SELECT Balance FROM users WHERE ID = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, CurrentID);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                balance = resultSet.getDouble("Balance");

            } else {
                showAlert("错误", "查询失败！请重新尝试！", Alert.AlertType.ERROR);
            }
        } else if (CurrentType.equals("Administrator")) {
            String sql = "SELECT Balance FROM administrators WHERE ID = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, CurrentID);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                balance = resultSet.getDouble("Balance");
            } else {
                showAlert("错误", "查询失败！请重新尝试！", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("错误", "用户类别错误！", Alert.AlertType.ERROR);
            return -1; // 返回-1表示类型错误
        }

        return balance; // 返回余额
    }
    // 用户充值
    public static void Recharge(double input, int CurrentID, String CurrentType) throws Exception {
        // 充值要求：必须已经登录并且是用户
        if (CurrentID == 0 || CurrentType == null) {
            showAlert("错误", "Invalid Account!", Alert.AlertType.ERROR);
            return;
        }

        if (CurrentType.equals("Administrator")) {
            showAlert("错误", "你不是用户，无法充值！", Alert.AlertType.ERROR);
            return;
        }

        String sql = "SELECT * FROM users WHERE ID = ?";
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, CurrentID);
        resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            double new_balance = input + resultSet.getDouble("Balance");
            String updateSql = "UPDATE users SET Balance = ? WHERE ID = ?";
            preparedStatement = connection.prepareStatement(updateSql);
            preparedStatement.setDouble(1, new_balance);
            preparedStatement.setInt(2, CurrentID);
            preparedStatement.executeUpdate();
            showAlert("充值成功", "充值成功！当前余额为：" + new_balance + " 元", Alert.AlertType.INFORMATION);
        } else {
            showAlert("错误", "充值失败！请重新尝试！", Alert.AlertType.ERROR);
        }
    }
    public static void updateBalance(int CurrentID, double newBalance) throws Exception {
        // 更新余额要求：必须已登录
        if (CurrentID == 0) {
            showAlert("错误", "无效账户!", Alert.AlertType.ERROR);
            return;
        }

        String sql = "UPDATE users SET Balance = ? WHERE ID = ?";
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setDouble(1, newBalance);
        preparedStatement.setInt(2, CurrentID);

        // 执行更新操作
        int rowsAffected = preparedStatement.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("余额更新成功！");
        } else {
            showAlert("错误", "未找到账户或余额未更新！", Alert.AlertType.ERROR);
        }
    }


    // 用户/管理员更新信息
    public static void UpdateInfo() throws Exception {
        if (CurrentID == 0 || CurrentName == null || CurrentType == null) {
            System.out.println("Invalid Account!");
            return;
        }
        // 输入以及合法性判断
        System.out.println("输入新名字、新电话、新密码：");
        String new_name = scanner.next();
        String new_phoneNumber = scanner.next();
        String new_password = scanner.next();
        if (new_phoneNumber.length() != 11) {
            System.out.println("PhoneNumber Error!");
            return;
        }
        if (CurrentType.equals("User")) {
            String sql = "UPDATE users SET Name = ?, PhoneNumber = ?, Password = ? WHERE ID = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, new_name);
            preparedStatement.setString(2, new_phoneNumber);
            preparedStatement.setString(3, new_password);
            preparedStatement.setInt(4, CurrentID);
            int result = preparedStatement.executeUpdate();
            if (result != 0) {
                System.out.println("更新成功！");
                CurrentName = new_name;
            }
            else {
                System.out.println("更新失败！请重新尝试！");
            }
        }
        else if (CurrentType.equals("Administrator")) {
            String sql = "UPDATE administrators SET Name = ?, PhoneNumber = ?, Password = ? WHERE ID = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, new_name);
            preparedStatement.setString(2, new_phoneNumber);
            preparedStatement.setString(3, new_password);
            preparedStatement.setInt(4, CurrentID);
            int result = preparedStatement.executeUpdate();
            if (result != 0) {
                System.out.println("更新成功！");
                CurrentName = new_name;
            }
            else {
                System.out.println("更新失败！请重新尝试！");
            }
        }
        else {
            System.out.println("User Kind Error!");
            return;
        }
    }
    //id获取
    public static int getUserIdByUsername(String username, String currentType) throws Exception {
        int userId = -1; // 默认用户 ID 为 -1，表示未找到用户

        if (username == null || username.isEmpty()) {
            showAlert("错误", "用户名不能为空！", Alert.AlertType.ERROR);
            return userId; // 返回 -1 表示输入无效
        }

        String sql;

        // 根据用户类别选择查询的表
        if (currentType.equals("User")) {
            sql = "SELECT ID FROM users WHERE Name = ?";
        } else if (currentType.equals("Administrator")) {
            sql = "SELECT ID FROM administrators WHERE Name = ?";
        } else {
            showAlert("错误", "用户类别错误！", Alert.AlertType.ERROR);
            return userId;
        }

        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, username); // 设置查询参数
        resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            userId = resultSet.getInt("ID"); // 获取用户 ID
        } else {
            showAlert("错误", "用户不存在！", Alert.AlertType.ERROR);
        }

        return userId; // 返回用户 ID，未找到返回 -1
    }

    // 用户/管理员注销信息
    public static void DeleteInfo() throws Exception {
        if (CurrentID == 0 || CurrentName == null || CurrentType == null) {
            System.out.println("Invalid Account!");
            return;
        }

        if (CurrentType.equals("User")) {
            String sql = "DELETE FROM users WHERE ID = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, CurrentID);
            int result = preparedStatement.executeUpdate();
            if (result != 0) {
                System.out.println("查询成功！删除成功！");
                CurrentID = 0;
                CurrentName = null;
                CurrentType = null;
            }
            else {
                System.out.println("删除失败！请重新尝试");
            }
        }
        else if (CurrentType.equals("Administrator")) {
            String sql = "DELETE FROM administrators WHERE ID = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, CurrentID);
            int result = preparedStatement.executeUpdate();
            if (result != 0) {
                System.out.println("查询成功！删除成功！");
                CurrentID = 0;
                CurrentName = null;
                CurrentType = null;
            }
            else {
                System.out.println("删除失败！请重新尝试");
            }
        }
        else {
            System.out.println("User Kind Error!");
            return;
        }
    }
    // 更新菜品只能由管理者来执行：
    // 管理者注册菜品信息
    public static void EnrollDishes(String name, double price, int surplus, int type) throws Exception {
        if (CurrentName == null || CurrentID == 0 || CurrentType == null) {
            showAlert("错误", "无效的账户！", Alert.AlertType.ERROR);  // 弹窗显示错误信息
            return;
        }
        if (CurrentType.equals("User")) {
            showAlert("权限错误", "你不是管理者，无法更新菜品！", Alert.AlertType.ERROR);  // 弹窗显示权限错误
            return;
        }
        String StrType = "Staple";
        switch (type) {
            case 0:
                StrType = "Staple";
                break;
            case 1:
                StrType = "Cuisine";
                break;
            case 2:
                StrType = "Drink";
                break;
            default:
                showAlert("类型错误", "菜品类型错误！", Alert.AlertType.ERROR);  // 弹窗显示类型错误
                return;
        }

        // 检查菜品是否重复
        String sql = "SELECT * FROM dishes WHERE Name = ?";
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, name);
        resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            showAlert("错误", "该菜品已存在！", Alert.AlertType.ERROR);  // 弹窗显示菜品已存在
            return;
        }

        preparedStatement = connection.prepareStatement("SELECT COALESCE(MAX(ID), 0) AS max_id FROM dishes");
        resultSet = preparedStatement.executeQuery();
        int max_id = 0;
        if (resultSet.next()) {
            max_id = resultSet.getInt("max_id");
        }
        int ID = max_id + 1;
        if (ID < 100) {
            ID = 101;
        }

        // 插入菜品信息
        String insertSQL = "INSERT INTO dishes (ID, Name, Price, Surplus, Type) VALUES (?, ?, ?, ?, ?)";
        preparedStatement = connection.prepareStatement(insertSQL);
        preparedStatement.setInt(1, ID);
        preparedStatement.setString(2, name);
        preparedStatement.setDouble(3, price);
        preparedStatement.setInt(4, surplus);
        preparedStatement.setString(5, StrType);
        preparedStatement.executeUpdate();
        showAlert("成功", "菜品录入成功！", Alert.AlertType.INFORMATION);  // 弹窗显示录入成功
    }

    // 管理者查询菜品信息
    public static void CheckDishes() throws Exception {
        if (CurrentName == null || CurrentID == 0 || CurrentType == null) {
            System.out.println("Invalid Account!");
            return;
        }
        if (CurrentType.equals("User")) {
            System.out.println("你不是管理者，无法查询菜品！");
            return;
        }
        System.out.println("请输入：菜品名字：");
        String name = scanner.next();
        String sql = "SELECT * FROM dishes WHERE Name = ?";
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, name);
        resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            System.out.println("查询成功！");
            System.out.println("ID：" + resultSet.getInt("ID"));
            System.out.println("名字：" + resultSet.getString("Name"));
            System.out.println("价格：" + resultSet.getDouble("Price"));
            System.out.println("剩余数量：" + resultSet.getInt("Surplus"));
            System.out.println("种类：" + resultSet.getString("Type"));
        }
        else {
            System.out.println("查询失败！请重新尝试");
        }
    }
    // 管理者更新菜品信息
    public static void UpdateDishes() throws Exception {
        if (CurrentName == null || CurrentID == 0 || CurrentType == null) {
            System.out.println("Invalid Account!");
            return;
        }
        if (CurrentType.equals("User")) {
            System.out.println("你不是管理者，无法更新菜品！");
            return;
        }
        System.out.println("请输入：菜品名字：");
        String name = scanner.next();
        String sql = "SELECT * FROM dishes WHERE Name = ?";
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, name);
        resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            System.out.println("查询成功！开始更新！请输入：名字 价格 剩余数量：");
            String new_name = scanner.next();
            double new_price = scanner.nextDouble();
            int new_surplus = scanner.nextInt();

            String updateSql = "UPDATE dishes SET Name = ?, Price = ?, Surplus = ? WHERE Name = ?";
            preparedStatement = connection.prepareStatement(updateSql);
            preparedStatement.setString(1, new_name);
            preparedStatement.setDouble(2, new_price);
            preparedStatement.setInt(3, new_surplus);
            preparedStatement.setString(4, name);
            preparedStatement.executeUpdate();
        }
        else {
            System.out.println("查询失败！更新失败！请重新尝试");
        }
    }
    // 管理者删除菜品信息
    public static void DeleteDishes() throws Exception {
        if (CurrentName == null || CurrentID == 0 || CurrentType == null) {
            System.out.println("Invalid Account!");
            return;
        }
        if (CurrentType.equals("User")) {
            System.out.println("你不是管理者，无法删除菜品！");
            return;
        }
        System.out.println("请输入：菜品名字：");
        String name = scanner.next();
        String sql = "SELECT * FROM dishes WHERE Name = ?";
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, name);
        resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            System.out.println("查询成功！删除成功！");
            String deletesql = "DELETE FROM dishes WHERE Name = ?";
            preparedStatement = connection.prepareStatement(deletesql);
            preparedStatement.setString(1, name);
            preparedStatement.executeUpdate();
        }
        else {
            System.out.println("查询失败！删除失败！请重新尝试");
        }
    }
    // 订餐只能由用户来执行：
    // 用户修改订单（可能有订单或没有订单）
    public static void ChangeOrdering() throws Exception {
        if (CurrentName == null || CurrentID == 0 || CurrentType == null) {
            System.out.println("Invalid Account!");
            return;
        }
        if (CurrentType.equals("Administrator")) {
            System.out.println("你不是用户，无法订餐！");
            return;
        }

        System.out.println("请输入星期数：");
        int day = scanner.nextInt();
        String orderdate;
        if (weekmap.containsKey(day)) {
            orderdate = weekmap.get(day);
        } else {
            System.out.println("Error Date!");
            return;
        }
        // 查询某一用户、某一天是否已经有订单
        preparedStatement = connection.prepareStatement("SELECT * FROM ordering WHERE UserID = ? AND OrderDate = ?");
        preparedStatement.setInt(1, CurrentID);
        preparedStatement.setString(2, orderdate);
        resultSet = preparedStatement.executeQuery();
        // 已经有订单则将原有订单删除！
        if (resultSet.next()) {
            System.out.println("你已经有订单，将删除原有的订单：");
            int order_ID = resultSet.getInt("ID");
            DeleteOrdering(order_ID);
        }
        // 还没有订单则创建新的订单id
        else {
            // 第一次订单函数
            FirstOrdering(orderdate);
        }
    }
    // 没有订单时调用，下单
    public static void FirstOrdering(String orderdate) throws Exception {
        // 得到当前余额
        preparedStatement = connection.prepareStatement("SELECT Balance FROM users WHERE ID = ?");
        preparedStatement.setInt(1, CurrentID);
        resultSet = preparedStatement.executeQuery();
        double CurrentBalance = 0;
        if (resultSet.next()) {
            CurrentBalance = resultSet.getInt("Balance");
        }
        else {
            System.out.println("Account Balance Error!");
            return;
        }
        // 创建Ordering的ID
        preparedStatement = connection.prepareStatement("SELECT COALESCE(MAX(ID), 0) AS max_id FROM ordering");
        resultSet = preparedStatement.executeQuery();
        int max_id = 0;
        if (resultSet.next()) {
            max_id = resultSet.getInt("max_id");
        }
        int order_ID = max_id + 1;
        if (order_ID < 10000) {
            order_ID = 10001;
        }

        // 插入Ordering订单数据
        preparedStatement = connection.prepareStatement("INSERT INTO ordering (ID, UserID, OrderDate) VALUES (?, ?, ?)");
        preparedStatement.setInt(1, order_ID);
        preparedStatement.setInt(2, CurrentID);
        preparedStatement.setString(3, orderdate);
        preparedStatement.executeUpdate();
        // 输入菜品名字与预订数量
        int nums;
        String DishName;
        System.out.println("请输入菜品名字与数量：名字输入0表示结束：");
        boolean isOrder = false; // 是否有下单
        while (true) {
            DishName = scanner.next();
            if (DishName.equals("0")) {
                break;
            }
            nums = scanner.nextInt();
            // 从菜品数据库中寻找相应的数据
            preparedStatement = connection.prepareStatement("SELECT * FROM dishes WHERE Name = ?");
            preparedStatement.setString(1, DishName);
            resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                System.out.println("该菜品不存在！请重试！");
                continue;
            }
            else {
                // 数量不足，不下单
                if (resultSet.getInt("Surplus") < nums) {
                    System.out.println(DishName + "数量不足！无法下单！");
                    continue;
                }
                // 数量充足，可能可以减去预订数量
                else {
                    // 余额充足，菜品剩余数量减少，同时余额减少
                    double price = resultSet.getDouble("Price");
                    if (CurrentBalance >= (price * (double) nums)) {
                        CurrentBalance -= (price * (double) nums);
                        preparedStatement = connection.prepareStatement("UPDATE dishes SET Surplus = ? WHERE Name = ?");
                        preparedStatement.setInt(1, resultSet.getInt("Surplus") - nums);
                        preparedStatement.setString(2, DishName);
                        preparedStatement.executeUpdate();
                    }
                    else {
                        System.out.println("余额不足！无法预订" + DishName);
                        continue;
                    }
                }
            }
            int dish_ID = resultSet.getInt("ID");
            // order_ID 和 dish_ID 都有了，我们开始创建orderdishes数据对象
            // 获取orderdishes_id
            preparedStatement = connection.prepareStatement("SELECT COALESCE(MAX(ID), 0) AS max_id FROM orderdishes");
            resultSet = preparedStatement.executeQuery();
            max_id = 0;
            if (resultSet.next()) {
                max_id = resultSet.getInt("max_id");
            }
            int orderdishes_id = max_id + 1;
            if (orderdishes_id < 100000) {
                orderdishes_id = 100001;
            }
            // 插入orderdishes数据对象
            String insertSQL = "INSERT INTO orderdishes (ID, OrderID, DishID, Number) VALUES (?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(insertSQL);
            preparedStatement.setInt(1, orderdishes_id);
            preparedStatement.setInt(2, order_ID);
            preparedStatement.setInt(3, dish_ID);
            preparedStatement.setInt(4, nums);
            preparedStatement.executeUpdate();
            if (!isOrder) {
                // 明确有菜品下单
                isOrder = true;
            }
        }
        // 订餐完成，如果有订单则修改余额，否则删除之前的OrderID
        if (isOrder) {
            preparedStatement = connection.prepareStatement("UPDATE users SET Balance = ? WHERE ID = ?");
            preparedStatement.setDouble(1, CurrentBalance);
            preparedStatement.setInt(2, CurrentID);
            preparedStatement.executeUpdate();
        } else {
            preparedStatement = connection.prepareStatement("DELETE FROM ordering WHERE ID = ?");
            preparedStatement.setInt(1, order_ID);
            preparedStatement.executeUpdate();
        }
    }
    // 有订单时调用，删除
    public static void DeleteOrdering(int order_ID) throws Exception {
        // 退返余额
        double returnBalance = 0;
        String sql = "SELECT * FROM orderdishes WHERE OrderID = ?";
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, order_ID);
        resultSet = preparedStatement.executeQuery();
        // 在orderdishes中寻找有orderid的数据
        while (resultSet.next()) {
            int dish_ID = resultSet.getInt("DishID");
            int nums = resultSet.getInt("Number");
            // 以dishid在dishes中寻找
            PreparedStatement temp_pre = connection.prepareStatement("SELECT * FROM dishes WHERE ID = ?");
            temp_pre.setInt(1, dish_ID);
            ResultSet temp_res = temp_pre.executeQuery();

            if (!temp_res.next()) {
                System.out.println("Error! Dish does not exist!");
                continue;
            }
            // 退钱
            double price = temp_res.getDouble("Price");
            returnBalance += (price * (double) nums);
            // 退回菜品数量
            int surplus = temp_res.getInt("Surplus");
            surplus += nums;
            temp_pre = connection.prepareStatement("UPDATE dishes SET Surplus = ? WHERE ID = ?");
            temp_pre.setInt(1, surplus);
            temp_pre.setInt(2, dish_ID);
            temp_pre.executeUpdate();
        }
        // 从orderdishes中删除信息
        sql = "DELETE FROM orderdishes WHERE OrderID = ?";
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, order_ID);
        preparedStatement.executeUpdate();
        // 从ordering中删除信息
        sql = "DELETE FROM ordering WHERE ID = ?";
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, order_ID);
        preparedStatement.executeUpdate();
        // 退返余额
        preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE ID = ?");
        preparedStatement.setInt(1, CurrentID);
        resultSet = preparedStatement.executeQuery();
        if (!resultSet.next()) {
            System.out.println("Balance Error!");
            return;
        }
        preparedStatement = connection.prepareStatement("UPDATE users SET Balance = ? WHERE ID = ?");
        preparedStatement.setDouble(1, returnBalance + resultSet.getDouble("Balance"));
        preparedStatement.setInt(2, CurrentID);
        preparedStatement.executeUpdate();
    }
    // 用户查询五天的订单
    public static void CheckOrdering() throws Exception {
        if (CurrentName == null || CurrentID == 0 || CurrentType == null) {
            System.out.println("Invalid Account!");
            return;
        }
        if (CurrentType.equals("Administrator")) {
            System.out.println("你不是用户，无法订餐！");
            return;
        }
        // 遍历五天
        for (int i = 1; i <= 5; i++) {
            String day = weekmap.get(i);
            System.out.println(day + ": ");
            // 订单表中按个人ID查找orderID
            preparedStatement = connection.prepareStatement("SELECT * FROM ordering WHERE UserID = ? AND OrderDate = ?");
            preparedStatement.setInt(1, CurrentID);
            preparedStatement.setString(2, day);
            resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                System.out.println("无");
                continue;
            }

            int order_ID = resultSet.getInt("ID");
            // 在orderdishes中以orderID查找订单的详细信息
            preparedStatement = connection.prepareStatement("SELECT * FROM orderdishes WHERE OrderID = ?");
            preparedStatement.setInt(1, order_ID);
            resultSet = preparedStatement.executeQuery();
            // 遍历输出
            while (resultSet.next()) {
                int nums = resultSet.getInt("Number");
                int dish_ID = resultSet.getInt("DishID");
                // 获取菜品名字
                preparedStatement = connection.prepareStatement("SELECT * FROM dishes WHERE ID = ?");
                preparedStatement.setInt(1, dish_ID);
                ResultSet temp = preparedStatement.executeQuery();
                if (!temp.next()) {
                    System.out.println("Error! Dish does not exist!");
                    continue;
                }
                String Dishes_Name = temp.getString("Name");
                System.out.println(Dishes_Name + ":" + nums);
            }
        }
    }
    // 根据菜品名字返回单价
    public static double getDishPrice(String name) throws Exception {
        double price = 0.0;
        String sql = "SELECT Price FROM dishes WHERE Name = ?";
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, name);
        resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            price = resultSet.getDouble("Price");
        } else {
            System.out.println("菜品不存在！");
        }
        return price;
    }

    // 根据菜品名字返回种类
    public static String getDishType(String name) throws Exception {
        String type = null;
        String sql = "SELECT Type FROM dishes WHERE Name = ?";
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, name);
        resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            type = resultSet.getString("Type");
        } else {
            System.out.println("菜品不存在！");
        }
        return type;
    }

    // 根据菜品名字返回剩余份数
    public static int getDishSurplus(String name) throws Exception {
        int surplus = 0;
        String sql = "SELECT Surplus FROM dishes WHERE Name = ?";
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, name);
        resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            surplus = resultSet.getInt("Surplus");
        } else {
            System.out.println("菜品不存在！");
        }
        return surplus;
    }

    // 根据菜品名字和数量返回该菜品的总价格
    public static double getTotalPrice(String name, int quantity) throws Exception {
        double price = getDishPrice(name); // 获取菜品单价
        if (price == 0.0) {
            System.out.println("无法计算总价格，菜品不存在或没有价格信息！");
            return 0.0;
        }
        return price * quantity; // 计算总价格
    }
    public static int getDishId(String name) throws Exception {
        int dishId = -1; // 默认值为-1，表示菜品不存在
        String sql = "SELECT ID FROM dishes WHERE Name = ?";
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, name);
        resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            dishId = resultSet.getInt("ID");
        } else {
            System.out.println("菜品不存在！");
        }
        return dishId;
    }
    // 根据菜品名字检查菜品是否存在，存在返回true，否则返回false
    public static boolean isDishExist(String name) throws Exception {
        boolean exists = false; // 默认值为false，表示菜品不存在
        String sql = "SELECT COUNT(*) AS count FROM dishes WHERE Name = ?";
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, name);
        resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            int count = resultSet.getInt("count");
            exists = (count > 0); // 如果计数大于0，表示菜品存在
        }
        return exists; // 返回查询结果
    }
    public static void addOrder(int dishId, int quantity) throws Exception {

    }
    public static void setDishSurplus(int dishId, int newSurplus) throws Exception {
        // SQL 更新语句
        String sql = "UPDATE dishes SET Surplus = ? WHERE ID = ?";
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, newSurplus); // 设置新的剩余量
        preparedStatement.setInt(2, dishId);      // 设置菜品 ID

        int rowsUpdated = preparedStatement.executeUpdate();
        if (rowsUpdated > 0) {
            System.out.println("菜品剩余量已成功更新！");
        } else {
            System.out.println("无法更新菜品剩余量，请检查菜品 ID 或联系管理员！");
        }
    }
    public static List<Dish> getAllDishes() throws Exception {
        List<Dish> dishes = new ArrayList<>(); // 创建菜品列表
        String sql = "SELECT ID, Name, Price, Surplus, Type FROM dishes"; // 查询菜品的信息

        preparedStatement = connection.prepareStatement(sql);
        resultSet = preparedStatement.executeQuery();

        // 遍历结果集，创建 Dish 对象并添加到列表中
        while (resultSet.next()) {
            int id = resultSet.getInt("ID");
            String name = resultSet.getString("Name");
            double price = resultSet.getDouble("Price");
            int surplus = resultSet.getInt("Surplus");
            String type = resultSet.getString("Type");

            // 创建 Dish 对象并添加到列表
            dishes.add(new Dish(id, name, price, surplus, type));
        }

        return dishes; // 返回菜品列表
    }
    public static void deleteDishById(int dishId) throws Exception {
        // 检查菜品 ID 是否有效
        if (dishId <= 0) {
            showAlert("错误", "无效的菜品 ID！", Alert.AlertType.ERROR);
            return;
        }

        // SQL 删除语句
        String sql = "DELETE FROM dishes WHERE ID = ?";
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, dishId); // 设置菜品 ID

        int rowsDeleted = preparedStatement.executeUpdate();
        if (rowsDeleted > 0) {
            System.out.println("菜品已成功删除！");
        } else {
            System.out.println("未找到指定的菜品，请检查菜品 ID！");
        }
    }
    public static void addOrder(int userId, String orderDate, int dishId, int quantity) throws Exception {
        // SQL 插入订单记录
        String sql = "INSERT INTO orders (UserID, OrderDate, DishID, Number) VALUES (?, ?, ?, ?)";
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, userId); // 设置用户 ID
        preparedStatement.setString(2, orderDate); // 设置订单日期
        preparedStatement.setInt(3, dishId); // 设置菜品 ID
        preparedStatement.setInt(4, quantity); // 设置数量

        int rowsInserted = preparedStatement.executeUpdate();
        if (rowsInserted > 0) {
            // 弹出成功提示
            System.out.println("订单已成功添加！");
            showAlert("购买成功", "订单已添加！", Alert.AlertType.INFORMATION);
        } else {
            // 弹出失败提示
            System.out.println("无法添加订单，请重试。");
            showAlert("错误", "无法添加订单，请重试。", Alert.AlertType.ERROR);
        }
    }
    // 根据 DishID 获取菜名
    public static String getDishName(int dishId) {
        String name = "";
        try {
            String sql = "SELECT Name FROM dishes WHERE ID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, dishId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                name = resultSet.getString("Name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }
}

