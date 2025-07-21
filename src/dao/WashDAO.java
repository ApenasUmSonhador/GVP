package dao;

import model.Lavagem;
import util.DBConnector;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WashDAO {

    public WashDAO() {
        criarTabelas();
    }

    private void criarTabelas() {
        String sqlLavagem = """
                    CREATE TABLE IF NOT EXISTS lavagem (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        data TEXT NOT NULL
                    );
                """;

        String sqlLavagemItem = """
                    CREATE TABLE IF NOT EXISTS lavagem_item (
                        lavagem_id INTEGER,
                        item_id INTEGER,
                        FOREIGN KEY (lavagem_id) REFERENCES lavagem(id)
                    );
                """;

        try (Connection conn = DBConnector.connect();
                Statement stmt = conn.createStatement()) {
            stmt.execute(sqlLavagem);
            stmt.execute(sqlLavagemItem);
        } catch (SQLException e) {
            System.out.println("Erro ao criar tabelas de lavagem: " + e.getMessage());
        }
    }

    public void inserirLavagem(Lavagem lavagem) {
        String insertLavagem = "INSERT INTO lavagem (data) VALUES (?)";
        String insertLavagemItem = "INSERT INTO lavagem_item (lavagem_id, item_id) VALUES (?, ?)";

        try (Connection conn = DBConnector.connect()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt1 = conn.prepareStatement(insertLavagem)) {
                pstmt1.setString(1, lavagem.getData().toString());
                pstmt1.executeUpdate();

                // Alternativa compatível com SQLite
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid();");
                int lavagemId = -1;
                if (rs.next()) {
                    lavagemId = rs.getInt(1);
                }

                try (PreparedStatement pstmt2 = conn.prepareStatement(insertLavagemItem)) {
                    for (int itemId : lavagem.getItemIds()) {
                        pstmt2.setInt(1, lavagemId);
                        pstmt2.setInt(2, itemId);
                        pstmt2.addBatch();
                    }
                    pstmt2.executeBatch();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.out.println("Erro ao registrar lavagem: " + e.getMessage());
        }
    }

    public List<Lavagem> getLavagensDoUsuario(String ownerId) {
        List<Lavagem> lavagens = new ArrayList<>();

        String sqlLavagem = "SELECT * FROM lavagem ORDER BY data DESC";
        String sqlItens = """
                    SELECT i.id, i.type, i.color
                    FROM lavagem_item li
                    JOIN item i ON i.id = li.item_id
                    WHERE li.lavagem_id = ? AND i.owner_id = ?
                """;

        try (Connection conn = DBConnector.connect();
                Statement stmt1 = conn.createStatement();
                ResultSet rs1 = stmt1.executeQuery(sqlLavagem)) {

            while (rs1.next()) {
                int lavagemId = rs1.getInt("id");
                LocalDate data = LocalDate.parse(rs1.getString("data"));
                List<Integer> itemIds = new ArrayList<>();

                try (PreparedStatement stmt2 = conn.prepareStatement(sqlItens)) {
                    stmt2.setInt(1, lavagemId);
                    stmt2.setString(2, ownerId);
                    ResultSet rs2 = stmt2.executeQuery();
                    while (rs2.next()) {
                        itemIds.add(rs2.getInt("id"));
                    }
                }

                if (!itemIds.isEmpty()) {
                    lavagens.add(new Lavagem(lavagemId, data, itemIds));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar lavagens: " + e.getMessage());
        }

        return lavagens;
    }

    public int contarPorUsuario(String userId) {
        String sql = """
                    SELECT COUNT(DISTINCT li.lavagem_id)
                    FROM lavagem_item li
                    JOIN item i ON i.id = li.item_id
                    WHERE i.owner_id = ?
                """;
        try (Connection conn = DBConnector.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            System.out.println("Erro ao contar lavagens: " + e.getMessage());
            return 0;
        }
    }
}
