
package com.example.dao;

import com.example.model.Empleado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

public class EmpleadoDAO {

    // CREATE - CREAR EMPLEADO
    public void guardar(Empleado empleado) {

        validar(empleado);

        String sql = """
                INSERT INTO empleados
                (id, nombre, apellido, email, telefono,
                 direccion, rol, estado, fecha_registro)
                VALUES (nextval('empleado_seq'), ?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING id
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            asignarCampos(ps, empleado);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    empleado.setId(rs.getLong("id"));
                } else {
                    throw new SQLException(
                            "No se pudo obtener el ID del empleado"
                    );
                }
            }

            System.out.println(
                    "Empleado guardado correctamente. ID: "
                            + empleado.getId()
            );

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al guardar empleado: " + e.getMessage(), e
            );
        }
    }

    // READ - CONSULTAR EMPLEADOS
    public List<Empleado> listarTodos() {

        List<Empleado> empleados = new ArrayList<>();

        String sql = """
                SELECT id, nombre, apellido, email, telefono,
                       direccion, rol, estado, fecha_registro
                FROM empleados
                ORDER BY id
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                empleados.add(construirEmpleado(rs));
            }

            return empleados;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al consultar empleados: " + e.getMessage(), e
            );
        }
    }

    // UPDATE - ACTUALIZAR EMPLEADO
    public void actualizar(Empleado empleado) {

        validar(empleado);

        if (empleado.getId() == null) {
            throw new IllegalArgumentException(
                    "El empleado no tiene un ID válido"
            );
        }

        String sql = """
                UPDATE empleados
                SET nombre = ?,
                    apellido = ?,
                    email = ?,
                    telefono = ?,
                    direccion = ?,
                    rol = ?,
                    estado = ?,
                    fecha_registro = ?
                WHERE id = ?
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            asignarCampos(ps, empleado);
            ps.setLong(9, empleado.getId());

            int filas = ps.executeUpdate();

            if (filas == 0) {
                throw new IllegalArgumentException(
                        "No existe un empleado con ID: "
                                + empleado.getId()
                );
            }

            System.out.println(
                    "Empleado actualizado correctamente. ID: "
                            + empleado.getId()
            );

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al actualizar empleado: " + e.getMessage(), e
            );
        }
    }

    // DELETE - ELIMINAR EMPLEADO
    public void eliminar(Long id) {

        if (id == null) {
            throw new IllegalArgumentException(
                    "Debes indicar el ID del empleado"
            );
        }

        String sql = "DELETE FROM empleados WHERE id = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setLong(1, id);

            int filas = ps.executeUpdate();

            if (filas == 0) {
                throw new IllegalArgumentException(
                        "No existe un empleado con ID: " + id
                );
            }

            System.out.println(
                    "Empleado eliminado correctamente. ID: " + id
            );

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al eliminar empleado: " + e.getMessage(), e
            );
        }
    }

    // ASIGNAR VALORES A LA CONSULTA SQL
    private void asignarCampos(
            PreparedStatement ps,
            Empleado empleado) throws SQLException {

        ps.setString(1, empleado.getNombre().trim());
        ps.setString(2, empleado.getApellido().trim());
        ps.setString(3, empleado.getEmail().trim());
        ps.setString(4, empleado.getTelefono());
        ps.setString(5, empleado.getDireccion());
        ps.setString(6, empleado.getRol());
        ps.setString(7, empleado.getEstado());

        LocalDateTime fecha = empleado.getFechaRegistro();

        if (fecha == null) {
            fecha = LocalDateTime.now();
            empleado.setFechaRegistro(fecha);
        }

        ps.setTimestamp(8, Timestamp.valueOf(fecha));
    }

    // CONVERTIR UNA FILA SQL EN OBJETO EMPLEADO
    private Empleado construirEmpleado(
            ResultSet rs) throws SQLException {

        Timestamp timestamp = rs.getTimestamp("fecha_registro");

        LocalDateTime fecha = timestamp == null
                ? null
                : timestamp.toLocalDateTime();

        return new Empleado(
                rs.getLong("id"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("email"),
                rs.getString("telefono"),
                rs.getString("direccion"),
                rs.getString("rol"),
                rs.getString("estado"),
                fecha
        );
    }

    // VALIDACIONES
    private void validar(Empleado empleado) {

        if (empleado == null) {
            throw new IllegalArgumentException(
                    "El empleado no puede ser nulo"
            );
        }

        validarTexto(empleado.getNombre(), "Nombre", 100);
        validarTexto(empleado.getApellido(), "Apellido", 100);
        validarTexto(empleado.getEmail(), "Correo", 150);

        if (!empleado.getEmail().trim()
                .matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {

            throw new IllegalArgumentException(
                    "El correo electrónico no es válido"
            );
        }

        validarOpcional(empleado.getTelefono(), "Teléfono", 30);
        validarOpcional(empleado.getDireccion(), "Dirección", 200);
        validarOpcional(empleado.getRol(), "Rol", 50);
        validarOpcional(empleado.getEstado(), "Estado", 30);
    }

    private void validarTexto(
            String valor,
            String campo,
            int longitudMaxima) {

        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(
                    "El campo " + campo + " es obligatorio"
            );
        }

        if (valor.length() > longitudMaxima) {
            throw new IllegalArgumentException(
                    "El campo " + campo
                            + " supera " + longitudMaxima + " caracteres"
            );
        }
    }

    private void validarOpcional(
            String valor,
            String campo,
            int longitudMaxima) {

        if (valor != null && valor.length() > longitudMaxima) {
            throw new IllegalArgumentException(
                    "El campo " + campo
                            + " supera " + longitudMaxima + " caracteres"
            );
        }
    }

    // COMPATIBILIDAD CON MAINVIEW
    public void cerrar() {
        // Las conexiones se cierran automáticamente
        // mediante try-with-resources.
    }
}
