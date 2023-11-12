package reto4c;

import java.util.Random;

public class HiloLector extends HiloConsultaBD {

	public HiloLector(int indice, BaseDatos bd, int ciclos) {
		super(indice, bd, ciclos);
	}

	@Override
	public void run() {
		for (int c = 0; c < ciclos; c++) {
			int idTupla = new Random().nextInt(bd.getNumeroTuplas());
			int valorLeido=bd.select(idTupla);
			if (MainBaseDatos.debugSelect) 
				System.out.println("Hilo " + this.indice + " lee tupla " + idTupla + " y obtiene valor: " + valorLeido);
			
			if (MainBaseDatos.debugProgresoSelect)
				System.out.println("Progreso del hilo lector id "+this.indice+": "  + (c * 100 / (ciclos-1)) + "%");
			
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
