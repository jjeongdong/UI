package com.example.ui.client;

import com.example.ui.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", url = "http://localhost:8081")
public interface UserClient {

    @GetMapping("/api/users/{id}")
    User getUserById(@PathVariable("id") Long id);

    @GetMapping("/api/users/email")
    User getUserByEmail(@RequestParam("email") String email);

    @PostMapping("/api/users")
    void createUser(@RequestBody User user);

    @PutMapping("/api/users/{id}")
    void updateUser(@PathVariable("id") Long id, @RequestBody User user);
}
