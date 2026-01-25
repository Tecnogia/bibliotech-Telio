# BiblioTech Legacy

## Projet fil rouge - Génie Logiciel et Qualité (M1 MIAGE)

### 📚 Description

BiblioTech est un système de gestion de bibliothèque universitaire. Ce code représente une version **legacy** intentionnellement problématique, que vous allez améliorer tout au long du cours.

Le système permet de :
- Gérer un catalogue de livres
- Gérer les adhérents (étudiants, enseignants, personnel, externes)
- Gérer les emprunts et retours
- Gérer les réservations
- Calculer les pénalités de retard
- Générer des rapports

---

## ⚠️ Avertissement

**Ce code contient volontairement de nombreux problèmes !**

Il a été conçu pour illustrer les anti-patterns et mauvaises pratiques que vous apprendrez à identifier et corriger. Ne l'utilisez pas comme modèle pour vos propres projets.

---

## 🛠️ Commandes utiles

```bash
# Compiler le projet
mvn compile

# Exécuter les tests
mvn test

# Générer le rapport de couverture
mvn test jacoco:report
# Ouvrir target/site/jacoco/index.html

# Vérifier le style (Checkstyle)
mvn checkstyle:check

# Analyse SonarQube (si serveur disponible)
mvn sonar:sonar -Dsonar.host.url=http://localhost:9000
```

---

## 📁 Structure du projet

```
bibliotech-legacy/
├── pom.xml
├── README.md
└── src/
    ├── main/java/com/bibliotech/
    │   ├── model/
    │   │   ├── Book.java
    │   │   ├── Member.java
    │   │   ├── Loan.java
    │   │   └── Reservation.java
    │   ├── service/
    │   │   └── LibraryManager.java      
    │   ├── util/
    │   │   ├── DateUtils.java
    │   │   └── ValidationUtils.java
    │   └── db/
    │       └── DatabaseConnection.java
    └── test/java/com/bibliotech/
        └── LibraryManagerTest.java       
```

---

## 📊 Métriques initiales (à améliorer)

| Métrique | Valeur actuelle | Objectif |
|----------|-----------------|----------|
| Couverture de tests | < 20% | > 80% |
| Lignes de `LibraryManager` | ~600 | < 100 par classe |
| Complexité cyclomatique max | > 20 | < 10 |
| Duplication | > 10% | < 3% |
| Violations Checkstyle | > 100 | 0 |

---

## 📚 Ressources

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [AssertJ Documentation](https://assertj.github.io/doc/)
- [Refactoring Guru](https://refactoring.guru/refactoring)
- [Clean Code - Robert C. Martin](https://www.oreilly.com/library/view/clean-code-a/9780136083238/)

---

Cours Génie Logiciel et Qualité - M1 MIAGE

*Ce projet est fourni à des fins pédagogiques uniquement.*
