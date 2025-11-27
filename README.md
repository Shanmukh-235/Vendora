# 🛍️ Vendora — Online Marketplace Platform

Vendora is a full-stack e-commerce web application built with **Spring Boot** and **Thymeleaf**, designed to enable users to browse products, manage carts and wishlists, and place orders — while providing administration capabilities for product and user management.


## ✨ Features

### 👤 User & Authentication
- Registration and secure login with Spring Security
- Custom login success handling
- Role-based access: **User**, **Agent**, **Admin**

### 🛒 Shopping
- Browse products and categories
- Product details page
- Add / remove items from **Cart**
- Add / remove items from **Wishlist**
- Proceed through Checkout flow

### 📦 Order & Delivery
- Place and track orders
- Delivery agent management
- Order status handling

### 🛠️ Admin / Agent Dashboards
- Manage products, users, and orders  
- Approve / manage delivery agents


## 🧱 Tech Stack

| Layer | Technology |
|-------|------------|
| Backend | Spring Boot, Spring MVC, Spring Security |
| Frontend | Thymeleaf templates, HTML, Tailwind CSS |
| Data | Spring Data JPA, MySQL (configurable) |
| Build System | Maven |
| File Uploads | Local storage (`uploads/` directory) |


## 📂 Project Structure

```
Vendora/
├── pom.xml
├── README.md
├── HELP.md
├── mvnw
├── mvnw.cmd
├── .gitignore
├── .gitattributes
├── .vscode/
│   ├── launch.json
│   └── settings.json
├── .mvn/
│   └── wrapper/
│       └── maven-wrapper.properties
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/
    │   │       └── vendora/
    │   │           ├── VendoraApplication.java
    │   │           ├── config/
    │   │           │   ├── CustomLoginSuccessHandler.java
    │   │           │   ├── DataLoader.java
    │   │           │   ├── SecurityConfig.java
    │   │           │   └── WebConfig.java
    │   │           ├── controller/
    │   │           │   ├── AuthController.java
    │   │           │   ├── CartController.java
    │   │           │   ├── CheckoutController.java
    │   │           │   ├── DeliveryController.java
    │   │           │   ├── HomeController.java
    │   │           │   ├── OrderController.java
    │   │           │   ├── ProductController.java
    │   │           │   ├── ShopActionController.java
    │   │           │   ├── ShopController.java
    │   │           │   ├── UserController.java
    │   │           │   ├── UserOrderController.java
    │   │           │   ├── WishlistController.java
    │   │           │   ├── agent/
    │   │           │   │   └── AgentOrderController.java
    │   │           │   └── admin/
    │   │           │       ├── AdminAgentController.java
    │   │           │       ├── AdminCategoryController.java
    │   │           │       ├── AdminController.java
    │   │           │       ├── AdminDeliveryAgentController.java
    │   │           │       ├── AdminOrderController.java
    │   │           │       └── AdminProductController.java
    │   │           ├── model/
    │   │           │   ├── CartItem.java
    │   │           │   ├── Category.java
    │   │           │   ├── Delivery.java
    │   │           │   ├── DeliveryAgent.java
    │   │           │   ├── Order.java
    │   │           │   ├── OrderItem.java
    │   │           │   ├── Product.java
    │   │           │   ├── User.java
    │   │           │   └── Wishlist.java
    │   │           ├── repository/
    │   │           │   ├── AgentRepository.java
    │   │           │   ├── CartRepository.java
    │   │           │   ├── CategoryRepository.java
    │   │           │   ├── DeliveryAgentRepository.java
    │   │           │   ├── DeliveryRepository.java
    │   │           │   ├── OrderItemRepository.java
    │   │           │   ├── OrderRepository.java
    │   │           │   ├── ProductRepository.java
    │   │           │   ├── UserRepository.java
    │   │           │   └── WishlistRepository.java
    │   │           ├── service/
    │   │           │   ├── AdminService.java
    │   │           │   ├── AgentService.java
    │   │           │   ├── CartService.java
    │   │           │   ├── CustomUserDetailsService.java
    │   │           │   ├── DeliveryAgentService.java
    │   │           │   ├── OrderService.java
    │   │           │   ├── ProductService.java
    │   │           │   ├── UserService.java
    │   │           │   ├── WishlistService.java
    │   │           │   └── impl/
    │   │           │       ├── ProductServiceImpl.java
    │   │           │       └── UserServiceImpl.java
    │   ├── resources/
    │   │   ├── application.properties
    │   │   ├── META-INF/
    │   │   │   └── additional-spring-configuration-metadata.json
    │   │   └── templates/
    │   │       ├── index.html
    │   │       ├── login.html
    │   │       ├── register.html
    │   │       ├── cart.html
    │   │       ├── orders.html
    │   │       ├── wishlist.html
    │   │       ├── user/
    │   │       │   ├── dashboard.html
    │   │       │   ├── checkout.html
    │   │       │   ├── settings.html
    │   │       │   ├── product.html
    │   │       │   ├── orders.html
    │   │       │   ├── order-details.html
    │   │       │   ├── order_success.html
    │   │       │   └── cart.html
    │   │       ├── shop/
    │   │       │   ├── shop.html
    │   │       │   ├── product-details.html
    │   │       │   ├── wishlist.html
    │   │       │   └── cart.html
    │   │       ├── agent/
    │   │       │   └── dashboard.html
    │   │       └── admin/
    │   │           ├── dashboard.html
    │   │           ├── users.html
    │   │           ├── products.html
    │   │           ├── add-product.html
    │   │           ├── edit-product.html
    │   │           ├── categories.html
    │   │           ├── categorey-form.html
    │   │           ├── orders.html
    │   │           ├── order-details.html
    │   │           ├── deliveries.html
    │   │           └── delivery-agents.html
    └── test/
        └── java/
            └── com/
                └── vendora/
                    └── VendoraApplicationTests.java

```


