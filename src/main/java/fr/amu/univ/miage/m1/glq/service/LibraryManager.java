package fr.amu.univ.miage.m1.glq.service;

import fr.amu.univ.miage.m1.glq.model.Book;
import fr.amu.univ.miage.m1.glq.model.Loan;
import fr.amu.univ.miage.m1.glq.model.Member;
import fr.amu.univ.miage.m1.glq.model.Reservation;
import fr.amu.univ.miage.m1.glq.service.penalty.PenaltyCalculator;
import java.util.*;

/**
 * Gestionnaire principal de la bibliothèque.
 *
 * <p>⚠️ ATTENTION - CODE LEGACY ⚠️
 *
 * <p>- Accès base de données
 */
public class LibraryManager {

  private static final int MAX_RENEWALS = 2;
  private static final int STUDENT_LOAN_DURATION_DAYS = 14;
  private static final int TEACHER_LOAN_DURATION_DAYS = 30;
  private static final int DEFAULT_LOAN_DURATION_DAYS = 21;
  private static LibraryManager instance;
  private final BookService bookService = new BookService();
  private final MemberService memberService = new MemberService();
  private final PenaltyCalculator penaltyCalculator = new PenaltyCalculator();
  private final NotificationService notificationService = new NotificationService();
  private Map<String, Loan> loans = new HashMap<>();
  private Map<String, Reservation> reservations = new HashMap<>();
  private int loanIdCounter = 1;
  private int reservationIdCounter = 1;

  // Constructeur privé pour le singleton
  private LibraryManager() {
    // Initialisation avec quelques données de test
    initTestData();
  }

  // Singleton getInstance
  public static LibraryManager getInstance() {
    if (instance == null) {
      instance = new LibraryManager();
    }
    return instance;
  }

  // Pour les tests : reset l'instance
  public static void resetInstance() {
    instance = null;
  }

  // ==================== GESTION DES LIVRES ====================

  private Reservation getBookFirstReservation(List<Reservation> bookReservations) {
    // Trier par date
    bookReservations.sort((r1, r2) -> r1.getReservationDate().compareTo(r2.getReservationDate()));
    Reservation firstReservation = bookReservations.get(0);
    return firstReservation;
  }

  private void ensureMemberIsActive(Member member) {
    // Vérifier si le membre est actif
    if (!member.isActive()) {
      throw new RuntimeException("Le membre " + member.getId() + " n'est pas actif");
    }
  }

  private void ensureMembershipNotExpired(Member member) {
    // Vérifier si l'adhésion n'est pas expirée
    if (member.getMembershipExpiryDate() != null
        && member.getMembershipExpiryDate().before(new Date())) {
      throw new RuntimeException("L'adhésion du membre " + member.getId() + " a expiré");
    }
  }

  private void checkBookActiveStatus(Book book) {
    // Vérifier si le livre est actif
    if (!book.isActive()) {
      throw new RuntimeException("Le livre " + book.getId() + " n'est plus disponible au prêt");
    }
  }

  private void checkBookAvailability(Book book) {
    // Vérifier la disponibilité
    if (book.getAvailableCopies() <= 0) {
      // Proposer une réservation
      throw new RuntimeException("Aucun exemplaire disponible. Voulez-vous réserver ?");
    }
  }

  private void ensureBookIsNotReservedByAnotherMember(
      String memberId, Reservation firstReservation) {
    if (!firstReservation.getMemberId().equals(memberId)) {
      throw new RuntimeException("Ce livre est réservé par un autre membre");
    }
  }

  private void markReservationAsFullfilled(Reservation firstReservation) {
    // Marquer la réservation comme fulfilled
    firstReservation.setStatus("FULFILLED");
  }

  // ==================== GESTION DES MEMBRES ====================

  /** Ajoute un livre à la bibliothèque. */
  public String addBook(
      String title, String author, String isbn, int year, int copies, String category) {
    return bookService.addBook(title, author, isbn, year, copies, category);
  }

  public Book getBook(String id) {
    return bookService.getBook(id);
  }

  public Book getBookByIsbn(String isbn) {
    return bookService.getBookByIsbn(isbn);
  }

  public List<Book> getAllBooks() {
    return bookService.getAllBooks();
  }

