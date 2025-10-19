/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import modelo.Persona;

/**
 *
 * @author jorge
 */
//Definición de la clase pública "PersonaDAO"
public class PersonaDAO {
	
	// Declaración de atributos privados de la clase "PersonaDAO"
	private File archivo; // Archivo donde se almacenarán los datos de los contactos
	private Persona persona; // Objeto "Persona" que se gestionará
	
	// Constructor público de la clase "PersonaDAO" que recibe un objeto "Persona" como parámetro
	public PersonaDAO(Persona persona) {
		this.persona = persona; // Asigna el objeto "Persona" recibido al atributo de la clase
		//archivo = new File("c:/gestionContactos"); // Establece la ruta donde se alojará el archivo
		// Llama al método para preparar el archivo
		prepararArchivo();
	}
        // Método privado para gestionar el archivo utilizando la clase File
	// Crea carpeta y archivo de forma portable (Linux/Windows/macOS)
        private void prepararArchivo() {
            try {
                Path dir = Paths.get(System.getProperty("user.home"), "gestionContactos");
                Files.createDirectories(dir);                          // crea ~/gestionContactos
                Path file = dir.resolve("datosContactos.csv");         // apunta al csv

                if (Files.notExists(file)) {
                    Files.createFile(file);
                    Files.write(file,
                        "NOMBRE;TELEFONO;EMAIL;CATEGORIA;FAVORITO\n".getBytes(StandardCharsets.UTF_8));
                }
                this.archivo = file.toFile();
            } catch (IOException e) {
                throw new RuntimeException("No se pudo preparar el archivo de contactos", e);
            }
        }
	private void escribir(String texto){
		// Prepara el archivo para escribir en la última línea
		FileWriter escribir;
		try {
			escribir = new FileWriter(archivo.getAbsolutePath(), true);
			escribir.write(texto + "\n"); // Escribe los datos del contacto en el archivo
			// Cierra el archivo
			escribir.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	// Método público para escribir en el archivo
	public boolean escribirArchivo() {
//		// Prepara el archivo para escribir en la última línea
//		FileWriter escribir = new FileWriter(archivo.getAbsolutePath(), true);
//		escribir.write(Persona.datosContacto() + "\n"); // Escribe los datos del contacto en el archivo
//		// Cierra el archivo
//		escribir.close();
		escribir(persona.datosContacto());
		return true; // Retorna true si la escritura fue exitosa
	}
	
	// Método público para leer los datos del archivo
	public List<Persona> leerArchivo() throws IOException {
		// Cadena que contendrá toda la data del archivo
		String contactos = "";
		// Abre el archivo para leer
		FileReader leer = new FileReader(archivo.getAbsolutePath());
		int c;
		while ((c = leer.read()) != -1) { // Lee hasta la última línea del archivo
			contactos += String.valueOf((char) c);
		}
		// Separa cada contacto por salto de línea
		String[] datos = contactos.split("\n");
		// Crea una lista que almacenará cada Persona encontrada
		List<Persona> personas = new ArrayList<>();
		// Recorre cada contacto
		for (String contacto : datos) {
			// Crea una instancia de Persona
			Persona p = new Persona();
			p.setNombre(contacto.split(";")[0]); // Asigna el nombre
			p.setTelefono(contacto.split(";")[1]); // Asigna el teléfono
			p.setEmail(contacto.split(";")[2]); // Asigna el email
			p.setCategoria(contacto.split(";")[3]); // Asigna la categoría
			p.setFavorito(Boolean.parseBoolean(contacto.split(";")[4])); // Asigna si es favorito
			// Añade cada Persona a la lista
			personas.add(p);
		}
		// Cierra el archivo
		leer.close();
		// Retorna la lista de personas
		return personas;
	}
	
	// Método público para guardar los contactos modificados o eliminados
	public void actualizarContactos(List<Persona> personas) throws IOException {
		// Borra los datos del archivo
		archivo.delete();
		// Recorre los elementos de la lista
		for (Persona p : personas) {
			// Instancia el DAO
			new PersonaDAO(p);
			// Escribe en el archivo
			escribirArchivo();
		}
	}
}