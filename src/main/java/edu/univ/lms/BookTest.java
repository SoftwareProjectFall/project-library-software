package edu.univ.lms;

public class BookTest {

	public static void main(String[] args) {
        Book b1 = new Book("9780132350884", "Clean Code", "Robert C. Martin");

        System.out.println(b1);

        b1.setTitle("Clean Code (Updated Edition)");
        b1.setAuthor("Uncle Bob");

        System.out.println(b1);

        System.out.println("Title: " + b1.getTitle());
    }
	}


