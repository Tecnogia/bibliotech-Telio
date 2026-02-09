package fr.amu.univ.miage.m1.glq.service;

import fr.amu.univ.miage.m1.glq.model.Book;
import fr.amu.univ.miage.m1.glq.model.Loan;
import fr.amu.univ.miage.m1.glq.model.Member;

public class NotificationService {
    public NotificationService() {
    }

    void sendNotification(Member member, Book book, Loan loan) {
        // Envoyer une notification (simulation)
        sendNotification(
                member.getEmail(),
                "Emprunt confirmé",
                "Vous avez emprunté : " + book.getTitle() + ". Date de retour : " + loan.getDueDate());

        System.out.println("Emprunt créé : " + loan);
    }

    /**
     * Envoie une notification (simulation).
     */
    void sendNotification(String email, String subject, String body) {
        // Simulation d'envoi d'email
        System.out.println("=== EMAIL ===");
        System.out.println("To: " + email);
        System.out.println("Subject: " + subject);
        System.out.println("Body: " + body);
        System.out.println("=============");
    }
}