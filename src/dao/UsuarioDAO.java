package dao;

import model.Usuario;
import util.ConnectionFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    // AGORA AUTOMATIZADO E ESPECIALIZADO
    public void salvarEspecializado(Usuario usuario, String telefone, String tipoUsuario) {
        String sqlUsuario = "INSERT INTO usuario (nomeUsuario, email) VALUES (?, ?)";
        String sqlPadrao = "INSERT INTO padrao (idPadrao) VALUES (?)";
        String sqlEspecializacao = tipoUsuario.equalsIgnoreCase("Leitor") 
                ? "INSERT INTO leitor (idLeitor) VALUES (?)" 
                : "INSERT INTO artista (idArtista) VALUES (?)";
        String sqlTelefone = "INSERT INTO usuario_telefone (idUsuario, telefone) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false); // Inicia a transação explícita exigida pelo PDF

            // 1. Insere na tabela pai (Usuario) e solicita o ID gerado automaticamente
            try (PreparedStatement stmtU = conn.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
                stmtU.setString(1, usuario.getNome());
                stmtU.setString(2, usuario.getEmail());
                stmtU.executeUpdate();

                // Captura o ID gerado pelo AUTO_INCREMENT do MySQL
                int idGerado = 0;
                try (ResultSet rs = stmtU.getGeneratedKeys()) {
                    if (rs.next()) {
                        idGerado = rs.getInt(1);
                    }
                }

                if (idGerado == 0) {
                    throw new SQLException("Falha ao obter o ID gerado pelo banco.");
                }

                // 2. Insere na tabela Padrao usando o ID capturado
                try (PreparedStatement stmtP = conn.prepareStatement(sqlPadrao)) {
                    stmtP.setInt(1, idGerado);
                    stmtP.executeUpdate();
                }

                // 3. Insere na tabela filha correspondente (Leitor ou Artista)
                try (PreparedStatement stmtE = conn.prepareStatement(sqlEspecializacao)) {
                    stmtE.setInt(1, idGerado);
                    stmtE.executeUpdate();
                }

                // 4. Insere o telefone, se fornecido
                if (telefone != null && !telefone.trim().isEmpty()) {
                    try (PreparedStatement stmtT = conn.prepareStatement(sqlTelefone)) {
                        stmtT.setInt(1, idGerado);
                        stmtT.setString(2, telefone);
                        stmtT.executeUpdate();
                    }
                }

                conn.commit(); // Confirma todas as inserções juntas
                System.out.println("Usuário especializado cadastrado com sucesso! ID: " + idGerado);
            } catch (SQLException e) {
                if (conn != null) conn.rollback(); // Desfaz tudo em caso de erro
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro na transação de cadastro: ", e);
        }
    }

    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuario";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                usuarios.add(new Usuario(
                    rs.getInt("idUsuario"),
                    rs.getString("nomeUsuario"),
                    rs.getString("email")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar usuários: ", e);
        }
        return usuarios;
    }

    public void atualizarEmail(int idUsuario, String novoEmail) {
    String sql = "UPDATE usuario SET email = ? WHERE idUsuario = ?";
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setString(1, novoEmail);
        stmt.setInt(2, idUsuario);
        stmt.executeUpdate();
        System.out.println("E-mail atualizado com sucesso via JDBC!");
    } catch (SQLException e) {
        throw new RuntimeException("Erro ao atualizar e-mail: ", e);
    }
}
    public void deletar(int idUsuario) {
        String sqlTelefone = "DELETE FROM usuario_telefone WHERE idUsuario = ?";
        String sqlLeitor = "DELETE FROM leitor WHERE idLeitor = ?";
        String sqlArtista = "DELETE FROM artista WHERE idArtista = ?";
        String sqlPadrao = "DELETE FROM padrao WHERE idPadrao = ?";
        String sqlUsuario = "DELETE FROM usuario WHERE idUsuario = ?";

        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false); // Transação explícita para garantir a integridade

            try (PreparedStatement stT = conn.prepareStatement(sqlTelefone);
                PreparedStatement stL = conn.prepareStatement(sqlLeitor);
                PreparedStatement stA = conn.prepareStatement(sqlArtista);
                PreparedStatement stP = conn.prepareStatement(sqlPadrao);
                PreparedStatement stU = conn.prepareStatement(sqlUsuario)) {

                // 1. Remove das tabelas filhas/associativas primeiro
                stT.setInt(1, idUsuario); stT.executeUpdate();
                stL.setInt(1, idUsuario); stL.executeUpdate();
                stA.setInt(1, idUsuario); stA.executeUpdate();
                
                // 2. Remove da tabela intermediária
                stP.setInt(1, idUsuario); stP.executeUpdate();
                
                // 3. Remove da tabela pai principal
                stU.setInt(1, idUsuario); stU.executeUpdate();

                conn.commit();
                System.out.println("Usuário e todas as suas dependências deletados!");
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar usuário: ", e);
        }
    }
}