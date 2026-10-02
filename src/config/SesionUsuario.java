package config;

import entidad.Usuario;

public class SesionUsuario {

    private static Usuario usuarioActual = null;

    private SesionUsuario() {
    }

    public static void setUsuario(Usuario u) {
        usuarioActual = u;
    }

    public static Usuario getUsuario() {
        return usuarioActual;
    }

    public static String getRol() {
        return (usuarioActual != null) ? usuarioActual.getRol() : null;
    }

    public static int getId() {
        return (usuarioActual != null) ? usuarioActual.getId() : 0;
    }

    public static String getNombre() {
        return (usuarioActual != null) ? usuarioActual.getNombreCompleto() : "";
    }

    public static boolean esAdmin() {
        return usuarioActual != null && "ADMIN".equals(usuarioActual.getRol());
    }

    public static boolean esEmpleado() {
        return usuarioActual != null && "EMPLEADO".equals(usuarioActual.getRol());
    }

    public static void cerrarSesion() {
        usuarioActual = null;
    }
}