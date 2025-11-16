/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package concurrencia;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Gestor de notificaciones que utiliza threads para mostrar mensajes
 * en la interfaz gráfica sin bloquear la aplicación
 * @author jorge
 */
public class NotificationManager {
    
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);
    private static final Color ERROR_COLOR = new Color(239, 68, 68);
    private static final Color INFO_COLOR = new Color(59, 130, 246);
    
    /**
     * Muestra una notificación de éxito
     */
    public static void showSuccess(JFrame parent, String message) {
        showNotification(parent, message, SUCCESS_COLOR, "✓ Éxito");
    }
    
    /**
     * Muestra una notificación de error
     */
    public static void showError(JFrame parent, String message) {
        showNotification(parent, message, ERROR_COLOR, "✗ Error");
    }
    
    /**
     * Muestra una notificación informativa
     */
    public static void showInfo(JFrame parent, String message) {
        showNotification(parent, message, INFO_COLOR, "ℹ Información");
    }
    
    /**
     * Método principal para mostrar notificaciones en un thread separado
     */
    private static void showNotification(JFrame parent, String message, Color color, String title) {
        executor.submit(() -> {
            // Asegurar que la actualización de la UI se haga en el EDT
            SwingUtilities.invokeLater(() -> {
                JDialog notification = createNotificationDialog(parent, message, color, title);
                notification.setVisible(true);
                
                // Auto-cerrar después de 3 segundos en un thread separado
                new Thread(() -> {
                    try {
                        Thread.sleep(3000);
                        SwingUtilities.invokeLater(() -> notification.dispose());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();
            });
        });
    }
    
    /**
     * Crea el diálogo de notificación con estilo moderno
     */
    private static JDialog createNotificationDialog(JFrame parent, String message, Color color, String title) {
        JDialog dialog = new JDialog(parent, title, false);
        dialog.setUndecorated(true);
        dialog.setLayout(new BorderLayout(10, 10));
        
        // Panel principal con borde de color
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(30, 30, 30));
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 3),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        // Icono
        JLabel iconLabel = new JLabel(getIcon(title));
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        
        // Mensaje
        JLabel messageLabel = new JLabel("<html><body style='width: 250px'>" + message + "</body></html>");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setForeground(new Color(243, 244, 246));
        
        mainPanel.add(iconLabel, BorderLayout.WEST);
        mainPanel.add(messageLabel, BorderLayout.CENTER);
        
        dialog.add(mainPanel);
        dialog.pack();
        
        // Posicionar en la esquina superior derecha
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        dialog.setLocation(
            screenSize.width - dialog.getWidth() - 20,
            20
        );
        
        // Hacer clic para cerrar
        dialog.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dialog.dispose();
            }
        });
        
        return dialog;
    }
    
    private static String getIcon(String title) {
        if (title.contains("Éxito")) return "✓";
        if (title.contains("Error")) return "✗";
        return "ℹ";
    }
    
    /**
     * Cierra el executor al finalizar la aplicación
     */
    public static void shutdown() {
        executor.shutdown();
    }
}