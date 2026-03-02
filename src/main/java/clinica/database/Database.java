package clinica.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javafx.scene.control.Alert;

/**
 * Gerencia a conexão única com o banco de dados MySQL.
 * Edite as constantes URL, USER e PASSWORD conforme seu ambiente.
 */
public class Database {

    // ─────────────── CONFIGURE AQUI ───────────────
    private static final String URL  = "jdbc:mysql://localhost:3306/clinica" +
            "?useSSL=false" +
            "&allowPublicKeyRetrieval=true" +
            "&serverTimezone=America/Cuiaba" +
            "&characterEncoding=UTF-8" +
            "&useUnicode=true" +
            "&connectionCollation=utf8mb4_unicode_ci";
    private static final String USER = "root";
    private static final String PASS = "root";
    // ──────────────────────────────────────────────

    private static Connection connection;

    private Database() {}

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASS);
                connection.setAutoCommit(false); // Transações manuais
            }
        } catch (SQLException e) {
            showError("Erro ao conectar ao banco de dados:\n" + e.getMessage());
        }
        return connection;
    }

    public static void commit() {
        try {
            if (connection != null) connection.commit();
        } catch (SQLException e) {
            showError("Erro ao confirmar transação:\n" + e.getMessage());
        }
    }

    public static void rollback() {
        try {
            if (connection != null) connection.rollback();
        } catch (SQLException e) {
            showError("Erro ao reverter transação:\n" + e.getMessage());
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            showError("Erro ao fechar conexão:\n" + e.getMessage());
        }
    }

    private static void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro de Banco de Dados");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
