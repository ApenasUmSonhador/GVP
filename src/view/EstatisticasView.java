package view;

import dao.*;
import model.Item;
import model.ItemType;
import util.Session;
import util.TemaManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EstatisticasView extends JFrame {

    public EstatisticasView() {
        setTitle("Estatísticas do Guarda-Roupa");
        setSize(650, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        String userId = String.valueOf(Session.getUsuario().getId());

        ItemDAO itemDAO = new ItemDAO();
        LavagemDAO lavagemDAO = new LavagemDAO();
        LookDAO lookDAO = new LookDAO();
        EmprestimoDAO emprestimoDAO = new EmprestimoDAO();
        LookUsoDAO usoDAO = new LookUsoDAO();

        List<Item> itens = itemDAO.listarPorDono(userId);
        int totalItens = itens.size();
        int totalLavagens = lavagemDAO.contarPorUsuario(userId);
        int totalLooks = lookDAO.contarPorUsuario(userId);
        int emprestimosFeitos = emprestimoDAO.contarFeitos(userId);
        int emprestimosRecebidos = emprestimoDAO.contarRecebidos(userId);
        int totalUsos = usoDAO.contarUsosPorUsuario(userId);
        String maisUsado = usoDAO.lookMaisUtilizado(userId);
        String turnoPopular = usoDAO.turnoMaisComum(userId);

        JPanel painelGeral = new JPanel();
        painelGeral.setLayout(new BoxLayout(painelGeral, BoxLayout.Y_AXIS));
        painelGeral.setBorder(new EmptyBorder(15, 15, 15, 15));
        painelGeral.setBackground(Color.WHITE);

        // RESUMO
        JPanel painelResumo = new JPanel(new GridLayout(0, 1, 5, 5));
        painelResumo.setBorder(BorderFactory.createTitledBorder("Resumo Geral"));
        painelResumo.setBackground(Color.WHITE);

        painelResumo.add(new JLabel("Total de Itens: " + totalItens));
        painelResumo.add(new JLabel("Total de Lavagens: " + totalLavagens));
        painelResumo.add(new JLabel("Empréstimos Feitos: " + emprestimosFeitos));
        painelResumo.add(new JLabel("Empréstimos Recebidos: " + emprestimosRecebidos));
        painelResumo.add(new JLabel("Looks Montados: " + totalLooks));

        painelGeral.add(painelResumo);
        painelGeral.add(Box.createVerticalStrut(15));

        // DISTRIBUIÇÃO POR TIPO
        JPanel painelGrafico = new JPanel(new GridLayout(0, 1, 5, 5));
        painelGrafico.setBorder(BorderFactory.createTitledBorder("Distribuição por Tipo de Item"));
        painelGrafico.setBackground(Color.WHITE);

        Map<ItemType, Integer> contadorTipo = new HashMap<>();
        for (ItemType tipo : ItemType.values()) {
            contadorTipo.put(tipo, 0);
        }
        for (Item item : itens) {
            contadorTipo.put(item.getType(), contadorTipo.get(item.getType()) + 1);
        }

        for (ItemType tipo : ItemType.values()) {
            int qtde = contadorTipo.get(tipo);
            int percent = totalItens == 0 ? 0 : (qtde * 100 / totalItens);
            JProgressBar barra = new JProgressBar(0, 100);
            barra.setValue(percent);
            barra.setStringPainted(true);
            barra.setString(tipo.name() + ": " + qtde + " (" + percent + "%)");
            painelGrafico.add(barra);
        }

        painelGeral.add(painelGrafico);
        painelGeral.add(Box.createVerticalStrut(15));

        // USOS DE LOOKS
        JPanel painelUso = new JPanel();
        painelUso.setLayout(new BoxLayout(painelUso, BoxLayout.Y_AXIS));
        painelUso.setBorder(BorderFactory.createTitledBorder("Usos de Looks"));
        painelUso.setBackground(Color.WHITE);

        painelUso.add(new JLabel("Total de usos registrados: " + totalUsos));
        painelUso.add(new JLabel("Look mais utilizado: " + maisUsado));
        painelUso.add(new JLabel("Turno mais comum: " + turnoPopular));

        painelGeral.add(painelUso);
        painelGeral.add(Box.createVerticalStrut(15));

        JScrollPane scrollPane = new JScrollPane(painelGeral);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        JButton btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> dispose());
        JPanel painelBotao = new JPanel();
        painelBotao.add(btnFechar);
        add(painelBotao, BorderLayout.SOUTH);

        TemaManager.aplicarTema(this);
        TemaManager.registrarJanela(this);
        setVisible(true);
    }
}
