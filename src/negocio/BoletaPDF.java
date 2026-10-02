package negocio;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import entidad.Boleta;
import entidad.Configuracion;
import entidad.DetalleBoleta;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.List;

public class BoletaPDF {

    private final ConfiguracionDAO configDAO = new ConfiguracionDAO();
    private final DetalleBoletaDAO detalleDAO = new DetalleBoletaDAO();

    /**
     * Genera el PDF de la boleta y lo guarda en un archivo.
     * @param boleta la boleta a imprimir
     * @param archivoDestino el archivo PDF a crear
     * @return true si se generó correctamente
     */
    public boolean generar(Boleta boleta, File archivoDestino) {
        try {
            Configuracion config = configDAO.obtener();
            List<DetalleBoleta> items = detalleDAO.listarPorBoleta(boleta.getId());

            // Crear documento
            Document doc = new Document(PageSize.A5, 30, 30, 30, 30);
            PdfWriter.getInstance(doc, new FileOutputStream(archivoDestino));
            doc.open();

            // Fuentes
            Font titulo = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Font subtitulo = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
            Font normal = new Font(Font.FontFamily.HELVETICA, 9);
            Font pequeño = new Font(Font.FontFamily.HELVETICA, 8);
            Font negrita = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
            Font totalFont = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD);

            // ============ ENCABEZADO ============
            Paragraph pTaller = new Paragraph(config.getNombreTaller(), titulo);
            pTaller.setAlignment(Element.ALIGN_CENTER);
            doc.add(pTaller);

            if (config.getRuc() != null && !config.getRuc().isEmpty()) {
                Paragraph pRuc = new Paragraph("RUC: " + config.getRuc(), normal);
                pRuc.setAlignment(Element.ALIGN_CENTER);
                doc.add(pRuc);
            }

            if (config.getDireccion() != null && !config.getDireccion().isEmpty()) {
                Paragraph pDir = new Paragraph(config.getDireccion(), pequeño);
                pDir.setAlignment(Element.ALIGN_CENTER);
                doc.add(pDir);
            }

            if (config.getTelefono() != null && !config.getTelefono().isEmpty()) {
                Paragraph pTel = new Paragraph("Tel: " + config.getTelefono(), pequeño);
                pTel.setAlignment(Element.ALIGN_CENTER);
                doc.add(pTel);
            }

            doc.add(new Paragraph(" "));

            // Línea separadora
            doc.add(new Paragraph("─────────────────────────────────────────", pequeño));

            // ============ TÍTULO DE LA BOLETA ============
            Paragraph pTipo = new Paragraph("BOLETA DE VENTA ELECTRÓNICA", subtitulo);
            pTipo.setAlignment(Element.ALIGN_CENTER);
            doc.add(pTipo);

            Paragraph pNum = new Paragraph("N° " + boleta.getNumero(), subtitulo);
            pNum.setAlignment(Element.ALIGN_CENTER);
            doc.add(pNum);

            String fecha = boleta.getFechaEmision() != null
                         ? boleta.getFechaEmision()
                         : new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date());
            Paragraph pFecha = new Paragraph("Fecha: " + fecha, pequeño);
            pFecha.setAlignment(Element.ALIGN_CENTER);
            doc.add(pFecha);

            doc.add(new Paragraph(" "));

            // ============ DATOS DEL CLIENTE ============
            doc.add(new Paragraph("Cliente: " + valorSeguro(boleta.getNombreCliente()), normal));
            doc.add(new Paragraph("DNI:     " + valorSeguro(boleta.getDniCliente()), normal));

            if (boleta.getPlacaVehiculo() != null && !boleta.getPlacaVehiculo().isEmpty()) {
                doc.add(new Paragraph("Vehículo: " + boleta.getPlacaVehiculo(), normal));
            }

            doc.add(new Paragraph(" "));

            // ============ TABLA DE DETALLE ============
            PdfPTable tabla = new PdfPTable(4);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{1f, 5f, 2f, 2f});

            // Encabezados
            agregarCeldaEncabezado(tabla, "Cant");
            agregarCeldaEncabezado(tabla, "Descripción");
            agregarCeldaEncabezado(tabla, "P.Unit");
            agregarCeldaEncabezado(tabla, "Subtotal");

            // Filas
            for (DetalleBoleta d : items) {
                agregarCelda(tabla, String.valueOf(d.getCantidad()), normal, Element.ALIGN_CENTER);
                agregarCelda(tabla, d.getDescripcion(), normal, Element.ALIGN_LEFT);
                agregarCelda(tabla, formatoMoneda(d.getPrecioUnitario()), normal, Element.ALIGN_RIGHT);
                agregarCelda(tabla, formatoMoneda(d.getSubtotal()), normal, Element.ALIGN_RIGHT);
            }

            doc.add(tabla);

            doc.add(new Paragraph(" "));

            // ============ TOTALES ============
            PdfPTable tablaTotales = new PdfPTable(2);
            tablaTotales.setWidthPercentage(50);
            tablaTotales.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tablaTotales.setWidths(new float[]{3f, 2f});

            agregarCeldaTotal(tablaTotales, "Subtotal:", normal);
            agregarCeldaTotal(tablaTotales, "S/ " + formatoMoneda(boleta.getSubtotal()), normal);

            agregarCeldaTotal(tablaTotales, "IGV (" + (int) config.getIgvPorcentaje() + "%):", normal);
            agregarCeldaTotal(tablaTotales, "S/ " + formatoMoneda(boleta.getIgv()), normal);

            agregarCeldaTotal(tablaTotales, "TOTAL:", negrita);
            agregarCeldaTotal(tablaTotales, "S/ " + formatoMoneda(boleta.getTotal()), totalFont);

            doc.add(tablaTotales);

            doc.add(new Paragraph(" "));

            // ============ PIE ============
            doc.add(new Paragraph("Método de pago: " + valorSeguro(boleta.getMetodoPago()), normal));
            if (boleta.getNombreUsuario() != null) {
                doc.add(new Paragraph("Atendido por: " + boleta.getNombreUsuario(), normal));
            }

            doc.add(new Paragraph(" "));
            Paragraph pGracias = new Paragraph("¡Gracias por su preferencia!", negrita);
            pGracias.setAlignment(Element.ALIGN_CENTER);
            doc.add(pGracias);

            doc.close();
            return true;

        } catch (Exception e) {
            System.err.println("Error al generar PDF: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ============ MÉTODOS AUXILIARES ============

    private void agregarCeldaEncabezado(PdfPTable tabla, String texto) {
        PdfPCell celda = new PdfPCell(new Phrase(texto,
                new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.WHITE)));
        celda.setBackgroundColor(new BaseColor(33, 97, 140));
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        celda.setPadding(5);
        tabla.addCell(celda);
    }

    private void agregarCelda(PdfPTable tabla, String texto, Font fuente, int align) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, fuente));
        celda.setHorizontalAlignment(align);
        celda.setPadding(4);
        tabla.addCell(celda);
    }

    private void agregarCeldaTotal(PdfPTable tabla, String texto, Font fuente) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, fuente));
        celda.setBorder(Rectangle.NO_BORDER);
        celda.setPadding(3);
        celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
        tabla.addCell(celda);
    }

    private String formatoMoneda(double valor) {
        return String.format(java.util.Locale.US, "%.2f", valor);
    }

    private String valorSeguro(String valor) {
        return valor != null ? valor : "";
    }
}