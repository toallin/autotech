package negocio;

import config.conexion;
import entidad.OrdenTrabajo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OrdenDAO {

    // ============================================================
    // INSERTAR (retorna el ID generado, o -1 si falla)
    // ============================================================
    public int insertar(OrdenTrabajo o) {
        String sql = "INSERT INTO ordenes_trabajo "
                   + "(numero_orden, cliente_id, vehiculo_id, mecanico_id, "
                   + "descripcion_problema, diagnostico, estado, "
                   + "total, observacion, activo) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 1)";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, o.getNumeroOrden());
            ps.setInt(2, o.getClienteId());
            ps.setInt(3, o.getVehiculoId());
            if (o.getMecanicoId() > 0) ps.setInt(4, o.getMecanicoId());
            else ps.setNull(4, java.sql.Types.INTEGER);
            ps.setString(5, o.getDescripcionProblema());
            ps.setString(6, o.getDiagnostico());
            ps.setString(7, o.getEstado());
            ps.setDouble(8, o.getTotal());
            ps.setString(9, o.getObservacion());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar orden: " + e.getMessage());
        }

        return -1;
    }

    // ============================================================
    // ACTUALIZAR
    // ============================================================
    public boolean actualizar(OrdenTrabajo o) {
        String sql = "UPDATE ordenes_trabajo SET "
                   + "cliente_id=?, vehiculo_id=?, mecanico_id=?, "
                   + "descripcion_problema=?, diagnostico=?, estado=?, "
                   + "fecha_entrega=?, total=?, observacion=?, activo=? "
                   + "WHERE id=?";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, o.getClienteId());
            ps.setInt(2, o.getVehiculoId());
            if (o.getMecanicoId() > 0) ps.setInt(3, o.getMecanicoId());
            else ps.setNull(3, java.sql.Types.INTEGER);
            ps.setString(4, o.getDescripcionProblema());
            ps.setString(5, o.getDiagnostico());
            ps.setString(6, o.getEstado());
            ps.setString(7, o.getFechaEntrega());
            ps.setDouble(8, o.getTotal());
            ps.setString(9, o.getObservacion());
            ps.setInt(10, o.getActivo());
            ps.setInt(11, o.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar orden: " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    // ANULAR
    // ============================================================
    public boolean anular(int id) {
        String sql = "UPDATE ordenes_trabajo SET estado = 'ANULADO' WHERE id = ?";
        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al anular orden: " + e.getMessage());
            return false;
        }
    }

    // ============================================================
    // BUSCAR POR ID
    // ============================================================
    public OrdenTrabajo buscarPorId(int id) {
        String sql = "SELECT o.*, c.nombre AS nombre_cliente, "
                   + "v.placa AS placa_vehiculo, "
                   + "u.nombre_completo AS nombre_mecanico "
                   + "FROM ordenes_trabajo o "
                   + "LEFT JOIN clientes c ON o.cliente_id = c.id "
                   + "LEFT JOIN vehiculos v ON o.vehiculo_id = v.id "
                   + "LEFT JOIN usuarios u ON o.mecanico_id = u.id "
                   + "WHERE o.id = ?";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar orden por ID: " + e.getMessage());
        }

        return null;
    }

    // ============================================================
    // LISTAR TODAS
    // ============================================================
    public List<OrdenTrabajo> listar() {
        List<OrdenTrabajo> lista = new ArrayList<>();
        String sql = "SELECT o.*, c.nombre AS nombre_cliente, "
                   + "v.placa AS placa_vehiculo, "
                   + "u.nombre_completo AS nombre_mecanico "
                   + "FROM ordenes_trabajo o "
                   + "LEFT JOIN clientes c ON o.cliente_id = c.id "
                   + "LEFT JOIN vehiculos v ON o.vehiculo_id = v.id "
                   + "LEFT JOIN usuarios u ON o.mecanico_id = u.id "
                   + "ORDER BY o.id DESC";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(mapear(rs));

        } catch (SQLException e) {
            System.err.println("Error al listar órdenes: " + e.getMessage());
        }

        return lista;
    }

    // ============================================================
    // LISTAR SIN ANULADAS
    // ============================================================
    public List<OrdenTrabajo> listarSinAnuladas() {
        List<OrdenTrabajo> lista = new ArrayList<>();
        String sql = "SELECT o.*, c.nombre AS nombre_cliente, "
                   + "v.placa AS placa_vehiculo, "
                   + "u.nombre_completo AS nombre_mecanico "
                   + "FROM ordenes_trabajo o "
                   + "LEFT JOIN clientes c ON o.cliente_id = c.id "
                   + "LEFT JOIN vehiculos v ON o.vehiculo_id = v.id "
                   + "LEFT JOIN usuarios u ON o.mecanico_id = u.id "
                   + "WHERE o.estado <> 'ANULADO' "
                   + "ORDER BY o.id DESC";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(mapear(rs));

        } catch (SQLException e) {
            System.err.println("Error al listar órdenes: " + e.getMessage());
        }

        return lista;
    }

    // ============================================================
    // BUSCAR POR N° ORDEN O PLACA
    // ============================================================
    public List<OrdenTrabajo> buscar(String texto, boolean incluirAnuladas) {
        List<OrdenTrabajo> lista = new ArrayList<>();
        String sql = "SELECT o.*, c.nombre AS nombre_cliente, "
                   + "v.placa AS placa_vehiculo, "
                   + "u.nombre_completo AS nombre_mecanico "
                   + "FROM ordenes_trabajo o "
                   + "LEFT JOIN clientes c ON o.cliente_id = c.id "
                   + "LEFT JOIN vehiculos v ON o.vehiculo_id = v.id "
                   + "LEFT JOIN usuarios u ON o.mecanico_id = u.id "
                   + "WHERE (o.numero_orden LIKE ? OR v.placa LIKE ? OR c.nombre LIKE ?) "
                   + (incluirAnuladas ? "" : "AND o.estado <> 'ANULADO' ")
                   + "ORDER BY o.id DESC";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            String filtro = "%" + texto + "%";
            ps.setString(1, filtro);
            ps.setString(2, filtro);
            ps.setString(3, filtro);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar órdenes: " + e.getMessage());
        }

        return lista;
    }

    // ============================================================
    // OBTENER ÚLTIMO N° DE ORDEN
    // ============================================================
    public String obtenerSiguienteNumero() {
        String sql = "SELECT MAX(CAST(SUBSTRING(numero_orden, 4) AS UNSIGNED)) AS ultimo "
                   + "FROM ordenes_trabajo "
                   + "WHERE numero_orden LIKE 'OT-%'";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                int ultimo = rs.getInt("ultimo");
                return String.format("OT-%06d", ultimo + 1);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener siguiente N°: " + e.getMessage());
        }

        return "OT-000001";
    }

    // ============================================================
    // MAPEAR
    // ============================================================
    private OrdenTrabajo mapear(ResultSet rs) throws SQLException {
        OrdenTrabajo o = new OrdenTrabajo();
        o.setId(rs.getInt("id"));
        o.setNumeroOrden(rs.getString("numero_orden"));
        o.setClienteId(rs.getInt("cliente_id"));
        o.setVehiculoId(rs.getInt("vehiculo_id"));
        o.setMecanicoId(rs.getInt("mecanico_id"));
        o.setDescripcionProblema(rs.getString("descripcion_problema"));
        o.setDiagnostico(rs.getString("diagnostico"));
        o.setEstado(rs.getString("estado"));
        o.setFechaIngreso(rs.getString("fecha_ingreso"));
        o.setFechaEntrega(rs.getString("fecha_entrega"));
        o.setTotal(rs.getDouble("total"));
        o.setObservacion(rs.getString("observacion"));
        o.setActivo(rs.getInt("activo"));

        try { o.setNombreCliente(rs.getString("nombre_cliente")); } catch (SQLException ignored) {}
        try { o.setPlacaVehiculo(rs.getString("placa_vehiculo")); } catch (SQLException ignored) {}
        try { o.setNombreMecanico(rs.getString("nombre_mecanico")); } catch (SQLException ignored) {}

        return o;
    }
        // ============================================================
    // LISTAR SOLO FINALIZADAS (para generar boletas)
    // ============================================================
    public List<OrdenTrabajo> listarFinalizadas() {
        List<OrdenTrabajo> lista = new ArrayList<>();
        String sql = "SELECT o.*, c.nombre AS nombre_cliente, "
                   + "v.placa AS placa_vehiculo, "
                   + "u.nombre_completo AS nombre_mecanico "
                   + "FROM ordenes_trabajo o "
                   + "LEFT JOIN clientes c ON o.cliente_id = c.id "
                   + "LEFT JOIN vehiculos v ON o.vehiculo_id = v.id "
                   + "LEFT JOIN usuarios u ON o.mecanico_id = u.id "
                   + "WHERE o.estado = 'FINALIZADO' "
                   + "ORDER BY o.id DESC";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(mapear(rs));

        } catch (SQLException e) {
            System.err.println("Error al listar órdenes finalizadas: " + e.getMessage());
        }

        return lista;
    }

    // ============================================================
    // MARCAR COMO ENTREGADO (al generar boleta)
    // ============================================================
    public boolean marcarEntregado(int id) {
        String sql = "UPDATE ordenes_trabajo SET estado = 'ENTREGADO', "
                   + "fecha_entrega = NOW() WHERE id = ?";
        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al marcar entregado: " + e.getMessage());
            return false;
        }
    }
        // ============================================================
    // LISTAR ÓRDENES DE UN MECÁNICO
    // ============================================================
    public List<OrdenTrabajo> listarPorMecanico(int mecanicoId, boolean soloActivas) {
        List<OrdenTrabajo> lista = new ArrayList<>();
        String sql = "SELECT o.*, c.nombre AS nombre_cliente, "
                   + "v.placa AS placa_vehiculo, "
                   + "u.nombre_completo AS nombre_mecanico "
                   + "FROM ordenes_trabajo o "
                   + "LEFT JOIN clientes c ON o.cliente_id = c.id "
                   + "LEFT JOIN vehiculos v ON o.vehiculo_id = v.id "
                   + "LEFT JOIN usuarios u ON o.mecanico_id = u.id "
                   + "WHERE o.mecanico_id = ? ";
        if (soloActivas) {
            sql += "AND o.estado IN ('PENDIENTE','EN_PROCESO') ";
        } else {
            sql += "AND o.estado IN ('FINALIZADO','ENTREGADO','ANULADO') ";
        }
        sql += "ORDER BY o.id DESC";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, mecanicoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar órdenes por mecánico: " + e.getMessage());
        }
        return lista;
    }

    // ============================================================
    // BUSCAR ÓRDENES DE UN MECÁNICO POR TEXTO
    // ============================================================
    public List<OrdenTrabajo> buscarPorMecanico(int mecanicoId, String texto, boolean soloActivas) {
        List<OrdenTrabajo> lista = new ArrayList<>();
        String sql = "SELECT o.*, c.nombre AS nombre_cliente, "
                   + "v.placa AS placa_vehiculo, "
                   + "u.nombre_completo AS nombre_mecanico "
                   + "FROM ordenes_trabajo o "
                   + "LEFT JOIN clientes c ON o.cliente_id = c.id "
                   + "LEFT JOIN vehiculos v ON o.vehiculo_id = v.id "
                   + "LEFT JOIN usuarios u ON o.mecanico_id = u.id "
                   + "WHERE o.mecanico_id = ? "
                   + "  AND (o.numero_orden LIKE ? OR c.nombre LIKE ? OR v.placa LIKE ?) ";
        if (soloActivas) {
            sql += "AND o.estado IN ('PENDIENTE','EN_PROCESO') ";
        } else {
            sql += "AND o.estado IN ('FINALIZADO','ENTREGADO','ANULADO') ";
        }
        sql += "ORDER BY o.id DESC";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, mecanicoId);
            String f = "%" + texto + "%";
            ps.setString(2, f);
            ps.setString(3, f);
            ps.setString(4, f);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar órdenes por mecánico: " + e.getMessage());
        }
        return lista;
    }
        // ============================================================
    // MARCAR ENTREGADO CON TRANSACCIÓN (recibe Connection)
    // ============================================================
    public void marcarEntregado(Connection cn, int id) throws SQLException {
        String sql = "UPDATE ordenes_trabajo SET estado = 'ENTREGADO', "
                   + "fecha_entrega = NOW() WHERE id = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

}