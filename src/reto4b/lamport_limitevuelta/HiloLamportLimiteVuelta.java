package reto4b.lamport_limitevuelta;

import java.util.Random;

public class HiloLamportLimiteVuelta extends Thread {

	//ATRIBUTOS DE CLASE
	//estado de escogiendo
	public static boolean[] escogiendo=new boolean[2];
	//numeros adjudicados
	public static int[][] numero;
	
	//numero maximo de cada vuelta
	public static int tamanoVuelta=0;
	//vuelta mayor (vuelta mayor significa numero con menos prioridad que uno de vuelta no mayor)
	public static int vueltaMayor=0;
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
		numero=new int[n][2];
		tamanoVuelta=n;
	}
	
	
	//METODOS DE OBJETO
	
	/**
	 * Constructor
	 * @param id indice del hilo para su gestion en Lamport
	 */
	public HiloLamportLimiteVuelta(int id, int ciclos,int valorOperacionCritica){
		this.id=id;
		this.ciclos=ciclos;
		this.valorOperacionCritica=valorOperacionCritica;
		}
	
	/**
	 * Devuelve el numero de turno
	 * @return El numero
	 */
	private void getNumero() {
		numero[this.id][0] = ++max;
		//comprobar si nos hemos pasado
		if (numero[this.id][0]>tamanoVuelta) {
			max=0;
			numero[this.id][0] = 1;	
			vueltaMayor=(vueltaMayor==0)?1:0;
		}
		numero[this.id][1] = vueltaMayor;
	}
	
	@Override
	public void run() {
		while (ciclos>0) {
 			
			escogiendo[this.id]=true;
			getNumero();
			escogiendo[this.id]=false;
			
 			for (int j=0;j<escogiendo.length;j++) {
				while(escogiendo[j]&&j!=this.id);
				
				//numero de j no es igual a 0 
				//Y
				//(
				//numero de j es menor que numero de this y vuelta es la misma
				//O
				//vuelta de j es direrente que la de this y vuelta de this = vuelta mayor
				//O
				//numero de j es igual que nuemro de this y vuelta de j es igual que la de this y j es menor que this
				//)
				//
				while (
						numero[j][0]!=0 
						&& 
						(
								(numero[j][0]<numero[this.id][0]&&numero[j][1]==numero[this.id][1]) 
								||
								(numero[j][1]!=numero[this.id][1] && numero[this.id][1]==vueltaMayor)
								||
								(numero[j][0]==numero[this.id][0]&&numero[j][1]==numero[this.id][1]&&j<this.id))
						) {
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				};
			}
			
			//inicio seccion critica>>
 			int t = MainLamportLimiteVuelta.contadorCritico;
			MainLamportLimiteVuelta.contadorCritico=t+this.valorOperacionCritica;
			//<< fin seccion critica
			numero[this.id][0]=0;
			ciclos--;
			//simular tiempos diferentes tras la seccion critica
			try {
				Thread.sleep(r.nextInt(5));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//imprimir progreso cada 10 ciclos
			if (ciclos%10==0)
			System.out.println("hilo "+this.id+" ciclos restantes:"+ciclos);
		}
	}
}
 