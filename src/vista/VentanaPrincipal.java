package vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Locale;
import i18n.Messages;
import controlador.MainController;

/**
 * Ventana principal mejorada para modo dark
 *
 * @author jorge
 */
public class VentanaPrincipal extends JFrame {

    private JTabbedPane tabs;
    private PanelContactos panelContactos;
    private PanelEstadisticas panelEstadisticas;
    private MainController controller;
    private final Messages msg;
    private final Locale locale;

    // Colores modernos para modo dark
    private static final Color BG_DARK = new Color(18, 18, 18);
    private static final Color TOPBAR_BG = new Color(24, 24, 27);
    private static final Color CARD_BG = new Color(30, 30, 30);
    private static final Color BORDER_COLOR = new Color(55, 65, 81);
    private static final Color TEXT_PRIMARY = new Color(243, 244, 246);
    private static final Color TEXT_SECONDARY = new Color(156, 163, 175);
    private static final Color ACCENT_BLUE = new Color(59, 130, 246);
    private static final Color TAB_SELECTED = new Color(45, 45, 48);

    public VentanaPrincipal(Locale locale) {
        this.locale = locale;
        this.msg = new Messages(locale);

        setTitle(msg.get("app.title"));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));

        // Fondo oscuro principal
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 0));

        controller = new MainController(this, msg);

        // Barra superior moderna
        add(buildModernTopBar(), BorderLayout.NORTH);

        // Componentes principales
        initComponents();

        // Icono de la aplicación (opcional)
        try {
            setIconImage(createAppIcon());
        } catch (Exception e) {
            // Si falla, continuar sin icono
        }
    }

    private JPanel buildModernTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(TOPBAR_BG);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                new EmptyBorder(12, 20, 12, 20)
        ));

        // Panel izquierdo: título y versión
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setBackground(TOPBAR_BG);

        JLabel titleLabel = new JLabel(msg.get("app.title"));
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel versionLabel = new JLabel("v1.0");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        versionLabel.setForeground(TEXT_SECONDARY);
        versionLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(2, 8, 2, 8)
        ));

        leftPanel.add(titleLabel);
        leftPanel.add(versionLabel);

        // Panel derecho: selector de idioma
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(TOPBAR_BG);

        JLabel langIcon = new JLabel("🌐");
        langIcon.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JLabel langLabel = new JLabel(msg.get("menu.lang") + ":");
        langLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        langLabel.setForeground(TEXT_SECONDARY);

        JComboBox<String> langCombo = createModernComboBox();

        rightPanel.add(langIcon);
        rightPanel.add(langLabel);
        rightPanel.add(langCombo);

        topBar.add(leftPanel, BorderLayout.WEST);
        topBar.add(rightPanel, BorderLayout.EAST);

        return topBar;
    }

    private JComboBox<String> createModernComboBox() {
        String[] idiomas = {"🇪🇸 Español", "🇬🇧 English", "🇧🇷 Português"};
        JComboBox<String> combo = new JComboBox<>(idiomas);

        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setBackground(CARD_BG);
        combo.setForeground(TEXT_PRIMARY);
        combo.setPreferredSize(new Dimension(160, 34));
        combo.setMaximumSize(new Dimension(160, 34));
        combo.setFocusable(false);
        combo.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Estilo personalizado para el ComboBox
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(2, 10, 2, 10)
        ));

        // Selección actual basada en locale
        Locale cur = Locale.getDefault();
        combo.setSelectedIndex(
                "es".equals(cur.getLanguage()) ? 0
                : "en".equals(cur.getLanguage()) ? 1 : 2
        );

        // Listener para cambio de idioma
        combo.addActionListener(e -> {
            int idx = combo.getSelectedIndex();
            Locale target = (idx == 0) ? new Locale("es")
                    : (idx == 1) ? Locale.ENGLISH
                            : new Locale("pt");

            // Confirmación antes de cambiar
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    msg.get("confirm.change.language", "¿Desea cambiar el idioma? La aplicación se reiniciará."),
                    msg.get("confirm.title", "Confirmar"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                util.LocaleUtils.switchLocale(
                        VentanaPrincipal.this,
                        target,
                        new Runnable() {
                    @Override
                    public void run() {
                        new VentanaPrincipal(target).setVisible(true);
                    }
                }
                );
            } else {
                // Revertir selección si cancela
                combo.setSelectedIndex(
                        "es".equals(cur.getLanguage()) ? 0
                        : "en".equals(cur.getLanguage()) ? 1 : 2
                );
            }
        });

        return combo;
    }

    private void initComponents() {
        tabs = new JTabbedPane();

        // Estilo moderno para las pestañas
        tabs.setBackground(BG_DARK);
        tabs.setForeground(TEXT_PRIMARY);
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Personalizar UI de las pestañas
        UIManager.put("TabbedPane.selected", TAB_SELECTED);
        UIManager.put("TabbedPane.background", BG_DARK);
        UIManager.put("TabbedPane.foreground", TEXT_PRIMARY);
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
        UIManager.put("TabbedPane.tabAreaInsets", new Insets(10, 20, 0, 20));
        UIManager.put("TabbedPane.selectedForeground", ACCENT_BLUE);
        UIManager.put("TabbedPane.focus", ACCENT_BLUE);

        tabs.setBorder(null);

        // Crear paneles
        panelContactos = new PanelContactos(controller, msg);
        panelEstadisticas = new PanelEstadisticas(controller, msg);

        // Agregar pestañas con iconos
        tabs.addTab("  " + msg.get("tab.contacts") + "  ",
                createTabIcon("👥"), panelContactos);
        tabs.addTab("  " + msg.get("tab.stats") + "  ",
                createTabIcon("📊"), panelEstadisticas);

        // Listener para repintar estadísticas al cambiar de pestaña
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedIndex() == 1) {
                panelEstadisticas.repaint();
            }
        });

        getContentPane().add(tabs, BorderLayout.CENTER);

        // Barra de estado (opcional)
        add(createStatusBar(), BorderLayout.SOUTH);
    }

    private Icon createTabIcon(String emoji) {
        return new Icon() {
            public int getIconWidth() {
                return 20;
            }

            public int getIconHeight() {
                return 20;
            }

            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
                g2.setColor(TEXT_SECONDARY);
                g2.drawString(emoji, x, y + 15);
            }
        };
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(TOPBAR_BG);
        statusBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
                new EmptyBorder(8, 20, 8, 20)
        ));

        JLabel statusLabel = new JLabel("Jorge Rivera");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(TEXT_SECONDARY);

        JLabel copyrightLabel = new JLabel("© 2025 Gestión de Contactos");
        copyrightLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        copyrightLabel.setForeground(TEXT_SECONDARY);

        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(copyrightLabel, BorderLayout.EAST);

        return statusBar;
    }

    private Image createAppIcon() {
        // Crear un icono simple de la aplicación
        int size = 64;
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(
                size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Fondo circular azul
        g2.setColor(ACCENT_BLUE);
        g2.fillOval(4, 4, size - 8, size - 8);

        // Letra C blanca
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 36));
        FontMetrics fm = g2.getFontMetrics();
        String letter = "C";
        int x = (size - fm.stringWidth(letter)) / 2;
        int y = (size - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(letter, x, y);

        g2.dispose();
        return img;
    }

    // Getters
    public PanelContactos getPanelContactos() {
        return panelContactos;
    }

    public PanelEstadisticas getPanelEstadisticas() {
        return panelEstadisticas;
    }

    public Messages getMessages() {
        return msg;
    }

    public MainController getController() {
        return controller;
    }
}
