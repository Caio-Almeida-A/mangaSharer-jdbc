package main;

import dao.MangaDAO;
import dao.AvaliacaoDAO;
import dao.LogGerenciamentoDAO;
import model.LogGerenciamento;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelaPrincipal extends JFrame {

    private final MangaDAO mangaDAO = new MangaDAO();
    private final AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO();
    private final LogGerenciamentoDAO logDAO = new LogGerenciamentoDAO();

    private JTable tabelaRanking;
    private JTable tabelaLogs;
    private DefaultTableModel modeloRanking;
    private DefaultTableModel modeloLogs;

    public TelaPrincipal() {
        setTitle("MangaSharer - Sistema de Gerenciamento de Mangas");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Componente de Abas para separar os perfis exigidos
        JTabbedPane abas = new JTabbedPane();

        abas.addTab("Perfil: Leitor (Notas & Avaliações)", criarPainelLeitor());
        abas.addTab("Perfil: Administrador (Cursor & Triggers)", criarPainelAdmin());

        add(abas);
        
        // Carrega os dados iniciais do banco nas tabelas
        atualizarDadosLeitor();
        atualizarTabelaLogs();
    }

    // -------------------------------------------------------------------------
    // INTERFACE DO LEITOR
    // -------------------------------------------------------------------------
    private JPanel criarPainelLeitor() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderBorderFactory(10));

        // Topo: Tabela com a VIEW da Etapa 04
        modeloRanking = new DefaultTableModel(new Object[]{"Manga", "Total de Comentários"}, 0);
        tabelaRanking = new JTable(modeloRanking);
        painel.add(new JScrollPane(tabelaRanking), BorderLayout.CENTER);

        // Rodapé: Formulário para PROCEDURE de Atualização e FUNÇÃO Condicional
        JPanel painelForm = new JPanel(new GridLayout(4, 2, 5, 5));
        painelForm.setBorder(BorderFactory.createTitledBorder("Avaliar Obra (Integração com Procedimentos e Funções)"));

        JTextField txtLeitor = new JTextField();
        JTextField txtManga = new JTextField();
        JTextField txtNota = new JTextField();
        JLabel lblClassificacao = new JLabel("Classificação Dinâmica: -", SwingConstants.CENTER);
        lblClassificacao.setFont(new Font("SansSerif", Font.BOLD, 12));

        painelForm.add(new JLabel("ID do Leitor:"));
        painelForm.add(txtLeitor);
        painelForm.add(new JLabel("ID do Mangá:"));
        painelForm.add(txtManga);
        painelForm.add(new JLabel("Nova Nota (0-10):"));
        painelForm.add(txtNota);

        JButton btnSalvarNota = new JButton("Atualizar Nota (Call Procedure)");
        JButton btnVerClasse = new JButton("Ver Categoria (Call Function)");

        painelForm.add(btnSalvarNota);
        painelForm.add(btnVerClasse);

        painel.add(painelForm, BorderLayout.SOUTH);

        // Ação: Procedure de Atualização Cadastral
        btnSalvarNota.addActionListener(e -> {
            try {
                int idLeitor = Integer.parseInt(txtLeitor.getText());
                int idManga = Integer.parseInt(txtManga.getText());
                int nota = Integer.parseInt(txtNota.getText());

                avaliacaoDAO.atualizarNotaViaProcedure(idLeitor, idManga, nota);
                JOptionPane.showMessageDialog(this, "Nota atualizada via Procedure com sucesso!");
                atualizarDadosLeitor();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao processar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Ação: Função Condicional do Banco
        btnVerClasse.addActionListener(e -> {
            try {
                int idManga = Integer.parseInt(txtManga.getText());
                String classe = mangaDAO.obterClassificacaoManga(idManga);
                lblClassificacao.setText("Classificação Dinâmica: " + classe);
                JOptionPane.showMessageDialog(this, "O banco classificou este mangá como: " + classe);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Informe um ID de Mangá válido.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        return painel;
    }

    // -------------------------------------------------------------------------
    // INTERFACE DO ADMINISTRADOR
    // -------------------------------------------------------------------------
    private JPanel criarPainelAdmin() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderBorderFactory(10));

        // Topo: Painel de Controle de Operações Complexas
        JPanel painelControle = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBalancear = new JButton("⚡ Executar Balanceamento Crítico (Procedure com CURSOR)");
        btnBalancear.setBackground(new Color(220, 53, 69));
        btnBalancear.setForeground(Color.WHITE);
        btnBalancear.setFont(new Font("SansSerif", Font.BOLD, 12));
        painelControle.add(btnBalancear);
        painel.add(painelControle, BorderLayout.NORTH);

        // Centro: Tabela de Logs alimentada pelo TRIGGER automático do banco
        modeloLogs = new DefaultTableModel(new Object[]{"ID Admin Moderador", "ID do Mangá Afetado", "Data/Hora da Ação Automática"}, 0);
        tabelaLogs = new JTable(modeloLogs);
        painel.add(new JScrollPane(tabelaLogs), BorderLayout.CENTER);

        // Ação: Executa Cursor e Atualiza a Tabela de Logs disparada pelo Trigger
        btnBalancear.addActionListener(e -> {
            try {
                mangaDAO.executarBalancearModeracao();
                JOptionPane.showMessageDialog(this, "Procedimento executado! O Cursor redistribuiu os moderadores e disparou os Triggers de auditoria.");
                atualizarTabelaLogs();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro no balanceamento: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        return painel;
    }

    // -------------------------------------------------------------------------
    // ATUALIZADORES DE TABELA (JDBC PURO)
    // -------------------------------------------------------------------------
    private void atualizarDadosLeitor() {
        modeloRanking.setRowCount(0);
        List<String> ranking = mangaDAO.obterRankingComentarios();
        for (String linha : ranking) {
            String[] partes = linha.split(" - Comentários: ");
            if (partes.length == 2) {
                modeloRanking.addRow(new Object[]{partes[0], partes[1]});
            }
        }
    }

    private void atualizarTabelaLogs() {
        modeloLogs.setRowCount(0);
        List<LogGerenciamento> logs = logDAO.listarLogsAuditoria();
        for (LogGerenciamento log : logs) {
            modeloLogs.addRow(new Object[]{log.getIdAdmin(), log.getIdPadrao(), log.getDataAcao()});
        }
    }

    private static javax.swing.border.Border BorderBorderFactory(int i) {
        return BorderFactory.createEmptyBorder(i, i, i, i);
    }
}