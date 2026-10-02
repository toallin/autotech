package entidad;

public class Configuracion {

    private int id;
    private String nombreTaller;
    private String ruc;
    private String direccion;
    private String telefono;
    private String serieBoleta;
    private int correlativoActual;
    private double igvPorcentaje;
    private String moneda;
    private String logo;

    public Configuracion() {
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombreTaller() { return nombreTaller; }
    public void setNombreTaller(String nombreTaller) { this.nombreTaller = nombreTaller; }

    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getSerieBoleta() { return serieBoleta; }
    public void setSerieBoleta(String serieBoleta) { this.serieBoleta = serieBoleta; }

    public int getCorrelativoActual() { return correlativoActual; }
    public void setCorrelativoActual(int correlativoActual) { this.correlativoActual = correlativoActual; }

    public double getIgvPorcentaje() { return igvPorcentaje; }
    public void setIgvPorcentaje(double igvPorcentaje) { this.igvPorcentaje = igvPorcentaje; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }

    public String getLogo() { return logo; }
    public void setLogo(String logo) { this.logo = logo; }
}