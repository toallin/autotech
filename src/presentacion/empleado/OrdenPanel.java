/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package presentacion.empleado;

/**
 *
 * @author PC
 */
public class OrdenPanel extends javax.swing.JPanel {
private final negocio.OrdenDAO ordenDAO = new negocio.OrdenDAO();
private final negocio.DetalleOrdenDAO detalleDAO = new negocio.DetalleOrdenDAO();
private final negocio.ClienteDAO clienteDAO = new negocio.ClienteDAO();
private final negocio.VehiculoDAO vehiculoDAO = new negocio.VehiculoDAO();
private final negocio.UsuarioDAO usuarioDAO = new negocio.UsuarioDAO();
private final negocio.RepuestoDAO repuestoDAO = new negocio.RepuestoDAO();
    /**
     * Creates new form OrdenPanel
     */
    public OrdenPanel() {
        initComponents();
        cargarTabla();
    }
private void cargarTabla() {
    java.util.List<entidad.OrdenTrabajo> lista;

    if (chkMostrarAnuladas.isSelected()) {
        lista = ordenDAO.listar();
    } else {
        lista = ordenDAO.listarSinAnuladas();
    }

    llenarTabla(lista);
}

private void llenarTabla(java.util.List<entidad.OrdenTrabajo> lista) {
    javax.swing.table.DefaultTableModel modelo = new javax.swing.table.DefaultTableModel(
        new Object[][]{},
        new String[]{"ID", "N° Orden", "Fecha", "Cliente", "Vehículo", "Estado", "Total", "Acciones"}
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 7;   // solo la columna Acciones
        }
    };

    int pendientes = 0, enProceso = 0, finalizadas = 0;

    for (entidad.OrdenTrabajo o : lista) {
        modelo.addRow(new Object[]{
            o.getId(),
            o.getNumeroOrden(),
            o.getFechaIngreso() != null ? o.getFechaIngreso() : "",
            o.getNombreCliente() != null ? o.getNombreCliente() : "",
            o.getPlacaVehiculo() != null ? o.getPlacaVehiculo() : "",
            o.getEstado(),
            String.format("S/ %.2f", o.getTotal()),
            ""
        });

        switch (o.getEstado()) {
            case "PENDIENTE":   pendientes++; break;
            case "EN_PROCESO":  enProceso++;  break;
            case "FINALIZADO":  finalizadas++; break;
        }
    }

    tblOrdenes.setModel(modelo);

    // Anchos
    tblOrdenes.getColumnModel().getColumn(0).setPreferredWidth(40);
    tblOrdenes.getColumnModel().getColumn(1).setPreferredWidth(100);
    tblOrdenes.getColumnModel().getColumn(2).setPreferredWidth(90);
    tblOrdenes.getColumnModel().getColumn(3).setPreferredWidth(150);
    tblOrdenes.getColumnModel().getColumn(4).setPreferredWidth(90);
    tblOrdenes.getColumnModel().getColumn(5).setPreferredWidth(100);
    tblOrdenes.getColumnModel().getColumn(6).setPreferredWidth(80);
    tblOrdenes.getColumnModel().getColumn(7).setPreferredWidth(110);

    // Renderer de colores según estado
    tblOrdenes.setDefaultRenderer(Object.class, new EstadoRenderer());
    tblOrdenes.getColumnModel().getColumn(7).setCellRenderer(new BotonesRenderer());
    tblOrdenes.getColumnModel().getColumn(7).setCellEditor(new BotonesEditor());

    tblOrdenes.setRowHeight(30);

    lblInfo.setText("Total: " + lista.size()
                  + "   ·   Pendientes: " + pendientes
                  + "   ·   En proceso: " + enProceso
                  + "   ·   Finalizadas: " + finalizadas);
}
private void buscarOrdenes() {
    String texto = txtBuscar.getText().trim();
    boolean incluirAnuladas = chkMostrarAnuladas.isSelected();

    java.util.List<entidad.OrdenTrabajo> lista;

    if (texto.isEmpty()) {
        lista = incluirAnuladas ? ordenDAO.listar() : ordenDAO.listarSinAnuladas();
    } else {
        lista = ordenDAO.buscar(texto, incluirAnuladas);
    }

    llenarTabla(lista);
}
private void accionEditar(int fila) {
    int id = Integer.parseInt(tblOrdenes.getValueAt(fila, 0).toString());
    entidad.OrdenTrabajo o = ordenDAO.buscarPorId(id);
    if (o == null) return;

    presentacion.empleado.OrdenFormDialog dialog =
        new presentacion.empleado.OrdenFormDialog(
            (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this),
            o
        );
    dialog.setVisible(true);
    cargarTabla();
}

