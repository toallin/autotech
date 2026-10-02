/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package presentacion.admin;

/**
 *
 * @author PC
 */
public class UsuarioAdminPanel extends javax.swing.JPanel {
private final negocio.UsuarioDAO usuarioDAO = new negocio.UsuarioDAO();
private int idUsuarioActual = 0;
    /**
     * Creates new form UsuarioAdminPanel
     */
    public UsuarioAdminPanel() {
        initComponents();
         cargarRoles();
    cargarTabla();
    limpiarFormulario();
    }
private void cargarRoles() {
    cboRol.removeAllItems();
    cboRol.addItem("ADMIN");
    cboRol.addItem("EMPLEADO");
    cboRol.addItem("MECANICO");
}
private void cargarTabla() {
    java.util.List<entidad.Usuario> lista;
    if (chkMostrarInactivos.isSelected()) {
        lista = usuarioDAO.listarTodos();
    } else {
        lista = usuarioDAO.listar();
    }
    llenarTabla(lista);
}

private void llenarTabla(java.util.List<entidad.Usuario> lista) {
    javax.swing.table.DefaultTableModel modelo = new javax.swing.table.DefaultTableModel(
        new Object[][]{},
        new String[]{"ID", "Usuario", "Nombre completo", "Rol", "Teléfono", "Estado", "Acciones"}
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 6;
        }
    };

    for (entidad.Usuario u : lista) {
        String estado = (u.getActivo() == 1) ? "Activo" : "Inactivo";
        modelo.addRow(new Object[]{
            u.getId(),
            u.getUsuario(),
            u.getNombreCompleto(),
            u.getRol(),
            u.getTelefono() != null ? u.getTelefono() : "",
            estado,
            ""
        });
    }

    tblUsuarios.setModel(modelo);

    tblUsuarios.getColumnModel().getColumn(0).setPreferredWidth(40);
    tblUsuarios.getColumnModel().getColumn(1).setPreferredWidth(120);
    tblUsuarios.getColumnModel().getColumn(2).setPreferredWidth(200);
    tblUsuarios.getColumnModel().getColumn(3).setPreferredWidth(100);
    tblUsuarios.getColumnModel().getColumn(4).setPreferredWidth(100);
    tblUsuarios.getColumnModel().getColumn(5).setPreferredWidth(80);
    tblUsuarios.getColumnModel().getColumn(6).setPreferredWidth(110);

    tblUsuarios.getColumnModel().getColumn(6).setCellRenderer(new BotonesRenderer());
    tblUsuarios.getColumnModel().getColumn(6).setCellEditor(new BotonesEditor());

    tblUsuarios.setRowHeight(30);
}
private void buscarUsuarios() {
    String texto = txtBuscar.getText().trim();
    boolean incluirInactivos = chkMostrarInactivos.isSelected();
    java.util.List<entidad.Usuario> lista;

    if (texto.isEmpty()) {
        lista = incluirInactivos ? usuarioDAO.listarTodos() : usuarioDAO.listar();
    } else {
        lista = usuarioDAO.buscar(texto, incluirInactivos);
    }
    llenarTabla(lista);
}
private void limpiarFormulario() {
    txtId.setText("");
    txtUsuario.setText("");
    txtPassword.setText("");
    txtNombre.setText("");
    txtTelefono.setText("");
    if (cboRol.getItemCount() > 0) cboRol.setSelectedIndex(1);   // EMPLEADO por defecto
    rbActivo.setSelected(true);
    idUsuarioActual = 0;
    txtUsuario.requestFocus();
}
private void cargarUsuarioEnFormulario(entidad.Usuario u) {
    txtId.setText(String.valueOf(u.getId()));
    txtUsuario.setText(u.getUsuario());
    txtPassword.setText("");     // nunca mostrar la contraseña
    txtNombre.setText(u.getNombreCompleto());
    txtTelefono.setText(u.getTelefono() != null ? u.getTelefono() : "");
    cboRol.setSelectedItem(u.getRol());

    if (u.getActivo() == 1) rbActivo.setSelected(true);
    else rbInactivo.setSelected(true);

    idUsuarioActual = u.getId();
}
private void accionEditar(int fila) {
    int id = Integer.parseInt(tblUsuarios.getValueAt(fila, 0).toString());
    entidad.Usuario u = usuarioDAO.buscarPorId(id);
    if (u != null) cargarUsuarioEnFormulario(u);
}

