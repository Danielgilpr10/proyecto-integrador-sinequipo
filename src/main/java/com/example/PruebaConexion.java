package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PruebaConexion {

    public static void main(String[] args) {

        String url = "jdbc:postgresql://localhost:5432/proyecto_integrador";
        String usuario = "postgres";
        String password = System.getenv("DB_PASSWORD");

        if (password == null || password.isBlank()) {
            System.out.println("ERROR: No se encontro la variable DB_PASSWORD.");
            return;
        }

        try (Connection conexion = DriverManager.getConnection(
                url, usuario, password)) {

            System.out.println("CONEXION EXITOSA CON POSTGRESQL");
            System.out.println("Base de datos: proyecto_integrador");
            System.out.println("Conexion activa: " + !conexion.isClosed());

        } catch (SQLException e) {
            System.out.println("ERROR AL CONECTAR CON POSTGRESQL");
            System.out.println("Detalle: " + e.getMessage());
        }
    }
}