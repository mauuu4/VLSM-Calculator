package com.miapp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class Frame extends JFrame {
    private JTextField txtIP, txtMask, txtSubnets;
    private JButton btnGenerar, btnCalcular;
    private JPanel panelSubnets;
    private JTextField[] txtHosts;

    public Frame() {
        initComponents();
    }

    private void initComponents() {
        // Configuración principal de la ventana
        setTitle("Calculadora VLSM");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel superior para datos iniciales
        JPanel inputPanel = new JPanel();
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        inputPanel.setLayout(new GridLayout(2, 1, 10, 10));

        // Subpanel de configuración IP
        JPanel configPanel = new JPanel();
        JLabel lblIP = new JLabel("Direccion IP:");
        txtIP = new JTextField(10);
        JLabel lblMask = new JLabel("/");
        txtMask = new JTextField(3);

        configPanel.add(lblIP);
        configPanel.add(txtIP);
        configPanel.add(lblMask);
        configPanel.add(txtMask);

        // Subpanel de cantidad de subredes
        JPanel subnetPanel = new JPanel();
        JLabel lblSubnets = new JLabel("Cantidad de subredes:");
        txtSubnets = new JTextField(5);
        btnGenerar = new JButton("Generar Subredes");

        subnetPanel.add(lblSubnets);
        subnetPanel.add(txtSubnets);
        subnetPanel.add(btnGenerar);

        inputPanel.add(configPanel);
        inputPanel.add(subnetPanel);

        add(inputPanel, BorderLayout.NORTH);

        // Panel central para las subredes
        panelSubnets = new JPanel();
        panelSubnets.setLayout(new BoxLayout(panelSubnets, BoxLayout.Y_AXIS));

        JScrollPane scrollPaneSubnets = new JScrollPane(panelSubnets);
        scrollPaneSubnets.setPreferredSize(new Dimension(400, 500));
        scrollPaneSubnets.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(scrollPaneSubnets, BorderLayout.CENTER);

        btnGenerar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generarSubnets(e);
            }
        });
        btnCalcular = new JButton("Calcular");
        btnCalcular.setVisible(false);
        btnCalcular.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calcularSubnets(e);
            }
        });
        add(btnCalcular, BorderLayout.SOUTH);

    }
    private void generarSubnets(ActionEvent e) {
        try {
            int numSubnets = Integer.parseInt(txtSubnets.getText());
            if (numSubnets <= 0 || numSubnets > 20) { // Agregada validación de rango
                throw new NumberFormatException();
            }

            // Resetear el panel
            panelSubnets.removeAll();
            panelSubnets.setLayout(new GridLayout(numSubnets, 2, 5, 5));

            txtHosts = new JTextField[numSubnets];

            for (int i = 0; i < numSubnets; i++) {
                JLabel lblHost = new JLabel("Subred " + (i + 1) + " (hosts requeridos):");
                txtHosts[i] = new JTextField(5);
                panelSubnets.add(lblHost);
                panelSubnets.add(txtHosts[i]);
            }
            btnCalcular.setVisible(true);

            panelSubnets.revalidate();
            panelSubnets.repaint();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un número válido de subredes (1-20).", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void calcularSubnets(ActionEvent e) {
        try {
            int mask = Integer.parseInt(txtMask.getText());
            int[] hosts = new int[txtHosts.length];
            for (int i = 0; i < txtHosts.length; i++) {
                hosts[i] = Integer.parseInt(txtHosts[i].getText());
                if (hosts[i] <= 0) throw new IllegalArgumentException("El número de hosts debe ser mayor a cero.");
            }
            CalculadoraVLSM calc = new CalculadoraVLSM(txtIP.getText(), mask, hosts);
            calc.calcular();
            calc.mostrarResultados();
            mostrarResultado(calc); // Modificado para pasar la calculadora completa
        }  catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarResultado(CalculadoraVLSM calc) {
        JDialog resultDialog = new JDialog(this, "Resultados VLSM", true);
        resultDialog.setSize(600, 800);
        resultDialog.setLocationRelativeTo(this);

        JTextArea resultados = new JTextArea(calc.mostrarResultados());
        resultados.setEditable(false);
        resultados.setFont(new Font("Monospaced", Font.PLAIN, 13));

        JScrollPane scrollPane = new JScrollPane(resultados);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resultDialog.dispose();
            }
        });

        JButton btnExportarPDF = new JButton("Exportar PDF");
        btnExportarPDF.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    exportarPDF(calc);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(resultDialog, "Error al exportar el PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        buttonPanel.add(btnExportarPDF);
        buttonPanel.add(btnCerrar);

        resultDialog.setLayout(new BorderLayout());
        resultDialog.add(scrollPane, BorderLayout.CENTER);
        resultDialog.add(buttonPanel, BorderLayout.SOUTH);

        resultDialog.setVisible(true);
    }

    private void exportarPDF(CalculadoraVLSM calc) throws Exception {
        String rutaArchivo = "Resultado_VLSM.pdf";
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(rutaArchivo));
        document.open();

        // Título estilizado
        com.itextpdf.text.Font tituloFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD);
        Paragraph titulo = new Paragraph("Resultados VLSM", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);
        document.add(new Paragraph("\n"));

        // Crear tabla con diseño mejorado
        PdfPTable tabla = new PdfPTable(6);
        tabla.setWidthPercentage(110);

        // Ajustar anchos de las columnas (proporciones)
        float[] columnWidths = {3f, 3f, 3f, 3f, 3f, 1f}; //
        tabla.setWidths(columnWidths);

        // Estilo para encabezados
        com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD, BaseColor.WHITE);
        PdfPCell headerCell = new PdfPCell();
        headerCell.setBackgroundColor(BaseColor.DARK_GRAY);
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);

        String[] encabezados = {"Red", "Gateway", "Primer Host", "Último Host", "Broadcast", "LAN"};
        for (String encabezado : encabezados) {
            headerCell.setPhrase(new com.itextpdf.text.Phrase(encabezado, headerFont));
            tabla.addCell(headerCell);
        }

        // Estilo para datos
        com.itextpdf.text.Font dataFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.COURIER, 10, com.itextpdf.text.Font.NORMAL);
        String infoTabla = calc.getRedPadre().getInfoTabla();
        String[] lineas = infoTabla.split("\n");

        for (String linea : lineas) {
            if (!linea.trim().isEmpty()) {
                String[] datos = linea.split("\\|");
                for (String dato : datos) {
                    PdfPCell dataCell = new PdfPCell(new com.itextpdf.text.Phrase(dato.trim(), dataFont));
                    dataCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    dataCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    dataCell.setMinimumHeight(25f);
                    tabla.addCell(dataCell);
                }
            }
        }
        document.add(tabla);

        Paragraph nota = new Paragraph("Nota: Todas las direcciones IP están en formato IPv4");
        nota.setSpacingBefore(10f);
        document.add(nota);

        // Resumen con fuente monoespaciada
        com.itextpdf.text.Font resumenFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.COURIER, 12, com.itextpdf.text.Font.NORMAL);
        Paragraph resumenTitulo = new Paragraph("\nResumen VLSM", new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 14, com.itextpdf.text.Font.BOLD));
        document.add(resumenTitulo);

        Paragraph resumen = new Paragraph(calc.mostrarResultados(), resumenFont);
        document.add(resumen);

        document.close();

        JOptionPane.showMessageDialog(null, "PDF exportado correctamente a: " + rutaArchivo);
    }

    public static void main(String args[]) {
        new Frame().setVisible(true);
    }
}
