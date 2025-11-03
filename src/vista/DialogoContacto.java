package vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import modelo.Persona;
import i18n.Messages;

/**
 * Diálogo moderno para crear/editar contactos en modo dark
 * @author jorge
 */
public class DialogoContacto extends JDialog {
    private JTextField txtNombre;
    private JTextField txtTelefono;
    private JTextField txtEmail;
    private JComboBox<String> cmbCategoria;
    private JCheckBox chkFavorito;
    private boolean confirmado = false;
    
    // Colores modernos para modo dark
    private static final Color BG_DARK = new Color(24, 24, 27);
    private static final Color CARD_BG = new Color(39, 39, 42);
    private static final Color INPUT_BG = new Color(30, 30, 30);
    private static final Color BORDER_COLOR = new Color(55, 65, 81);
    private static final Color TEXT_PRIMARY = new Color(243, 244, 246);
    private static final Color TEXT_SECONDARY = new Color(156, 163, 175);
    private static final Color ACCENT_BLUE = new Color(59, 130, 246);
    private static final Color ACCENT_GREEN = new Color(34, 197, 94);
    private static final Color ACCENT_RED = new Color(239, 68, 68);
    private static final Color LABEL_BG = new Color(51, 51, 55);
    
    public DialogoContacto(Frame parent, Messages msg, boolean esNuevo) {
        this(parent, msg, esNuevo, null);
    }
    
    public DialogoContacto(Frame parent, Messages msg, boolean esNuevo, Persona contacto) {
        super(parent, esNuevo ? msg.get("dialog.new.title") : msg.get("dialog.edit.title"), true);
        
        setSize(400, 680);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(BG_DARK);
        
        initComponents(msg, esNuevo, contacto);
    }
    
