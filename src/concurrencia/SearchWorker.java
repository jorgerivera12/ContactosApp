/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package concurrencia;

import modelo.Persona;
import javax.swing.SwingWorker;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SwingWorker para realizar búsquedas de contactos en segundo plano
 * sin congelar la interfaz gráfica
 * @author jorge
 */
public class SearchWorker extends SwingWorker<List<Persona>, Void> {
    
    private final String searchTerm;
    private final List<Persona> allContacts;
    private final SearchCallback callback;
    
    public interface SearchCallback {
        void onSearchComplete(List<Persona> results);
        void onSearchError(Exception e);
    }
    
    public SearchWorker(String searchTerm, List<Persona> allContacts, SearchCallback callback) {
        this.searchTerm = searchTerm.toLowerCase();
        this.allContacts = allContacts;
        this.callback = callback;
    }
    
    @Override
    protected List<Persona> doInBackground() throws Exception {
        // Simular búsqueda en grandes volúmenes de datos
        Thread.sleep(100);
        
        if (searchTerm.isEmpty()) {
            return allContacts;
        }
        
        // Filtrar contactos que coincidan con el término de búsqueda
        return allContacts.stream()
            .filter(p -> 
                p.getNombre().toLowerCase().contains(searchTerm) ||
                p.getEmail().toLowerCase().contains(searchTerm) ||
                p.getTelefono().contains(searchTerm) ||
                p.getCategoria().toLowerCase().contains(searchTerm)
            )
            .collect(Collectors.toList());
    }
    
    @Override
    protected void done() {
        try {
            List<Persona> results = get();
            callback.onSearchComplete(results);
        } catch (Exception e) {
            callback.onSearchError(e);
        }
    }
}