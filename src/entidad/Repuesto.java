package entidad;

public class Repuesto {

    private int id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private int stock;
    private int stockMinimo;
    private double precioCompra;
    private double precioVenta;
    private int activo;

    public Repuesto() {
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }

    public double getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(double precioCompra) { this.precioCompra = precioCompra; }

    public double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(double precioVenta) { this.precioVenta = precioVenta; }

    public int getActivo() { return activo; }
    public void setActivo(int activo) { this.activo = activo; }

    /** Indica si el stock está por debajo del mínimo */
    public boolean isStockBajo() {
        return stock <= stockMinimo;
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre;
    }
}