package view;

import dao.EmprestimoDAO;
import dao.ItemDAO;
import dao.UsuarioDAO;
import model.Emprestimo;
import model.Item;
import model.ItemType;
import model.Usuario;
import util.Session;
import util.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class LendForm extends JFrame {

    private JComboBox<Item> cbItens;
    private JComboBox<Usuario> cbUsuarios;
    private JButton btnEmprestar;
    private Runnable onUpdate;

    public LendForm() {
        setTitle("Emprestar Item");
        setSize(400, 250);
        setLayout(new GridLayout(4, 1, 10, 10));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        cbItens = new JComboBox<>();
        cbUsuarios = new JComboBox<>();
        btnEmprestar = new JButton("Emprestar");

        carregarItens();
        if (cbItens.getItemCount() == 0)
            return;

        carregarUsuarios();
        if (cbUsuarios.getItemCount() == 0)
            return;

        add(new JLabel("Selecione o item:"));
        add(cbItens);
        add(new JLabel("Emprestar para o usuário:"));
        add(cbUsuarios);
        add(btnEmprestar);

        btnEmprestar.addActionListener(e -> realizarEmprestimo());

        setLocationRelativeTo(null);
        ThemeManager.aplicarTema(this);
        ThemeManager.registrarJanela(this);
        setVisible(true);
    }

    public LendForm(Item itemPreSelecionado, Runnable onUpdate) {
        this();
        this.onUpdate = onUpdate;
        if (itemPreSelecionado != null) {
            cbItens.setSelectedItem(itemPreSelecionado);
        }
    }

    private void carregarItens() {
        ItemDAO itemDAO = new ItemDAO();
        int user_id = Session.getUsuario().getId();
        List<Item> itens = itemDAO.listarEmprestaveis(String.valueOf(user_id));
        cbItens.removeAllItems();
        for (Item item : itens) {
            cbItens.addItem(item);
        }

        if (cbItens.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Você não possui itens disponíveis para empréstimo.");
            dispose();
        }
    }

    private void carregarUsuarios() {
        UsuarioDAO dao = new UsuarioDAO();
        List<Usuario> usuarios = dao.listarTodos();

        for (Usuario u : usuarios) {
            if (u.getId() != Session.getUsuario().getId()) {
                cbUsuarios.addItem(u);
            }
        }

        if (cbUsuarios.getItemCount() == 0) {
            JOptionPane.showMessageDialog(null, "Nenhum outro usuário disponível para empréstimo.");
            dispose();
        }
    }

    private void realizarEmprestimo() {
        Item item = (Item) cbItens.getSelectedItem();
        Usuario destino = (Usuario) cbUsuarios.getSelectedItem();

        if (item == null || destino == null) {
            JOptionPane.showMessageDialog(this, "Selecione o item e o usuário!");
            return;
        }

        Emprestimo emp = new Emprestimo(
                0,
                item.getId(),
                Session.getUsuario().getId(),
                destino.getId(),
                LocalDate.now(),
                null);

        new EmprestimoDAO().registrarEmprestimo(emp);
        JOptionPane.showMessageDialog(this, "Empréstimo realizado com sucesso!");
        if (onUpdate != null) {
            onUpdate.run();
        }
        dispose();
    }
}
