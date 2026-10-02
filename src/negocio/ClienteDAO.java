package negocio;

import config.conexion;
import entidad.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    // ============================================================
    // INSERTAR
    // ============================================================
    public boolean insertar(Cliente c) {
        String sql = "INSERT INTO clientes (nombre, dni, telefono, email, direccion, activo) "
                   + "VALUES (?, ?, ?, ?, ?, 1)";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDni());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getEmail());
            ps.setString(5, c.getDireccion());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar cliente: " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    // ACTUALIZAR
    // ============================================================
    public boolean actualizar(Cliente c) {
        String sql = "UPDATE clientes SET nombre=?, dni=?, telefono=?, "
                   + "email=?, direccion=?, activo=? WHERE id=?";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDni());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getEmail());
            ps.setString(5, c.getDireccion());
            ps.setInt(6, c.getActivo());
            ps.setInt(7, c.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar cliente: " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    // DESACTIVAR (borrado lógico)
    // ============================================================
    public boolean desactivar(int id) {
        String sql = "UPDATE clientes SET activo = 0 WHERE id = ?";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al desactivar cliente: " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    // REACTIVAR
    // ============================================================
    public boolean reactivar(int id) {
        String sql = "UPDATE clientes SET activo = 1 WHERE id = ?";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al reactivar cliente: " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    // BUSCAR POR ID
    // ============================================================
    public Cliente buscarPorId(int id) {
        String sql = "SELECT * FROM clientes WHERE id = ?";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar cliente por ID: " + e.getMessage());
        }

        return null;
    }

    // ============================================================
    // BUSCAR POR DNI (para verificar duplicados)
    // ============================================================
    public Cliente buscarPorDni(String dni) {
        String sql = "SELECT * FROM clientes WHERE dni = ?";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, dni);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar cliente por DNI: " + e.getMessage());
        }

        return null;
    }

    // ============================================================
    // LISTAR (solo activos)
    // ============================================================
    public List<Cliente> listar() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes WHERE activo = 1 ORDER BY nombre";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar clientes: " + e.getMessage());
        }

        return lista;
    }

    // ============================================================
    // LISTAR TODOS (activos + inactivos)
    // ============================================================
    public List<Cliente> listarTodos() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes ORDER BY activo DESC, nombre";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar todos los clientes: " + e.getMessage());
        }

        return lista;
    }

      // ============================================================
    // BUSCAR (por ID o Nombre, solo activos)
    // ============================================================
    public List<Cliente> buscar(String texto) {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes "
                   + "WHERE activo = 1 AND (id = ? OR nombre LIKE ?) "
                   + "ORDER BY nombre";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            int idBuscado = -1;
            try {
                idBuscado = Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                idBuscado = -1;   // no es número
            }

            ps.setInt(1, idBuscado);
            ps.setString(2, "%" + texto + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar clientes: " + e.getMessage());
        }

        return lista;
    }

    // ============================================================
    // BUSCAR TODOS (por ID o Nombre, activos + inactivos)
    // ============================================================
    public List<Cliente> buscarTodos(String texto) {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes "
                   + "WHERE id = ? OR nombre LIKE ? "
                   + "ORDER BY activo DESC, nombre";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            int idBuscado = -1;
            try {
                idBuscado = Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                idBuscado = -1;
            }

            ps.setInt(1, idBuscado);
            ps.setString(2, "%" + texto + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar todos los clientes: " + e.getMessage());
        }

        return lista;
    }

    // ============================================================
    // MAPEAR ResultSet → Cliente
    // ============================================================
    private Cliente mapear(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setId(rs.getInt("id"));
        c.setNombre(rs.getString("nombre"));
        c.setDni(rs.getString("dni"));
        c.setTelefono(rs.getString("telefono"));
        c.setEmail(rs.getString("email"));
        c.setDireccion(rs.getString("direccion"));
        c.setActivo(rs.getInt("activo"));
        return c;
    }
}