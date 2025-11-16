/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package concurrencia;

import modelo.Persona;
import javax.swing.SwingWorker;
import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * SwingWorker para exportar contactos a CSV en segundo plano
 * con sincronización para evitar corrupción de datos
 * @author jorge
 */
public class ExportWorker extends SwingWorker<Boolean, Integer> {
    
    private final List<Persona> contactos;
    private final File archivo;
    private final ExportCallback callback;
    
    // Lock estático para sincronizar el acceso al archivo entre múltiples exportaciones
    private static final ReentrantLock fileLock = new ReentrantLock();
    
    public interface ExportCallback {
        void onExportProgress(int progress);
        void onExportComplete(String filePath);
        void onExportError(Exception e);
    }
    
    public ExportWorker(List<Persona> contactos, File archivo, ExportCallback callback) {
        this.contactos = contactos;
        this.archivo = archivo;
        this.callback = callback;
    }
    
    @Override
    protected Boolean doInBackground() throws Exception {
        // Adquirir el lock antes de escribir en el archivo
        fileLock.lock();
        
        try {
            int total = contactos.size();
            
            try (PrintWriter pw = new PrintWriter(archivo, StandardCharsets.UTF_8)) {
                // Escribir encabezado
                pw.println("Nombre;Teléfono;Email;Categoría;Favorito");
                
                // Exportar cada contacto con actualización de progreso
                for (int i = 0; i < total; i++) {
                    Persona p = contactos.get(i);
                    pw.println(p.datosContacto());
                    
                    // Simular procesamiento y actualizar progreso
                    Thread.sleep(50);
                    
                    int progress = (int) ((i + 1) * 100.0 / total);
                    publish(progress);
                    setProgress(progress);
                }
            }
            
            return true;
            
        } finally {
            // Siempre liberar el lock
            fileLock.unlock();
        }
    }
    
    @Override
    protected void process(List<Integer> chunks) {
        // Actualizar la UI con el progreso más reciente
        if (!chunks.isEmpty() && callback != null) {
            callback.onExportProgress(chunks.get(chunks.size() - 1));
        }
    }
    
    @Override
    protected void done() {
        try {
            Boolean success = get();
            if (success && callback != null) {
                callback.onExportComplete(archivo.getAbsolutePath());
            }
        } catch (Exception e) {
            if (callback != null) {
                callback.onExportError(e);
            }
        }
    }
}