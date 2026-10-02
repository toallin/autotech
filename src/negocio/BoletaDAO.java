package negocio;

import config.conexion;
import entidad.Boleta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BoletaDAO {

    // ============================================================
    // INSERTAR (retorna ID generado)
    // ============================================================
    public int insertar(Boleta b) {
        String sql = "INSERT INTO boletas "
                   + "(numero, serie, correlativo, cliente_id, vehiculo_id, orden_id, "
                   + "tipo, subtotal, igv, total, metodo_pago, estado, "
                   + "usuario_id, observacion, activo) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'EMITIDA', ?, ?, 1)";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, b.getNumero());
            ps.setString(2, b.getSerie());
            ps.setInt(3, b.getCorrelativo());
            ps.setInt(4, b.getClienteId());
            if (b.getVehiculoId() > 0) ps.setInt(5, b.getVehiculoId());
            else ps.setNull(5, java.sql.Types.INTEGER);
            if (b.getOrdenId() > 0) ps.setInt(6, b.getOrdenId());
            else ps.setNull(6, java.sql.Types.INTEGER);
            ps.setString(7, b.getTipo());
            ps.setDouble(8, b.getSubtotal());
            ps.setDouble(9, b.getIgv());
            ps.setDouble(10, b.getTotal());
            ps.setString(11, b.getMetodoPago());
            ps.setInt(12, b.getUsuarioId());
            ps.setString(13, b.getObservacion());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar boleta: " + e.getMessage());
        }

        return -1;
    }

    // ============================================================
    // ANULAR
    // ============================================================
    public boolean anular(int id) {
        String sql = "UPDATE boletas SET estado = 'ANULADA' WHERE id = ?";
        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al anular boleta: " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    // BUSCAR POR ID
    // ============================================================
    public Boleta buscarPorId(int id) {
        String sql = "SELECT b.*, c.nombre AS nombre_cliente, c.dni AS dni_cliente, "
                   + "v.placa AS placa_vehiculo, u.nombre_completo AS nombre_usuario "
                   + "FROM boletas b "
                   + "LEFT JOIN clientes c ON b.cliente_id = c.id "
                   + "LEFT JOIN vehiculos v ON b.vehiculo_id = v.id "
                   + "LEFT JOIN usuarios u ON b.usuario_id = u.id "
                   + "WHERE b.id = ?";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar boleta: " + e.getMessage());
        }
        return null;
    }

    // ============================================================
    // LISTAR TODAS
    // ============================================================
    public List<Boleta> listar() {
        return listarConFiltro(null, true);
    }

    // ============================================================
    // LISTAR SIN ANULADAS
    // ============================================================
    public List<Boleta> listarSinAnuladas() {
        return listarConFiltro(null, false);
    }

    // ============================================================
    // BUSCAR POR TEXTO
    // ============================================================
    public List<Boleta> buscar(String texto, boolean incluirAnuladas) {
        return listarConFiltro(texto, incluirAnuladas);
    }

    private List<Boleta> listarConFiltro(String texto, boolean incluirAnuladas) {
        List<Boleta> lista = new ArrayList<>();

        String sql = "SELECT b.*, c.nombre AS nombre_cliente, c.dni AS dni_cliente, "
                   + "v.placa AS placa_vehiculo, u.nombre_completo AS nombre_usuario "
                   + "FROM boletas b "
                   + "LEFT JOIN clientes c ON b.cliente_id = c.id "
                   + "LEFT JOIN vehiculos v ON b.vehiculo_id = v.id "
                   + "LEFT JOIN usuarios u ON b.usuario_id = u.id "
                   + "WHERE 1=1 ";

        if (texto != null && !texto.isEmpty()) {
            sql += "AND (b.numero LIKE ? OR c.nombre LIKE ? OR c.dni LIKE ?) ";
        }
        if (!incluirAnuladas) {
            sql += "AND b.estado <> 'ANULADA' ";
        }
        sql += "ORDER BY b.id DESC";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            if (texto != null && !texto.isEmpty()) {
                String f = "%" + texto + "%";
                ps.setString(1, f);
                ps.setString(2, f);
                ps.setString(3, f);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar boletas: " + e.getMessage());
        }

        return lista;
    }

    // ============================================================
    // OBTENER SIGUIENTE NÚMERO DE BOLETA
    // ============================================================
    public String obtenerSiguienteNumero() {
        String sql = "SELECT MAX(correlativo) AS ultimo FROM boletas";

        int ultimo = 0;
        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) ultimo = rs.getInt("ultimo");
        } catch (SQLException e) {
            System.err.println("Error al obtener correlativo: " + e.getMessage());
        }

        return String.format("B001-%06d", ultimo + 1);
    }

    // ============================================================
    // OBTENER CORRELATIVO SIGUIENTE (número)
    // ============================================================
    public int obtenerSiguienteCorrelativo() {
        String sql = "SELECT MAX(correlativo) AS ultimo FROM boletas";
        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt("ultimo") + 1;
        } catch (SQLException e) {
            System.err.println("Error al obtener correlativo: " + e.getMessage());
        }
        return 1;
    }

    // ============================================================
    // MAPEAR
    // ============================================================
    private Boleta mapear(ResultSet rs) throws SQLException {
        Boleta b = new Boleta();
        b.setId(rs.getInt("id"));
        b.setNumero(rs.getString("numero"));
        b.setSerie(rs.getString("serie"));
        b.setCorrelativo(rs.getInt("correlativo"));
        b.setClienteId(rs.getInt("cliente_id"));
        b.setVehiculoId(rs.getInt("vehiculo_id"));
        b.setOrdenId(rs.getInt("orden_id"));
        b.setTipo(rs.getString("tipo"));
        b.setFechaEmision(rs.getString("fecha_emision"));
        b.setSubtotal(rs.getDouble("subtotal"));
        b.setIgv(rs.getDouble("igv"));
        b.setTotal(rs.getDouble("total"));
        b.setMetodoPago(rs.getString("metodo_pago"));
        b.setEstado(rs.getString("estado"));
        b.setUsuarioId(rs.getInt("usuario_id"));
        b.setObservacion(rs.getString("observacion"));
        b.setActivo(rs.getInt("activo"));

        try { b.setNombreCliente(rs.getString("nombre_cliente")); } catch (SQLException ignored) {}
        try { b.setDniCliente(rs.getString("dni_cliente")); } catch (SQLException ignored) {}
        try { b.setPlacaVehiculo(rs.getString("placa_vehiculo")); } catch (SQLException ignored) {}
        try { b.setNombreUsuario(rs.getString("nombre_usuario")); } catch (SQLException ignored) {}

        return b;
    }
        // ============================================================
    // INSERTAR CON TRANSACCIÓN (recibe Connection)
    // ============================================================
    public int insertar(Connection cn, Boleta b) throws SQLException {
        String sql = "INSERT INTO boletas "
                   + "(numero, serie, correlativo, cliente_id, vehiculo_id, orden_id, "
                   + "tipo, subtotal, igv, total, metodo_pago, estado, "
                   + "usuario_id, observacion, activo) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'EMITIDA', ?, ?, 1)";

        try (PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, b.getNumero());
            ps.setString(2, b.getSerie());
            ps.setInt(3, b.getCorrelativo());
            ps.setInt(4, b.getClienteId());
            if (b.getVehiculoId() > 0) ps.setInt(5, b.getVehiculoId());
            else ps.setNull(5, java.sql.Types.INTEGER);
            if (b.getOrdenId() > 0) ps.setInt(6, b.getOrdenId());
            else ps.setNull(6, java.sql.Types.INTEGER);
            ps.setString(7, b.getTipo());
            ps.setDouble(8, b.getSubtotal());
            ps.setDouble(9, b.getIgv());
            ps.setDouble(10, b.getTotal());
            ps.setString(11, b.getMetodoPago());
            ps.setInt(12, b.getUsuarioId());
            ps.setString(13, b.getObservacion());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    // ============================================================
    // OBTENER SIGUIENTE CORRELATIVO CON BLOQUEO (FOR UPDATE)
    // ============================================================
    public int obtenerSiguienteCorrelativoConBloqueo(Connection cn) throws SQLException {
        String sqlSel = "SELECT correlativo_actual FROM configuracion WHERE id = 1 FOR UPDATE";
        String sqlUpd = "UPDATE configuracion SET correlativo_actual = ? WHERE id = 1";

        int corr = 1;
        try (PreparedStatement ps = cn.prepareStatement(sqlSel);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                corr = rs.getInt("correlativo_actual") + 1;
            }
        }
        try (PreparedStatement ps = cn.prepareStatement(sqlUpd)) {
            ps.setInt(1, corr);
            ps.executeUpdate();
        }
        return corr;
    }

    // ============================================================
    // ANULAR CON TRANSACCIÓN (devuelve stock + reabre orden)
    // ============================================================
    public void anular(Connection cn, int id) throws SQLException {
        // 1. Devolver stock de repuestos
        String sqlStock = "UPDATE repuestos r "
                        + "JOIN detalle_boleta d ON d.repuesto_id = r.id "
                        + "SET r.stock = r.stock + d.cantidad "
                        + "WHERE d.boleta_id = ? AND d.tipo_item = 'REPUESTO'";
        try (PreparedStatement ps = cn.prepareStatement(sqlStock)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }

        // 2. Devolver la orden a FINALIZADO
        String sqlOrden = "UPDATE ordenes_trabajo o "
                        + "JOIN boletas b ON b.orden_id = o.id "
                        + "SET o.estado = 'FINALIZADO', o.fecha_entrega = NULL "
                        + "WHERE b.id = ?";
        try (PreparedStatement ps = cn.prepareStatement(sqlOrden)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }

        // 3. Marcar la boleta como ANULADA (solo si estaba EMITIDA)
        String sqlBoleta = "UPDATE boletas SET estado = 'ANULADA' "
                         + "WHERE id = ? AND estado = 'EMITIDA'";
        try (PreparedStatement ps = cn.prepareStatement(sqlBoleta)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}