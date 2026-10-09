
package com.example;

import com.example.dao.EmpleadoDAO;
import com.example.model.Empleado;

import java.util.List;

public class PruebaEmpleadoDAO {

    public static void main(String[] args) {

        System.out.println("=== PRUEBA DE EMPLEADOS ===");

        EmpleadoDAO dao = null;

        try {
            dao = new EmpleadoDAO();

            List<Empleado> empleados = dao.listarTodos();

            System.out.println("Conexion con PostgreSQL exitosa.");
            System.out.println("Total de empleados: " + empleados.size());

            for (Empleado empleado : empleados) {
                System.out.println(empleado);
            }

            System.out.println("=== FIN DE LA PRUEBA ===");

        } catch (Exception e) {

            System.err.println(
                    "Error al consultar empleados: " + e.getMessage());

            e.printStackTrace();

        } finally {

            if (dao != null) {
                dao.cerrar();
            }
        }
    }
}
