/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package presentacion.mecanico;

/**
 *
 * @author PC
 */
public class MisOrdenesPanel extends javax.swing.JPanel {
private final negocio.OrdenDAO ordenDAO = new negocio.OrdenDAO();
private final negocio.RepuestoDAO repuestoDAO = new negocio.RepuestoDAO();
    /**
     * Creates new form MisOrdenesPanel
     */
    public MisOrdenesPanel() {
        initComponents();
         cargarTabla();
         cargarFinalizadas();
    }
private void cargarTabla() {
    int mecanicoId = config.SesionUsuario.getId();
    java.util.List<entidad.OrdenTrabajo> lista =
        ordenDAO.listarPorMecanico(mecanicoId, true);
    llenarTabla(lista);
}
private void llenarTabla(java.util.List<entidad.OrdenTrabajo> lista) {
    javax.swing.table.DefaultTableModel modelo = new javax.swing.table.DefaultTableModel(
        new Object[][]{},
        new String[]{"ID", "N° Orden", "Fecha", "Cliente", "Vehículo", "Estado", "Acciones"}
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 6;
        }
    };

    int pendientes = 0, enProceso = 0;

    for (entidad.OrdenTrabajo o : lista) {
        modelo.addRow(new Object[]{
            o.getId(),
            o.getNumeroOrden(),
            o.getFechaIngreso() != null ? o.getFechaIngreso() : "",
            o.getNombreCliente() != null ? o.getNombreCliente() : "",
            o.getPlacaVehiculo() != null ? o.getPlacaVehiculo() : "",
            o.getEstado(),
            ""
        });

        switch (o.getEstado()) {
            case "PENDIENTE":  pendientes++; break;
            case "EN_PROCESO": enProceso++;  break;
        }
    }

    tblOrdenes.setModel(modelo);

    tblOrdenes.getColumnModel().getColumn(0).setPreferredWidth(40);
    tblOrdenes.getColumnModel().getColumn(1).setPreferredWidth(100);
    tblOrdenes.getColumnModel().getColumn(2).setPreferredWidth(120);
    tblOrdenes.getColumnModel().getColumn(3).setPreferredWidth(150);
    tblOrdenes.getColumnModel().getColumn(4).setPreferredWidth(90);
    tblOrdenes.getColumnModel().getColumn(5).setPreferredWidth(100);
    tblOrdenes.getColumnModel().getColumn(6).setPreferredWidth(80);

    tblOrdenes.setDefaultRenderer(Object.class, new EstadoRenderer());
    tblOrdenes.getColumnModel().getColumn(6).setCellRenderer(new BotonRenderer());
    tblOrdenes.getColumnModel().getColumn(6).setCellEditor(new BotonEditor());

    tblOrdenes.setRowHeight(30);

