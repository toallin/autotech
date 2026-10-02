/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package presentacion.empleado;

/**
 *
 * @author PC
 */
public class ClientePanel extends javax.swing.JPanel {
private final negocio.ClienteDAO clienteDAO = new negocio.ClienteDAO();
private int idClienteActual = 0; 

    /**
     * Creates new form ClientePanel
     */
    public ClientePanel() {
        initComponents();
        cargarTabla();            // llena la tabla al abrir
    limpiarFormulario();      // deja el form en blanco
    
    }
 
private void cargarTabla() {
    java.util.List<entidad.Cliente> lista;

    if (chkMostrarInactivos.isSelected()) {
        lista = clienteDAO.listarTodos();
    } else {
        lista = clienteDAO.listar();
    }

    javax.swing.table.DefaultTableModel modelo = new javax.swing.table.DefaultTableModel(
        new Object[][]{},
        new String[]{"ID", "Nombre", "DNI", "Teléfono", "Email", "Estado", "Acciones"}
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 6;
        }
    };

    for (entidad.Cliente c : lista) {
        String estado = (c.getActivo() == 1) ? "Activo" : "Inactivo";
        modelo.addRow(new Object[]{
            c.getId(), c.getNombre(), c.getDni(),
            c.getTelefono(), c.getEmail(), estado, ""
        });
    }

    tblClientes.setModel(modelo);

    // Anchos de columnas
    tblClientes.getColumnModel().getColumn(0).setPreferredWidth(40);
    tblClientes.getColumnModel().getColumn(1).setPreferredWidth(150);
    tblClientes.getColumnModel().getColumn(2).setPreferredWidth(90);
    tblClientes.getColumnModel().getColumn(3).setPreferredWidth(90);
    tblClientes.getColumnModel().getColumn(4).setPreferredWidth(160);
    tblClientes.getColumnModel().getColumn(5).setPreferredWidth(70);
    tblClientes.getColumnModel().getColumn(6).setPreferredWidth(110);

    // Renderer y Editor SOLO en columna 6
    tblClientes.getColumnModel().getColumn(6).setCellRenderer(new BotonesRenderer());
    tblClientes.getColumnModel().getColumn(6).setCellEditor(new BotonesEditor());

