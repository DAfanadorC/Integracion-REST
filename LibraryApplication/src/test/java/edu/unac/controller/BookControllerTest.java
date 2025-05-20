package edu.unac.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.unac.domain.Book;
import edu.unac.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }

    @Test
    void testCreateBook() throws Exception {
        Book book = new Book(null, "El Principito", "Antoine de Saint-Exupéry", 1943);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("El Principito")));
    }

    @Test
    void testGetAllBooks() throws Exception {
        repository.save(new Book(null, "Cien años de soledad", "Gabriel García Márquez", 1967));
        repository.save(new Book(null, "La vorágine", "José Eustasio Rivera", 1924));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }


    @Test
    void testGetBookById() throws Exception {
        Book saved = repository.save(new Book(null, "Crónica de una muerte anunciada", "Gabo", 1981));

        mockMvc.perform(get("/api/books/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Crónica de una muerte anunciada")));
    }

    @Test
    void testUpdateBook() throws Exception {
        Book original = repository.save(new Book(null, "Título viejo", "Autor X", 1999));
        Book modified = new Book(null, "Título nuevo", "Autor X", 1999);

        mockMvc.perform(put("/api/books/{id}", original.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modified)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Título nuevo")));
    }

    @Test
    void testDeleteBook() throws Exception {
        Book toDelete = repository.save(new Book(null, "Libro Eliminable", "Autor Y", 2005));

        mockMvc.perform(delete("/api/books/{id}", toDelete.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testCreateInvalidBook() throws Exception {
        Book invalid = new Book(null, "", "", 1000); 

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The title cannot be empty."));
    }

    @Test
    void testUpdateInvalidBook() throws Exception {
        Book existing = repository.save(new Book(null, "Título válido", "Autor válido", 2010));
        Book invalid = new Book(null, "", "", 1000);

        mockMvc.perform(put("/api/books/{id}", existing.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The title cannot be empty."));
    }

    @Test
    void testGetBookByNonExistentId() throws Exception {
        mockMvc.perform(get("/api/books/{id}", 123456))
                .andExpect(status().isNotFound());
    }
}
