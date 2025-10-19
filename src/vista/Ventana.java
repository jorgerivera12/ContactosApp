/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import controlador.LogicaVentana;

import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JList;
/**
 *
 * @author jorge
 */
public class Ventana extends JFrame {

	public JPanel contentPane; // Panel principal que contendrá todos los componentes de la interfaz.
	public JTextField txt_nombres; // Campo de texto para ingresar nombres.
	public JTextField txt_telefono; // Campo de texto para ingresar números de teléfono.
	public JTextField txt_email; // Campo de texto para ingresar direcciones de correo electrónico.
	public JTextField txt_buscar; // Campo de texto adicional.
	public JCheckBox chb_favorito; // Casilla de verificación para marcar un contacto como favorito.
	public JComboBox cmb_categoria; // Menú desplegable para seleccionar la categoría de contacto.
	public JButton btn_add; // Botón para agregar un nuevo contacto.
	public JButton btn_modificar; // Botón para modificar un contacto existente.
	public JButton btn_eliminar; // Botón para eliminar un contacto.
	public JList lst_contactos; // Lista para mostrar los contactos.
	public JScrollPane scrLista; // Panel de desplazamiento para la lista de contactos.

	/**
	 * Create the frame.
	 */
	public Ventana() {
		setTitle("GESTION DE CONTACTOS"); // Establece el título de la Ventana.
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Define el comportamiento al cerrar la Ventana.
		setResizable(false); // Evita que la Ventana sea redimensionable.
		setBounds(100, 100, 1026, 748); // Establece el tamaño y la posición inicial de la Ventana.
		contentPane = new JPanel(); // Crea un nuevo panel de contenido.
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5)); // Establece un borde vacío alrededor del panel.

		setContentPane(contentPane); // Establece el panel de contenido como el panel principal de la Ventana.
		contentPane.setLayout(null); // Configura el diseño del panel como nulo para posicionar manualmente los componentes.
		
		// Creación y configuración de etiquetas para los campos de entrada.
		JLabel lbl_etiqueta1 = new JLabel("NOMBRES:"); // Etiqueta para nombres.
		lbl_etiqueta1.setBounds(25, 41, 89, 13); // Define la posición y tamaño de la etiqueta.
		lbl_etiqueta1.setFont(new Font("Tahoma", Font.BOLD, 15)); // Configura la fuente de la etiqueta.
		contentPane.add(lbl_etiqueta1); // Agrega la etiqueta al panel de contenido.
		
		JLabel lbl_etiqueta2 = new JLabel("TELEFONO:");
		lbl_etiqueta2.setBounds(25, 80, 89, 13);
		lbl_etiqueta2.setFont(new Font("Tahoma", Font.BOLD, 15));
		contentPane.add(lbl_etiqueta2);
		
		JLabel lbl_etiqueta3 = new JLabel("EMAIL:");
		lbl_etiqueta3.setBounds(25, 122, 89, 13);
		lbl_etiqueta3.setFont(new Font("Tahoma", Font.BOLD, 15));
		contentPane.add(lbl_etiqueta3);
		
		JLabel lbl_etiqueta4 = new JLabel("BUSCAR POR NOMBRE:");
		lbl_etiqueta4.setFont(new Font("Tahoma", Font.BOLD, 15));
		lbl_etiqueta4.setBounds(25, 661, 192, 13);
		contentPane.add(lbl_etiqueta4);
		
		// Creación y configuración de campos de texto para ingresar nombres, teléfonos y correos electrónicos.
		txt_nombres = new JTextField(); // Campo de texto para nombres.
		txt_nombres.setBounds(124, 28, 427, 31); // Define la posición y tamaño del campo de texto.
		txt_nombres.setFont(new Font("Tahoma", Font.PLAIN, 15)); // Configura la fuente del campo de texto.
		contentPane.add(txt_nombres); // Agrega el campo de texto al panel de contenido.
		txt_nombres.setColumns(10); // Establece el número de columnas para el campo de texto.
		
		txt_telefono = new JTextField();
		txt_telefono.setBounds(124, 69, 427, 31);
		txt_telefono.setFont(new Font("Tahoma", Font.PLAIN, 15));
		txt_telefono.setColumns(10);
		contentPane.add(txt_telefono);
		
		txt_email = new JTextField();
		txt_email.setBounds(124, 110, 427, 31);
		txt_email.setFont(new Font("Tahoma", Font.PLAIN, 15));
		txt_email.setColumns(10);
		contentPane.add(txt_email);
		
		txt_buscar = new JTextField();
		txt_buscar.setFont(new Font("Tahoma", Font.PLAIN, 15));
		txt_buscar.setColumns(10);
		txt_buscar.setBounds(212, 650, 784, 31);
		contentPane.add(txt_buscar);
		
		// Creación y configuración de una casilla de verificación para indicar si un contacto es favorito.
		chb_favorito = new JCheckBox("CONTACTO FAVORITO"); // Casilla de verificación.
		chb_favorito.setBounds(24, 170, 193, 21); // Define la posición y tamaño de la casilla de verificación.
		chb_favorito.setFont(new Font("Tahoma", Font.PLAIN, 15)); // Configura la fuente de la casilla de verificación.
		contentPane.add(chb_favorito); // Agrega la casilla de verificación al panel de contenido.

		
		cmb_categoria = new JComboBox(); // Crea un nuevo JComboBox para permitir la selección de categorías.
		cmb_categoria.setBounds(300, 167, 251, 31); // Establece la posición y el tamaño del JComboBox en el panel.
		contentPane.add(cmb_categoria); // Agrega el JComboBox al panel de contenido.

		// Arreglo que contiene las categorías disponibles.
		String[] categorias = {"Elija una Categoria", "Familia", "Amigos", "Trabajo"};
		for (String categoria : categorias) {
		    // Agrega cada categoría al JComboBox.
		    cmb_categoria.addItem(categoria);
		}

		btn_add = new JButton("AGREGAR"); // Crea un nuevo botón con el texto "AGREGAR".
		btn_add.setFont(new Font("Tahoma", Font.PLAIN, 15)); // Configura la fuente del botón.
		btn_add.setBounds(601, 70, 125, 65); // Establece la posición y el tamaño del botón en el panel.
		contentPane.add(btn_add); // Agrega el botón al panel de contenido.
		
		btn_modificar = new JButton("MODIFICAR");
		btn_modificar.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_modificar.setBounds(736, 70, 125, 65);
		contentPane.add(btn_modificar);
		
		btn_eliminar = new JButton("ELIMINAR");
		btn_eliminar.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_eliminar.setBounds(871, 69, 125, 65);
		contentPane.add(btn_eliminar);
		
		lst_contactos = new JList(); // Crea una nueva JList para mostrar la lista de contactos.
		lst_contactos.setFont(new Font("Tahoma", Font.PLAIN, 15)); // Configura la fuente de la JList.
		lst_contactos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Establece el modo de selección a un solo elemento.
		lst_contactos.setBounds(25, 242, 971, 398); // Establece la posición y el tamaño de la JList en el panel.

		scrLista = new JScrollPane(lst_contactos); // Crea un JScrollPane para permitir el desplazamiento de la JList.
		scrLista.setBounds(25, 242, 971, 398); // Establece la posición y el tamaño del JScrollPane en el panel.
		contentPane.add(scrLista); // Agrega el JScrollPane al panel de contenido.
		
		//Instanciar el controlador para usar el delegado
		LogicaVentana lv=new LogicaVentana(this);
	}
}