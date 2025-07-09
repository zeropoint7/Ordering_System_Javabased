public class Dish {
    private int id;
    private String name;
    private double price;
    private int surplus;
    private String type;

    // 构造函数
    public Dish(int id, String name, double price, int surplus, String type) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.surplus = surplus;
        this.type = type;
    }

    // Getter 和 Setter 方法
    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getSurplus() { return surplus; }

    public void setSurplus(int surplus) { // 设置剩余份数
        this.surplus = surplus;
    }

    public String getType() { return type; }
}

