package reto4c;

import java.util.ArrayList;

public class MainBaseDatosSeparado {
public static int suma =0;
	public static void main(String[] args) {
		int totalHebras=20;
		int ciclos=50;
		BaseDatos bd = new BaseDatos(totalHebras);
		ArrayList<HiloPeticionBD> hilos=new ArrayList<HiloPeticionBD> (); 
		for(int i=0;i<totalHebras;i++) {
			if (i%2==0)
			hilos.add(new HiloEscritor(i, bd,ciclos));
			else
				hilos.add(new HiloLector(i, bd,ciclos));
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
