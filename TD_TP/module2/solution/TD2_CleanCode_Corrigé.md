# TD 2 — Solutions

## Exercice 1 : Chasse aux code smells

### Extrait 1 — Variables mystérieuses

**a) Noms problématiques :**

- `p` → constante de pénalité
- `d1`, `d2`, `d3` → durées d'emprunt
- `calc` → nom de méthode
- `m` → paramètre membre
- `d` → paramètre jours
- `r` → résultat
- `t` → type

**b) Noms explicites :**

```java
private static final double PENALTY_RATE_PER_DAY = 0.50;
private static final int STUDENT_LOAN_DURATION_DAYS = 14;
private static final int TEACHER_LOAN_DURATION_DAYS = 30;
private static final int DEFAULT_LOAN_DURATION_DAYS = 21;

public double calculatePenalty(Member member, int daysOverdue) {
    double penaltyAmount = 0;
    String memberType = member.getType();
    // ...
}
```

**c) Autre code smell :**

- **Switch/if sur le type** au lieu de polymorphisme
- Violation du principe **Open/Closed** (OCP)

---

### Extrait 2 — Méthode trop longue

**a) Responsabilités identifiées (7) :**

1. Récupération et validation du membre
2. Vérification que le membre est actif
3. Vérification de l'expiration de l'adhésion
4. Calcul des pénalités impayées
5. Vérification du quota d'emprunts
6. Détermination du quota max selon le type
7. (+ la suite : vérification du livre, création de l'emprunt...)

**b) Découpage proposé :**

```java
public String createLoan(String memberId, String bookId) {
    Member member = findMemberById(memberId);
    Book book = findBookById(bookId);

    validateMemberCanBorrow(member);
    validateBookIsAvailable(book);

    return createAndSaveLoan(member, book);
}

private void validateMemberCanBorrow(Member member) {
    ensureMemberIsActive(member);
    ensureMembershipNotExpired(member);
    ensureNoPendingPenalties(member);
    ensureLoanQuotaNotReached(member);
}

private int getMaxLoansForMemberType(String memberType) { ...}

private int countActiveLoans(String memberId) { ...}

private double calculateUnpaidPenalties(String memberId) { ...}
```

**c) Principe SOLID violé :**

- **SRP** (Single Responsibility Principle) : la méthode a trop de responsabilités
- Accessoirement **OCP** pour le switch sur les types

---

### Extrait 3 — God Class

**a) Responsabilités (7) :**

1. Gestion des livres (CRUD)
2. Gestion des membres (CRUD)
3. Gestion des emprunts
4. Calcul des pénalités
5. Envoi de notifications
6. Génération de rapports
7. Gestion des réservations

**b) Découpage en classes :**

- `BookService`
- `MemberService`
- `LoanService`
- `ReservationService`
- `PenaltyCalculator`
- `NotificationService`
- `ReportGenerator`

**c) Responsabilité unique de chaque classe :**

| Classe                | Responsabilité                              |
|-----------------------|---------------------------------------------|
| `BookService`         | CRUD des livres, recherche                  |
| `MemberService`       | CRUD des membres, validation                |
| `LoanService`         | Création, retour, renouvellement d'emprunts |
| `ReservationService`  | Gestion des réservations                    |
| `PenaltyCalculator`   | Calcul des pénalités de retard              |
| `NotificationService` | Envoi d'emails et rappels                   |
| `ReportGenerator`     | Génération des rapports statistiques        |

---

### Extrait 4 — Switch sur le type

**a) Problème si nouveau type :**

- Il faut modifier **3 méthodes** différentes
- Risque d'oublier l'un des switch
- Violation du principe **Open/Closed**

**b) Principe SOLID violé :**

- **OCP** (Open/Closed Principle) : le code n'est pas ouvert à l'extension sans modification

**c) Solution polymorphique :**

```java
// Interface
public interface MemberPolicy {
    double calculatePenalty(int daysOverdue, double baseRate);

    int getMaxLoans();

    int getLoanDurationDays();
}

// Implémentations
public class StudentPolicy implements MemberPolicy {
    public double calculatePenalty(int days, double rate) {
        return days * rate * 0.5;
    }

    public int getMaxLoans() {
        return 5;
    }

    public int getLoanDurationDays() {
        return 14;
    }
}

public class TeacherPolicy implements MemberPolicy {
    public double calculatePenalty(int days, double rate) {
        return 0;
    }

    public int getMaxLoans() {
        return 10;
    }

    public int getLoanDurationDays() {
        return 30;
    }
}

// Utilisation
public class PenaltyCalculator {
    public double calculate(Member member, int daysOverdue) {
        MemberPolicy policy = member.getPolicy(); // ou PolicyFactory
        return policy.calculatePenalty(daysOverdue, PENALTY_RATE_PER_DAY);
    }
}
```

