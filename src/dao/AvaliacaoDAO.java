package dao;

import model.Avaliacao;
import util.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AvaliacaoDAO {

    // [CRUD - Create] Registrar avaliação
    public void salvar(Avaliacao avaliacao) {
        String sql = "INSERT INTO ler (idLeitor, idManga, pontuacao) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, avaliacao.getIdLeitor());
            stmt.setInt(2, avaliacao.getIdManga());
            stmt.setInt(3, avaliacao.getPontuacao());
            stmt.executeUpdate();
            System.out.println("Avaliação registrada!");
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao registrar avaliação: ", e);
        }
    }

    // [Integração Etapa 05 - CRUD Update] Chama a Procedure de atualização cadastral
    public void atualizarNotaViaProcedure(int idLeitor, int idManga, int novaPontuacao) {
        String sql = "CALL sp_atualizar_nota_leitor(?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idLeitor);
            stmt.setInt(2, idManga);
            stmt.setInt(3, novaPontuacao);
            stmt.execute();
            System.out.println("Nota atualizada via Procedure com sucesso!");
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar nota via procedure: ", e);
        }
    }
}