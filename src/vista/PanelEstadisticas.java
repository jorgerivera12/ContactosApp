package vista;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.RenderingHints;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.BasicStroke;
import java.util.List;
import modelo.Persona;
import controlador.MainController;
import i18n.Messages;

/**
 * @author jorge
 */
public class PanelEstadisticas extends JPanel {
    private MainController controller;
    private final Messages msg;
    
    // Colores modernos para modo dark
    private static final Color BG_DARK = new Color(18, 18, 18);
    private static final Color CARD_BG = new Color(30, 30, 30);
    private static final Color FAV_COLOR = new Color(239, 68, 68); // Rojo vibrante
    private static final Color FAV_COLOR_LIGHT = new Color(252, 165, 165);
    private static final Color OTHER_COLOR = new Color(99, 102, 241); // Índigo
    private static final Color OTHER_COLOR_LIGHT = new Color(165, 180, 252);
    private static final Color TEXT_PRIMARY = new Color(243, 244, 246);
    private static final Color TEXT_SECONDARY = new Color(156, 163, 175);
    private static final Color ACCENT_LINE = new Color(55, 65, 81);
    
    public PanelEstadisticas(MainController controller, Messages msg) {
        this.controller = controller;
        this.msg = msg;
        setPreferredSize(new Dimension(800, 600));
        setBackground(BG_DARK);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        List<Persona> todos = controller.getTodos(); 
        if (todos.isEmpty()) {
            drawEmptyState(g);
            return;
        }
        
        Graphics2D g2 = (Graphics2D) g;
        
        // Anti-aliasing para gráficos suaves
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Cuenta favoritos y no favoritos
        long fav = todos.stream().filter(Persona::isFavorito).count();
        long resto = todos.size() - fav;
        
        int width = getWidth();
        int height = getHeight();
        
        // Dibuja título
        drawTitle(g2, width);
        
        // Dibuja tarjetas de resumen
        drawSummaryCards(g2, todos.size(), fav, resto, width);
        
        // Dibuja gráfica de dona mejorada
        drawDonutChart(g2, fav, resto, width, height);
        
        // Dibuja leyenda mejorada
        drawModernLegend(g2, fav, resto, width, height);
    }
    
    private void drawTitle(Graphics2D g2, int width) {
        g2.setFont(new Font("Segoe UI", Font.BOLD, 28));
        g2.setColor(TEXT_PRIMARY);
        String title = msg.get("stats.title", "Estadísticas");
        FontMetrics fm = g2.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        g2.drawString(title, (width - titleWidth) / 2, 40);
    }
    
    private void drawSummaryCards(Graphics2D g2, int total, long fav, long resto, int width) {
        int cardWidth = 180;
        int cardHeight = 90;
        int spacing = 20;
        int startX = (width - (cardWidth * 3 + spacing * 2)) / 2;
        int y = 70;
        
        // Tarjeta Total
        drawCard(g2, startX, y, cardWidth, cardHeight, 
                 String.valueOf(total), "Total", new Color(59, 130, 246));
        
        // Tarjeta Favoritos
        drawCard(g2, startX + cardWidth + spacing, y, cardWidth, cardHeight,
                 String.valueOf(fav), msg.get("stats.favorites.short", "Favoritos"), FAV_COLOR);
        
        // Tarjeta Otros
        drawCard(g2, startX + (cardWidth + spacing) * 2, y, cardWidth, cardHeight,
                 String.valueOf(resto), msg.get("stats.others.short", "Otros"), OTHER_COLOR);
    }
    
    private void drawCard(Graphics2D g2, int x, int y, int w, int h, 
                          String value, String label, Color accentColor) {
        // Fondo de tarjeta con sombra
        g2.setColor(new Color(0, 0, 0, 50));
        g2.fillRoundRect(x + 4, y + 4, w, h, 15, 15);
        
        g2.setColor(CARD_BG);
        g2.fillRoundRect(x, y, w, h, 15, 15);
        
        // Borde sutil
        g2.setColor(ACCENT_LINE);
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(x, y, w, h, 15, 15);
        
        // Línea de acento
        g2.setColor(accentColor);
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(x + 15, y, x + w - 15, y);
        
        // Valor
        g2.setFont(new Font("Segoe UI", Font.BOLD, 32));
        g2.setColor(TEXT_PRIMARY);
        FontMetrics fm = g2.getFontMetrics();
        int valueWidth = fm.stringWidth(value);
        g2.drawString(value, x + (w - valueWidth) / 2, y + 45);
        
        // Etiqueta
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        g2.setColor(TEXT_SECONDARY);
        fm = g2.getFontMetrics();
        int labelWidth = fm.stringWidth(label);
        g2.drawString(label, x + (w - labelWidth) / 2, y + 70);
    }
    
