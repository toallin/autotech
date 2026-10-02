package negocio;

import entidad.Usuario;

public class LoginNegocio {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    /**
     * Valida las credenciales del usuario.
     *
     * @param nombreUsuario login ingresado
     * @param password      contraseña ingresada
     * @return Usuario si las credenciales son válidas, null en caso contrario
     */
    public Usuario validar(String nombreUsuario, String password) {

        // Validación básica
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) return null;
        if (password == null || password.isEmpty()) return null;

        // Busca en BD
        Usuario u = usuarioDAO.buscarPorUsuario(nombreUsuario.trim());

        if (u == null) return null;

        // Compara contraseña (texto plano por ahora)
        if (!password.equals(u.getPassword())) return null;

        return u;
    }
}