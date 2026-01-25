package fr.amu.univ.miage.m1.glq.model;

import java.util.Date;

/**
 * Représente un adhérent de la bibliothèque.
 * 
 * TODO: Cette classe a plusieurs problèmes à corriger :

 */
public class Member {
    
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    
    // Primitive obsession : l'adresse devrait être un objet
    private String streetAddress;
    private String city;
    private String zipCode;
    private String country;
    
    // Utilisation de java.util.Date (devrait être java.time.LocalDate)
    private Date birthDate;
    private Date membershipDate;
    private Date membershipExpiryDate;
    
    // Type en String au lieu d'enum (STUDENT, TEACHER, EXTERNAL, STAFF)
    private String memberType;
    
    // Quota d'emprunts selon le type (devrait être calculé, pas stocké)
    private int loanQuota;
    
    private boolean isActive;
    
    // Compteurs (problème : logique métier dans le modèle)
    private int currentLoansCount;
    private int totalLoansCount;
    private int lateReturnsCount;
    
    public Member() {
    }
    
    public Member(String id, String firstName, String lastName, String email, String memberType) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.memberType = memberType;
        this.isActive = true;
        this.membershipDate = new Date();
        this.currentLoansCount = 0;
        this.totalLoansCount = 0;
        this.lateReturnsCount = 0;
        
        // Logique métier dans le constructeur (mauvais !)
        if (memberType.equals("STUDENT")) {
            this.loanQuota = 3;
        } else if (memberType.equals("TEACHER")) {
            this.loanQuota = 10;
        } else if (memberType.equals("STAFF")) {
            this.loanQuota = 5;
        } else {
            this.loanQuota = 2;
        }
    }
    
    // Méthode avec logique métier dans le modèle (devrait être dans un service)
    public boolean canBorrow() {
        if (!isActive) {
            return false;
        }
        if (membershipExpiryDate != null && membershipExpiryDate.before(new Date())) {
            return false;
        }
        if (currentLoansCount >= loanQuota) {
            return false;
        }
        if (lateReturnsCount > 3) {
            return false;
        }
        return true;
    }
    
    // Méthode utilitaire dans le modèle (devrait être ailleurs)
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    // Méthode utilitaire dans le modèle (devrait être ailleurs)
    public String getFullAddress() {
        return streetAddress + ", " + zipCode + " " + city + ", " + country;
    }
    
    // Getters et setters (pas de validation)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getStreetAddress() { return streetAddress; }
    public void setStreetAddress(String streetAddress) { this.streetAddress = streetAddress; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    public Date getBirthDate() { return birthDate; }
    public void setBirthDate(Date birthDate) { this.birthDate = birthDate; }
    
    public Date getMembershipDate() { return membershipDate; }
    public void setMembershipDate(Date membershipDate) { this.membershipDate = membershipDate; }
    
    public Date getMembershipExpiryDate() { return membershipExpiryDate; }
    public void setMembershipExpiryDate(Date membershipExpiryDate) { 
        this.membershipExpiryDate = membershipExpiryDate; 
    }
    
    public String getMemberType() { return memberType; }
    public void setMemberType(String memberType) { this.memberType = memberType; }
    
    public int getLoanQuota() { return loanQuota; }
    public void setLoanQuota(int loanQuota) { this.loanQuota = loanQuota; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public int getCurrentLoansCount() { return currentLoansCount; }
    public void setCurrentLoansCount(int currentLoansCount) { 
        this.currentLoansCount = currentLoansCount; 
    }
    
    public int getTotalLoansCount() { return totalLoansCount; }
    public void setTotalLoansCount(int totalLoansCount) { 
        this.totalLoansCount = totalLoansCount; 
    }
    
    public int getLateReturnsCount() { return lateReturnsCount; }
    public void setLateReturnsCount(int lateReturnsCount) { 
        this.lateReturnsCount = lateReturnsCount; 
    }
    
    @Override
    public String toString() {
        return "Member[" + id + "] " + getFullName() + " (" + memberType + ")";
    }
}
