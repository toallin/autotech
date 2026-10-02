/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package presentacion.empleado;

/**
 *
 * @author PC
 */
public class VehiculoPanel extends javax.swing.JPanel {

    private final negocio.VehiculoDAO vehiculoDAO = new negocio.VehiculoDAO();
    private final negocio.ClienteDAO clienteDAO = new negocio.ClienteDAO();
    private final java.util.List<entidad.Cliente> clientesCombo = new java.util.ArrayList<>();
    private int idVehiculoActual = 0;

    /**
     * Creates new form VehiculoPanel
     */
    public VehiculoPanel() {
        initComponents();
        cargarComboClientes();
        cargarTabla();
        limpiarFormulario();
    }

    private void cargarComboClientes() {
        cboCliente.removeAllItems();
        clientesCombo.clear();

        java.util.List<entidad.Cliente> clientes = clienteDAO.listar();
        for (entidad.Cliente c : clientes) {
            clientesCombo.add(c);              // guarda el objeto real
            cboCliente.addItem(c.toString());  // muestra el texto
        }
    }
private void cargarTabla() {
    java.util.List<entidad.Vehiculo> lista;

    if (chkMostrarInactivos.isSelected()) {
        lista = vehiculoDAO.listarTodos();
    } else {
        lista = vehiculoDAO.listar();
    }

    javax.swing.table.DefaultTableModel modelo = new javax.swing.table.DefaultTableModel(
        new Object[][]{},
        new String[]{"ID", "Placa", "Marca", "Modelo", "Año", "Cliente", "Estado", "Acciones"}
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 7;
        }
    };

    for (entidad.Vehiculo v : lista) {
        String estado = (v.getActivo() == 1) ? "Activo" : "Inactivo";
        modelo.addRow(new Object[]{
            v.getId(), v.getPlaca(), v.getMarca(), v.getModelo(),
            v.getAnio(), v.getNombreCliente(), estado, ""
        });
    }

    tblVehiculos.setModel(modelo);

    tblVehiculos.getColumnModel().getColumn(0).setPreferredWidth(40);
    tblVehiculos.getColumnModel().getColumn(1).setPreferredWidth(80);
    tblVehiculos.getColumnModel().getColumn(2).setPreferredWidth(100);
    tblVehiculos.getColumnModel().getColumn(3).setPreferredWidth(100);
    tblVehiculos.getColumnModel().getColumn(4).setPreferredWidth(60);
    tblVehiculos.getColumnModel().getColumn(5).setPreferredWidth(150);
    tblVehiculos.getColumnModel().getColumn(6).setPreferredWidth(70);
    tblVehiculos.getColumnModel().getColumn(7).setPreferredWidth(110);

    tblVehiculos.getColumnModel().getColumn(7).setCellRenderer(new BotonesRenderer());
    tblVehiculos.getColumnModel().getColumn(7).setCellEditor(new BotonesEditor());

