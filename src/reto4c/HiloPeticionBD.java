package reto4c;

import java.util.Random;

public class HiloPeticionBD extends Thread {

	int indice;
	BaseDatos bd;
	int ciclos;
	
	public HiloPeticionBD(int indice, BaseDatos bd, int ciclos) {
		super();
		this.indice = indice;
		this.bd=bd;
		this.ciclos=ciclos*2;
	}

	public int getIndice() {
		return indice;
	}

	@Override
	public void run() {
 
		//decidir si empieza leyendo o escribiendo
		int escribir=new Random().nextInt(2);
		
		for (int c=0;c<ciclos;c++) {
			if (c%2==escribir) {
				bd.update();
				System.out.println("Hilo "+this.indice+" escribe");
			}
			else
				System.out.println("Hilo "+this.indice+" lee: "+bd.select());
			try {
				this.sleep(new Random().nextInt(1000));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	 
	
	
}
