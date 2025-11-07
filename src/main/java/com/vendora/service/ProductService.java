package com.vendora.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vendora.model.Product;
import com.vendora.repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    // Get all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Get all products by category (if category is String)
    public List<Product> findByCategory(String category) {
    return productRepository.findByCategory(category);
    }

    

    // Get product by ID
    public Product findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    // Save or update product
    public Product save(Product product) {
        return productRepository.save(product);
    }

    //Delete product by ID
    public void delete(Long id) {
        productRepository.deleteById(id);
    }
}
