package util; // Lembre-se de ajustar para apenas 'util'

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    // Configurações do seu banco de dados
    private static final String URL = "jdbc:mysql://localhost:3306/manga_db2";
    private static final String USER = "root"; 
    private static final String PASS = "rootLit123"; // Coloque a senha do seu MySQL

    public static Connection getConnection() {
        try {
            // O Java 8+ já carrega o driver automaticamente se o .jar estiver na lib
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            throw new RuntimeException("Erro na conexão com o banco manga_db2: ", e);
        }
    }
}