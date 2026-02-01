# TP 2 — Nettoyer BiblioTech

## Génie Logiciel et Qualité — M1 MIAGE

**Durée : 1h30 | Projet : BiblioTech**

---

## Objectifs du TP

- Configurer les **outils d'analyse statique** (Checkstyle, PMD)
- Corriger les **violations** identifiées
- Écrire des **tests d'architecture** avec ArchUnit
- Appliquer les **principes Clean Code** sur du code réel

---

## Prérequis

- JDK 17+
- Maven 3.8+
- IntelliJ IDEA (recommandé) ou Eclipse
- Projet BiblioTech cloné et importé

---

## Partie 1 : Configuration des outils (20 min)

### 1.1 Ajouter les plugins Maven

Ouvrez le fichier `pom.xml` et ajoutez les plugins suivants dans la section `<build><plugins>` :

```xml
<!-- Checkstyle -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
    <version>3.3.1</version>
    <configuration>
        <configLocation>checkstyle.xml</configLocation>
        <consoleOutput>true</consoleOutput>
        <failsOnError>false</failsOnError>
    </configuration>
</plugin>

        <!-- PMD -->
<plugin>
<groupId>org.apache.maven.plugins</groupId>
<artifactId>maven-pmd-plugin</artifactId>
<version>3.21.2</version>
<configuration>
    <rulesets>
        <ruleset>pmd-rules.xml</ruleset>
    </rulesets>
    <failOnViolation>false</failOnViolation>
</configuration>
</plugin>
```

### 1.2 Créer le fichier checkstyle.xml

Créez le fichier `checkstyle.xml` à la racine du projet :

```xml
<?xml version="1.0"?>
<!DOCTYPE module PUBLIC
        "-//Checkstyle//DTD Checkstyle Configuration 1.3//EN"
        "https://checkstyle.org/dtds/configuration_1_3.dtd">

<module name="Checker">
    <module name="TreeWalker">
        <!-- Nommage -->
        <module name="ConstantName"/>
        <module name="LocalVariableName"/>
        <module name="MemberName"/>
        <module name="MethodName"/>
        <module name="ParameterName"/>
        <module name="TypeName"/>

        <!-- Taille -->
        <module name="MethodLength">
            <property name="max" value="30"/>
        </module>
        <module name="ParameterNumber">
            <property name="max" value="4"/>
        </module>

        <!-- Complexité -->
        <module name="CyclomaticComplexity">
            <property name="max" value="10"/>
        </module>

        <!-- Imports -->
        <module name="UnusedImports"/>
        <module name="AvoidStarImport"/>

        <!-- Bonnes pratiques -->
        <module name="EmptyBlock"/>
        <module name="NeedBraces"/>
        <module name="MagicNumber">
            <property name="ignoreNumbers" value="-1, 0, 1, 2"/>
        </module>
    </module>

    <!-- Longueur des fichiers -->
    <module name="FileLength">
        <property name="max" value="500"/>
    </module>
</module>
```

### 1.3 Créer le fichier pmd-rules.xml

Créez le fichier `pmd-rules.xml` à la racine du projet :

```xml
<?xml version="1.0"?>
<ruleset name="BiblioTech Rules"
         xmlns="http://pmd.sourceforge.net/ruleset/2.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://pmd.sourceforge.net/ruleset/2.0.0 
                        https://pmd.sourceforge.io/ruleset_2_0_0.xsd">

    <description>Règles PMD pour BiblioTech</description>

    <!-- Best Practices -->
    <rule ref="category/java/bestpractices.xml/UnusedLocalVariable"/>
    <rule ref="category/java/bestpractices.xml/UnusedPrivateField"/>
    <rule ref="category/java/bestpractices.xml/UnusedPrivateMethod"/>

    <!-- Code Style -->
    <rule ref="category/java/codestyle.xml/ShortVariable">
        <properties>
            <property name="minimum" value="3"/>
        </properties>
    </rule>
    <rule ref="category/java/codestyle.xml/LongVariable">
        <properties>
            <property name="minimum" value="30"/>
        </properties>
    </rule>

    <!-- Design -->
    <rule ref="category/java/design.xml/GodClass"/>
    <rule ref="category/java/design.xml/TooManyMethods">
        <properties>
            <property name="maxmethods" value="20"/>
        </properties>
    </rule>
    <rule ref="category/java/design.xml/ExcessiveMethodLength">
        <properties>
            <property name="minimum" value="30"/>
        </properties>
    </rule>
    <rule ref="category/java/design.xml/CyclomaticComplexity">
        <properties>
            <property name="methodReportLevel" value="10"/>
        </properties>
    </rule>

    <!-- Error Prone -->
    <rule ref="category/java/errorprone.xml/EmptyCatchBlock"/>
    <rule ref="category/java/errorprone.xml/AvoidDuplicateLiterals"/>
</ruleset>
```

