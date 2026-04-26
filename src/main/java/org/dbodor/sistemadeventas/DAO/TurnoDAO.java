package org.dbodor.sistemadeventas.DAO;

import org.dbodor.sistemadeventas.Util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TurnoDAO {
    public boolean abrirTurno(int usuarioId, double montoInicial) {
        String sql = "INSERT INTO turnos (usuario_id, fecha_inicio, monto_inicial, estado) VALUES (?, datetime('now', 'localtime'), ?, 'ABIERTO')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, usuarioId);
            pstmt.setDouble(2, montoInicial);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al abrir turno: " + e.getMessage());
            return false;
        }
    }

    public boolean hayTurnoAbierto() {
        String sql = "SELECT id FROM turnos WHERE estado = 'ABIERTO'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }
}