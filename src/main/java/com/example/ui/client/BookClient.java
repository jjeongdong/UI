package com.example.ui.client;

import java.util.List;

import com.example.ui.entity.Book;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "book-api", url = "http://211.188.54.228:30316/api/books")
//@FeignClient(name = "book-api", url = "http://211.188.54.228:30316/api/books")  // ← 수정 http://localhost:8092/api/books
public interface BookClient {
    @GetMapping
    List<Book> getAllBooks();

    @GetMapping("/{id}")
    Book getBookById(@PathVariable Long id);

    @PostMapping
    Book createBook(@RequestBody Book book);

    @PutMapping("/{id}")
    Book updateBook(@PathVariable Long id, @RequestBody Book book);

    @DeleteMapping("/{id}")
    void deleteBook(@PathVariable Long id);
        
    @GetMapping("/search")
    List<Book> searchBooks(@RequestParam("name") String name);

}