    lblInfo.setText("Total: " + lista.size()
                  + "   ·   Pendientes: " + pendientes
                  + "   ·   En proceso: " + enProceso);
}
// ============================================================
// CARGAR TABLA DE ÓRDENES FINALIZADAS
// ============================================================
private void cargarFinalizadas() {
    int mecanicoId = config.SesionUsuario.getId();
    java.util.List<entidad.OrdenTrabajo> lista =
        ordenDAO.listarPorMecanico(mecanicoId, false);  // ← false = finalizadas/entregadas/anuladas

    javax.swing.table.DefaultTableModel modelo = new javax.swing.table.DefaultTableModel(
        new Object[][]{},
        new String[]{"ID", "N° Orden", "Fecha", "Cliente", "Vehículo", "Estado", "Total"}
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    for (entidad.OrdenTrabajo o : lista) {
        modelo.addRow(new Object[]{
            o.getId(),
            o.getNumeroOrden(),
            o.getFechaIngreso() != null ? o.getFechaIngreso() : "",
            o.getNombreCliente() != null ? o.getNombreCliente() : "",
            o.getPlacaVehiculo() != null ? o.getPlacaVehiculo() : "",
            o.getEstado(),
            String.format("S/ %.2f", o.getTotal())
        });
    }

    tblFinalizadas.setModel(modelo);

    // Anchos
    tblFinalizadas.getColumnModel().getColumn(0).setPreferredWidth(40);
    tblFinalizadas.getColumnModel().getColumn(1).setPreferredWidth(100);
    tblFinalizadas.getColumnModel().getColumn(2).setPreferredWidth(130);
    tblFinalizadas.getColumnModel().getColumn(3).setPreferredWidth(150);
    tblFinalizadas.getColumnModel().getColumn(4).setPreferredWidth(90);
    tblFinalizadas.getColumnModel().getColumn(5).setPreferredWidth(100);
    tblFinalizadas.getColumnModel().getColumn(6).setPreferredWidth(90);

    tblFinalizadas.setDefaultRenderer(Object.class, new EstadoRenderer());
    tblFinalizadas.setRowHeight(28);
}

private void buscarOrdenes() {
    String texto = txtBuscar.getText().trim();
    int mecanicoId = config.SesionUsuario.getId();
    java.util.List<entidad.OrdenTrabajo> lista;

    if (texto.isEmpty()) {
        lista = ordenDAO.listarPorMecanico(mecanicoId, true);
    } else {
        lista = ordenDAO.buscarPorMecanico(mecanicoId, texto, true);
    }
    llenarTabla(lista);
}
private void accionVer(int fila) {
    int id = Integer.parseInt(tblOrdenes.getValueAt(fila, 0).toString());
    entidad.OrdenTrabajo o = ordenDAO.buscarPorId(id);
    if (o == null) return;

    // ✅ Usar el OrdenMecanicoDialog del paquete mecanico
    presentacion.mecanico.OrdenMecanicoDialog dialog =
        new presentacion.mecanico.OrdenMecanicoDialog(
            (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this),
            o
        );
    dialog.setVisible(true);
    cargarTabla();
}

private void abrirDetalle() {
    int fila = tblOrdenes.getSelectedRow();
    if (fila == -1) return;
    accionVer(fila);
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
        jLabel1 = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        btnActualizar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblOrdenes = new javax.swing.JTable();
        lblInfo = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblFinalizadas = new javax.swing.JTable();
        lblInfo1 = new javax.swing.JLabel();

        lblTitulo.setBackground(new java.awt.Color(255, 255, 255));
        lblTitulo.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(102, 102, 255));
        lblTitulo.setText("MIS ÓRDENES ASIGNADAS");

        jLabel1.setText("Buscar:");

        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarKeyReleased(evt);
            }
        });

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

        lblInfo.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblInfo.setText("Total : 0");

        tblFinalizadas.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tblFinalizadas);

        lblInfo1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblInfo1.setText("Ordenes Finalisadas");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(286, 286, 286)
                        .addComponent(lblTitulo))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(112, 112, 112)
                        .addComponent(jLabel1)
                        .addGap(27, 27, 27)
                        .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(61, 61, 61)
                        .addComponent(btnActualizar))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 664, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(147, 147, 147)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(147, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(394, 394, 394)
                .addComponent(lblInfo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblInfo1)
                .addGap(336, 336, 336))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(lblTitulo)
                .addGap(44, 44, 44)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnActualizar))
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblInfo)
                    .addComponent(lblInfo1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(320, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
       cargarTabla();
    }//GEN-LAST:event_btnActualizarActionPerformed

    private void txtBuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyReleased
         if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
        buscarOrdenes();
    }
    }//GEN-LAST:event_txtBuscarKeyReleased

    private void tblOrdenesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblOrdenesMouseClicked
        if (evt.getClickCount() == 2) {
        abrirDetalle();
    }
    }//GEN-LAST:event_tblOrdenesMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JLabel lblInfo1;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JTable tblFinalizadas;
    private javax.swing.JTable tblOrdenes;
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
                case "PENDIENTE":
                    c.setBackground(new java.awt.Color(255, 248, 200));
                    c.setForeground(java.awt.Color.BLACK);
                    break;
                case "EN_PROCESO":
                    c.setBackground(new java.awt.Color(210, 230, 255));
                    c.setForeground(java.awt.Color.BLACK);
                    break;
                default:
                    c.setBackground(table.getBackground());
                    c.setForeground(table.getForeground());
            }
            return c;
        }
    }

    private class BotonRenderer implements javax.swing.table.TableCellRenderer {
        private final javax.swing.JPanel panel;
        private final javax.swing.JButton btnVer;

        public BotonRenderer() {
            panel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));
            panel.setOpaque(true);
            btnVer = new javax.swing.JButton("👁️ Ver");
            btnVer.setFocusable(false);
            panel.add(btnVer);
        }

        @Override
        public java.awt.Component getTableCellRendererComponent(
                javax.swing.JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            if (isSelected) panel.setBackground(table.getSelectionBackground());
            else panel.setBackground(table.getBackground());
            return panel;
        }
    }

    private class BotonEditor extends javax.swing.AbstractCellEditor
            implements javax.swing.table.TableCellEditor {
        private final javax.swing.JPanel panel;
        private final javax.swing.JButton btnVer;
        private int filaActual;

        public BotonEditor() {
            panel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));
            panel.setOpaque(true);
            btnVer = new javax.swing.JButton("👁️ Ver");
            btnVer.setFocusable(false);
            btnVer.addActionListener(e -> {
                fireEditingStopped();
                accionVer(filaActual);
            });
            panel.add(btnVer);
        }

        @Override
        public java.awt.Component getTableCellEditorComponent(
                javax.swing.JTable table, Object value, boolean isSelected,
                int row, int column) {
            this.filaActual = row;
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() { return ""; }

        @Override
        public boolean isCellEditable(java.util.EventObject e) { return true; }
    }
}
