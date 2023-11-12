package reto4c;

import java.util.Random;

public class SemaforoEstricto extends Semaforo{
 

	private long[] prioridadDesbloqueo;

 
 

	public SemaforoEstricto(int n, int nHebras) {
		super(n,nHebras);
		prioridadDesbloqueo = new long[nHebras];
		
	}

	protected void bloquear(int i) {
		bloqueadas[i] = true;
		prioridadDesbloqueo[i] = System.currentTimeMillis();
	}
	
	protected void desbloquear() {
		boolean desbloqueado = false;
		int indiceBloqueo = 0;
		long menorDesbloqueo = 0;
		while (!desbloqueado) {
			for (int i = 0; i < prioridadDesbloqueo.length; i++) {
				if (menorDesbloqueo == 0
						|| (menorDesbloqueo > prioridadDesbloqueo[i] && prioridadDesbloqueo[i] != 0)) {
					menorDesbloqueo = prioridadDesbloqueo[i];
					indiceBloqueo = i;
				}
			}

			if (bloqueadas[indiceBloqueo]) {
				bloqueadas[indiceBloqueo] = false;
				prioridadDesbloqueo[indiceBloqueo] = 0;
				desbloqueado = true;
			}
		}
	}
}
