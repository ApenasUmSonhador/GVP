package view;

import dao.EmprestimoDAO;
import dao.ItemDAO;
import model.Item;
import util.Session;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.time.LocalDate;

public class ItemCard extends JPanel {

    public ItemCard(Item item, Runnable onUpdate) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        setPreferredSize(new Dimension(750, 120));

        int userId = Session.getUsuario().getId();
        boolean souDono = String.valueOf(userId).equals(item.getOwnerId());

        // Painel da imagem
        JLabel imgLabel = new JLabel();
        imgLabel.setPreferredSize(new Dimension(100, 100));
        try {
            BufferedImage img = ImageIO.read(new File(item.getImagePath()));
            Image scaled = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            imgLabel.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            imgLabel.setText("Sem imagem");
        }

        // Painel de informações
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.add(new JLabel("Tipo: " + item.getType()));
        infoPanel.add(new JLabel("Cor: " + item.getColor()));
        infoPanel.add(new JLabel("Tamanho: " + item.getSize()));
        infoPanel.add(new JLabel("Marca: " + item.getStoreOfOrigin()));
        infoPanel.add(new JLabel("Conservação: " + item.getConservation()));
        if (!souDono) {
            infoPanel.add(new JLabel("Emprestado para você"));
        }

        // Painel de botões
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        EmprestimoDAO emprestimoDAO = new EmprestimoDAO();

        if (souDono) {
            JButton btnEditar = new JButton("Editar");
            JButton btnExcluir = new JButton("Excluir");

            btnEditar.addActionListener(e -> new ItemForm(item, onUpdate));

            btnExcluir.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Tem certeza que deseja excluir este item?", "Confirmação",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    new ItemDAO().delete(item.getId());
                    onUpdate.run();
                }
            });

            btnPanel.add(btnEditar);
            btnPanel.add(btnExcluir);

            // Botão de empréstimo (se não estiver emprestado)
            boolean estaEmprestado = emprestimoDAO.estaEmprestado(item.getId());
            if (!estaEmprestado) {
                JButton btnEmprestar = new JButton("Emprestar");
                btnEmprestar.addActionListener(e -> new EmprestimoForm(item, onUpdate));
                btnPanel.add(btnEmprestar);
            }

        } else {
            JButton btnDevolver = new JButton("Devolver");

            btnDevolver.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Deseja realmente devolver este item ao dono?",
                        "Confirmar Devolução",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    emprestimoDAO.registrarDevolucao(item.getId(), LocalDate.now());
                    JOptionPane.showMessageDialog(this, "Item devolvido com sucesso!");
                    onUpdate.run();
                }
            });

            btnPanel.add(btnDevolver);
        }

        // Montar layout
        add(imgLabel, BorderLayout.WEST);
        add(infoPanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.EAST);
    }
}