  public List<Book> searchBooks(String query) {
    return bookService.searchBooks(query);
  }

  public void updateBook(Book book) {
    bookService.updateBook(book);
  }

  // ==================== GESTION DES EMPRUNTS ====================

  public void deleteBook(String id) {
    bookService.deleteBook(id);
  }

  /** Ajoute un membre à la bibliothèque. */
  public String addMember(String firstName, String lastName, String email, String type) {
    return memberService.addMember(firstName, lastName, email, type);
  }

  public Member getMember(String id) {
    return memberService.getMember(id);
  }

  public Member getMemberByEmail(String email) {
    return memberService.getMemberByEmail(email);
  }

  public List<Member> getAllMembers() {
    return memberService.getAllMembers();
  }

  public void updateMember(Member member) {
    memberService.updateMember(member);
  }

  public void deleteMember(String id) {
    memberService.deleteMember(id);
  }

  /** Crée un emprunt. */
  public String createLoan(String memberId, String bookId) {
    Member member = findMemberById(memberId);
    Book book = findBookById(bookId);

    validateMemberCanBorrow(member);
    validateBookIsAvailable(memberId, bookId, book);

    return createAndSaveLoan(member, book);
  }

  private Member findMemberById(String memberId) {
    // Récupérer le membre
    Member member = memberService.getMembers().get(memberId);
    if (member == null) {
      throw new RuntimeException("Membre non trouvé : " + memberId);
    }
    return member;
  }

  private Book findBookById(String bookId) {
    // Récupérer le livre
    Book book = bookService.getBooks().get(bookId);
    if (book == null) {
      throw new RuntimeException("Livre non trouvé : " + bookId);
    }
    return book;
  }

  private void validateMemberCanBorrow(Member member) {
    ensureMemberIsActive(member);
    ensureMembershipNotExpired(member);
    ensureNoPendingPenalties(member);
    ensureLoanQuotaNotReached(member);
  }

  private void ensureNoPendingPenalties(Member member) {
    calculateUnpaidPenalties(member);

    // Vérifier si le membre a trop de retards
    if (member.getLateReturnsCount() > 3) {
      throw new RuntimeException("Trop de retards dans l'historique");
    }
  }

  private void calculateUnpaidPenalties(Member member) {
    // Vérifier si le membre a des pénalités impayées
    double unpaidPenalties = 0;
    for (Loan loan : loans.values()) {
      if (loan.getMemberId().equals(member.getId()) && loan.getPenaltyAmount() > 0) {
        unpaidPenalties += loan.getPenaltyAmount();
      }
    }

    if (unpaidPenalties > 10.0) {
      throw new RuntimeException("Pénalités impayées trop élevées : " + unpaidPenalties + "€");
    }
  }

  private void ensureLoanQuotaNotReached(Member member) {
    int quota = getMAxLoansForMemberType(member);
    int activeLoans = countActiveLoans(member);

    if (activeLoans >= quota) {
      throw new RuntimeException("Quota d'emprunts atteint (" + activeLoans + "/" + quota + ")");
    }
  }

  private int getMAxLoansForMemberType(Member member) {
    // Vérifier le quota d'emprunts selon le type de membre
    int quota;
    String memberType = member.getMemberType();
    if (memberType.equals("STUDENT")) {
      quota = 3;
    } else if (memberType.equals("TEACHER")) {
      quota = 10;
    } else if (memberType.equals("STAFF")) {
      quota = 5;
    } else {
      quota = 2;
    }
    return quota;
  }

  private int countActiveLoans(Member member) {
    // Compter les emprunts actifs du membre
    int activeLoans = 0;
    for (Loan loan : loans.values()) {
      if (loan.getMemberId().equals(member.getId())
          && (loan.getStatus().equals("ACTIVE") || loan.getStatus().equals("OVERDUE"))) {
        activeLoans++;
      }
    }
    return activeLoans;
  }

  private void validateBookIsAvailable(String memberId, String bookId, Book book) {
    checkBookActiveStatus(book);
    checkBookAvailability(book);
    ensureMemberHasNotAlreadyBorrowedBook(memberId, bookId);
    ensureBookCanBeReservedByMemeber(memberId, bookId);
  }

