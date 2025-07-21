package view;

import dao.ItemDAO;
import dao.LookDAO;
import model.Item;
import model.ItemType;
import model.Look;
import util.Session;
import util.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LookForm extends JFrame {

    private Map<ItemType, JComboBox<Item>> comboMap = new HashMap<>();
    private JTextField nomeField;

    public LookForm() {
        // Definições da Janela
        setTitle("Montar Look");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel painelCampos = new JPanel(new GridLayout(0, 2, 10, 10));
        painelCampos.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        nomeField = new JTextField();
        painelCampos.add(new JLabel("Nome do Look:*"));
        painelCampos.add(nomeField);

        // Carregar itens disponíveis
        ItemDAO itemDAO = new ItemDAO();
        int userId = Session.getUsuario().getId();
        List<Item> itens = itemDAO.listarDisponiveisParaLook(String.valueOf(userId));

        // Criar combos por tipo
        for (ItemType tipo : ItemType.values()) {
            JComboBox<Item> combo = new JComboBox<>();
            if (tipo == ItemType.UNDERWEAR || tipo == ItemType.ACCESSORY) {
                combo.addItem(null); // Permitir "nenhum"
            }

            for (Item item : itens) {
                if (item.getType() == tipo) {
                    combo.addItem(item);
                }
            }

            comboMap.put(tipo, combo);
            painelCampos.add(new JLabel(tipo.toString().charAt(0) + tipo.toString().substring(1).toLowerCase() + ":"));
            painelCampos.add(combo);
        }

        // Botão de salvar
        JButton btnSalvar = new JButton("Salvar Look");
        btnSalvar.addActionListener((ActionEvent e) -> salvarLook());

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBotoes.add(btnSalvar);

        add(painelCampos, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        ThemeManager.aplicarTema(this);
        ThemeManager.registrarJanela(this);
        setVisible(true);
    }

    // Ações
    private void salvarLook() {
        String nome = nomeField.getText().trim();
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Dê um nome ao look!", "Erro", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Item upper = (Item) comboMap.get(ItemType.UPPERBODY).getSelectedItem();
        Item lower = (Item) comboMap.get(ItemType.LOWERBODY).getSelectedItem();
        Item foot = (Item) comboMap.get(ItemType.FOOTWEAR).getSelectedItem();

        if (upper == null || lower == null || foot == null) {
            StringBuilder camposFaltando = new StringBuilder();
            if (upper == null)
                camposFaltando.append("- Parte Superior\n");
            if (lower == null)
                camposFaltando.append("- Parte Inferior\n");
            if (foot == null)
                camposFaltando.append("- Calçado\n");

            JOptionPane.showMessageDialog(this,
                    "Os seguintes campos são obrigatórios:\n" + camposFaltando,
                    "Campos Obrigatórios", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Item underwear = (Item) comboMap.get(ItemType.UNDERWEAR).getSelectedItem();
        Item accessory = (Item) comboMap.get(ItemType.ACCESSORY).getSelectedItem();

        Look look = new Look(
                0,
                Session.getUsuario().getId(),
                nome,
                LocalDate.now(),
                upper.getId(),
                lower.getId(),
                foot.getId(),
                underwear != null ? underwear.getId() : null,
                accessory != null ? accessory.getId() : null);

        LookDAO dao = new LookDAO();
        dao.insert(look);
        JOptionPane.showMessageDialog(this, "Look salvo com sucesso!");
        dispose();
    }
}
