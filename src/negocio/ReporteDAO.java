package negocio;

import config.conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReporteDAO {

    // ============================================================
    // CLASE INTERNA PARA RESULTADOS
    // ============================================================
    public static class FilaReporte {
        public Object[] datos;
        public FilaReporte(Object[] datos) { this.datos = datos; }
    }

    // ============================================================
    // 1. REPORTE POR EMPLEADO
    //    Cuántas boletas emitió cada usuario y cuánto facturó
    // ============================================================
    public List<Object[]> reporteEmpleados(String fechaIni, String fechaFin) {
        List<Object[]> lista = new ArrayList<>();

        String sql = "SELECT u.nombre_completo, u.rol, "
                   + "       COUNT(b.id) AS total_boletas, "
                   + "       COALESCE(SUM(b.total), 0) AS total_vendido "
                   + "FROM usuarios u "
                   + "LEFT JOIN boletas b ON b.usuario_id = u.id "
                   + "     AND b.estado = 'EMITIDA' "
                   + "     AND DATE(b.fecha_emision) BETWEEN ? AND ? "
                   + "WHERE u.activo = 1 "
                   + "GROUP BY u.id, u.nombre_completo, u.rol "
                   + "ORDER BY total_vendido DESC";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, fechaIni);
            ps.setString(2, fechaFin);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Object[]{
                        rs.getString("nombre_completo"),
                        rs.getString("rol"),
                        rs.getInt("total_boletas"),
                        rs.getDouble("total_vendido")
                    });
                }
            }

        } catch (SQLException e) {
            System.err.println("Error en reporteEmpleados: " + e.getMessage());
        }

        return lista;
    }

    // ============================================================
    // 2. REPORTE DE BOLETAS
    //    Historial de boletas emitidas en el período
    // ============================================================
    public List<Object[]> reporteBoletas(String fechaIni, String fechaFin) {
        List<Object[]> lista = new ArrayList<>();

        String sql = "SELECT b.numero, DATE(b.fecha_emision) AS fecha, "
                   + "       c.nombre AS cliente, c.dni, "
                   + "       b.metodo_pago, b.subtotal, b.igv, b.total, "
                   + "       u.nombre_completo AS usuario "
                   + "FROM boletas b "
                   + "LEFT JOIN clientes c ON b.cliente_id = c.id "
                   + "LEFT JOIN usuarios u ON b.usuario_id = u.id "
                   + "WHERE b.estado = 'EMITIDA' "
                   + "  AND DATE(b.fecha_emision) BETWEEN ? AND ? "
                   + "ORDER BY b.fecha_emision DESC";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, fechaIni);
            ps.setString(2, fechaFin);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Object[]{
                        rs.getString("numero"),
                        rs.getString("fecha"),
                        rs.getString("cliente"),
                        rs.getString("dni"),
                        rs.getString("metodo_pago"),
                        rs.getDouble("subtotal"),
                        rs.getDouble("igv"),
                        rs.getDouble("total"),
                        rs.getString("usuario")
                    });
                }
            }

        } catch (SQLException e) {
            System.err.println("Error en reporteBoletas: " + e.getMessage());
        }

        return lista;
    }

    // ============================================================
    // 3. REPORTE DE REPUESTOS MÁS VENDIDOS
    // ============================================================
    public List<Object[]> reporteRepuestos(String fechaIni, String fechaFin) {
        List<Object[]> lista = new ArrayList<>();

        String sql = "SELECT r.codigo, r.nombre, "
                   + "       SUM(db.cantidad) AS total_cantidad, "
                   + "       SUM(db.subtotal) AS total_ingresos "
                   + "FROM detalle_boleta db "
                   + "INNER JOIN boletas b ON db.boleta_id = b.id "
                   + "INNER JOIN repuestos r ON db.repuesto_id = r.id "
                   + "WHERE b.estado = 'EMITIDA' "
                   + "  AND DATE(b.fecha_emision) BETWEEN ? AND ? "
                   + "  AND db.tipo_item = 'REPUESTO' "
                   + "GROUP BY r.id, r.codigo, r.nombre "
                   + "ORDER BY total_cantidad DESC";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, fechaIni);
            ps.setString(2, fechaFin);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Object[]{
                        rs.getString("codigo"),
                        rs.getString("nombre"),
                        rs.getInt("total_cantidad"),
                        rs.getDouble("total_ingresos")
                    });
                }
            }

        } catch (SQLException e) {
            System.err.println("Error en reporteRepuestos: " + e.getMessage());
        }

        return lista;
    }

    // ============================================================
    // 4. REPORTE DE TOP CLIENTES
    // ============================================================
    public List<Object[]> reporteClientes(String fechaIni, String fechaFin) {
        List<Object[]> lista = new ArrayList<>();

        String sql = "SELECT c.nombre, c.dni, c.telefono, "
                   + "       COUNT(b.id) AS total_boletas, "
                   + "       COALESCE(SUM(b.total), 0) AS total_gastado "
                   + "FROM clientes c "
                   + "INNER JOIN boletas b ON b.cliente_id = c.id "
                   + "     AND b.estado = 'EMITIDA' "
                   + "     AND DATE(b.fecha_emision) BETWEEN ? AND ? "
                   + "WHERE c.activo = 1 "
                   + "GROUP BY c.id, c.nombre, c.dni, c.telefono "
                   + "ORDER BY total_gastado DESC";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, fechaIni);
            ps.setString(2, fechaFin);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Object[]{
                        rs.getString("nombre"),
                        rs.getString("dni"),
                        rs.getString("telefono"),
                        rs.getInt("total_boletas"),
                        rs.getDouble("total_gastado")
                    });
                }
            }

        } catch (SQLException e) {
            System.err.println("Error en reporteClientes: " + e.getMessage());
        }

        return lista;
    }

    // ============================================================
    // MÉTODOS AUXILIARES: TOTALES
    // ============================================================

    /** Total facturado en un período */
    public double totalFacturado(String fechaIni, String fechaFin) {
        String sql = "SELECT COALESCE(SUM(total), 0) AS total "
                   + "FROM boletas "
                   + "WHERE estado = 'EMITIDA' "
                   + "  AND DATE(fecha_emision) BETWEEN ? AND ?";
        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, fechaIni);
            ps.setString(2, fechaFin);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("Error al calcular total: " + e.getMessage());
        }
        return 0;
    }

    /** Cantidad total de boletas emitidas */
    public int totalBoletas(String fechaIni, String fechaFin) {
        String sql = "SELECT COUNT(*) AS total FROM boletas "
                   + "WHERE estado = 'EMITIDA' "
                   + "  AND DATE(fecha_emision) BETWEEN ? AND ?";
        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, fechaIni);
            ps.setString(2, fechaFin);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error al contar boletas: " + e.getMessage());
        }
        return 0;
    }

    /** Cantidad total de repuestos vendidos */
    public int totalRepuestosVendidos(String fechaIni, String fechaFin) {
        String sql = "SELECT COALESCE(SUM(db.cantidad), 0) AS total "
                   + "FROM detalle_boleta db "
                   + "INNER JOIN boletas b ON db.boleta_id = b.id "
                   + "WHERE b.estado = 'EMITIDA' "
                   + "  AND DATE(b.fecha_emision) BETWEEN ? AND ? "
                   + "  AND db.tipo_item = 'REPUESTO'";
        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, fechaIni);
            ps.setString(2, fechaFin);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error al contar repuestos: " + e.getMessage());
        }
        return 0;
    }

    /** Cantidad total de clientes atendidos (con al menos 1 boleta) */
    public int totalClientesAtendidos(String fechaIni, String fechaFin) {
        String sql = "SELECT COUNT(DISTINCT cliente_id) AS total FROM boletas "
                   + "WHERE estado = 'EMITIDA' "
                   + "  AND DATE(fecha_emision) BETWEEN ? AND ?";
        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, fechaIni);
            ps.setString(2, fechaFin);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error al contar clientes: " + e.getMessage());
        }
        return 0;
    }
}