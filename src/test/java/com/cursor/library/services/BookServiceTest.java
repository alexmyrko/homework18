package com.cursor.library.services;

import com.cursor.library.daos.BookDao;
import com.cursor.library.exceptions.BadIdException;
import com.cursor.library.exceptions.BookNameIsNullException;
import com.cursor.library.exceptions.BookNameIsTooLongException;
import com.cursor.library.models.Book;
import com.cursor.library.models.CreateBookDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(MockitoJUnitRunner.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    private BookDao bookDao = new BookDao();
    private BookService bookService;

    @BeforeAll
    void setUp() {
        bookDao = Mockito.mock(BookDao.class);
        bookService = new BookService(bookDao);
    }

    @Test
    void getBookByIdSuccessTest() {
        String bookId = "book-id";
        Book testBook = new Book(bookId);

        Mockito.when(bookDao.getById(bookId)).thenReturn(testBook);
        Book bookFromDB = bookService.getById(bookId);

        assertEquals(
                bookId,
                bookFromDB.getBookId()
        );
    }

    @Test
    void createBookTest() {
        CreateBookDto createBookDto = new CreateBookDto();
        createBookDto.setName("Cool createBookDto");

        Book bookToReturn = new Book("test-id");

        Mockito.when(bookDao.addBook(Mockito.any(Book.class))).thenReturn(bookToReturn);
        assertEquals(bookToReturn.getBookId(), bookService.createBook(createBookDto).getBookId());
    }

    @Test
    void getBookByIdBadIdExceptionTest() {
        assertThrows(
                BadIdException.class,
                () -> bookService.getById("       ")
        );
    }

    @Test
    void getValidatedBookNameExpectBookNameIsNullExceptionTest() {
        assertThrows(
                BookNameIsNullException.class,
                () -> bookService.getValidatedBookName(null)
        );
    }

    @Test
    void getValidateBookNameExpectBookNameIsTooLongException() {
        String tooLongString = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.";
        assertThrows(
                BookNameIsTooLongException.class,
                () -> bookService.getValidatedBookName(tooLongString)
        );
    }

    @Test
    void getValidatedBookNameExpectedTrimmedNameTest() {
        String testName = "  Test Book Name     ";
        assertEquals(
                "Test Book Name",
                bookService.getValidatedBookName(testName)
        );
    }
}
