import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

// ================= DB CONNECTION =================
class DBConnection {

    static Connection getConnection() {

        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");

            return DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:1521:xe",
                    "system",
                    "av"
            );

        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }
    }
}

// ================= ENCAPSULATION =================
class FoodItem {

    private int id;
    private String name;
    private double price;

    public FoodItem(int id, String name, double price) {

        this.id = id;
        this.name = name;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
}

// ================= INHERITANCE =================
class User {

    protected int user_id;
    protected String name;
}

class Customer extends User {
}

class Admin extends User {
}

// ================= POLYMORPHISM =================
interface PaymentMethod {

    void pay(double amount);
}

class CashPayment implements PaymentMethod {

    public void pay(double amount) {

        System.out.println("Paid via Cash: " + amount);
    }
}

class UPIPayment implements PaymentMethod {

    public void pay(double amount) {

        System.out.println("Paid via UPI: " + amount);
    }
}

// ================= LOGIN UI =================
class LoginUI extends JFrame {

    JTextField username;
    JPasswordField password;

    public LoginUI() {

        setTitle("Smart Canteen Login");
        setSize(500, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 247, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);

        JLabel title = new JLabel("SMART CANTEEN SYSTEM");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));

        username = new JTextField(16);
        password = new JPasswordField(16);

        JButton login = new JButton("Login");
        JButton signup = new JButton("Signup");

        login.setBackground(new Color(52, 152, 219));
        signup.setBackground(new Color(46, 204, 113));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        panel.add(title, gbc);

        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        panel.add(username, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        panel.add(password, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(login, gbc);

        gbc.gridx = 1;
        panel.add(signup, gbc);

        add(panel);

        login.addActionListener(e -> loginUser());
        signup.addActionListener(e -> signupUser());

        setVisible(true);
    }

    void loginUser() {

        try {

            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(
                    "SELECT user_id, role FROM user2 WHERE username=? AND password=?"
            );

            ps.setString(1, username.getText());
            ps.setString(2, String.valueOf(password.getPassword()));

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                int userId = rs.getInt("user_id");
                String role = rs.getString("role");

                dispose();

                new FoodDeliveryUI(role, userId);

            } else {

                JOptionPane.showMessageDialog(this,
                        "Invalid Username or Password");
            }

        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }

    void signupUser() {

        try {

            String uname = username.getText();
            String pass = String.valueOf(password.getPassword());

            Connection con = DBConnection.getConnection();

            PreparedStatement check = con.prepareStatement(
                    "SELECT * FROM user2 WHERE username=?"
            );

            check.setString(1, uname);

            ResultSet rs = check.executeQuery();

            if (rs.next()) {

                JOptionPane.showMessageDialog(this,
                        "Username already exists");

                return;
            }

            int id = (int)(System.currentTimeMillis() % 100000);

            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO user2(user_id, username, password, role) VALUES (?, ?, ?, ?)"
            );

            ps.setInt(1, id);
            ps.setString(2, uname);
            ps.setString(3, pass);
            ps.setString(4, "USER");

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Signup Successful! Please Login");

        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }
}

// ================= MAIN UI =================
public class FoodDeliveryUI extends JFrame {

    JTextField idField, nameField;

    String role;

    int loggedInUserId;

    int currentOrderId = 0;
    double currentAmount = 0;

    JTabbedPane tabs;

    public FoodDeliveryUI(String role, int userId) {

        this.role = role;
        this.loggedInUserId = userId;

        setTitle("Smart Canteen Ordering System");

        setExtendedState(JFrame.MAXIMIZED_BOTH);

        tabs = new JTabbedPane();

        loadTabs();

        JButton logout = new JButton("Logout");

        logout.addActionListener(e -> {

            dispose();
            new LoginUI();
        });

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        top.add(logout);

        add(top, BorderLayout.NORTH);

        add(tabs);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setVisible(true);
    }

