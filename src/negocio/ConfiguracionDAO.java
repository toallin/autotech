package negocio;

import config.conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConfiguracionDAO {

    public double obtenerIgv() {
        String sql = "SELECT igv_porcentaje FROM configuracion LIMIT 1";
        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble("igv_porcentaje");
        } catch (SQLException e) {
            System.err.println("Error al obtener IGV: " + e.getMessage());
        }
        return 18.0;
    }

    public String obtenerNombreTaller() {
        String sql = "SELECT nombre_taller FROM configuracion LIMIT 1";
        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getString("nombre_taller");
        } catch (SQLException e) {
            System.err.println("Error al obtener nombre taller: " + e.getMessage());
        }
        return "AutoTech";
    }

    public String obtenerRuc() {
        String sql = "SELECT ruc FROM configuracion LIMIT 1";
        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getString("ruc");
        } catch (SQLException e) {
            System.err.println("Error al obtener RUC: " + e.getMessage());
        }
        return "";
    }

    public String obtenerDireccion() {
        String sql = "SELECT direccion FROM configuracion LIMIT 1";
        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getString("direccion");
        } catch (SQLException e) {
            System.err.println("Error al obtener dirección: " + e.getMessage());
        }
        return "";
    }
        // ============================================================
    // OBTENER TODA LA CONFIGURACIÓN
    // ============================================================
    public entidad.Configuracion obtener() {
        entidad.Configuracion c = new entidad.Configuracion();
        String sql = "SELECT * FROM configuracion LIMIT 1";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                c.setId(rs.getInt("id"));
                c.setNombreTaller(rs.getString("nombre_taller"));
                c.setRuc(rs.getString("ruc"));
                c.setDireccion(rs.getString("direccion"));
                c.setTelefono(rs.getString("telefono"));
                c.setSerieBoleta(rs.getString("serie_boleta"));
                c.setCorrelativoActual(rs.getInt("correlativo_actual"));
                c.setIgvPorcentaje(rs.getDouble("igv_porcentaje"));
                c.setMoneda(rs.getString("moneda"));
                c.setLogo(rs.getString("logo"));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener configuración: " + e.getMessage());
        }

        return c;
    }

    // ============================================================
    // ACTUALIZAR CONFIGURACIÓN
    // ============================================================
    public boolean actualizar(entidad.Configuracion c) {
        String sql = "UPDATE configuracion SET "
                   + "nombre_taller=?, ruc=?, direccion=?, telefono=?, "
                   + "serie_boleta=?, igv_porcentaje=?, moneda=? "
                   + "WHERE id=?";

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, c.getNombreTaller());
            ps.setString(2, c.getRuc());
            ps.setString(3, c.getDireccion());
            ps.setString(4, c.getTelefono());
            ps.setString(5, c.getSerieBoleta());
            ps.setDouble(6, c.getIgvPorcentaje());
            ps.setString(7, c.getMoneda());
            ps.setInt(8, c.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar configuración: " + e.getMessage());
            return false;
        }
    }
}