package reto4b.lamport_simplificado;

import java.util.Random;

/**
 * Hebra que implementa una version simplificada del algoritmo de Lamport en el que no
 * se tiene en cuenta la posibilidad de que se llegue al limite de numero maximo y por tanto
 * necesidad de contemplar vueltas del numero
 */
public class HebraLamportSimplificado extends Thread {

	//ATRIBUTOS DE CLASE
	//estado de escogiendo
	public static boolean[] escogiendo;
	//numeros adjudicados
	public static int[] numero;

	//numero maximo actual
	public static int max = 0; 
	
	
	//ATRIBUTOS DE OBJETO
	private int id=0;
	private int ciclos;
	private int valorOperacionCritica=1;
 
	// METODOS DE CLASE  ####################################
	/**
	 * Configurar la cantidad de hebras soportados por Lamport	
	 * @param n Cantidad de hebras
	 */
	public static void setNumeroHebras(int n) {
		escogiendo=new boolean[n];
		numero=new int[n];
	}
	
	
	//METODOS DE OBJETO  #########################################
	
	/**
	 * Constructor
	 * @param id indice del hebra para su gestion en Lamport
	 */
	public HebraLamportSimplificado(int id, int ciclos,int valorOperacionCritica){
		this.id=id;
		this.ciclos=ciclos;
		this.valorOperacionCritica=valorOperacionCritica;
		}
	
	
	@Override
	public void run() {
		while (ciclos>0) {
 			//subir bandera propia de escogiendo
			escogiendo[this.id]=true;
			//coger siguiente numero
			numero[this.id]=++max;
			//bajar bandera de escogiendo
			escogiendo[this.id]=false;
			
			//recorrer hebras para ver si se tiene el numero minimo
 			for (int j=0;j<escogiendo.length;j++) {
 				//si la hebra j esta escogiendo esperar
				while(escogiendo[j]);
				//si la hebra j tiene un numero menor valido o tiene un numero igual pero una id menor esperar
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
			int t = MainLamportSimplificado.contadorCritico;

			//complejidad de seccion critica forzada para testear ruptura de coherencia
			try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
			
			//incrementar la variable compartida
			MainLamportSimplificado.contadorCritico=t+this.valorOperacionCritica;
			//<< fin seccion critica
			
			numero[this.id]=0;
			ciclos--;
			
			
			//simular tiempos diferentes en seccion no critica
			try {
				Thread.sleep(new Random().nextInt(5));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//imprimir progreso cada 5 ciclos
			if (ciclos%5==0)
			System.out.println("Hebra Lamport nº "+this.id+". Ciclos restantes:"+ciclos);
		}
	}
}
 