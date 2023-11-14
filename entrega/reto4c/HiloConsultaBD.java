package reto4c;

import java.util.Random;

/**
 * Hilo base de consulta a la base de datos
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class HiloConsultaBD extends Thread {

	/**
	 * Indice id del hilo, usado para el control de mutex dentro de semaforos
	 */
	protected int indice;
	
	/**
	 * Base de datos a la que realizar las consultas
	 */
	protected BaseDatos bd;
	
	/**
	 * Cantidad de veces que debe ejecutar consulta
	 */
	protected int ciclos;
	
	/**
	 * Constructor 
	 * 
	 * @param indice Indice id del hilo
	 * @param bd Base de datos a la que realizar las consultas
	 * @param ciclos Cantidad de veces que debe ejecutar consulta
	 */
	public HiloConsultaBD(int indice, BaseDatos bd, int ciclos) {
		super();
		this.indice = indice;
		this.bd=bd;
		this.ciclos=ciclos;
	}

	/**
	 * Devuelve el indice
	 * @return El indice
	 */
	public int getIndice() {
		return indice;
	}

	
}
