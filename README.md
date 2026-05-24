# Smart Canteen Food Delivery System

A comprehensive Java-based food delivery system with a graphical user interface (GUI), built using Swing and Oracle Database. This system allows customers to place food orders and manage payments, while providing administrators with tools to manage menus, customers, and delivery partners.

## Features

### For Customers
- **User Authentication**: Secure login and signup system
- **Browse Food Menu**: View available food items with prices
- **Shopping Cart**: Add items to cart and manage quantities
- **Order Management**: Place orders with real-time tracking
- **Payment Options**: Multiple payment methods (Cash & UPI)
- **Order Confirmation**: Detailed bill generation with delivery partner details

### For Administrators
- **Customer Management**: View and manage customer information
- **Menu Management**: Add, update, and manage food items
- **Delivery Partner Management**: Manage delivery personnel and their assignments
- **Order Oversight**: Monitor all orders in the system

## Technology Stack

- **Language**: Java
- **GUI Framework**: Java Swing
- **Database**: Oracle Database (Oracle 11g XE)
- **JDBC Driver**: Oracle JDBC Driver
- **IDE**: Eclipse IDE (recommended)

## Project Structure

```
FoodDeliverySystem/
├── src/
│   └── FoodDeliveryUI.java          # Main application file
├── bin/                              # Compiled class files
├── .classpath                        # Eclipse classpath configuration
└── .project                          # Eclipse project configuration
```

## Object-Oriented Concepts Implemented

### 1. **Encapsulation**
- `FoodItem` class encapsulates food product data with private attributes and public getters

### 2. **Inheritance**
- `User` base class extended by `Customer` and `Admin` for role-based access

### 3. **Polymorphism**
- `PaymentMethod` interface implemented by `CashPayment` and `UPIPayment` for flexible payment processing

### 4. **Abstraction**
- `DBConnection` class abstracts database connection logic
- Abstract UI components separated into distinct classes

## Class Overview

| Class | Purpose |
|-------|---------|
| `DBConnection` | Manages Oracle database connections |
| `FoodItem` | Data class for food items |
| `User` | Base class for users |
| `Customer` | Customer role extending User |
| `Admin` | Admin role extending User |
| `PaymentMethod` | Interface for payment strategies |
| `CashPayment` | Cash payment implementation |
| `UPIPayment` | UPI payment implementation |
| `LoginUI` | Login and signup interface |
| `FoodDeliveryUI` | Main application interface |

## 🔧 Installation & Setup

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- Oracle Database 11g XE or higher
- Eclipse IDE (optional but recommended)
- Oracle JDBC Driver (ojdbc.jar)

### Database Setup

1. **Create Database Tables**:
   ```sql
   CREATE TABLE user2 (
       user_id NUMBER PRIMARY KEY,
       username VARCHAR2(50) NOT NULL UNIQUE,
       password VARCHAR2(50) NOT NULL,
       role VARCHAR2(20) DEFAULT 'USER'
   );

   CREATE TABLE FoodItems (
       item_id NUMBER PRIMARY KEY,
       name VARCHAR2(100) NOT NULL,
       price NUMBER(10, 2) NOT NULL
   );

   CREATE TABLE Orders (
       order_id NUMBER PRIMARY KEY,
       customer_id NUMBER NOT NULL,
       restaurant_id NUMBER,
       delivery_id NUMBER,
       order_date DATE DEFAULT SYSDATE,
       FOREIGN KEY (customer_id) REFERENCES user2(user_id)
   );

   CREATE TABLE Payment (
       payment_id NUMBER PRIMARY KEY,
       order_id NUMBER NOT NULL,
       amount NUMBER(10, 2) NOT NULL,
       method VARCHAR2(50),
       status VARCHAR2(20),
       payment_date DATE,
       FOREIGN KEY (order_id) REFERENCES Orders(order_id)
   );

   CREATE TABLE Delivery (
       delivery_id NUMBER PRIMARY KEY,
       name VARCHAR2(100),
       phone VARCHAR2(15)
   );
   ```

2. **Update Database Credentials**:
   - Modify the database connection details in `DBConnection.getConnection()`:
   ```java
   DriverManager.getConnection(
       "jdbc:oracle:thin:@localhost:1521:xe",
       "your_username",
       "your_password"
   );
   ```

### Running the Application

1. **Compile the project**:
   ```bash
   javac -cp "path/to/ojdbc.jar" src/FoodDeliveryUI.java
   ```

2. **Run the application**:
   ```bash
   java -cp ".:path/to/ojdbc.jar" FoodDeliveryUI
   ```

   Or from Eclipse: Right-click the project → Run As → Java Application

## Usage Guide

### Login
- Start the application to see the login screen
- Enter username and password
- Click "Login" to access the system
- New users can click "Signup" to create an account

### For Customers
1. **Place Order**:
   - Navigate to "Orders" tab
   - Click "Load Menu" to fetch available items
   - Select an item and enter quantity
   - Click "Add To Cart" to add items
   - Click "Place Order" to confirm

2. **Make Payment**:
   - Go to "Payment" tab after placing an order
   - Select payment method (Cash or UPI)
   - Click "Pay Now" to complete transaction
   - View bill with delivery partner details

### For Administrators
1. **Manage Customers**: View customer information
2. **Manage Menu**: Add/update food items
3. **Manage Delivery**: Assign delivery partners to orders

## Security Features

- User authentication with username and password
- Role-based access control (User vs Admin)
- Prepared statements to prevent SQL injection
- Password field masking in login UI

## Database Schema

### User Table (user2)
- Stores user credentials and roles

### FoodItems Table
- Contains menu items with prices

### Orders Table
- Tracks customer orders with relationships to customers, restaurants, and delivery partners

### Payment Table
- Records all payment transactions

### Delivery Table
- Manages delivery partner information

## Future Enhancements

- [ ] Email/SMS notifications for order status
- [ ] Order tracking with real-time updates
- [ ] Rating and review system
- [ ] Multiple restaurant support
- [ ] Online payment gateway integration
- [ ] Mobile application
- [ ] Advanced reporting and analytics
- [ ] Inventory management system
- [ ] Restaurant partner dashboard

## Known Issues & Limitations

- Hard-coded database credentials (use environment variables in production)
- Order ID and Payment ID generation using timestamp modulo (not guaranteed unique)
- No input validation for numeric fields
- Restaurant and Delivery IDs are hard-coded to 1
- Payment status always set to 'SUCCESS'



Last Updated: May 2026
