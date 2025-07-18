package dao;

import model.LookUso;
import util.DBConnector;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LookUsoDAO {

    public LookUsoDAO() {
        criarTabela();
    }

    private void criarTabela() {
        String sql = """
                    CREATE TABLE IF NOT EXISTS look_uso (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        look_id INTEGER NOT NULL,
                        data TEXT NOT NULL,
                        turno TEXT NOT NULL,
                        contexto TEXT,
                        FOREIGN KEY (look_id) REFERENCES look(id),
                        FOREIGN KEY (usuario_id) REFERENCES user(id)
                    );
                """;

        try (Connection conn = DBConnector.connect();
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erro ao criar tabela look_uso: " + e.getMessage());
        }
    }

    public void inserir(LookUso uso) {
        String sql = "INSERT INTO look_uso (look_id, data, turno, contexto) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnector.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, uso.getLookId());
            stmt.setString(2, uso.getData().toString());
            stmt.setString(3, uso.getTurno());
            stmt.setString(4, uso.getContexto());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao inserir uso do look: " + e.getMessage());
        }
    }

    public List<LookUso> listarPorLook(int lookId) {
        List<LookUso> lista = new ArrayList<>();
        String sql = "SELECT * FROM look_uso WHERE look_id = ? ORDER BY data DESC";

        try (Connection conn = DBConnector.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, lookId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                LookUso uso = new LookUso(
                        rs.getInt("id"),
                        rs.getInt("look_id"),
                        LocalDate.parse(rs.getString("data")),
                        rs.getString("turno"),
                        rs.getString("contexto"));
                lista.add(uso);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar usos: " + e.getMessage());
        }

        return lista;
    }

    public int contarUsosPorUsuario(String userId) {
        String sql = """
                    SELECT COUNT(*)
                    FROM look_uso lu
                    JOIN look l ON l.id = lu.look_id
                    WHERE l.usuario_id = ?
                """;

        try (Connection conn = DBConnector.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            System.out.println("Erro ao contar usos de looks: " + e.getMessage());
            return 0;
        }
    }

    public String turnoMaisComum(String userId) {
        String sql = """
                    SELECT turno, COUNT(*) as total
                    FROM look_uso lu
                    JOIN look l ON l.id = lu.look_id
                    WHERE l.usuario_id = ?
                    GROUP BY turno
                    ORDER BY total DESC
                    LIMIT 1
                """;

        try (Connection conn = DBConnector.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("turno") + " (" + rs.getInt("total") + " usos)";
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar turno mais comum: " + e.getMessage());
        }

        return "Nenhum uso registrado.";
    }

    public String lookMaisUtilizado(String userId) {
        String sql = """
                    SELECT l.nome, COUNT(*) as total
                    FROM look_uso lu
                    JOIN look l ON l.id = lu.look_id
                    WHERE l.usuario_id = ?
                    GROUP BY l.id
                    ORDER BY total DESC
                    LIMIT 1
                """;

        try (Connection conn = DBConnector.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("nome") + " (" + rs.getInt("total") + " usos)";
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar look mais utilizado: " + e.getMessage());
        }

        return "Nenhum uso registrado.";
    }

}
