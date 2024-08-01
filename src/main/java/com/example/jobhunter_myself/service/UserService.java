package com.example.jobhunter_myself.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.jobhunter_myself.domain.User;
import com.example.jobhunter_myself.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(User rqUser) {
        User userDB = userRepository.findById(rqUser.getId()).orElse(null);
        if (userDB == null) {
            return null;
        }
        userDB.setName(rqUser.getName());
        userDB.setEmail(rqUser.getEmail());
        userDB.setPassword(rqUser.getPassword());
        return userRepository.save(userDB);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    public boolean isExistUserByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
