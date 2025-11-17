package vista;
import javax.swing.*;
import javax.swing.table.TableRowSorter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.border.EmptyBorder;
import controlador.MainController;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import modelo.ContactoTableModel;
import modelo.Persona;
import i18n.Messages;

/**
 * Panel de contactos mejorado con indicador de búsqueda en tiempo real
 * @author jorge
 */
public class PanelContactos extends JPanel {
    private JTextField txtBuscar;
    private JTable tabla;
    private JButton btnNuevo, btnExportar;
    private JProgressBar barraProgreso;
    private ContactoTableModel modelo;
    private TableRowSorter<ContactoTableModel> sorter;
    
    // Componentes para indicador de búsqueda
    private JLabel lblBuscando;
    private JLabel lblResultados;
    private Timer spinnerTimer;
    private int spinnerAngle = 0;
    
    // Colores modernos para modo dark
    private static final Color BG_DARK = new Color(18, 18, 18);
    private static final Color CARD_BG = new Color(30, 30, 30);
    private static final Color HOVER_BG = new Color(45, 45, 45);
    private static final Color SELECTED_BG = new Color(59, 130, 246, 40);
    private static final Color BORDER_COLOR = new Color(55, 65, 81);
    private static final Color TEXT_PRIMARY = new Color(243, 244, 246);
    private static final Color TEXT_SECONDARY = new Color(156, 163, 175);
    private static final Color ACCENT_BLUE = new Color(59, 130, 246);
    private static final Color ACCENT_GREEN = new Color(34, 197, 94);
    
    public PanelContactos(MainController controller, Messages msg) {
        setLayout(new BorderLayout(0, 15));
        setBackground(BG_DARK);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Norte: barra de herramientas moderna
        JPanel norte = createModernToolbar(msg);
        add(norte, BorderLayout.NORTH);
        
        // Centro: tabla estilizada
        modelo = new ContactoTableModel(controller.getTodos(), msg);
        tabla = createStyledTable();
        sorter = new TableRowSorter<>(modelo);
        tabla.setRowSorter(sorter);
        
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scroll.getViewport().setBackground(CARD_BG);
        add(scroll, BorderLayout.CENTER);
        
        // Sur: barra de progreso moderna
        barraProgreso = createModernProgressBar();
        add(barraProgreso, BorderLayout.SOUTH);
        
        // Conectar eventos al controlador
        controller.bindPanelContactos(this);
        
        // Inicializar timer para animación del spinner
        spinnerTimer = new Timer(50, e -> {
            spinnerAngle = (spinnerAngle + 15) % 360;
            lblBuscando.repaint();
        });
    }
    
