package org.dbodor.sistemadeventas.DAO;

import org.dbodor.sistemadeventas.Model.Turno;
import org.dbodor.sistemadeventas.Util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TurnoDAO {

    public boolean abrirTurno(double montoInicial) {
        // 1. Obtenemos la fecha y hora exacta en Bogotá
        LocalDateTime ahoraEnBogota = LocalDateTime.now(ZoneId.of("America/Bogota"));
        String fechaFormateada = ahoraEnBogota.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // 2. Enviamos esa fecha explícitamente a la base de datos
        String sql = "INSERT INTO turnos (monto_inicial, estado, fecha_apertura) VALUES (?, 'ABIERTO', ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, montoInicial);
            pstmt.setString(2, fechaFormateada); // Inyectamos la hora de Colombia

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

    public boolean cerrarTurno(double montoFinal) {
        // 1. Hora de Bogotá para el cierre
        LocalDateTime ahoraEnBogota = LocalDateTime.now(ZoneId.of("America/Bogota"));
        String fechaCierre = ahoraEnBogota.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // 2. Actualizamos el turno que esté abierto
        String sql = "UPDATE turnos SET monto_final = ?, fecha_cierre = ?, estado = 'CERRADO' WHERE estado = 'ABIERTO'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, montoFinal);
            pstmt.setString(2, fechaCierre);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al cerrar turno: " + e.getMessage());
            return false;
        }
    }

    public Turno getTurnoAbierto() {
        String sql = "SELECT id, monto_inicial FROM turnos WHERE estado = 'ABIERTO'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                Turno turno = new Turno();
                turno.setId(rs.getInt("id"));
                turno.setMontoInicial(rs.getDouble("monto_inicial"));
                return turno;
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener el turno abierto: " + e.getMessage());
        }
        return null;
    }
}