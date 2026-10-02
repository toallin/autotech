package entidad;

import java.util.ArrayList;
import java.util.List;

public class OrdenTrabajo {

    private int id;
    private String numeroOrden;
    private int clienteId;
    private int vehiculoId;
    private int mecanicoId;
    private String descripcionProblema;
    private String diagnostico;
    private String estado;      // PENDIENTE, EN_PROCESO, FINALIZADO, ENTREGADO, ANULADO
    private String fechaIngreso;
    private String fechaEntrega;
    private double total;
    private String observacion;
    private int activo;

    // Campos extra para mostrar en tabla
    private String nombreCliente;
    private String placaVehiculo;
    private String nombreMecanico;

    // Lista de ítems (detalle)
    private List<DetalleOrden> items = new ArrayList<>();

    public OrdenTrabajo() {
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNumeroOrden() { return numeroOrden; }
    public void setNumeroOrden(String numeroOrden) { this.numeroOrden = numeroOrden; }

    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }

    public int getVehiculoId() { return vehiculoId; }
    public void setVehiculoId(int vehiculoId) { this.vehiculoId = vehiculoId; }

    public int getMecanicoId() { return mecanicoId; }
    public void setMecanicoId(int mecanicoId) { this.mecanicoId = mecanicoId; }

    public String getDescripcionProblema() { return descripcionProblema; }
    public void setDescripcionProblema(String descripcionProblema) { this.descripcionProblema = descripcionProblema; }

    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(String fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public String getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(String fechaEntrega) { this.fechaEntrega = fechaEntrega; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }

    public int getActivo() { return activo; }
    public void setActivo(int activo) { this.activo = activo; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getPlacaVehiculo() { return placaVehiculo; }
    public void setPlacaVehiculo(String placaVehiculo) { this.placaVehiculo = placaVehiculo; }

    public String getNombreMecanico() { return nombreMecanico; }
    public void setNombreMecanico(String nombreMecanico) { this.nombreMecanico = nombreMecanico; }

    public List<DetalleOrden> getItems() { return items; }
    public void setItems(List<DetalleOrden> items) { this.items = items; }

    @Override
    public String toString() {
        return numeroOrden + " - " + nombreCliente;
    }
}