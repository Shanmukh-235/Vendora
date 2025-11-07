package com.vendora.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.vendora.model.User;
import com.vendora.repository.UserRepository;
import com.vendora.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private static final String UPLOAD_DIR = "uploads/profiles/";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public long findAllUsersCount() {
        return userRepository.count();
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public void registerUser(User user, MultipartFile file) throws IOException {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        user.setRole("ROLE_USER");

        // ✅ Handle profile image upload
        if (file != null && !file.isEmpty()) {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            user.setProfileImage("/" + UPLOAD_DIR + fileName);
        } else {
            user.setProfileImage("/uploads/profiles/default.png");
        }

        userRepository.save(user);
    }

    @Override
    public void encodeUnencryptedPasswords() {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            String password = user.getPassword();
            if (password != null && !password.startsWith("$2a$")) { // Not bcrypt yet
                user.setPassword(passwordEncoder.encode(password));
                userRepository.save(user);
                System.out.println("🔒 Encoded password for user: " + user.getEmail());
            }
        }
    }

    // ✅ New method merged in
    @Override
    public User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getName().equals("anonymousUser")) {
            return null;
        }

        String email = authentication.getName(); // username = email
        return userRepository.findByEmail(email).orElse(null);
    }
}
