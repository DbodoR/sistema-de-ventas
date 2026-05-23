package org.dbodor.sistemadeventas.Util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:database/tienda.db";

    public static Connection getConnection() throws SQLException {
        File directory = new File("database");
        boolean esNueva = !new File("database/tienda.db").exists();

        if (!directory.exists()) {
            directory.mkdirs();
        }

        Connection conn = DriverManager.getConnection(URL);

        if (esNueva) {
            crearTablasIniciales(conn);
        }

        return conn;
    }

    private static void crearTablasIniciales(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE categorias (\n" +
                    "                            id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "                            nombre TEXT NOT NULL UNIQUE\n" +
                    ");");

            stmt.execute("CREATE TABLE productos (\n" +
                    "                           id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "                           codigo_barras TEXT UNIQUE,\n" +
                    "                           nombre TEXT NOT NULL,\n" +
                    "                           costo REAL NOT NULL DEFAULT 0.0,\n" +
                    "                           precio REAL NOT NULL DEFAULT 0.0,\n" +
                    "                           stock INTEGER NOT NULL DEFAULT 0,\n" +
                    "                           categoria_id INTEGER,\n" +
                    "                           precio_variable INTEGER DEFAULT 0 CHECK(precio_variable IN (0, 1)),\n" +
                    "                           FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE SET NULL\n" +
                    ");");

            stmt.execute("CREATE TABLE turnos (\n" +
                    "                        id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "                        fecha_apertura DATETIME DEFAULT CURRENT_TIMESTAMP,\n" +
                    "                        monto_inicial REAL NOT NULL,\n" +
                    "                        monto_final REAL,\n" +
                    "                        fecha_cierre DATETIME,\n" +
                    "                        estado TEXT DEFAULT 'ABIERTO' CHECK(estado IN ('ABIERTO', 'CERRADO'))\n" +
                    ");");

            stmt.execute("CREATE TABLE ventas (\n" +
                    "                        id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "                        turno_id INTEGER NOT NULL,\n" +
                    "                        fecha DATETIME DEFAULT CURRENT_TIMESTAMP,\n" +
                    "                        metodo_pago TEXT NOT NULL DEFAULT 'EFECTIVO' CHECK(metodo_pago IN ('EFECTIVO', 'TRANSFERENCIA')),\n" +
                    "                        total REAL NOT NULL DEFAULT 0.0,\n" +
                    "                        FOREIGN KEY (turno_id) REFERENCES turnos(id)\n" +
                    ");");

            stmt.execute("CREATE TABLE detalle_ventas (\n" +
                    "                                id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "                                venta_id INTEGER NOT NULL,\n" +
                    "                                producto_id INTEGER NOT NULL,\n" +
                    "                                cantidad INTEGER NOT NULL CHECK(cantidad > 0),\n" +
                    "                                costo_unitario REAL NOT NULL,\n" +
                    "                                precio_unitario REAL NOT NULL,\n" +
                    "                                FOREIGN KEY (venta_id) REFERENCES ventas(id) ON DELETE CASCADE,\n" +
                    "                                FOREIGN KEY (producto_id) REFERENCES productos(id)\n" +
                    ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS usuarios (id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "                          username TEXT NOT NULL UNIQUE,\n" +
                    "                          password TEXT NOT NULL);");

            stmt.execute("INSERT OR IGNORE INTO usuarios (username, password) VALUES ('admin', 'admin123');");

            stmt.execute("INSERT OR IGNORE INTO categorias (id, nombre) VALUES (1, 'Sin categoria');");

            System.out.println("Base de datos e infraestructura inicializadas con éxito.");
        } catch (SQLException e) {
            System.err.println("Error al inicializar la base de datos: " + e.getMessage());
        }
    }

    public static void testConnection() throws SQLException {
        try (Connection conn = getConnection()) {
            if (conn == null) throw new SQLException("No se pudo establecer la conexión.");
        }
    }
}
