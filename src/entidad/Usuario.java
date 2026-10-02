package entidad;

public class Usuario {

    private int id;
    private String usuario;
    private String password;
    private String nombreCompleto;
    private String rol;          // "ADMIN" o "EMPLEADO"
    private String telefono;
    private int activo;          // 1 = activo, 0 = inactivo

    public Usuario() {
    }

    public Usuario(String usuario, String password, String nombreCompleto, String rol, String telefono, int activo) {
        this.usuario = usuario;
        this.password = password;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
        this.telefono = telefono;
        this.activo = activo;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public int getActivo() { return activo; }
    public void setActivo(int activo) { this.activo = activo; }

    @Override
    public String toString() {
        return nombreCompleto + " (" + rol + ")";
    }
}