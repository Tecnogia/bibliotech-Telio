package fr.amu.univ.miage.m1.glq;

import fr.amu.univ.miage.m1.glq.model.Book;
import fr.amu.univ.miage.m1.glq.model.Member;
import fr.amu.univ.miage.m1.glq.service.LibraryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests pour le LibraryManager.
 * 
 */
class LibraryManagerTest {
    
    private LibraryManager manager;
    
    @BeforeEach
    void setUp() {
        // Reset le singleton avant chaque test
        LibraryManager.resetInstance();
        manager = LibraryManager.getInstance();
    }
    
    // ==================== TESTS LIVRES ====================
    
    @Test
    void testAddBook() {
        String id = manager.addBook("Test Book", "Test Author", "1234567890", 2023, 1, "TECHNIQUE");
        assertNotNull(id);
    }
    
    @Test
    void testGetBook() {
        Book book = manager.getBook("B00001");
        assertNotNull(book);
        assertEquals("Clean Code", book.getTitle());
    }
    
    @Test
    void testSearchBooks() {
        var results = manager.searchBooks("Clean");
        assertFalse(results.isEmpty());
    }
    
    // ==================== TESTS MEMBRES ====================
    
    @Test
    void testAddMember() {
        String id = manager.addMember("Test", "User", "test@test.com", "STUDENT");
        assertNotNull(id);
    }
    
    @Test
    void testGetMember() {
        Member member = manager.getMember("M00001");
        assertNotNull(member);
    }
    
    // ==================== TESTS EMPRUNTS ====================
    
    @Test
    void testCreateLoan() {
        String loanId = manager.createLoan("M00001", "B00001");
        assertNotNull(loanId);
    }
    
    @Test
    void testCreateLoanMemberNotFound() {
        assertThrows(RuntimeException.class, () -> {
            manager.createLoan("INVALID", "B00001");
        });
    }
    
    @Test
    void testReturnLoan() {
        // Créer un emprunt puis le retourner
        String loanId = manager.createLoan("M00002", "B00002");
        manager.returnLoan(loanId);
        
        var loan = manager.getLoan(loanId);
        assertEquals("RETURNED", loan.getStatus());
    }

}
