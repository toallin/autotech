package entidad;

public class DetalleOrden {

    private int id;
    private int ordenId;
    private int repuestoId;      // 0 si es mano de obra
    private String tipoItem;     // REPUESTO o MANO_OBRA
    private String descripcion;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;

    public DetalleOrden() {
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOrdenId() { return ordenId; }
    public void setOrdenId(int ordenId) { this.ordenId = ordenId; }

    public int getRepuestoId() { return repuestoId; }
    public void setRepuestoId(int repuestoId) { this.repuestoId = repuestoId; }

    public String getTipoItem() { return tipoItem; }
    public void setTipoItem(String tipoItem) { this.tipoItem = tipoItem; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    /** Calcula el subtotal según cantidad y precio */
    public void calcularSubtotal() {
        this.subtotal = cantidad * precioUnitario;
    }

    @Override
    public String toString() {
        return tipoItem + ": " + descripcion + " x" + cantidad;
    }
}