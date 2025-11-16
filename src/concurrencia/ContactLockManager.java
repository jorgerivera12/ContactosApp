/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package concurrencia;


import modelo.Persona;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Gestor para controlar el acceso concurrente a contactos individuales
 * Previene que múltiples threads modifiquen el mismo contacto simultáneamente
 * @author jorge
 */
public class ContactLockManager {
    
    // Mapa de locks por contacto (basado en email como identificador único)
    private static final ConcurrentHashMap<String, ReentrantLock> contactLocks = 
        new ConcurrentHashMap<>();
    
    // Mapa para rastrear quién está editando cada contacto
    private static final ConcurrentHashMap<String, String> editingUsers = 
        new ConcurrentHashMap<>();
    
    /**
     * Intenta adquirir el lock para editar un contacto
     * @param contacto El contacto a bloquear
     * @param userId Identificador del usuario/thread que intenta editar
     * @return true si se adquirió el lock, false si ya está siendo editado
     */
    public static synchronized boolean tryLockContact(Persona contacto, String userId) {
        String contactKey = getContactKey(contacto);
        
        // Verificar si el contacto ya está siendo editado
        if (editingUsers.containsKey(contactKey)) {
            return false; // Ya está bloqueado por otro usuario
        }
        
        // Obtener o crear el lock para este contacto
        ReentrantLock lock = contactLocks.computeIfAbsent(contactKey, 
            k -> new ReentrantLock());
        
        // Intentar adquirir el lock
        if (lock.tryLock()) {
            editingUsers.put(contactKey, userId);
            return true;
        }
        
        return false;
    }
    
    /**
     * Libera el lock de un contacto
     * @param contacto El contacto a desbloquear
     * @param userId El usuario que está liberando el lock
     */
    public static synchronized void unlockContact(Persona contacto, String userId) {
        String contactKey = getContactKey(contacto);
        
        // Verificar que el usuario actual es quien tiene el lock
        String currentEditor = editingUsers.get(contactKey);
        if (currentEditor != null && currentEditor.equals(userId)) {
            ReentrantLock lock = contactLocks.get(contactKey);
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
                editingUsers.remove(contactKey);
            }
        }
    }
    
    /**
     * Verifica si un contacto está siendo editado
     * @param contacto El contacto a verificar
     * @return true si está bloqueado, false en caso contrario
     */
    public static boolean isLocked(Persona contacto) {
        return editingUsers.containsKey(getContactKey(contacto));
    }
    
    /**
     * Obtiene el usuario que está editando un contacto
     * @param contacto El contacto a verificar
     * @return El ID del usuario editando, o null si no está bloqueado
     */
    public static String getEditor(Persona contacto) {
        return editingUsers.get(getContactKey(contacto));
    }
    
    /**
     * Genera una clave única para un contacto basada en email y nombre
     */
    private static String getContactKey(Persona contacto) {
        return contacto.getEmail() + "_" + contacto.getNombre();
    }
    
    /**
     * Limpia todos los locks (útil para testing o reset)
     */
    public static synchronized void clearAllLocks() {
        contactLocks.values().forEach(lock -> {
            if (lock.isLocked()) {
                lock.unlock();
            }
        });
        contactLocks.clear();
        editingUsers.clear();
    }
    
    /**
     * Obtiene el número de contactos actualmente bloqueados
     */
    public static int getLockedContactsCount() {
        return editingUsers.size();
    }
}