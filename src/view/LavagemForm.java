package view;

import dao.ItemDAO;
import dao.LavagemDAO;
import model.*;
import util.TemaManager;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LavagemForm extends JFrame {

    private JPanel checkboxPanel;
    private JButton btnRegistrar;
    private List<JCheckBox> checkboxes;
    private List<Item> itensLavaveis;
    private final int usuarioLogado = util.Session.getUsuario().getId();

    public LavagemForm() {
        setTitle("Registrar Lavagem");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
        checkboxes = new ArrayList<>();

        carregarItensLavaveis();
        if (itensLavaveis.isEmpty())
            return;

        JScrollPane scrollPane = new JScrollPane(checkboxPanel);
        add(scrollPane, BorderLayout.CENTER);

        btnRegistrar = new JButton("Registrar Lavagem");
        btnRegistrar.addActionListener(e -> registrarLavagem());
        add(btnRegistrar, BorderLayout.SOUTH);

        TemaManager.aplicarTema(this);
        TemaManager.registrarJanela(this);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void carregarItensLavaveis() {
        List<Item> todos = new ItemDAO().listarLavaveis(String.valueOf(usuarioLogado));
        this.itensLavaveis = new ArrayList<>();
        for (Item i : todos) {
            this.itensLavaveis.add(i);

            JCheckBox cb = new JCheckBox(
                    String.format("ID %d - %s (%s)", i.getId(), i.getType(), i.getColor()));
            checkboxPanel.add(cb);
            checkboxes.add(cb);
        }

        if (itensLavaveis.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Você não possui itens disponíveis para lavar no momento.");
            dispose();
        }

    }

    private void registrarLavagem() {
        List<Integer> selecionados = new ArrayList<>();

        for (int i = 0; i < checkboxes.size(); i++) {
            if (checkboxes.get(i).isSelected()) {
                selecionados.add(itensLavaveis.get(i).getId());
            }
        }

        if (selecionados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione ao menos um item para lavar.");
            return;
        }

        Lavagem lavagem = new Lavagem(0, LocalDate.now(), selecionados);
        new LavagemDAO().inserirLavagem(lavagem);

        JOptionPane.showMessageDialog(this, "Lavagem registrada com sucesso!");
        dispose();
    }
}
