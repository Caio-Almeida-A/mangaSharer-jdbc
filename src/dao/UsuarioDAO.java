package dao;

import model.Usuario;
import util.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UsuarioDAO {

    // Método para Inserir Usuário e seus respectivos Telefones (CREATE)
    public void salvar(Usuario usuario) {
        String sqlUsuario = "INSERT INTO usuario (idUsuario, nomeUsuario, email) VALUES (?, ?, ?)";
        String sqlTelefone = "INSERT INTO usuario_telefone (idUsuario, telefone) VALUES (?, ?)";

        // O bloco try-with-resources garante que as conexões serão fechadas automaticamente
        try (Connection conn = ConnectionFactory.getConnection()) {
            
            // Desativa o auto-commit para garantir que se a inserção do telefone falhar,
            // o usuário também não seja salvo (Conceito de Transação ATÔMICA)
            conn.setAutoCommit(false);

            try (PreparedStatement stmtUser = conn.prepareStatement(sqlUsuario)) {
                stmtUser.setInt(1, usuario.getId());
                stmtUser.setString(2, usuario.getNome());
                stmtUser.setString(3, usuario.getEmail());
                stmtUser.executeUpdate();
                
                // Salva todos os telefones associados a este usuário
                try (PreparedStatement stmtTel = conn.prepareStatement(sqlTelefone)) {
                    for (String telefone : usuario.getTelefones()) {
                        stmtTel.setInt(1, usuario.getId());
                        stmtTel.setString(2, telefone);
                        stmtTel.executeUpdate();
                    }
                }
                
                // Se tudo correu bem, confirma as alterações no banco
                conn.commit();
                System.out.println("Usuário e telefones cadastrados com sucesso!");
                
            } catch (SQLException e) {
                conn.rollback(); // Cancela tudo se houver erro
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar usuário no JDBC: ", e);
        }
    }
}