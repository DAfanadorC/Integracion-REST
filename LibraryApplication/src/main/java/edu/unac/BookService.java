package edu.unac;

import edu.unac.domain.Book;
import edu.unac.exception.InvalidBookException;
import edu.unac.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    @Autowired
    private BookRepository repository;

    public List<Book> getAllBooks() {
        return repository.findAll();
    }

    public Optional<Book> getBookById(Long id) {
        return repository.findById(id);
    }

    public Book saveBook(Book book) {
        validateBook(book);
        return repository.save(book);
    }

    public Optional<Book> updateBook(Long id, Book book) {
        validateBook(book);
        return repository.findById(id).map(existing -> {
            existing.setTitle(book.getTitle());
            existing.setAuthor(book.getAuthor());
            existing.setYear(book.getYear());
            return repository.save(existing);
        });
    }

    public void deleteBook(Long id) {
        repository.deleteById(id);
    }

    private void validateBook(Book book) {
        if (book.getTitle() == null || book.getTitle().isBlank()) {
            throw new InvalidBookException("The title cannot be empty.");
        }

        if (book.getAuthor() == null || book.getAuthor().isBlank()) {
            throw new InvalidBookException("The author cannot be empty.");
        }

        int currentYear = Year.now().getValue();
        if (book.getYear() < 1500 || book.getYear() > currentYear) {
            throw new InvalidBookException("The year must be between 1500 and" + currentYear + ".");
        }
    }
}
