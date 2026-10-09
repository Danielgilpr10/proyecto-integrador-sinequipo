
package com.example.dao;

import com.example.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    // CREATE - CREAR USUARIO
    public void guardar(Usuario usuario) {

        validar(usuario);

        String sql = """
                INSERT INTO usuarios
                (id, nombre, apellido, email, telefono,
                 direccion, rol, estado, fecha_registro)
                VALUES (nextval('usuario_seq'), ?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING id
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            asignarCampos(ps, usuario);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    usuario.setId(rs.getLong("id"));
                } else {
                    throw new SQLException(
                            "No se pudo obtener el ID del usuario"
                    );
                }
            }

            System.out.println(
                    "Usuario guardado correctamente. ID: "
                            + usuario.getId()
            );

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al guardar usuario: " + e.getMessage(), e
            );
        }
    }

    // READ - CONSULTAR USUARIOS
    public List<Usuario> listarTodos() {

        List<Usuario> usuarios = new ArrayList<>();

        String sql = """
                SELECT id, nombre, apellido, email, telefono,
                       direccion, rol, estado, fecha_registro
                FROM usuarios
                ORDER BY id
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                usuarios.add(construirUsuario(rs));
            }

            return usuarios;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al consultar usuarios: " + e.getMessage(), e
            );
        }
    }

    // UPDATE - ACTUALIZAR USUARIO
    public void actualizar(Usuario usuario) {

        validar(usuario);

        if (usuario.getId() == null) {
            throw new IllegalArgumentException(
                    "El usuario no tiene un ID válido"
            );
        }

        String sql = """
                UPDATE usuarios
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

            asignarCampos(ps, usuario);
            ps.setLong(9, usuario.getId());

            int filas = ps.executeUpdate();

            if (filas == 0) {
                throw new IllegalArgumentException(
                        "No existe un usuario con ID: "
                                + usuario.getId()
                );
            }

            System.out.println(
                    "Usuario actualizado correctamente. ID: "
                            + usuario.getId()
            );

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al actualizar usuario: " + e.getMessage(), e
            );
        }
    }

    // DELETE - ELIMINAR USUARIO
    public void eliminar(Long id) {

        if (id == null) {
            throw new IllegalArgumentException(
                    "Debes indicar el ID del usuario"
            );
        }

        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setLong(1, id);

            int filas = ps.executeUpdate();

            if (filas == 0) {
                throw new IllegalArgumentException(
                        "No existe un usuario con ID: " + id
                );
            }

            System.out.println(
                    "Usuario eliminado correctamente. ID: " + id
            );

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al eliminar usuario: " + e.getMessage(), e
            );
        }
    }

    // ASIGNAR VALORES A LA CONSULTA SQL
    private void asignarCampos(
            PreparedStatement ps,
            Usuario usuario) throws SQLException {

        ps.setString(1, usuario.getNombre().trim());
        ps.setString(2, usuario.getApellido().trim());
        ps.setString(3, usuario.getEmail().trim());
        ps.setString(4, usuario.getTelefono());
        ps.setString(5, usuario.getDireccion());
        ps.setString(6, usuario.getRol());
        ps.setString(7, usuario.getEstado());

        LocalDateTime fecha = usuario.getFechaRegistro();

        if (fecha == null) {
            fecha = LocalDateTime.now();
            usuario.setFechaRegistro(fecha);
        }

        ps.setTimestamp(8, Timestamp.valueOf(fecha));
    }

    // CONVERTIR UNA FILA SQL EN OBJETO USUARIO
    private Usuario construirUsuario(
            ResultSet rs) throws SQLException {

        Timestamp timestamp = rs.getTimestamp("fecha_registro");

        LocalDateTime fecha = timestamp == null
                ? null
                : timestamp.toLocalDateTime();

        return new Usuario(
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
    private void validar(Usuario usuario) {

        if (usuario == null) {
            throw new IllegalArgumentException(
                    "El usuario no puede ser nulo"
            );
        }

        validarTexto(usuario.getNombre(), "Nombre", 100);
        validarTexto(usuario.getApellido(), "Apellido", 100);
        validarTexto(usuario.getEmail(), "Correo", 150);

        if (!usuario.getEmail().trim()
                .matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {

            throw new IllegalArgumentException(
                    "El correo electrónico no es válido"
            );
        }

        validarOpcional(usuario.getTelefono(), "Teléfono", 30);
        validarOpcional(usuario.getDireccion(), "Dirección", 200);
        validarOpcional(usuario.getRol(), "Rol", 50);
        validarOpcional(usuario.getEstado(), "Estado", 30);
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
