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
        String sql = "INSERT INTO manga (idManga, nome, idArtista, idAdmin_moderador) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, manga.getId());
            stmt.setString(2, manga.getNome());
            stmt.setInt(3, manga.getIdArtista());
            if (manga.getIdModerador() == null) {
                stmt.setNull(4, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(4, manga.getIdModerador());
            }
            stmt.executeUpdate();
            System.out.println("Mangá salvo com sucesso!");
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar mangá: ", e);
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
}