### 1.4 Lancer l'analyse

Exécutez les commandes suivantes et notez le nombre de violations :

```bash
# Checkstyle
mvn checkstyle:check

# PMD  
mvn pmd:check

# Rapport complet
mvn site
```

**📝 À rendre :** Capture d'écran ou copie du résumé des violations.

| Outil      | Violations | Catégorie principale |
|------------|------------|----------------------|
| Checkstyle | ___        | ___                  |
| PMD        | ___        | ___                  |

---

## Partie 2 : Corrections guidées (40 min)

### 2.1 Renommer les variables (10 min)

Dans `LibraryManager.java`, renommez les éléments suivants :

| Avant                 | Après                         | Justification      |
|-----------------------|-------------------------------|--------------------|
| `p` (constante)       | `PENALTY_RATE_PER_DAY`        | Révèle l'intention |
| `d1`, `d2`, `d3`      | `STUDENT_LOAN_DURATION`, etc. | Explicite          |
| `calc()`              | `calculatePenalty()`          | Verbe + intention  |
| `chk()`               | `canMemberBorrowBook()`       | Question booléenne |
| `m`, `b` (paramètres) | `member`, `book`              | Lisibilité         |

**Raccourci IntelliJ :** `Shift + F6` pour renommer en toute sécurité.

### 2.2 Extraire des méthodes (15 min)

Dans la méthode `createLoan()`, extrayez les méthodes suivantes :

```java
// AVANT : tout dans createLoan()

// APRÈS : décomposition
public String createLoan(String memberId, String bookId) {
    Member member = findMemberOrThrow(memberId);
    Book book = findBookOrThrow(bookId);

    validateMemberCanBorrow(member);
    validateBookAvailable(book);

    return processLoanCreation(member, book);
}

private Member findMemberOrThrow(String memberId) {
    // À implémenter
}

private void validateMemberCanBorrow(Member member) {
    validateMemberIsActive(member);
    validateMembershipNotExpired(member);
    validateNoPendingPenalties(member);
    validateLoanQuotaNotReached(member);
}

private void validateMemberIsActive(Member member) {
    // À implémenter
}

// etc.
```

**📝 À faire :**

1. Utilisez `Ctrl + Alt + M` pour extraire chaque bloc
2. Donnez des noms qui révèlent l'intention
3. Vérifiez que les tests passent après chaque extraction

### 2.3 Appliquer SRP sur `LibraryManager` (15 min)

Créez les classes suivantes en déplaçant le code approprié :

#### `BookService.java`

```java
package fr.amu.univ.miage.m1.glq.service;

public class BookService {
    private Map<String, Book> books = new HashMap<>();

    public String addBook(String title, String author, String isbn,
                          int year, int copies, String category) {
        // Déplacer le code de LibraryManager.addBook()
    }

    public Book getBook(String id) { /* ... */ }

    public Book getBookByIsbn(String isbn) { /* ... */ }

    public List<Book> getAllBooks() { /* ... */ }

    public List<Book> searchBooks(String query) { /* ... */ }

    public void updateBook(Book book) { /* ... */ }

    public void deleteBook(String id) { /* ... */ }
}
```

#### `MemberService.java`

```java
package fr.amu.univ.miage.m1.glq.service;

public class MemberService {
    private Map<String, Member> members = new HashMap<>();

    public String addMember(String firstName, String lastName,
                            String email, String type) {
        // Déplacer le code de LibraryManager.addMember()
    }

    public Member getMember(String id) { /* ... */ }
    // etc.
}
```

#### `PenaltyCalculator.java`

```java
package fr.amu.univ.miage.m1.glq.service;

public class PenaltyCalculator {
    private static final double PENALTY_RATE_PER_DAY = 0.50;
    private static final double MAX_PENALTY = 50.0;

    public double calculate(Member member, int daysOverdue) {
        // Déplacer et refactorer calculatePenalty()
    }
}
```

**📝 À faire :**

1. Créez les 3 classes dans le package `fr.amu.univ.miage.m1.glq.service`
2. Utilisez `F6` pour déplacer les méthodes
3. Mettez à jour les références dans `LibraryManager`
4. Relancez les tests

---

## Partie 3 : Tests d'architecture avec ArchUnit (30 min)

### 3.1 Ajouter la dépendance ArchUnit

Dans `pom.xml`, ajoutez :

```xml

<dependency>
    <groupId>com.tngtech.archunit</groupId>
    <artifactId>archunit-junit5</artifactId>
    <version>1.2.1</version>
    <scope>test</scope>
</dependency>
```

