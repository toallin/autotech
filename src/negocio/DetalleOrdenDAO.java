package negocio;

import config.conexion;
import entidad.DetalleOrden;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DetalleOrdenDAO {

    // ============================================================
    // INSERTAR UN ÍTEM
    // ============================================================
    public boolean insertar(DetalleOrden d) {
        String sql = "INSERT INTO detalle_orden "
                   + "(orden_id, repuesto_id, tipo_item, descripcion, "
                   + "cantidad, precio_unitario, subtotal) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, d.getOrdenId());
            if (d.getRepuestoId() > 0) ps.setInt(2, d.getRepuestoId());
            else ps.setNull(2, java.sql.Types.INTEGER);
            ps.setString(3, d.getTipoItem());
            ps.setString(4, d.getDescripcion());
            ps.setInt(5, d.getCantidad());
            ps.setDouble(6, d.getPrecioUnitario());
            ps.setDouble(7, d.getSubtotal());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar detalle: " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    // LISTAR POR ORDEN
    // ============================================================
    public List<DetalleOrden> listarPorOrden(int ordenId) {
        List<DetalleOrden> lista = new ArrayList<>();
        String sql = "SELECT * FROM detalle_orden WHERE orden_id = ? ORDER BY id";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, ordenId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar detalle: " + e.getMessage());
        }

        return lista;
    }

    // ============================================================
    // ELIMINAR TODOS LOS ÍTEMS DE UNA ORDEN
    // ============================================================
    public boolean eliminarPorOrden(int ordenId) {
        String sql = "DELETE FROM detalle_orden WHERE orden_id = ?";
        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, ordenId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al eliminar detalle: " + e.getMessage());
            return false;
        }
    }

    private DetalleOrden mapear(ResultSet rs) throws SQLException {
        DetalleOrden d = new DetalleOrden();
        d.setId(rs.getInt("id"));
        d.setOrdenId(rs.getInt("orden_id"));
        d.setRepuestoId(rs.getInt("repuesto_id"));
        d.setTipoItem(rs.getString("tipo_item"));
        d.setDescripcion(rs.getString("descripcion"));
        d.setCantidad(rs.getInt("cantidad"));
        d.setPrecioUnitario(rs.getDouble("precio_unitario"));
        d.setSubtotal(rs.getDouble("subtotal"));
        return d;
    }
}