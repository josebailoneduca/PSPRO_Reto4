package reto4c;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class BaseDatos {
	int numeroLectores = 0;

	private Semaforo lectores;
	private Semaforo escritores;
	private Semaforo cola;
	private int numeroTuplas;
	private File f;
	private boolean prioridadEnLectura;

	/**
	 * Constructor
	 * 
	 * @param ruta                     Ruta del archivo en disco
	 * @param numeroTuplas             Numero de tuplas de la base de datos
	 * @param nHebras                  Numero de hebras maximas que manejara la base
	 *                                 de datos
	 * @param controlEstrictoInanicion Control de inanicion de las hebras. True si
	 *                                 se quiere usar un semaforo estricto para
	 *                                 ello. False si se quiere usar un semaforo
	 *                                 debil
	 */
	public BaseDatos(String ruta, int numeroTuplas, int nHebras, boolean prioridadEnLectura,
			boolean controlEstrictoInanicion) {
		this.prioridadEnLectura = prioridadEnLectura;
		this.lectores = (controlEstrictoInanicion) ? new SemaforoEstricto(1, nHebras) : new Semaforo(1, nHebras);
		this.escritores = (controlEstrictoInanicion) ? new SemaforoEstricto(1, nHebras) : new Semaforo(1, nHebras);
		this.cola = (controlEstrictoInanicion) ? new SemaforoEstricto(1, nHebras) : new Semaforo(1, nHebras);
		this.f = new File(ruta);
		this.numeroTuplas = numeroTuplas;
		crearBaseDatos();
	}

	/**
	 * Crea la base de datos en disco con el numero de tuplas especificado con valor
	 * 0 cada una. Si el archivo ya existe es borrado
	 */
	private void crearBaseDatos() {
		if (f.exists())
			f.delete();
		try {
			f.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	 * Incrementa el valor de una tupla en +1
	 * 
	 * @param id Id de la tupla a editar
	 */
	public void update(int id) {
		if (!prioridadEnLectura)
			cola.esperar();
		escritores.esperar();
		if (!prioridadEnLectura)
			cola.senalar();
		escribirADisco(id);
		escritores.senalar();
	}

	public int select(int id) {
		int leido = 0;
		if (!prioridadEnLectura)
		cola.esperar();
		lectores.esperar();
		numeroLectores++;
		if (numeroLectores == 1)
			escritores.esperar();
		if (!prioridadEnLectura)
		cola.senalar();
		lectores.senalar();

		leido = leerDeDisco(id);

		lectores.esperar();
		numeroLectores--;
		try {
			Thread.currentThread().sleep(1);
		} catch (InterruptedException e) {
		}
		if (numeroLectores == 0)
			escritores.senalar();
		lectores.senalar();
		return leido;
	}

	/**
	 * Lee el valor de una tupla y actualiza su valor a +1
	 * 
	 * @param id Id de la tupla
	 */
	private void escribirADisco(int id) {
		RandomAccessFile raf = null;
		try {
			int actual = this.leerDeDisco(id);
			raf = new RandomAccessFile(f, "rw");
			raf.seek(id * 4);
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

	/**
	 * Devuelve el numero de tuplas disponibles en la base de datos
	 * 
	 * @return El numero de tuplas
	 */
	public int getNumeroTuplas() {
		return numeroTuplas;
	}

	public List<Integer> getTodasLasTuplas() {
		ArrayList<Integer> salida = new ArrayList<Integer>();
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(f, "r");
			raf.seek(0);
			while (true)
				salida.add(raf.readInt());

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
		return salida;
	}

	public String getRuta() {
		return this.f.getAbsolutePath();
	}

}
