package com.vendora.controller.admin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.vendora.model.Product;
import com.vendora.repository.ProductRepository;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    @Autowired
    private ProductRepository productRepository;

    // 🔹 View all products
    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "admin/products";
    }

    // 🔹 Show add product form
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        return "admin/add-product";
    }

    // 🔹 Save new product
    @PostMapping("/add")
    public String addProduct(@ModelAttribute Product product,
                             @RequestParam("imageFile") MultipartFile imageFile) throws IOException {

        String imagePath = "/uploads/products/default.png";

        if (!imageFile.isEmpty()) {
            Path uploadDir = Paths.get("uploads/products");
            if (!Files.exists(uploadDir))
                Files.createDirectories(uploadDir);

            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            Path filePath = uploadDir.resolve(fileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            imagePath = "/uploads/products/" + fileName;
        }

        product.setImageUrl(imagePath);
        productRepository.save(product);
        return "redirect:/admin/products";
    }

    // 🔹 Show edit form
    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + id));
        model.addAttribute("product", product);
        return "admin/edit-product";
    }

    // 🔹 Update product
    @PostMapping("/update")
    public String updateProduct(@ModelAttribute Product product,
                                @RequestParam("imageFile") MultipartFile imageFile) throws IOException {

        if (!imageFile.isEmpty()) {
            Path uploadDir = Paths.get("uploads/products");
            if (!Files.exists(uploadDir))
                Files.createDirectories(uploadDir);

            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            Path filePath = uploadDir.resolve(fileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            product.setImageUrl("/uploads/products/" + fileName);
        } else {
            Product existing = productRepository.findById(product.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + product.getId()));
            product.setImageUrl(existing.getImageUrl());
        }

        productRepository.save(product);
        return "redirect:/admin/products";
    }

    // 🔹 Delete product
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        return "redirect:/admin/products";
    }
}
