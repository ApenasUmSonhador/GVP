package view;

import dao.UserDAO;
import model.User;

import javax.swing.*;
import java.awt.*;

public class RegisterUserForm extends JFrame {

    public RegisterUserForm() {
        setTitle("Cadastro de Usuário");
        setSize(350, 250);
        setLayout(new GridLayout(5, 2, 5, 5));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextField txtNome = new JTextField();
        JTextField txtLogin = new JTextField();
        JPasswordField txtSenha = new JPasswordField();
        JButton btnCadastrar = new JButton("Cadastrar");

        add(new JLabel("Nome:"));
        add(txtNome);
        add(new JLabel("Login:"));
        add(txtLogin);
        add(new JLabel("Senha:"));
        add(txtSenha);
        add(btnCadastrar);

        btnCadastrar.addActionListener(e -> {
            String nome = txtNome.getText().trim();
            String login = txtLogin.getText().trim();
            String senha = new String(txtSenha.getPassword());

            if (nome.isEmpty() || login.isEmpty() || senha.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos!");
                return;
            }

            User usuario = new User(0, nome, login, senha);
            boolean sucesso = new UserDAO().cadastrar(usuario);
            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Usuário cadastrado com sucesso!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao cadastrar. Login pode já estar em uso.");
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
