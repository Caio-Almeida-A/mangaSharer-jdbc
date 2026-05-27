package dao;

import model.Manga;
import util.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MangaDAO {

    // [CRUD - Create] Inserir novo mangá
    public void salvar(Manga manga) {
    // Removemos o idManga da query para o MySQL gerar sozinho
        String sql = "INSERT INTO manga (nome, idArtista, idAdmin_moderador) VALUES (?, ?, ?)";
        
        try (Connection conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, manga.getNome());
            stmt.setInt(2, manga.getIdArtista());
            
            if (manga.getIdModerador() == null) {
                stmt.setNull(3, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(3, manga.getIdModerador());
            }
            
            stmt.executeUpdate();
            
            // Recupera o ID que o AUTO_INCREMENT acabou de criar
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGerado = rs.getInt(1);
                    System.out.println("Mangá cadastrado com sucesso! ID Gerado automaticamente: " + idGerado);
                }
            }
        } catch (java.sql.SQLException e) {
            throw new RuntimeException("Erro ao salvar mangá via JDBC: ", e);
        }
    }

    // [CRUD - Read] Listar todos os mangás
    public List<Manga> listarTodos() {
        List<Manga> mangas = new ArrayList<>();
        String sql = "SELECT * FROM manga";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Manga m = new Manga(
                    rs.getInt("idManga"),
                    rs.getString("nome"),
                    rs.getInt("idArtista"),
                    rs.getInt("idAdmin_moderador")
                );
                if (rs.wasNull()) {
                    m.setIdModerador(null);
                }
                mangas.add(m);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar mangás: ", e);
        }
        return mangas;
    }

    // [Integração Etapa 04] Puxar dados da Visão do Ranking de Comentários
    public List<String> obterRankingComentarios() {
        List<String> ranking = new ArrayList<>();
        String sql = "SELECT * FROM vw_ranking_comentarios";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                ranking.add(rs.getString("nome") + " - Comentários: " + rs.getInt("Total_Comentarios"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao ler view de ranking: ", e);
        }
        return ranking;
    }

    // [Integração Etapa 05] Executar a Função fn_classificar_manga
    public String obterClassificacaoManga(int idManga) {
        String sql = "SELECT fn_classificar_manga(?) AS classe";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idManga);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("classe");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao executar função de classificação: ", e);
        }
        return "Sem dados";
    }

    // [Integração Etapa 05] Executar o Procedimento sp_balancear_moderacao_mangas
    public void executarBalancearModeracao() {
        String sql = "CALL sp_balancear_moderacao_mangas()";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.execute();
            System.out.println("Procedimento de balanceamento executado no banco!");
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao executar procedure com cursor: ", e);
        }
    }
    public void deletar(int idManga) {
        String sqlLer = "DELETE FROM ler WHERE idManga = ?";
        String sqlManga = "DELETE FROM manga WHERE idManga = ?";

        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stL = conn.prepareStatement(sqlLer);
                PreparedStatement stM = conn.prepareStatement(sqlManga)) {

                // 1. Remove o histórico de avaliações do mangá
                stL.setInt(1, idManga);
                stL.executeUpdate();

                // 2. Remove o mangá propriamente dito
                stM.setInt(1, idManga);
                stM.executeUpdate();

                conn.commit();
                System.out.println("Mangá e suas avaliações foram deletados!");
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar mangá: ", e);
        }
    }
}