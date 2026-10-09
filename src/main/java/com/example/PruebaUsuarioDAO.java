
package com.example;

import com.example.dao.UsuarioDAO;
import com.example.model.Usuario;

import java.util.List;

public class PruebaUsuarioDAO {

    public static void main(String[] args) {

        UsuarioDAO dao = null;

        try {
            dao = new UsuarioDAO();

            // Buscar exclusivamente al usuario de prueba
            List<Usuario> usuarios = dao.listarTodos();

            List<Usuario> encontrados = usuarios.stream()
                    .filter(u -> "juan.prueba@example.com"
                            .equals(u.getEmail()))
                    .toList();

            if (encontrados.size() != 1) {
                System.out.println(
                        "PRUEBA CANCELADA: se esperaban exactamente "
                                + "1 usuario de prueba, encontrados: "
                                + encontrados.size());
                return;
            }

            Usuario usuario = encontrados.get(0);

            // Verificar la identidad antes de eliminar
            if (!"Juan Carlos".equals(usuario.getNombre())
                    || !"Perez".equals(usuario.getApellido())) {

                System.out.println(
                        "PRUEBA CANCELADA: los datos no coinciden.");
                return;
            }

            Long id = usuario.getId();

            System.out.println("Usuario de prueba encontrado. ID: " + id);

            // Eliminar el usuario de prueba
            dao.eliminar(id);

            // Comprobar que ya no existe
            boolean existe = dao.listarTodos().stream()
                    .anyMatch(u -> id.equals(u.getId()));

            if (!existe) {
                System.out.println("PRUEBA DELETE EXITOSA");
                System.out.println("Usuario eliminado. ID: " + id);
            } else {
                System.out.println(
                        "ERROR: el usuario sigue en la base de datos.");
            }

        } catch (Exception e) {
            System.out.println("ERROR EN LA PRUEBA DELETE");
            e.printStackTrace();

        } finally {
            if (dao != null) {
                dao.cerrar();
            }
        }
    }
}
