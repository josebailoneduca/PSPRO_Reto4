package reto4c;

import java.util.ArrayList;

public class MainPruebaSemaforo {
public static int suma =0;
	public static void main(String[] args) {
		int totalHebras=100;
		int ciclos=100;
		BaseDatos bd = new BaseDatos(totalHebras);
		ArrayList<HiloPeticionBD> hilos=new ArrayList<HiloPeticionBD> (); 
		for(int i=0;i<totalHebras;i++) {
			
			hilos.add(new HiloPeticionBD(i, bd,ciclos));
			hilos.get(i).start();
		}
		for(int i=0;i<totalHebras;i++) {
			try {
				hilos.get(i).join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println("total "+bd.getTupla());
	}

}
