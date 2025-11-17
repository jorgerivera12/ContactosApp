package controlador;

import dao.PersonaDAO;
import vista.VentanaPrincipal;
import vista.PanelContactos;
import vista.DialogoContacto;
import modelo.ContactoTableModel;
import modelo.*;
import concurrencia.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.RowFilter;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.text.MessageFormat;
import i18n.Messages;

/**
 * Controlador principal con programación concurrente implementada
 * @author jorge
 */
public class MainController {

    private VentanaPrincipal view;
    private PersonaDAO dao;
    private final Messages msg;
    private List<Persona> todos;
    
    // ExecutorService para manejar tareas concurrentes
    private final ExecutorService executorService;
    
    // Timer para búsqueda con debounce
    private Timer searchTimer;

    public MainController(VentanaPrincipal v, Messages msg) {
        this.view = v;
        this.msg = msg;
        this.dao = new PersonaDAO(new Persona());
        this.executorService = Executors.newFixedThreadPool(4);
    }

    public List<Persona> getTodos() {
        try {
            todos = dao.leerArchivo();
        } catch (IOException e) {
            NotificationManager.showError(view,
                    MessageFormat.format(msg.get("load.error"), e.getMessage()));
            todos = new ArrayList<>();
        }
        return todos;
    }

    public void bindPanelContactos(PanelContactos pc) {
        // Botón nuevo contacto con validación concurrente
        pc.getBtnNuevo().addActionListener(e -> crearContactoConValidacion());
        
        // Botón exportar con thread en segundo plano
        pc.getBtnExportar().addActionListener(e -> exportarCSVConcurrente(pc));
        
        // Filtro en tiempo real con búsqueda concurrente
        pc.getTxtBuscar().getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                busquedaConcurrente(pc);
            }
            public void removeUpdate(DocumentEvent e) {
                busquedaConcurrente(pc);
            }
            public void changedUpdate(DocumentEvent e) {
            }
        });
        
        // Doble clic en tabla para editar con sincronización
        pc.getTabla().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editarSeleccionadoSincronizado(pc);
                }
            }
        });
        
        // Menú contextual
        JPopupMenu menu = crearMenuContextual(pc);
        pc.getTabla().setComponentPopupMenu(menu);
    }

    /**
     * REQUERIMIENTO 1: Validación de contactos en segundo plano
     */
    private void crearContactoConValidacion() {
        DialogoContacto dialogo = new DialogoContacto(view, msg, true);
        dialogo.setVisible(true);
        
        if (dialogo.isConfirmado()) {
            Persona nuevoContacto = dialogo.getContacto();
            
            // Mostrar indicador de validación
            JDialog loadingDialog = createLoadingDialog("Validando contacto...");
            loadingDialog.setVisible(true);
            
            // Validar en segundo plano usando ExecutorService
            ContactoValidator validator = new ContactoValidator(
                nuevoContacto, getTodos()
            );
            
            Future<ValidationResult> futureResult = executorService.submit(validator);
            
            // Procesar resultado en un thread separado
            new Thread(() -> {
                try {
                    ValidationResult result = futureResult.get();
                    
                    SwingUtilities.invokeLater(() -> {
                        loadingDialog.dispose();
                        
                        if (result.isValido()) {
                            // Guardar el contacto
                            new PersonaDAO(nuevoContacto).escribirArchivo();
                            refrescarDatos();
                            NotificationManager.showSuccess(view,
                                msg.get("success.contact.created", "Contacto creado exitosamente"));
                        } else {
                            // Mostrar error de validación
                            NotificationManager.showError(view, result.getMensaje());
                        }
                    });
                    
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        loadingDialog.dispose();
                        NotificationManager.showError(view, 
                            "Error en la validación: " + ex.getMessage());
                    });
                }
            }).start();
        }
    }

    /**
     * REQUERIMIENTO 2: Búsqueda de contactos en segundo plano
     */
    private void busquedaConcurrente(PanelContactos pc) {
        // Mostrar indicador de búsqueda inmediatamente
        pc.mostrarIndicadorBusqueda(true);
        
        // Cancelar búsqueda anterior si existe (debounce)
        if (searchTimer != null && searchTimer.isRunning()) {
            searchTimer.stop();
        }
        
        // Crear nuevo timer con delay de 300ms
        searchTimer = new Timer(300, e -> {
            String searchTerm = pc.getTxtBuscar().getText();
            
            // Ejecutar búsqueda en SwingWorker
            SearchWorker worker = new SearchWorker(
                searchTerm,
                getTodos(),
                new SearchWorker.SearchCallback() {
                    @Override
                    public void onSearchComplete(List<Persona> results) {
                        // Ocultar indicador de búsqueda
                        pc.mostrarIndicadorBusqueda(false);
                        
                        // Actualizar filtro con resultados
                        if (searchTerm.isEmpty()) {
                            pc.getSorter().setRowFilter(null);
                        } else {
                            pc.getSorter().setRowFilter(
                                RowFilter.regexFilter("(?i)" + searchTerm)
                            );
                        }
                        
                        // Actualizar contador de resultados
                        int resultCount = pc.getTabla().getRowCount();
                        pc.actualizarContadorResultados(resultCount);
                    }
                    
                    @Override
                    public void onSearchError(Exception ex) {
                        pc.mostrarIndicadorBusqueda(false);
                        NotificationManager.showError(view,
                            "Error en la búsqueda: " + ex.getMessage());
                    }
                }
            );
            
            worker.execute();
        });
        
        searchTimer.setRepeats(false);
        searchTimer.start();
    }

    /**
     * REQUERIMIENTO 3: Exportación con hilos múltiples
     */
    private void exportarCSVConcurrente(PanelContactos pc) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(msg.get("export.dialog.title"));
        
        if (fc.showSaveDialog(view) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            
            // Asegurar extensión .csv
            if (!f.getName().toLowerCase().endsWith(".csv")) {
                f = new File(f.getAbsolutePath() + ".csv");
            }
            
            final File archivo = f;
            
            // Mostrar barra de progreso
            pc.getBarraProgreso().setValue(0);
            pc.getBarraProgreso().setVisible(true);
            pc.getBarraProgreso().setStringPainted(true);
            
            // Crear worker para exportación
            ExportWorker worker = new ExportWorker(
                getTodos(),
                archivo,
                new ExportWorker.ExportCallback() {
                    @Override
                    public void onExportProgress(int progress) {
                        pc.getBarraProgreso().setValue(progress);
                        pc.getBarraProgreso().setString(
                            String.format("Exportando... %d%%", progress)
                        );
                    }
                    
                    @Override
                    public void onExportComplete(String filePath) {
                        pc.getBarraProgreso().setVisible(false);
                        NotificationManager.showSuccess(view,
                            msg.get("export.ok", filePath));
                    }
                    
                    @Override
                    public void onExportError(Exception e) {
                        pc.getBarraProgreso().setVisible(false);
                        NotificationManager.showError(view,
                            msg.get("export.error", e.getMessage()));
                    }
                }
            );
            
            worker.execute();
        }
    }

    /**
     * REQUERIMIENTO 5: Sincronización en la modificación de contactos
     */
    private void editarSeleccionadoSincronizado(PanelContactos pc) {
        int rowView = pc.getTabla().getSelectedRow();
        if (rowView < 0) {
            NotificationManager.showInfo(view,
                msg.get("error.no.selection", "Por favor seleccione un contacto"));
            return;
        }
        
        int rowModel = pc.getTabla().convertRowIndexToModel(rowView);
        Persona original = ((ContactoTableModel) pc.getTabla().getModel())
            .getContactoAt(rowModel);

        // Intentar adquirir el lock del contacto
        String userId = "USER_" + Thread.currentThread().getId();
        
        if (!ContactLockManager.tryLockContact(original, userId)) {
            NotificationManager.showError(view,
                "Este contacto está siendo editado por otro usuario. " +
                "Por favor, intente más tarde.");
            return;
        }

        try {
            DialogoContacto dialogo = new DialogoContacto(view, msg, false, original);
            dialogo.setVisible(true);

            if (dialogo.isConfirmado()) {
                Persona actualizado = dialogo.getContacto();
                
                // Operación sincronizada de actualización
                synchronized (this) {
                    List<Persona> lista = getTodos();
                    lista.set(rowModel, actualizado);
                    
                    dao.actualizarContactos(lista);
                    refrescarDatos();
                    
                    NotificationManager.showSuccess(view,
                        msg.get("success.contact.updated", 
                        "Contacto actualizado exitosamente"));
                }
            }
        } catch (IOException ex) {
            NotificationManager.showError(view, 
                msg.get("update.error", ex.getMessage()));
        } finally {
            // SIEMPRE liberar el lock
            ContactLockManager.unlockContact(original, userId);
        }
    }

    private JPopupMenu crearMenuContextual(PanelContactos pc) {
        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(new java.awt.Color(30, 30, 30));
        menu.setBorder(BorderFactory.createLineBorder(
            new java.awt.Color(55, 65, 81), 1));
        
        JMenuItem mEditar = new JMenuItem("✏️ " + msg.get("menu.edit", "Editar"));
        estilizarMenuItem(mEditar);
        mEditar.addActionListener(e -> editarSeleccionadoSincronizado(pc));
        
        JMenuItem mEliminar = new JMenuItem("🗑️ " + msg.get("menu.delete", "Eliminar"));
        estilizarMenuItem(mEliminar);
        mEliminar.addActionListener(e -> eliminarSeleccionadoSincronizado(pc));
        
        JMenuItem mFavorito = new JMenuItem("⭐ " + 
            msg.get("menu.toggle.favorite", "Alternar favorito"));
        estilizarMenuItem(mFavorito);
        mFavorito.addActionListener(e -> toggleFavoritoSincronizado(pc));
        
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

    private synchronized void eliminarSeleccionadoSincronizado(PanelContactos pc) {
        int rowView = pc.getTabla().getSelectedRow();
        if (rowView < 0) {
            NotificationManager.showInfo(view,
                msg.get("error.no.selection", "Por favor seleccione un contacto"));
            return;
        }
        
        int rowModel = pc.getTabla().convertRowIndexToModel(rowView);
        Persona p = ((ContactoTableModel)pc.getTabla().getModel())
            .getContactoAt(rowModel);

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
            NotificationManager.showSuccess(view,
                msg.get("success.contact.deleted", "Contacto eliminado exitosamente"));
        } catch (IOException ex) {
            NotificationManager.showError(view, 
                msg.get("update.error", ex.getMessage()));
        }
    }
    
    private synchronized void toggleFavoritoSincronizado(PanelContactos pc) {
        int rowView = pc.getTabla().getSelectedRow();
        if (rowView < 0) {
            NotificationManager.showInfo(view,
                msg.get("error.no.selection", "Por favor seleccione un contacto"));
            return;
        }
        
        int rowModel = pc.getTabla().convertRowIndexToModel(rowView);
        List<Persona> lista = getTodos();
        Persona p = lista.get(rowModel);
        
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
            NotificationManager.showSuccess(view,
                actualizado.isFavorito() ? 
                    msg.get("success.favorite.added", "Agregado a favoritos") :
                    msg.get("success.favorite.removed", "Removido de favoritos"));
        } catch (IOException ex) {
            NotificationManager.showError(view, 
                msg.get("update.error", ex.getMessage()));
        }
    }

    private int mostrarConfirmacionEliminar(String nombre) {
        UIManager.put("OptionPane.background", new java.awt.Color(30, 30, 30));
        UIManager.put("Panel.background", new java.awt.Color(30, 30, 30));
        UIManager.put("OptionPane.messageForeground", 
            new java.awt.Color(243, 244, 246));
        
        String mensaje = msg.get("confirm.delete.text", nombre);
        String[] opciones = {
            msg.get("btn.delete", "Eliminar"),
            msg.get("btn.cancel", "Cancelar")
        };
        
        return JOptionPane.showOptionDialog(
            view, mensaje,
            msg.get("confirm.delete.title"),
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE,
            null, opciones, opciones[1]
        );
    }

    private synchronized void refrescarDatos() {
        List<Persona> lista = getTodos();
        view.getPanelContactos().getModelo().setDatos(lista);
        view.getPanelEstadisticas().repaint();
    }

    private JDialog createLoadingDialog(String message) {
        JDialog dialog = new JDialog(view, "Procesando", false);
        dialog.setUndecorated(true);
        dialog.setLayout(new java.awt.BorderLayout(10, 10));
        
        JPanel panel = new JPanel(new java.awt.FlowLayout(
            java.awt.FlowLayout.CENTER, 15, 15));
        panel.setBackground(new java.awt.Color(30, 30, 30));
        panel.setBorder(BorderFactory.createLineBorder(
            new java.awt.Color(59, 130, 246), 2));
        
        JLabel label = new JLabel(message);
        label.setForeground(new java.awt.Color(243, 244, 246));
        label.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new java.awt.Dimension(200, 25));
        
        panel.add(label);
        panel.add(progressBar);
        
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(view);
        
        return dialog;
    }

    /**
     * Liberar recursos al cerrar la aplicación
     */
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        NotificationManager.shutdown();
        ContactLockManager.clearAllLocks();
    }
}