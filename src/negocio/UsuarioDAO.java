package negocio;

import config.conexion;
import entidad.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    /**
     * Busca un usuario por su login y que esté activo.
     * Retorna null si no existe o está inactivo.
     */
    public Usuario buscarPorUsuario(String nombreUsuario) {

        String sql = "SELECT id, usuario, password, nombre_completo, rol, telefono, activo "
                   + "FROM usuarios WHERE usuario = ? AND activo = 1";

        try (Connection c = conexion.getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, nombreUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getInt("id"));
                    u.setUsuario(rs.getString("usuario"));
                    u.setPassword(rs.getString("password"));
                    u.setNombreCompleto(rs.getString("nombre_completo"));
                    u.setRol(rs.getString("rol"));
                    u.setTelefono(rs.getString("telefono"));
                    u.setActivo(rs.getInt("activo"));
                    return u;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error en UsuarioDAO.buscarPorUsuario: " + e.getMessage());
        }

        return null;
    }
        // ============================================================
    // BUSCAR MECÁNICOS (usuarios con rol EMPLEADO)
    // ============================================================
    public java.util.List<Usuario> buscarMecanicos() {
        java.util.List<Usuario> lista = new java.util.ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE rol = 'MECANICO' AND activo = 1 ORDER BY nombre_completo";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setUsuario(rs.getString("usuario"));
                u.setNombreCompleto(rs.getString("nombre_completo"));
                u.setRol(rs.getString("rol"));
                u.setActivo(rs.getInt("activo"));
                lista.add(u);
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar mecánicos: " + e.getMessage());
        }

        return lista;
    }
        // ============================================================
    // LISTAR TODOS (activos + inactivos)
    // ============================================================
    public java.util.List<Usuario> listarTodos() {
        java.util.List<Usuario> lista = new java.util.ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY activo DESC, nombre_completo";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(mapear(rs));

        } catch (SQLException e) {
            System.err.println("Error al listar usuarios: " + e.getMessage());
        }

        return lista;
    }

    // ============================================================
    // LISTAR SOLO ACTIVOS
    // ============================================================
    public java.util.List<Usuario> listar() {
        java.util.List<Usuario> lista = new java.util.ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE activo = 1 ORDER BY nombre_completo";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(mapear(rs));

        } catch (SQLException e) {
            System.err.println("Error al listar usuarios: " + e.getMessage());
        }

        return lista;
    }

    // ============================================================
    // BUSCAR POR TEXTO
    // ============================================================
    public java.util.List<Usuario> buscar(String texto, boolean incluirInactivos) {
        java.util.List<Usuario> lista = new java.util.ArrayList<>();
        String sql = "SELECT * FROM usuarios "
                   + "WHERE (usuario LIKE ? OR nombre_completo LIKE ?) "
                   + (incluirInactivos ? "" : "AND activo = 1 ")
                   + "ORDER BY activo DESC, nombre_completo";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            String filtro = "%" + texto + "%";
            ps.setString(1, filtro);
            ps.setString(2, filtro);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar usuarios: " + e.getMessage());
        }

        return lista;
    }

    // ============================================================
    // INSERTAR
    // ============================================================
    public boolean insertar(Usuario u) {
        String sql = "INSERT INTO usuarios "
                   + "(usuario, password, nombre_completo, rol, telefono, activo) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, u.getUsuario());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getNombreCompleto());
            ps.setString(4, u.getRol());
            ps.setString(5, u.getTelefono());
            ps.setInt(6, u.getActivo());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar usuario: " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    // ACTUALIZAR (sin cambiar contraseña)
    // ============================================================
    public boolean actualizar(Usuario u) {
        String sql = "UPDATE usuarios SET usuario=?, nombre_completo=?, rol=?, "
                   + "telefono=?, activo=? WHERE id=?";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, u.getUsuario());
            ps.setString(2, u.getNombreCompleto());
            ps.setString(3, u.getRol());
            ps.setString(4, u.getTelefono());
            ps.setInt(5, u.getActivo());
            ps.setInt(6, u.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    // ACTUALIZAR CON CONTRASEÑA
    // ============================================================
    public boolean actualizarConPassword(Usuario u) {
        String sql = "UPDATE usuarios SET usuario=?, password=?, nombre_completo=?, "
                   + "rol=?, telefono=?, activo=? WHERE id=?";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, u.getUsuario());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getNombreCompleto());
            ps.setString(4, u.getRol());
            ps.setString(5, u.getTelefono());
            ps.setInt(6, u.getActivo());
            ps.setInt(7, u.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario con password: " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    // DESACTIVAR
    // ============================================================
    public boolean desactivar(int id) {
        String sql = "UPDATE usuarios SET activo = 0 WHERE id = ?";
        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al desactivar usuario: " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    // REACTIVAR
    // ============================================================
    public boolean reactivar(int id) {
        String sql = "UPDATE usuarios SET activo = 1 WHERE id = ?";
        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al reactivar usuario: " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    // BUSCAR POR ID
    // ============================================================
    public Usuario buscarPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar usuario por ID: " + e.getMessage());
        }
        return null;
    }

    // ============================================================
    // MAPEAR
    // ============================================================
    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setUsuario(rs.getString("usuario"));
        u.setPassword(rs.getString("password"));
        u.setNombreCompleto(rs.getString("nombre_completo"));
        u.setRol(rs.getString("rol"));
        u.setTelefono(rs.getString("telefono"));
        u.setActivo(rs.getInt("activo"));
        return u;
    }
}