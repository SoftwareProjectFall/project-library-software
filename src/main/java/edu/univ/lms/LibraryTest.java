package edu.univ.lms;

public class LibraryTest {

    public static void main(String[] args) {
        Library library = new Library();

        Book b1 = new Book("9780132350884", "Clean Code", "Robert C. Martin");
        Book b2 = new Book("9780134685991", "Effective Java", "Joshua Bloch");
        Book duplicate = new Book("9780132350884", "Clean Code (Copy)", "Uncle Bob");

        System.out.println("Adding b1: " + library.addBook(b1));      
        System.out.println("Adding b2: " + library.addBook(b2));      
        System.out.println("Adding duplicate: " + library.addBook(duplicate)); 
        System.out.println("Adding null: " + library.addBook(null));  
   
    
    
    System.out.println("\n--- Testing searchBookByIsbn ---");
    Book found = library.searchBookByIsbn("9780132350884");
    System.out.println("Found book: " + found);

    Book notFound = library.searchBookByIsbn("0000000000000");
    System.out.println("Found book: " + notFound);
    
    System.out.println("\n--- Testing showAllBooks ---");
    library.showAllBooks();

    System.out.println("\n--- Testing removeBook ---");

 boolean removed1 = library.removeBook("9780132350884");
 System.out.println("Removed Clean Code: " + removed1);

 boolean removed2 = library.removeBook("0000000000000");
 System.out.println("Removed non-existing: " + removed2);

 library.showAllBooks();
 
 
 System.out.println("\n--- Testing updateBook ---");

boolean updated1 = library.updateBook(
      "9780134685991",
      "Effective Java (3rd Edition)",
      "Joshua J. Bloch"
);
System.out.println("Updated existing: " + updated1);

boolean updated2 = library.updateBook(
      "1111111111111",
      "Doesn't Matter",
      "Nobody"
);
System.out.println("Updated non-existing: " + updated2);

library.showAllBooks();
 
 
System.out.println("\n--- Quick checks ---");
System.out.println("Library size: " + library.size());
System.out.println("Has 9780134685991? " + library.containsIsbn("9780134685991"));
System.out.println("Has 0000000000000? " + library.containsIsbn("0000000000000"));

    }
    
    
    
    
}