---

### Extrait 5 — Commentaires compensatoires

**a) Pourquoi c'est un code smell :**

- Les commentaires **compensent un mauvais nommage**
- Si le code était bien écrit, les commentaires seraient inutiles
- Les commentaires peuvent devenir **obsolètes** et mentir

**b) Réécriture sans commentaires :**

```java
public boolean canMemberBorrowBook(String memberId, String bookId) {
    Member member = members.get(memberId);
    if (member == null) return false;
    if (!member.isActive()) return false;

    Book book = books.get(bookId);
    if (book == null) return false;
    if (!book.hasAvailableCopies()) return false;

    return true;
}
```

Ou version plus fluide :

```java
public boolean canMemberBorrowBook(String memberId, String bookId) {
    return memberExists(memberId)
            && memberIsActive(memberId)
            && bookExists(bookId)
            && bookHasAvailableCopies(bookId);
}
```

**c) Lignes de commentaires économisées :** 6 lignes (toutes !)

---

## Exercice 2 : Réécriture Clean Code

### Solution

```java
public class PenaltyCalculator {

    private static final double BASE_PENALTY_RATE_PER_DAY = 0.50;
    private static final double MAX_PENALTY_AMOUNT = 50.0;

    private static final double STUDENT_DISCOUNT = 0.5;
    private static final double STAFF_DISCOUNT = 0.75;
    private static final double EXTERNAL_SURCHARGE = 1.5;

    public double calculatePenalty(Member member, int daysOverdue) {
        if (daysOverdue <= 0) {
            return 0;
        }

        double penalty = computeRawPenalty(member, daysOverdue);
        penalty = applyMaximumCap(penalty);
        return roundToTwoDecimals(penalty);
    }

    private double computeRawPenalty(Member member, int daysOverdue) {
        double rate = getEffectiveRateForMember(member);
        return daysOverdue * rate;
    }

    private double getEffectiveRateForMember(Member member) {
        return switch (member.getType()) {
            case "STUDENT" -> BASE_PENALTY_RATE_PER_DAY * STUDENT_DISCOUNT;
            case "TEACHER" -> 0;
            case "STAFF" -> BASE_PENALTY_RATE_PER_DAY * STAFF_DISCOUNT;
            case "EXTERNAL" -> BASE_PENALTY_RATE_PER_DAY * EXTERNAL_SURCHARGE;
            default -> BASE_PENALTY_RATE_PER_DAY;
        };
    }

    private double applyMaximumCap(double penalty) {
        return Math.min(penalty, MAX_PENALTY_AMOUNT);
    }

    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
```

### Améliorations apportées :

1. ✅ Constantes nommées explicitement
2. ✅ Méthodes extraites avec noms révélant l'intention
3. ✅ Commentaires supprimés (code auto-documenté)
4. ✅ Validation des entrées (daysOverdue <= 0)
5. ✅ DRY respecté (taux de base en constante)

---

## Exercice 3 : Lecture rapport SonarQube

### a) Priorisation

| Priorité | Problème                    | Justification                                        |
|----------|-----------------------------|------------------------------------------------------|
| **1**    | Mot de passe en dur         | **Sécurité critique** - risque immédiat              |
| **2**    | SQL injection               | **Sécurité** - faille exploitable                    |
| **3**    | Couverture tests 23%        | **Filet de sécurité** - prérequis au refactoring     |
| **4**    | Méthode 127 lignes          | **Maintenabilité** - difficile à comprendre/modifier |
| **5**    | Complexité 23 / Duplication | **Dette technique** - à traiter progressivement      |

### b) Plan d'action

| Problème           | Action corrective                                    |
|--------------------|------------------------------------------------------|
| Null pointer       | Ajouter des null checks ou utiliser `Optional`       |
| SQL injection      | Utiliser des `PreparedStatement` avec paramètres     |
| Hardcoded password | Externaliser dans variables d'environnement ou vault |
| God Class          | Extract Class : séparer en services dédiés           |
| Méthode longue     | Extract Method : découper en sous-méthodes           |

### c) Quality Gate

**Objectifs réalistes pour Sprint 1 :**

| Métrique              | Actuel | Cible Sprint 1 | Cible finale |
|-----------------------|--------|----------------|--------------|
| Bugs critiques        | 8      | 0              | 0            |
| Vulnerabilities       | 3      | 0              | 0            |
| Coverage              | 23%    | 50%            | 80%          |
| Duplication           | 8.7%   | 5%             | 3%           |
| Code Smells critiques | 12     | 6              | 0            |

**Priorité :** Sécurité d'abord (bugs + vulnérabilités), puis couverture de tests pour sécuriser le refactoring.
