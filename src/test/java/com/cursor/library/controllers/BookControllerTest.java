package com.cursor.library.controllers;

import com.cursor.library.models.Book;
import com.cursor.library.models.CreateBookDto;
import com.fasterxml.jackson.core.type.TypeReference;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BookControllerTest extends BaseControllerTest {
    private CreateBookDto createBookDto;
    private MockHttpServletRequestBuilder createBookRequestBuilder;

    @BeforeEach
    void createBookDtoAndRequestBuilderForBookCreation() throws Exception {
        // Creating DTO for book
        createBookDto = new CreateBookDto();
        createBookDto.setName("Cool createBookDto");
        createBookDto.setDescription("Cool description");
        createBookDto.setNumberOfWords(100500);
        createBookDto.setRating(10);
        createBookDto.setYearOfPublication(2020);
        createBookDto.setAuthors(Arrays.asList("author1", "author2"));

        // Creating MockHttpServletRequestBuilder instance for book creation
        createBookRequestBuilder = MockMvcRequestBuilders.post("/books")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(OBJECT_MAPPER.writeValueAsString(createBookDto));
    }

    @Test
    public void createBookTest() throws Exception {

        mockMvc.perform(createBookRequestBuilder)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(jsonPath("$.name").value("Cool createBookDto"));
    }

    @Test
    void deleteByIdTest() throws Exception {
        // Create and read book instance
        Book book = OBJECT_MAPPER.readValue(mockMvc.perform(createBookRequestBuilder).andReturn()
                        .getResponse().getContentAsString(),
                Book.class
        );

        // Delete and check success status
        mockMvc.perform(MockMvcRequestBuilders.delete("/books/" + book.getBookId()))
                .andExpect(status().isOk());
    }

    @Test
    void getByIdTest() throws Exception {
        // Create and read book instance
        Book book = OBJECT_MAPPER.readValue(mockMvc.perform(createBookRequestBuilder).andReturn()
                        .getResponse().getContentAsString(),
                Book.class
        );
        mockMvc.perform(MockMvcRequestBuilders.get("/books/" + book.getBookId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Cool description"));
    }

    @Test
    void getAllTest() throws Exception {
        // Add book to existing books in DB
        mockMvc.perform(createBookRequestBuilder);

        // Retrieve result from GET command
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/books"))
                .andExpect(status().isOk()).andReturn();

        // Get list of books from MvcResult
        List<Book> books = OBJECT_MAPPER.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        // Assert that previously added book contains in list of retrieved books
        Assertions.assertThat(books).extracting("name").contains("Cool createBookDto");
    }
}
