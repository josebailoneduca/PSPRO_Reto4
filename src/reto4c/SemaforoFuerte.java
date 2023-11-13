package reto4c;

import java.util.Random;


/**
 * Semaforo implementado usando el algoritmo de Lamport para el mutex de las secciones criticas
 * Se trata de un semaforo fuerte basado en el semaforo debil modificando el mecanismo de bloqueo y desbloqueo
 * En este caso en vez de ser aleatorio el desbloqueo desbloquea la hebra que tenga registrado un tiempo anterior de acceso al semaforo
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class SemaforoFuerte extends Semaforo{
 
	/**
	 * Registra la prioridad para ser desbloqueado. A menor numero mayor priioridad. Valor 0 no es tenido en cuenta
	 */
	private long[] prioridadDesbloqueo;

 
 
	/**
	 * Construcor
	 * @param n Numero inicial del semarofo
	 * @param nHebras Cantidad de hebras maximo que puede manejar sus mecanismos de mutex
	 */
	public SemaforoFuerte(int n, int nHebras) {
		super(n,nHebras);
		prioridadDesbloqueo = new long[nHebras];
	}

	/**
	 * Bloquea un hilo registrando el tiempo en el que ha sido bloqueado
	 */
	protected void bloquear(int i) {
		bloqueadas[i] = true;
		prioridadDesbloqueo[i] = System.currentTimeMillis();
	}
	
	
	/**
	 * Desbloquea un hilo siendo este el que tenga el menor tiempo registrado en el que ha sido bloqueado
	 */
	protected void desbloquear() {
		
		//busqueda del bloqueado mas antiguo
		boolean desbloqueado = false;
		int indiceBloqueo = 0;
		long menorDesbloqueo = 0;
		while (!desbloqueado) {
			for (int i = 0; i < prioridadDesbloqueo.length; i++) {
				if (menorDesbloqueo == 0 || (menorDesbloqueo > prioridadDesbloqueo[i] && prioridadDesbloqueo[i] != 0)) {
					menorDesbloqueo = prioridadDesbloqueo[i];
					indiceBloqueo = i;
				}
			}

			if (bloqueadas[indiceBloqueo]) {
				//desbloqueo del encontrado si esta bloqueado
				bloqueadas[indiceBloqueo] = false;
				//puesta de su registro de espera a 0
				prioridadDesbloqueo[indiceBloqueo] = 0;
				//marcar que se ha podido desbloquear
				desbloqueado = true;
			}
		}
	}
}