### 3.2 Créer la classe de test

Créez `src/test/java/com/bibliotech/architecture/ArchitectureTest.java` :

```java
package fr.amu.univ.miage.m1.glq.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

class ArchitectureTest {

    private static JavaClasses classes;

    @BeforeAll
    static void setUp() {
        classes = new ClassFileImporter()
                .importPackages("fr.amu.univ.miage.m1.glq");
    }

    // ══════════════════════════════════════════════════════════════
    // TEST 1 : Les services ne doivent pas dépendre de la couche DB
    // ══════════════════════════════════════════════════════════════

    @Test
    void services_should_not_depend_on_database_layer() {
        // TODO : Compléter cette règle
        ArchRule rule = noClasses()
                .that().resideInAPackage("..service..")
                .should().dependOnClassesThat()
                .resideInAPackage("..db..");

        // Note : Ce test échouera probablement !
        // C'est normal, le code legacy viole cette règle.
        // rule.check(classes);
    }

    // ══════════════════════════════════════════════════════════════
    // TEST 2 : Les modèles ne doivent pas avoir de dépendances
    // ══════════════════════════════════════════════════════════════

    @Test
    void models_should_not_have_dependencies_to_services() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..model..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..service..", "..db..");

        rule.check(classes);
    }

    // ══════════════════════════════════════════════════════════════
    // TEST 3 : Pas d'injection de champs (préférer constructeur)
    // ══════════════════════════════════════════════════════════════

    @Test
    void no_field_injection() {
        ArchRule rule = noFields()
                .should().beAnnotatedWith("org.springframework.beans.factory.annotation.Autowired")
                .orShould().beAnnotatedWith("javax.inject.Inject");

        rule.check(classes);
    }

    // ══════════════════════════════════════════════════════════════
    // TODO : Ajouter vos propres règles
    // ══════════════════════════════════════════════════════════════

    @Test
    void services_should_have_service_suffix() {
        // TODO : Écrire une règle qui vérifie que les classes
        // dans le package "service" ont un nom se terminant par "Service"
        // ou "Manager" ou "Calculator"

        ArchRule rule = classes()
                .that().resideInAPackage("..service..")
                .should().haveSimpleNameEndingWith("Service")
                .orShould().haveSimpleNameEndingWith("Manager")
                .orShould().haveSimpleNameEndingWith("Calculator");

        // rule.check(classes);
    }

    @Test
    void repository_classes_should_only_be_accessed_by_services() {
        // TODO : Écrire une règle qui vérifie que les classes
        // contenant "Repository" ne sont appelées que depuis "service"

        // Indice : utiliser onlyBeAccessed().byAnyPackage()
    }

    @Test
    void no_cycles_between_packages() {
        // TODO : Vérifier qu'il n'y a pas de dépendances cycliques
        // Indice : utiliser slices().matching("fr.amu.univ.miage.m1.glq.(*)..").should().beFreeOfCycles()
    }
}
```

### 3.3 Travail demandé

1. **Complétez** les règles marquées TODO
2. **Exécutez** les tests : `mvn test -Dtest=ArchitectureTest`
3. **Analysez** les échecs (certains sont attendus sur du code legacy)
4. **Documentez** les violations trouvées

**📝 À rendre :**

| Règle                   | Statut | Violations                                  |
|-------------------------|--------|---------------------------------------------|
| Services sans DB        | ❌      | LibraryManager dépend de DatabaseConnection |
| Models sans dépendances | ✅      | -                                           |
| Pas de field injection  | ✅      | -                                           |
| Suffix Service          | ?      |                                             |
| Pas de cycles           | ?      |                                             |

---

## Livrables

À la fin du TP, vous devez avoir :

1. ✅ Fichiers `checkstyle.xml` et `pmd-rules.xml` configurés
2. ✅ Rapport des violations initiales
3. ✅ Variables et méthodes renommées
4. ✅ Méthode `createLoan()` décomposée
5. ✅ Classes `BookService`, `MemberService`, `PenaltyCalculator` créées
6. ✅ Tests ArchUnit complétés
7. ✅ Tous les tests existants passent toujours

---

## Barème indicatif

| Partie               | Points     |
|----------------------|------------|
| Configuration outils | 4 pts      |
| Renommage            | 4 pts      |
| Extract Method       | 5 pts      |
| SRP / Extract Class  | 5 pts      |
| ArchUnit             | 2 pts      |
| **Total**            | **20 pts** |

---

## Ressources

- 📖 Checkstyle Rules : https://checkstyle.sourceforge.io/checks.html
- 📖 PMD Rules : https://pmd.github.io/latest/pmd_rules_java.html
- 📖 ArchUnit Guide : https://www.archunit.org/userguide/html/000_Index.html
