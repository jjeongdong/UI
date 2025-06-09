package com.example.ui.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/book")
public class BookController {

    @GetMapping
    public String showBookList(HttpSession session, Model model) {
        String loggedInUserName = (String) session.getAttribute("loggedInUserName");
        model.addAttribute("loggedInUserName", loggedInUserName);
        return "book/book-list";
    }
}
