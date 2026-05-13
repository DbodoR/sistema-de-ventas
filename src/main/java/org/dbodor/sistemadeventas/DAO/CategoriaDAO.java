package org.dbodor.sistemadeventas.DAO;

import org.dbodor.sistemadeventas.Model.Categoria;
import org.dbodor.sistemadeventas.Util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    public boolean nuevaCategoria(String nombre){
        String sql = "INSERT INTO categorias (nombre) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            pstmt.executeUpdate();
            return true;
            } catch (SQLException e) {
            System.out.println("Error al agregar categoría: " + e.getMessage());
            return false;
        }
    }
    public List<Categoria> listarTodas() {
        List<Categoria> listaCategorias = new ArrayList<>();
        String sql = "SELECT id, nombre FROM categorias ORDER BY nombre ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Categoria categoria = new Categoria();
                categoria.setId(rs.getInt("id"));
                categoria.setNombre(rs.getString("nombre"));

                listaCategorias.add(categoria);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar categorías: " + e.getMessage());
        }

        return listaCategorias;
    }

    public boolean eliminarCategoria(int id){
        String sqlActualizarProductos = "UPDATE productos SET categoria_id = 6 WHERE categoria_id = ?";
        String sqlEliminarCategoria = "DELETE FROM categorias WHERE id = ?";

        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();

            conn.setAutoCommit(false);

            try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlActualizarProductos)) {
                pstmtUpdate.setInt(1, id);
                pstmtUpdate.executeUpdate();
            }

            try (PreparedStatement pstmtDelete = conn.prepareStatement(sqlEliminarCategoria)) {
                pstmtDelete.setInt(1, id);
                int filasEliminadas = pstmtDelete.executeUpdate();

                if (filasEliminadas == 0) {
                    throw new SQLException("La categoría que intentas eliminar no existe.");
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            System.err.println("Error al eliminar la categoría: " + e.getMessage());
            return false;

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
