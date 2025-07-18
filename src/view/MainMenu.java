package view;

import util.TemaManager;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends JFrame {

    public MainMenu() {
        setTitle("GVP - Gestor de Vestuário Pessoal");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Botão de tema no topo
        JButton btnTema = new JButton(TemaManager.isModoEscuro() ? "Claro" : "Escuro");
        btnTema.setFocusPainted(false);
        btnTema.setBorderPainted(false);
        btnTema.addActionListener(e -> {
            TemaManager.alternarTema();
            TemaManager.aplicarTema(this);
            btnTema.setText(TemaManager.isModoEscuro() ? "Claro" : "Escuro");
        });

        JPanel painelTopo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        painelTopo.add(btnTema);
        painelTopo.setBackground(UIManager.getColor("Panel.background"));
        add(painelTopo, BorderLayout.NORTH);

        // Painel central com botões
        JPanel painelCentral = new JPanel(new GridLayout(0, 1, 10, 10));
        painelCentral.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton btnCadastrarItem = new JButton("Cadastrar Item");
        JButton btnListarItens = new JButton("Listar Itens");
        JButton btnLavar = new JButton("Registrar Lavagem");
        JButton btnLavagens = new JButton("Histórico de Lavagens");
        JButton btnEstatisticas = new JButton("Estatísticas");
        JButton btnEmprestimos = new JButton("Histórico de Empréstimos");
        JButton btnEmprestar = new JButton("Emprestar Item");
        JButton btnLook = new JButton("Criar Look");
        JButton btnLooks = new JButton("Looks");
        JButton btnLogout = new JButton("Sair / Trocar Usuário");

        // Ações
        btnCadastrarItem.addActionListener(e -> new ItemForm());
        btnListarItens.addActionListener(e -> new ItemList());
        btnLavar.addActionListener(e -> new LavagemForm());
        btnLavagens.addActionListener(e -> new LavagemHistorico());
        btnEstatisticas.addActionListener(e -> new EstatisticasView());
        btnEmprestimos.addActionListener(e -> new EmprestimoHistorico());
        btnEmprestar.addActionListener(e -> new EmprestimoForm());
        btnLook.addActionListener(e -> new LookForm());
        btnLooks.addActionListener(e -> new LookList());
        btnLogout.addActionListener(e -> {
            util.Session.logout();
            dispose();
            new LoginForm();
        });

        // Adiciona os botões ao painel
        painelCentral.add(btnCadastrarItem);
        painelCentral.add(btnListarItens);
        painelCentral.add(btnLavar);
        painelCentral.add(btnLavagens);
        painelCentral.add(btnEstatisticas);
        painelCentral.add(btnEmprestar);
        painelCentral.add(btnEmprestimos);
        painelCentral.add(btnLooks);
        painelCentral.add(btnLook);
        painelCentral.add(btnLogout);

        add(painelCentral, BorderLayout.CENTER);

        setLocationRelativeTo(null); // Centraliza janela
        TemaManager.aplicarTema(this);
        setVisible(true);
    }
}
