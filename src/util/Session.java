package util;

import model.User;

public class Session {
    private static User usuarioLogado;

    public static void login(User usuario) {
        usuarioLogado = usuario;
    }

    public static User getUsuario() {
        return usuarioLogado;
    }

    public static void logout() {
        usuarioLogado = null;
    }

}
