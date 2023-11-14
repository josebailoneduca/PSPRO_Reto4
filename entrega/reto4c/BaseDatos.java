package reto4c;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * Simula una base de datos. Almacena un listado de numeros enteros en un
 * archivo binario de manera secuencial. Se le puede pedir que devuelve el valor
 * de una de las posiciones guardadas o que incremente en 1 el valor de alguna
 * de las posiciones de archivo.
 * 
 * Controla el acceso con un algoritmo de lector escritor de W. Stallins con
 * ciertas modificaciones que hacen que puede ser configurado para que sea con
 * prioridad para los lectores o sin prioridad de lectura escritura.
 * 
 * Varios lectores puede acceder simultaneamente pero cuando un escritor tiene
 * el acceso nadie puede acceder. Este comportamiento esta implementado con
 * semaforos. En el caso de activarse el modo en el que no hay prioridad se
 * puede elegir si el semaforo que controla el acceso sin prioridad sea un
 * semaforo fuerte o debil.
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class BaseDatos {
	/**
	 * lectores activos
	 */
	private int numeroLectores = 0;

	/**
	 * Semaforo lectores
	 */
	private Semaforo lectores;
	/**
	 * Semaforo escritores
	 */
	private Semaforo escritores;

	/**
	 * Semaforo que permite eliminar la prioridad de lectura/escritura
	 */
	private Semaforo cola;

	/**
	 * Define si el algoritmos de lectura escritura debe dar prioridad a los
	 * lectores
	 */
	private boolean prioridadEnLectura;

	/**
	 * Cantidad de tuplas de la base de datos
	 */
	private int numeroTuplas;

	/**
	 * File que apunta al archivo de la base de datos
	 */
	private File f;

	/**
	 * Constructor
	 * 
	 * @param ruta                     Ruta del archivo en disco
	 * @param numeroTuplas             Numero de tuplas de la base de datos
	 * @param nHebras                  Numero de hebras maximas que manejara la base de datos
	 * @param prioridadEnLectura       True para activar la prioridad de lectura. False para que no haya prioridad ni de lectura ni de escritura
	 * @param semaforosFuertes         True para usar semaforos fuertes. False para usar semaforos debiles
	 */
	public BaseDatos(String ruta, int numeroTuplas, int nHebras, boolean prioridadEnLectura,boolean semaforosFuertes) {
		
		//inicializar valores
		this.prioridadEnLectura = prioridadEnLectura;
		this.lectores = new Semaforo(1, nHebras);
		this.escritores = new Semaforo(1, nHebras);
		this.cola = (semaforosFuertes) ? new SemaforoFuerte(1, nHebras) : new Semaforo(1, nHebras);
		this.f = new File(ruta);
		this.numeroTuplas = numeroTuplas;
		
		//crear base de datos
		crearBaseDatos();
	}

	/**
	 * Incrementa el valor de una tupla en +1
	 * 
	 * Controla el mutex de la seccion critica según la parte correspondiente a 
	 * los escritores del algoritmo de W. Stallins con modificaciones
	 * para evitar prioridad de lectores si asi se ha configurado
	 * 
	 * @param id Id de la tupla a editar
	 */
	public void update(int id) {
		//semaforo de anulacion de prioridades
		if (!prioridadEnLectura)
			cola.esperar();
		//escritores esperan
		escritores.esperar();
		//fin de semaforo de anulacion de prioridades
		if (!prioridadEnLectura)
			cola.senalar();
		
		//escritura a base de datos
		//>>seccion critica
		escribirADisco(id);
		//<<fin seccion critica
		
		//liberacion de nuevo escritor
		escritores.senalar();
	}

	/**
	 * Devuelve el valor de una tupla
	 * Controla el mutex de la seccion critica según la parte correspondiente a 
	 * los lectores del algoritmo de W. Stallins con modificaciones
	 * para evitar prioridad de lectores si asi se ha configurado
	 * 
	 * @param id Id de la tupla
	 * @return El valor de la tupla
	 */
	public int select(int id) {
	 
		//semaforo de anulacion de prioridades
 		if (!prioridadEnLectura)
			cola.esperar();
 		//lectores esperan
		lectores.esperar();
		//registrar lector activo
		numeroLectores++;
		//bloquear escritores si se es el primer lector
		if (numeroLectores == 1)
			escritores.esperar();
		//fin de semaforo de anulacion de prioridades
		if (!prioridadEnLectura)
			cola.senalar();
		
		//liberacion de un lector
		lectores.senalar();

		//lectura de la base de datos
		//>>seccion critica
		int leido = leerDeDisco(id);
		//<<fin seccion critica

		//letores esperan
		lectores.esperar();
		//decrementar el nuemro de lectores activos
		numeroLectores--;
		try {
			Thread.currentThread().sleep(1);
		} catch (InterruptedException e) {
		}
		//si no quedan lectores se da paso a los escritores
		if (numeroLectores == 0)
			escritores.senalar();
		//liberar lector
		lectores.senalar();
		
		//devolver valor leido
		return leido;
	}

	/**
	 * Devuelve el numero de tuplas disponibles en la base de datos
	 * 
	 * @return El numero de tuplas
	 */
	public int getNumeroTuplas() {
		return numeroTuplas;
	}

	/**
	 * Devuelve una lista con todos los valores de las tuplas
	 * 
	 * @return La lista de valores
	 */
	public List<Integer> getTodasLasTuplas() {
		ArrayList<Integer> listaValores = new ArrayList<Integer>();
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(f, "r");
			//ir a inicio de archivo
			raf.seek(0);
			//recoger todos los valores hasta EOF
			while (true)
				listaValores.add(raf.readInt());

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (EOFException e) {
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//devolver lista
		return listaValores;
	}

	/**
	 * Devuelve la ruta del archivo de la base de datos
	 * @return La ruta absoluta del archivo
	 */
	public String getRuta() {
		return this.f.getAbsolutePath();
	}

	/**
	 * Crea la base de datos en disco con el numero de tuplas especificado con valor
	 * 0 cada una. Si el archivo ya existe es borrado
	 */
	private void crearBaseDatos() {
		//borrado
		if (f.exists()) {
			if (!f.isDirectory())
				f.delete();
			else {
				System.err.println("Debe especificar la ruta de un archivo en la configuracion de MainBaseDatos.java");
				System.exit(0);
			}
		}
		
		//creacion
		try {
			f.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//rellenar archivo con ceros
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(f, "rw");
			for (int i = 0; i < numeroTuplas; i++) {
				raf.writeInt(0);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Base de datos creada en " + f.getAbsolutePath() + " con " + numeroTuplas + " registros");
	}

	/**
	 * Lee el valor de una tupla del disco y actualiza su valor a +1
	 * El acceso a la posicion en el archivo es realizado con id*4 debido a que almacena enteros los cuales ocupan 4 bytes.
	 * @param id Id de la tupla
	 */
	private void escribirADisco(int id) {
		RandomAccessFile raf = null;
		try {
			//recoger valor actual
			int actual = this.leerDeDisco(id);
			raf = new RandomAccessFile(f, "rw");
			//desfase hasta la posicion de la tupla
			raf.seek(id * 4);
			//escribir nuevo valor
			raf.writeInt(actual + 1);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Lee el valor de una tupla del disco
	 * El acceso a la posicion en el archivo es realizado con id*4 debido a que almacena enteros los cuales ocupan 4 bytes.
	 * 
	 * @param id Id de la tupla
	 * @return valor de la tupla
	 */
	private int leerDeDisco(int id) {
		int salida = 0;
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(f, "r");
			raf.seek(id * 4);
			salida = raf.readInt();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return salida;
	}

}
