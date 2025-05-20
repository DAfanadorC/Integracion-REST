package edu.unac.controller;

import edu.unac.BookService;
import edu.unac.domain.Book;
import edu.unac.exception.InvalidBookException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    @Autowired
    private BookService service;

    @GetMapping
    public List<Book> getAllBooks() {
        return service.getAllBooks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(@PathVariable("id") Long id) {
        return service.getBookById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createBook(@RequestBody Book book) {
        try {
            Book saved = service.saveBook(book);
            return ResponseEntity.ok(saved);
        } catch (InvalidBookException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable("id") Long id, @RequestBody Book book) {
        try {
            return service.updateBook(id, book)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (InvalidBookException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable("id") Long id) {
        service.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
