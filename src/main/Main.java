package main;

import util.ConnectionFactory;
import javax.swing.SwingUtilities;
import java.sql.Connection;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        // CORREÇÃO: Garante a criação da tabela de logs dinamicamente sem alterar scripts passados
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS LogGerenciamento (" +
                         "idAdmin INT, idPadrao INT, dataAcao DATETIME)");
            System.out.println("[Gatilho de Segurança] Tabela LogGerenciamento verificada/criada com sucesso.");
        } catch (Exception e) {
            System.out.println("Aviso na inicialização do banco: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            TelaPrincipal tela = new TelaPrincipal();
            tela.setVisible(true);
        });
    }
}