    tblVehiculos.setRowHeight(30);
}
private void buscarVehiculos() {
    String texto = txtBuscar.getText().trim();
    java.util.List<entidad.Vehiculo> lista;

    if (texto.isEmpty()) {
        if (chkMostrarInactivos.isSelected()) lista = vehiculoDAO.listarTodos();
        else lista = vehiculoDAO.listar();
    } else {
        if (chkMostrarInactivos.isSelected()) lista = vehiculoDAO.buscarTodos(texto);
        else lista = vehiculoDAO.buscar(texto);
    }

    javax.swing.table.DefaultTableModel modelo = new javax.swing.table.DefaultTableModel(
        new Object[][]{},
        new String[]{"ID", "Placa", "Marca", "Modelo", "Año", "Cliente", "Estado", "Acciones"}
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 7;
        }
    };

    for (entidad.Vehiculo v : lista) {
        String estado = (v.getActivo() == 1) ? "Activo" : "Inactivo";
        modelo.addRow(new Object[]{
            v.getId(), v.getPlaca(), v.getMarca(), v.getModelo(),
            v.getAnio(), v.getNombreCliente(), estado, ""
        });
    }

    tblVehiculos.setModel(modelo);

    tblVehiculos.getColumnModel().getColumn(0).setPreferredWidth(40);
    tblVehiculos.getColumnModel().getColumn(1).setPreferredWidth(80);
    tblVehiculos.getColumnModel().getColumn(2).setPreferredWidth(100);
    tblVehiculos.getColumnModel().getColumn(3).setPreferredWidth(100);
    tblVehiculos.getColumnModel().getColumn(4).setPreferredWidth(60);
    tblVehiculos.getColumnModel().getColumn(5).setPreferredWidth(150);
    tblVehiculos.getColumnModel().getColumn(6).setPreferredWidth(70);
    tblVehiculos.getColumnModel().getColumn(7).setPreferredWidth(110);

    tblVehiculos.getColumnModel().getColumn(7).setCellRenderer(new BotonesRenderer());
    tblVehiculos.getColumnModel().getColumn(7).setCellEditor(new BotonesEditor());

    tblVehiculos.setRowHeight(30);
}
private void limpiarFormulario() {
    txtId.setText("");
    if (cboCliente.getItemCount() > 0) cboCliente.setSelectedIndex(0);
    txtPlaca.setText("");
    txtMarca.setText("");
    txtModelo.setText("");
    txtAnio.setText("");
    txtColor.setText("");
    txtVin.setText("");
    rbActivo.setSelected(true);
    idVehiculoActual = 0;
    txtPlaca.requestFocus();
}
private void cargarVehiculoEnFormulario(entidad.Vehiculo v) {
    txtId.setText(String.valueOf(v.getId()));
    txtPlaca.setText(v.getPlaca());
    txtMarca.setText(v.getMarca());
    txtModelo.setText(v.getModelo());
    txtAnio.setText(String.valueOf(v.getAnio()));
    txtColor.setText(v.getColor());
    txtVin.setText(v.getVin());

    // Seleccionar cliente en el combo (usa la lista paralela)
    for (int i = 0; i < clientesCombo.size(); i++) {
        if (clientesCombo.get(i).getId() == v.getClienteId()) {
            cboCliente.setSelectedIndex(i);
            break;
        }
    }

    if (v.getActivo() == 1) rbActivo.setSelected(true);
    else rbInactivo.setSelected(true);

    idVehiculoActual = v.getId();
}
private void accionEditar(int fila) {
    int id = Integer.parseInt(tblVehiculos.getValueAt(fila, 0).toString());
    entidad.Vehiculo v = vehiculoDAO.buscarPorId(id);
    if (v != null) cargarVehiculoEnFormulario(v);
}

