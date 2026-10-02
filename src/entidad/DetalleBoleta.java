package entidad;

public class DetalleBoleta {

    private int id;
    private int boletaId;
    private String tipoItem;        // REPUESTO / SERVICIO / MANO_OBRA
    private int repuestoId;
    private String descripcion;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;

    public DetalleBoleta() {
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getBoletaId() { return boletaId; }
    public void setBoletaId(int boletaId) { this.boletaId = boletaId; }

    public String getTipoItem() { return tipoItem; }
    public void setTipoItem(String tipoItem) { this.tipoItem = tipoItem; }

    public int getRepuestoId() { return repuestoId; }
    public void setRepuestoId(int repuestoId) { this.repuestoId = repuestoId; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public void calcularSubtotal() {
        this.subtotal = cantidad * precioUnitario;
    }

    @Override
    public String toString() {
        return tipoItem + ": " + descripcion + " x" + cantidad;
    }
}