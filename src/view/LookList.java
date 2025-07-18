package view;

import dao.ItemDAO;
import dao.LookDAO;
import dao.LookUsoDAO;
import model.Item;
import model.Look;
import model.LookUso;
import util.Session;
import util.TemaManager;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LookList extends JFrame {

    public LookList() {
        setTitle("Looks Montados");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLayout(new BorderLayout());

        JPanel painelCards = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        JScrollPane scroll = new JScrollPane(painelCards);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        int userId = Session.getUsuario().getId();
        LookDAO lookDAO = new LookDAO();
        ItemDAO itemDAO = new ItemDAO();

        List<Look> looks = lookDAO.listarPorUsuario(userId);
        List<Item> todosItens = itemDAO.listarTodos(String.valueOf(userId));

        Map<Integer, Item> itemMap = todosItens.stream()
                .collect(Collectors.toMap(Item::getId, i -> i));

        for (Look look : looks) {
            JPanel card = criarCardDoLook(look, itemMap, lookDAO, painelCards);
            painelCards.add(card);
        }

        if (looks.isEmpty()) {
            JLabel nenhum = new JLabel("Nenhum look montado ainda.");
            nenhum.setAlignmentX(Component.CENTER_ALIGNMENT);
            painelCards.add(nenhum);
        }

        JButton btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> dispose());
        add(btnFechar, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        TemaManager.aplicarTema(this);
        TemaManager.registrarJanela(this);
        setVisible(true);
    }

    private JPanel criarCardDoLook(Look look, Map<Integer, Item> itemMap, LookDAO lookDAO, JPanel painelPai) {
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(280, 500)); // Altura ajustada
        card.setBorder(BorderFactory.createTitledBorder(look.getNome()));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);

        // Data
        JLabel data = new JLabel(look.getDataCriacao().toString());
        data.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(data);
        card.add(Box.createVerticalStrut(8)); // Espaço após data

        // Mostrar imagens dos itens principais
        int[] ids = { look.getUpperbodyId(), look.getLowerbodyId(), look.getFootwearId() };
        for (int id : ids) {
            Item item = new ItemDAO().buscarPorId(id);
            if (item != null) {
                JLabel img = carregarImagem(item);
                card.add(img);
                card.add(Box.createVerticalStrut(5));
            }
        }

        card.add(Box.createVerticalGlue()); // empurra os botões para o fim

        // Botões organizados em coluna
        JPanel botoes = new JPanel();
        botoes.setLayout(new GridLayout(3, 1, 5, 5));
        botoes.setMaximumSize(new Dimension(240, 100));
        botoes.setOpaque(false);

        JButton btnVerDetalhes = new JButton("Ver Detalhes");
        JButton btnRegistrarUso = new JButton("Registrar Uso");
        JButton btnExcluir = new JButton("Excluir");

        btnVerDetalhes.addActionListener(e -> mostrarDetalhes(look, itemMap));
        btnRegistrarUso.addActionListener(e -> new LookUsoForm(this, look));
        btnExcluir.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(card,
                    "Tem certeza que deseja excluir este look?", "Confirmação",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                lookDAO.deletar(look.getId());
                painelPai.remove(card);
                painelPai.revalidate();
                painelPai.repaint();
                JOptionPane.showMessageDialog(this, "Look excluído com sucesso!");
            }
        });

        botoes.add(btnVerDetalhes);
        botoes.add(btnRegistrarUso);
        botoes.add(btnExcluir);

        card.add(Box.createVerticalStrut(10));
        card.add(botoes);
        card.add(Box.createVerticalStrut(10));

        return card;
    }

    private void mostrarDetalhes(Look look, Map<Integer, Item> itemMap) {
        JDialog dialog = new JDialog(this, "Detalhes do Look: " + look.getNome(), true);
        dialog.setSize(500, 650);
        dialog.setLocationRelativeTo(this);

        JPanel painelPrincipal = new JPanel();
        painelPrincipal.setLayout(new BoxLayout(painelPrincipal, BoxLayout.Y_AXIS));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        painelPrincipal.setBackground(Color.WHITE);

        JLabel titulo = new JLabel("📅 Criado em: " + look.getDataCriacao());
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelPrincipal.add(titulo);
        painelPrincipal.add(Box.createVerticalStrut(10));

        // Itens do look
        int[] ids = {
                look.getUpperbodyId(),
                look.getLowerbodyId(),
                look.getFootwearId()
        };
        if (look.getUnderwearId() != null)
            ids = append(ids, look.getUnderwearId());
        if (look.getAccessoryId() != null)
            ids = append(ids, look.getAccessoryId());

        for (int id : ids) {
            Item item = new ItemDAO().buscarPorId(id);
            if (item != null) {
                painelPrincipal.add(criarBlocoComImagem(item));
                painelPrincipal.add(Box.createVerticalStrut(10));
            }
        }

        // 🔹 Usos do look
        painelPrincipal.add(new JSeparator());
        painelPrincipal.add(Box.createVerticalStrut(10));
        JLabel usosLabel = new JLabel("📖 Usos registrados:");
        usosLabel.setFont(usosLabel.getFont().deriveFont(Font.BOLD));
        usosLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelPrincipal.add(usosLabel);
        painelPrincipal.add(Box.createVerticalStrut(5));

        LookUsoDAO usoDAO = new LookUsoDAO();
        List<LookUso> usos = usoDAO.listarPorLook(look.getId());

        if (usos.isEmpty()) {
            JLabel nenhum = new JLabel("Ainda não há registros de uso.");
            nenhum.setAlignmentX(Component.CENTER_ALIGNMENT);
            painelPrincipal.add(nenhum);
        } else {
            for (LookUso uso : usos) {
                JLabel lbl = new JLabel(uso.toString());
                lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
                painelPrincipal.add(lbl);
            }
        }

        painelPrincipal.add(Box.createVerticalStrut(20));

        JButton btnFechar = new JButton("Fechar");
        btnFechar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnFechar.addActionListener(e -> dialog.dispose());
        painelPrincipal.add(btnFechar);

        JScrollPane scroll = new JScrollPane(painelPrincipal);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        dialog.add(scroll);
        dialog.setVisible(true);
    }

    private JPanel criarBlocoComImagem(Item item) {
        JPanel bloco = new JPanel();
        bloco.setLayout(new BoxLayout(bloco, BoxLayout.Y_AXIS));
        bloco.setAlignmentX(Component.CENTER_ALIGNMENT);
        bloco.setOpaque(false);

        try {
            JLabel label = carregarImagem(item);

            bloco.add(label);
            bloco.add(Box.createVerticalStrut(5));

        } catch (Exception e) {
            bloco.add(new JLabel("Erro ao carregar imagem de " + item.getType()));
        }

        return bloco;
    }

    private JLabel carregarImagem(Item item) {
        try {
            ImageIcon icon = new ImageIcon(new File(item.getImagePath()).getAbsolutePath());
            Image img = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(img);

            JLabel label = new JLabel(scaledIcon, JLabel.CENTER);
            label.setText(item.getType() + ": " + item.getColor() + " - " + item.getSize());
            label.setHorizontalTextPosition(JLabel.CENTER);
            label.setVerticalTextPosition(JLabel.TOP);
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            return label;
        } catch (Exception e) {
            return new JLabel("Erro ao carregar imagem de " + item.getType());
        }
    }

    private int[] append(int[] arr, int novo) {
        int[] novoArr = new int[arr.length + 1];
        System.arraycopy(arr, 0, novoArr, 0, arr.length);
        novoArr[arr.length] = novo;
        return novoArr;
    }
}
