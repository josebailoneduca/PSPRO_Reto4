package reto4b.lamport;

public class MainLamport {

	
	public static int contadorCritico=0;
	
	
	
	
	public static void main(String[] args) {

		int cantidadHebras=100000;
		int ciclosPorHebra=50;
		HiloLamport.setNumeroHebras(cantidadHebras);
				
		
		for (int i = 0; i<cantidadHebras;i++) {
			int valor=1;
			if (i%2==0)
				valor=-1;
				
			HiloLamport hebra= new HiloLamport(i,ciclosPorHebra,valor);
			hebra.start();
			try {
				hebra.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("total:"+contadorCritico);
	}	
	
	
	
}
