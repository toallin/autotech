package negocio;

import config.conexion;
import entidad.DetalleBoleta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DetalleBoletaDAO {

    public boolean insertar(DetalleBoleta d) {
        String sql = "INSERT INTO detalle_boleta "
                   + "(boleta_id, tipo_item, repuesto_id, descripcion, "
                   + "cantidad, precio_unitario, subtotal) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, d.getBoletaId());
            ps.setString(2, d.getTipoItem());
            if (d.getRepuestoId() > 0) ps.setInt(3, d.getRepuestoId());
            else ps.setNull(3, java.sql.Types.INTEGER);
            ps.setString(4, d.getDescripcion());
            ps.setInt(5, d.getCantidad());
            ps.setDouble(6, d.getPrecioUnitario());
            ps.setDouble(7, d.getSubtotal());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar detalle boleta: " + e.getMessage());
            return false;
        }
    }

    public List<DetalleBoleta> listarPorBoleta(int boletaId) {
        List<DetalleBoleta> lista = new ArrayList<>();
        String sql = "SELECT * FROM detalle_boleta WHERE boleta_id = ? ORDER BY id";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, boletaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar detalle boleta: " + e.getMessage());
        }
        return lista;
    }

    public boolean eliminarPorBoleta(int boletaId) {
        String sql = "DELETE FROM detalle_boleta WHERE boleta_id = ?";
        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, boletaId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al eliminar detalle boleta: " + e.getMessage());
            return false;
        }
    }

    private DetalleBoleta mapear(ResultSet rs) throws SQLException {
        DetalleBoleta d = new DetalleBoleta();
        d.setId(rs.getInt("id"));
        d.setBoletaId(rs.getInt("boleta_id"));
        d.setTipoItem(rs.getString("tipo_item"));
        d.setRepuestoId(rs.getInt("repuesto_id"));
        d.setDescripcion(rs.getString("descripcion"));
        d.setCantidad(rs.getInt("cantidad"));
        d.setPrecioUnitario(rs.getDouble("precio_unitario"));
        d.setSubtotal(rs.getDouble("subtotal"));
        return d;
    }
        // ============================================================
    // INSERTAR CON TRANSACCIÓN (recibe Connection)
    // ============================================================
    public void insertar(Connection cn, DetalleBoleta d) throws SQLException {
        String sql = "INSERT INTO detalle_boleta "
                   + "(boleta_id, tipo_item, repuesto_id, descripcion, "
                   + "cantidad, precio_unitario, subtotal) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, d.getBoletaId());
            ps.setString(2, d.getTipoItem());
            if (d.getRepuestoId() > 0) ps.setInt(3, d.getRepuestoId());
            else ps.setNull(3, java.sql.Types.INTEGER);
            ps.setString(4, d.getDescripcion());
            ps.setInt(5, d.getCantidad());
            ps.setDouble(6, d.getPrecioUnitario());
            ps.setDouble(7, d.getSubtotal());
            ps.executeUpdate();
        }
    }
}