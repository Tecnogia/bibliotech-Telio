package fr.amu.univ.miage.m1.glq.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestionnaire de connexion à la base de données.
 * 
 * ⚠️ PROBLÈMES À CORRIGER ⚠️
 *
 * Note : Dans cette version, on utilise le stockage en mémoire dans LibraryManager.
 * Cette classe est fournie pour montrer comment NE PAS faire l'accès BDD.
 */
public class DatabaseConnection {
    
    // Singleton instance (non thread-safe !)
    private static DatabaseConnection instance;
    
    // Configuration en dur (très mauvais pour la sécurité et la flexibilité)
    private static final String DB_URL = "jdbc:h2:mem:bibliotech;DB_CLOSE_DELAY=-1";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";
    
    private Connection connection;
    
    // Constructeur privé
    private DatabaseConnection() {
        try {
            // Chargement du driver (obsolète avec JDBC 4.0+)
            Class.forName("org.h2.Driver");
            this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            initializeSchema();
        } catch (Exception e) {
            // Mauvaise gestion : avale l'exception
            System.err.println("Erreur de connexion BDD : " + e.getMessage());
        }
    }
    
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    public Connection getConnection() {
        return connection;
    }
    
    /**
     * Initialise le schéma de la base de données.
     */
    private void initializeSchema() {
        String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS books (
                id VARCHAR(10) PRIMARY KEY,
                title VARCHAR(255) NOT NULL,
                author VARCHAR(255),
                isbn VARCHAR(20),
                year INT,
                copies INT DEFAULT 1,
                available_copies INT DEFAULT 1,
                category VARCHAR(50),
                is_active BOOLEAN DEFAULT TRUE
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS members (
                id VARCHAR(10) PRIMARY KEY,
                first_name VARCHAR(100) NOT NULL,
                last_name VARCHAR(100) NOT NULL,
                email VARCHAR(255),
                phone VARCHAR(20),
                street_address VARCHAR(255),
                city VARCHAR(100),
                zip_code VARCHAR(10),
                country VARCHAR(100),
                birth_date DATE,
                membership_date DATE,
                membership_expiry_date DATE,
                member_type VARCHAR(20),
                loan_quota INT DEFAULT 3,
                is_active BOOLEAN DEFAULT TRUE,
                current_loans_count INT DEFAULT 0,
                total_loans_count INT DEFAULT 0,
                late_returns_count INT DEFAULT 0
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS loans (
                id VARCHAR(10) PRIMARY KEY,
                member_id VARCHAR(10) NOT NULL,
                book_id VARCHAR(10) NOT NULL,
                loan_date DATE NOT NULL,
                due_date DATE NOT NULL,
                return_date DATE,
                status VARCHAR(20) DEFAULT 'ACTIVE',
                penalty_amount DECIMAL(10,2) DEFAULT 0,
                renewal_count INT DEFAULT 0,
                notes TEXT,
                FOREIGN KEY (member_id) REFERENCES members(id),
                FOREIGN KEY (book_id) REFERENCES books(id)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS reservations (
                id VARCHAR(10) PRIMARY KEY,
                member_id VARCHAR(10) NOT NULL,
                book_id VARCHAR(10) NOT NULL,
                reservation_date DATE NOT NULL,
                expiry_date DATE,
                status VARCHAR(20) DEFAULT 'PENDING',
                queue_position INT DEFAULT 1,
                FOREIGN KEY (member_id) REFERENCES members(id),
                FOREIGN KEY (book_id) REFERENCES books(id)
            )
            """
        };
        
        try {
            for (String sql : createStatements) {
                connection.createStatement().execute(sql);
            }
            System.out.println("Schéma BDD initialisé");
        } catch (SQLException e) {
            System.err.println("Erreur création schéma : " + e.getMessage());
        }
    }
    
    /**
     * Ferme la connexion.
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Erreur fermeture connexion : " + e.getMessage());
        }
    }
    
    /**
     * Réinitialise la connexion (pour les tests).
     */
    public static void reset() {
        if (instance != null) {
            instance.close();
            instance = null;
        }
    }
}
