package com.vendora.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.vendora.model.User;

public interface UserService {

    Optional<User> findByEmail(String email);
    long findAllUsersCount();
    List<User> getAllUsers();
    User save(User user);
    void registerUser(User user, MultipartFile file) throws IOException;
    void encodeUnencryptedPasswords();
    User getLoggedInUser();
}
