package view;

import model.*;
import util.Session;
import util.ThemeManager;
import dao.ItemDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class ItemForm extends JFrame {

    private JTextField txtColor, txtSize, txtStore, txtImage;
    private JComboBox<ItemType> cbType;
    private JComboBox<ConservationLevel> cbConservation;
    private Item itemEditando;
    private Runnable onSaveCallback;

    public ItemForm() {
        this(null, null); // Construtor padrão para cadastro
    }

    public ItemForm(Item itemParaEditar, Runnable onSaveCallback) {
        this.itemEditando = itemParaEditar;
        this.onSaveCallback = onSaveCallback;

        setTitle(itemEditando == null ? "Cadastrar Item" : "Editar Item");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 400);
        setLayout(new GridLayout(8, 2, 5, 5));

        cbType = new JComboBox<>(ItemType.values());
        txtColor = new JTextField();
        txtSize = new JTextField();
        txtStore = new JTextField();
        txtImage = new JTextField("img/placeholder.png");
        cbConservation = new JComboBox<>(ConservationLevel.values());

        JButton btnImage = new JButton("Selecionar Imagem");
        JButton btnSalvar = new JButton(itemParaEditar == null ? "Salvar" : "Atualizar");

        // Adiciona campos ao formulário
        add(new JLabel("Tipo:"));
        add(cbType);
        add(new JLabel("Cor:*"));
        add(txtColor);
        add(new JLabel("Tamanho:*"));
        add(txtSize);
        add(new JLabel("Loja de Origem:*"));
        add(txtStore);
        add(new JLabel("Imagem:"));
        add(txtImage);
        add(btnImage);
        add(new JLabel("Conservação:"));
        add(cbConservation);
        add(btnSalvar);

        // Botão para escolher imagem
        btnImage.addActionListener((ActionEvent e) -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("img/"));
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                txtImage.setText(selectedFile.getPath());
            }
        });

        // Botão para salvar item
        btnSalvar.addActionListener((ActionEvent e) -> {
            String color = txtColor.getText().trim();
            String size = txtSize.getText().trim();
            String store = txtStore.getText().trim();

            if (color.isEmpty() || size.isEmpty() || store.isEmpty()) {
                String remaningFields = "";
                if (color.isEmpty())
                    remaningFields += "- Cor\n";
                if (size.isEmpty())
                    remaningFields += "- Tamanho\n";
                if (store.isEmpty())
                    remaningFields += "- Loja de origem\n";
                JOptionPane.showMessageDialog(this, "Os seguintes campos são obrigatórios:\n" + remaningFields,
                        "Campos Obrigatórios", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Item item = new Item(
                    itemEditando == null ? 0 : itemEditando.getId(),
                    String.valueOf(util.Session.getUsuario().getId()),
                    (ItemType) cbType.getSelectedItem(),
                    color,
                    size,
                    store,
                    txtImage.getText(),
                    (ConservationLevel) cbConservation.getSelectedItem()) {
            };

            ItemDAO dao = new ItemDAO();

            if (itemEditando == null) {
                dao.insert(item);
                JOptionPane.showMessageDialog(this, "Item cadastrado com sucesso!");
                limparCampos();
            } else {
                dao.update(item);
                JOptionPane.showMessageDialog(this, "Item atualizado com sucesso!");
                dispose();
                if (onSaveCallback != null)
                    onSaveCallback.run();
            }
        });

        // Preencher campos se estiver editando
        if (itemEditando != null) {
            cbType.setSelectedItem(itemEditando.getType());
            txtColor.setText(itemEditando.getColor());
            txtSize.setText(itemEditando.getSize());
            txtStore.setText(itemEditando.getStoreOfOrigin());
            txtImage.setText(itemEditando.getImagePath());
            cbConservation.setSelectedItem(itemEditando.getConservation());
        }

        setLocationRelativeTo(null);
        ThemeManager.aplicarTema(this);
        ThemeManager.registrarJanela(this);
        setVisible(true);
    }

    private void limparCampos() {
        cbType.setSelectedIndex(0);
        txtColor.setText("");
        txtSize.setText("");
        txtStore.setText("");
        txtImage.setText("img/placeholder.png");
        cbConservation.setSelectedIndex(0);
    }
}