    void loadTabs() {

        tabs.removeAll();

        tabs.add("Home", createHomePanel());

        // USER tabs
        if(role.equals("USER")) {

            tabs.add("Orders", createOrderPanel());
            tabs.add("Payment", createPaymentPanel());
     
        }

        // ADMIN tabs
        if(role.equals("ADMIN")) {

            tabs.add("Customer", createCustomerPanel());
            tabs.add("Food Menu", createFoodPanel());
            tabs.add("Delivery Partner", createDeliveryPanel());
        }
    }

    JButton btn(String text) {

        JButton b = new JButton(text);

        b.setBackground(new Color(52,152,219));

        return b;
    }

    JPanel createHomePanel() {

        JPanel panel = new JPanel(new GridBagLayout());

        JLabel title = new JLabel("SMART CANTEEN MANAGEMENT SYSTEM");

        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(title);

        return panel;
    }

    JPanel createCustomerPanel() {

        JPanel outer = new JPanel(new GridBagLayout());

        JPanel panel = new JPanel(new GridBagLayout());

        panel.setPreferredSize(new Dimension(400, 220));

        panel.setBorder(BorderFactory.createTitledBorder(
                "Customer Details"
        ));

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.fill = GridBagConstraints.HORIZONTAL;

        idField = new JTextField(15);

        idField.setEditable(false);

        nameField = new JTextField(15);

        JButton add = btn("Add Customer");

        // Row 1
        gbc.gridx = 0;
        gbc.gridy = 0;

        panel.add(new JLabel("Customer ID:"), gbc);

        gbc.gridx = 1;

        panel.add(idField, gbc);

        // Row 2
        gbc.gridx = 0;
        gbc.gridy = 1;

        panel.add(new JLabel("Customer Name:"), gbc);

        gbc.gridx = 1;

        panel.add(nameField, gbc);

        // Row 3
        gbc.gridx = 0;
        gbc.gridy = 2;

        gbc.gridwidth = 2;

        panel.add(add, gbc);

        add.addActionListener(e -> addCustomer());

        outer.add(panel);

        return outer;
    }

    void addCustomer() {

        try {

            Connection con = DBConnection.getConnection();

            int id = (int)(System.currentTimeMillis() % 100000);

            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO user2(user_id, username, password, role) VALUES (?, ?, ?, ?)"
            );

            ps.setInt(1, id);
            ps.setString(2, nameField.getText());
            ps.setString(3, "123");
            ps.setString(4, "USER");

            ps.executeUpdate();

            idField.setText(String.valueOf(id));

            JOptionPane.showMessageDialog(this,
                    "Customer Added Successfully");

        } catch(Exception e) {

            e.printStackTrace();
        }
    }

    JPanel createFoodPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID","Name","Price"},0);

        JTable table = new JTable(model);

        JButton load = btn("Load Menu");
        JButton add = btn("Add Food");

        JPanel top = new JPanel();

        top.add(load);
        top.add(add);

        load.addActionListener(e -> {

            try {

                Connection con = DBConnection.getConnection();

                ResultSet rs = con.createStatement()
                        .executeQuery("SELECT item_id, name, price FROM Menu_Item");

                model.setRowCount(0);

                while(rs.next()) {

                    model.addRow(new Object[]{

                            rs.getInt("item_id"),
                            rs.getString("name"),
                            rs.getDouble("price")
                    });
                }

            } catch(Exception ex) {

                ex.printStackTrace();
            }
        });

