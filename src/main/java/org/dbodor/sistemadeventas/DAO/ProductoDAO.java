package org.dbodor.sistemadeventas.DAO;

import org.dbodor.sistemadeventas.Model.Producto;
import org.dbodor.sistemadeventas.Util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductoDAO {


    public Producto buscarParaVenta(String criterio) {
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
}