
package com.example;

import com.example.dao.UsuarioDAO;
import com.example.dao.EmpleadoDAO;
import com.example.model.Usuario;
import com.example.model.Empleado;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDateTime;

@PageTitle("Entidad financiera")
@Route("")
public class MainView extends VerticalLayout {

        public MainView() {
                setSizeFull();
                setPadding(true);
                setSpacing(true);

                H2 titulo = new H2("Gestión de Entidad Financiera");

                TabSheet tabSheet = new TabSheet();
                tabSheet.setWidthFull();

                tabSheet.add("Usuarios", crearSeccionUsuarios());
                tabSheet.add("Empleados", crearSeccionEmpleados());

                add(titulo, tabSheet);
        }

        // ==========================================
        // PRIMERA PESTAÑA: USUARIOS
        // ==========================================

        private Component crearSeccionUsuarios() {

                VerticalLayout layout = new VerticalLayout();
                layout.setPadding(false);
                layout.setWidthFull();

                TextField idField = new TextField("ID");
                idField.setReadOnly(true);

                TextField nombreField = new TextField("Nombre");
                TextField apellidoField = new TextField("Apellido");
                TextField emailField = new TextField("Correo electrónico");
                TextField telefonoField = new TextField("Teléfono");
                TextField direccionField = new TextField("Dirección");
                TextField rolField = new TextField("Rol");
                TextField estadoField = new TextField("Estado");

                FormLayout form = new FormLayout(
                                idField,
                                nombreField,
                                apellidoField,
                                emailField,
                                telefonoField,
                                direccionField,
                                rolField,
                                estadoField);

                form.setWidthFull();

                Grid<Usuario> grid = new Grid<>(Usuario.class, false);

                grid.addColumn(Usuario::getId)
                                .setHeader("ID").setAutoWidth(true);
                grid.addColumn(Usuario::getNombre)
                                .setHeader("Nombre").setAutoWidth(true);
                grid.addColumn(Usuario::getApellido)
                                .setHeader("Apellido").setAutoWidth(true);
                grid.addColumn(Usuario::getEmail)
                                .setHeader("Correo electrónico").setAutoWidth(true);
                grid.addColumn(Usuario::getTelefono)
                                .setHeader("Teléfono").setAutoWidth(true);
                grid.addColumn(Usuario::getDireccion)
                                .setHeader("Dirección").setAutoWidth(true);
                grid.addColumn(Usuario::getRol)
                                .setHeader("Rol").setAutoWidth(true);
                grid.addColumn(Usuario::getEstado)
                                .setHeader("Estado").setAutoWidth(true);

                grid.setWidthFull();

                final Usuario[] usuarioSeleccionado = { null };

                grid.addSelectionListener(event -> {
                        event.getFirstSelectedItem().ifPresentOrElse(
                                        usuario -> {
                                                usuarioSeleccionado[0] = usuario;

                                                idField.setValue(
                                                                String.valueOf(usuario.getId()));
                                                nombreField.setValue(
                                                                texto(usuario.getNombre()));
                                                apellidoField.setValue(
                                                                texto(usuario.getApellido()));
                                                emailField.setValue(
                                                                texto(usuario.getEmail()));
                                                telefonoField.setValue(
                                                                texto(usuario.getTelefono()));
                                                direccionField.setValue(
                                                                texto(usuario.getDireccion()));
                                                rolField.setValue(
                                                                texto(usuario.getRol()));
                                                estadoField.setValue(
                                                                texto(usuario.getEstado()));
                                        },
                                        () -> usuarioSeleccionado[0] = null);
                });

                Button btnCrear = new Button("Crear", e -> {

                        if (!camposValidos(
                                        nombreField, apellidoField, emailField)) {
                                return;
                        }

                        UsuarioDAO dao = null;

                        try {
                                dao = new UsuarioDAO();

                                Usuario nuevoUsuario = new Usuario(
                                                nombreField.getValue().trim(),
                                                apellidoField.getValue().trim(),
                                                emailField.getValue().trim(),
                                                telefonoField.getValue().trim(),
                                                direccionField.getValue().trim(),
                                                rolField.getValue().trim(),
                                                estadoField.getValue().trim(),
                                                LocalDateTime.now());

                                dao.guardar(nuevoUsuario);

                                grid.deselectAll();
                                usuarioSeleccionado[0] = null;
                                grid.setItems(dao.listarTodos());

                                idField.setValue(
                                                String.valueOf(nuevoUsuario.getId()));

                                Notification.show(
                                                "Usuario creado correctamente. ID: "
                                                                + nuevoUsuario.getId());

                        } catch (Exception ex) {
                                Notification.show(
                                                "Error al crear usuario: "
                                                                + ex.getMessage());
                        } finally {
                                if (dao != null)
                                        dao.cerrar();
                        }
                });

                btnCrear.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

                Button btnConsultar = new Button("Consultar", e -> {

                        UsuarioDAO dao = null;

                        try {
                                dao = new UsuarioDAO();

                                grid.deselectAll();
                                usuarioSeleccionado[0] = null;
                                grid.setItems(dao.listarTodos());

                                Notification.show(
                                                "Usuarios consultados correctamente");

                        } catch (Exception ex) {
                                Notification.show(
                                                "Error al consultar usuarios: "
                                                                + ex.getMessage());
                        } finally {
                                if (dao != null)
                                        dao.cerrar();
                        }
                });

                Button btnActualizar = new Button("Actualizar", e -> {

                        Usuario usuario = usuarioSeleccionado[0];

                        if (usuario == null) {
                                Notification.show(
                                                "Selecciona un usuario en la tabla");
                                return;
                        }

                        if (!camposValidos(
                                        nombreField, apellidoField, emailField)) {
                                return;
                        }

                        UsuarioDAO dao = null;

                        try {
                                dao = new UsuarioDAO();

                                usuario.setNombre(nombreField.getValue().trim());
                                usuario.setApellido(apellidoField.getValue().trim());
                                usuario.setEmail(emailField.getValue().trim());
                                usuario.setTelefono(telefonoField.getValue().trim());
                                usuario.setDireccion(direccionField.getValue().trim());
                                usuario.setRol(rolField.getValue().trim());
                                usuario.setEstado(estadoField.getValue().trim());

                                dao.actualizar(usuario);

                                grid.deselectAll();
                                usuarioSeleccionado[0] = null;
                                grid.setItems(dao.listarTodos());

                                Notification.show(
                                                "Usuario actualizado correctamente. ID: "
                                                                + usuario.getId());

                        } catch (Exception ex) {
                                Notification.show(
                                                "Error al actualizar usuario: "
                                                                + ex.getMessage());
                        } finally {
                                if (dao != null)
                                        dao.cerrar();
                        }
                });

                Button btnEliminar = new Button("Eliminar", e -> {

                        Usuario usuario = usuarioSeleccionado[0];

                        if (usuario == null) {
                                Notification.show(
                                                "Selecciona un usuario en la tabla");
                                return;
                        }

                        Long idEliminar = usuario.getId();

                        ConfirmDialog dialogo = new ConfirmDialog();
                        dialogo.setHeader("Confirmar eliminación");
                        dialogo.setText(
                                        "¿Eliminar al usuario "
                                                        + usuario.getNombre() + " "
                                                        + usuario.getApellido()
                                                        + " (ID " + idEliminar + ")?");

                        dialogo.setCancelable(true);
                        dialogo.setCancelText("Cancelar");
                        dialogo.setConfirmText("Eliminar");
                        dialogo.setConfirmButtonTheme("error primary");

                        dialogo.addConfirmListener(evento -> {

                                UsuarioDAO dao = null;

                                try {
                                        dao = new UsuarioDAO();
                                        dao.eliminar(idEliminar);

                                        grid.deselectAll();
                                        usuarioSeleccionado[0] = null;
                                        grid.setItems(dao.listarTodos());

                                        limpiarCampos(
                                                        idField, nombreField, apellidoField,
                                                        emailField, telefonoField, direccionField,
                                                        rolField, estadoField);

                                        Notification.show(
                                                        "Usuario eliminado correctamente. ID: "
                                                                        + idEliminar);

                                } catch (Exception ex) {
                                        Notification.show(
                                                        "Error al eliminar usuario: "
                                                                        + ex.getMessage());
                                } finally {
                                        if (dao != null)
                                                dao.cerrar();
                                }
                        });

                        dialogo.open();
                });

                btnEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

                Button btnLimpiar = new Button("Limpiar", e -> {

                        grid.deselectAll();
                        usuarioSeleccionado[0] = null;

                        limpiarCampos(
                                        idField, nombreField, apellidoField,
                                        emailField, telefonoField, direccionField,
                                        rolField, estadoField);
                });

                HorizontalLayout acciones = new HorizontalLayout(
                                btnCrear, btnConsultar, btnActualizar,
                                btnEliminar, btnLimpiar);

                acciones.getStyle().set("flex-wrap", "wrap");

                layout.add(form, acciones, grid);

                return layout;
        }

