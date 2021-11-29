package com.rohlik.useradminbe.controllers;

import com.rohlik.useradminbe.DTO.LazyDTO;
import com.rohlik.useradminbe.DTO.UserDTO;
import com.rohlik.useradminbe.DTO.UserDTO2;
import com.rohlik.useradminbe.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;


@CrossOrigin(origins = "http://localhost:8081/")
@RestController
public class UserController {
    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public ResponseEntity<?> getUsers(@RequestBody LazyDTO lazyDTO) {
        return new ResponseEntity<>(userService.lazyLoadUsers(
                lazyDTO.first, lazyDTO.rows, lazyDTO.filters, lazyDTO.sortField, lazyDTO.sortOrder),
                HttpStatus.OK);
    }

    @PatchMapping("/user.status")
    public ResponseEntity<?> flipUserStatus(@RequestParam int userId) {
        if (userService.flipStatus(userId)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/user")
    public ResponseEntity<?> createUser(@RequestBody UserDTO user) {
        if (userService.createUser(user)) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("user")
    public ResponseEntity<?> editUser(@RequestBody UserDTO2 user) {
        if (userService.editUser(user)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/user")
    public ResponseEntity<?> deleteUser(@RequestParam int userId) {
        if (userService.deleteUser(userId)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/initDB")
    public void initDB() throws ParseException {
        userService.initDatabase();
    }
}
