package main;

import dao.MangaDAO;
import dao.AvaliacaoDAO;
import dao.LogGerenciamentoDAO;
import dao.UsuarioDAO;
import model.Usuario;
import model.Manga;
import model.LogGerenciamento;
import util.ConnectionFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class TelaPrincipal extends JFrame {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final MangaDAO mangaDAO = new MangaDAO();
    private final AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO();
    private final LogGerenciamentoDAO logDAO = new LogGerenciamentoDAO();

    private DefaultTableModel modeloUsuarios;
    private DefaultTableModel modeloAutores;
    private DefaultTableModel modeloMangas;
    private DefaultTableModel modeloLogs;
    private DefaultTableModel modeloView;

    public TelaPrincipal() {
        setTitle("MangaSharer MODIF - Sistema de Gestão Integrado (Versão Final)");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane abas = new JTabbedPane();

        // Resgatando e integrando todas as frentes do projeto
        abas.addTab("1. Gerenciar Usuários", criarPainelUsuarios());
        abas.addTab("2. Visualizar Autores", criarPainelAutores());
        abas.addTab("3. Catálogo de Mangás & Views", criarPainelMangas());
        abas.addTab("4. Avaliações (Procedures)", criarPainelAvaliacoes());
        abas.addTab("5. Admin (Cursor & Triggers)", criarPainelAdmin());

        add(abas);

        // Carga inicial de dados
        atualizarTabelaUsuarios();
        atualizarTabelaAutores();
        atualizarTabelaMangas();
        atualizarTabelaLogs();
    }

    // ABA 1: CRIAÇÃO E LISTAGEM DE USUÁRIOS (Trazendo de volta o core do sistema)
    private JPanel criarPainelUsuarios() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modeloUsuarios = new DefaultTableModel(new Object[]{"ID Usuário", "Nome de Usuário", "E-mail"}, 0);
        JTable tabela = new JTable(modeloUsuarios);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);

        // Ajustado para 7 linhas para acomodar o novo botão de Delete
        JPanel form = new JPanel(new GridLayout(7, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("Gerenciamento Avançado de Usuários"));

        JTextField txtNome = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtTelefone = new JTextField();
        JComboBox<String> comboTipo = new JComboBox<>(new String[]{"Leitor", "Artista"});
        JTextField txtIdAlterar = new JTextField(); 

        form.add(new JLabel("Nome de Usuário (Cadastro):")); form.add(txtNome);
        form.add(new JLabel("E-mail (Cadastro ou Alteração):")); form.add(txtEmail);
        form.add(new JLabel("Telefone (Opcional):")); form.add(txtTelefone);
        form.add(new JLabel("Tipo de Perfil (Cadastro):")); form.add(comboTipo);
        form.add(new JLabel("ID do Usuário (Para Alterar ou Deletar):")); form.add(txtIdAlterar);

        JButton btnSalvar = new JButton("Salvar Novo Usuário (Create)");
        JButton btnAlterarEmail = new JButton("Atualizar E-mail (Update)");
        JButton btnDeletarUsuario = new JButton("Excluir Usuário (Delete)");
        
        // Customização visual do botão de exclusão
        btnDeletarUsuario.setBackground(new Color(220, 53, 69));
        btnDeletarUsuario.setForeground(Color.WHITE);

        form.add(btnSalvar);
        form.add(btnAlterarEmail);
        form.add(new JLabel("Ação Destrutiva:")); form.add(btnDeletarUsuario);

        painel.add(form, BorderLayout.SOUTH);

        // EVENTO: Salvar
        btnSalvar.addActionListener(e -> {
            try {
                String nome = txtNome.getText();
                String email = txtEmail.getText();
                String tel = txtTelefone.getText();
                String tipoSelecionado = (String) comboTipo.getSelectedItem();
                usuarioDAO.salvarEspecializado(new Usuario(0, nome, email), tel, tipoSelecionado);
                JOptionPane.showMessageDialog(this, "Perfil criado com sucesso!");
                txtNome.setText(""); txtEmail.setText(""); txtTelefone.setText("");
                atualizarTabelaUsuarios();
                atualizarTabelaAutores();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        // EVENTO: Alterar E-mail
        btnAlterarEmail.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtIdAlterar.getText());
                String novoEmail = txtEmail.getText();
                if (novoEmail.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Informe o novo e-mail.");
                    return;
                }
                usuarioDAO.atualizarEmail(id, novoEmail);
                JOptionPane.showMessageDialog(this, "E-mail alterado!");
                txtIdAlterar.setText(""); txtEmail.setText("");
                atualizarTabelaUsuarios();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        // EVENTO NOVO: Deletar Usuário
        btnDeletarUsuario.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtIdAlterar.getText());
                int confirmacao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir o usuário " + id + " e todas as suas contas vinculadas?", "Confirmação", JOptionPane.YES_NO_OPTION);
                
                if (confirmacao == JOptionPane.YES_OPTION) {
                    usuarioDAO.deletar(id);
                    JOptionPane.showMessageDialog(this, "Usuário removido do sistema com sucesso!");
                    txtIdAlterar.setText("");
                    atualizarTabelaUsuarios();
                    atualizarTabelaAutores();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Informe um ID válido para exclusão: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        return painel;
    }
    
    // ABA 2: VISUALIZAÇÃO DE AUTORES / ARTISTAS (Atendendo ao seu pedido!)
    private JPanel criarPainelAutores() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modeloAutores = new DefaultTableModel(new Object[]{"ID Artista/Autor", "Nome do Autor", "E-mail de Contato"}, 0);
        JTable tabela = new JTable(modeloAutores);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);

        JLabel info = new JLabel("Lista de Autores e Artistas cadastrados no Ecossistema MangaSharer", SwingConstants.CENTER);
        painel.add(info, BorderLayout.NORTH);

        

        return painel;
    }

    // ABA 3: LIVRARIA DE MANGÁS, RATING E VIEWS DA ETAPA 4
    private JPanel criarPainelMangas() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Centro: Tabela de listagem de Mangás (Read)
        modeloMangas = new DefaultTableModel(new Object[]{"ID Mangá", "Título", "ID Autor", "ID Moderador"}, 0);
        JTable tabela = new JTable(modeloMangas);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);

        // Lado Direito: Formulário para Adicionar Novo Mangá (ID Removido!)
        JPanel painelCadastro = new JPanel(new GridLayout(0, 1, 5, 5));
        painelCadastro.setBorder(BorderFactory.createTitledBorder("Novo Mangá (Create)"));
        
        JTextField txtNovoNome = new JTextField();
        JTextField txtIdArtista = new JTextField();
        JTextField txtIdModerador = new JTextField();
        JButton btnCadastrarManga = new JButton("💾 Cadastrar Mangá");
        
        painelCadastro.add(new JLabel("Título da Obra:")); 
        painelCadastro.add(txtNovoNome);
        painelCadastro.add(new JLabel("ID do Artista/Autor:")); 
        painelCadastro.add(txtIdArtista);
        painelCadastro.add(new JLabel("ID do Moderador (Opcional):")); 
        painelCadastro.add(txtIdModerador);
        painelCadastro.add(new JLabel("")); 
        painelCadastro.add(btnCadastrarManga);
        
        JPanel containerDireita = new JPanel(new BorderLayout());
        containerDireita.setPreferredSize(new Dimension(250, 0));
        containerDireita.add(painelCadastro, BorderLayout.NORTH);
        painel.add(containerDireita, BorderLayout.EAST);

        // Rodapé: Ações de Função (Etapa 05) e Deleção (Delete)
        JPanel painelAcoes = new JPanel(new GridLayout(2, 1, 5, 5));

        JPanel buscaFuncao = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buscaFuncao.setBorder(BorderFactory.createTitledBorder("Análise de Categoria (Função)"));
        JTextField txtMangaId = new JTextField(8);
        JButton btnCalcular = new JButton("Executar fn_classificar_manga");
        JLabel lblResultado = new JLabel("Classificação: -");
        buscaFuncao.add(new JLabel("ID do Mangá:")); buscaFuncao.add(txtMangaId);
        buscaFuncao.add(btnCalcular); buscaFuncao.add(lblResultado);

        JPanel exclusaoManga = new JPanel(new FlowLayout(FlowLayout.LEFT));
        exclusaoManga.setBorder(BorderFactory.createTitledBorder("Remover Obra (Delete)"));
        JButton btnDeletarManga = new JButton("🗑️ Excluir Mangá do Catálogo");
        btnDeletarManga.setBackground(new Color(220, 53, 69));
        btnDeletarManga.setForeground(Color.WHITE);
        exclusaoManga.add(btnDeletarManga);

        painelAcoes.add(buscaFuncao);
        painelAcoes.add(exclusaoManga);
        painel.add(painelAcoes, BorderLayout.SOUTH);

        // -------------------------------------------------------------------------
        // EVENTOS DOS BOTÕES
        // -------------------------------------------------------------------------

        // EVENTO: Cadastrar Mangá (ID automático)
        btnCadastrarManga.addActionListener(e -> {
            try {
                String nome = txtNovoNome.getText();
                int idArtista = Integer.parseInt(txtIdArtista.getText());
                
                Integer idMod = null;
                if (!txtIdModerador.getText().trim().isEmpty()) {
                    idMod = Integer.parseInt(txtIdModerador.getText());
                }

                // Passamos 0 como ID porque o banco de dados vai gerar o valor definitivo
                Manga novoManga = new Manga(0, nome, idArtista, idMod);
                mangaDAO.salvar(novoManga);

                JOptionPane.showMessageDialog(this, "Mangá '" + nome + "' inserido com sucesso! O ID foi gerado automaticamente pelo MySQL.");
                
                txtNovoNome.setText(""); txtIdArtista.setText(""); txtIdModerador.setText("");
                atualizarTabelaMangas();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao cadastrar mangá: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        // EVENTO: Calcular Classificação (Function)
        btnCalcular.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtMangaId.getText());
                String classe = mangaDAO.obterClassificacaoManga(id);
                lblResultado.setText("Classificação: " + classe);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Informe um ID válido.");
            }
        });

        // EVENTO: Deletar Mangá (Delete)
        btnDeletarManga.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtMangaId.getText());
                int confirmacao = JOptionPane.showConfirmDialog(this, "Deseja apagar o mangá " + id + "?", "Aviso", JOptionPane.YES_NO_OPTION);
                if (confirmacao == JOptionPane.YES_OPTION) {
                    mangaDAO.deletar(id);
                    JOptionPane.showMessageDialog(this, "Mangá excluído com sucesso!");
                    txtMangaId.setText(""); lblResultado.setText("Classificação: -");
                    atualizarTabelaMangas();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Digite o ID no campo de texto da análise para deletar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        return painel;
    }

    // ABA 4: AVALIAÇÕES E PROCEDURES DE ATUALIZAÇÃO
    private JPanel criarPainelAvaliacoes() {
        JPanel painel = new JPanel(new GridLayout(2, 1, 10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Alterado para usar a variável global que declaramos no topo
        modeloView = new DefaultTableModel(new Object[]{"Manga Vinculado", "Quantidade de Comentários Ativos"}, 0);
        JTable tabelaView = new JTable(modeloView);
        JScrollPane scroll = new JScrollPane(tabelaView);
        scroll.setBorder(BorderFactory.createTitledBorder("Métricas da View: Ranking de Comentários (Etapa 04)"));
        painel.add(scroll);

        JPanel form = new JPanel(new GridLayout(4, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("Modificar Notas (Integração com Procedure)"));

        JTextField txtLeitor = new JTextField();
        JTextField txtManga = new JTextField();
        JTextField txtNota = new JTextField();

        form.add(new JLabel("ID Leitor:")); form.add(txtLeitor);
        form.add(new JLabel("ID Mangá:")); form.add(txtManga);
        form.add(new JLabel("Nova Nota (0-10):")); form.add(txtNota);

        JButton btnExecutar = new JButton("Atualizar Nota via Procedure (CALL)");
        form.add(btnExecutar);
        painel.add(form);

        // Carrega dados iniciais da View
        atualizarTabelaView();

        btnExecutar.addActionListener(e -> {
            try {
                int l = Integer.parseInt(txtLeitor.getText());
                int m = Integer.parseInt(txtManga.getText());
                int n = Integer.parseInt(txtNota.getText());
                
                avaliacaoDAO.atualizarNotaViaProcedure(l, m, n);
                JOptionPane.showMessageDialog(this, "Procedure executada com sucesso!");
                
                // 🔥 NOVO: Força a tabela de notas a se redesenhar com os novos valores
                atualizarTabelaView(); 
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        });

        return painel;
    }

    // ABA 5: CONTROLADORES DO ADMINISTRADOR, CURSORES E TRIGGERS
    private JPanel criarPainelAdmin() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnCursor = new JButton("⚡ Disparar Balanceamento de Carga de Moderadores (Procedure com CURSOR)");
        btnCursor.setBackground(new Color(220, 53, 69));
        btnCursor.setForeground(Color.WHITE);
        painel.add(btnCursor, BorderLayout.NORTH);

        modeloLogs = new DefaultTableModel(new Object[]{"ID Admin", "ID Obra Alterada", "Data e Hora do Trigger"}, 0);
        JTable tabela = new JTable(modeloLogs);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);

        btnCursor.addActionListener(e -> {
            try {
                mangaDAO.executarBalancearModeracao();
                JOptionPane.showMessageDialog(this, "Carga balanceada! O Trigger capturou as alterações em segundo plano.");
                
                // 🔥 AGORA ATUALIZA TUDO EM TEMPO REAL:
                atualizarTabelaLogs();     // Atualiza as linhas nesta aba de Admin
                atualizarTabelaMangas();   // Atualiza os IDs dos novos moderadores lá na Aba 3!
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        });

        return painel;
    }

    // -------------------------------------------------------------------------
    // ATUALIZADORES DE TABELA - REFEITOS COM JDBC PURO E EXPLICITO
    // -------------------------------------------------------------------------
    private void atualizarTabelaUsuarios() {
        modeloUsuarios.setRowCount(0);
        List<Usuario> lista = usuarioDAO.listarTodos();
        for (Usuario u : lista) {
            modeloUsuarios.addRow(new Object[]{u.getId(), u.getNome(), u.getEmail()});
        }
    }

    private void atualizarTabelaAutores() {
        modeloAutores.setRowCount(0);
        // Consulta explícita unindo Artista com Usuário para exibir os dados legíveis
        String sql = "SELECT a.idArtista, u.nomeUsuario, u.email FROM artista a " +
                     "JOIN padrao p ON a.idArtista = p.idPadrao " +
                     "JOIN usuario u ON p.idPadrao = u.idUsuario";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                modeloAutores.addRow(new Object[]{
                    rs.getInt("idArtista"),
                    rs.getString("nomeUsuario"),
                    rs.getString("email")
                });
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar autores: " + e.getMessage());
        }
    }

    private void atualizarTabelaMangas() {
        modeloMangas.setRowCount(0);
        List<Manga> lista = mangaDAO.listarTodos();
        for (Manga m : lista) {
            modeloMangas.addRow(new Object[]{m.getId(), m.getNome(), m.getIdArtista(), m.getIdModerador()});
        }
    }

    private void atualizarTabelaLogs() {
        modeloLogs.setRowCount(0);
        List<LogGerenciamento> lista = logDAO.listarLogsAuditoria();
        for (LogGerenciamento log : lista) {
            modeloLogs.addRow(new Object[]{log.getIdAdmin(), log.getIdPadrao(), log.getDataAcao()});
        }
    }
    private void atualizarTabelaView() {
        if (modeloView != null) {
            modeloView.setRowCount(0);
            List<String> ranking = mangaDAO.obterRankingComentarios();
            for (String linha : ranking) {
                String[] partes = linha.split(" - Comentários: ");
                if (partes.length == 2) {
                    modeloView.addRow(new Object[]{partes[0], partes[1]});
                }
            }
        }
    }
}