  private void ensureMemberHasNotAlreadyBorrowedBook(String memberId, String bookId) {
    // Vérifier si le membre n'a pas déjà emprunté ce livre
    for (Loan loan : loans.values()) {
      if (loan.getMemberId().equals(memberId)
          && loan.getBookId().equals(bookId)
          && loan.getStatus().equals("ACTIVE")) {
        throw new RuntimeException("Vous avez déjà emprunté ce livre");
      }
    }
  }

  private void ensureBookCanBeReservedByMemeber(String memberId, String bookId) {
    List<Reservation> bookReservations = getBookPendingReservations(bookId);
    if (!bookReservations.isEmpty()) {
      Reservation firstReservation = getBookFirstReservation(bookReservations);
      ensureBookIsNotReservedByAnotherMember(memberId, firstReservation);
      markReservationAsFullfilled(firstReservation);
    }
  }

  private List<Reservation> getBookPendingReservations(String bookId) {
    // Vérifier les réservations
    List<Reservation> bookReservations = new ArrayList<>();
    for (Reservation res : reservations.values()) {
      if (res.getBookId().equals(bookId) && res.getStatus().equals("PENDING")) {
        bookReservations.add(res);
      }
    }
    return bookReservations;
  }

  private String createAndSaveLoan(Member member, Book book) {
    Loan createdLoan = createLoan(member, book);
    saveLoan(createdLoan);
    updateBookAvailableCopies(book);
    updateMemberLoanInformations(member);
    notificationService.sendNotification(member, book, createdLoan);

    return createdLoan.getId();
  }

  private Loan createLoan(Member member, Book book) {
    // Créer l'emprunt
    String loanId = "L" + String.format("%05d", loanIdCounter++);
    return new Loan(loanId, member, book);
  }

  private void saveLoan(Loan createdLoan) {
    loans.put(createdLoan.getId(), createdLoan);
  }

  private void updateBookAvailableCopies(Book book) {
    // Mettre à jour le stock
    book.setAvailableCopies(book.getAvailableCopies() - 1);
  }

  private void updateMemberLoanInformations(Member member) {
    // Mettre à jour les compteurs du membre
    member.setCurrentLoansCount(member.getCurrentLoansCount() + 1);
    member.setTotalLoansCount(member.getTotalLoansCount() + 1);
  }

  private void sendNotification(Member member, Book book, Loan loan) {
    // Envoyer une notification (simulation)
    notificationService.sendNotification(member, book, loan);
  }

  /** Retourne un livre. */
  public void returnLoan(String loanId) {
    Loan loan = loans.get(loanId);
    if (loan == null) {
      throw new RuntimeException("Emprunt non trouvé : " + loanId);
    }

    if (loan.getStatus().equals("RETURNED")) {
      throw new RuntimeException("Ce livre a déjà été retourné");
    }

    // Calculer les pénalités si en retard
    if (loan.isOverdue()) {
      int daysOverdue = loan.getDaysOverdue();
      double penalty = penaltyCalculator.calculatePenalty(loan.getMember(), daysOverdue);
      loan.setPenaltyAmount(penalty);

      // Incrémenter le compteur de retards
      Member member = loan.getMember();
      member.setLateReturnsCount(member.getLateReturnsCount() + 1);

      // Notification de pénalité
      notificationService.sendNotification(
          member.getEmail(),
          "Retour en retard",
          "Votre retour est en retard de "
              + daysOverdue
              + " jours. "
              + "Pénalité : "
              + penalty
              + "€");
    }

    // Mettre à jour le statut
    loan.setStatus("RETURNED");
    loan.setReturnDate(new Date());

    // Remettre le livre en stock
    Book book = loan.getBook();
    book.setAvailableCopies(book.getAvailableCopies() + 1);

    // Mettre à jour le compteur du membre
    Member member = loan.getMember();
    member.setCurrentLoansCount(member.getCurrentLoansCount() - 1);

    // Vérifier s'il y a des réservations en attente
    notifyNextReservation(book.getId());

    System.out.println("Retour effectué : " + loan);
  }

  /** Calcule la pénalité de retard. */
  public double calculatePenalty(Member member, int daysOverdue) {

    return penaltyCalculator.calculatePenalty(member, daysOverdue);
  }

