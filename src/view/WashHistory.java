package view;

import dao.ItemDAO;
import dao.WashDAO;
import model.*;
import util.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class WashHistory extends JFrame {

    private final String usuarioLogado = String.valueOf(util.Session.getUsuario().getId());

    public WashHistory() {
        // Definições de Janela
        setTitle("Histórico de Lavagens");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));

        List<Lavagem> lavagens = new WashDAO().getLavagensDoUsuario(usuarioLogado);
        ItemDAO itemDAO = new ItemDAO();

        if (lavagens.isEmpty()) {
            painel.add(new JLabel("Nenhuma lavagem registrada para este usuário."));
        } else {
            for (Lavagem lavagem : lavagens) {
                JPanel box = new JPanel();
                box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
                box.setBorder(BorderFactory.createTitledBorder("Lavagem em " + lavagem.getData().toString()));

                for (int itemId : lavagem.getItemIds()) {
                    Item item = itemDAO.getAll().stream()
                            .filter(i -> i.getId() == itemId)
                            .findFirst().orElse(null);

                    if (item != null) {
                        JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

                        JLabel imgLabel = new JLabel();
                        imgLabel.setIcon(carregarImagem(item.getImagePath(), 80, 80));

                        JLabel descLabel = new JLabel(
                                String.format("  #%d - %s (%s)", item.getId(), item.getType(), item.getColor()));

                        itemPanel.add(imgLabel);
                        itemPanel.add(descLabel);
                        box.add(itemPanel);
                    }
                }

                painel.add(box);
            }
        }

        // Botões
        JScrollPane scrollPane = new JScrollPane(painel);
        add(scrollPane, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        ThemeManager.aplicarTema(this);
        ThemeManager.registrarJanela(this);
        setVisible(true);
    }

    // Método para carregar imagem com tamanho fixo
    private ImageIcon carregarImagem(String path, int width, int height) {
        try {
            File imgFile = new File(path);
            if (!imgFile.exists()) {
                imgFile = new File("img/placeholder.png");
            }
            ImageIcon icon = new ImageIcon(imgFile.getAbsolutePath());
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            System.out.println("Erro ao carregar imagem: " + e.getMessage());
            return new ImageIcon(); // vazio
        }
    }
}
