/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package presentacion.empleado;

/**
 *
 * @author PC
 */
public class BoletaPanel extends javax.swing.JPanel {
private final negocio.BoletaDAO boletaDAO = new negocio.BoletaDAO();
private final negocio.DetalleBoletaDAO detalleBoletaDAO = new negocio.DetalleBoletaDAO();
    /**
     * Creates new form BoletaPanel
     */
    public BoletaPanel() {
        initComponents();
        cargarTabla();
    }
private void cargarTabla() {
    java.util.List<entidad.Boleta> lista;
    if (chkMostrarAnuladas.isSelected()) {
        lista = boletaDAO.listar();
    } else {
        lista = boletaDAO.listarSinAnuladas();
    }
    llenarTabla(lista);
}

private void llenarTabla(java.util.List<entidad.Boleta> lista) {
    javax.swing.table.DefaultTableModel modelo = new javax.swing.table.DefaultTableModel(
        new Object[][]{},
        new String[]{"ID", "N° Boleta", "Fecha", "Cliente", "Total", "Estado", "Acciones"}
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 6;
        }
    };

    int emitidas = 0, anuladas = 0;

    for (entidad.Boleta b : lista) {
        modelo.addRow(new Object[]{
            b.getId(),
            b.getNumero(),
            b.getFechaEmision() != null ? b.getFechaEmision() : "",
            b.getNombreCliente() != null ? b.getNombreCliente() : "",
            String.format("S/ %.2f", b.getTotal()),
            b.getEstado(),
            ""
        });

        if ("EMITIDA".equals(b.getEstado())) emitidas++;
        else if ("ANULADA".equals(b.getEstado())) anuladas++;
    }

    tblBoletas.setModel(modelo);

    tblBoletas.getColumnModel().getColumn(0).setPreferredWidth(40);
    tblBoletas.getColumnModel().getColumn(1).setPreferredWidth(120);
    tblBoletas.getColumnModel().getColumn(2).setPreferredWidth(130);
    tblBoletas.getColumnModel().getColumn(3).setPreferredWidth(150);
    tblBoletas.getColumnModel().getColumn(4).setPreferredWidth(90);
    tblBoletas.getColumnModel().getColumn(5).setPreferredWidth(80);
    tblBoletas.getColumnModel().getColumn(6).setPreferredWidth(100);

    tblBoletas.setDefaultRenderer(Object.class, new EstadoRenderer());
    tblBoletas.getColumnModel().getColumn(6).setCellRenderer(new BotonesRenderer());
    tblBoletas.getColumnModel().getColumn(6).setCellEditor(new BotonesEditor());

    tblBoletas.setRowHeight(30);

    lblInfo.setText("Total boletas: " + lista.size()
                  + "   ·   Emitidas: " + emitidas
                  + "   ·   Anuladas: " + anuladas);
}
private void buscarBoletas() {
    String texto = txtBuscar.getText().trim();
    boolean incluirAnuladas = chkMostrarAnuladas.isSelected();

    java.util.List<entidad.Boleta> lista;
    if (texto.isEmpty()) {
        lista = incluirAnuladas ? boletaDAO.listar() : boletaDAO.listarSinAnuladas();
    } else {
        lista = boletaDAO.buscar(texto, incluirAnuladas);
    }
    llenarTabla(lista);
}
private void accionVer(int fila) {
    int id = Integer.parseInt(tblBoletas.getValueAt(fila, 0).toString());
    entidad.Boleta b = boletaDAO.buscarPorId(id);
    if (b == null) return;

    presentacion.empleado.BoletaFormDialog dialog =
        new presentacion.empleado.BoletaFormDialog(
            (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this),
            b
        );
    dialog.setVisible(true);
    cargarTabla();
}

