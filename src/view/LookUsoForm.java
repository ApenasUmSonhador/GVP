package view;

import dao.LookUsoDAO;
import model.Look;
import model.LookUso;
import util.TemaManager;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class LookUsoForm extends JDialog {

    private final Look look;

    public LookUsoForm(JFrame parent, Look look) {
        super(parent, "Registrar Uso do Look: " + look.getNome(), true);
        this.look = look;

        setSize(400, 300);
        setLayout(new GridLayout(5, 2, 10, 10));
        setLocationRelativeTo(parent);

        JTextField txtData = new JTextField(LocalDate.now().toString());
        JComboBox<String> cbTurno = new JComboBox<>(new String[] { "manhã", "tarde", "noite" });
        JTextArea txtContexto = new JTextArea(3, 20);
        JScrollPane scroll = new JScrollPane(txtContexto);

        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");

        add(new JLabel("Data (AAAA-MM-DD):"));
        add(txtData);
        add(new JLabel("Turno:"));
        add(cbTurno);
        add(new JLabel("Contexto:"));
        add(scroll);
        add(btnSalvar);
        add(btnCancelar);

        btnSalvar.addActionListener(e -> {
            try {
                LocalDate data = LocalDate.parse(txtData.getText().trim());
                String turno = (String) cbTurno.getSelectedItem();
                String contexto = txtContexto.getText().trim();

                if (turno == null || turno.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Escolha o turno!", "Erro", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                LookUso uso = new LookUso(0, look.getId(), data, turno, contexto);
                new LookUsoDAO().inserir(uso);
                JOptionPane.showMessageDialog(this, "Uso registrado com sucesso!");
                dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao registrar uso: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> dispose());
        TemaManager.aplicarTema(this);
        TemaManager.registrarJanela(this);
        setVisible(true);
    }
}
