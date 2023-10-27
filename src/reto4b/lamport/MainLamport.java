package reto4b.lamport;

public class MainLamport {

	
	public static int contadorCritico=0;
	
	
	
	
	public static void main(String[] args) {

		//parametros de creacion
		int cantidadHebras=400;
		int ciclosPorHebra=100;
		
		//configuracion dde los arrays para lamport
		HiloLamport.setNumeroHebras(cantidadHebras);
		
		//iniciar todos los hilos
		HiloLamport[] hilos= new HiloLamport[cantidadHebras];		
		for (int i = 0; i<cantidadHebras;i++) {
			int valor=1;
			//alternas hebras sumando y restando (el resultado final deberia se 0)
			if (i%2==0)
				valor=-1;
			HiloLamport hebra= new HiloLamport(i,ciclosPorHebra,valor);
			hilos[i]=hebra;
			hebra.start();
		}
		
		
		//esperar a todos los hilos para imprimir el resultado final
		for (HiloLamport hiloLamport : hilos) {
			try {
				hiloLamport.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Valor final (0 es OK):"+contadorCritico);
	}	
}
