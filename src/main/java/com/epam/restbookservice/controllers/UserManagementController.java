package com.epam.restbookservice.controllers;

import com.epam.restbookservice.domain.Book;
import com.epam.restbookservice.domain.BookBorrow;
import com.epam.restbookservice.domain.Role;
import com.epam.restbookservice.domain.User;
import com.epam.restbookservice.dtos.UserDTO;
import com.epam.restbookservice.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserManagementController {

    private final UserService userService;

    public UserManagementController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers()
                .stream().map(this::userToUserDTO)
                .collect(Collectors.toList());
    }

    private UserDTO userToUserDTO(User user) {
        return UserDTO.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .roles(user.getRoles().stream()
                        .map(Role::getRoleName).collect(Collectors.toList()))
                .books(user.getBorrowedBooks().stream()
                        .map(BookBorrow::getBook)
                        .map(Book::getTitle)
                        .collect(Collectors.toList()))
                .build();
    }

    @GetMapping
    @RequestMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public User getUser(@PathVariable Long id) {
        return userService.getUserById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User with id " + id + " not found."));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "User with username " + user.getUsername() + " already exists."));
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public User modifyUser(@RequestBody User user) {
        return userService.modifyUser(user);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
