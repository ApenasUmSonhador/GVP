package dao;

import model.ConservationLevel;
import model.Emprestimo;
import model.EmprestimoComInfo;
import model.Item;
import model.ItemType;
import util.DBConnector;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmprestimoDAO {

    public EmprestimoDAO() {
        criarTabela();
    }

    private void criarTabela() {
        String sql = """
                    CREATE TABLE IF NOT EXISTS emprestimo (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        item_id INTEGER NOT NULL,
                        de_usuario_id INTEGER NOT NULL,
                        para_usuario_id INTEGER NOT NULL,
                        data_emprestimo TEXT NOT NULL,
                        data_devolucao TEXT
                    );
                """;
        try (Connection conn = DBConnector.connect();
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erro ao criar tabela emprestimo: " + e.getMessage());
        }
    }

    public void registrarEmprestimo(Emprestimo emp) {
        String sql = """
                    INSERT INTO emprestimo (item_id, de_usuario_id, para_usuario_id, data_emprestimo)
                    VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = DBConnector.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, emp.getItemId());
            pstmt.setInt(2, emp.getDeUsuarioId());
            pstmt.setInt(3, emp.getParaUsuarioId());
            pstmt.setString(4, emp.getDataEmprestimo().toString());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao registrar empréstimo: " + e.getMessage());
        }
    }

    public void registrarDevolucao(int emprestimoId) {
        String sql = "UPDATE emprestimo SET data_devolucao = ? WHERE id = ?";
        try (Connection conn = DBConnector.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, LocalDate.now().toString());
            pstmt.setInt(2, emprestimoId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao registrar devolução: " + e.getMessage());
        }
    }

    public List<Integer> getIdsDeItensEmprestados() {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT item_id FROM emprestimo WHERE data_devolucao IS NULL";

        try (Connection conn = DBConnector.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ids.add(rs.getInt("item_id"));
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar itens emprestados: " + e.getMessage());
        }

        return ids;
    }

    public void registrarDevolucao(int itemId, LocalDate data) {
        String sql = "UPDATE emprestimo SET data_devolucao = ? WHERE item_id = ? AND data_devolucao IS NULL";
        try (Connection conn = DBConnector.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, data.toString());
            stmt.setInt(2, itemId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao registrar devolução: " + e.getMessage());
        }
    }

    public boolean estaEmprestado(int itemId) {
        String sql = "SELECT COUNT(*) FROM emprestimo WHERE item_id = ? AND data_devolucao IS NULL";
        try (Connection conn = DBConnector.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, itemId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao verificar empréstimo: " + e.getMessage());
        }
        return false;
    }

    public List<EmprestimoComInfo> listarHistoricoCompletoPara(int usuarioId) {
        List<EmprestimoComInfo> lista = new ArrayList<>();

        String sql = """
                    SELECT e.*, i.*,
                           u1.nome AS de_nome,
                           u2.nome AS para_nome
                    FROM emprestimo e
                    JOIN item i ON e.item_id = i.id
                    JOIN usuario u1 ON i.owner_id = u1.id
                    JOIN usuario u2 ON e.para_usuario_id = u2.id
                    WHERE i.owner_id = ? OR e.para_usuario_id = ?
                    ORDER BY data_emprestimo DESC
                """;

        try (Connection conn = DBConnector.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, usuarioId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Item item = new Item(
                        rs.getInt("item_id"),
                        String.valueOf(rs.getInt("owner_id")),
                        ItemType.valueOf(rs.getString("type")),
                        rs.getString("color"),
                        rs.getString("size"),
                        rs.getString("store_of_origin"),
                        rs.getString("image_path"),
                        ConservationLevel.valueOf(rs.getString("conservation"))) {
                };

                LocalDate dataEmp = LocalDate.parse(rs.getString("data_emprestimo"));
                LocalDate dataDev = rs.getString("data_devolucao") != null
                        ? LocalDate.parse(rs.getString("data_devolucao"))
                        : null;

                lista.add(new EmprestimoComInfo(
                        item,
                        rs.getString("de_nome"),
                        rs.getString("para_nome"),
                        dataEmp,
                        dataDev));
            }

        } catch (SQLException e) {
            System.out.println("Erro no histórico completo: " + e.getMessage());
        }

        return lista;
    }

    public int contarFeitos(String userId) {
        String sql = "SELECT COUNT(*) FROM emprestimo WHERE de_usuario_id = ?";
        try (Connection conn = DBConnector.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            System.out.println("Erro ao contar empréstimos feitos: " + e.getMessage());
            return 0;
        }
    }

    public int contarRecebidos(String userId) {
        String sql = "SELECT COUNT(*) FROM emprestimo WHERE para_usuario_id = ?";
        try (Connection conn = DBConnector.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            System.out.println("Erro ao contar empréstimos recebidos: " + e.getMessage());
            return 0;
        }
    }

}
