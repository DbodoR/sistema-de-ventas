package org.dbodor.sistemadeventas.DAO;

import org.dbodor.sistemadeventas.Util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReporteDAO {
    public Map<String, Double> obtenerDistribucionCajaHoy() {
        Map<String, Double> datos = new HashMap<>();
        datos.put("costo", 0.0);
        datos.put("beneficio", 0.0);

        String sql = "SELECT " +
                "    SUM(dv.cantidad * dv.costo_unitario) AS total_costo, " +
                "    SUM(v.total) - SUM(dv.cantidad * dv.costo_unitario) AS beneficio " +
                "FROM ventas v " +
                "JOIN detalle_ventas dv ON v.id = dv.venta_id " +
                "WHERE date(v.fecha) = date('now', 'localtime')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                datos.put("costo", rs.getDouble("total_costo"));
                datos.put("beneficio", rs.getDouble("beneficio"));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener distribución de hoy: " + e.getMessage());
        }
        return datos;
    }

    public List<Map<String, Object>> obtenerVentasUltimos7Dias() {
        List<Map<String, Object>> resultado = new ArrayList<>();

        String sql = "SELECT " +
                "    date(v.fecha) AS dia, " +
                "    SUM(dv.cantidad * dv.costo_unitario) AS costos_dia, " +
                "    SUM(v.total) - SUM(dv.cantidad * dv.costo_unitario) AS beneficio_dia " +
                "FROM ventas v " +
                "JOIN detalle_ventas dv ON v.id = dv.venta_id " +
                "WHERE date(v.fecha) >= date('now', '-6 days', 'localtime') " +
                "GROUP BY dia " +
                "ORDER BY dia ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("fecha", rs.getString("dia"));
                fila.put("costos", rs.getDouble("costos_dia"));
                fila.put("beneficio", rs.getDouble("beneficio_dia"));
                resultado.add(fila);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener desglose semanal: " + e.getMessage());
        }
        return resultado;
    }

    public List<Map<String, Object>> obtenerVentasMensualesPorSemana() {
        List<Map<String, Object>> resultado = new ArrayList<>();

        String sql = "SELECT " +
                "    CASE " +
                "        WHEN cast(strftime('%d', v.fecha) as integer) BETWEEN 1 AND 7 THEN 'Semana 1' " +
                "        WHEN cast(strftime('%d', v.fecha) as integer) BETWEEN 8 AND 14 THEN 'Semana 2' " +
                "        WHEN cast(strftime('%d', v.fecha) as integer) BETWEEN 15 AND 21 THEN 'Semana 3' " +
                "        ELSE 'Semana 4' " +
                "    END AS num_semana, " +
                "    SUM(dv.cantidad * dv.costo_unitario) AS costos_semana, " +
                "    SUM(v.total) - SUM(dv.cantidad * dv.costo_unitario) AS beneficio_semana " +
                "FROM ventas v " +
                "JOIN detalle_ventas dv ON v.id = dv.venta_id " +
                "WHERE strftime('%Y-%m', v.fecha) = strftime('%Y-%m', 'now', 'localtime') " +
                "GROUP BY num_semana " +
                "ORDER BY num_semana ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("semana", rs.getString("num_semana"));
                fila.put("costos", rs.getDouble("costos_semana"));
                fila.put("beneficio", rs.getDouble("beneficio_semana"));
                resultado.add(fila);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener desglose mensual: " + e.getMessage());
        }
        return resultado;
    }

    public List<Map<String, Object>> obtenerResumenAnualPorMeses(String anio) {
        List<Map<String, Object>> tablaAnual = new ArrayList<>();
        String sql = "SELECT " +
                "    CASE strftime('%m', v.fecha) " +
                "        WHEN '01' THEN 'Enero' WHEN '02' THEN 'Febrero' WHEN '03' THEN 'Marzo' " +
                "        WHEN '04' THEN 'Abril' WHEN '05' THEN 'Mayo' WHEN '06' THEN 'Junio' " +
                "        WHEN '07' THEN 'Julio' WHEN '08' THEN 'Agosto' WHEN '09' THEN 'Septiembre' " +
                "        WHEN '10' THEN 'Octubre' WHEN '11' THEN 'Noviembre' WHEN '12' THEN 'Diciembre' " +
                "    END AS mes_nombre, " +
                "    COUNT(v.id) AS transacciones, " +
                "    SUM(v.total) AS ingresos_totales, " +
                "    SUM(dv.cantidad * dv.costo_unitario) AS costos_totales, " +
                "    SUM(v.total) - SUM(dv.cantidad * dv.costo_unitario) AS beneficio_neto " +
                "FROM ventas v " +
                "JOIN detalle_ventas dv ON v.id = dv.venta_id " +
                "WHERE strftime('%Y', v.fecha) = ? " +
                "GROUP BY strftime('%m', v.fecha) " +
                "ORDER BY strftime('%m', v.fecha) ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, anio);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> renglon = new HashMap<>();
                    renglon.put("mes", rs.getString("mes_nombre"));
                    renglon.put("transacciones", rs.getInt("transacciones"));
                    renglon.put("ingresos", rs.getDouble("ingresos_totales"));
                    renglon.put("costos", rs.getDouble("costos_totales"));
                    renglon.put("beneficio", rs.getDouble("beneficio_neto"));
                    tablaAnual.add(renglon);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en resumen anual: " + e.getMessage());
        }
        return tablaAnual;
    }

    public List<String> obtenerAniosConVentas() {
        List<String> anios = new ArrayList<>();
        String sql = "SELECT DISTINCT strftime('%Y', fecha) AS anio FROM ventas ORDER BY anio DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) { anios.add(rs.getString("anio")); }
        } catch (SQLException e) { e.printStackTrace(); }
        if (anios.isEmpty()) anios.add(String.valueOf(java.time.LocalDate.now().getYear()));
        return  anios;
    }

    public String obtenerProductoMasVendido(String anio) {
        String sql = "SELECT p.nombre, SUM(dv.cantidad) AS total_vendido " +
                "FROM detalle_ventas dv " +
                "JOIN productos p ON dv.producto_id = p.id " +
                "JOIN ventas v ON dv.venta_id = v.id " +
                "WHERE strftime('%Y', v.fecha) = ? " +
                "GROUP BY p.id " +
                "ORDER BY total_vendido DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, anio);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getString("nombre") + " (" + rs.getInt("total_vendido") + " unds)";
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "Ninguno";
    }

    public String obtenerCategoriaMasVendida(String anio) {
        String sql = "SELECT c.nombre, SUM(dv.cantidad) AS total_vendido " +
                "FROM detalle_ventas dv " +
                "JOIN productos p ON dv.producto_id = p.id " +
                "JOIN categorias c ON p.categoria_id = c.id " +
                "JOIN ventas v ON dv.venta_id = v.id " +
                "WHERE strftime('%Y', v.fecha) = ? " +
                "GROUP BY c.id " +
                "ORDER BY total_vendido DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, anio);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getString("nombre") + " (" + rs.getInt("total_vendido") + " unds)";
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "Ninguno";
    }
}