    private JPanel createModernToolbar(Messages msg) {
        JPanel toolbar = new JPanel(new BorderLayout(15, 0));
        toolbar.setBackground(BG_DARK);
        toolbar.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        // Panel izquierdo: botones de acción
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setBackground(BG_DARK);
        
        btnNuevo = createModernButton(msg.get("btn.new"), ACCENT_BLUE);
        btnExportar = createModernButton(msg.get("btn.export"), ACCENT_GREEN);
        
        leftPanel.add(btnNuevo);
        leftPanel.add(btnExportar);
        
        // Panel derecho: búsqueda con indicadores
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(BG_DARK);
        
        JLabel lblBuscar = new JLabel("🔍 " + msg.get("label.search"));
        lblBuscar.setForeground(TEXT_SECONDARY);
        lblBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        txtBuscar = createModernTextField();
        
        // Indicador de búsqueda activa (spinner animado)
        lblBuscando = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (isVisible()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                                       RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    int size = 16;
                    int x = (getWidth() - size) / 2;
                    int y = (getHeight() - size) / 2;
                    
                    // Dibujar spinner circular
                    g2.setColor(ACCENT_BLUE);
                    g2.setStroke(new java.awt.BasicStroke(2.5f));
                    Arc2D.Float arc = new Arc2D.Float(x, y, size, size, 
                                                      spinnerAngle, 270, Arc2D.OPEN);
                    g2.draw(arc);
                }
            }
        };
        lblBuscando.setPreferredSize(new Dimension(24, 24));
        lblBuscando.setVisible(false);
        
        // Etiqueta de resultados
        lblResultados = new JLabel();
        lblResultados.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblResultados.setForeground(TEXT_SECONDARY);
        lblResultados.setVisible(false);
        
        rightPanel.add(lblBuscar);
        rightPanel.add(txtBuscar);
        rightPanel.add(lblBuscando);
        rightPanel.add(lblResultados);
        
        toolbar.add(leftPanel, BorderLayout.WEST);
        toolbar.add(rightPanel, BorderLayout.EAST);
        
        return toolbar;
    }
    
    /**
     * Muestra u oculta el indicador de búsqueda activa
     */
    public void mostrarIndicadorBusqueda(boolean mostrar) {
        lblBuscando.setVisible(mostrar);
        if (mostrar) {
            spinnerTimer.start();
            lblResultados.setVisible(false);
        } else {
            spinnerTimer.stop();
        }
    }
    
    /**
     * Actualiza el contador de resultados de búsqueda
     */
    public void actualizarContadorResultados(int cantidad) {
        if (txtBuscar.getText().isEmpty()) {
            lblResultados.setVisible(false);
        } else {
            lblResultados.setText(cantidad + " resultado" + (cantidad != 1 ? "s" : ""));
            lblResultados.setVisible(true);
        }
    }
    
    private JButton createModernButton(String text, Color accentColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(accentColor);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(120, 38));
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
        // Efecto hover
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(accentColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(accentColor);
            }
        });
        
        return btn;
    }
    
    private JTextField createModernTextField() {
        JTextField txt = new JTextField(25);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setBackground(CARD_BG);
        txt.setForeground(TEXT_PRIMARY);
        txt.setCaretColor(TEXT_PRIMARY);
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        txt.setPreferredSize(new Dimension(250, 38));
        
        return txt;
    }
    
    private JTable createStyledTable() {
        JTable table = new JTable(modelo);
        
        // Configuración general
        table.setBackground(CARD_BG);
        table.setForeground(TEXT_PRIMARY);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(SELECTED_BG);
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setRowHeight(45);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Header estilizado
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(24, 24, 27));
        header.setForeground(TEXT_PRIMARY);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_BLUE));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 45));
        
        // Renderer personalizado para celdas
        table.setDefaultRenderer(Object.class, new ModernTableCellRenderer());
        
        // Renderer especial para la columna de favoritos
        table.setDefaultRenderer(Boolean.class, new FavoriteRenderer());
        
        return table;
    }
    
    private JProgressBar createModernProgressBar() {
        JProgressBar progress = new JProgressBar();
        progress.setStringPainted(true);
        progress.setVisible(false);
        progress.setBackground(CARD_BG);
        progress.setForeground(ACCENT_BLUE);
        progress.setBorderPainted(false);
        progress.setPreferredSize(new Dimension(progress.getPreferredSize().width, 8));
        progress.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        return progress;
    }
    
    // Renderer moderno para celdas normales
    private class ModernTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, 
                    isSelected, hasFocus, row, column);
            
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setBorder(new EmptyBorder(5, 12, 5, 12));
            
            if (isSelected) {
                setBackground(SELECTED_BG);
                setForeground(TEXT_PRIMARY);
            } else {
                // Alternar colores de filas
                if (row % 2 == 0) {
                    setBackground(CARD_BG);
                } else {
                    setBackground(new Color(35, 35, 35));
                }
                setForeground(TEXT_PRIMARY);
            }
            
            // Color especial para categoría
            if (column == 3) { // Asumiendo que la columna 3 es categoría
                setForeground(TEXT_SECONDARY);
                setFont(new Font("Segoe UI", Font.ITALIC, 13));
            }
            
            return c;
        }
    }
    
    // Renderer especial para la columna de favoritos
    private class FavoriteRenderer extends DefaultTableCellRenderer {
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, 
                    isSelected, hasFocus, row, column);
            
            setHorizontalAlignment(CENTER);
            setBorder(new EmptyBorder(5, 12, 5, 12));
            setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
            
            if (isSelected) {
                setBackground(SELECTED_BG);
            } else {
                if (row % 2 == 0) {
                    setBackground(CARD_BG);
                } else {
                    setBackground(new Color(35, 35, 35));
                }
            }
            
            // Mostrar estrella emoji o guión
            if (value != null && (Boolean) value) {
                setText("⭐"); // Estrella para favorito
                setForeground(new Color(234, 179, 8)); // Amarillo dorado
            } else {
                setText("—"); // Guión para no favorito
                setForeground(TEXT_SECONDARY);
            }
            
            setIcon(null); // No usar iconos personalizados
            
            return c;
        }
    }
    
    // Getters
    public ContactoTableModel getModelo() {
        return modelo;
    }
    
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