    tblClientes.setRowHeight(30);
}
private void cargarClienteEnFormulario(entidad.Cliente c) {
    txtId.setText(String.valueOf(c.getId()));
    txtNombre.setText(c.getNombre());
    txtDni.setText(c.getDni());
    txtTelefono.setText(c.getTelefono());
    txtEmail.setText(c.getEmail());
    txtDireccion.setText(c.getDireccion());

    if (c.getActivo() == 1) {
        rbActivo.setSelected(true);
    } else {
        rbInactivo.setSelected(true);
    }

    idClienteActual = c.getId();
}
private void buscarClientes() {
    String texto = txtBuscar.getText().trim();
    java.util.List<entidad.Cliente> lista;

    if (texto.isEmpty()) {
        if (chkMostrarInactivos.isSelected()) {
            lista = clienteDAO.listarTodos();
        } else {
            lista = clienteDAO.listar();
        }
    } else {
        if (chkMostrarInactivos.isSelected()) {
            lista = clienteDAO.buscarTodos(texto);
        } else {
            lista = clienteDAO.buscar(texto);
        }
    }

    javax.swing.table.DefaultTableModel modelo = new javax.swing.table.DefaultTableModel(
        new Object[][]{},
        new String[]{"ID", "Nombre", "DNI", "Teléfono", "Email", "Estado", "Acciones"}
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 6;
        }
    };

    for (entidad.Cliente c : lista) {
        String estado = (c.getActivo() == 1) ? "Activo" : "Inactivo";
        modelo.addRow(new Object[]{
            c.getId(), c.getNombre(), c.getDni(),
            c.getTelefono(), c.getEmail(), estado, ""
        });
    }

    tblClientes.setModel(modelo);

    tblClientes.getColumnModel().getColumn(0).setPreferredWidth(40);
    tblClientes.getColumnModel().getColumn(1).setPreferredWidth(150);
    tblClientes.getColumnModel().getColumn(2).setPreferredWidth(90);
    tblClientes.getColumnModel().getColumn(3).setPreferredWidth(90);
    tblClientes.getColumnModel().getColumn(4).setPreferredWidth(160);
    tblClientes.getColumnModel().getColumn(5).setPreferredWidth(70);
    tblClientes.getColumnModel().getColumn(6).setPreferredWidth(110);

    tblClientes.getColumnModel().getColumn(6).setCellRenderer(new BotonesRenderer());
    tblClientes.getColumnModel().getColumn(6).setCellEditor(new BotonesEditor());

    tblClientes.setRowHeight(30);
}
private void limpiarFormulario() {
    txtId.setText("");
    txtNombre.setText("");
    txtDni.setText("");
    txtTelefono.setText("");
    txtEmail.setText("");
    txtDireccion.setText("");
    rbActivo.setSelected(true);
    idClienteActual = 0;
    txtNombre.requestFocus();
}
    private void accionEditar(int fila) {
        int id = Integer.parseInt(tblClientes.getValueAt(fila, 0).toString());
        entidad.Cliente c = clienteDAO.buscarPorId(id);
        if (c != null) {
            cargarClienteEnFormulario(c);
        }
    }

    private void accionEstado(int fila) {
        int id = Integer.parseInt(tblClientes.getValueAt(fila, 0).toString());
        String estado = tblClientes.getValueAt(fila, 5).toString();
        String nombre = tblClientes.getValueAt(fila, 1).toString();

        if ("Activo".equals(estado)) {
            int r = javax.swing.JOptionPane.showConfirmDialog(this,
                "¿Desactivar al cliente \"" + nombre + "\"?",
                "Confirmar desactivación",
                javax.swing.JOptionPane.YES_NO_OPTION,
                javax.swing.JOptionPane.QUESTION_MESSAGE);

            if (r == javax.swing.JOptionPane.YES_OPTION) {
                if (clienteDAO.desactivar(id)) {
                    javax.swing.JOptionPane.showMessageDialog(this,
                        "Cliente desactivado.",
                        "Éxito", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                    cargarTabla();
                    limpiarFormulario();
                }
            }
        } else {
            int r = javax.swing.JOptionPane.showConfirmDialog(this,
                "¿Reactivar al cliente \"" + nombre + "\"?",
                "Confirmar reactivación",
                javax.swing.JOptionPane.YES_NO_OPTION,
                javax.swing.JOptionPane.QUESTION_MESSAGE);

            if (r == javax.swing.JOptionPane.YES_OPTION) {
                if (clienteDAO.reactivar(id)) {
                    javax.swing.JOptionPane.showMessageDialog(this,
                        "Cliente reactivado.",
                        "Éxito", javax.swing.JOptionPane.INFORMATION_MESSAGE);
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
        lblId = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        lblNombre = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        lblDni = new javax.swing.JLabel();
        txtDni = new javax.swing.JTextField();
        lblTelefono = new javax.swing.JLabel();
        txtTelefono = new javax.swing.JTextField();
        lblEmail = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        lblDireccion = new javax.swing.JLabel();
        txtDireccion = new javax.swing.JTextField();
        rbInactivo = new javax.swing.JRadioButton();
        rbActivo = new javax.swing.JRadioButton();
        btnGuardar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        lblBuscar = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        chkMostrarInactivos = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblClientes = new javax.swing.JTable();

        lblTitulo.setBackground(new java.awt.Color(255, 255, 255));
        lblTitulo.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(102, 102, 255));
        lblTitulo.setText("Gestion de Clientes");

        lblId.setText("id");

        lblNombre.setText("Nombre");

        txtId.setBackground(new java.awt.Color(255, 255, 204));
        txtId.setEnabled(false);

        lblDni.setText("DNI");

        lblTelefono.setText("Telefono");

        lblEmail.setText("Email");

        lblDireccion.setText("Direccion");

        grupoEstado.add(rbInactivo);
        rbInactivo.setText("Inactivo");

        grupoEstado.add(rbActivo);
        rbActivo.setText("Activo");

        btnGuardar.setText("💾 Guardar");
        btnGuardar.addActionListener(this::btnGuardarActionPerformed);

        btnLimpiar.setText("Limpiar");
        btnLimpiar.addActionListener(this::btnLimpiarActionPerformed);

        lblBuscar.setText("buscar");

        txtBuscar.addActionListener(this::txtBuscarActionPerformed);
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarKeyReleased(evt);
            }
        });

        chkMostrarInactivos.setText("Mostrar Inactivos");
        chkMostrarInactivos.addItemListener(this::chkMostrarInactivosItemStateChanged);

        tblClientes.setModel(new javax.swing.table.DefaultTableModel(
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
        tblClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblClientesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblClientes);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTelefono)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(lblNombre)
                                .addComponent(lblDni)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addGap(13, 13, 13)
                                    .addComponent(lblId)))
                            .addComponent(lblEmail)
                            .addComponent(lblDireccion))
                        .addGap(41, 41, 41)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtDni, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(87, 87, 87)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(btnLimpiar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnGuardar, javax.swing.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)))
                            .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 731, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(rbActivo)
                            .addComponent(lblTitulo))
                        .addGap(48, 48, 48)
                        .addComponent(rbInactivo)))
                .addGap(0, 443, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(361, 361, 361)
                        .addComponent(chkMostrarInactivos))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(160, 160, 160)
                        .addComponent(lblBuscar)
                        .addGap(67, 67, 67)
                        .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblTitulo)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblId)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblNombre)
                            .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblDni)
                                    .addComponent(txtDni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblTelefono))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblEmail))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblDireccion)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(13, 13, 13)
                                .addComponent(btnLimpiar)
                                .addGap(96, 96, 96)
                                .addComponent(chkMostrarInactivos))))
                    .addComponent(btnGuardar))
                .addGap(1, 1, 1)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbInactivo)
                    .addComponent(rbActivo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 61, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblBuscar))
                .addGap(198, 198, 198))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
      // ===== VALIDAR NOMBRE =====
    if (txtNombre.getText().trim().isEmpty()) {
        javax.swing.JOptionPane.showMessageDialog(this,
            "El nombre es obligatorio.",
            "Campo incompleto",
            javax.swing.JOptionPane.WARNING_MESSAGE);
        return;
    }

    // ===== VALIDAR DNI (8 dígitos numéricos) - E10 =====
    String dni = txtDni.getText().trim();
    if (dni.isEmpty()) {
        javax.swing.JOptionPane.showMessageDialog(this,
            "El DNI es obligatorio.",
            "Campo incompleto",
            javax.swing.JOptionPane.WARNING_MESSAGE);
        return;
    }
    if (!dni.matches("\\d{8}")) {
        javax.swing.JOptionPane.showMessageDialog(this,
            "El DNI debe tener exactamente 8 dígitos numéricos.",
            "DNI inválido",
            javax.swing.JOptionPane.WARNING_MESSAGE);
        txtDni.requestFocus();
        return;
    }

    // ===== VALIDAR TELÉFONO (opcional pero si se ingresa, debe ser válido) =====
    String telefono = txtTelefono.getText().trim();
    if (!telefono.isEmpty() && !telefono.matches("[0-9\\-\\+\\s]{6,20}")) {
        javax.swing.JOptionPane.showMessageDialog(this,
            "El teléfono solo puede contener números, guiones y espacios.",
            "Teléfono inválido",
            javax.swing.JOptionPane.WARNING_MESSAGE);
        txtTelefono.requestFocus();
        return;
    }

    // ===== VALIDAR EMAIL (opcional pero si se ingresa, debe ser válido) =====
    String email = txtEmail.getText().trim();
    if (!email.isEmpty() && !email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
        javax.swing.JOptionPane.showMessageDialog(this,
            "El email no tiene un formato válido.",
            "Email inválido",
            javax.swing.JOptionPane.WARNING_MESSAGE);
        txtEmail.requestFocus();
        return;
    }

    // ===== VERIFICAR DUPLICADO POR DNI =====
    entidad.Cliente existente = clienteDAO.buscarPorDni(dni);
    if (existente != null && existente.getId() != idClienteActual) {
        javax.swing.JOptionPane.showMessageDialog(this,
            "Ya existe un cliente con ese DNI.",
            "DNI duplicado",
            javax.swing.JOptionPane.ERROR_MESSAGE);
        return;
    }

    // ===== CONSTRUIR OBJETO =====
    entidad.Cliente c = new entidad.Cliente();
    c.setNombre(txtNombre.getText().trim());
    c.setDni(dni);
    c.setTelefono(telefono);
    c.setEmail(email);
    c.setDireccion(txtDireccion.getText().trim());
    c.setActivo(rbActivo.isSelected() ? 1 : 0);

    // ===== INSERTAR O ACTUALIZAR =====
    boolean ok;
    String mensaje;

    if (idClienteActual == 0) {
        ok = clienteDAO.insertar(c);
        mensaje = "Cliente registrado correctamente.";
    } else {
        c.setId(idClienteActual);
        ok = clienteDAO.actualizar(c);
        mensaje = "Cliente actualizado correctamente.";
    }

    if (ok) {
        javax.swing.JOptionPane.showMessageDialog(this, mensaje,
            "Éxito", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        cargarTabla();
        limpiarFormulario();
    } else {
        javax.swing.JOptionPane.showMessageDialog(this,
            "No se pudo guardar el cliente.",
            "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
         limpiarFormulario();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void txtBuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyReleased
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
        buscarClientes();
    }
    }//GEN-LAST:event_txtBuscarKeyReleased

    private void chkMostrarInactivosItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkMostrarInactivosItemStateChanged
        buscarClientes();
    }//GEN-LAST:event_chkMostrarInactivosItemStateChanged

    private void tblClientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblClientesMouseClicked
          int fila = tblClientes.getSelectedRow();
    if (fila == -1) return;

    int id = Integer.parseInt(tblClientes.getValueAt(fila, 0).toString());
    entidad.Cliente c = clienteDAO.buscarPorId(id);
    if (c != null) {
        cargarClienteEnFormulario(c);
    }
    }//GEN-LAST:event_tblClientesMouseClicked

    private void txtBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JCheckBox chkMostrarInactivos;
    private javax.swing.ButtonGroup grupoEstado;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblDireccion;
    private javax.swing.JLabel lblDni;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblId;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblTelefono;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JRadioButton rbActivo;
    private javax.swing.JRadioButton rbInactivo;
    private javax.swing.JTable tblClientes;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtDireccion;
    private javax.swing.JTextField txtDni;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
    // ============================================================
    // RENDERER: dibuja los 2 botones en la celda "Acciones"
    // ============================================================
    private class BotonesRenderer implements javax.swing.table.TableCellRenderer {

        private final javax.swing.JPanel panel;
        private final javax.swing.JButton btnEditar;
        private final javax.swing.JButton btnEstado;

        public BotonesRenderer() {
            panel = new javax.swing.JPanel(new java.awt.GridLayout(1, 2, 4, 0));
            panel.setOpaque(true);

            btnEditar = new javax.swing.JButton("✏️");
            btnEditar.setToolTipText("Editar");
            btnEditar.setFocusable(false);
            btnEditar.setMargin(new java.awt.Insets(2, 2, 2, 2));

            btnEstado = new javax.swing.JButton("🚫");
            btnEstado.setToolTipText("Desactivar / Reactivar");
            btnEstado.setFocusable(false);
            btnEstado.setMargin(new java.awt.Insets(2, 2, 2, 2));

            panel.add(btnEditar);
            panel.add(btnEstado);
        }

        @Override
        public java.awt.Component getTableCellRendererComponent(
                javax.swing.JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            String estado = table.getValueAt(row, 5).toString();
            if ("Activo".equals(estado)) {
                btnEstado.setText("🚫");
                btnEstado.setToolTipText("Desactivar");
            } else {
                btnEstado.setText("♻️");
                btnEstado.setToolTipText("Reactivar");
            }

            if (isSelected) {
                panel.setBackground(table.getSelectionBackground());
            } else {
                panel.setBackground(table.getBackground());
            }

            return panel;
        }
    }

    // ============================================================
    // EDITOR: captura los clics en los botones
    // ============================================================
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
            btnEditar.setMargin(new java.awt.Insets(2, 2, 2, 2));

            btnEstado = new javax.swing.JButton("🚫");
            btnEstado.setFocusable(false);
            btnEstado.setMargin(new java.awt.Insets(2, 2, 2, 2));

            btnEditar.addActionListener(e -> {
                fireEditingStopped();
                accionEditar(filaActual);
            });

            btnEstado.addActionListener(e -> {
                fireEditingStopped();
                accionEstado(filaActual);
            });

            panel.add(btnEditar);
            panel.add(btnEstado);
        }

        @Override
        public java.awt.Component getTableCellEditorComponent(
                javax.swing.JTable table, Object value, boolean isSelected,
                int row, int column) {

            this.filaActual = row;

            String estado = table.getValueAt(row, 5).toString();
            if ("Activo".equals(estado)) {
                btnEstado.setText("🚫");
            } else {
                btnEstado.setText("♻️");
            }

            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() { return ""; }

        @Override
        public boolean isCellEditable(java.util.EventObject e) { return true; }
    }

}   // ← ESTA LLAVE CIERRA ClientePanel

