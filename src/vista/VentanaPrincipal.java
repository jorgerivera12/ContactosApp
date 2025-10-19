/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

import javax.swing.*;
import controlador.MainController;

/**
 *
 * @author jorge
 */
public class VentanaPrincipal extends JFrame {

    private JTabbedPane tabs;
    private PanelContactos panelContactos;
    private PanelEstadisticas panelEstadisticas;
    private MainController controller;

    public VentanaPrincipal() {
        setTitle("Gestión de Contactos");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1026, 748);
        setLocationRelativeTo(null);

        controller = new MainController(this);
        initComponents();
    }

    private void initComponents() {
        tabs = new JTabbedPane();

        panelContactos = new PanelContactos(controller);
        panelEstadisticas = new PanelEstadisticas(controller);

        tabs.addTab("Contactos", panelContactos);
        tabs.addTab("Estadísticas", panelEstadisticas);

        getContentPane().add(tabs);
    }

    public PanelContactos getPanelContactos() {
        return panelContactos;
    }

    public PanelEstadisticas getPanelEstadisticas() {
        return panelEstadisticas;
    }
}
