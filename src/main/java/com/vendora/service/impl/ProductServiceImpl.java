// package com.vendora.service.impl;

// import java.io.IOException;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.nio.file.Paths;
// import java.nio.file.StandardCopyOption;
// import java.util.List;
// import java.util.Optional;
// import java.util.UUID;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
// import org.springframework.web.multipart.MultipartFile;

// import com.vendora.model.Product;
// import com.vendora.repository.ProductRepository;
// import com.vendora.service.ProductService;

// @Service
// @Transactional
// public class ProductServiceImpl implements ProductService {

//     private final ProductRepository productRepository;
//     private final Path uploadDir = Paths.get("src/main/resources/static/uploads/products");

//     @Autowired
//     public ProductServiceImpl(ProductRepository productRepository) {
//         this.productRepository = productRepository;
//         try {
//             if (!Files.exists(uploadDir)) {
//                 Files.createDirectories(uploadDir);
//             }
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }

//     @Override
//     public List<Product> findAll() {
//         return productRepository.findAll();
//     }

//     @Override
//     public Optional<Product> findById(Long id) {
//         return productRepository.findById(id);
//     }

//     private String saveImage(MultipartFile image) throws IOException {
//         if (image == null || image.isEmpty()) return null;
//         String original = image.getOriginalFilename();
//         String ext = "";
//         if (original != null && original.contains(".")) {
//             ext = original.substring(original.lastIndexOf('.'));
//         }
//         String filename = "prod_" + UUID.randomUUID() + ext;
//         Path target = uploadDir.resolve(filename);
//         // replace if exists
//         Files.copy(image.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
//         return "/uploads/products/" + filename;
//     }

//     @Override
//     public Product save(Product product, MultipartFile image) throws IOException {
//         String path = saveImage(image);
//         if (path != null) product.setImageUrl(path);
//         return productRepository.save(product);
//     }

//     @Override
//     public Product update(Product product, MultipartFile image) throws IOException {
//         if (image != null && !image.isEmpty()) {
//             // save new image and set path
//             String path = saveImage(image);
//             product.setImageUrl(path);
//         }
//         return productRepository.save(product);
//     }

//     @Override
//     public void deleteById(Long id) throws IOException {
//         // optionally delete file from disk
//         Optional<Product> opt = productRepository.findById(id);
//         if (opt.isPresent()) {
//             String imagePath = opt.get().getImageUrl();
//             if (imagePath != null && imagePath.startsWith("/uploads/products/")) {
//                 Path file = Paths.get("src/main/resources/static", imagePath);
//                 try {
//                     Files.deleteIfExists(file);
//                 } catch (IOException e) {
//                     // log but allow delete of DB record
//                     e.printStackTrace();
//                 }
//             }
//             productRepository.deleteById(id);
//         }
//     }

//     @Override
//     public long count() {
//         return productRepository.count();
//     }
// }
