package view;

import dao.UserDAO;
import model.User;
import util.Session;
import util.ThemeManager;

import javax.swing.*;
import java.awt.*;

public class LoginForm extends JFrame {

    public LoginForm() {
        // Definições da Janela
        setTitle("Login");
        setSize(300, 200);
        setLayout(new GridLayout(4, 1, 5, 5));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Ações
        JTextField txtLogin = new JTextField();
        JPasswordField txtSenha = new JPasswordField();
        JButton btnEntrar = new JButton("Entrar");
        JButton btnCadastrar = new JButton("Novo Usuário");

        // Botões
        add(new JLabel("Login:"));
        add(txtLogin);
        add(new JLabel("Senha:"));
        add(txtSenha);
        add(btnEntrar);
        add(btnCadastrar);

        // Listennes
        btnEntrar.addActionListener(e -> {
            String login = txtLogin.getText().trim();
            String senha = new String(txtSenha.getPassword());

            UserDAO dao = new UserDAO();
            dao.autenticar(login, senha).ifPresentOrElse(usuario -> {
                Session.login(usuario);
                JOptionPane.showMessageDialog(this, "Bem-vindo, " + usuario.getNome());
                dispose();
                new MainMenu();
            }, () -> JOptionPane.showMessageDialog(this, "Login ou senha inválidos."));
        });

        btnCadastrar.addActionListener(e -> new RegisterUserForm());

        setLocationRelativeTo(null);
        ThemeManager.aplicarTema(this);
        ThemeManager.registrarJanela(this);
        setVisible(true);
    }
}
