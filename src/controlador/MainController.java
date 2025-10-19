/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import dao.PersonaDAO;
import vista.VentanaPrincipal;
import vista.PanelContactos;
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

/**
 *
 * @author jorge
 */
public class MainController {

    private VentanaPrincipal view;
    private PersonaDAO dao;
    private List<Persona> todos;

    public MainController(VentanaPrincipal v) {
        this.view = v;
        this.dao = new PersonaDAO(new Persona());
    }

    public List<Persona> getTodos() {
        try {
            todos = dao.leerArchivo();
        } catch (IOException e) {
            // Mostrar mensaje de error si lo deseas:
            JOptionPane.showMessageDialog(view,
                    "Error al cargar contactos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            todos = new ArrayList<>();
        }
        return todos;
    }

    public void bindPanelContactos(PanelContactos pc) {
        // Atajo Ctrl+N
        pc.getBtnNuevo().addActionListener(e -> crearContacto());
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
        // Doble clic en tabla
        pc.getTabla().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editarSeleccionado(pc);
                }
            }
        });
        // Menú contextual
        JPopupMenu menu = new JPopupMenu();
        JMenuItem mEliminar = new JMenuItem("Eliminar");
        mEliminar.addActionListener(e -> eliminarSeleccionado(pc));
        menu.add(mEliminar);
        pc.getTabla().setComponentPopupMenu(menu);
    }

    private void filtrar(PanelContactos pc) {
        String txt = pc.getTxtBuscar().getText();
        pc.getSorter().setRowFilter(RowFilter.regexFilter("(?i)" + txt));
    }

    private void crearContacto() {
        // Mostrar diálogo, guardar y refrescar tabla
        // Pides datos al usuario con un diálogo modal
        JTextField txtNombre = new JTextField();
        JTextField txtTelefono = new JTextField();
        JTextField txtEmail = new JTextField();
        JComboBox<String> cmbCat = new JComboBox<>(new String[]{"Familia", "Amigos", "Trabajo"});
        JCheckBox chkFav = new JCheckBox("Favorito");
        Object[] form = {
            "Nombre:", txtNombre,
            "Teléfono:", txtTelefono,
            "Email:", txtEmail,
            "Categoría:", cmbCat,
            chkFav
        };
        
        int opc = JOptionPane.showConfirmDialog(view, form,
                "Nuevo Contacto", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opc == JOptionPane.OK_OPTION) {
            Persona p = new Persona(
                    txtNombre.getText(),
                    txtTelefono.getText(),
                    txtEmail.getText(),
                    (String) cmbCat.getSelectedItem(),
                    chkFav.isSelected()
            );
            // Escribe en CSV
            new PersonaDAO(p).escribirArchivo();
            // Refresca datos en vista
            refrescarDatos();
        }
    }

    private void refrescarDatos() {
        // 1. recarga lista en tabla
        List<Persona> lista = getTodos();
        view.getPanelContactos().getModelo().setDatos(lista);
        view.getPanelEstadisticas().repaint();
    }

    private void editarSeleccionado(PanelContactos pc) {
        int rowView = pc.getTabla().getSelectedRow();
        if (rowView < 0) {
            return;
        }
        int rowModel = pc.getTabla().convertRowIndexToModel(rowView);
        Persona original = ((ContactoTableModel) pc.getTabla().getModel()).getContactoAt(rowModel);

        // Lanza un formulario igual al de crear, pero precargado
        JTextField txtNombre = new JTextField(original.getNombre());
        JTextField txtTelefono = new JTextField(original.getTelefono());
        JTextField txtEmail = new JTextField(original.getEmail());
        JComboBox<String> cmbCat = new JComboBox<>(new String[]{"Familia", "Amigos", "Trabajo"});
        cmbCat.setSelectedItem(original.getCategoria());
        JCheckBox chkFav = new JCheckBox("Favorito", original.isFavorito());
        Object[] form = {
            "Nombre:", txtNombre,
            "Teléfono:", txtTelefono,
            "Email:", txtEmail,
            "Categoría:", cmbCat,
            chkFav
        };
        int opc = JOptionPane.showConfirmDialog(view, form,
                "Modificar Contacto", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opc == JOptionPane.OK_OPTION) {
            // Reemplazas datos en la lista
            List<Persona> lista = getTodos();
            lista.set(rowModel, new Persona(
                    txtNombre.getText(),
                    txtTelefono.getText(),
                    txtEmail.getText(),
                    (String) cmbCat.getSelectedItem(),
                    chkFav.isSelected()
            ));
            try {
              dao.actualizarContactos(lista);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            refrescarDatos();
        }
    }

    private void eliminarSeleccionado(PanelContactos pc) {
        int rowView = pc.getTabla().getSelectedRow();
        if (rowView < 0) return;
        int rowModel = pc.getTabla().convertRowIndexToModel(rowView);
        Persona p = ((ContactoTableModel)pc.getTabla().getModel()).getContactoAt(rowModel);

        int conf = JOptionPane.showConfirmDialog(
            view,
            "¿Eliminar a " + p.getNombre() + "?",
            "Confirmar",
            JOptionPane.YES_NO_OPTION
        );
        if (conf != JOptionPane.YES_OPTION) return;

        // 1) Carga la lista actual
        List<Persona> lista = getTodos();
        // 2) Elimínalo
        lista.removeIf(x ->
            x.getNombre().equals(p.getNombre()) &&
            x.getEmail().equals(p.getEmail()) &&
            x.getTelefono().equals(p.getTelefono())
        );
        // 3) Sobrescribe TODO el archivo con la lista filtrada
        try {
            dao.actualizarContactos(lista);
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view,
                "Error al actualizar archivo:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        // 4) Refresca tablas y gráficos
        refrescarDatos();
    }

    private void exportarCSV() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Guardar contactos como CSV");
        if (fc.showSaveDialog(view) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            try (PrintWriter pw = new PrintWriter(f, StandardCharsets.UTF_8)) {
                pw.println("Nombre;Teléfono;Email;Categoría;Favorito");
                for (Persona p : getTodos()) {
                    pw.println(p.datosContacto());
                }
                JOptionPane.showMessageDialog(view, "Exportado: " + f.getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view,
                        "Error al exportar:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Ejemplo de carga con SwingWorker y progreso
    private void cargaMasivaConProgreso(PanelContactos pc, List<Persona> lista) {
        pc.getBarraProgreso().setMaximum(lista.size());
        pc.getBarraProgreso().setVisible(true);
        new SwingWorker<Void, Integer>() {
            protected Void doInBackground() {
                for (int i = 0; i < lista.size(); i++) {
                    // procesar lista.get(i)…
                    publish(i + 1);
                }
                return null;
            }

            protected void process(List<Integer> chunks) {
                pc.getBarraProgreso().setValue(chunks.get(chunks.size() - 1));
            }

            protected void done() {
                pc.getBarraProgreso().setVisible(false);
            }
        }.execute();
    }
}