private void accionAnular(int fila) {
    int id = Integer.parseInt(tblOrdenes.getValueAt(fila, 0).toString());
    String numero = tblOrdenes.getValueAt(fila, 1).toString();
    String estado = tblOrdenes.getValueAt(fila, 5).toString();

    if ("ANULADO".equals(estado)) {
        javax.swing.JOptionPane.showMessageDialog(this,
            "Esta orden ya está anulada.");
        return;
    }

    if ("ENTREGADO".equals(estado)) {
        javax.swing.JOptionPane.showMessageDialog(this,
            "No se puede anular una orden ya entregada.");
        return;
    }

    int r = javax.swing.JOptionPane.showConfirmDialog(this,
        "¿Anular la orden " + numero + "?\nEsta acción no se puede deshacer.",
        "Confirmar anulación",
        javax.swing.JOptionPane.YES_NO_OPTION);

    if (r == javax.swing.JOptionPane.YES_OPTION) {
        if (ordenDAO.anular(id)) {
            javax.swing.JOptionPane.showMessageDialog(this, "Orden anulada.");
            cargarTabla();
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "No se pudo anular.");
        }
    }
}
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
                case "PENDIENTE":
                    c.setBackground(new java.awt.Color(255, 248, 200));   // amarillo claro
                    c.setForeground(java.awt.Color.BLACK);
                    break;
                case "EN_PROCESO":
                    c.setBackground(new java.awt.Color(210, 230, 255));   // azul claro
                    c.setForeground(java.awt.Color.BLACK);
                    break;
                case "FINALIZADO":
                    c.setBackground(new java.awt.Color(210, 245, 210));   // verde claro
                    c.setForeground(java.awt.Color.BLACK);
                    break;
                case "ENTREGADO":
                    c.setBackground(new java.awt.Color(235, 235, 235));   // gris claro
                    c.setForeground(java.awt.Color.BLACK);
                    break;
                case "ANULADO":
                    c.setBackground(new java.awt.Color(255, 215, 215));   // rojo claro
                    c.setForeground(java.awt.Color.BLACK);
                    break;
                default:
                    c.setBackground(table.getBackground());
                    c.setForeground(table.getForeground());
            }

            return c;
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
        lblBuscar = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        chkMostrarAnuladas = new javax.swing.JCheckBox();
        btnNuevaOrden = new javax.swing.JButton();
        btnActualizar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblOrdenes = new javax.swing.JTable();
        lblInfo = new javax.swing.JLabel();

        lblTitulo.setBackground(new java.awt.Color(255, 255, 255));
        lblTitulo.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(102, 102, 255));
        lblTitulo.setText("ÓRDENES DE TRABAJO");

        lblBuscar.setText("buscar");

        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarKeyReleased(evt);
            }
        });

        chkMostrarAnuladas.setText("Mostrar Anulados");
        chkMostrarAnuladas.addItemListener(this::chkMostrarAnuladasItemStateChanged);

        btnNuevaOrden.setText("+ Nueva Orden");
        btnNuevaOrden.addActionListener(this::btnNuevaOrdenActionPerformed);

        btnActualizar.setText("🔄 Actualizar");
        btnActualizar.addActionListener(this::btnActualizarActionPerformed);

        tblOrdenes.setModel(new javax.swing.table.DefaultTableModel(
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
        tblOrdenes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblOrdenesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblOrdenes);

        lblInfo.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblInfo.setText("Total: 0");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addComponent(lblBuscar)
                        .addGap(18, 18, 18)
                        .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(56, 56, 56)
                        .addComponent(chkMostrarAnuladas))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnNuevaOrden)
                                .addGap(71, 71, 71)
                                .addComponent(btnActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblTitulo)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 823, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(67, 67, 67)
                        .addComponent(lblInfo)))
                .addContainerGap(237, Short.MAX_VALUE))
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
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNuevaOrden)
                    .addComponent(btnActualizar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblInfo)
                .addContainerGap(257, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtBuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyReleased
         if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
        buscarOrdenes();
    }
    }//GEN-LAST:event_txtBuscarKeyReleased

    private void chkMostrarAnuladasItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkMostrarAnuladasItemStateChanged
        buscarOrdenes();
    }//GEN-LAST:event_chkMostrarAnuladasItemStateChanged

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
       cargarTabla();
    }//GEN-LAST:event_btnActualizarActionPerformed

    private void btnNuevaOrdenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevaOrdenActionPerformed
          presentacion.empleado.OrdenFormDialog dialog =
        new presentacion.empleado.OrdenFormDialog(
            (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this),
            null   // null = nueva orden
        );
    dialog.setVisible(true);

    // Al cerrar el dialog, refrescar la tabla
    cargarTabla();
    }//GEN-LAST:event_btnNuevaOrdenActionPerformed

    private void tblOrdenesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblOrdenesMouseClicked
         if (evt.getClickCount() == 2) {
        int fila = tblOrdenes.getSelectedRow();
        if (fila == -1) return;

        int id = Integer.parseInt(tblOrdenes.getValueAt(fila, 0).toString());
        entidad.OrdenTrabajo o = ordenDAO.buscarPorId(id);
        if (o == null) return;

        presentacion.empleado.OrdenFormDialog dialog =
            new presentacion.empleado.OrdenFormDialog(
                (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this),
                o
            );
        dialog.setVisible(true);

        cargarTabla();
    }
    }//GEN-LAST:event_tblOrdenesMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnNuevaOrden;
    private javax.swing.JCheckBox chkMostrarAnuladas;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JTable tblOrdenes;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables
    private class BotonesRenderer implements javax.swing.table.TableCellRenderer {
        private final javax.swing.JPanel panel;
        private final javax.swing.JButton btnEditar;
        private final javax.swing.JButton btnAnular;

        public BotonesRenderer() {
            panel = new javax.swing.JPanel(new java.awt.GridLayout(1, 2, 4, 0));
            panel.setOpaque(true);
            btnEditar = new javax.swing.JButton("✏️");
            btnEditar.setFocusable(false);
            btnAnular = new javax.swing.JButton("🚫");
            btnAnular.setFocusable(false);
            panel.add(btnEditar);
            panel.add(btnAnular);
        }

        @Override
        public java.awt.Component getTableCellRendererComponent(
                javax.swing.JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            String estado = table.getValueAt(row, 5).toString();
            if ("ANULADO".equals(estado) || "ENTREGADO".equals(estado)) {
                btnEditar.setEnabled(false);
                btnAnular.setEnabled(false);
            } else {
                btnEditar.setEnabled(true);
                btnAnular.setEnabled(true);
            }

            if (isSelected) panel.setBackground(table.getSelectionBackground());
            else panel.setBackground(table.getBackground());
            return panel;
        }
    }

    private class BotonesEditor extends javax.swing.AbstractCellEditor
            implements javax.swing.table.TableCellEditor {
        private final javax.swing.JPanel panel;
        private final javax.swing.JButton btnEditar;
        private final javax.swing.JButton btnAnular;
        private int filaActual;

        public BotonesEditor() {
            panel = new javax.swing.JPanel(new java.awt.GridLayout(1, 2, 4, 0));
            panel.setOpaque(true);
            btnEditar = new javax.swing.JButton("✏️");
            btnEditar.setFocusable(false);
            btnAnular = new javax.swing.JButton("🚫");
            btnAnular.setFocusable(false);

            btnEditar.addActionListener(e -> {
                fireEditingStopped();
                accionEditar(filaActual);
            });
            btnAnular.addActionListener(e -> {
                fireEditingStopped();
                accionAnular(filaActual);
            });

            panel.add(btnEditar);
            panel.add(btnAnular);
        }

        @Override
        public java.awt.Component getTableCellEditorComponent(
                javax.swing.JTable table, Object value, boolean isSelected,
                int row, int column) {
            this.filaActual = row;

            String estado = table.getValueAt(row, 5).toString();
            if ("ANULADO".equals(estado) || "ENTREGADO".equals(estado)) {
                btnEditar.setEnabled(false);
                btnAnular.setEnabled(false);
            } else {
                btnEditar.setEnabled(true);
                btnAnular.setEnabled(true);
            }

            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() { return ""; }

        @Override
        public boolean isCellEditable(java.util.EventObject e) { return true; }
    }}
