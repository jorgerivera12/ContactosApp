/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

package app;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.util.Locale;
import vista.VentanaPrincipal;
import javax.swing.UIManager;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Aplicación de Gestión de Contactos con programación concurrente
 * Unidad 3 - Universidad Politécnica Salesiana
 * @author jorge
 */
public class AppContactos {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 13));
            UIManager.put("Button.background", new Color(230, 240, 250));
            UIManager.put("Button.foreground", Color.BLACK);
            UIManager.put("Panel.background", new Color(245, 247, 250));
            UIManager.put("Label.foreground", new Color(50, 50, 50));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Locale.setDefault(new Locale("es"));
        
        EventQueue.invokeLater(() -> {
            try {
                VentanaPrincipal ventana = new VentanaPrincipal(Locale.getDefault());
                
                // Agregar WindowListener para liberar recursos al cerrar
                ventana.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        // Liberar recursos de concurrencia
                        if (ventana.getController() != null) {
                            ventana.getController().shutdown();
                        }
                        System.exit(0);
                    }
                });
                
                ventana.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}