private void accionAnular(int fila) {
    int id = Integer.parseInt(tblBoletas.getValueAt(fila, 0).toString());
    String numero = tblBoletas.getValueAt(fila, 1).toString();
    String estado = tblBoletas.getValueAt(fila, 5).toString();

    if ("ANULADA".equals(estado)) {
        javax.swing.JOptionPane.showMessageDialog(this, "Esta boleta ya está anulada.");
        return;
    }

    int r = javax.swing.JOptionPane.showConfirmDialog(this,
        "¿Anular la boleta " + numero + "?\n"
      + "Se devolverá el stock de los repuestos y la orden volverá a FINALIZADO.",
        "Confirmar anulación",
        javax.swing.JOptionPane.YES_NO_OPTION);

    if (r != javax.swing.JOptionPane.YES_OPTION) return;

    java.sql.Connection cn = null;
    try {
        cn = config.conexion.getConexion();
        cn.setAutoCommit(false);

        boletaDAO.anular(cn, id);

        cn.commit();

        javax.swing.JOptionPane.showMessageDialog(this,
            "Boleta anulada correctamente.");

        cargarTabla();

    } catch (java.sql.SQLException e) {
        if (cn != null) {
            try { cn.rollback(); } catch (java.sql.SQLException ignored) { }
        }
        javax.swing.JOptionPane.showMessageDialog(this,
            "No se pudo anular: " + e.getMessage(),
            "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
    } finally {
        if (cn != null) {
            try { cn.setAutoCommit(true); cn.close(); }
            catch (java.sql.SQLException ignored) { }
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

        lblTitulo = new javax.swing.JLabel();
        chkMostrarAnuladas = new javax.swing.JCheckBox();
        txtBuscar = new javax.swing.JTextField();
        lblBuscar = new javax.swing.JLabel();
        btnNuevaBoleta = new javax.swing.JButton();
        btnActualizar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblBoletas = new javax.swing.JTable();
        lblInfo = new javax.swing.JLabel();

        lblTitulo.setBackground(new java.awt.Color(255, 255, 255));
        lblTitulo.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(102, 102, 255));
        lblTitulo.setText("Boletas");

        chkMostrarAnuladas.setText("Mostrar Anulados");
        chkMostrarAnuladas.addItemListener(this::chkMostrarAnuladasItemStateChanged);

        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarKeyReleased(evt);
            }
        });

        lblBuscar.setText("buscar");

        btnNuevaBoleta.setText("+ Nueva Boleta");
        btnNuevaBoleta.addActionListener(this::btnNuevaBoletaActionPerformed);

        btnActualizar.setText("🔄 Actualizar");
        btnActualizar.addActionListener(this::btnActualizarActionPerformed);

        tblBoletas.setModel(new javax.swing.table.DefaultTableModel(
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
        tblBoletas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblBoletasMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblBoletas);

        lblInfo.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblInfo.setText("Total: 0");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblTitulo))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(93, 93, 93)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnNuevaBoleta)
                                .addGap(69, 69, 69)
                                .addComponent(btnActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblBuscar)
                                .addGap(18, 18, 18)
                                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(56, 56, 56)
                                .addComponent(chkMostrarAnuladas))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(123, 123, 123)
                        .addComponent(lblInfo))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 655, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(262, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblTitulo)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBuscar)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkMostrarAnuladas))
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNuevaBoleta)
                    .addComponent(btnActualizar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(lblInfo)
                .addContainerGap(42, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void chkMostrarAnuladasItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkMostrarAnuladasItemStateChanged
         buscarBoletas();
    }//GEN-LAST:event_chkMostrarAnuladasItemStateChanged

    private void txtBuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyReleased
         if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
        buscarBoletas();
    }
    }//GEN-LAST:event_txtBuscarKeyReleased

    private void btnNuevaBoletaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevaBoletaActionPerformed
           presentacion.empleado.BoletaFormDialog dialog =
        new presentacion.empleado.BoletaFormDialog(
            (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this),
            null
        );
    dialog.setVisible(true);
    cargarTabla();

    }//GEN-LAST:event_btnNuevaBoletaActionPerformed

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
        cargarTabla();
    }//GEN-LAST:event_btnActualizarActionPerformed

    private void tblBoletasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBoletasMouseClicked
         if (evt.getClickCount() == 2) {
        int fila = tblBoletas.getSelectedRow();
        if (fila == -1) return;
        int id = Integer.parseInt(tblBoletas.getValueAt(fila, 0).toString());
        entidad.Boleta b = boletaDAO.buscarPorId(id);
        if (b == null) return;

        presentacion.empleado.BoletaFormDialog dialog =
            new presentacion.empleado.BoletaFormDialog(
                (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this),
                b
            );
        dialog.setVisible(true);
        cargarTabla();
    }
    }//GEN-LAST:event_tblBoletasMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnNuevaBoleta;
    private javax.swing.JCheckBox chkMostrarAnuladas;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JTable tblBoletas;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables
    private class EstadoRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public java.awt.Component getTableCellRendererComponent(
                javax.swing.JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            java.awt.Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            if (isSelected) {
                c.setBackground(table.getSelectionBackground());
                c.setForeground(table.getSelectionForeground());
                return c;
            }

            String estado = table.getValueAt(row, 5).toString();
            switch (estado) {
                case "EMITIDA":
                    c.setBackground(new java.awt.Color(210, 245, 210));
                    c.setForeground(java.awt.Color.BLACK);
                    break;
                case "ANULADA":
                    c.setBackground(new java.awt.Color(255, 215, 215));
                    c.setForeground(java.awt.Color.BLACK);
                    break;
                default:
                    c.setBackground(table.getBackground());
                    c.setForeground(table.getForeground());
            }
            return c;
        }
    }

    private class BotonesRenderer implements javax.swing.table.TableCellRenderer {
        private final javax.swing.JPanel panel;
        private final javax.swing.JButton btnVer;
        private final javax.swing.JButton btnAnular;

        public BotonesRenderer() {
            panel = new javax.swing.JPanel(new java.awt.GridLayout(1, 2, 4, 0));
            panel.setOpaque(true);
            btnVer = new javax.swing.JButton("👁️");
            btnVer.setFocusable(false);
            btnAnular = new javax.swing.JButton("🚫");
            btnAnular.setFocusable(false);
            panel.add(btnVer);
            panel.add(btnAnular);
        }

        @Override
        public java.awt.Component getTableCellRendererComponent(
                javax.swing.JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            String estado = table.getValueAt(row, 5).toString();
            btnAnular.setEnabled(!"ANULADA".equals(estado));

            if (isSelected) panel.setBackground(table.getSelectionBackground());
            else panel.setBackground(table.getBackground());
            return panel;
        }
    }

    private class BotonesEditor extends javax.swing.AbstractCellEditor
            implements javax.swing.table.TableCellEditor {
        private final javax.swing.JPanel panel;
        private final javax.swing.JButton btnVer;
        private final javax.swing.JButton btnAnular;
        private int filaActual;

        public BotonesEditor() {
            panel = new javax.swing.JPanel(new java.awt.GridLayout(1, 2, 4, 0));
            panel.setOpaque(true);
            btnVer = new javax.swing.JButton("👁️");
            btnVer.setFocusable(false);
            btnAnular = new javax.swing.JButton("🚫");
            btnAnular.setFocusable(false);

            btnVer.addActionListener(e -> { fireEditingStopped(); accionVer(filaActual); });
            btnAnular.addActionListener(e -> { fireEditingStopped(); accionAnular(filaActual); });

            panel.add(btnVer);
            panel.add(btnAnular);
        }

        @Override
        public java.awt.Component getTableCellEditorComponent(
                javax.swing.JTable table, Object value, boolean isSelected,
                int row, int column) {
            this.filaActual = row;
            String estado = table.getValueAt(row, 5).toString();
            btnAnular.setEnabled(!"ANULADA".equals(estado));
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() { return ""; }

        @Override
        public boolean isCellEditable(java.util.EventObject e) { return true; }
    }
}