  /** Renouvelle un emprunt. */
  public void renewLoan(String loanId) {
    Loan loan = loans.get(loanId);
    if (loan == null) {
      throw new RuntimeException("Emprunt non trouvé : " + loanId);
    }

    if (!loan.getStatus().equals("ACTIVE")) {
      throw new RuntimeException("Seuls les emprunts actifs peuvent être renouvelés");
    }

    if (loan.isOverdue()) {
      throw new RuntimeException("Impossible de renouveler un emprunt en retard");
    }

    if (loan.getRenewalCount() >= MAX_RENEWALS) {
      throw new RuntimeException("Nombre maximum de renouvellements atteint");
    }

    // Vérifier s'il y a des réservations
    for (Reservation res : reservations.values()) {
      if (res.getBookId().equals(loan.getBookId()) && res.getStatus().equals("PENDING")) {
        throw new RuntimeException("Renouvellement impossible : livre réservé");
      }
    }

    loan.renew();

    notificationService.sendNotification(
        loan.getMember().getEmail(),
        "Emprunt renouvelé",
        "Votre emprunt a été renouvelé. Nouvelle date de retour : " + loan.getDueDate());

    System.out.println("Emprunt renouvelé : " + loan);
  }

  public Loan getLoan(String id) {
    return loans.get(id);
  }

  public List<Loan> getAllLoans() {
    return new ArrayList<>(loans.values());
  }

  public List<Loan> getMemberLoans(String memberId) {
    List<Loan> memberLoans = new ArrayList<>();
    for (Loan loan : loans.values()) {
      if (loan.getMemberId().equals(memberId)) {
        memberLoans.add(loan);
      }
    }
    return memberLoans;
  }

  public List<Loan> getActiveLoans() {
    List<Loan> activeLoans = new ArrayList<>();
    for (Loan loan : loans.values()) {
      if (loan.getStatus().equals("ACTIVE") || loan.getStatus().equals("OVERDUE")) {
        activeLoans.add(loan);
      }
    }
    return activeLoans;
  }

  public List<Loan> getOverdueLoans() {
    List<Loan> overdueLoans = new ArrayList<>();
    for (Loan loan : loans.values()) {
      if (loan.isOverdue()) {
        overdueLoans.add(loan);
      }
    }
    return overdueLoans;
  }

  // ==================== GESTION DES RÉSERVATIONS ====================

  public String createReservation(String memberId, String bookId) {
    Member member = memberService.getMembers().get(memberId);
    if (member == null) {
      throw new RuntimeException("Membre non trouvé");
    }

    Book book = bookService.getBooks().get(bookId);
    if (book == null) {
      throw new RuntimeException("Livre non trouvé");
    }

    // Vérifier si le membre n'a pas déjà réservé ce livre
    for (Reservation res : reservations.values()) {
      if (res.getMemberId().equals(memberId)
          && res.getBookId().equals(bookId)
          && res.getStatus().equals("PENDING")) {
        throw new RuntimeException("Vous avez déjà une réservation pour ce livre");
      }
    }

    String id = "R" + String.format("%05d", reservationIdCounter++);
    Reservation reservation = new Reservation(id, memberId, bookId);

    // Calculer la position dans la file
    int position = 1;
    for (Reservation res : reservations.values()) {
      if (res.getBookId().equals(bookId) && res.getStatus().equals("PENDING")) {
        position++;
      }
    }
    reservation.setQueuePosition(position);

    reservations.put(id, reservation);

    notificationService.sendNotification(
        member.getEmail(),
        "Réservation confirmée",
        "Vous avez réservé : " + book.getTitle() + ". Position dans la file : " + position);

    System.out.println(
        "Réservation créée : " + reservation.getId() + " (position " + position + ")");

    return id;
  }

  public void cancelReservation(String reservationId) {
    Reservation reservation = reservations.get(reservationId);
    if (reservation == null) {
      throw new RuntimeException("Réservation non trouvée");
    }

    reservation.setStatus("CANCELLED");

    // Mettre à jour les positions
    String bookId = reservation.getBookId();
    int cancelledPosition = reservation.getQueuePosition();

    for (Reservation res : reservations.values()) {
      if (res.getBookId().equals(bookId)
          && res.getStatus().equals("PENDING")
          && res.getQueuePosition() > cancelledPosition) {
        res.setQueuePosition(res.getQueuePosition() - 1);
      }
    }
  }

