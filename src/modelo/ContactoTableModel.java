/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;
import javax.swing.table.AbstractTableModel;
import java.util.List;
import i18n.Messages;
/**
 *
 * @author jorge
 */
public class ContactoTableModel extends AbstractTableModel {
    private String[] columnas;
    private List<Persona> datos;

    public ContactoTableModel(List<Persona> datos, Messages msg) {
        this.datos = datos;
        this.columnas = new String[] {
            msg.get("table.name"),
            msg.get("table.phone"),
            msg.get("table.email"),
            msg.get("table.category"),
            msg.get("table.favorite")
        };
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