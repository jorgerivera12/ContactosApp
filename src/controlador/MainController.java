package controlador;

import dao.PersonaDAO;
import vista.VentanaPrincipal;
import vista.PanelContactos;
import vista.DialogoContacto;
import modelo.ContactoTableModel;
import modelo.Persona;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.RowFilter;
import javax.swing.SwingWorker;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.text.MessageFormat;
import i18n.Messages;

/**
 * Controlador principal con diálogos modernos
 * @author jorge
 */
public class MainController {

    private VentanaPrincipal view;
    private PersonaDAO dao;
    private final Messages msg;
    private List<Persona> todos;

    public MainController(VentanaPrincipal v, Messages msg) {
        this.view = v;
        this.msg = msg;
        this.dao = new PersonaDAO(new Persona());
    }

    public List<Persona> getTodos() {
        try {
            todos = dao.leerArchivo();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(view,
                    MessageFormat.format(msg.get("load.error"), e.getMessage()),
                    "Error", JOptionPane.ERROR_MESSAGE);
            todos = new ArrayList<>();
        }
        return todos;
    }

    public void bindPanelContactos(PanelContactos pc) {
        // Botón nuevo contacto
        pc.getBtnNuevo().addActionListener(e -> crearContacto());
        
        // Botón exportar
        pc.getBtnExportar().addActionListener(e -> exportarCSV());
        
        // Filtro en tiempo real
        pc.getTxtBuscar().getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                filtrar(pc);
            }
            public void removeUpdate(DocumentEvent e) {
                filtrar(pc);
            }
            public void changedUpdate(DocumentEvent e) {
            }
        });
        
        // Doble clic en tabla para editar
        pc.getTabla().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editarSeleccionado(pc);
                }
            }
        });
        
        // Menú contextual mejorado
        JPopupMenu menu = crearMenuContextual(pc);
        pc.getTabla().setComponentPopupMenu(menu);
    }

    private JPopupMenu crearMenuContextual(PanelContactos pc) {
        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(new java.awt.Color(30, 30, 30));
        menu.setBorder(BorderFactory.createLineBorder(new java.awt.Color(55, 65, 81), 1));
        
        JMenuItem mEditar = new JMenuItem("✏️ " + msg.get("menu.edit", "Editar"));
        estilizarMenuItem(mEditar);
        mEditar.addActionListener(e -> editarSeleccionado(pc));
        
        JMenuItem mEliminar = new JMenuItem("🗑️ " + msg.get("menu.delete", "Eliminar"));
        estilizarMenuItem(mEliminar);
        mEliminar.addActionListener(e -> eliminarSeleccionado(pc));
        
        JMenuItem mFavorito = new JMenuItem("⭐ " + msg.get("menu.toggle.favorite", "Alternar favorito"));
        estilizarMenuItem(mFavorito);
        mFavorito.addActionListener(e -> toggleFavorito(pc));
        
        menu.add(mEditar);
        menu.addSeparator();
        menu.add(mFavorito);
        menu.addSeparator();
        menu.add(mEliminar);
        
        return menu;
    }
    
    private void estilizarMenuItem(JMenuItem item) {
        item.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        item.setForeground(new java.awt.Color(243, 244, 246));
        item.setBackground(new java.awt.Color(30, 30, 30));
        item.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        item.setOpaque(true);
        
        item.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                item.setBackground(new java.awt.Color(45, 45, 45));
            }
            public void mouseExited(MouseEvent e) {
                item.setBackground(new java.awt.Color(30, 30, 30));
            }
        });
    }

    private void filtrar(PanelContactos pc) {
        String txt = pc.getTxtBuscar().getText();
        pc.getSorter().setRowFilter(RowFilter.regexFilter("(?i)" + txt));
    }

    private void crearContacto() {
        DialogoContacto dialogo = new DialogoContacto(view, msg, true);
        dialogo.setVisible(true);
        
        if (dialogo.isConfirmado()) {
            Persona p = dialogo.getContacto();
            new PersonaDAO(p).escribirArchivo();
            refrescarDatos();
            
            // Notificación de éxito
            mostrarNotificacion(
                msg.get("success.contact.created", "Contacto creado exitosamente"),
                true
            );
        }
    }

    private void editarSeleccionado(PanelContactos pc) {
        int rowView = pc.getTabla().getSelectedRow();
        if (rowView < 0) {
            mostrarNotificacion(
                msg.get("error.no.selection", "Por favor seleccione un contacto"),
                false
            );
            return;
        }
        
        int rowModel = pc.getTabla().convertRowIndexToModel(rowView);
        Persona original = ((ContactoTableModel) pc.getTabla().getModel()).getContactoAt(rowModel);

        DialogoContacto dialogo = new DialogoContacto(view, msg, false, original);
        dialogo.setVisible(true);

        if (dialogo.isConfirmado()) {
            List<Persona> lista = getTodos();
            lista.set(rowModel, dialogo.getContacto());
            
            try {
                dao.actualizarContactos(lista);
                refrescarDatos();
                mostrarNotificacion(
                    msg.get("success.contact.updated", "Contacto actualizado exitosamente"),
                    true
                );
            } catch (IOException ex) {
                mostrarError(msg.get("update.error", ex.getMessage()));
            }
        }
    }

    private void eliminarSeleccionado(PanelContactos pc) {
        int rowView = pc.getTabla().getSelectedRow();
        if (rowView < 0) {
            mostrarNotificacion(
                msg.get("error.no.selection", "Por favor seleccione un contacto"),
                false
            );
            return;
        }
        
        int rowModel = pc.getTabla().convertRowIndexToModel(rowView);
        Persona p = ((ContactoTableModel)pc.getTabla().getModel()).getContactoAt(rowModel);

        // Diálogo de confirmación estilizado
        int conf = mostrarConfirmacionEliminar(p.getNombre());
        if (conf != JOptionPane.YES_OPTION) return;

        List<Persona> lista = getTodos();
        lista.removeIf(x ->
            x.getNombre().equals(p.getNombre()) &&
            x.getEmail().equals(p.getEmail()) &&
            x.getTelefono().equals(p.getTelefono())
        );
        
        try {
            dao.actualizarContactos(lista);
            refrescarDatos();
            mostrarNotificacion(
                msg.get("success.contact.deleted", "Contacto eliminado exitosamente"),
                true
            );
        } catch (IOException ex) {
            mostrarError(msg.get("update.error", ex.getMessage()));
        }
    }
    
    private void toggleFavorito(PanelContactos pc) {
        int rowView = pc.getTabla().getSelectedRow();
        if (rowView < 0) {
            mostrarNotificacion(
                msg.get("error.no.selection", "Por favor seleccione un contacto"),
                false
            );
            return;
        }
        
        int rowModel = pc.getTabla().convertRowIndexToModel(rowView);
        List<Persona> lista = getTodos();
        Persona p = lista.get(rowModel);
        
        // Alternar favorito
        Persona actualizado = new Persona(
            p.getNombre(),
            p.getTelefono(),
            p.getEmail(),
            p.getCategoria(),
            !p.isFavorito()
        );
        
        lista.set(rowModel, actualizado);
        
        try {
            dao.actualizarContactos(lista);
            refrescarDatos();
            mostrarNotificacion(
                actualizado.isFavorito() ? 
                    msg.get("success.favorite.added", "Agregado a favoritos") :
                    msg.get("success.favorite.removed", "Removido de favoritos"),
                true
            );
        } catch (IOException ex) {
            mostrarError(msg.get("update.error", ex.getMessage()));
        }
    }

    private int mostrarConfirmacionEliminar(String nombre) {
        UIManager.put("OptionPane.background", new java.awt.Color(30, 30, 30));
        UIManager.put("Panel.background", new java.awt.Color(30, 30, 30));
        UIManager.put("OptionPane.messageForeground", new java.awt.Color(243, 244, 246));
        
        String mensaje = msg.get("confirm.delete.text", nombre);
        String[] opciones = {
            msg.get("btn.delete", "Eliminar"),
            msg.get("btn.cancel", "Cancelar")
        };
        
        return JOptionPane.showOptionDialog(
            view,
            mensaje,
            msg.get("confirm.delete.title"),
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE,
            null,
            opciones,
            opciones[1]
        );
    }
    
    private void mostrarNotificacion(String mensaje, boolean esExito) {
        JOptionPane pane = new JOptionPane(
            mensaje,
            esExito ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE
        );
        
        JDialog dialog = pane.createDialog(view, esExito ? "Éxito" : "Atención");
        dialog.setModal(false);
        dialog.setVisible(true);
        
        // Auto-cerrar después de 2 segundos
        Timer timer = new Timer(2000, e -> dialog.dispose());
        timer.setRepeats(false);
        timer.start();
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(
            view,
            mensaje,
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    private void refrescarDatos() {
        List<Persona> lista = getTodos();
        view.getPanelContactos().getModelo().setDatos(lista);
        view.getPanelEstadisticas().repaint();
    }

    private void exportarCSV() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(msg.get("export.dialog.title"));
        
        if (fc.showSaveDialog(view) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            
            // Asegurar extensión .csv
            if (!f.getName().toLowerCase().endsWith(".csv")) {
                f = new File(f.getAbsolutePath() + ".csv");
            }
            
            try (PrintWriter pw = new PrintWriter(f, StandardCharsets.UTF_8)) {
                pw.println("Nombre;Teléfono;Email;Categoría;Favorito");
                for (Persona p : getTodos()) {
                    pw.println(p.datosContacto());
                }
                
                mostrarNotificacion(
                    msg.get("export.ok", f.getAbsolutePath()),
                    true
                );
            } catch (Exception ex) {
                mostrarError(msg.get("export.error", ex.getMessage()));
            }
        }
    }

    // Carga masiva con progreso (ejemplo)
    private void cargaMasivaConProgreso(PanelContactos pc, List<Persona> lista) {
        pc.getBarraProgreso().setMaximum(lista.size());
        pc.getBarraProgreso().setVisible(true);
        
        new SwingWorker<Void, Integer>() {
            protected Void doInBackground() {
                for (int i = 0; i < lista.size(); i++) {
                    // Procesar lista.get(i)...
                    try {
                        Thread.sleep(50); // Simular procesamiento
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    publish(i + 1);
                }
                return null;
            }

            protected void process(List<Integer> chunks) {
                int progreso = chunks.get(chunks.size() - 1);
                pc.getBarraProgreso().setValue(progreso);
                pc.getBarraProgreso().setString(
                    String.format("Procesando... %d/%d", progreso, lista.size())
                );
            }

            protected void done() {
                pc.getBarraProgreso().setVisible(false);
                refrescarDatos();
                mostrarNotificacion(
                    msg.get("success.bulk.import", "Importación completada"),
                    true
                );
            }
        }.execute();
    }
}