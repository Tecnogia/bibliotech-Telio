package fr.amu.univ.miage.m1.glq.model;

/**
 * Représente un livre dans la bibliothèque.
 * 
 * TODO: Cette classe a plusieurs problèmes à corriger :
 */
public class Book {
    
    public String id;           // Devrait être privé
    public String title;        // Devrait être privé
    public String author;       // Devrait être privé  
    public String isbn;         // Devrait être privé
    public int year;            // Devrait être privé
    public int copies;          // Nombre total d'exemplaires
    public int availableCopies; // Nombre d'exemplaires disponibles
    public String category;     // "ROMAN", "SCIENCE", "TECHNIQUE", etc.
    public boolean isActive;    // Si le livre est actif dans le catalogue
    
    public Book() {
    }
    
    public Book(String id, String title, String author, String isbn,
                int year, int copies, String category) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.year = year;
        this.copies = copies;
        this.availableCopies = copies;
        this.category = category;
        this.isActive = true;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    
    public int getCopies() { return copies; }
    public void setCopies(int copies) { this.copies = copies; }
    
    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int availableCopies) { 
        this.availableCopies = availableCopies; 
    }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    @Override
    public String toString() {
        return "Book[" + id + "] " + title + " by " + author;
    }
}
