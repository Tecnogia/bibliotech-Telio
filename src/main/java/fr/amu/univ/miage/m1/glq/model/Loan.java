package fr.amu.univ.miage.m1.glq.model;

import java.util.Date;

/**
 * Représente un emprunt de livre.
 * 
 * TODO: Cette classe a plusieurs problèmes à corriger :

 */
public class Loan {
    
    private String id;
    private String memberId;
    private String bookId;
    
    private Member member;
    private Book book;
    
    private Date loanDate;
    private Date dueDate;
    private Date returnDate;
    
    // Statut en String : "ACTIVE", "RETURNED", "OVERDUE", "LOST"
    private String status;
    
    private double penaltyAmount;
    
    // Nombre de renouvellements
    private int renewalCount;
    private int maxRenewals = 2;
    
    // Notes diverses
    private String notes;
    
    public Loan() {
    }
    
    public Loan(String id, Member member, Book book) {
        this.id = id;
        this.member = member;
        this.memberId = member.getId();
        this.book = book;
        this.bookId = book.getId();
        this.loanDate = new Date();
        this.status = "ACTIVE";
        this.renewalCount = 0;
        this.penaltyAmount = 0.0;
        
        // Calcul de la date de retour dans le constructeur
        // 14 jours pour les étudiants, 30 pour les enseignants, 21 pour les autres
        long durationMs;
        if (member.getMemberType().equals("STUDENT")) {
            durationMs = 14L * 24 * 60 * 60 * 1000;
        } else if (member.getMemberType().equals("TEACHER")) {
            durationMs = 30L * 24 * 60 * 60 * 1000;
        } else {
            durationMs = 21L * 24 * 60 * 60 * 1000;
        }
        this.dueDate = new Date(loanDate.getTime() + durationMs);
    }
    
    public boolean isOverdue() {
        if (status.equals("RETURNED") || status.equals("LOST")) {
            return false;
        }
        return new Date().after(dueDate);
    }
    
    public int getDaysOverdue() {
        if (!isOverdue()) {
            return 0;
        }
        long diff = new Date().getTime() - dueDate.getTime();
        return (int) (diff / (24 * 60 * 60 * 1000));
    }
    
    public boolean canRenew() {
        if (!status.equals("ACTIVE")) {
            return false;
        }
        if (isOverdue()) {
            return false;
        }
        if (renewalCount >= maxRenewals) {
            return false;
        }
        // Vérifier si le livre est réservé par quelqu'un d'autre
        return true;
    }
    
    public void renew() {
        if (!canRenew()) {
            throw new RuntimeException("Cannot renew this loan");
        }
        renewalCount++;
        // Ajouter 14 jours à la date de retour
        long extensionMs = 14L * 24 * 60 * 60 * 1000;
        this.dueDate = new Date(dueDate.getTime() + extensionMs);
    }
    
    // Getters et setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }
    
    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }
    
    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }
    
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
    
    public Date getLoanDate() { return loanDate; }
    public void setLoanDate(Date loanDate) { this.loanDate = loanDate; }
    
    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
    
    public Date getReturnDate() { return returnDate; }
    public void setReturnDate(Date returnDate) { this.returnDate = returnDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public double getPenaltyAmount() { return penaltyAmount; }
    public void setPenaltyAmount(double penaltyAmount) { this.penaltyAmount = penaltyAmount; }
    
    public int getRenewalCount() { return renewalCount; }
    public void setRenewalCount(int renewalCount) { this.renewalCount = renewalCount; }
    
    public int getMaxRenewals() { return maxRenewals; }
    public void setMaxRenewals(int maxRenewals) { this.maxRenewals = maxRenewals; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    @Override
    public String toString() {
        return "Loan[" + id + "] " + bookId + " -> " + memberId + " (" + status + ")";
    }
}
