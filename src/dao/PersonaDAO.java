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

    private void escribir(String texto) {
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
        List<Persona> personas = new ArrayList<>();

        // Empieza en i = 1 para ignorar la línea de cabeceras
        for (int i = 1; i < datos.length; i++) {
            String contacto = datos[i];
            if (contacto.trim().isEmpty()) {
                continue;  // por si hay líneas vacías
            }
            String[] campos = contacto.split(";");
            Persona p = new Persona();
            p.setNombre(campos[0]);
            p.setTelefono(campos[1]);
            p.setEmail(campos[2]);
            p.setCategoria(campos[3]);
            p.setFavorito(Boolean.parseBoolean(campos[4]));
            personas.add(p);
        }
        // Cierra el archivo
        leer.close();
        // Retorna la lista de personas
        return personas;
    }

    // Método público para guardar los contactos modificados o eliminados
    public void actualizarContactos(List<Persona> personas) throws IOException {
        // 1) Borra el archivo CSV completo
        archivo.delete();
        // 2) Recrea el CSV con carpeta + archivo + cabecera
        prepararArchivo();

        // 3) Escribe cada persona al final
        for (Persona p : personas) {
            // Ajustamos la instancia interna para poder reusar 'escribir(...)'
            this.persona = p;
            escribir(p.datosContacto());
        }

    }
}
