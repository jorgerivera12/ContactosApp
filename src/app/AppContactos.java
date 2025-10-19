/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package app;

import java.awt.EventQueue;
import vista.Ventana;

/**
 *
 * @author jorge
 */
public class AppContactos {

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
     	    EventQueue.invokeLater(new Runnable() {
	        public void run() {
	            try {
	                // Dentro de este método, se crea una instancia de la clase Ventana, que es la Ventana principal de la aplicación.
	                Ventana frame = new Ventana();
	                // Establece la visibilidad de la Ventana como verdadera, lo que hace que la Ventana sea visible para el usuario.
	                frame.setVisible(true);
	            } catch (Exception e) {
	                // En caso de que ocurra una excepción durante la creación o visualización de la Ventana, se imprime la traza de la pila de la excepción.
	                e.printStackTrace();
	            }
	        }
	    });
    }
    
}
