package reto4c;

import java.util.Random;

public class HiloEscritor extends HiloConsultaBD {
	public HiloEscritor(int indice, BaseDatos bd, int ciclos) {
		super(indice, bd, ciclos);
	}

	@Override
	public void run() {
		for (int c = 0; c < ciclos; c++) {
			int idTupla = new Random().nextInt(bd.getNumeroTuplas());
			bd.update(idTupla);
			if (MainBaseDatos.debugUpdate) 
				System.out.println("Hilo " + this.indice + " escribe en la tupla " + idTupla);
			
			if (MainBaseDatos.debugProgresoUpdate)
				System.out.println("Progreso del hilo escritor id "+this.indice+": " + (c * 100 / (ciclos-1)) + "%");

			if (MainBaseDatos.esperaAleatoriaEnCiclosDeHebras > 0)
				try {
					Thread.sleep(new Random().nextInt(MainBaseDatos.esperaAleatoriaEnCiclosDeHebras));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

}
