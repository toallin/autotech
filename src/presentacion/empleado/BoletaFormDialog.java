/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package presentacion.empleado;

/**
 *
 * @author PC
 */
public class BoletaFormDialog extends javax.swing.JDialog {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(BoletaFormDialog.class.getName());
private entidad.Boleta boletaActual;

private final negocio.BoletaDAO boletaDAO = new negocio.BoletaDAO();
private final negocio.DetalleBoletaDAO detalleBoletaDAO = new negocio.DetalleBoletaDAO();
private final negocio.OrdenDAO ordenDAO = new negocio.OrdenDAO();
private final negocio.DetalleOrdenDAO detalleOrdenDAO = new negocio.DetalleOrdenDAO();
private final negocio.ClienteDAO clienteDAO = new negocio.ClienteDAO();
private final negocio.VehiculoDAO vehiculoDAO = new negocio.VehiculoDAO();
private final negocio.RepuestoDAO repuestoDAO = new negocio.RepuestoDAO();
private final negocio.ConfiguracionDAO configDAO = new negocio.ConfiguracionDAO();

private final java.util.List<entidad.OrdenTrabajo> ordenesCombo = new java.util.ArrayList<>();
private final java.util.List<entidad.Repuesto> repuestosCombo = new java.util.ArrayList<>();
private final java.util.List<entidad.DetalleBoleta> itemsActuales = new java.util.ArrayList<>();

private double porcentajeIgv = 18.0;
private boolean modoVer = false;   // true = solo lectura (boleta ya emitida)
    /**
     * Creates new form BoletaFormDialog
     */
    public BoletaFormDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public BoletaFormDialog(java.awt.Frame parent, entidad.Boleta boleta) {
         super(parent, true);
    initComponents();
    this.boletaActual = boleta;
    setLocationRelativeTo(parent);
    setTitle(boleta == null ? "Generar Boleta" : "Boleta " + boleta.getNumero());

    porcentajeIgv = configDAO.obtenerIgv();

    configurarTablaDetalle();
    cargarCombos();

    if (boleta == null) {
        prepararNuevaBoleta();
    } else {
        modoVer = true;
        cargarBoletaEnFormulario(boleta);
        bloquearEdicion();
    }
    }
    private void configurarTablaDetalle() {
    javax.swing.table.DefaultTableModel modelo = new javax.swing.table.DefaultTableModel(
        new Object[][]{},
        new String[]{"Tipo", "Descripción", "Cantidad", "P. Unit", "Subtotal"}
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    tblDetalle.setModel(modelo);

    tblDetalle.getColumnModel().getColumn(0).setPreferredWidth(90);
    tblDetalle.getColumnModel().getColumn(1).setPreferredWidth(300);
    tblDetalle.getColumnModel().getColumn(2).setPreferredWidth(70);
    tblDetalle.getColumnModel().getColumn(3).setPreferredWidth(90);
    tblDetalle.getColumnModel().getColumn(4).setPreferredWidth(100);

    tblDetalle.setRowHeight(25);
}
    private void cargarCombos() {
    // Métodos de pago
    cboMetodoPago.removeAllItems();
    cboMetodoPago.addItem("EFECTIVO");
    cboMetodoPago.addItem("TARJETA");
    cboMetodoPago.addItem("TRANSFERENCIA");
    cboMetodoPago.addItem("YAPE");
    cboMetodoPago.addItem("PLIN");

    // Tipos de ítem
    cboTipoItem.removeAllItems();
    cboTipoItem.addItem("REPUESTO");
    cboTipoItem.addItem("MANO_OBRA");

    // Órdenes finalizadas
    cboOrden.removeAllItems();
    ordenesCombo.clear();
    cboOrden.addItem("-- Seleccione una orden --");
    ordenesCombo.add(null);
    for (entidad.OrdenTrabajo o : ordenDAO.listarFinalizadas()) {
        ordenesCombo.add(o);
        cboOrden.addItem(o.getNumeroOrden() + " - " + o.getNombreCliente() + " (S/ " + String.format("%.2f", o.getTotal()) + ")");
    }

    // Repuestos
    cboRepuesto.removeAllItems();
    repuestosCombo.clear();
    for (entidad.Repuesto r : repuestoDAO.listar()) {
        repuestosCombo.add(r);
        cboRepuesto.addItem(r.getCodigo() + " - " + r.getNombre());
    }
}
    private void prepararNuevaBoleta() {
    txtNumero.setText("B001-XXXXXX");txtNumero.setText("B001-XXXXXX");
    txtFecha.setText(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date()));

    rbDesdeOrden.setSelected(true);
    cboOrden.setEnabled(true);
    cboTipoItem.setEnabled(false);
    cboRepuesto.setEnabled(false);
    txtDescripcionItem.setEnabled(false);
    txtCantidad.setEnabled(false);
    txtPrecioItem.setEnabled(false);
    btnAgregarItem.setEnabled(false);
    btnQuitarItem.setEnabled(false);

    txtCliente.setText("");
    txtDni.setText("");
    txtVehiculo.setText("");
    cboOrden.setSelectedIndex(0);
    cboMetodoPago.setSelectedIndex(0);

    itemsActuales.clear();
    refrescarTablaDetalle();
    recalcularTotales();
}
    private void cargarBoletaEnFormulario(entidad.Boleta b) {
    txtNumero.setText(b.getNumero());
    txtFecha.setText(b.getFechaEmision() != null ? b.getFechaEmision() : "");
    txtCliente.setText(b.getNombreCliente() != null ? b.getNombreCliente() : "");
    txtDni.setText(b.getDniCliente() != null ? b.getDniCliente() : "");
    txtVehiculo.setText(b.getPlacaVehiculo() != null ? b.getPlacaVehiculo() : "");
    cboMetodoPago.setSelectedItem(b.getMetodoPago());

    if ("SERVICIO".equals(b.getTipo())) {
        rbDesdeOrden.setSelected(true);
    } else {
        rbVentaDirecta.setSelected(true);
    }

    // Cargar ítems desde BD
    itemsActuales.clear();
    itemsActuales.addAll(detalleBoletaDAO.listarPorBoleta(b.getId()));
    refrescarTablaDetalle();
    recalcularTotales();

    // Deshabilitar todo (solo lectura)
    cboOrden.setEnabled(false);
    rbDesdeOrden.setEnabled(false);
    rbVentaDirecta.setEnabled(false);
}
    private int clienteVentaDirectaId = 0;

private int obtenerClienteSeleccionadoId() {
    return clienteVentaDirectaId;
}

private void seleccionarClienteVentaDirecta() {
    // Por ahora, abre un diálogo simple para buscar cliente
    String dni = javax.swing.JOptionPane.showInputDialog(this, 
        "Ingrese el DNI del cliente:");
    if (dni == null || dni.trim().isEmpty()) return;

    entidad.Cliente c = clienteDAO.buscarPorDni(dni.trim());
    if (c == null) {
        javax.swing.JOptionPane.showMessageDialog(this, "Cliente no encontrado.");
        return;
    }
    txtCliente.setText(c.getNombre());
    txtDni.setText(c.getDni());
    clienteVentaDirectaId = c.getId();
}

private void bloquearEdicion() {
    cboOrden.setEnabled(false);
    rbDesdeOrden.setEnabled(false);
    rbVentaDirecta.setEnabled(false);
    cboTipoItem.setEnabled(false);
    cboRepuesto.setEnabled(false);
    txtDescripcionItem.setEnabled(false);
    txtCantidad.setEnabled(false);
    txtPrecioItem.setEnabled(false);
    btnAgregarItem.setEnabled(false);
    btnQuitarItem.setEnabled(false);
    cboMetodoPago.setEnabled(false);
    btnGuardar.setEnabled(false);
}private void refrescarTablaDetalle() {
    javax.swing.table.DefaultTableModel modelo = (javax.swing.table.DefaultTableModel) tblDetalle.getModel();
    modelo.setRowCount(0);

    for (entidad.DetalleBoleta d : itemsActuales) {
        modelo.addRow(new Object[]{
            d.getTipoItem(),
            d.getDescripcion(),
            d.getCantidad(),
            String.format(java.util.Locale.US, "%.2f", d.getPrecioUnitario()),
            String.format(java.util.Locale.US, "%.2f", d.getSubtotal())
        });
    }
}

private void recalcularTotales() {
    double subtotal = 0;
    for (entidad.DetalleBoleta d : itemsActuales) subtotal += d.getSubtotal();

    // Los precios YA incluyen IGV → separarlo:
    double base = subtotal / (1 + porcentajeIgv / 100.0);
    double igv  = subtotal - base;

    lblSubtotal.setText(String.format("S/ %.2f", base));
    lblIgv.setText(String.format("S/ %.2f", igv));
    lblTotal.setText(String.format("S/ %.2f", subtotal));
}
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        grupoTipoBoleta = new javax.swing.ButtonGroup();
        lblTitulo = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        lblFecha = new javax.swing.JLabel();
        lblTipo = new javax.swing.JLabel();
        lblNumero = new javax.swing.JLabel();
        rbVentaDirecta = new javax.swing.JRadioButton();
        rbDesdeOrden = new javax.swing.JRadioButton();
        lblOrden = new javax.swing.JLabel();
        cboOrden = new javax.swing.JComboBox<>();
        txtFecha = new javax.swing.JTextField();
        txtNumero = new javax.swing.JTextField();
        txtCliente = new javax.swing.JTextField();
        txtDni = new javax.swing.JTextField();
        txtVehiculo = new javax.swing.JTextField();
        lblOrden1 = new javax.swing.JLabel();
        lblOrden2 = new javax.swing.JLabel();
        lblOrden3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        lblTipoItem = new javax.swing.JLabel();
        cboTipoItem = new javax.swing.JComboBox<>();
        lblRepuesto = new javax.swing.JLabel();
        cboRepuesto = new javax.swing.JComboBox<>();
        lblDescripcionItem = new javax.swing.JLabel();
        txtDescripcionItem = new javax.swing.JTextField();
        lblCantidad = new javax.swing.JLabel();
        txtPrecioItem = new javax.swing.JTextField();
        lblPrecioItem = new javax.swing.JLabel();
        txtCantidad = new javax.swing.JTextField();
        btnAgregarItem = new javax.swing.JButton();
        btnQuitarItem = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDetalle = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        lblSubtotalTitulo = new javax.swing.JLabel();
        lblSubtotal = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        lblIgvTitulo = new javax.swing.JLabel();
        lblIgv = new javax.swing.JLabel();
        lblTotalTitulo = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblMetodoPago = new javax.swing.JLabel();
        cboMetodoPago = new javax.swing.JComboBox<>();
        btnGuardar = new javax.swing.JButton();
        btnImprimir = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        lblTitulo.setBackground(new java.awt.Color(255, 255, 255));
        lblTitulo.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(102, 102, 255));
        lblTitulo.setText("GENERAR BOLETA");

        jPanel1.setBackground(new java.awt.Color(255, 204, 204));
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));

        lblFecha.setText("Fecha:");

        lblTipo.setText("tipo");

        lblNumero.setText("N° Boleta:");

        grupoTipoBoleta.add(rbVentaDirecta);
        rbVentaDirecta.setText("Venta Directa");
        rbVentaDirecta.addActionListener(this::rbVentaDirectaActionPerformed);

        grupoTipoBoleta.add(rbDesdeOrden);
        rbDesdeOrden.setText("Desde Orden");
        rbDesdeOrden.addActionListener(this::rbDesdeOrdenActionPerformed);

        lblOrden.setText("Orden N°:");

        cboOrden.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboOrden.addActionListener(this::cboOrdenActionPerformed);

        txtFecha.setBackground(new java.awt.Color(255, 255, 204));
        txtFecha.setEnabled(false);

        txtNumero.setBackground(new java.awt.Color(255, 255, 204));
        txtNumero.setEnabled(false);
        txtNumero.addActionListener(this::txtNumeroActionPerformed);

        txtCliente.setEnabled(false);

        txtDni.setEnabled(false);

        txtVehiculo.setEnabled(false);

        lblOrden1.setText("Cliente:");

        lblOrden2.setText("DNI:");

        lblOrden3.setText("Vehículo:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblNumero)
                            .addComponent(lblFecha)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(lblTipo)
                                .addGap(2, 2, 2))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblOrden1)
                                    .addComponent(lblOrden)
                                    .addComponent(lblOrden2)
                                    .addComponent(lblOrden3))
                                .addGap(18, 18, 18)))))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(cboOrden, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addGap(10, 10, 10)
                            .addComponent(rbDesdeOrden)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(rbVentaDirecta))
                        .addComponent(txtCliente, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtDni, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtVehiculo, javax.swing.GroupLayout.Alignment.LEADING)))
                .addContainerGap(176, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNumero)
                    .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFecha)
                    .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbVentaDirecta)
                    .addComponent(rbDesdeOrden)
                    .addComponent(lblTipo))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblOrden)
                    .addComponent(cboOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblOrden1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblOrden2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtVehiculo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblOrden3))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(255, 204, 255));

        lblTipoItem.setText("Tipo:");

        cboTipoItem.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboTipoItem.addActionListener(this::cboTipoItemActionPerformed);

        lblRepuesto.setText(" Repuesto:");

        cboRepuesto.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboRepuesto.addActionListener(this::cboRepuestoActionPerformed);

        lblDescripcionItem.setText("Descripción:");

        lblCantidad.setText("Cantidad:");

        lblPrecioItem.setText("Precio:");

        btnAgregarItem.setText("+ Agregar");
        btnAgregarItem.addActionListener(this::btnAgregarItemActionPerformed);

        btnQuitarItem.setText("- Quitar");
        btnQuitarItem.addActionListener(this::btnQuitarItemActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(63, 63, 63)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lblDescripcionItem)
                        .addGap(18, 18, 18)
                        .addComponent(txtDescripcionItem, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(lblTipoItem)
                                .addGap(52, 52, 52)
                                .addComponent(cboTipoItem, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(39, 39, 39)
                                .addComponent(lblRepuesto))
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(btnAgregarItem)
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addComponent(lblCantidad)
                                    .addGap(22, 22, 22)
                                    .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(lblPrecioItem)
                                        .addGap(28, 28, 28)
                                        .addComponent(txtPrecioItem, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(cboRepuesto, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(36, 36, 36)
                                .addComponent(btnQuitarItem)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTipoItem)
                    .addComponent(cboTipoItem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboRepuesto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRepuesto))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDescripcionItem)
                    .addComponent(txtDescripcionItem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCantidad)
                    .addComponent(txtPrecioItem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPrecioItem)
                    .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(46, 46, 46)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAgregarItem)
                    .addComponent(btnQuitarItem))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblDetalle.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tblDetalle);

        jPanel3.setBackground(new java.awt.Color(204, 204, 255));

        lblSubtotalTitulo.setText("Subtotal:");

        lblSubtotal.setText("jLabel1");

        jLabel1.setText("TOTALES");

        lblIgvTitulo.setText("IGV (18%):");

        lblIgv.setText("jLabel2");

        lblTotalTitulo.setText("TOTAL:");

        lblTotal.setText("jLabel2");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("--------------------------------------------");

        lblMetodoPago.setText(" Método de pago:");

        cboMetodoPago.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnGuardar.setText("💾 Guardar");
        btnGuardar.addActionListener(this::btnGuardarActionPerformed);

        btnImprimir.setText("🖨️ Imprimir");
        btnImprimir.addActionListener(this::btnImprimirActionPerformed);

        btnCancelar.setText("❌ Cancelar");
        btnCancelar.addActionListener(this::btnCancelarActionPerformed);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel1))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(90, 90, 90)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblSubtotalTitulo)
                            .addComponent(lblIgvTitulo))
                        .addGap(78, 78, 78)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblIgv)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(lblSubtotal)
                                .addGap(198, 198, 198)
                                .addComponent(lblMetodoPago)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboMetodoPago, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(97, 97, 97)
                                .addComponent(lblTotalTitulo)
                                .addGap(86, 86, 86)
                                .addComponent(lblTotal))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(51, 51, 51)
                                .addComponent(jLabel2)))
                        .addGap(155, 155, 155)
                        .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnCancelar)))
                .addContainerGap(285, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jLabel1)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblSubtotalTitulo)
                            .addComponent(lblSubtotal)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblMetodoPago)
                            .addComponent(cboMetodoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblIgvTitulo)
                            .addComponent(lblIgv))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblTotalTitulo)
                            .addComponent(lblTotal)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnCancelar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(btnImprimir, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(137, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(430, 430, 430)
                        .addComponent(lblTitulo))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(113, 113, 113)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 763, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(326, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(lblTitulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(113, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void rbDesdeOrdenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbDesdeOrdenActionPerformed
       cboOrden.setEnabled(true);
    cboTipoItem.setEnabled(false);
    cboRepuesto.setEnabled(false);
    txtDescripcionItem.setEnabled(false);
    txtCantidad.setEnabled(false);
    txtPrecioItem.setEnabled(false);
    btnAgregarItem.setEnabled(false);
    btnQuitarItem.setEnabled(false);

    itemsActuales.clear();
    refrescarTablaDetalle();
    recalcularTotales();

    txtCliente.setText("");
    txtDni.setText("");
    txtVehiculo.setText("");
    }//GEN-LAST:event_rbDesdeOrdenActionPerformed

    private void txtNumeroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNumeroActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNumeroActionPerformed

    private void rbVentaDirectaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbVentaDirectaActionPerformed
       cboOrden.setEnabled(false);
    cboOrden.setSelectedIndex(0);

    cboTipoItem.setEnabled(true);
    cboRepuesto.setEnabled(true);
    txtDescripcionItem.setEnabled(true);
    txtCantidad.setEnabled(true);
    txtPrecioItem.setEnabled(false);  // readonly, se autocompleta
    btnAgregarItem.setEnabled(true);
    btnQuitarItem.setEnabled(true);

    txtCliente.setText("");
    txtDni.setText("");
    txtVehiculo.setText("");

    itemsActuales.clear();
    refrescarTablaDetalle();
    recalcularTotales();

    // Pedir selección de cliente (venta directa lo requiere)
    seleccionarClienteVentaDirecta();
    }//GEN-LAST:event_rbVentaDirectaActionPerformed

    private void cboOrdenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboOrdenActionPerformed
          int idx = cboOrden.getSelectedIndex();
    if (idx <= 0) {
        txtCliente.setText("");
        txtDni.setText("");
        txtVehiculo.setText("");
        itemsActuales.clear();
        refrescarTablaDetalle();
        recalcularTotales();
        return;
    }

    entidad.OrdenTrabajo o = ordenesCombo.get(idx);

    // Cargar datos del cliente
    entidad.Cliente c = clienteDAO.buscarPorId(o.getClienteId());
    if (c != null) {
        txtCliente.setText(c.getNombre());
        txtDni.setText(c.getDni());
    }

    // Vehículo
    entidad.Vehiculo v = vehiculoDAO.buscarPorId(o.getVehiculoId());
    if (v != null) {
        txtVehiculo.setText(v.getPlaca() + " - " + v.getMarca() + " " + v.getModelo());
    }

    // Cargar los ítems de la orden como detalle de la boleta
    itemsActuales.clear();
    for (entidad.DetalleOrden d : detalleOrdenDAO.listarPorOrden(o.getId())) {
        entidad.DetalleBoleta db = new entidad.DetalleBoleta();
        db.setTipoItem(d.getTipoItem());
        db.setRepuestoId(d.getRepuestoId());
        db.setDescripcion(d.getDescripcion());
        db.setCantidad(d.getCantidad());
        db.setPrecioUnitario(d.getPrecioUnitario());
        db.setSubtotal(d.getSubtotal());
        itemsActuales.add(db);
    }
    refrescarTablaDetalle();
    recalcularTotales();
    }//GEN-LAST:event_cboOrdenActionPerformed

    private void cboTipoItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTipoItemActionPerformed
      String tipo = (String) cboTipoItem.getSelectedItem();
    if ("REPUESTO".equals(tipo)) {
        cboRepuesto.setEnabled(true);
        txtPrecioItem.setEditable(false);
        txtPrecioItem.setBackground(new java.awt.Color(230, 230, 230));
    } else {
        cboRepuesto.setEnabled(false);
        cboRepuesto.setSelectedIndex(-1);
        txtDescripcionItem.setText("");
        txtPrecioItem.setText("");
        txtPrecioItem.setEditable(true);
        txtPrecioItem.setBackground(java.awt.Color.WHITE);
    }
    }//GEN-LAST:event_cboTipoItemActionPerformed

    private void cboRepuestoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboRepuestoActionPerformed
        int idx = cboRepuesto.getSelectedIndex();
    if (idx < 0) return;

    entidad.Repuesto r = repuestosCombo.get(idx);
    txtDescripcionItem.setText(r.getNombre());
    txtPrecioItem.setText(String.format(java.util.Locale.US, "%.2f", r.getPrecioVenta()));

    if (txtCantidad.getText().trim().isEmpty()) {
        txtCantidad.setText("1");
    }
    txtCantidad.requestFocus();
    txtCantidad.selectAll();
    }//GEN-LAST:event_cboRepuestoActionPerformed

    private void btnAgregarItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarItemActionPerformed
        String tipo = (String) cboTipoItem.getSelectedItem();
    String descripcion = txtDescripcionItem.getText().trim();
    String cantStr = txtCantidad.getText().trim();

    if (descripcion.isEmpty()) {
        javax.swing.JOptionPane.showMessageDialog(this, "Ingrese una descripción.");
        return;
    }
    if (cantStr.isEmpty()) {
        javax.swing.JOptionPane.showMessageDialog(this, "Ingrese la cantidad.");
        return;
    }

    int cantidad;
    try {
        cantidad = Integer.parseInt(cantStr);
        if (cantidad <= 0) throw new NumberFormatException();
    } catch (NumberFormatException e) {
        javax.swing.JOptionPane.showMessageDialog(this, "La cantidad debe ser un número mayor a 0.");
        return;
    }

    // ===== VALIDAR STOCK ANTES DE AGREGAR (E02) =====
    if ("REPUESTO".equals(tipo)) {
        int idxStock = cboRepuesto.getSelectedIndex();
        if (idxStock < 0) {
            javax.swing.JOptionPane.showMessageDialog(this, "Seleccione un repuesto.");
            return;
        }
        entidad.Repuesto rep = repuestosCombo.get(idxStock);

        // Sumar cantidades ya agregadas del mismo repuesto
        int cantidadEnBoleta = 0;
        for (entidad.DetalleBoleta d : itemsActuales) {
            if (d.getRepuestoId() == rep.getId()) {
                cantidadEnBoleta += d.getCantidad();
            }
        }

        if ((cantidad + cantidadEnBoleta) > rep.getStock()) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Stock insuficiente.\n" +
                "Disponible: " + rep.getStock() + "\n" +
                "Ya en la boleta: " + cantidadEnBoleta + "\n" +
                "Solicitado: " + cantidad,
                "Sin stock", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
    }

    double precio;
    if ("REPUESTO".equals(tipo)) {
        int idx = cboRepuesto.getSelectedIndex();
        if (idx < 0) {
            javax.swing.JOptionPane.showMessageDialog(this, "Seleccione un repuesto.");
            return;
        }
        precio = repuestosCombo.get(idx).getPrecioVenta();
    } else {
        String precStr = txtPrecioItem.getText().trim().replace(",", ".");
        if (precStr.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Ingrese el precio.");
            return;
        }
        try {
            precio = Double.parseDouble(precStr);
            if (precio < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "El precio debe ser un número válido.");
            return;
        }
    }

    entidad.DetalleBoleta d = new entidad.DetalleBoleta();
    d.setTipoItem(tipo);
    d.setDescripcion(descripcion);
    d.setCantidad(cantidad);
    d.setPrecioUnitario(precio);
    d.calcularSubtotal();

    if ("REPUESTO".equals(tipo)) {
        int idx = cboRepuesto.getSelectedIndex();
        d.setRepuestoId(repuestosCombo.get(idx).getId());
    }

    itemsActuales.add(d);
    refrescarTablaDetalle();
    recalcularTotales();

    // Limpiar
    txtDescripcionItem.setText("");
    txtCantidad.setText("");
    txtPrecioItem.setText("");
    if ("REPUESTO".equals(tipo)) cboRepuesto.setSelectedIndex(-1);
    txtDescripcionItem.requestFocus();
    }//GEN-LAST:event_btnAgregarItemActionPerformed

    private void btnQuitarItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuitarItemActionPerformed
        int fila = tblDetalle.getSelectedRow();
    if (fila == -1) {
        javax.swing.JOptionPane.showMessageDialog(this, "Seleccione un ítem para quitar.");
        return;
    }

    int r = javax.swing.JOptionPane.showConfirmDialog(this,
        "¿Quitar este ítem?", "Confirmar", javax.swing.JOptionPane.YES_NO_OPTION);
    if (r == javax.swing.JOptionPane.YES_OPTION) {
        itemsActuales.remove(fila);
        refrescarTablaDetalle();
        recalcularTotales();
    }
    }//GEN-LAST:event_btnQuitarItemActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
      // ===== VALIDACIONES PREVIAS =====
    if (itemsActuales.isEmpty()) {
        javax.swing.JOptionPane.showMessageDialog(this, "Agregue al menos un ítem a la boleta.");
        return;
    }

    int clienteId = 0;
    int vehiculoId = 0;
    int ordenId = 0;
    String tipoBoleta;

    if (rbDesdeOrden.isSelected()) {
        int idx = cboOrden.getSelectedIndex();
        if (idx <= 0) {
            javax.swing.JOptionPane.showMessageDialog(this, "Seleccione una orden.");
            return;
        }
        entidad.OrdenTrabajo o = ordenesCombo.get(idx);
        clienteId = o.getClienteId();
        vehiculoId = o.getVehiculoId();
        ordenId = o.getId();
        tipoBoleta = "SERVICIO";
    } else {
        if (txtCliente.getText().trim().isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Seleccione un cliente.");
            return;
        }
        clienteId = obtenerClienteSeleccionadoId();
        if (clienteId <= 0) {
            javax.swing.JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente.");
            return;
        }
        tipoBoleta = "VENTA";
    }

    // ===== CALCULAR TOTALES =====
    double total = 0;
    for (entidad.DetalleBoleta d : itemsActuales) total += d.getSubtotal();
    double base = total / (1 + porcentajeIgv / 100.0);
    double igv  = total - base;

    // ===== TRANSACCIÓN =====
    java.sql.Connection cn = null;
    try {
        cn = config.conexion.getConexion();
        cn.setAutoCommit(false);

        // 1. Obtener correlativo con bloqueo
        int correlativo = boletaDAO.obtenerSiguienteCorrelativoConBloqueo(cn);
        String numero = String.format("B001-%06d", correlativo);

        // 2. Crear boleta
        entidad.Boleta b = new entidad.Boleta();
        b.setNumero(numero);
        b.setSerie("B001");
        b.setCorrelativo(correlativo);
        b.setClienteId(clienteId);
        b.setVehiculoId(vehiculoId);
        b.setOrdenId(ordenId);
        b.setTipo(tipoBoleta);
        b.setSubtotal(base);
        b.setIgv(igv);
        b.setTotal(total);
        b.setMetodoPago((String) cboMetodoPago.getSelectedItem());
        b.setUsuarioId(config.SesionUsuario.getId());
        b.setObservacion("");
        b.setActivo(1);

        // 3. Insertar cabecera
        int idBoleta = boletaDAO.insertar(cn, b);
        if (idBoleta <= 0) {
            throw new java.sql.SQLException("No se pudo insertar la cabecera de la boleta.");
        }

        // 4. Insertar detalle + descontar stock
        for (entidad.DetalleBoleta d : itemsActuales) {
            d.setBoletaId(idBoleta);
            detalleBoletaDAO.insertar(cn, d);

            if (d.getRepuestoId() > 0) {
                boolean ok = repuestoDAO.descontarStock(cn, d.getRepuestoId(), d.getCantidad());
                if (!ok) {
                    throw new java.sql.SQLException(
                        "Stock insuficiente para: " + d.getDescripcion() +
                        " (necesita " + d.getCantidad() + ")"
                    );
                }
            }
        }

        // 5. Marcar la orden como ENTREGADO
        if (ordenId > 0) {
            ordenDAO.marcarEntregado(cn, ordenId);
        }

        // 6. COMMIT
        cn.commit();

        javax.swing.JOptionPane.showMessageDialog(this,
            "Boleta emitida: " + numero + "\nTOTAL: S/ " + String.format("%.2f", total));

        dispose();

    } catch (java.sql.SQLException e) {
        if (cn != null) {
            try { cn.rollback(); } catch (java.sql.SQLException ignored) { }
        }
        javax.swing.JOptionPane.showMessageDialog(this,
            "No se emitió la boleta:\n" + e.getMessage(),
            "Error", javax.swing.JOptionPane.ERROR_MESSAGE);

    } finally {
        if (cn != null) {
            try { cn.setAutoCommit(true); cn.close(); }
            catch (java.sql.SQLException ignored) { }
        }
    }
    }//GEN-LAST:event_btnGuardarActionPerformed
