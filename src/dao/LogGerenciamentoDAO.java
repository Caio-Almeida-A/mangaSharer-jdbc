package dao;

import model.LogGerenciamento;
import util.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LogGerenciamentoDAO {

    // [CRUD - Read] Listar logs preenchidos pelo TRIGGER automático do banco
    public List<LogGerenciamento> listarLogsAuditoria() {
        List<LogGerenciamento> logs = new ArrayList<>();
        String sql = "SELECT * FROM LogGerenciamento ORDER BY dataAcao DESC";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                LogGerenciamento log = new LogGerenciamento(
                    rs.getInt("idAdmin"),
                    rs.getInt("idPadrao"),
                    rs.getTimestamp("dataAcao") // Converte DATETIME do SQL para o Date do Java
                );
                logs.add(log);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar logs de auditoria: ", e);
        }
        return logs;
    }
}