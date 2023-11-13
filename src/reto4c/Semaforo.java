package reto4c;

import java.util.Random;


/**
 * Semaforo implementado usando el algoritmo de Lamport para el mutex de las secciones criticas
 * Se trata de un semaforo debil que libera una hebra no determinada en su funcion senalar.
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class Semaforo{
	
	/**
	 * Numero de hebras que pueden pasar el semaforo sin espera
	 */
	protected int n = 0;
	
	/**
	 * hebras encoladas y bloqueadas esperando al semaforo
	 */
	protected boolean[] bloqueadas;

	// ATRIBUTOS PARA MUTEX POR LAMPORT
	/**
	 * Estado de hebras escogiendo
	 */
	protected boolean[] escogiendo = new boolean[2];
	
	/**
	 * Numeros adjudicados a cada hebra (indice 0:numero, indice 1:tipo de vuelta)
	 */
	protected int[][] numero;

	/**
	 * Numero maximo actual
	 */
	protected int max = 0;

	/**
	 * Numero maximo de cada vuelta. Cuando max llega a este numero se empieza una nueva vuelta
	 */
	protected int tamanoVuelta = 0;

	/**
	 *  Vuelta mayor (vuelta con menos prioridad que uno de vuelta no mayor) Va alternando entre 0 y 1
	 */
	protected int vueltaMayor = 0;

	
	/**
	 * Constructor
	 * @param n Valor inicial del semaforo
	 * @param nHebras Cantidad de hebras maxima con el que configurar los arrays necesarios para Lamport
	 */
	public Semaforo(int n, int nHebras) {
		this.n = n;
		bloqueadas = new boolean[nHebras];
		escogiendo = new boolean[nHebras];
		numero = new int[nHebras][2];
		tamanoVuelta = nHebras;
	}

	
	/**
	 * Espera del semaforo
	 */
	public void esperar() {
		//indice del hilo en ejecucion
		int i = ((HiloConsultaBD) Thread.currentThread()).getIndice();
		//espera lamport
		lamportEspera();
		n--;
		if (n < 0)
			// bloquear esta
			bloquear(i);

		//libera lamport
		lamportLibera();
		
		// espera ocupada si esta bloqueado
		while (bloqueadas[i] == true) {
			try {
				Thread.currentThread().sleep(1);
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * Senala el semaforo
	 */
	public void senalar() {
		//espera lamport
		lamportEspera();
		n++;
		try {
			Thread.currentThread().sleep(1);
		} catch (InterruptedException e) {
		}
		// sacar un bloqueado si hay alguno
		if (hayBloqueados()) {
			desbloquear();
		}
		try {
			Thread.currentThread().sleep(1);
		} catch (InterruptedException e) {
		}
		lamportLibera();
	}

	/**
	 * Bloquea un hilo en el semaforo
	 * @param i Indice del hilo
	 */
	protected void bloquear(int i) {
		bloqueadas[i] = true;
	}


	/**
	 * Desbloquea un hilo que este en espera en el semaforo
	 * La eleccion del hilo desbloqueado es aleatoria haciendo el semaforo debil
	 */
	protected void desbloquear() {
		Random r = new Random();
		boolean desbloqueado = false;
		while (!desbloqueado) {
			int i = r.nextInt(bloqueadas.length);
			if (bloqueadas[i]) {
				bloqueadas[i] = false;
				desbloqueado = true;
			}
		}
		
	}

	/**
	 * Devuelve si hay hilos bloqueados en el semaforo
	 * @return
	 */
	protected boolean hayBloqueados() {
		for (int i = 0; i < bloqueadas.length; i++) {
			if (bloqueadas[i])
				return true;
		}
		return false;
	}


	/**
	 * Parte de espera del algoritmo de Lamport
	 */
	protected void lamportEspera() {
		// recoger indice del hilo actual
		int i = ((HiloConsultaBD) Thread.currentThread()).getIndice();
		// levantar bandera de escogiendo
		escogiendo[i] = true;
		// coger numero
		cogerNumero(i);
		// bajar bandera de escogiendo
		escogiendo[i] = false;

		// comprobar si el resto de hebras tiene un numero menor que la hebra actual
		for (int j = 0; j < escogiendo.length; j++) {
			// ESPERAR SI J ESTA COGIENDO NUMERO
			while (escogiendo[j]) {
				try {
					Thread.currentThread().sleep(1);
				} catch (InterruptedException e) {
				}
			}
			;

			// ESPERAR SI HAY OTRA HEBRA CON NUMERO MENOR. EL NUMERO MENOR DEPENDE DEL
			// NUMERO Y DEL TIPO DE VUELTA
			// j:otra hebra, i:hebra actual
			// Esperar si se da ALGUNO de estos casos:
			// 1-Numero de j no es 0, j e i tienen el mismo tipo de vuelta Y se da ALGUNO de
			// estos casos:
			// 2.1:El numero de j es menor que el numero de i
			// 2.2:El numero de j es igual al numero de i Y j es menor que i
			// 2-Numero de j no es 0, tienen diferente tipo de vuelta Y el tipo de vuelta de
			// j tiene mas prioridad que la vuelta de i

			while (numero[j][0] != 0 // Numero de j no es 0
					&& ((numero[j][1] == numero[i][1] // los tipos de vuelta son iguales
							&& (numero[j][0] < numero[i][0] // el numero de j es menor que el de i
									|| (numero[j][0] == numero[i][0] && j < i) // numero de j es igual que el numero de
																				// this y j es menor que this.i
							)) || (numero[j][1] != numero[i][1] && numero[i][1] == vueltaMayor) // vueltas diferentes
																								// siendo la de i de
																								// menor prioridad
					)) {
				try {
					Thread.currentThread().sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		} // fin for comprobar HEBRAS

		// INICIO SECCION CRITICA>>>
	}

	
	/**
	 * Parte de liberar del algoritmo de Lamport tras la seccion critica
	 */
	protected void lamportLibera() {
		//<< fin seccion critica
		int i = ((HiloConsultaBD) Thread.currentThread()).getIndice();
		// tirar numero
		numero[i][0] = 0;
	}

	/**
	 * Recogida de numero para el algoritmo de Lamport
	 * @param i Indice del hilo
	 */
	protected void cogerNumero(int i) {
		// incrementar max y asignar numero
		numero[i][0] = ++max;
		// comprobar si se ha terminado la vuelta y en su caso se cambia la vuelta de
		// numeros
		if (numero[i][0] > tamanoVuelta) {
			max = 0;
			numero[i][0] = 1;
			vueltaMayor = (vueltaMayor == 0) ? 1 : 0;
		}
		// asignar el tipo de vuelta
		numero[i][1] = vueltaMayor;
	}
}
