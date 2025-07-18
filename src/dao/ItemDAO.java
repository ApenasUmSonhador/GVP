package dao;

import model.*;
import util.DBConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemDAO {

    public ItemDAO() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = """
                    CREATE TABLE IF NOT EXISTS item (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        owner_id TEXT NOT NULL,
                        type TEXT NOT NULL,
                        color TEXT,
                        size TEXT,
                        store_of_origin TEXT,
                        image_path TEXT,
                        conservation TEXT
                    );
                """;

        try (Connection conn = DBConnector.connect();
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erro ao criar tabela item: " + e.getMessage());
        }
    }

    public List<Item> listarTodos(String ownerId) {
        List<Item> lista = new ArrayList<>();
        // Itens próprios (exceto os que foram emprestados)
        List<Item> meus = listarPorDono(String.valueOf(ownerId));

        // Itens emprestados para mim
        List<Item> recebidos = listarItensEmprestadosPara(ownerId);

        lista.addAll(meus);
        lista.addAll(recebidos);
        return lista;
    }

    public List<Item> listarItensDosLooksDoUsuario(String userId) {
        List<Item> lista = new ArrayList<>();

        String sql = """
                    SELECT DISTINCT i.*
                    FROM item i
                    JOIN look l ON
                        i.id IN (
                            l.upperbody_id,
                            l.lowerbody_id,
                            l.footwear_id,
                            COALESCE(l.underwear_id, -1),
                            COALESCE(l.accessory_id, -1)
                        )
                    WHERE l.user_id = ?
                """;

        try (Connection conn = DBConnector.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Item item = montarItem(rs); // Usa o método já existente que monta o objeto
                lista.add(item);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar itens dos looks: " + e.getMessage());
        }

        return lista;
    }

    public List<Item> listarPorDono(String ownerId) {
        List<Item> lista = new ArrayList<>();
        EmprestimoDAO emprestimoDAO = new EmprestimoDAO();
        List<Integer> idsEmprestados = emprestimoDAO.getIdsDeItensEmprestados();

        String sql = "SELECT * FROM item WHERE owner_id = ?";
        try (Connection conn = DBConnector.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ownerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int itemId = rs.getInt("id");
                if (!idsEmprestados.contains(itemId)) {
                    Item item = new Item(
                            itemId,
                            rs.getString("owner_id"),
                            ItemType.valueOf(rs.getString("type")),
                            rs.getString("color"),
                            rs.getString("size"),
                            rs.getString("store_of_origin"),
                            rs.getString("image_path"),
                            ConservationLevel.valueOf(rs.getString("conservation"))) {
                    };
                    lista.add(item);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar itens por dono: " + e.getMessage());
        }

        return lista;
    }

    public List<Item> listarNaoEmprestadosDoUsuario(String userId) {
        List<Item> lista = new ArrayList<>();

        String sql = """
                    SELECT * FROM item
                    WHERE owner_id = ?
                    AND id NOT IN (SELECT item_id FROM emprestimo WHERE data_devolucao IS NULL)
                """;

        try (Connection conn = DBConnector.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Item item = new Item(
                        rs.getInt("id"),
                        String.valueOf(rs.getInt("owner_id")),
                        ItemType.valueOf(rs.getString("type")),
                        rs.getString("color"),
                        rs.getString("size"),
                        rs.getString("store_of_origin"),
                        rs.getString("image_path"),
                        ConservationLevel.valueOf(rs.getString("conservation"))) {
                };
                lista.add(item);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar itens não emprestados: " + e.getMessage());
        }

        return lista;
    }

    public List<Item> listarItensEmprestadosPara(String usuarioId) {
        List<Item> lista = new ArrayList<>();
        String sql = """
                    SELECT i.*
                    FROM item i
                    JOIN emprestimo e ON i.id = e.item_id
                    WHERE e.para_usuario_id = ? AND e.data_devolucao IS NULL
                """;

        try (Connection conn = DBConnector.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuarioId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Item item = new Item(
                        rs.getInt("id"),
                        rs.getString("owner_id"),
                        ItemType.valueOf(rs.getString("type")),
                        rs.getString("color"),
                        rs.getString("size"),
                        rs.getString("store_of_origin"),
                        rs.getString("image_path"),
                        ConservationLevel.valueOf(rs.getString("conservation"))) {
                };
                lista.add(item);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar itens emprestados para usuário: " + e.getMessage());
        }

        return lista;
    }

    public void insert(Item item) {
        String sql = "INSERT INTO item (owner_id, type, color, size, store_of_origin, image_path, conservation) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnector.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, item.getOwnerId());
            pstmt.setString(2, item.getType().name());
            pstmt.setString(3, item.getColor());
            pstmt.setString(4, item.getSize());
            pstmt.setString(5, item.getStoreOfOrigin());
            pstmt.setString(6, item.getImagePath());
            pstmt.setString(7, item.getConservation().name());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao inserir item: " + e.getMessage());
        }
    }

    public List<Item> getAll() {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM item";

        try (Connection conn = DBConnector.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Item item = new Item(
                        rs.getInt("id"),
                        rs.getString("owner_id"),
                        ItemType.valueOf(rs.getString("type")),
                        rs.getString("color"),
                        rs.getString("size"),
                        rs.getString("store_of_origin"),
                        rs.getString("image_path"),
                        ConservationLevel.valueOf(rs.getString("conservation"))) {
                };
                items.add(item);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar itens: " + e.getMessage());
        }

        return items;
    }

    public void delete(int id) {
        String sql = "DELETE FROM item WHERE id = ?";

        try (Connection conn = DBConnector.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao deletar item: " + e.getMessage());
        }
    }

    public void update(Item item) {
        String sql = """
                    UPDATE item SET
                        owner_id = ?, type = ?, color = ?, size = ?,
                        store_of_origin = ?, image_path = ?, conservation = ?
                    WHERE id = ?
                """;

        try (Connection conn = DBConnector.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, item.getOwnerId());
            pstmt.setString(2, item.getType().name());
            pstmt.setString(3, item.getColor());
            pstmt.setString(4, item.getSize());
            pstmt.setString(5, item.getStoreOfOrigin());
            pstmt.setString(6, item.getImagePath());
            pstmt.setString(7, item.getConservation().name());
            pstmt.setInt(8, item.getId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar item: " + e.getMessage());
        }
    }

    public List<Item> listarDisponiveisParaLook(String userId) {
        List<Item> lista = new ArrayList<>();

        String sql = """
                SELECT DISTINCT i.*
                FROM item i
                LEFT JOIN emprestimo e ON e.item_id = i.id AND e.data_devolucao IS NULL
                WHERE
                  (
                    -- Itens que são do usuário E que não estão emprestados
                    i.owner_id = ?
                    AND NOT EXISTS (
                        SELECT 1 FROM emprestimo ex
                        WHERE ex.item_id = i.id AND ex.data_devolucao IS NULL
                    )
                  )
                  OR
                  (
                    -- Itens emprestados para ele (ativos)
                    e.para_usuario_id = ? AND e.data_devolucao IS NULL
                  )

                                """;

        try (Connection conn = DBConnector.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            stmt.setString(2, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Item item = new Item(
                        rs.getInt("id"),
                        String.valueOf(rs.getInt("owner_id")),
                        ItemType.valueOf(rs.getString("type")),
                        rs.getString("color"),
                        rs.getString("size"),
                        rs.getString("store_of_origin"),
                        rs.getString("image_path"),
                        ConservationLevel.valueOf(rs.getString("conservation"))) {
                };
                lista.add(item);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar itens disponíveis para look: " + e.getMessage());
        }

        return lista;
    }

    private Item montarItem(ResultSet rs) throws SQLException {
        return new Item(
                rs.getInt("id"),
                rs.getString("owner_id"),
                ItemType.valueOf(rs.getString("type")),
                rs.getString("color"),
                rs.getString("size"),
                rs.getString("store_of_origin"),
                rs.getString("image_path"),
                ConservationLevel.valueOf(rs.getString("conservation"))) {
        };
    }

    public Item buscarPorId(int id) {
        String sql = "SELECT * FROM item WHERE id = ?";
        try (Connection conn = DBConnector.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return montarItem(rs); // método já criado
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar item por ID: " + e.getMessage());
        }
        return null;
    }

    public int contarPorUsuario(String userId) {
        String sql = "SELECT COUNT(*) FROM item WHERE owner_id = ?";
        try (Connection conn = DBConnector.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            System.out.println("Erro ao contar itens: " + e.getMessage());
            return 0;
        }
    }

    public Map<ItemType, Integer> contarPorTipo(String userId) {
        String sql = """
                    SELECT type, COUNT(*) as total
                    FROM item
                    WHERE owner_id = ?
                    GROUP BY type
                """;

        Map<ItemType, Integer> mapa = new HashMap<>();
        try (Connection conn = DBConnector.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ItemType tipo = ItemType.valueOf(rs.getString("type"));
                int total = rs.getInt("total");
                mapa.put(tipo, total);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao contar por tipo: " + e.getMessage());
        }
        return mapa;
    }

}