  private void notifyNextReservation(String bookId) {
    Reservation nextReservation = null;

    for (Reservation res : reservations.values()) {
      if (res.getBookId().equals(bookId)
          && res.getStatus().equals("PENDING")
          && res.getQueuePosition() == 1) {
        nextReservation = res;
        break;
      }
    }

    if (nextReservation != null) {
      Member member = memberService.getMembers().get(nextReservation.getMemberId());
      Book book = bookService.getBooks().get(bookId);

      notificationService.sendNotification(
          member.getEmail(),
          "Livre disponible",
          "Le livre que vous avez réservé est disponible : "
              + book.getTitle()
              + ". Vous avez 7 jours pour venir le chercher.");
    }
  }

  // ==================== NOTIFICATIONS ====================

  /** Envoie une notification (simulation). */
  private void sendNotification(String email, String subject, String body) {
    // Simulation d'envoi d'email
    notificationService.sendNotification(email, subject, body);
  }

  /** Envoie les rappels pour les emprunts qui arrivent à échéance. */
  public void sendDueReminders() {
    Date today = new Date();
    long threeDaysMs = 3L * 24 * 60 * 60 * 1000;

    for (Loan loan : loans.values()) {
      if (loan.getStatus().equals("ACTIVE")) {
        long timeUntilDue = loan.getDueDate().getTime() - today.getTime();

        if (timeUntilDue > 0 && timeUntilDue <= threeDaysMs) {
          int daysLeft = (int) (timeUntilDue / (24 * 60 * 60 * 1000));
          Member member = loan.getMember();
          Book book = loan.getBook();

          notificationService.sendNotification(
              member.getEmail(),
              "Rappel : retour proche",
              "Votre emprunt de '"
                  + book.getTitle()
                  + "' doit être rendu dans "
                  + daysLeft
                  + " jour(s).");
        }
      }
    }
  }

  /** Envoie les notifications de retard. */
  public void sendOverdueNotifications() {
    for (Loan loan : loans.values()) {
      if (loan.isOverdue() && loan.getStatus().equals("ACTIVE")) {
        // Marquer comme en retard
        loan.setStatus("OVERDUE");

        Member member = loan.getMember();
        Book book = loan.getBook();
        int daysOverdue = loan.getDaysOverdue();
        double penalty = penaltyCalculator.calculatePenalty(member, daysOverdue);

        notificationService.sendNotification(
            member.getEmail(),
            "RETARD : " + book.getTitle(),
            "Votre emprunt est en retard de "
                + daysOverdue
                + " jours. "
                + "Pénalité actuelle : "
                + penalty
                + "€. "
                + "Merci de retourner le livre au plus vite.");
      }
    }
  }

  // ==================== RAPPORTS ====================

