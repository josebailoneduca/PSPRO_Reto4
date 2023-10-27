package reto4b.lamport_limitevuelta;

public class MainLamportLimiteVuelta {

	
	public static int contadorCritico=0;
	
	
	
	
	public static void main(String[] args) {

		//parametros de creacion
		int cantidadHebras=1000;
		int ciclosPorHebra=50;
		
		//configuracion dde los arrays para lamport
		HiloLamportLimiteVuelta.setNumeroHebras(cantidadHebras);
		
		//iniciar todos los hilos
		HiloLamportLimiteVuelta[] hilos= new HiloLamportLimiteVuelta[cantidadHebras];		
		for (int i = 0; i<cantidadHebras;i++) {
			int valor=1;
			//alternas hebras sumando y restando (el resultado final deberia se 0)
			if (i%2==0)
				valor=-1;
			HiloLamportLimiteVuelta hebra= new HiloLamportLimiteVuelta(i,ciclosPorHebra,valor);
			hilos[i]=hebra;
			hebra.start();
		}
		
		
		//esperar a todos los hilos para imprimir el resultado final
		for (HiloLamportLimiteVuelta hiloLamport : hilos) {
			try {
				hiloLamport.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("******************************");
		System.out.println("Valor final (0 es OK):"+contadorCritico);
		System.out.println("******************************");
	}	
}
