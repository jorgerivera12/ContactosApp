/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 * Clase para encapsular el resultado de la validación
 * @author jorge
 */
public class ValidationResult {
    private final boolean valido;
    private final String mensaje;
    
    public ValidationResult(boolean valido, String mensaje) {
        this.valido = valido;
        this.mensaje = mensaje;
    }
    
    public boolean isValido() {
        return valido;
    }
    
    public String getMensaje() {
        return mensaje;
    }
}