        // ==========================================
        // SEGUNDA PESTAÑA: EMPLEADOS
        // ==========================================

        private Component crearSeccionEmpleados() {

                VerticalLayout layout = new VerticalLayout();
                layout.setPadding(false);
                layout.setWidthFull();

                TextField idField = new TextField("ID");
                idField.setReadOnly(true);

                TextField nombreField = new TextField("Nombre");
                TextField apellidoField = new TextField("Apellido");
                TextField emailField = new TextField("Correo electrónico");
                TextField telefonoField = new TextField("Teléfono");
                TextField direccionField = new TextField("Dirección");
                TextField rolField = new TextField("Rol");
                TextField estadoField = new TextField("Estado");

                FormLayout form = new FormLayout(
                                idField,
                                nombreField,
                                apellidoField,
                                emailField,
                                telefonoField,
                                direccionField,
                                rolField,
                                estadoField);

                form.setWidthFull();

                Grid<Empleado> grid = new Grid<>(Empleado.class, false);

                grid.addColumn(Empleado::getId)
                                .setHeader("ID").setAutoWidth(true);
                grid.addColumn(Empleado::getNombre)
                                .setHeader("Nombre").setAutoWidth(true);
                grid.addColumn(Empleado::getApellido)
                                .setHeader("Apellido").setAutoWidth(true);
                grid.addColumn(Empleado::getEmail)
                                .setHeader("Correo electrónico").setAutoWidth(true);
                grid.addColumn(Empleado::getTelefono)
                                .setHeader("Teléfono").setAutoWidth(true);
                grid.addColumn(Empleado::getDireccion)
                                .setHeader("Dirección").setAutoWidth(true);
                grid.addColumn(Empleado::getRol)
                                .setHeader("Rol").setAutoWidth(true);
                grid.addColumn(Empleado::getEstado)
                                .setHeader("Estado").setAutoWidth(true);

                grid.setWidthFull();

                final Empleado[] empleadoSeleccionado = { null };

                // Seleccionar empleado en la tabla
                grid.addSelectionListener(event -> {
                        event.getFirstSelectedItem().ifPresentOrElse(
                                        empleado -> {
                                                empleadoSeleccionado[0] = empleado;

                                                idField.setValue(
                                                                String.valueOf(empleado.getId()));
                                                nombreField.setValue(
                                                                texto(empleado.getNombre()));
                                                apellidoField.setValue(
                                                                texto(empleado.getApellido()));
                                                emailField.setValue(
                                                                texto(empleado.getEmail()));
                                                telefonoField.setValue(
                                                                texto(empleado.getTelefono()));
                                                direccionField.setValue(
                                                                texto(empleado.getDireccion()));
                                                rolField.setValue(
                                                                texto(empleado.getRol()));
                                                estadoField.setValue(
                                                                texto(empleado.getEstado()));
                                        },
                                        () -> empleadoSeleccionado[0] = null);
                });

                // CREAR EMPLEADO
                Button btnCrear = new Button("Crear", e -> {

                        if (!camposValidos(
                                        nombreField, apellidoField, emailField)) {
                                return;
                        }

                        EmpleadoDAO dao = null;

                        try {
                                dao = new EmpleadoDAO();

                                Empleado nuevoEmpleado = new Empleado(
                                                nombreField.getValue().trim(),
                                                apellidoField.getValue().trim(),
                                                emailField.getValue().trim(),
                                                telefonoField.getValue().trim(),
                                                direccionField.getValue().trim(),
                                                rolField.getValue().trim(),
                                                estadoField.getValue().trim(),
                                                LocalDateTime.now());

                                dao.guardar(nuevoEmpleado);

                                grid.deselectAll();
                                empleadoSeleccionado[0] = null;
                                grid.setItems(dao.listarTodos());

                                idField.setValue(
                                                String.valueOf(nuevoEmpleado.getId()));

                                Notification.show(
                                                "Empleado creado correctamente. ID: "
                                                                + nuevoEmpleado.getId());

                        } catch (Exception ex) {
                                Notification.show(
                                                "Error al crear empleado: "
                                                                + ex.getMessage());
                        } finally {
                                if (dao != null)
                                        dao.cerrar();
                        }
                });

                btnCrear.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

                // CONSULTAR EMPLEADOS
                Button btnConsultar = new Button("Consultar", e -> {

                        EmpleadoDAO dao = null;

                        try {
                                dao = new EmpleadoDAO();

                                grid.deselectAll();
                                empleadoSeleccionado[0] = null;
                                grid.setItems(dao.listarTodos());

                                Notification.show(
                                                "Empleados consultados correctamente");

                        } catch (Exception ex) {
                                Notification.show(
                                                "Error al consultar empleados: "
                                                                + ex.getMessage());
                        } finally {
                                if (dao != null)
                                        dao.cerrar();
                        }
                });

                // ACTUALIZAR EMPLEADO
                Button btnActualizar = new Button("Actualizar", e -> {

                        Empleado empleado = empleadoSeleccionado[0];

                        if (empleado == null) {
                                Notification.show(
                                                "Selecciona un empleado en la tabla");
                                return;
                        }

                        if (!camposValidos(
                                        nombreField, apellidoField, emailField)) {
                                return;
                        }

                        EmpleadoDAO dao = null;

                        try {
                                dao = new EmpleadoDAO();

                                empleado.setNombre(nombreField.getValue().trim());
                                empleado.setApellido(apellidoField.getValue().trim());
                                empleado.setEmail(emailField.getValue().trim());
                                empleado.setTelefono(telefonoField.getValue().trim());
                                empleado.setDireccion(direccionField.getValue().trim());
                                empleado.setRol(rolField.getValue().trim());
                                empleado.setEstado(estadoField.getValue().trim());

                                dao.actualizar(empleado);

                                grid.deselectAll();
                                empleadoSeleccionado[0] = null;
                                grid.setItems(dao.listarTodos());

                                Notification.show(
                                                "Empleado actualizado correctamente. ID: "
                                                                + empleado.getId());

                        } catch (Exception ex) {
                                Notification.show(
                                                "Error al actualizar empleado: "
                                                                + ex.getMessage());
                        } finally {
                                if (dao != null)
                                        dao.cerrar();
                        }
                });

                // ELIMINAR EMPLEADO CON CONFIRMACIÓN
                Button btnEliminar = new Button("Eliminar", e -> {

                        Empleado empleado = empleadoSeleccionado[0];

                        if (empleado == null) {
                                Notification.show(
                                                "Selecciona un empleado en la tabla");
                                return;
                        }

                        Long idEliminar = empleado.getId();

                        ConfirmDialog dialogo = new ConfirmDialog();
                        dialogo.setHeader("Confirmar eliminación");
                        dialogo.setText(
                                        "¿Eliminar al empleado "
                                                        + empleado.getNombre() + " "
                                                        + empleado.getApellido()
                                                        + " (ID " + idEliminar + ")?");

                        dialogo.setCancelable(true);
                        dialogo.setCancelText("Cancelar");
                        dialogo.setConfirmText("Eliminar");
                        dialogo.setConfirmButtonTheme("error primary");

                        dialogo.addConfirmListener(evento -> {

                                EmpleadoDAO dao = null;

                                try {
                                        dao = new EmpleadoDAO();
                                        dao.eliminar(idEliminar);

                                        grid.deselectAll();
                                        empleadoSeleccionado[0] = null;
                                        grid.setItems(dao.listarTodos());

                                        limpiarCampos(
                                                        idField, nombreField, apellidoField,
                                                        emailField, telefonoField, direccionField,
                                                        rolField, estadoField);

                                        Notification.show(
                                                        "Empleado eliminado correctamente. ID: "
                                                                        + idEliminar);

                                } catch (Exception ex) {
                                        Notification.show(
                                                        "Error al eliminar empleado: "
                                                                        + ex.getMessage());
                                } finally {
                                        if (dao != null)
                                                dao.cerrar();
                                }
                        });

                        dialogo.open();
                });

                btnEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

                // LIMPIAR FORMULARIO
                Button btnLimpiar = new Button("Limpiar", e -> {

                        grid.deselectAll();
                        empleadoSeleccionado[0] = null;

                        limpiarCampos(
                                        idField, nombreField, apellidoField,
                                        emailField, telefonoField, direccionField,
                                        rolField, estadoField);
                });

                HorizontalLayout acciones = new HorizontalLayout(
                                btnCrear, btnConsultar, btnActualizar,
                                btnEliminar, btnLimpiar);

                acciones.getStyle().set("flex-wrap", "wrap");

                layout.add(form, acciones, grid);

                return layout;
        }

        // ==========================================
        // MÉTODOS AUXILIARES
        // ==========================================

        private String texto(String valor) {
                return valor == null ? "" : valor;
        }

        private boolean camposValidos(
                        TextField nombre,
                        TextField apellido,
                        TextField email) {

                if (nombre.getValue().isBlank()
                                || apellido.getValue().isBlank()
                                || email.getValue().isBlank()) {

                        Notification.show(
                                        "Completa Nombre, Apellido y Correo electrónico");
                        return false;
                }

                return true;
        }

        private void limpiarCampos(TextField... campos) {
                for (TextField campo : campos) {
                        campo.clear();
                }
        }
}
