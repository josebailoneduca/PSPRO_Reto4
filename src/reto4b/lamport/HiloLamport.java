package reto4b.lamport;

import java.util.Random;

import reto4b.lamport_limitevuelta.MainLamportLimiteVuelta;

public class HiloLamport extends Thread {

	//ATRIBUTOS DE CLASE
	//estado de escogiendo
	public static boolean[] escogiendo=new boolean[2];
	//numeros adjudicados
	public static int[] numero;

	//numero maximo actual
	public static int max = 0; 
	
	
	//ATRIBUTOS DE OBJETO
	private int id=0;
	private int ciclos;
	private int valorOperacionCritica=1;
	private Random r=new Random();
	// METODOS DE CLASE
	/**
	 * Configurar la cantidad de hilos soportados por Lamport	
	 * @param n Cantidad de hilos
	 */
	public static void setNumeroHebras(int n) {
		escogiendo=new boolean[n];
		numero=new int[n];
	}
	
	
	//METODOS DE OBJETO
	
	/**
	 * Constructor
	 * @param id indice del hilo para su gestion en Lamport
	 */
	public HiloLamport(int id, int ciclos,int valorOperacionCritica){
		this.id=id;
		this.ciclos=ciclos;
		this.valorOperacionCritica=valorOperacionCritica;
		}
	
	
	@Override
	public void run() {
		while (ciclos>0) {
 			
			escogiendo[this.id]=true;
			numero[this.id]=++max;
			escogiendo[this.id]=false;
			
 			for (int j=0;j<escogiendo.length;j++) {
				while(escogiendo[j]&&j!=this.id);
				while (numero[j]!=0 && (numero[j]<numero[this.id]||(numero[j]==numero[this.id]&&j<this.id))) {
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				};
			}
			
			//inicio seccion critica>>
			//complejidad de seccion critica forzada para testear ruptura de coherencia
			int t = MainLamportLimiteVuelta.contadorCritico;
			try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
			MainLamport.contadorCritico=t+this.valorOperacionCritica;
			//<< fin seccion critica
			numero[this.id]=0;
			ciclos--;
			//simular tiempos diferentes tras la seccion critica
			try {
				Thread.sleep(r.nextInt(5));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//imprimir progreso
			if (ciclos%10==0)
			System.out.println("hilo "+this.id+" ciclos restantes:"+ciclos);
		}
	}
}
 