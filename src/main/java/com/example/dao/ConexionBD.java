
package com.example.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConexionBD {

    private static final String URL =
            "jdbc:postgresql://localhost:5432/proyecto_integrador";

    private static final String USUARIO = "postgres";

    private ConexionBD() {
    }

    public static Connection obtenerConexion() throws SQLException {

        String password = System.getenv("DB_PASSWORD");

        if (password == null || password.isBlank()) {
            throw new IllegalStateException(
                    "No se encontró la variable DB_PASSWORD"
            );
        }

        return DriverManager.getConnection(
                URL,
                USUARIO,
                password
        );
    }
}
