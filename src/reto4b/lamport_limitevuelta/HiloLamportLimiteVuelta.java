package reto4b.lamport_limitevuelta;

import java.util.Random;

public class HiloLamportLimiteVuelta extends Thread {

	// ATRIBUTOS DE CLASE#################################
	
	// Estado de hilos escogiendo
	public static boolean[] escogiendo = new boolean[2];
	// Numeros adjudicados a cada hilo (indice 0:numero, indice 1:tipo de vuelta)
	public static int[][] numero;

	// Numero maximo actual
	public static int max = 0;
	
	// Numero maximo de cada vuelta. Cuando max llega a este numero se empieza una nueva vuelta
	public static int tamanoVuelta = 0;
	
	// Vuelta mayor (vuelta con menos prioridad que uno de vuelta no mayor)
	// Va alternando entre 0 y 1
	public static int vueltaMayor = 0;
	

	// ATRIBUTOS DE OBJETO#############################
	//Indice del hilo actual
	private int i = 0;
	
	//Ciclos que debe dar el hilo actual
	private int ciclos;
	
	//valor a sumar para la operacion critica
	private int valorOperacionCritica = 1;
	
	//random para esperas simuladas
	private Random r = new Random();

	
	// METODOS DE CLASE  ####################################
	/**
	 * Configura la cantidad de hilos soportados por Lamport
	 * 
	 * @param n Cantidad de hilos
	 */
	public static void setNumeroHebras(int n) {
		escogiendo = new boolean[n];
		numero = new int[n][2];
		tamanoVuelta = n;
	}

	// METODOS DE OBJETO  ##############################################

	/**
	 * Constructor
	 * 
	 * @param id indice del hilo para su gestion en Lamport
	 */
	public HiloLamportLimiteVuelta(int id, int ciclos, int valorOperacionCritica) {
		this.i = id;
		this.ciclos = ciclos;
		this.valorOperacionCritica = valorOperacionCritica;
	}

	/**
	 * Asigna el numero de turno al hilo actual.
	 * En su indice 0 pone el numero y en el 1 el tipo de vuelta (0,1)
	 * Este ultimo valor sera comparado con vueltaMayor para determinar prioridad ante un numero igual
	 * 
	 */
	private void cogerNumero() {
		//incrementar max y asignar numero
		numero[this.i][0] = ++max;
		// comprobar si se ha terminado la vuelta y en su caso se cambia la vuelta mayor
		if (numero[this.i][0] > tamanoVuelta) {
			max = 0;
			numero[this.i][0] = 1;
			vueltaMayor = (vueltaMayor == 0) ? 1 : 0;
		}
		//asignar el tipo de vuelta
		numero[this.i][1] = vueltaMayor;
	}

	@Override
	public void run() {
		//ciclos que realizara el hilo antes de morir
		while (ciclos > 0) {

			//levantar bandera de escogiendo
			escogiendo[this.i] = true;
			//coger numero
			cogerNumero();
			//bajar bandera de escogiendo
			escogiendo[this.i] = false;

			//comprobar si el resto de hilos tiene un numero menor que el hilo actual
			for (int j = 0; j < escogiendo.length; j++) {
				//ESPERAR SI J ESTA COGIENDO NUMERO
				while (escogiendo[j] && j != this.i);

				//ESPERAR SI HAY OTRO HILO CON NUMERO MENOR. EL NUMERO MENOR DEPENDE DEL NUMERO Y DEL TIPO DE VUELTA
				// j:otra hebra, i:hebra actual				
				// Esperar si se da ALGUNO de estos dos casos:
				//1-Numero de j no es 0, j e i tienen el mismo tipo de vuelta Y se da ALGUNO de estos casos:  
				//    2.1:El numero de j es menor que el numero de i
				//	  2.2: j es menor que i
				//2-Numero de j no es 0, tienen diferente tipo de vuelta Y el tipo de vuelta de j tiene mas prioridad que la vuelta de i
				
				//Leyenda e condicion de while
				// Numero de j no es igual a 0
				// Y(
				//   (
				//    Vuelta de j es igual a vuelta de i
				//      Y(
				// 		  Numero de j es menor que numero de i 
				// 		  O
				//		  (Numero de j es igual que numero de i  Y   j es menor que i)
				//      )
				//    )
				//	 O
				// 	 Vuelta de j es diferente que la de i y vuelta de i es una vuelta mayor(vuelta de menor prioridad)
				// )
				//
				while (numero[j][0] != 0 //Numero de j no es 0
						&&( 
							(
							  numero[j][1] == numero[this.i][1] //los tipos de vuelta son iguales
							  &&(
								  numero[j][0] < numero[this.i][0] //el numero de j es menor que el de i
								  || 
								  (numero[j][0] == numero[this.i][0] 	&& j < this.i) //numero de j es igual que el numero de this y j es menor que this.i
							    )
							)
							|| 
							(numero[j][1] != numero[this.i][1] && numero[this.i][1] == vueltaMayor) //vueltas diferentes siendo la de i de menor prioridad
						  ) 
						) 
				{
					try {Thread.sleep(1);} catch (InterruptedException e) {	e.printStackTrace();}
				}
				
			}//fin for comprobar hilos

			
			
			// inicio seccion critica>>>
			int t = MainLamportLimiteVuelta.contadorCritico;
			MainLamportLimiteVuelta.contadorCritico = t + this.valorOperacionCritica;
			// <<<fin seccion critica
			
			
			
			
			//tirar numero
			numero[this.i][0] = 0;
			
		
			// simular tiempos diferentes tras la seccion critica
			try {Thread.sleep(r.nextInt(10));} catch (InterruptedException e) {e.printStackTrace();}
			
			// imprimir progreso cada 10 ciclos
			if (ciclos % 10 == 0)
				System.out.println("hilo " + this.i + " ciclos restantes:" + ciclos);
			
			//reducir ciclos de vida del hilo
			ciclos--;
		}
	}
}
