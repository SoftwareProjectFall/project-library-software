package edu.univ.lms;

public class Book {
	private String isbn;
	private String title;
	private String author;

	//Constructor
	public Book(String isbn, String title, String author) {
	 this.isbn = isbn;
	 this.title = title;
	 this.author = author;
	}


	//Getters
	public String getIsbn() {
	 return isbn;
	}

	public String getTitle() {
	 return title;
	}

	public String getAuthor() {
	 return author;
	}

	 // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
    
 // toString
    @Override
    public String toString() {
        return "Book{" +
               "ISBN='" + isbn + '\'' +
               ", Title='" + title + '\'' +
               ", Author='" + author + '\'' +
               '}';
    }
    
    
    
    
    
    
    
    
 
    
    
    
}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	


















