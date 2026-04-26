package org.dbodor.sistemadeventas.DAO;

import org.dbodor.sistemadeventas.Model.Venta;
import org.dbodor.sistemadeventas.Model.DetalleVenta;
import org.dbodor.sistemadeventas.Util.DatabaseConnection;
import java.sql.*;
import java.util.List;

public class VentaDAO {

    public boolean procesarVentaCompleta(Venta venta, List<DetalleVenta> detalles) {
        String sqlVenta = "INSERT INTO ventas (turno_id, metodo_pago, total, fecha) VALUES (?, ?, ?, datetime('now', 'localtime'))";

        String sqlDetalle = "INSERT INTO detalle_ventas (venta_id, producto_id, cantidad, costo_unitario, precio_unitario) VALUES (?, ?, ?, ?, ?)";

        String sqlStock = "UPDATE productos SET stock = stock - ? WHERE id = ?";

        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            PreparedStatement pstmtVenta = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);
            pstmtVenta.setInt(1, venta.getTurnoId());
            pstmtVenta.setString(2, venta.getMetodoPago());
            pstmtVenta.setDouble(3, venta.getTotal());
            pstmtVenta.executeUpdate();

            ResultSet rsKeys = pstmtVenta.getGeneratedKeys();
            int idVentaGenerado = 0;
            if (rsKeys.next()) {
                idVentaGenerado = rsKeys.getInt(1);
            }

            PreparedStatement pstmtDetalle = conn.prepareStatement(sqlDetalle);
            PreparedStatement pstmtStock = conn.prepareStatement(sqlStock);

            for (DetalleVenta item : detalles) {
                pstmtDetalle.setInt(1, idVentaGenerado);
                pstmtDetalle.setInt(2, item.getProductoId());
                pstmtDetalle.setInt(3, item.getCantidad());
                pstmtDetalle.setDouble(4, item.getCostoUnitario());
                pstmtDetalle.setDouble(5, item.getPrecioUnitario());
                pstmtDetalle.executeUpdate();

                pstmtStock.setInt(1, item.getCantidad());
                pstmtStock.setInt(2, item.getProductoId());
                pstmtStock.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.out.println("Error grave en venta. Revirtiendo... " + e.getMessage());
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}