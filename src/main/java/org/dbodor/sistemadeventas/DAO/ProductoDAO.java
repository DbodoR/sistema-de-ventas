package org.dbodor.sistemadeventas.DAO;

import org.dbodor.sistemadeventas.Model.Producto;
import org.dbodor.sistemadeventas.Util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductoDAO {

    public Producto buscar(String criterio) {
        String sql = "SELECT * FROM productos WHERE codigo_barras = ? OR nombre LIKE ?";
        Producto p = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, criterio);
            pstmt.setString(2, "%" + criterio + "%");

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                p = new Producto();
                p.setId(rs.getInt("id"));
                p.setCodigoBarras(rs.getString("codigo_barras"));
                p.setNombre(rs.getString("nombre"));
                p.setPrecio(rs.getDouble("precio"));
                p.setCosto(rs.getDouble("costo"));
                p.setStock(rs.getInt("stock"));
                p.setPrecioVariable(rs.getInt("precio_variable") == 1);
            }
        } catch (SQLException e) {
            System.out.println("Error buscando producto: " + e.getMessage());
        }
        return p;
    }

    public boolean agregarProducto(Producto producto) {
        String sql = "INSERT INTO productos (codigo_barras, nombre, costo, precio, stock, categoria_id, precio_variable) VALUES (?, ?, ?, ?, ? , ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, producto.getCodigoBarras());
            pstmt.setString(2, producto.getNombre());
            pstmt.setDouble(3, producto.getCosto());
            pstmt.setDouble(4, producto.getPrecio());
            pstmt.setInt(5, producto.getStock());

            if (producto.getCategoria_id() > 0) {
                pstmt.setInt(6, producto.getCategoria_id());
            } else {
                pstmt.setNull(6, java.sql.Types.INTEGER);
            }

            pstmt.setInt(7, producto.isPrecioVariable() ? 1 : 0);

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}