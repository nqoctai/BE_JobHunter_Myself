package com.example.jobhunter_myself.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jobhunter_myself.domain.User;
import com.example.jobhunter_myself.domain.response.ResCreateUserDTO;
import com.example.jobhunter_myself.domain.response.ResUpdateUserDTO;
import com.example.jobhunter_myself.domain.response.ResUserDTO;
import com.example.jobhunter_myself.domain.response.ResultPaginationDTO;
import com.example.jobhunter_myself.service.UserService;
import com.example.jobhunter_myself.util.annotation.ApiMessage;
import com.example.jobhunter_myself.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    @ApiMessage("CREATE USER")
    public ResponseEntity<ResCreateUserDTO> createUser(@RequestBody User rqUser) throws IdInvalidException {
        boolean isEmailExist = userService.isExistUserByEmail(rqUser.getEmail());
        if (isEmailExist) {
            throw new IdInvalidException("Email is already exist");
        }
        String hashPassword = this.passwordEncoder.encode(rqUser.getPassword());
        rqUser.setPassword(hashPassword);
        User user = userService.createUser(rqUser);
        ResCreateUserDTO res = userService.convertToResCreateUserDTO(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping("/users/{id}")
    @ApiMessage("GET USER BY ID")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") Long id) throws IdInvalidException {
        boolean isExistUser = userService.isExistUserById(id);
        if (!isExistUser) {
            throw new IdInvalidException("User is not exist");
        }
        User user = userService.getUserById(id);
        return ResponseEntity.ok().body(this.userService.convertToResUserDTO(user));
    }

    @PutMapping("/users")
    @ApiMessage("UPDATE USER")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User rqUser) throws IdInvalidException {
        boolean isExistUser = userService.isExistUserById(rqUser.getId());
        if (!isExistUser) {
            throw new IdInvalidException("User is not exist");
        }
        User user = userService.updateUser(rqUser);
        return ResponseEntity.ok().body(userService.convertToResUpdateUserDTO(user));
    }

    @GetMapping("/users")
    @ApiMessage("GET ALL USER")
    public ResponseEntity<ResultPaginationDTO> getAllUser(@Filter Specification<User> spec, Pageable pageable) {
        return ResponseEntity.ok().body(userService.getAllUser(spec, pageable));
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("DELETE USER")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) throws IdInvalidException {
        boolean isExistUser = userService.isExistUserById(id);
        if (!isExistUser) {
            throw new IdInvalidException("User is not exist");
        }
        userService.deleteUser(id);
        return ResponseEntity.ok().body(null);
    }

}