private void accionEstado(int fila) {
    int id = Integer.parseInt(tblUsuarios.getValueAt(fila, 0).toString());
    String estado = tblUsuarios.getValueAt(fila, 5).toString();
    String usuario = tblUsuarios.getValueAt(fila, 1).toString();

    // No permitir desactivarse a sí mismo
    if (id == config.SesionUsuario.getId()) {
        javax.swing.JOptionPane.showMessageDialog(this,
            "No puede desactivar su propio usuario.");
        return;
    }

    if ("Activo".equals(estado)) {
        int r = javax.swing.JOptionPane.showConfirmDialog(this,
            "¿Desactivar al usuario \"" + usuario + "\"?",
            "Confirmar", javax.swing.JOptionPane.YES_NO_OPTION);
        if (r == javax.swing.JOptionPane.YES_OPTION) {
            if (usuarioDAO.desactivar(id)) {
                javax.swing.JOptionPane.showMessageDialog(this, "Usuario desactivado.");
                cargarTabla();
                limpiarFormulario();
            }
        }
    } else {
        int r = javax.swing.JOptionPane.showConfirmDialog(this,
            "¿Reactivar al usuario \"" + usuario + "\"?",
            "Confirmar", javax.swing.JOptionPane.YES_NO_OPTION);
        if (r == javax.swing.JOptionPane.YES_OPTION) {
            if (usuarioDAO.reactivar(id)) {
                javax.swing.JOptionPane.showMessageDialog(this, "Usuario reactivado.");
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
        btnGuardar = new javax.swing.JButton();
        lblTelefono = new javax.swing.JLabel();
        btnLimpiar = new javax.swing.JButton();
        txtTelefono = new javax.swing.JTextField();
        lblBuscar = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        chkMostrarInactivos = new javax.swing.JCheckBox();
        lblTitulo = new javax.swing.JLabel();
        lblId = new javax.swing.JLabel();
        txtUsuario = new javax.swing.JTextField();
        rbInactivo = new javax.swing.JRadioButton();
        lblNombre = new javax.swing.JLabel();
        rbActivo = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblUsuarios = new javax.swing.JTable();
        txtId = new javax.swing.JTextField();
        lblDni = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        lblTelefono1 = new javax.swing.JLabel();
        cboRol = new javax.swing.JComboBox<>();
        lblTelefono2 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        lblTelefono3 = new javax.swing.JLabel();

        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(this::btnGuardarActionPerformed);

        lblTelefono.setText("Nombre Completo :");

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

        lblTitulo.setBackground(new java.awt.Color(255, 255, 255));
        lblTitulo.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(102, 102, 255));
        lblTitulo.setText("Gestion de Usuarios");

        lblId.setText("id :");

        grupoEstado.add(rbInactivo);
        rbInactivo.setText("Inactivo");

        lblNombre.setText("Usuario :");

        grupoEstado.add(rbActivo);
        rbActivo.setText("Activo");

        tblUsuarios.setModel(new javax.swing.table.DefaultTableModel(
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
        tblUsuarios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblUsuariosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblUsuarios);

        txtId.setBackground(new java.awt.Color(255, 255, 204));
        txtId.setEnabled(false);

        lblDni.setText("Contraseña :");

        lblTelefono1.setText("Rol :");

        cboRol.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lblTelefono2.setText("Telefono :");

        lblTelefono3.setText("Estado :");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 731, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(lblTitulo)))
                .addGap(0, 374, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblDni)
                                    .addComponent(lblTelefono)
                                    .addComponent(lblNombre)
                                    .addComponent(lblId)
                                    .addComponent(lblTelefono1)
                                    .addComponent(lblTelefono2))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 853, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(cboRol, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(txtUsuario, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
                                        .addComponent(txtTelefono, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
                                        .addComponent(txtPassword, javax.swing.GroupLayout.Alignment.LEADING))))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(66, 66, 66)
                                .addComponent(lblTelefono3)
                                .addGap(34, 34, 34)
                                .addComponent(rbActivo)
                                .addGap(45, 45, 45)
                                .addComponent(rbInactivo)
                                .addGap(102, 102, 102)
                                .addComponent(chkMostrarInactivos))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(321, 321, 321)
                        .addComponent(btnGuardar)
                        .addGap(58, 58, 58)
                        .addComponent(btnLimpiar))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(201, 201, 201)
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
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblId))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNombre)
                    .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDni)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblTelefono)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTelefono1)
                    .addComponent(cboRol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTelefono2)
                    .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbInactivo)
                    .addComponent(rbActivo)
                    .addComponent(lblTelefono3)
                    .addComponent(chkMostrarInactivos))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardar)
                    .addComponent(btnLimpiar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblBuscar))
                .addContainerGap(186, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
      // Validaciones
    if (txtUsuario.getText().trim().isEmpty()) {
        javax.swing.JOptionPane.showMessageDialog(this, "El nombre de usuario es obligatorio.");
        return;
    }
    if (txtNombre.getText().trim().isEmpty()) {
        javax.swing.JOptionPane.showMessageDialog(this, "El nombre completo es obligatorio.");
        return;
    }

    String password = new String(txtPassword.getPassword());

    // Al crear, la contraseña es obligatoria
    if (idUsuarioActual == 0 && password.isEmpty()) {
        javax.swing.JOptionPane.showMessageDialog(this, "La contraseña es obligatoria al crear un usuario.");
        return;
    }

    // Verificar usuario duplicado
    entidad.Usuario existente = usuarioDAO.buscarPorUsuario(txtUsuario.getText().trim());
    if (existente != null && existente.getId() != idUsuarioActual) {
        javax.swing.JOptionPane.showMessageDialog(this, "Ya existe un usuario con ese nombre.");
        return;
    }

    // Construir objeto
    entidad.Usuario u = new entidad.Usuario();
    u.setUsuario(txtUsuario.getText().trim());
    u.setPassword(password);
    u.setNombreCompleto(txtNombre.getText().trim());
    u.setRol((String) cboRol.getSelectedItem());
    u.setTelefono(txtTelefono.getText().trim());
    u.setActivo(rbActivo.isSelected() ? 1 : 0);

    boolean ok;
    String msg;

    if (idUsuarioActual == 0) {
        ok = usuarioDAO.insertar(u);
        msg = "Usuario registrado.";
    } else {
        u.setId(idUsuarioActual);
        if (password.isEmpty()) {
            ok = usuarioDAO.actualizar(u);            // sin tocar password
            msg = "Usuario actualizado.";
        } else {
            ok = usuarioDAO.actualizarConPassword(u); // con password nuevo
            msg = "Usuario actualizado (contraseña cambiada).";
        }
    }

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

    private void txtBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarActionPerformed

    private void txtBuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyReleased
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
        buscarUsuarios();
    }
    }//GEN-LAST:event_txtBuscarKeyReleased

    private void chkMostrarInactivosItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkMostrarInactivosItemStateChanged
         buscarUsuarios();
    }//GEN-LAST:event_chkMostrarInactivosItemStateChanged

    private void tblUsuariosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblUsuariosMouseClicked
       
        
    }//GEN-LAST:event_tblUsuariosMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JComboBox<String> cboRol;
    private javax.swing.JCheckBox chkMostrarInactivos;
    private javax.swing.ButtonGroup grupoEstado;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblDni;
    private javax.swing.JLabel lblId;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblTelefono;
    private javax.swing.JLabel lblTelefono1;
    private javax.swing.JLabel lblTelefono2;
    private javax.swing.JLabel lblTelefono3;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JRadioButton rbActivo;
    private javax.swing.JRadioButton rbInactivo;
    private javax.swing.JTable tblUsuarios;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtTelefono;
    private javax.swing.JTextField txtUsuario;
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
            String estado = table.getValueAt(row, 5).toString();
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
            String estado = table.getValueAt(row, 5).toString();
            if ("Activo".equals(estado)) btnEstado.setText("🚫");
            else btnEstado.setText("♻️");
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() { return ""; }

        @Override
        public boolean isCellEditable(java.util.EventObject e) { return true; }
    }
}
