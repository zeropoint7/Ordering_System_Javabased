public class Order {
    private int id;
    private String name;   // 菜名
    private double price;   // 单价
    private String type;    // 类型
    private int number;     // 数量
    private double totalPrice; // 总价
    private String date; //日期

    // 构造函数
    public Order(int id, String name, double price, String type, int number,String date) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.type = type;
        this.number = number;
        this.totalPrice = price * number; // 计算总价
        this.date = date;
    }

    // 添加 getter 方法
    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getType() { return type; }
    public int getNumber() { return number; }
    public double getTotalPrice() { return totalPrice; }
    public String getDate() {return date;}
}

