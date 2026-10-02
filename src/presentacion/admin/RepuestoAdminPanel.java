/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package presentacion.admin;

/**
 *
 * @author PC
 */
public class RepuestoAdminPanel extends javax.swing.JPanel {
private final negocio.RepuestoDAO repuestoDAO = new negocio.RepuestoDAO();
private int idRepuestoActual = 0;
    /**
     * Creates new form RepuestoAdminPanel
     */
    public RepuestoAdminPanel() {
        initComponents();
         cargarTabla();
    limpiarFormulario();
    }
private void cargarTabla() {
    java.util.List<entidad.Repuesto> lista;
    if (chkMostrarInactivos.isSelected()) {
        lista = repuestoDAO.listarTodos();
    } else {
        lista = repuestoDAO.listar();
    }
    llenarTabla(lista);
}

private void llenarTabla(java.util.List<entidad.Repuesto> lista) {
    javax.swing.table.DefaultTableModel modelo = new javax.swing.table.DefaultTableModel(
        new Object[][]{},
        new String[]{"ID", "Código", "Nombre", "Stock", "Mín", "P. Venta", "Estado", "Acciones"}
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 7;
        }
    };

    for (entidad.Repuesto r : lista) {
        String estado = (r.getActivo() == 1) ? "Activo" : "Inactivo";
        modelo.addRow(new Object[]{
            r.getId(),
            r.getCodigo(),
            r.getNombre(),
            r.getStock(),
            r.getStockMinimo(),
            String.format(java.util.Locale.US, "S/ %.2f", r.getPrecioVenta()),
            estado,
            ""
        });
    }

    tblRepuestos.setModel(modelo);

    tblRepuestos.getColumnModel().getColumn(0).setPreferredWidth(40);
    tblRepuestos.getColumnModel().getColumn(1).setPreferredWidth(90);
    tblRepuestos.getColumnModel().getColumn(2).setPreferredWidth(180);
    tblRepuestos.getColumnModel().getColumn(3).setPreferredWidth(60);
    tblRepuestos.getColumnModel().getColumn(4).setPreferredWidth(60);
    tblRepuestos.getColumnModel().getColumn(5).setPreferredWidth(90);
    tblRepuestos.getColumnModel().getColumn(6).setPreferredWidth(80);
    tblRepuestos.getColumnModel().getColumn(7).setPreferredWidth(110);

    tblRepuestos.getColumnModel().getColumn(7).setCellRenderer(new BotonesRenderer());
    tblRepuestos.getColumnModel().getColumn(7).setCellEditor(new BotonesEditor());

    tblRepuestos.setRowHeight(30);
}
private void buscarRepuestos() {
    String texto = txtBuscar.getText().trim();
    java.util.List<entidad.Repuesto> lista;

    if (texto.isEmpty()) {
        lista = chkMostrarInactivos.isSelected() ? repuestoDAO.listarTodos() : repuestoDAO.listar();
    } else {
        lista = chkMostrarInactivos.isSelected() ? repuestoDAO.buscarTodos(texto) : repuestoDAO.buscar(texto);
    }
    llenarTabla(lista);
}
private void limpiarFormulario() {
    txtId.setText("");
    txtCodigo.setText("");
    txtNombre.setText("");
    txtDescripcion.setText("");
    txtStock.setText("0");
    txtStockMinimo.setText("5");
    txtPrecioCompra.setText("0.00");
    txtPrecioVenta.setText("0.00");
    rbActivo.setSelected(true);
    idRepuestoActual = 0;
    txtCodigo.requestFocus();
}
private void cargarRepuestoEnFormulario(entidad.Repuesto r) {
    txtId.setText(String.valueOf(r.getId()));
    txtCodigo.setText(r.getCodigo());
    txtNombre.setText(r.getNombre());
    txtDescripcion.setText(r.getDescripcion() != null ? r.getDescripcion() : "");
    txtStock.setText(String.valueOf(r.getStock()));
    txtStockMinimo.setText(String.valueOf(r.getStockMinimo()));
    txtPrecioCompra.setText(String.format(java.util.Locale.US, "%.2f", r.getPrecioCompra()));
    txtPrecioVenta.setText(String.format(java.util.Locale.US, "%.2f", r.getPrecioVenta()));

    if (r.getActivo() == 1) rbActivo.setSelected(true);
    else rbInactivo.setSelected(true);

    idRepuestoActual = r.getId();
}
private void accionEditar(int fila) {
    int id = Integer.parseInt(tblRepuestos.getValueAt(fila, 0).toString());
    entidad.Repuesto r = repuestoDAO.buscarPorId(id);
    if (r != null) cargarRepuestoEnFormulario(r);
}

