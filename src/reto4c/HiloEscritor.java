package reto4c;

import java.util.Random;

public class HiloEscritor extends HiloPeticionBD {

 
	
	public HiloEscritor(int indice, BaseDatos bd, int ciclos)  {
		super(indice, bd, ciclos);

		this.ciclos=ciclos;
	}

	public int getIndice() {
		return indice;
	}

	@Override
	public void run() {
 
		//decidir si empieza leyendo o escribiendo
		int escribir=new Random().nextInt(2);
		
		for (int c=0;c<ciclos;c++) {
 
				bd.update(new Random().nextInt(100));
				System.out.println("Hilo "+this.indice+" escribe");
			 
		}
	}

	 
	
	
}
