package com.example.ui.controller;

import com.example.ui.client.BookClient;
import com.example.ui.entity.Book;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
class BookController {

    @Autowired
    private BookClient bookClient;

    private void addLoginInfo(Model model, HttpSession session) {
        String loginUser = (String) session.getAttribute("loginUser");
        String loginUserName = (String) session.getAttribute("loginUserName");
        model.addAttribute("loginUser", loginUser);
        model.addAttribute("loginUserName", loginUserName);
    }

    @GetMapping("/book")
    public String listBooksUsers(@RequestParam(required = false, name = "name") String name,
                                 Model model,
                                 HttpSession session) {
        addLoginInfo(model, session);

        if (name != null && !name.isEmpty()) {
            model.addAttribute("books", bookClient.searchBooks(name));
        } else {
            model.addAttribute("books", bookClient.getAllBooks());
        }

        model.addAttribute("name", name);
        return "book/book-list";
    }

    @GetMapping("/book/add")
    public String showAddBookForm(Model model, HttpSession session) {
        String loginUser = (String) session.getAttribute("loginUser");
        addLoginInfo(model, session);
        Book book = new Book();
        book.setOwner(loginUser);  // owner 미리 설정
        model.addAttribute("book", book);
        return "book/create-book";
    }

    @PostMapping("/book/add")
    public String addBook(@ModelAttribute Book book, HttpSession session) {
        String loginUser = (String) session.getAttribute("loginUser");
        book.setOwner(loginUser);  // owner 다시 확실히 설정
        bookClient.createBook(book);
        return "redirect:/book";
    }

    @GetMapping("/book/edit/{id}")
    public String editBook(@PathVariable Long id, Model model, HttpSession session) {
        addLoginInfo(model, session);

        Book book = bookClient.getBookById(id);
        String loginUser = (String) session.getAttribute("loginUser");

        if (book == null || !loginUser.equals(book.getOwner())) {
            return "redirect:/book";  // 권한 없으면 목록으로
        }

        model.addAttribute("book", book);
        return "book/edit-book";
    }

    @PostMapping("/book/edit/{id}")
    public String updateBook(@PathVariable Long id, @ModelAttribute Book book, HttpSession session) {
        String loginUser = (String) session.getAttribute("loginUser");
        Book existingBook = bookClient.getBookById(id);

        if (existingBook == null || !loginUser.equals(existingBook.getOwner())) {
            return "redirect:/book";  // 권한 없으면 목록으로
        }

        book.setId(id);
        book.setOwner(loginUser);  // 기존 owner 유지 (또는 덮어쓰기)
        bookClient.updateBook(id, book);
        return "redirect:/book";
    }

    @PostMapping("/book/delete/{id}")
    public String deleteBook(@PathVariable Long id, HttpSession session) {
        String loginUser = (String) session.getAttribute("loginUser");
        Book book = bookClient.getBookById(id);

        if (book == null || !loginUser.equals(book.getOwner())) {
            return "redirect:/book";  // 권한 없으면 목록으로
        }

        bookClient.deleteBook(id);
        return "redirect:/book";
    }
}
