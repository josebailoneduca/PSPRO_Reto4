package reto4b.dekker;

import java.util.Random;

/**
 * Hebra que implementa el algoritmo de Dekker. 
 * En su seccion critica suma a la variable compartida un valor predeterminado para esta hebra
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class HebraDekker extends Thread {

	//ATRIBUTOS DE CLASE
	//banderas
	public static boolean[] bandera=new boolean[2];
	public static int turno=0;

	
	//ATRIBUTOS DE OBJETO
	//identificador de la hebra
	private int id=0;
	
	//cicos a girar en el while
	private int ciclos;
	
	//valor a sumar a la variable compartida de la seccion critica
	private int valorOperacionCritica=1;
	
 
 
	
	//METODOS DE OBJETO
	/**
	 * Constructor
	 * @param id Identificador de la hebra
	 * @param ciclos Ciclos a girar
	 * @param valorOperacionCritica Valor a agregar a la variable compartida
	 */
	public HebraDekker(int id, int ciclos,int valorOperacionCritica){
		this.id=id;
		this.ciclos=ciclos;
		this.valorOperacionCritica=valorOperacionCritica;
		}
	
	
	@Override
	public void run() {
		while (ciclos>0) {
			//levantar bandera propia
			bandera[this.id]=true;
			//si no es el turno propio bajar bandera propia y esperar a tener el turno para volver a levantarla
			if (turno!=this.id) {
				bandera[this.id]=false;
				while(turno!=this.id) {
					Thread.yield();
				}
				bandera[this.id]=true;
			}
			
 
			//inicio seccion critica>>>>
			int t = MainDekker.contadorCritico;

			//complejidad de seccion critica forzada para probar ruptura de coherencia
			try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}

			//incrementar la variable compartida
			MainDekker.contadorCritico=t+this.valorOperacionCritica;
			//<<<< fin seccion critica
 
			
			
			//dar turno al contrario
			turno=(this.id==0)?1:0;
			//bajar la bandera propia
			bandera[this.id]=false;
			
			//aumentar ciclos que ha completado la hebra
			ciclos--;
 
			//imprimir progreso
			if (ciclos%1==0)
			System.out.println("Hebra Dekker nº "+this.id+" Ciclos restantes:"+ciclos);
		}
	}
}
 