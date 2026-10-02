package negocio;

import config.conexion;
import entidad.Vehiculo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VehiculoDAO {

    // ============================================================
    // INSERTAR
    // ============================================================
    public boolean insertar(Vehiculo v) {
        String sql = "INSERT INTO vehiculos "
                   + "(cliente_id, placa, marca, modelo, anio, color, vin, activo) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, 1)";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, v.getClienteId());
            ps.setString(2, v.getPlaca());
            ps.setString(3, v.getMarca());
            ps.setString(4, v.getModelo());
            ps.setInt(5, v.getAnio());
            ps.setString(6, v.getColor());
            ps.setString(7, v.getVin());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar vehículo: " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    // ACTUALIZAR
    // ============================================================
    public boolean actualizar(Vehiculo v) {
        String sql = "UPDATE vehiculos SET cliente_id=?, placa=?, marca=?, "
                   + "modelo=?, anio=?, color=?, vin=?, activo=? WHERE id=?";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, v.getClienteId());
            ps.setString(2, v.getPlaca());
            ps.setString(3, v.getMarca());
            ps.setString(4, v.getModelo());
            ps.setInt(5, v.getAnio());
            ps.setString(6, v.getColor());
            ps.setString(7, v.getVin());
            ps.setInt(8, v.getActivo());
            ps.setInt(9, v.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar vehículo: " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    // DESACTIVAR
    // ============================================================
    public boolean desactivar(int id) {
        String sql = "UPDATE vehiculos SET activo = 0 WHERE id = ?";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al desactivar vehículo: " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    // REACTIVAR
    // ============================================================
    public boolean reactivar(int id) {
        String sql = "UPDATE vehiculos SET activo = 1 WHERE id = ?";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al reactivar vehículo: " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    // BUSCAR POR ID
    // ============================================================
    public Vehiculo buscarPorId(int id) {
        String sql = "SELECT v.*, c.nombre AS nombre_cliente "
                   + "FROM vehiculos v "
                   + "LEFT JOIN clientes c ON v.cliente_id = c.id "
                   + "WHERE v.id = ?";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar vehículo por ID: " + e.getMessage());
        }

        return null;
    }

    // ============================================================
    // BUSCAR POR PLACA (para verificar duplicados)
    // ============================================================
    public Vehiculo buscarPorPlaca(String placa) {
        String sql = "SELECT * FROM vehiculos WHERE placa = ?";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, placa);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar vehículo por placa: " + e.getMessage());
        }

        return null;
    }

    // ============================================================
    // LISTAR (solo activos)
    // ============================================================
    public List<Vehiculo> listar() {
        List<Vehiculo> lista = new ArrayList<>();
        String sql = "SELECT v.*, c.nombre AS nombre_cliente "
                   + "FROM vehiculos v "
                   + "LEFT JOIN clientes c ON v.cliente_id = c.id "
                   + "WHERE v.activo = 1 "
                   + "ORDER BY v.placa";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar vehículos: " + e.getMessage());
        }

        return lista;
    }

    // ============================================================
    // LISTAR TODOS (activos + inactivos)
    // ============================================================
    public List<Vehiculo> listarTodos() {
        List<Vehiculo> lista = new ArrayList<>();
        String sql = "SELECT v.*, c.nombre AS nombre_cliente "
                   + "FROM vehiculos v "
                   + "LEFT JOIN clientes c ON v.cliente_id = c.id "
                   + "ORDER BY v.activo DESC, v.placa";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar todos los vehículos: " + e.getMessage());
        }

        return lista;
    }

    // ============================================================
    // BUSCAR POR ID o PLACA (solo activos)
    // ============================================================
    public List<Vehiculo> buscar(String texto) {
        List<Vehiculo> lista = new ArrayList<>();
        String sql = "SELECT v.*, c.nombre AS nombre_cliente "
                   + "FROM vehiculos v "
                   + "LEFT JOIN clientes c ON v.cliente_id = c.id "
                   + "WHERE v.activo = 1 AND (v.id = ? OR v.placa LIKE ?) "
                   + "ORDER BY v.placa";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            int idBuscado = -1;
            try { idBuscado = Integer.parseInt(texto); } catch (Exception e) {}

            ps.setInt(1, idBuscado);
            ps.setString(2, "%" + texto + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar vehículos: " + e.getMessage());
        }

        return lista;
    }

    // ============================================================
    // BUSCAR TODOS (activos + inactivos)
    // ============================================================
    public List<Vehiculo> buscarTodos(String texto) {
        List<Vehiculo> lista = new ArrayList<>();
        String sql = "SELECT v.*, c.nombre AS nombre_cliente "
                   + "FROM vehiculos v "
                   + "LEFT JOIN clientes c ON v.cliente_id = c.id "
                   + "WHERE v.id = ? OR v.placa LIKE ? "
                   + "ORDER BY v.activo DESC, v.placa";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            int idBuscado = -1;
            try { idBuscado = Integer.parseInt(texto); } catch (Exception e) {}

            ps.setInt(1, idBuscado);
            ps.setString(2, "%" + texto + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar todos los vehículos: " + e.getMessage());
        }

        return lista;
    }

    // ============================================================
    // MAPEAR ResultSet → Vehiculo
    // ============================================================
    private Vehiculo mapear(ResultSet rs) throws SQLException {
        Vehiculo v = new Vehiculo();
        v.setId(rs.getInt("id"));
        v.setClienteId(rs.getInt("cliente_id"));
        v.setPlaca(rs.getString("placa"));
        v.setMarca(rs.getString("marca"));
        v.setModelo(rs.getString("modelo"));
        v.setAnio(rs.getInt("anio"));
        v.setColor(rs.getString("color"));
        v.setVin(rs.getString("vin"));
        v.setActivo(rs.getInt("activo"));
        try {
            v.setNombreCliente(rs.getString("nombre_cliente"));
        } catch (SQLException ignored) { }
        return v;
    }
}