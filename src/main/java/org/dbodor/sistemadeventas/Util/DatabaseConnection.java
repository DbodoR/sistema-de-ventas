package org.dbodor.sistemadeventas.Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:src/main/resources/database/tienda.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void testConnection() throws SQLException {
        try (Connection conn = getConnection()) {
            if (conn == null) throw new SQLException("No se pudo establecer la conexión.");
        }
    }
}
