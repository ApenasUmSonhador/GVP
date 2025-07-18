package dao;

import model.Look;
import util.DBConnector;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LookDAO {

    public LookDAO() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = """
                CREATE TABLE IF NOT EXISTS look (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    nome TEXT NOT NULL,
                    data_criacao TEXT NOT NULL,
                    upperbody_id INTEGER NOT NULL,
                    lowerbody_id INTEGER NOT NULL,
                    footwear_id INTEGER NOT NULL,
                    underwear_id INTEGER,
                    accessory_id INTEGER,
                    FOREIGN KEY(user_id) REFERENCES usuario(id),
                    FOREIGN KEY(upperbody_id) REFERENCES item(id),
                    FOREIGN KEY(lowerbody_id) REFERENCES item(id),
                    FOREIGN KEY(footwear_id) REFERENCES item(id),
                    FOREIGN KEY(underwear_id) REFERENCES item(id),
                    FOREIGN KEY(accessory_id) REFERENCES item(id)
                );""";

        try (Connection conn = DBConnector.connect();
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erro ao criar tabela Look: " + e.getMessage());
        }
    }

    public void insert(Look look) {
        String sql = """
                    INSERT INTO look (user_id, nome, data_criacao,
                    upperbody_id, lowerbody_id, footwear_id, underwear_id, accessory_id)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DBConnector.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, look.getUserId());
            stmt.setString(2, look.getNome());
            stmt.setString(3, look.getDataCriacao().toString());
            stmt.setInt(4, look.getUpperbodyId());
            stmt.setInt(5, look.getLowerbodyId());
            stmt.setInt(6, look.getFootwearId());
            if (look.getUnderwearId() != null)
                stmt.setInt(7, look.getUnderwearId());
            else
                stmt.setNull(7, Types.INTEGER);
            if (look.getAccessoryId() != null)
                stmt.setInt(8, look.getAccessoryId());
            else
                stmt.setNull(8, Types.INTEGER);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao inserir Look: " + e.getMessage());
        }
    }

    public List<Look> listarPorUsuario(int userId) {
        List<Look> lista = new ArrayList<>();
        String sql = "SELECT * FROM look WHERE user_id = ? ORDER BY data_criacao DESC";

        try (Connection conn = DBConnector.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Look look = new Look(
                        rs.getInt("id"),
                        userId,
                        rs.getString("nome"),
                        LocalDate.parse(rs.getString("data_criacao")),
                        rs.getInt("upperbody_id"),
                        rs.getInt("lowerbody_id"),
                        rs.getInt("footwear_id"),
                        rs.getObject("underwear_id") != null ? rs.getInt("underwear_id") : null,
                        rs.getObject("accessory_id") != null ? rs.getInt("accessory_id") : null);
                lista.add(look);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar Looks: " + e.getMessage());
        }

        return lista;
    }

    public void deletar(int id) {
        String sql = "DELETE FROM look WHERE id = ?";
        try (Connection conn = DBConnector.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao deletar look: " + e.getMessage());
        }
    }

    public int contarPorUsuario(String userId) {
        String sql = "SELECT COUNT(*) FROM look WHERE user_id = ?";
        try (Connection conn = DBConnector.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            System.out.println("Erro ao contar looks: " + e.getMessage());
            return 0;
        }
    }

}
