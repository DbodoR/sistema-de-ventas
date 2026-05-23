package org.dbodor.sistemadeventas.DAO;

import org.dbodor.sistemadeventas.Model.Venta;
import org.dbodor.sistemadeventas.Model.DetalleVenta;
import org.dbodor.sistemadeventas.Util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class VentaDAO {

    public boolean registrarVentaCompleta(Venta venta, List<DetalleVenta> detalles) {
        String sqlVenta = "INSERT INTO ventas (turno_id, metodo_pago, total, pago_efectivo, pago_transferencia, cambio) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlDetalle = "INSERT INTO detalle_ventas (venta_id, producto_id, cantidad, precio_unitario, costo_unitario) VALUES (?, ?, ?, ?, ?)";
        String sqlActualizarStock = "UPDATE productos SET stock = stock - ? WHERE id = ?";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            PreparedStatement psVenta = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);

            java.time.LocalDateTime ahoraEnBogota = java.time.LocalDateTime.now(java.time.ZoneId.of("America/Bogota"));
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String fechaFormateada = ahoraEnBogota.format(formatter);

            psVenta.setInt(1, venta.getTurnoId());
            psVenta.setString(2, venta.getMetodoPago());
            psVenta.setDouble(3, venta.getTotal());
            psVenta.setDouble(4, venta.getPagoEfectivo());
            psVenta.setDouble(5, venta.getPagoTransferencia());
            psVenta.setDouble(6, venta.getCambio());

            int filasAfectadas = psVenta.executeUpdate();
            if (filasAfectadas == 0) {
                throw new SQLException("Error: No se pudo registrar la cabecera de la venta.");
            }

            ResultSet generatedKeys = psVenta.getGeneratedKeys();
            int idVentaGenerado = 0;
            if (generatedKeys.next()) {
                idVentaGenerado = generatedKeys.getInt(1);
            }

            PreparedStatement psDetalle = conn.prepareStatement(sqlDetalle);
            PreparedStatement psStock = conn.prepareStatement(sqlActualizarStock);

            for (DetalleVenta detalle : detalles) {
                psDetalle.setInt(1, idVentaGenerado);
                psDetalle.setInt(2, detalle.getProductoId());
                psDetalle.setInt(3, detalle.getCantidad());
                psDetalle.setDouble(4, detalle.getPrecioUnitario());
                psDetalle.setDouble(5, detalle.getCostoUnitario());
                psDetalle.addBatch();

                psStock.setInt(1, detalle.getCantidad());
                psStock.setInt(2, detalle.getProductoId());
                psStock.addBatch();
            }

            psDetalle.executeBatch();
            psStock.executeBatch();

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        }
    }

    public double calcularTotalVentasPorTurno(int idTurno) {
        // Filtramos usando la cláusula WHERE metodo_pago = 'EFECTIVO'
        String sql = "SELECT SUM(total) FROM ventas WHERE turno_id = ? AND UPPER(metodo_pago) = 'EFECTIVO'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idTurno);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al calcular ventas en efectivo por turno: " + e.getMessage());
        }
        return 0.0;
    }

    public double calcularTotalTransferenciasPorTurno(int idTurno) {
        String sql = "SELECT SUM(total) FROM ventas WHERE turno_id = ? AND UPPER(metodo_pago) = 'TRANSFERENCIA'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idTurno);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al calcular transferencias por turno: " + e.getMessage());
        }
        return 0.0;
    }

    public List<Venta> obtenerVentasPorFiltros(String fechaFiltro, String ordenMetodoPago) {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT id, turno_id, fecha, metodo_pago, total, pago_efectivo, pago_transferencia, cambio " +
                "FROM ventas WHERE DATE(fecha) = ? ";

        if ("EFECTIVO".equals(ordenMetodoPago) || "TRANSFERENCIA".equals(ordenMetodoPago) || "MIXTO".equals(ordenMetodoPago)) {
            sql += "ORDER BY CASE WHEN metodo_pago = '" + ordenMetodoPago + "' THEN 0 ELSE 1 END, id DESC";
        } else {
            sql += "ORDER BY id DESC";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fechaFiltro);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Venta v = new Venta();
                    v.setId(rs.getInt("id"));
                    v.setTurnoId(rs.getInt("turno_id"));
                    v.setMetodoPago(rs.getString("metodo_pago"));
                    v.setTotal(rs.getDouble("total"));
                    v.setPagoEfectivo(rs.getDouble("pago_efectivo"));
                    v.setPagoTransferencia(rs.getDouble("pago_transferencia"));
                    v.setCambio(rs.getDouble("cambio"));

                    lista.add(v);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar historial de ventas: " + e.getMessage());
        }
        return lista;
    }

    public List<DetalleVenta> obtenerDetallesPorVentaId(int ventaId) {
        List<DetalleVenta> lista = new ArrayList<>();
        String sql = "SELECT dv.id, dv.producto_id, p.nombre AS nombre_producto, " +
                "dv.cantidad, dv.precio_unitario, (dv.cantidad * dv.precio_unitario) AS subtotal " +
                "FROM detalle_ventas dv " +
                "JOIN productos p ON dv.producto_id = p.id " +
                "WHERE dv.venta_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ventaId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DetalleVenta dv = new DetalleVenta();
                    dv.setId(rs.getInt("id"));
                    dv.setProductoId(rs.getInt("producto_id"));

                    dv.setNombreProducto(rs.getString("nombre_producto"));

                    dv.setCantidad(rs.getInt("cantidad"));
                    dv.setPrecioUnitario(rs.getDouble("precio_unitario"));
                    dv.setSubTotal(rs.getDouble("subtotal"));

                    lista.add(dv);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener los detalles de la venta: " + e.getMessage());
        }
        return lista;
    }
}