  /** Génère un rapport des emprunts. */
  public String generateLoanReport() {
    StringBuilder report = new StringBuilder();
    report.append("========== RAPPORT DES EMPRUNTS ==========\n");
    report.append("Date : ").append(new Date()).append("\n\n");

    // Statistiques globales
    int totalLoans = loans.size();
    int activeLoans = 0;
    int overdueLoans = 0;
    int returnedLoans = 0;
    double totalPenalties = 0;

    for (Loan loan : loans.values()) {
      if (loan.getStatus().equals("ACTIVE")) {
        activeLoans++;
      } else if (loan.getStatus().equals("OVERDUE")) {
        overdueLoans++;
      } else if (loan.getStatus().equals("RETURNED")) {
        returnedLoans++;
      }
      totalPenalties += loan.getPenaltyAmount();
    }

    report.append("STATISTIQUES GLOBALES\n");
    report.append("---------------------\n");
    report.append("Total emprunts : ").append(totalLoans).append("\n");
    report.append("Emprunts actifs : ").append(activeLoans).append("\n");
    report.append("Emprunts en retard : ").append(overdueLoans).append("\n");
    report.append("Emprunts retournés : ").append(returnedLoans).append("\n");
    report.append("Total pénalités : ").append(totalPenalties).append("€\n\n");

    // Top 5 des livres les plus empruntés
    Map<String, Integer> bookLoanCounts = new HashMap<>();
    for (Loan loan : loans.values()) {
      String bookId = loan.getBookId();
      bookLoanCounts.put(bookId, bookLoanCounts.getOrDefault(bookId, 0) + 1);
    }

    List<Map.Entry<String, Integer>> sortedBooks = new ArrayList<>(bookLoanCounts.entrySet());
    sortedBooks.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

    report.append("TOP 5 LIVRES LES PLUS EMPRUNTÉS\n");
    report.append("-------------------------------\n");
    int count = 0;
    for (Map.Entry<String, Integer> entry : sortedBooks) {
      if (count >= 5) break;
      Book book = bookService.getBooks().get(entry.getKey());
      if (book != null) {
        report
            .append(count + 1)
            .append(". ")
            .append(book.getTitle())
            .append(" (")
            .append(entry.getValue())
            .append(" emprunts)\n");
      }
      count++;
    }

    report.append("\n");

    // Membres avec le plus de retards
    List<Member> membersWithLateReturns = new ArrayList<>();
    for (Member member : memberService.getMembers().values()) {
      if (member.getLateReturnsCount() > 0) {
        membersWithLateReturns.add(member);
      }
    }
    membersWithLateReturns.sort(
        (m1, m2) -> Integer.compare(m2.getLateReturnsCount(), m1.getLateReturnsCount()));

    report.append("MEMBRES AVEC RETARDS\n");
    report.append("--------------------\n");
    for (int i = 0; i < Math.min(5, membersWithLateReturns.size()); i++) {
      Member m = membersWithLateReturns.get(i);
      report
          .append(m.getFullName())
          .append(" : ")
          .append(m.getLateReturnsCount())
          .append(" retard(s)\n");
    }

    report.append("\n========================================\n");

    return report.toString();
  }

  /** Génère un rapport de l'inventaire. */
  public String generateInventoryReport() {
    StringBuilder report = new StringBuilder();
    report.append("========== INVENTAIRE ==========\n");
    report.append("Date : ").append(new Date()).append("\n\n");

    int totalBooks = bookService.getBooks().size();
    int totalCopies = 0;
    int availableCopies = 0;

    Map<String, Integer> categoryCount = new HashMap<>();

    for (Book book : bookService.getBooks().values()) {
      totalCopies += book.getCopies();
      availableCopies += book.getAvailableCopies();

      String cat = book.getCategory();
      categoryCount.put(cat, categoryCount.getOrDefault(cat, 0) + 1);
    }

    report.append("Nombre de titres : ").append(totalBooks).append("\n");
    report.append("Nombre total d'exemplaires : ").append(totalCopies).append("\n");
    report.append("Exemplaires disponibles : ").append(availableCopies).append("\n\n");

    report.append("PAR CATÉGORIE\n");
    report.append("-------------\n");
    for (Map.Entry<String, Integer> entry : categoryCount.entrySet()) {
      report.append(entry.getKey()).append(" : ").append(entry.getValue()).append(" titres\n");
    }

    report.append("\n================================\n");

    return report.toString();
  }

  // ==================== INITIALISATION DONNÉES DE TEST ====================

  private void initTestData() {
    // Quelques livres
    bookService.addBook("Clean Code", "Robert C. Martin", "978-0132350884", 2008, 3, "TECHNIQUE");
    bookService.addBook("Design Patterns", "Gang of Four", "978-0201633610", 1994, 2, "TECHNIQUE");
    bookService.addBook("Refactoring", "Martin Fowler", "978-0134757599", 2018, 2, "TECHNIQUE");
    bookService.addBook(
        "The Pragmatic Programmer", "Hunt & Thomas", "978-0135957059", 2019, 2, "TECHNIQUE");
    bookService.addBook("1984", "George Orwell", "978-0451524935", 1949, 5, "ROMAN");
    bookService.addBook(
        "Le Petit Prince", "Antoine de Saint-Exupéry", "978-2070612758", 1943, 4, "ROMAN");

    // Quelques membres
    memberService.addMember("Alice", "Martin", "alice.martin@univ.fr", "STUDENT");
    memberService.addMember("Bob", "Dupont", "bob.dupont@univ.fr", "STUDENT");
    memberService.addMember("Claire", "Durand", "claire.durand@univ.fr", "TEACHER");
    memberService.addMember("David", "Bernard", "david.bernard@univ.fr", "STAFF");
  }
}
