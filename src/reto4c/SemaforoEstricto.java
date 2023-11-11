package reto4c;

import java.util.Random;

public class SemaforoEstricto {
	private int n = 0;

	// hebras encoladas y bloqueadas esperando al semaforo
	private boolean[] bloqueadas;
	private int[] tiempoloqueadas;
	private long[] prioridadDesbloqueo;
	// lamport
	// Estado de hebras escogiendo
	private boolean[] escogiendo = new boolean[2];
	// Numeros adjudicados a cada hebra (indice 0:numero, indice 1:tipo de vuelta)
	private int[][] numero;

	// Numero maximo actual
	private int max = 0;

	// Numero maximo de cada vuelta. Cuando max llega a este numero se empieza una
	// nueva vuelta
	private int tamanoVuelta = 0;

	// Vuelta mayor (vuelta con menos prioridad que uno de vuelta no mayor)
	// Va alternando entre 0 y 1
	private int vueltaMayor = 0;

	public SemaforoEstricto(int n, int nHebras) {
		this.n = n;
		bloqueadas = new boolean[nHebras];
		prioridadDesbloqueo = new long[nHebras];
		escogiendo = new boolean[nHebras];
		numero = new int[nHebras][2];
		tamanoVuelta = nHebras;
	}

	public void esperar() {
		int i = ((HiloPeticionBD) Thread.currentThread()).getIndice();
		lamportEspera();
		n--;
		if (n < 0) {
			// bloquear esta
			bloqueadas[i] = true;
			prioridadDesbloqueo[i]=System.currentTimeMillis();
		}

		lamportLibera();
		// espera ocupada si esta bloqueado
		while (bloqueadas[i] == true) {
			try {
				Thread.currentThread().sleep(1);
			} catch (InterruptedException e) {
			}
		}
	}

	public void senalar() {
		Random r = new Random();
		lamportEspera();
		n++;
		try {
			Thread.currentThread().sleep(1);
		} catch (InterruptedException e) {
		}
		// sacar un bloqueado si hay alguno
		if (hayBloqueados()) {
			boolean desbloqueado = false;
			int indiceBloqueo =0;
			long menorDesbloqueo=0;
			while (!desbloqueado) {
				for (int i=0; i<prioridadDesbloqueo.length;i++) {
					if (menorDesbloqueo==0||(menorDesbloqueo>prioridadDesbloqueo[i] && prioridadDesbloqueo[i]!=0)) {
						menorDesbloqueo=prioridadDesbloqueo[i];
						indiceBloqueo=i;
					}
					}
				
				
				if (bloqueadas[indiceBloqueo]) {
					bloqueadas[indiceBloqueo] = false;
					prioridadDesbloqueo[indiceBloqueo]=0;
					desbloqueado = true;
				}
			}
		}
		try {
			Thread.currentThread().sleep(1);
		} catch (InterruptedException e) {
		}
		lamportLibera();
	}

	private void lamportEspera() {
		// recoger indice del hilo actual
		int i = ((HiloPeticionBD) Thread.currentThread()).getIndice();
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

	private void lamportLibera() {
		int i = ((HiloPeticionBD) Thread.currentThread()).getIndice();
		// tirar numero
		numero[i][0] = 0;
	}

	private void cogerNumero(int i) {
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

	private boolean hayBloqueados() {
		for (int i = 0; i < bloqueadas.length; i++) {
			if (bloqueadas[i])
				return true;
		}
		return false;
	}
}
