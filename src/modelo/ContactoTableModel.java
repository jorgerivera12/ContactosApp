/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;
import javax.swing.table.AbstractTableModel;
import java.util.List;
/**
 *
 * @author jorge
 */
public class ContactoTableModel extends AbstractTableModel {
    private final String[] columnas = {"Nombre","Teléfono","Email","Categoría","Favorito"};
    private List<Persona> datos;

    public ContactoTableModel(List<Persona> datos) {
        this.datos = datos;
    }

    @Override public int getRowCount()    { return datos.size(); }
    @Override public int getColumnCount() { return columnas.length; }
    @Override public String getColumnName(int col) { return columnas[col]; }

    @Override
    public Object getValueAt(int row, int col) {
        Persona p = datos.get(row);
        switch(col) {
            case 0: return p.getNombre();
            case 1: return p.getTelefono();
            case 2: return p.getEmail();
            case 3: return p.getCategoria();
            case 4: return p.isFavorito();
            default: return "";
        }
    }

    public Persona getContactoAt(int row) {
        return datos.get(row);
    }

    public void setDatos(List<Persona> nuevos) {
        this.datos = nuevos;
        fireTableDataChanged();
    }
}