private void accionEstado(int fila) {
    int id = Integer.parseInt(tblRepuestos.getValueAt(fila, 0).toString());
    String estado = tblRepuestos.getValueAt(fila, 6).toString();
    String nombre = tblRepuestos.getValueAt(fila, 2).toString();

    if ("Activo".equals(estado)) {
        int r = javax.swing.JOptionPane.showConfirmDialog(this,
            "¿Desactivar el repuesto \"" + nombre + "\"?",
            "Confirmar", javax.swing.JOptionPane.YES_NO_OPTION);
        if (r == javax.swing.JOptionPane.YES_OPTION) {
            if (repuestoDAO.desactivar(id)) {
                javax.swing.JOptionPane.showMessageDialog(this, "Repuesto desactivado.");
                cargarTabla();
                limpiarFormulario();
            }
        }
    } else {
        int r = javax.swing.JOptionPane.showConfirmDialog(this,
            "¿Reactivar el repuesto \"" + nombre + "\"?",
            "Confirmar", javax.swing.JOptionPane.YES_NO_OPTION);
        if (r == javax.swing.JOptionPane.YES_OPTION) {
            if (repuestoDAO.reactivar(id)) {
                javax.swing.JOptionPane.showMessageDialog(this, "Repuesto reactivado.");
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
        lblId = new javax.swing.JLabel();
        lblTitulo = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        lblId1 = new javax.swing.JLabel();
        txtCodigo = new javax.swing.JTextField();
        lblId2 = new javax.swing.JLabel();
        txtDescripcion = new javax.swing.JTextField();
        lblId3 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        lblId4 = new javax.swing.JLabel();
        txtStock = new javax.swing.JTextField();
        txtStockMinimo = new javax.swing.JTextField();
        lblId5 = new javax.swing.JLabel();
        txtPrecioCompra = new javax.swing.JTextField();
        lblId6 = new javax.swing.JLabel();
        lblId7 = new javax.swing.JLabel();
        txtPrecioVenta = new javax.swing.JTextField();
        rbActivo = new javax.swing.JRadioButton();
        rbInactivo = new javax.swing.JRadioButton();
        lblId8 = new javax.swing.JLabel();
        chkMostrarInactivos = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblRepuestos = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        lblBuscar = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();

        lblId.setText("id");

        lblTitulo.setBackground(new java.awt.Color(255, 255, 255));
        lblTitulo.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(102, 102, 255));
        lblTitulo.setText("Gestion de Repuestos");

        txtId.setBackground(new java.awt.Color(255, 255, 204));
        txtId.setEnabled(false);

        lblId1.setText("Codigo : ");

        lblId2.setText("Nombre : ");

        lblId3.setText("Stock :");

        lblId4.setText("Descripcion :");

        lblId5.setText("Stock mínimo :");

        lblId6.setText("Precio compra :");

        lblId7.setText("Precio venta :");

        grupoEstado.add(rbActivo);
        rbActivo.setText("Activo");

        grupoEstado.add(rbInactivo);
        rbInactivo.setText("Inactivo");

        lblId8.setText("Estado :");

        chkMostrarInactivos.setText("Mostrar Inactivos");
        chkMostrarInactivos.addItemListener(this::chkMostrarInactivosItemStateChanged);

        tblRepuestos.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblRepuestos);

        jButton1.setText("Guardar");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        jButton2.setText("Limpiar");
        jButton2.addActionListener(this::jButton2ActionPerformed);

        lblBuscar.setText("buscar");

        txtBuscar.addActionListener(this::txtBuscarActionPerformed);
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(62, 62, 62)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(32, 32, 32)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(lblId1)
                                            .addComponent(lblId, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(lblId7))
                                .addGap(3, 3, 3))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblId6, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblId5, javax.swing.GroupLayout.Alignment.TRAILING))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lblId3)
                        .addGap(28, 28, 28))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lblId4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lblId2)
                        .addGap(18, 18, 18)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtStock, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 597, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPrecioCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPrecioVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtStockMinimo, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(428, 428, 428)
                        .addComponent(lblTitulo))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 792, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(lblId8)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(rbActivo)
                                    .addComponent(lblBuscar))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(67, 67, 67)
                                        .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 379, Short.MAX_VALUE)
                                        .addComponent(jButton1)
                                        .addGap(27, 27, 27)
                                        .addComponent(jButton2))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(rbInactivo)
                                        .addGap(0, 0, Short.MAX_VALUE))))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(339, 339, 339)
                                .addComponent(chkMostrarInactivos)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addGap(103, 103, 103))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblTitulo)
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblId)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblId1)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblId2))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblId4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblId3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtStockMinimo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblId5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPrecioCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblId6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPrecioVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblId7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addComponent(chkMostrarInactivos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblId8)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(rbActivo)
                        .addComponent(rbInactivo)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblBuscar))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton1)
                        .addComponent(jButton2)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void chkMostrarInactivosItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkMostrarInactivosItemStateChanged
         buscarRepuestos();
    }//GEN-LAST:event_chkMostrarInactivosItemStateChanged

    private void txtBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarActionPerformed

    private void txtBuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyReleased
           if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
        buscarRepuestos();
    }
    }//GEN-LAST:event_txtBuscarKeyReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
       // ===== VALIDAR CÓDIGO =====
    if (txtCodigo.getText().trim().isEmpty()) {
        javax.swing.JOptionPane.showMessageDialog(this, "El código es obligatorio.");
        return;
    }

    // ===== VALIDAR NOMBRE =====
    if (txtNombre.getText().trim().isEmpty()) {
        javax.swing.JOptionPane.showMessageDialog(this, "El nombre es obligatorio.");
        return;
    }

    // ===== PARSEAR NÚMEROS =====
    int stock, stockMin;
    double pCompra, pVenta;
    try {
        stock = Integer.parseInt(txtStock.getText().trim());
        stockMin = Integer.parseInt(txtStockMinimo.getText().trim());
        pCompra = Double.parseDouble(txtPrecioCompra.getText().trim().replace(",", "."));
        pVenta = Double.parseDouble(txtPrecioVenta.getText().trim().replace(",", "."));
    } catch (NumberFormatException e) {
        javax.swing.JOptionPane.showMessageDialog(this, "Los números no son válidos.");
        return;
    }

    // ===== VALIDAR STOCK NO NEGATIVO - E10 =====
    if (stock < 0) {
        javax.swing.JOptionPane.showMessageDialog(this,
            "El stock no puede ser negativo.",
            "Stock inválido",
            javax.swing.JOptionPane.WARNING_MESSAGE);
        txtStock.requestFocus();
        return;
    }

    // ===== VALIDAR STOCK MÍNIMO NO NEGATIVO - E10 =====
    if (stockMin < 0) {
        javax.swing.JOptionPane.showMessageDialog(this,
            "El stock mínimo no puede ser negativo.",
            "Stock mínimo inválido",
            javax.swing.JOptionPane.WARNING_MESSAGE);
        txtStockMinimo.requestFocus();
        return;
    }

    // ===== VALIDAR PRECIO DE COMPRA NO NEGATIVO - E10 =====
    if (pCompra < 0) {
        javax.swing.JOptionPane.showMessageDialog(this,
            "El precio de compra no puede ser negativo.",
            "Precio inválido",
            javax.swing.JOptionPane.WARNING_MESSAGE);
        txtPrecioCompra.requestFocus();
        return;
    }

    // ===== VALIDAR PRECIO DE VENTA NO NEGATIVO - E10 =====
    if (pVenta < 0) {
        javax.swing.JOptionPane.showMessageDialog(this,
            "El precio de venta no puede ser negativo.",
            "Precio inválido",
            javax.swing.JOptionPane.WARNING_MESSAGE);
        txtPrecioVenta.requestFocus();
        return;
    }

    // ===== VERIFICAR CÓDIGO DUPLICADO =====
    entidad.Repuesto existente = repuestoDAO.buscarPorCodigo(txtCodigo.getText().trim());
    if (existente != null && existente.getId() != idRepuestoActual) {
        javax.swing.JOptionPane.showMessageDialog(this, "Ya existe un repuesto con ese código.");
        return;
    }

    // ===== CONSTRUIR OBJETO =====
    entidad.Repuesto r = new entidad.Repuesto();
    r.setCodigo(txtCodigo.getText().trim());
    r.setNombre(txtNombre.getText().trim());
    r.setDescripcion(txtDescripcion.getText().trim());
    r.setStock(stock);
    r.setStockMinimo(stockMin);
    r.setPrecioCompra(pCompra);
    r.setPrecioVenta(pVenta);
    r.setActivo(rbActivo.isSelected() ? 1 : 0);

    // ===== INSERTAR O ACTUALIZAR =====
    boolean ok;
    String msg;
    if (idRepuestoActual == 0) {
        ok = repuestoDAO.insertar(r);
        msg = "Repuesto registrado.";
    } else {
        r.setId(idRepuestoActual);
        ok = repuestoDAO.actualizar(r);
        msg = "Repuesto actualizado.";
    }

    // ===== MOSTRAR RESULTADO =====
    if (ok) {
        javax.swing.JOptionPane.showMessageDialog(this, msg);
        cargarTabla();
        limpiarFormulario();
    } else {
        javax.swing.JOptionPane.showMessageDialog(this, "No se pudo guardar.");
    }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        limpiarFormulario();
    }//GEN-LAST:event_jButton2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkMostrarInactivos;
    private javax.swing.ButtonGroup grupoEstado;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblId;
    private javax.swing.JLabel lblId1;
    private javax.swing.JLabel lblId2;
    private javax.swing.JLabel lblId3;
    private javax.swing.JLabel lblId4;
    private javax.swing.JLabel lblId5;
    private javax.swing.JLabel lblId6;
    private javax.swing.JLabel lblId7;
    private javax.swing.JLabel lblId8;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JRadioButton rbActivo;
    private javax.swing.JRadioButton rbInactivo;
    private javax.swing.JTable tblRepuestos;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextField txtDescripcion;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtPrecioCompra;
    private javax.swing.JTextField txtPrecioVenta;
    private javax.swing.JTextField txtStock;
    private javax.swing.JTextField txtStockMinimo;
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
    }
}