## ⚙️ Setup & Installation

### ✔️ Prerequisites
- Java 17+
- Maven 3.6+
- MySQL (or alternative DB with config updates)

### 📌 Clone & Build

```bash
git clone https://github.com/Shanmukh-235/Vendora.git
cd Vendora
mvn clean install
```
### 🗄️ Configure Database

Update src/main/resources/application.properties:
```sql
spring.application.name=Vendora

# ==============================
# DATABASE CONFIG
# ==============================
spring.datasource.url=jdbc:mysql://localhost:3306/vendora?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# ==============================
# JPA / HIBERNATE CONFIG
# ==============================
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# ==============================
# FILE UPLOAD
# ==============================
file.upload-dir=src/main/resources/static/uploads/profiles

# Allow larger file uploads
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB


# ==============================
# SERVER
# ==============================
server.port=8080
spring.thymeleaf.cache=false
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html
spring.thymeleaf.mode=HTML
logging.level.org.thymeleaf=DEBUG

```
▶️ Run the App
```
mvn spring-boot:run
```

Now open in browser 👇

`http://localhost:8080
`
## 🔐 Default Roles & Access

| Role  | Access |
|-------|--------|
| User  | Shop, cart, wishlist, checkout |
| Agent | Manage deliveries |
| Admin | Manage users, orders, and products |

> Database seeds and role setup handled by `DataLoader.java`.


## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Submit a PR with a clear description

Bug reports and feature requests are welcome!



## 📄 License

This project is licensed under the **MIT License** — free to use and modify.



## 💡 Future Enhancements

- Payment gateway integration
- Product reviews and ratings
- Containerization (Docker)
- Improved admin analytics dashboard


### 🎉 Thanks for checking out Vendora!

If you like this project, feel free to **star ⭐ the repo**!

---

## 📄 Author

**Shanmukha Poorna Chand**  
Java Full-Stack Developer | Passionate about Web Development and Clean UI Design  
📧 shanmukhapoornachand14316@gmail.com  
🔗 [LinkedIn Profile](www.linkedin.com/in/shanmukha-poorna-chand-adapaka)


> *Built with ❤️*