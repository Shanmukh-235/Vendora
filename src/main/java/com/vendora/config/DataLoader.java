package com.vendora.config;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.vendora.model.Product;
import com.vendora.model.User;
import com.vendora.repository.ProductRepository;
import com.vendora.repository.UserRepository;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // Create admin if not exists
        if (userRepository.findByEmail("admin@ex.com").isEmpty()) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@ex.com");
            admin.setPassword("admin123");
            admin.setRole("ROLE_ADMIN");
            admin.setPhone("0000000000");
            admin.setEnabled(true);
            admin.setAddress("Admin Office");
            admin.setProfileImage("/uploads/profiles/admin.png");
            userRepository.save(admin);
            System.out.println("Admin user created: admin@ex.com / admin123");
        }

        // Add sample products only (for admin dashboard view)
        if (productRepository.count() == 0) {
            List<Product> products = Arrays.asList(
                createProduct("Wireless Mouse", "Ergonomic wireless mouse", new BigDecimal("29.99"), 50),
                createProduct("Mechanical Keyboard", "RGB backlit mechanical keyboard", new BigDecimal("89.99"), 30),
                createProduct("Noise Cancelling Headphones", "Over-ear Bluetooth headphones", new BigDecimal("199.99"), 20),
                createProduct("4K Monitor", "27-inch UHD display", new BigDecimal("299.99"), 15),
                createProduct("USB-C Dock", "Multi-port docking station", new BigDecimal("59.99"), 40)
            );

            productRepository.saveAll(products);
            System.out.println("Sample products added for admin view.");
        }
    }

    private Product createProduct(String name, String description, BigDecimal price, int stock) {
        Product p = new Product();
        p.setName(name);
        p.setDescription(description);
        p.setPrice(price);
        p.setStock(stock);
        p.setImageUrl("/uploads/products/default.png");
        return p;
    }
}
