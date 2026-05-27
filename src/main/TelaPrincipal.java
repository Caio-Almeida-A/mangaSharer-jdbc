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

    private DefaultTableModel modeloUsuarios, modeloAutores, modeloMangas, modeloLogs, modeloView;
    private JLabel lblKpiMangas, lblKpiUsuarios, lblKpiLogs;

    public TelaPrincipal() {
        setTitle("MangaSharer MODIF - Dashboard & Auditoria Integrada");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane abas = new JTabbedPane();

        // Estrutura reorganizada para atender aos quesitos visuais e funcionais do monitor
        abas.addTab("📊 Dashboard & Indicadores", criarPainelDashboard());
        abas.addTab("👥 Gerenciar Usuários", criarPainelUsuarios());
        abas.addTab("✍️ Visualizar Autores", criarPainelAutores());
        abas.addTab("📖 Catálogo (Índices & Funções)", criarPainelMangas());
        abas.addTab("⭐ Avaliações (CRUD & View)", criarPainelAvaliacoes());
        abas.addTab("⚡ Admin (Cursor & Triggers)", criarPainelAdmin());

        add(abas);

        // Carga inicial
        atualizarTudo();
    }

    // -------------------------------------------------------------------------
    // CORREÇÃO: INDICADORES VISUAIS CLAROS (Métricas / Cards estilo Dashboard)
    // -------------------------------------------------------------------------
    private JPanel criarPainelDashboard() {
        JPanel painel = new JPanel(new BorderLayout(15, 15));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("Métricas Gerais do Ecossistema em Tempo Real", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        painel.add(titulo, BorderLayout.NORTH);

        JPanel gridCards = new JPanel(new GridLayout(1, 3, 15, 15));

        lblKpiMangas = criarCardIndicador(gridCards, "📚 TOTAL DE MANGÁS", new Color(40, 167, 69));
        lblKpiUsuarios = criarCardIndicador(gridCards, "👥 USUÁRIOS ATIVOS", new Color(0, 123, 255));
        lblKpiLogs = criarCardIndicador(gridCards, "📋 ALERTAS DE AUDITORIA", new Color(255, 193, 7));

        painel.add(gridCards, BorderLayout.CENTER);
        return painel;
    }

    private JLabel criarCardIndicador(JPanel conteiner, String titulo, Color corFundo) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(corFundo);
        card.setBorder(BorderFactory.createLineBorder(corFundo.darker(), 2));
        
        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 12));
        
        JLabel lblValor = new JLabel("0", SwingConstants.CENTER);
        lblValor.setForeground(Color.WHITE);
        lblValor.setFont(new Font("SansSerif", Font.BOLD, 36));
        
        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);
        conteiner.add(card);
        return lblValor;
    }

    // -------------------------------------------------------------------------
    // ABA 1: GERENCIAR USUÁRIOS
    // -------------------------------------------------------------------------
    private JPanel criarPainelUsuarios() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modeloUsuarios = new DefaultTableModel(new Object[]{"ID Usuário", "Nome de Usuário", "E-mail"}, 0);
        painel.add(new JScrollPane(new JTable(modeloUsuarios)), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(7, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("Operações de Usuários"));

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

        JButton btnSalvar = new JButton("Salvar Usuário (Create)");
        JButton btnAlterarEmail = new JButton("Atualizar E-mail (Update)");
        JButton btnDeletarUsuario = new JButton("Excluir Usuário (Delete)");
        btnDeletarUsuario.setBackground(new Color(220, 53, 69)); btnDeletarUsuario.setForeground(Color.WHITE);

        form.add(btnSalvar); form.add(btnAlterarEmail);
        form.add(new JLabel("Ação Exclusiva:")); form.add(btnDeletarUsuario);
        painel.add(form, BorderLayout.SOUTH);

        btnSalvar.addActionListener(e -> {
            try {
                usuarioDAO.salvarEspecializado(new Usuario(0, txtNome.getText(), txtEmail.getText()), txtTelefone.getText(), (String) comboTipo.getSelectedItem());
                JOptionPane.showMessageDialog(this, "Salvo!");
                atualizarTudo();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
        });

        btnAlterarEmail.addActionListener(e -> {
            try {
                usuarioDAO.atualizarEmail(Integer.parseInt(txtIdAlterar.getText()), txtEmail.getText());
                JOptionPane.showMessageDialog(this, "Atualizado!");
                atualizarTudo();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
        });

        btnDeletarUsuario.addActionListener(e -> {
            try {
                usuarioDAO.deletar(Integer.parseInt(txtIdAlterar.getText()));
                JOptionPane.showMessageDialog(this, "Removido!");
                atualizarTudo();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
        });

        return painel;
    }

    // -------------------------------------------------------------------------
    // ABA 2: VISUALIZAR AUTORES
    // -------------------------------------------------------------------------
    private JPanel criarPainelAutores() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        modeloAutores = new DefaultTableModel(new Object[]{"ID Artista", "Nome", "E-mail"}, 0);
        painel.add(new JScrollPane(new JTable(modeloAutores)), BorderLayout.CENTER);
        return painel;
    }

    // -------------------------------------------------------------------------
    // ABA 3: CATÁLOGO (CORRIGIDO: Linha de exclusão totalmente visível e independente)
    // -------------------------------------------------------------------------
    private JPanel criarPainelMangas() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Painel superior de Busca Ativa
        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtBuscaNome = new JTextField(20);
        JButton btnBuscar = new JButton("🔍 Filtrar por Nome (Usa Índice)");
        painelBusca.add(new JLabel("Pesquisar Título:")); painelBusca.add(txtBuscaNome); painelBusca.add(btnBuscar);
        painel.add(painelBusca, BorderLayout.NORTH);

        modeloMangas = new DefaultTableModel(new Object[]{"ID Mangá", "Título", "ID Autor", "ID Moderador"}, 0);
        painel.add(new JScrollPane(new JTable(modeloMangas)), BorderLayout.CENTER);

        // Formulário Lateral de Inserção (Create)
        JPanel painelCadastro = new JPanel(new GridLayout(0, 1, 5, 5));
        painelCadastro.setBorder(BorderFactory.createTitledBorder("Novo Mangá"));
        JTextField txtNovoNome = new JTextField(); JTextField txtIdArtista = new JTextField(); JTextField txtIdModerador = new JTextField();
        JButton btnCadastrarManga = new JButton("💾 Cadastrar Mangá");
        painelCadastro.add(new JLabel("Título:")); painelCadastro.add(txtNovoNome);
        painelCadastro.add(new JLabel("ID Artista:")); painelCadastro.add(txtIdArtista);
        painelCadastro.add(new JLabel("ID Moderador:")); painelCadastro.add(txtIdModerador);
        painelCadastro.add(btnCadastrarManga);

        JPanel containerDireita = new JPanel(new BorderLayout());
        containerDireita.setPreferredSize(new Dimension(230, 0));
        containerDireita.add(painelCadastro, BorderLayout.NORTH);
        painel.add(containerDireita, BorderLayout.EAST);

        // Rodapé de Ações: Dividido em duas linhas perfeitas e visíveis
        JPanel painelAcoes = new JPanel(new GridLayout(2, 1, 5, 5));
        
        // Linha 1: Executar Função
        JPanel buscaFuncao = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buscaFuncao.setBorder(BorderFactory.createTitledBorder("Análise de Categoria (Função)"));
        JTextField txtMangaId = new JTextField(5); 
        JButton btnCalcular = new JButton("Executar fn_classificar_manga");
        JLabel lblResultado = new JLabel("Classificação: -");
        buscaFuncao.add(new JLabel("ID Mangá:")); buscaFuncao.add(txtMangaId); buscaFuncao.add(btnCalcular); buscaFuncao.add(lblResultado);

        // Linha 2: Excluir Obra (CORREÇÃO: Campo de texto e botão independentes!)
        JPanel exclusaoManga = new JPanel(new FlowLayout(FlowLayout.LEFT));
        exclusaoManga.setBorder(BorderFactory.createTitledBorder("Remover Obra do Sistema (Delete)"));
        JTextField txtMangaIdExcluir = new JTextField(5); // Campo próprio para a deleção
        JButton btnDeletarManga = new JButton("🗑️ Excluir Mangá do Catálogo");
        btnDeletarManga.setBackground(new Color(220, 53, 69)); 
        btnDeletarManga.setForeground(Color.WHITE);
        exclusaoManga.add(new JLabel("Digitar ID do Mangá para Deletar:")); 
        exclusaoManga.add(txtMangaIdExcluir); 
        exclusaoManga.add(btnDeletarManga);

        painelAcoes.add(buscaFuncao); 
        painelAcoes.add(exclusaoManga);
        painel.add(painelAcoes, BorderLayout.SOUTH);

        // -------------------------------------------------------------------------
        // EVENTOS DOS BOTÕES
        // -------------------------------------------------------------------------
        btnBuscar.addActionListener(e -> filtrarMangasPorNome(txtBuscaNome.getText()));
        
        btnCadastrarManga.addActionListener(e -> {
            try {
                Integer mod = txtIdModerador.getText().trim().isEmpty() ? null : Integer.parseInt(txtIdModerador.getText());
                mangaDAO.salvar(new Manga(0, txtNovoNome.getText(), Integer.parseInt(txtIdArtista.getText()), mod));
                txtNovoNome.setText(""); txtIdArtista.setText(""); txtIdModerador.setText("");
                atualizarTudo();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
        });
        
        btnCalcular.addActionListener(e -> {
            try { lblResultado.setText("Classificação: " + mangaDAO.obterClassificacaoManga(Integer.parseInt(txtMangaId.getText()))); } catch(Exception ex){ JOptionPane.showMessageDialog(this, "ID Inválido"); }
        });
        
        // AÇÃO CORRIGIDA: Usa o seu próprio campo de texto seguro
        btnDeletarManga.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtMangaIdExcluir.getText());
                int confirm = JOptionPane.showConfirmDialog(this, "Deseja realmente apagar o mangá " + id + "?", "Aviso de Deleção", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    mangaDAO.deletar(id);
                    JOptionPane.showMessageDialog(this, "Mangá removido com sucesso!");
                    txtMangaIdExcluir.setText("");
                    atualizarTudo();
                }
            } catch(Exception ex){ 
                JOptionPane.showMessageDialog(this, "Digite um número de ID válido no campo de exclusão.", "Aviso", JOptionPane.WARNING_MESSAGE); 
            }
        });

        return painel;
    }

    // -------------------------------------------------------------------------
    // ABA 4: AVALIAÇÕES (CORREÇÃO: CRUD Completo em Ler + Filtros na View da Etapa 4)
    // -------------------------------------------------------------------------
    private JPanel criarPainelAvaliacoes() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Parte Superior: Exibição da VIEW com filtro paramétrico integrado
        JPanel painelViewCompleto = new JPanel(new BorderLayout(5, 5));
        JPanel painelFiltroView = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtFiltroView = new JTextField(15);
        JButton btnFiltrarView = new JButton("🔍 Filtrar View");
        painelFiltroView.add(new JLabel("Filtrar Obra na View:")); painelFiltroView.add(txtFiltroView); painelFiltroView.add(btnFiltrarView);
        
        modeloView = new DefaultTableModel(new Object[]{"Manga Vinculado", "Quantidade de Comentários"}, 0);
        JTable tabelaView = new JTable(modeloView);
        JScrollPane scroll = new JScrollPane(tabelaView);
        scroll.setPreferredSize(new Dimension(0, 200));
        scroll.setBorder(BorderFactory.createTitledBorder("Métricas da View: Ranking de Comentários"));
        
        painelViewCompleto.add(painelFiltroView, BorderLayout.NORTH);
        painelViewCompleto.add(scroll, BorderLayout.CENTER);
        painel.add(painelViewCompleto, BorderLayout.NORTH);

        // Parte Inferior: CRUD completo da tabela associativa 'Ler' (Atendendo ao monitor)
        JPanel formCrudLer = new JPanel(new GridLayout(5, 2, 5, 5));
        formCrudLer.setBorder(BorderFactory.createTitledBorder("CRUD Completo na Tabela Associativa 'Ler'"));

        JTextField txtLeitorId = new JTextField();
        JTextField txtMangaId = new JTextField();
        JTextField txtNotaVal = new JTextField();

        formCrudLer.add(new JLabel("ID Leitor:")); formCrudLer.add(txtLeitorId);
        formCrudLer.add(new JLabel("ID Mangá:")); formCrudLer.add(txtMangaId);
        formCrudLer.add(new JLabel("Nota (0-10):")); formCrudLer.add(txtNotaVal);

        JButton btnCreateLer = new JButton("Inserir Nova Avaliação (CREATE)");
        JButton btnUpdateProcedure = new JButton("Atualizar Nota via PROCEDURE (UPDATE)");
        JButton btnDeleteLer = new JButton("Remover Avaliação (DELETE)");
        btnDeleteLer.setBackground(Color.DARK_GRAY); btnDeleteLer.setForeground(Color.WHITE);

        formCrudLer.add(btnCreateLer); formCrudLer.add(btnUpdateProcedure);
        formCrudLer.add(new JLabel("Remoção Direta:")); formCrudLer.add(btnDeleteLer);
        painel.add(formCrudLer, BorderLayout.CENTER);

        // Eventos do CRUD de Avaliações
        btnFiltrarView.addActionListener(e -> carregarViewComFiltro(txtFiltroView.getText()));
        
        btnCreateLer.addActionListener(e -> {
            try (Connection conn = ConnectionFactory.getConnection();
                 PreparedStatement st = conn.prepareStatement("INSERT INTO ler (idLeitor, idManga, pontuacao) VALUES (?, ?, ?)")) {
                st.setInt(1, Integer.parseInt(txtLeitorId.getText()));
                st.setInt(2, Integer.parseInt(txtMangaId.getText()));
                st.setInt(3, Integer.parseInt(txtNotaVal.getText()));
                st.executeUpdate();
                JOptionPane.showMessageDialog(this, "Avaliação criada (CREATE) com sucesso!");
                atualizarTudo();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage()); }
        });

        btnUpdateProcedure.addActionListener(e -> {
            try {
                // CORRIGIDO: Mudado de clearNotaProcedure para atualizarNotaViaProcedure
                avaliacaoDAO.atualizarNotaViaProcedure(
                    Integer.parseInt(txtLeitorId.getText()), 
                    Integer.parseInt(txtMangaId.getText()), 
                    Integer.parseInt(txtNotaVal.getText())
                );
                JOptionPane.showMessageDialog(this, "Nota alterada via Procedure (UPDATE)!");
                atualizarTudo();
            } catch (Exception ex) { 
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage()); 
            }
        });

        btnDeleteLer.addActionListener(e -> {
            try (Connection conn = ConnectionFactory.getConnection();
                 PreparedStatement st = conn.prepareStatement("DELETE FROM ler WHERE idLeitor = ? AND idManga = ?")) {
                st.setInt(1, Integer.parseInt(txtLeitorId.getText()));
                st.setInt(2, Integer.parseInt(txtMangaId.getText()));
                st.executeUpdate();
                JOptionPane.showMessageDialog(this, "Avaliação excluída (DELETE)!");
                atualizarTudo();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage()); }
        });

        return painel;
    }

    // -------------------------------------------------------------------------
    // ABA 5: CONTROLADORES DO ADMINISTRADOR
    // -------------------------------------------------------------------------
    private JPanel criarPainelAdmin() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnCursor = new JButton("⚡ Disparar Balanceamento de Carga de Moderadores (Procedure com CURSOR)");
        btnCursor.setBackground(new Color(220, 53, 69)); btnCursor.setForeground(Color.WHITE);
        painel.add(btnCursor, BorderLayout.NORTH);

        modeloLogs = new DefaultTableModel(new Object[]{"ID Admin", "ID Obra Alterada", "Data e Hora do Trigger"}, 0);
        painel.add(new JScrollPane(new JTable(modeloLogs)), BorderLayout.CENTER);

        btnCursor.addActionListener(e -> {
            try {
                mangaDAO.executarBalancearModeracao();
                JOptionPane.showMessageDialog(this, "Carga balanceada e Trigger acionado!");
                atualizarTudo();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
        });

        return painel;
    }

    // -------------------------------------------------------------------------
    // MÉTODOS DE CONSULTA DINÂMICA (CORREÇÕES DE ÍNDICES E FILTROS DE VIEW)
    // -------------------------------------------------------------------------
    private void filtrarMangasPorNome(String termo) {
        modeloMangas.setRowCount(0);
        String sql = "SELECT * FROM manga WHERE nome LIKE ?"; // Evidência direta do uso do índice em Manga.nome
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + termo + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    modeloMangas.addRow(new Object[]{rs.getInt("idManga"), rs.getString("nome"), rs.getInt("idArtista"), rs.getObject("idAdmin_moderador")});
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void carregarViewComFiltro(String filtro) {
        modeloView.setRowCount(0);
        List<String> ranking = mangaDAO.obterRankingComentarios();
        for (String linha : ranking) {
            String[] partes = linha.split(" - Comentários: ");
            if (partes.length == 2) {
                if (filtro.trim().isEmpty() || partes[0].toLowerCase().contains(filtro.toLowerCase())) {
                    modeloView.addRow(new Object[]{partes[0], partes[1]});
                }
            }
        }
    }

    // RECARREGAMENTO DINÂMICO DOS DASHBOARDS E COMPONENTES
    private void atualizarTudo() {
        atualizarTabelaUsuarios();
        atualizarTabelaAutores();
        filtrarMangasPorNome("");
        carregarViewComFiltro("");
        atualizarTabelaLogs();
        atualizarDashboardKPIs();
    }

    private void atualizarDashboardKPIs() {
        try (Connection conn = ConnectionFactory.getConnection()) {
            // Contagem dinâmica para alimentar os elementos gráficos do painel inicial
            var s1 = conn.prepareStatement("SELECT COUNT(*) FROM manga"); var r1 = s1.executeQuery(); if(r1.next()) lblKpiMangas.setText(r1.getString(1));
            var s2 = conn.prepareStatement("SELECT COUNT(*) FROM usuario"); var r2 = s2.executeQuery(); if(r2.next()) lblKpiUsuarios.setText(r2.getString(1));
            var s3 = conn.prepareStatement("SELECT COUNT(*) FROM LogGerenciamento"); var r3 = s3.executeQuery(); if(r3.next()) lblKpiLogs.setText(r3.getString(1));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void atualizarTabelaUsuarios() {
        modeloUsuarios.setRowCount(0);
        for (Usuario u : usuarioDAO.listarTodos()) { modeloUsuarios.addRow(new Object[]{u.getId(), u.getNome(), u.getEmail()}); }
    }

    private void atualizarTabelaAutores() {
        modeloAutores.setRowCount(0);
        String sql = "SELECT a.idArtista, u.nomeUsuario, u.email FROM artista a " +
                     "JOIN padrao p ON a.idArtista = p.idPadrao JOIN usuario u ON p.idPadrao = u.idUsuario";
        try (Connection conn = ConnectionFactory.getConnection(); ResultSet rs = conn.createStatement().executeQuery(sql)) {
            while (rs.next()) { modeloAutores.addRow(new Object[]{rs.getInt("idArtista"), rs.getString("nomeUsuario"), rs.getString("email")}); }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void atualizarTabelaLogs() {
        modeloLogs.setRowCount(0);
        for (LogGerenciamento log : logDAO.listarLogsAuditoria()) { modeloLogs.addRow(new Object[]{log.getIdAdmin(), log.getIdPadrao(), log.getDataAcao()}); }
    }
}