private void accionEstado(int fila) {
    int id = Integer.parseInt(tblVehiculos.getValueAt(fila, 0).toString());
    String estado = tblVehiculos.getValueAt(fila, 6).toString();
    String placa = tblVehiculos.getValueAt(fila, 1).toString();

    if ("Activo".equals(estado)) {
        int r = javax.swing.JOptionPane.showConfirmDialog(this,
            "¿Desactivar el vehículo \"" + placa + "\"?",
            "Confirmar", javax.swing.JOptionPane.YES_NO_OPTION);
        if (r == javax.swing.JOptionPane.YES_OPTION) {
            if (vehiculoDAO.desactivar(id)) {
                javax.swing.JOptionPane.showMessageDialog(this, "Vehículo desactivado.");
                cargarTabla();
                limpiarFormulario();
            }
        }
    } else {
        int r = javax.swing.JOptionPane.showConfirmDialog(this,
            "¿Reactivar el vehículo \"" + placa + "\"?",
            "Confirmar", javax.swing.JOptionPane.YES_NO_OPTION);
        if (r == javax.swing.JOptionPane.YES_OPTION) {
            if (vehiculoDAO.reactivar(id)) {
                javax.swing.JOptionPane.showMessageDialog(this, "Vehículo reactivado.");
                cargarTabla();
                limpiarFormulario();
            }
        }
    }
}
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        grupoEstado = new javax.swing.ButtonGroup();
        lblTitulo = new javax.swing.JLabel();
        txtPlaca = new javax.swing.JTextField();
        btnGuardar = new javax.swing.JButton();
        lblMarca = new javax.swing.JLabel();
        btnLimpiar = new javax.swing.JButton();
        txtMarca = new javax.swing.JTextField();
        lblBuscar = new javax.swing.JLabel();
        lblModelo = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        txtModelo = new javax.swing.JTextField();
        chkMostrarInactivos = new javax.swing.JCheckBox();
        lblAnio = new javax.swing.JLabel();
        lblId = new javax.swing.JLabel();
        txtAnio = new javax.swing.JTextField();
        rbInactivo = new javax.swing.JRadioButton();
        lblCliente = new javax.swing.JLabel();
        rbActivo = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblVehiculos = new javax.swing.JTable();
        txtId = new javax.swing.JTextField();
        lblPlaca = new javax.swing.JLabel();
        cboCliente = new javax.swing.JComboBox<>();
        lblColor = new javax.swing.JLabel();
        txtColor = new javax.swing.JTextField();
        lblVin = new javax.swing.JLabel();
        txtVin = new javax.swing.JTextField();
        btnRefrescar = new javax.swing.JButton();

        lblTitulo.setBackground(new java.awt.Color(255, 255, 255));
        lblTitulo.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(102, 102, 255));
        lblTitulo.setText("Gestion de vehiculos");

        btnGuardar.setText("💾 Guardar");
        btnGuardar.addActionListener(this::btnGuardarActionPerformed);

        lblMarca.setText("Marca:");

        btnLimpiar.setText("Limpiar");
        btnLimpiar.addActionListener(this::btnLimpiarActionPerformed);

        lblBuscar.setText("buscar");

        lblModelo.setText("Modelo:");

        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarKeyReleased(evt);
            }
        });

        chkMostrarInactivos.setText("Mostrar Inactivos");
        chkMostrarInactivos.addItemListener(this::chkMostrarInactivosItemStateChanged);

        lblAnio.setText("Año:");

        lblId.setText("id");

        grupoEstado.add(rbInactivo);
        rbInactivo.setText("Inactivo");

        lblCliente.setText("cliente");

        grupoEstado.add(rbActivo);
        rbActivo.setText("Activo");

        tblVehiculos.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblVehiculos);

        txtId.setBackground(new java.awt.Color(255, 255, 204));
        txtId.setEnabled(false);

        lblPlaca.setText("Placa:");

        cboCliente.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lblColor.setText("Color:");

        lblVin.setText("VIN:");

        btnRefrescar.setText("🔄 Actualizar");
        btnRefrescar.addActionListener(this::btnRefrescarActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(lblTitulo))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(57, 57, 57)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblModelo)
                            .addComponent(lblPlaca)
                            .addComponent(lblCliente)
                            .addComponent(lblId)
                            .addComponent(lblMarca))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtModelo, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtMarca, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtPlaca, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(96, 96, 96)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblAnio)
                                    .addComponent(lblColor)
                                    .addComponent(lblVin))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtAnio, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtColor, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(32, 32, 32)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(btnLimpiar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(btnGuardar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtVin, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(32, 32, 32)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(chkMostrarInactivos)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(btnRefrescar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGap(27, 27, 27))))))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(92, 92, 92)
                        .addComponent(rbActivo)
                        .addGap(39, 39, 39)
                        .addComponent(rbInactivo))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 796, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(237, 237, 237)
                        .addComponent(lblBuscar)
                        .addGap(81, 81, 81)
                        .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(83, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(lblTitulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblId)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCliente)
                    .addComponent(cboCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblPlaca)
                            .addComponent(txtPlaca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(15, 15, 15)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblMarca)
                            .addComponent(txtMarca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblModelo)
                            .addComponent(txtModelo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rbInactivo)
                            .addComponent(rbActivo)
                            .addComponent(chkMostrarInactivos)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblAnio)
                            .addComponent(txtAnio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnGuardar))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblColor, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnLimpiar))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblVin)
                            .addComponent(txtVin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnRefrescar))))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBuscar)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(31, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
         // 1) Validar placa
    if (txtPlaca.getText().trim().isEmpty()) {
        javax.swing.JOptionPane.showMessageDialog(this, "La placa es obligatoria.");
        return;
    }

    // 2) Validar cliente seleccionado
    int index = cboCliente.getSelectedIndex();
    if (index < 0) {
        javax.swing.JOptionPane.showMessageDialog(this, "Seleccione un cliente.");
        return;
    }

    // 3) Validar año
    if (txtAnio.getText().trim().isEmpty()) {
        javax.swing.JOptionPane.showMessageDialog(this, "El año es obligatorio.");
        return;
    }

    int anio;
    try {
        anio = Integer.parseInt(txtAnio.getText().trim());
    } catch (NumberFormatException e) {
        javax.swing.JOptionPane.showMessageDialog(this, "El año debe ser un número.");
        return;
    }

    // 3.1) VALIDAR RANGO DEL AÑO - E10
    int anioActual = java.time.Year.now().getValue();
    int anioMaximo = anioActual + 1;
    if (anio < 1950 || anio > anioMaximo) {
        javax.swing.JOptionPane.showMessageDialog(this,
            "El año debe estar entre 1950 y " + anioMaximo + ".",
            "Año inválido",
            javax.swing.JOptionPane.WARNING_MESSAGE);
        txtAnio.requestFocus();
        return;
    }

    // 3.2) VALIDAR FORMATO DE PLACA - E10
    String placa = txtPlaca.getText().trim().toUpperCase();
    if (!placa.matches("[A-Z0-9\\-]{5,10}")) {
        javax.swing.JOptionPane.showMessageDialog(this,
            "La placa solo puede contener letras, números y guiones (5 a 10 caracteres).",
            "Placa inválida",
            javax.swing.JOptionPane.WARNING_MESSAGE);
        txtPlaca.requestFocus();
        return;
    }

    // 4) Verificar placa duplicada
    entidad.Vehiculo existente = vehiculoDAO.buscarPorPlaca(placa);
    if (existente != null && existente.getId() != idVehiculoActual) {
        javax.swing.JOptionPane.showMessageDialog(this, "Ya existe un vehículo con esa placa.");
        return;
    }

    // 5) Obtener el cliente real desde la lista paralela
    entidad.Cliente c = clientesCombo.get(index);

    // 6) Construir el vehículo
    entidad.Vehiculo v = new entidad.Vehiculo();
    v.setClienteId(c.getId());
    v.setPlaca(placa);
    v.setMarca(txtMarca.getText().trim());
    v.setModelo(txtModelo.getText().trim());
    v.setAnio(anio);
    v.setColor(txtColor.getText().trim());
    v.setVin(txtVin.getText().trim());
    v.setActivo(rbActivo.isSelected() ? 1 : 0);

    // 7) Insertar o actualizar
    boolean ok;
    String msg;
    if (idVehiculoActual == 0) {
        ok = vehiculoDAO.insertar(v);
        msg = "Vehículo registrado.";
    } else {
        v.setId(idVehiculoActual);
        ok = vehiculoDAO.actualizar(v);
        msg = "Vehículo actualizado.";
    }

    // 8) Mostrar resultado y refrescar
    if (ok) {
        javax.swing.JOptionPane.showMessageDialog(this, msg);
        cargarTabla();
        limpiarFormulario();
    } else {
        javax.swing.JOptionPane.showMessageDialog(this, "No se pudo guardar.");
    }

    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        limpiarFormulario();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void txtBuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyReleased
          if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
        buscarVehiculos();
    }
    }//GEN-LAST:event_txtBuscarKeyReleased

    private void chkMostrarInactivosItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkMostrarInactivosItemStateChanged
        buscarVehiculos();
    }//GEN-LAST:event_chkMostrarInactivosItemStateChanged

    private void btnRefrescarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefrescarActionPerformed
        cargarComboClientes();   // recarga clientes + vehículos + mecánicos + repuestos
    cargarTabla();    // recarga la lista de órdenes
    }//GEN-LAST:event_btnRefrescarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnRefrescar;
    private javax.swing.JComboBox<String> cboCliente;
    private javax.swing.JCheckBox chkMostrarInactivos;
    private javax.swing.ButtonGroup grupoEstado;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblAnio;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblCliente;
    private javax.swing.JLabel lblColor;
    private javax.swing.JLabel lblId;
    private javax.swing.JLabel lblMarca;
    private javax.swing.JLabel lblModelo;
    private javax.swing.JLabel lblPlaca;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblVin;
    private javax.swing.JRadioButton rbActivo;
    private javax.swing.JRadioButton rbInactivo;
    private javax.swing.JTable tblVehiculos;
    private javax.swing.JTextField txtAnio;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtColor;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtMarca;
    private javax.swing.JTextField txtModelo;
    private javax.swing.JTextField txtPlaca;
    private javax.swing.JTextField txtVin;
    // End of variables declaration//GEN-END:variables
    private class BotonesRenderer implements javax.swing.table.TableCellRenderer {
        private final javax.swing.JPanel panel;
        private final javax.swing.JButton btnEditar;
        private final javax.swing.JButton btnEstado;

        public BotonesRenderer() {
            panel = new javax.swing.JPanel(new java.awt.GridLayout(1, 2, 4, 0));
            panel.setOpaque(true);
            btnEditar = new javax.swing.JButton("✏️");
            btnEditar.setFocusable(false);
            btnEstado = new javax.swing.JButton("🚫");
            btnEstado.setFocusable(false);
            panel.add(btnEditar);
            panel.add(btnEstado);
        }

        @Override
        public java.awt.Component getTableCellRendererComponent(
                javax.swing.JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            String estado = table.getValueAt(row, 6).toString();
            if ("Activo".equals(estado)) btnEstado.setText("🚫");
            else btnEstado.setText("♻️");

            if (isSelected) panel.setBackground(table.getSelectionBackground());
            else panel.setBackground(table.getBackground());
            return panel;
        }
    }

    private class BotonesEditor extends javax.swing.AbstractCellEditor
            implements javax.swing.table.TableCellEditor {
        private final javax.swing.JPanel panel;
        private final javax.swing.JButton btnEditar;
        private final javax.swing.JButton btnEstado;
        private int filaActual;

        public BotonesEditor() {
            panel = new javax.swing.JPanel(new java.awt.GridLayout(1, 2, 4, 0));
            panel.setOpaque(true);
            btnEditar = new javax.swing.JButton("✏️");
            btnEditar.setFocusable(false);
            btnEstado = new javax.swing.JButton("🚫");
            btnEstado.setFocusable(false);
            btnEditar.addActionListener(e -> { fireEditingStopped(); accionEditar(filaActual); });
            btnEstado.addActionListener(e -> { fireEditingStopped(); accionEstado(filaActual); });
            panel.add(btnEditar);
            panel.add(btnEstado);
        }

        @Override
        public java.awt.Component getTableCellEditorComponent(
                javax.swing.JTable table, Object value, boolean isSelected,
                int row, int column) {
            this.filaActual = row;
            String estado = table.getValueAt(row, 6).toString();
            if ("Activo".equals(estado)) btnEstado.setText("🚫");
            else btnEstado.setText("♻️");
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() { return ""; }

        @Override
        public boolean isCellEditable(java.util.EventObject e) { return true; }
    }}
