package fr.amu.univ.miage.m1.glq.util;

import java.util.regex.Pattern;

/**
 * Utilitaires de validation.
 * 
 */
public class ValidationUtils {
    
    // Regex - problème : recompilées à chaque validation
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String ISBN_10_REGEX = "^\\d{10}$";
    private static final String ISBN_13_REGEX = "^\\d{13}$";
    private static final String PHONE_REGEX = "^[0-9]{10}$";
    
    private ValidationUtils() {
    }
    
    /**
     * Valide une adresse email.
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return Pattern.matches(EMAIL_REGEX, email);
    }
    
    /**
     * Valide un ISBN (10 ou 13 chiffres).
     */
    public static boolean isValidIsbn(String isbn) {
        if (isbn == null || isbn.isEmpty()) {
            return false;
        }
        // Enlever les tirets
        String cleanIsbn = isbn.replace("-", "");
        return Pattern.matches(ISBN_10_REGEX, cleanIsbn) || 
               Pattern.matches(ISBN_13_REGEX, cleanIsbn);
    }
    
    /**
     * Valide un numéro de téléphone.
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        String cleanPhone = phone.replaceAll("[\\s.-]", "");
        return Pattern.matches(PHONE_REGEX, cleanPhone);
    }
    
    /**
     * Valide un code postal français.
     */
    public static boolean isValidZipCode(String zipCode) {
        if (zipCode == null || zipCode.isEmpty()) {
            return false;
        }
        return Pattern.matches("^[0-9]{5}$", zipCode);
    }
    
    /**
     * Valide qu'une chaîne n'est pas vide.
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
    
    /**
     * Valide qu'un entier est positif.
     */
    public static boolean isPositive(int value) {
        return value > 0;
    }
    
    /**
     * Valide qu'un entier est dans une plage.
     */
    public static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }
    
    /**
     * Valide une année de publication (entre 1450 et année courante).
     */
    public static boolean isValidPublicationYear(int year) {
        int currentYear = java.time.Year.now().getValue();
        return year >= 1450 && year <= currentYear;
    }
    
    /**
     * Valide le type de membre.
     */
    public static boolean isValidMemberType(String type) {
        if (type == null) {
            return false;
        }
        return type.equals("STUDENT") || 
               type.equals("TEACHER") || 
               type.equals("STAFF") || 
               type.equals("EXTERNAL");
    }
    
    /**
     * Valide la catégorie de livre.
     */
    public static boolean isValidBookCategory(String category) {
        if (category == null) {
            return false;
        }
        return category.equals("ROMAN") ||
               category.equals("SCIENCE") ||
               category.equals("TECHNIQUE") ||
               category.equals("HISTOIRE") ||
               category.equals("JEUNESSE") ||
               category.equals("BD") ||
               category.equals("AUTRE");
    }
    
    /**
     * Message d'erreur pour email invalide.
     */
    public static String getEmailErrorMessage() {
        return "L'adresse email n'est pas valide";
    }
    
    /**
     * Message d'erreur pour ISBN invalide.
     */
    public static String getIsbnErrorMessage() {
        return "L'ISBN doit contenir 10 ou 13 chiffres";
    }
    
    /**
     * Message d'erreur pour champ obligatoire.
     */
    public static String getRequiredFieldMessage(String fieldName) {
        return "Le champ '" + fieldName + "' est obligatoire";
    }
}
