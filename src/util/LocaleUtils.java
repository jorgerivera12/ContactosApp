/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;
import java.util.Locale;
import javax.swing.*;

/**
 *
 * @author jorge
 */
public final class LocaleUtils {
    private LocaleUtils(){}

    public static void switchLocale(JFrame frame, Locale locale, Runnable reloader) {
        Locale.setDefault(locale);
        SwingUtilities.invokeLater(() -> {
            frame.dispose();
            reloader.run(); // crea una nueva VentanaPrincipal con el nuevo locale
        });
    }
}