        add.addActionListener(e -> {

            try {

                String name = JOptionPane.showInputDialog("Food Name:");

                double price = Double.parseDouble(
                        JOptionPane.showInputDialog("Price:")
                );

                int id = (int)(System.currentTimeMillis() % 100000);

                Connection con = DBConnection.getConnection();

                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO Menu_Item(item_id, category_id, name, price) VALUES (?, ?, ?, ?)"
                );

                ps.setInt(1, id);
                ps.setInt(2, 1);
                ps.setString(3, name);
                ps.setDouble(4, price);

                ps.executeUpdate();

                JOptionPane.showMessageDialog(this,
                        "Food Added Successfully");

            } catch(Exception ex) {

                ex.printStackTrace();
            }
        });

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    JPanel createDeliveryPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID","Name","Phone"},0);

        JTable table = new JTable(model);

        JButton load = btn("Load Partners");

        JPanel top = new JPanel();

        top.add(load);

        load.addActionListener(e -> {

            try {

                Connection con = DBConnection.getConnection();

                ResultSet rs = con.createStatement()
                        .executeQuery("SELECT * FROM Delivery");

                model.setRowCount(0);

                while(rs.next()) {

                    model.addRow(new Object[]{

                            rs.getInt(1),
                            rs.getString(2),
                            rs.getString(3)
                    });
                }

            } catch(Exception ex) {

                ex.printStackTrace();
            }
        });

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    JPanel createOrderPanel() {

        JPanel main = new JPanel(new BorderLayout());

        DefaultTableModel menuModel = new DefaultTableModel(
                new String[]{"ID","Name","Price"},0);

        JTable menuTable = new JTable(menuModel);

        JButton loadMenu = btn("Load Menu");

        loadMenu.addActionListener(e -> {

            try {

                Connection con = DBConnection.getConnection();

                ResultSet rs = con.createStatement()
                        .executeQuery("SELECT item_id, name, price FROM Menu_Item");

                menuModel.setRowCount(0);

                while(rs.next()) {

                    menuModel.addRow(new Object[]{

                            rs.getInt("item_id"),
                            rs.getString("name"),
                            rs.getDouble("price")
                    });
                }

            } catch(Exception ex) {

                ex.printStackTrace();
            }
        });

        JPanel left = new JPanel(new BorderLayout());

        left.add(loadMenu, BorderLayout.NORTH);
        left.add(new JScrollPane(menuTable), BorderLayout.CENTER);

        DefaultTableModel cartModel = new DefaultTableModel(
                new String[]{"Item","Price","Qty","Total"},0);

        JTable cartTable = new JTable(cartModel);

        JTextField qty = new JTextField();

        JLabel totalLabel = new JLabel("Total: 0");

        JButton addCart = btn("Add To Cart");
        JButton placeOrder = btn("Place Order");

        addCart.addActionListener(e -> {

            int row = menuTable.getSelectedRow();

            if(row == -1) {

                JOptionPane.showMessageDialog(this,
                        "Select Item");

                return;
            }

            int id = Integer.parseInt(
                    menuModel.getValueAt(row,0).toString());

            String name = menuModel.getValueAt(row,1).toString();

            double price = Double.parseDouble(
                    menuModel.getValueAt(row,2).toString());

            int q = Integer.parseInt(qty.getText());

            FoodItem item = new FoodItem(id,name,price);

            double total = item.getPrice() * q;

            cartModel.addRow(new Object[]{
                    name, price, q, total
            });

            double grand = 0;

            for(int i=0;i<cartModel.getRowCount();i++) {

                grand += Double.parseDouble(
                        cartModel.getValueAt(i,3).toString());
            }

            totalLabel.setText("Total: " + grand);
        });

        placeOrder.addActionListener(e -> {

            try {

                if(cartModel.getRowCount() == 0) {

                    JOptionPane.showMessageDialog(this,
                            "Cart is Empty");

                    return;
                }

                Connection con = DBConnection.getConnection();

                int orderId = (int)(System.currentTimeMillis() % 100000);

                double total = Double.parseDouble(
                        totalLabel.getText().replace("Total: ",""));

                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO Orders(order_id, customer_id, restaurant_id, delivery_id) VALUES (?, ?, ?, ?)"
                );

                ps.setInt(1, orderId);

                ps.setInt(2, loggedInUserId);

                ps.setInt(3, 1);

                ps.setInt(4, 1);

                int x = ps.executeUpdate();

                if(x > 0) {

                    currentOrderId = orderId;

                    currentAmount = total;

                    JOptionPane.showMessageDialog(this,
                            "Order Placed Successfully");

                    tabs.setComponentAt(2, createPaymentPanel());

                    tabs.setSelectedIndex(2);
                }

            } catch(Exception ex) {

                ex.printStackTrace();

                JOptionPane.showMessageDialog(this,
                        ex.getMessage());
            }
        });

        JPanel right = new JPanel(new BorderLayout());

        JPanel top = new JPanel(new GridLayout(2,2));

        top.add(new JLabel("Qty"));
        top.add(qty);
        top.add(addCart);

        right.add(top, BorderLayout.NORTH);
        right.add(new JScrollPane(cartTable), BorderLayout.CENTER);
        right.add(totalLabel, BorderLayout.SOUTH);

        main.add(left, BorderLayout.WEST);
        main.add(right, BorderLayout.CENTER);
        main.add(placeOrder, BorderLayout.SOUTH);

        return main;
    }

    JPanel createPaymentPanel() {

        JPanel outer = new JPanel(new GridBagLayout());

        JPanel panel = new JPanel(new GridBagLayout());

        panel.setPreferredSize(new Dimension(400, 250));

        panel.setBackground(Color.WHITE);

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Payment Details"
        ));

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField orderId = new JTextField(
                String.valueOf(currentOrderId), 15);

        JTextField amount = new JTextField(
                String.valueOf(currentAmount), 15);

        orderId.setEditable(false);

        amount.setEditable(false);

        JComboBox<String> method = new JComboBox<>(
                new String[]{"Cash", "UPI"});

        JButton pay = btn("Pay Now");

        // ===== ROW 1 =====
        gbc.gridx = 0;
        gbc.gridy = 0;

        panel.add(new JLabel("Order ID:"), gbc);

        gbc.gridx = 1;

        panel.add(orderId, gbc);

        // ===== ROW 2 =====
        gbc.gridx = 0;
        gbc.gridy = 1;

        panel.add(new JLabel("Amount:"), gbc);

        gbc.gridx = 1;

        panel.add(amount, gbc);

        // ===== ROW 3 =====
        gbc.gridx = 0;
        gbc.gridy = 2;

        panel.add(new JLabel("Payment Method:"), gbc);

        gbc.gridx = 1;

        panel.add(method, gbc);

        // ===== ROW 4 =====
        gbc.gridx = 0;
        gbc.gridy = 3;

        gbc.gridwidth = 2;

        panel.add(pay, gbc);

        pay.addActionListener(e -> {

            try {

                Connection con = DBConnection.getConnection();

                PaymentMethod p;

                if(method.getSelectedItem().equals("Cash"))
                    p = new CashPayment();
                else
                    p = new UPIPayment();

                double amt = Double.parseDouble(amount.getText());

                p.pay(amt);

                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO Payment(payment_id, order_id, amount, method, status, payment_date) VALUES (?, ?, ?, ?, 'SUCCESS', SYSDATE)"
                );

                int pid = (int)(System.currentTimeMillis() % 100000);

                ps.setInt(1, pid);

                ps.setInt(2,
                        Integer.parseInt(orderId.getText()));

                ps.setDouble(3, amt);

                ps.setString(4,
                        method.getSelectedItem().toString());

                ps.executeUpdate();

             // ===== GET DELIVERY PARTNER DETAILS =====

                String deliveryName = "Not Assigned";
                String deliveryPhone = "N/A";

                PreparedStatement dp = con.prepareStatement(
                        "SELECT name, phone FROM Delivery WHERE delivery_id = ?"
                );

                dp.setInt(1, 1);

                ResultSet drs = dp.executeQuery();

                if(drs.next()) {

                    deliveryName = drs.getString("name");
                    deliveryPhone = drs.getString("phone");
                }

                // ===== BILL =====

                JOptionPane.showMessageDialog(this,
                        "========= BILL =========\n\n" +
                        "Order ID : " + orderId.getText() + "\n" +
                        "Amount : ₹" + amount.getText() + "\n" +
                        "Method : " + method.getSelectedItem() + "\n" +
                        "Status : SUCCESS\n\n" +

                        "===== DELIVERY PARTNER =====\n" +
                        "Name : " + deliveryName + "\n" +
                        "Phone : " + deliveryPhone + "\n\n" +

                        "Your order is on the way 🚚\n" +
                        "==========================="
                );

            } catch(Exception ex) {

                ex.printStackTrace();

                JOptionPane.showMessageDialog(this,
                        ex.getMessage());
            }
        });

        outer.add(panel);

        return outer;
    }
    
    public static void main(String[] args) {

        new LoginUI();
    }
}