private void abrirArchivo(java.io.File archivo) {
    try {
        java.awt.Desktop.getDesktop().open(archivo);
    } catch (Exception e) {
        javax.swing.JOptionPane.showMessageDialog(this,
            "No se pudo abrir el archivo: " + e.getMessage());
    }
}
    private void btnImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirActionPerformed
         // Si la boleta ya está guardada en BD, se puede imprimir
    if (boletaActual == null || boletaActual.getId() <= 0) {
        javax.swing.JOptionPane.showMessageDialog(this,
            "Primero debe guardar la boleta.");
        return;
    }

    // Elegir ubicación del archivo
    javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
    chooser.setDialogTitle("Guardar boleta como PDF");
    chooser.setSelectedFile(new java.io.File(boletaActual.getNumero() + ".pdf"));

    int r = chooser.showSaveDialog(this);
    if (r != javax.swing.JFileChooser.APPROVE_OPTION) return;

    java.io.File archivo = chooser.getSelectedFile();
    if (!archivo.getName().toLowerCase().endsWith(".pdf")) {
        archivo = new java.io.File(archivo.getAbsolutePath() + ".pdf");
    }

    // Generar PDF
    negocio.BoletaPDF generador = new negocio.BoletaPDF();
    if (generador.generar(boletaActual, archivo)) {
        int op = javax.swing.JOptionPane.showConfirmDialog(this,
            "PDF generado correctamente.\n¿Desea abrirlo ahora?",
            "PDF generado",
            javax.swing.JOptionPane.YES_NO_OPTION);
        if (op == javax.swing.JOptionPane.YES_OPTION) {
            abrirArchivo(archivo);
        }
    } else {
        javax.swing.JOptionPane.showMessageDialog(this,
            "No se pudo generar el PDF.");
    }
    }//GEN-LAST:event_btnImprimirActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
       int r = javax.swing.JOptionPane.showConfirmDialog(this,
        "¿Cancelar? Los cambios no se guardarán.",
        "Confirmar", javax.swing.JOptionPane.YES_NO_OPTION);
    if (r == javax.swing.JOptionPane.YES_OPTION) dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                BoletaFormDialog dialog = new BoletaFormDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregarItem;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnImprimir;
    private javax.swing.JButton btnQuitarItem;
    private javax.swing.JComboBox<String> cboMetodoPago;
    private javax.swing.JComboBox<String> cboOrden;
    private javax.swing.JComboBox<String> cboRepuesto;
    private javax.swing.JComboBox<String> cboTipoItem;
    private javax.swing.ButtonGroup grupoTipoBoleta;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCantidad;
    private javax.swing.JLabel lblDescripcionItem;
    private javax.swing.JLabel lblFecha;
    private javax.swing.JLabel lblIgv;
    private javax.swing.JLabel lblIgvTitulo;
    private javax.swing.JLabel lblMetodoPago;
    private javax.swing.JLabel lblNumero;
    private javax.swing.JLabel lblOrden;
    private javax.swing.JLabel lblOrden1;
    private javax.swing.JLabel lblOrden2;
    private javax.swing.JLabel lblOrden3;
    private javax.swing.JLabel lblPrecioItem;
    private javax.swing.JLabel lblRepuesto;
    private javax.swing.JLabel lblSubtotal;
    private javax.swing.JLabel lblSubtotalTitulo;
    private javax.swing.JLabel lblTipo;
    private javax.swing.JLabel lblTipoItem;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblTotalTitulo;
    private javax.swing.JRadioButton rbDesdeOrden;
    private javax.swing.JRadioButton rbVentaDirecta;
    private javax.swing.JTable tblDetalle;
    private javax.swing.JTextField txtCantidad;
    private javax.swing.JTextField txtCliente;
    private javax.swing.JTextField txtDescripcionItem;
    private javax.swing.JTextField txtDni;
    private javax.swing.JTextField txtFecha;
    private javax.swing.JTextField txtNumero;
    private javax.swing.JTextField txtPrecioItem;
    private javax.swing.JTextField txtVehiculo;
    // End of variables declaration//GEN-END:variables
}
