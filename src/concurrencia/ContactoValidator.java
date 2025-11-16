/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package concurrencia;

import modelo.Persona;
import modelo.ValidationResult;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Thread para validar si un contacto ya existe antes de guardarlo
 * Implementa Callable para poder retornar el resultado de la validación
 * @author jorge
 */
public class ContactoValidator implements Callable<ValidationResult> {
    
    private final Persona nuevoContacto;
    private final List<Persona> contactosExistentes;
    
    public ContactoValidator(Persona nuevoContacto, List<Persona> contactosExistentes) {
        this.nuevoContacto = nuevoContacto;
        this.contactosExistentes = contactosExistentes;
    }
    
    @Override
    public ValidationResult call() throws Exception {
        // Simular procesamiento (opcional)
        Thread.sleep(200);
        
        // Verificar si el contacto ya existe
        boolean existe = contactosExistentes.stream().anyMatch(p -> 
            p.getEmail().equalsIgnoreCase(nuevoContacto.getEmail()) ||
            (p.getNombre().equalsIgnoreCase(nuevoContacto.getNombre()) && 
             p.getTelefono().equals(nuevoContacto.getTelefono()))
        );
        
        if (existe) {
            return new ValidationResult(false, "El contacto ya existe en el sistema");
        }
        
        // Validaciones adicionales
        if (nuevoContacto.getNombre().trim().isEmpty()) {
            return new ValidationResult(false, "El nombre no puede estar vacío");
        }
        
        if (!nuevoContacto.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return new ValidationResult(false, "El formato del email no es válido");
        }
        
        if (nuevoContacto.getTelefono().trim().isEmpty()) {
            return new ValidationResult(false, "El teléfono no puede estar vacío");
        }
        
        return new ValidationResult(true, "Contacto válido");
    }
}

