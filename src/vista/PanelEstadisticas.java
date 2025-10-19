/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Dimension;
import java.util.List;
import modelo.Persona;
import controlador.MainController;
/**
 *
 * @author jorge
 */
public class PanelEstadisticas extends JPanel {
    private MainController controller;

    public PanelEstadisticas(MainController controller) {
        this.controller = controller;
        setPreferredSize(new Dimension(800, 600));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
         List<Persona> todos = controller.getTodos(); 
        if (todos.isEmpty()) return;

        // Cuenta favoritos y no favoritos
        long fav = todos.stream().filter(Persona::isFavorito).count();
        long resto = todos.size() - fav;

        // Calcula ángulos
        double total = fav + resto;
        int angFav   = (int) Math.round(fav / total * 360);
        int angResto = 360 - angFav;

        Graphics2D g2 = (Graphics2D) g;
        int x = 50, y = 50, w = 300, h = 300;

        // Dibuja segmento de favoritos (en rojo)
        g2.setColor(Color.RED);
        g2.fillArc(x, y, w, h, 0, angFav);
        // Dibuja segmento de resto (en gris)
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillArc(x, y, w, h, angFav, angResto);

        // Leyenda
        g2.setColor(Color.RED);
        g2.fillRect(x + w + 20, y, 20, 20);
        g2.setColor(Color.BLACK);
        g2.drawString("Favoritos: " + fav, x + w + 50, y + 15);

        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(x + w + 20, y + 30, 20, 20);
        g2.setColor(Color.BLACK);
        g2.drawString("No favoritos: " + resto, x + w + 50, y + 45);
    }
}