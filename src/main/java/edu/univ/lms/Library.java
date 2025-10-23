package edu.univ.lms;

import java.util.ArrayList;

import java.util.List;
import java.util.Collections;

public class Library {
    private List<Book> books;

    // Constructor
    public Library() {
        books = new ArrayList<>();
    }
    //
    public boolean addBook(Book book) {
        if (book == null || book.getIsbn() == null || book.getIsbn().isBlank()) {
            return false;
        }
     // to make sure that ISBN is not reapeted
        for (Book b : books) {
            if (book.getIsbn().equals(b.getIsbn())) {
                return false;
            }
        }

        books.add(book);
        return true;
    }
    
    public Book searchBookByIsbn(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            return null;
        }

        for (Book b : books) {
            if (b.getIsbn().equals(isbn)) {
                return b;
            }
        }

        return null; 
    }
    
    
    
    public void showAllBooks() {
        if (books.isEmpty()) {
            System.out.println("No books in the library.");
            return;
        }

        System.out.println("\n--- List of Books ---");
        for (Book b : books) {
            System.out.println(b);
        }
    }
    
    
    public boolean removeBook(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            return false;
        }

        for (Book b : books) {
            if (b.getIsbn().equals(isbn)) {
                books.remove(b);
                return true; 
                }
        }

        return false; 
    }

    public boolean updateBook(String isbn, String newTitle, String newAuthor) {
        if (isbn == null || isbn.isBlank()) {
            return false;
        }

        for (Book b : books) {
            if (b.getIsbn().equals(isbn)) {
                if (newTitle != null && !newTitle.isBlank()) {
                    b.setTitle(newTitle);
                }
                if (newAuthor != null && !newAuthor.isBlank()) {
                    b.setAuthor(newAuthor);
                }
                return true; 
            }
        }

        return false; 
    }

    public int size() {
        return books.size();
    }
    
    public boolean containsIsbn(String isbn) {
        if (isbn == null || isbn.isBlank()) return false;
        for (Book b : books) {
            if (isbn.equals(b.getIsbn())) return true;
        }
        return false;
    }

    public List<Book> getAllBooks() {
        return Collections.unmodifiableList(books);
    }
    
    
    
    
    
    
    
    
}