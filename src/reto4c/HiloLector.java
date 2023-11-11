package reto4c;

import java.util.Random;

public class HiloLector extends HiloPeticionBD {

 
	
	public HiloLector(int indice, BaseDatos bd, int ciclos) {
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
			
				System.out.println("Hilo "+this.indice+" lee: "+bd.select(new Random().nextInt(100)));
			
		}
		
		try {
			Thread.sleep(new Random().nextInt(10));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	 
	
	
}
