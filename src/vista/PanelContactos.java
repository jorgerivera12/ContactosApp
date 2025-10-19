/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import controlador.MainController;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import modelo.ContactoTableModel;

/**
 *
 * @author jorge
 */
public class PanelContactos extends JPanel {

    private JTextField txtBuscar;
    private JTable tabla;
    private JButton btnNuevo, btnExportar;
    private JProgressBar barraProgreso;
    private ContactoTableModel modelo;
    private TableRowSorter<ContactoTableModel> sorter;

    public PanelContactos(MainController controller) {
        setLayout(new BorderLayout());

        // Norte: formulario + búsqueda
        JPanel norte = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnNuevo = new JButton("Nuevo");
        btnExportar = new JButton("Exportar");
        txtBuscar = new JTextField(20);
        norte.add(btnNuevo);
        norte.add(btnExportar);
        norte.add(new JLabel("Buscar:"));
        norte.add(txtBuscar);
        add(norte, BorderLayout.NORTH);

        // Centro: tabla
        modelo = new ContactoTableModel(controller.getTodos());
        tabla = new JTable(modelo);
        sorter = new TableRowSorter<>(modelo);
        tabla.setRowSorter(sorter);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Sur: barra de progreso (oculta inicialmente)
        barraProgreso = new JProgressBar();
        barraProgreso.setStringPainted(true);
        barraProgreso.setVisible(false);
        add(barraProgreso, BorderLayout.SOUTH);

        // Conectar eventos al controlador
        controller.bindPanelContactos(this);
    }

    public ContactoTableModel getModelo() {
        return modelo;
    }

    // Getters para el controlador
    public JTextField getTxtBuscar() {
        return txtBuscar;
    }

    public JTable getTabla() {
        return tabla;
    }

    public JButton getBtnNuevo() {
        return btnNuevo;
    }

    public JButton getBtnExportar() {
        return btnExportar;
    }

    public JProgressBar getBarraProgreso() {
        return barraProgreso;
    }

    public TableRowSorter<ContactoTableModel> getSorter() {
        return sorter;
    }
}
