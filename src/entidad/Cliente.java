package entidad;

public class Cliente {

    private int id;
    private String nombre;
    private String dni;
    private String telefono;
    private String email;
    private String direccion;
    private int activo;   // 1 = activo, 0 = inactivo

    public Cliente() {
    }

    public Cliente(String nombre, String dni, String telefono,
                   String email, String direccion, int activo) {
        this.nombre = nombre;
        this.dni = dni;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.activo = activo;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public int getActivo() { return activo; }
    public void setActivo(int activo) { this.activo = activo; }

    @Override
    public String toString() {
        return nombre + " - " + dni;
    }
}