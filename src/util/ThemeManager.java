package util;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ThemeManager {

    private static boolean modoEscuro = false;

    private static final List<Window> janelasAbertas = new ArrayList<>();

    public static void registrarJanela(Window janela) {
        janelasAbertas.add(janela);
    }

    public static void alternarTema() {
        modoEscuro = !modoEscuro;
        for (Window janela : janelasAbertas) {
            aplicarTema(janela);
        }
    }

    public static boolean isModoEscuro() {
        return modoEscuro;
    }

    public static void aplicarTema(Component componente) {
        Color bg = modoEscuro ? Color.DARK_GRAY : Color.WHITE;
        Color fg = modoEscuro ? Color.WHITE : Color.BLACK;

        aplicarTemaRecursivo(componente, bg, fg);
        componente.repaint();
        componente.revalidate();
    }

    private static void aplicarTemaRecursivo(Component componente, Color bg, Color fg) {
        if (componente instanceof JPanel || componente instanceof JFrame || componente instanceof JDialog) {
            componente.setBackground(bg);
        }
        if (componente instanceof JLabel || componente instanceof JButton || componente instanceof JTextField) {
            componente.setForeground(fg);
            componente.setBackground(bg);
        }

        if (componente instanceof Container) {
            for (Component filho : ((Container) componente).getComponents()) {
                aplicarTemaRecursivo(filho, bg, fg);
            }
        }
    }
}
