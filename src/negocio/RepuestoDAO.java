package negocio;

import config.conexion;
import entidad.Repuesto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RepuestoDAO {

    // ============================================================
    // LISTAR (solo activos)
    // ============================================================
    public List<Repuesto> listar() {
        List<Repuesto> lista = new ArrayList<>();
        String sql = "SELECT * FROM repuestos WHERE activo = 1 ORDER BY nombre";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(mapear(rs));

        } catch (SQLException e) {
            System.err.println("Error al listar repuestos: " + e.getMessage());
        }

        return lista;
    }

    // ============================================================
    // LISTAR TODOS (activos + inactivos)
    // ============================================================
    public List<Repuesto> listarTodos() {
        List<Repuesto> lista = new ArrayList<>();
        String sql = "SELECT * FROM repuestos ORDER BY activo DESC, nombre";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(mapear(rs));

        } catch (SQLException e) {
            System.err.println("Error al listar todos los repuestos: " + e.getMessage());
        }

        return lista;
    }

    // ============================================================
    // BUSCAR (por ID, código o nombre; solo activos)
    // ============================================================
    public List<Repuesto> buscar(String texto) {
        List<Repuesto> lista = new ArrayList<>();
        String sql = "SELECT * FROM repuestos "
                   + "WHERE activo = 1 AND (id = ? OR codigo LIKE ? OR nombre LIKE ?) "
                   + "ORDER BY nombre";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            int idBuscado = -1;
            try { idBuscado = Integer.parseInt(texto); } catch (Exception e) {}

            ps.setInt(1, idBuscado);
            ps.setString(2, "%" + texto + "%");
            ps.setString(3, "%" + texto + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar repuestos: " + e.getMessage());
        }

        return lista;
    }

    // ============================================================
    // BUSCAR TODOS (activos + inactivos)
    // ============================================================
    public List<Repuesto> buscarTodos(String texto) {
        List<Repuesto> lista = new ArrayList<>();
        String sql = "SELECT * FROM repuestos "
                   + "WHERE id = ? OR codigo LIKE ? OR nombre LIKE ? "
                   + "ORDER BY activo DESC, nombre";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            int idBuscado = -1;
            try { idBuscado = Integer.parseInt(texto); } catch (Exception e) {}

            ps.setInt(1, idBuscado);
            ps.setString(2, "%" + texto + "%");
            ps.setString(3, "%" + texto + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar todos los repuestos: " + e.getMessage());
        }

        return lista;
    }

    // ============================================================
    // INSERTAR
    // ============================================================
    public boolean insertar(Repuesto r) {
        String sql = "INSERT INTO repuestos "
                   + "(codigo, nombre, descripcion, stock, stock_minimo, "
                   + "precio_compra, precio_venta, activo) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, r.getCodigo());
            ps.setString(2, r.getNombre());
            ps.setString(3, r.getDescripcion());
            ps.setInt(4, r.getStock());
            ps.setInt(5, r.getStockMinimo());
            ps.setDouble(6, r.getPrecioCompra());
            ps.setDouble(7, r.getPrecioVenta());
            ps.setInt(8, r.getActivo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar repuesto: " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    // ACTUALIZAR
    // ============================================================
    public boolean actualizar(Repuesto r) {
        String sql = "UPDATE repuestos SET codigo=?, nombre=?, descripcion=?, "
                   + "stock=?, stock_minimo=?, precio_compra=?, precio_venta=?, activo=? "
                   + "WHERE id=?";
        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, r.getCodigo());
            ps.setString(2, r.getNombre());
            ps.setString(3, r.getDescripcion());
            ps.setInt(4, r.getStock());
            ps.setInt(5, r.getStockMinimo());
            ps.setDouble(6, r.getPrecioCompra());
            ps.setDouble(7, r.getPrecioVenta());
            ps.setInt(8, r.getActivo());
            ps.setInt(9, r.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar repuesto: " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    // DESACTIVAR
    // ============================================================
    public boolean desactivar(int id) {
        String sql = "UPDATE repuestos SET activo = 0 WHERE id = ?";
        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al desactivar repuesto: " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    // REACTIVAR
    // ============================================================
    public boolean reactivar(int id) {
        String sql = "UPDATE repuestos SET activo = 1 WHERE id = ?";
        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al reactivar repuesto: " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    // BUSCAR POR ID
    // ============================================================
    public Repuesto buscarPorId(int id) {
        String sql = "SELECT * FROM repuestos WHERE id = ?";
        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar repuesto por ID: " + e.getMessage());
        }
        return null;
    }

    // ============================================================
    // BUSCAR POR CÓDIGO
    // ============================================================
    public Repuesto buscarPorCodigo(String codigo) {
        String sql = "SELECT * FROM repuestos WHERE codigo = ?";
        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar repuesto por código: " + e.getMessage());
        }
        return null;
    }

    // ============================================================
    // DESCONTAR STOCK
    // ============================================================
    public boolean descontarStock(int repuestoId, int cantidad) {
        String sql = "UPDATE repuestos SET stock = stock - ? WHERE id = ? AND stock >= ?";
        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setInt(2, repuestoId);
            ps.setInt(3, cantidad);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al descontar stock: " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    // MAPEAR
    // ============================================================
    private Repuesto mapear(ResultSet rs) throws SQLException {
        Repuesto r = new Repuesto();
        r.setId(rs.getInt("id"));
        r.setCodigo(rs.getString("codigo"));
        r.setNombre(rs.getString("nombre"));
        r.setDescripcion(rs.getString("descripcion"));
        r.setStock(rs.getInt("stock"));
        r.setStockMinimo(rs.getInt("stock_minimo"));
        r.setPrecioCompra(rs.getDouble("precio_compra"));
        r.setPrecioVenta(rs.getDouble("precio_venta"));
        r.setActivo(rs.getInt("activo"));
        return r;
    }
        // ============================================================
    // DESCONTAR STOCK CON TRANSACCIÓN (recibe Connection)
    // ============================================================
    public boolean descontarStock(Connection cn, int repuestoId, int cantidad) throws SQLException {
        String sql = "UPDATE repuestos SET stock = stock - ? "
                   + "WHERE id = ? AND stock >= ?";

        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setInt(2, repuestoId);
            ps.setInt(3, cantidad);
            return ps.executeUpdate() > 0;
        }
    }
}