    private void drawDonutChart(Graphics2D g2, long fav, long resto, int width, int height) {
        double total = fav + resto;
        int angFav = (int) Math.round(fav / total * 360);
        int angResto = 360 - angFav;
        
        // Posición centrada
        int diameter = Math.min(width, height - 250);
        diameter = Math.min(diameter, 320);
        int x = (width - diameter) / 2;
        int y = 190;
        
        // Sombra exterior
        g2.setColor(new Color(0, 0, 0, 30));
        g2.fillOval(x + 8, y + 8, diameter, diameter);
        
        // Gradiente para favoritos
        GradientPaint gpFav = new GradientPaint(
            x, y, FAV_COLOR,
            x + diameter, y + diameter, FAV_COLOR_LIGHT
        );
        g2.setPaint(gpFav);
        g2.fillArc(x, y, diameter, diameter, 90, -angFav);
        
        // Gradiente para otros
        GradientPaint gpOther = new GradientPaint(
            x, y, OTHER_COLOR,
            x + diameter, y + diameter, OTHER_COLOR_LIGHT
        );
        g2.setPaint(gpOther);
        g2.fillArc(x, y, diameter, diameter, 90 - angFav, -angResto);
        
        // Círculo interior para efecto dona
        int innerDiameter = (int) (diameter * 0.6);
        int innerX = x + (diameter - innerDiameter) / 2;
        int innerY = y + (diameter - innerDiameter) / 2;
        
        g2.setColor(BG_DARK);
        g2.fillOval(innerX, innerY, innerDiameter, innerDiameter);
        
        // Porcentaje en el centro
        g2.setFont(new Font("Segoe UI", Font.BOLD, 36));
        g2.setColor(TEXT_PRIMARY);
        String percent = String.format("%.1f%%", (fav / total) * 100);
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(percent);
        int textHeight = fm.getAscent();
        g2.drawString(percent, x + (diameter - textWidth) / 2, 
                     y + (diameter + textHeight) / 2 - 10);
        
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        g2.setColor(TEXT_SECONDARY);
        String favLabel = msg.get("stats.favorites.short", "Favoritos");
        textWidth = g2.getFontMetrics().stringWidth(favLabel);
        g2.drawString(favLabel, x + (diameter - textWidth) / 2, 
                     y + diameter / 2 + 20);
    }
    
    private void drawModernLegend(Graphics2D g2, long fav, long resto, int width, int height) {
        int legendY = height - 100;
        int centerX = width / 2;
        
        // Tarjeta de leyenda
        int cardWidth = 400;
        int cardHeight = 70;
        int cardX = centerX - cardWidth / 2;
        
        g2.setColor(CARD_BG);
        g2.fillRoundRect(cardX, legendY, cardWidth, cardHeight, 15, 15);
        
        g2.setColor(ACCENT_LINE);
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(cardX, legendY, cardWidth, cardHeight, 15, 15);
        
        // Item Favoritos
        int item1X = cardX + 30;
        g2.setColor(FAV_COLOR);
        g2.fillRoundRect(item1X, legendY + 20, 25, 25, 8, 8);
        
        g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g2.setColor(TEXT_PRIMARY);
        g2.drawString(msg.get("stats.favorites", fav), item1X + 35, legendY + 38);
        
        // Item Otros
        int item2X = cardX + 220;
        g2.setColor(OTHER_COLOR);
        g2.fillRoundRect(item2X, legendY + 20, 25, 25, 8, 8);
        
        g2.setColor(TEXT_PRIMARY);
        g2.drawString(msg.get("stats.others", resto), item2X + 35, legendY + 38);
    }
    
    private void drawEmptyState(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        g2.setColor(TEXT_SECONDARY);
        String emptyMsg = msg.get("stats.empty", "No hay datos para mostrar");
        FontMetrics fm = g2.getFontMetrics();
        int msgWidth = fm.stringWidth(emptyMsg);
        g2.drawString(emptyMsg, (width - msgWidth) / 2, height / 2);
    }
}