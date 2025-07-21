package view;

import dao.LendDAO;
import model.LendWithInfo;
import model.Item;
import util.Session;
import util.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.format.DateTimeFormatter;
import javax.imageio.ImageIO;
import java.util.List;

public class LendHistory extends JFrame {

    private JPanel painel;

    public LendHistory() {
        setTitle("Histórico de Empréstimos");
        setSize(800, 600);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(painel);
        add(scrollPane, BorderLayout.CENTER);

        carregarHistorico();
        setLocationRelativeTo(null);
        ThemeManager.aplicarTema(this);
        ThemeManager.registrarJanela(this);
        setVisible(true);
    }

    private void carregarHistorico() {
        painel.removeAll();
        LendDAO dao = new LendDAO();
        int userId = Session.getUsuario().getId();
        List<LendWithInfo> historico = dao.listarHistoricoCompletoPara(userId);

        if (historico.isEmpty()) {
            painel.add(new JLabel("Você ainda não participou de nenhum empréstimo."));
        } else {
            for (LendWithInfo e : historico) {
                String direcao = Session.getUsuario().getNome().equals(e.getNomeDe())
                        ? "Emprestado para: " + e.getNomePara()
                        : "Recebido de: " + e.getNomeDe();
                painel.add(criarCard(e, direcao));
            }
        }

        painel.revalidate();
        painel.repaint();
    }

    private JPanel criarCard(LendWithInfo emprestimo, String direcao) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        card.setPreferredSize(new Dimension(750, 120));

        JLabel imgLabel = new JLabel();
        imgLabel.setPreferredSize(new Dimension(100, 100));
        try {
            BufferedImage img = ImageIO.read(new File(emprestimo.getItem().getImagePath()));
            Image scaled = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            imgLabel.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            imgLabel.setText("Sem imagem");
        }

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        Item item = emprestimo.getItem();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        infoPanel.add(new JLabel(item.getType() + " - " + item.getColor() + " - " + item.getSize()));
        infoPanel.add(new JLabel(direcao));
        infoPanel.add(new JLabel("Empréstimo: " + emprestimo.getDataEmprestimo().format(df)));
        infoPanel.add(new JLabel("Devolução: " +
                (emprestimo.getDataDevolucao() != null ? emprestimo.getDataDevolucao().format(df)
                        : "Ainda não devolvido")));

        card.add(imgLabel, BorderLayout.WEST);
        card.add(infoPanel, BorderLayout.CENTER);
        return card;
    }
}
