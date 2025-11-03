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

/**
 *
 * @author jorge
 */
public class AppContactos {

    /**
     * Launch the application.
     */
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
                new VentanaPrincipal(Locale.getDefault()).setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

}
