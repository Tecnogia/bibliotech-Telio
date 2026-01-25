package fr.amu.univ.miage.m1.glq.model;

import java.util.Date;

/**
 * Représente une réservation de livre.
 */
public class Reservation {
    
    private String id;
    private String memberId;
    private String bookId;
    private Date reservationDate;
    private Date expiryDate;
    private String status; // "PENDING", "FULFILLED", "CANCELLED", "EXPIRED"
    private int queuePosition;
    
    public Reservation() {
    }
    
    public Reservation(String id, String memberId, String bookId) {
        this.id = id;
        this.memberId = memberId;
        this.bookId = bookId;
        this.reservationDate = new Date();
        this.status = "PENDING";
        // Expiration dans 7 jours
        this.expiryDate = new Date(reservationDate.getTime() + 7L * 24 * 60 * 60 * 1000);
    }
    
    // Getters et setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }
    
    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }
    
    public Date getReservationDate() { return reservationDate; }
    public void setReservationDate(Date reservationDate) { this.reservationDate = reservationDate; }
    
    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public int getQueuePosition() { return queuePosition; }
    public void setQueuePosition(int queuePosition) { this.queuePosition = queuePosition; }
}
