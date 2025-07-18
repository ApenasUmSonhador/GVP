package view;

import dao.ItemDAO;
import model.Item;
import util.Session;
import util.TemaManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ItemList extends JFrame {

    private JPanel painel;

    public ItemList() {
        setTitle("Meus Itens");
        setSize(800, 600);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(painel);
        add(scrollPane, BorderLayout.CENTER);

        JButton btnNovo = new JButton("Cadastrar Novo Item");
        btnNovo.addActionListener(e -> new ItemForm(null, this::atualizarItens));
        add(btnNovo, BorderLayout.SOUTH);

        atualizarItens();
        TemaManager.aplicarTema(this);
        TemaManager.registrarJanela(this);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void atualizarItens() {
        painel.removeAll();
        painel.repaint();

        int userId = Session.getUsuario().getId();
        ItemDAO itemDAO = new ItemDAO();

        // Itens próprios (exceto os que foram emprestados)
        List<Item> meus = itemDAO.listarPorDono(String.valueOf(userId));

        // Itens emprestados para mim
        List<Item> recebidos = itemDAO.listarItensEmprestadosPara(String.valueOf(userId));

        List<Item> todos = new ArrayList<>();
        todos.addAll(meus);
        todos.addAll(recebidos);

        if (todos.isEmpty()) {
            painel.add(new JLabel("Nenhum item encontrado."));
        } else {
            for (Item item : todos) {
                painel.add(new ItemCard(item, this::atualizarItens));
            }
        }

        painel.revalidate();
        painel.repaint();
    }
}