    private void initComponents(Messages msg, boolean esNuevo, Persona contacto) {
        
        
        setLayout(new BorderLayout(0, 0));
        
        // Panel principal con padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BG_DARK);
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        // Header con icono y título
        mainPanel.add(createHeader(msg, esNuevo));
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Campos del formulario
        txtNombre = createModernTextField();
        txtTelefono = createModernTextField();
        txtEmail = createModernTextField();
        cmbCategoria = createModernComboBox(new String[]{
            msg.get("category.family"),
            msg.get("category.friends"),
            msg.get("category.work")
        });
        chkFavorito = createModernCheckBox(msg.get("form.favorite"));
        
        // Si es edición, precargar datos
        if (contacto != null) {
            txtNombre.setText(contacto.getNombre());
            txtTelefono.setText(contacto.getTelefono());
            txtEmail.setText(contacto.getEmail());
            cmbCategoria.setSelectedItem(contacto.getCategoria());
            chkFavorito.setSelected(contacto.isFavorito());
        }
        
        // Agregar campos al formulario
       
        mainPanel.add(createFieldGroup(msg.get("form.name"), txtNombre, "👤👤"));
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(createFieldGroup(msg.get("form.phone"), txtTelefono, "📱"));
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(createFieldGroup(msg.get("form.email"), txtEmail, "📧"));
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(createFieldGroup(msg.get("form.category"), cmbCategoria, "️"));
        mainPanel.add(Box.createVerticalStrut(18));
       
        // Panel de favorito con estilo especial
        mainPanel.add(createFavoritePanel(chkFavorito));
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Botones de acción
        mainPanel.add(createButtonPanel(msg, esNuevo));
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createHeader(Messages msg, boolean esNuevo) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_DARK);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        
        // Icono grande a la izquierda
        JLabel iconLabel = new JLabel(esNuevo ? "➕" : "✏️");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 38));
        iconLabel.setBorder(new EmptyBorder(0, 0, 0, 15));
        
        // Panel de texto
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(BG_DARK);
        
        JLabel titleLabel = new JLabel(esNuevo ? msg.get("dialog.new.title") : msg.get("dialog.edit.title"));
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_PRIMARY);
        
        JLabel subtitleLabel = new JLabel(esNuevo ? 
            "Complete los datos del nuevo contacto" : 
            "Modifique los datos del contacto");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setBorder(new EmptyBorder(4, 0, 0, 0));
        
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);
        
        header.add(iconLabel, BorderLayout.WEST);
        header.add(textPanel, BorderLayout.CENTER);
        
        return header;
    }
    
    private JPanel createFieldGroup(String labelText, JComponent field,String emoji) {
        JPanel group = new JPanel();
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setBackground(BG_DARK);
        group.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80)); // permite expansión horizontal
        group.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Label con emoji
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        labelPanel.setBackground(BG_DARK);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_PRIMARY);
        
        labelPanel.add(label);
        labelPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        field.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        group.add(labelPanel);
        group.add(Box.createVerticalStrut(6));
        group.add(field);
        
        return group;
    }
    
    private JPanel createFavoritePanel(JCheckBox checkBox) {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(LABEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(15, 20, 15, 20)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setBackground(LABEL_BG);
        
        JLabel starLabel = new JLabel("⭐");
        starLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(LABEL_BG);
        
        JLabel titleLabel = new JLabel("Marcar");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_PRIMARY);
      
    
        textPanel.add(titleLabel);
        
        leftPanel.add(starLabel);
        leftPanel.add(textPanel);
        
        checkBox.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(checkBox, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createButtonPanel(Messages msg, boolean esNuevo) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(BG_DARK);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        
        JButton btnCancelar = createStyledButton(
            msg.get("btn.cancel", "Cancelar"), 
            ACCENT_RED, 
            false
        );
        JButton btnGuardar = createStyledButton(
            esNuevo ? msg.get("btn.save", "Guardar") : msg.get("btn.update", "Actualizar"), 
            ACCENT_GREEN, 
            true
        );
        
        btnCancelar.addActionListener(e -> {
            confirmado = false;
            dispose();
        });
        
        btnGuardar.addActionListener(e -> {
            if (validarCampos(msg)) {
                confirmado = true;
                dispose();
            }
        });
        
        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnGuardar);
        
        return buttonPanel;
    }
    
    private JTextField createModernTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(INPUT_BG);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(10, 15, 10, 15)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 42));
        
        // Efecto focus
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT_BLUE, 2),
                    new EmptyBorder(10, 15, 10, 15)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    new EmptyBorder(10, 15, 10, 15)
                ));
            }
        });
        
        return field;
    }
    
    private JComboBox<String> createModernComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBackground(INPUT_BG);
        combo.setForeground(TEXT_PRIMARY);
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        combo.setPreferredSize(new Dimension(combo.getPreferredSize().width, 42));
        combo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return combo;
    }
    
    private JCheckBox createModernCheckBox(String text) {
        JCheckBox checkBox = new JCheckBox(text);
        
        checkBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        checkBox.setBackground(CARD_BG);
        checkBox.setForeground(TEXT_PRIMARY);
        checkBox.setPreferredSize(new Dimension(160, 34));
        checkBox.setMaximumSize(new Dimension(160, 34));
        checkBox.setFocusable(false);
        checkBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        checkBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(2, 10, 2, 10)
        ));
        
        // Personalizar el icono del checkbox
        checkBox.setIcon(createCheckBoxIcon(false));
        checkBox.setSelectedIcon(createCheckBoxIcon(true));
        
        return checkBox;
    }
    
    private Icon createCheckBoxIcon(boolean selected) {
        return new Icon() {
            public int getIconWidth() { return 24; }
            public int getIconHeight() { return 24; }
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                                   RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (selected) {
                    g2.setColor(ACCENT_BLUE);
                    g2.fillRoundRect(x, y, 24, 24, 6, 6);
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(2.5f));
                    g2.drawLine(x + 6, y + 12, x + 10, y + 16);
                    g2.drawLine(x + 10, y + 16, x + 18, y + 7);
                } else {
                    g2.setColor(BORDER_COLOR);
                    g2.setStroke(new BasicStroke(2));
                    g2.drawRoundRect(x + 1, y + 1, 22, 22, 6, 6);
                }
            }
        };
    }
    
    private JButton createStyledButton(String text, Color color, boolean isPrimary) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(isPrimary ? Color.WHITE : color);
        button.setBackground(isPrimary ? color : BG_DARK);
        button.setBorderPainted(!isPrimary);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(130, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if (!isPrimary) {
            button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                new EmptyBorder(8, 20, 8, 20)
            ));
        } else {
            button.setBorder(new EmptyBorder(8, 20, 8, 20));
        }
        
        // Efecto hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (isPrimary) {
                    button.setBackground(color.brighter());
                } else {
                    button.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(isPrimary ? color : BG_DARK);
            }
        });
        
        return button;
    }
    
    private boolean validarCampos(Messages msg) {
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarError(msg.get("error.name.empty", "El nombre no puede estar vacío"));
            txtNombre.requestFocus();
            return false;
        }
        if (txtTelefono.getText().trim().isEmpty()) {
            mostrarError(msg.get("error.phone.empty", "El teléfono no puede estar vacío"));
            txtTelefono.requestFocus();
            return false;
        }
        if (txtEmail.getText().trim().isEmpty()) {
            mostrarError(msg.get("error.email.empty", "El email no puede estar vacío"));
            txtEmail.requestFocus();
            return false;
        }
        // Validación básica de email
        if (!txtEmail.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            mostrarError(msg.get("error.email.invalid", "El email no es válido"));
            txtEmail.requestFocus();
            return false;
        }
        return true;
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(
            this, 
            mensaje, 
            "Error de validación", 
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    // Getters
    public boolean isConfirmado() {
        return confirmado;
    }
    
    public Persona getContacto() {
        return new Persona(
            txtNombre.getText().trim(),
            txtTelefono.getText().trim(),
            txtEmail.getText().trim(),
            (String) cmbCategoria.getSelectedItem(),
            chkFavorito.isSelected()
        );
    }
}