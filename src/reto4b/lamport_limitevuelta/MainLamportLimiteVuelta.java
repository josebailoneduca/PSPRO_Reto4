package reto4b.lamport_limitevuelta;

public class MainLamportLimiteVuelta {

	
	public static int contadorCritico=0;
	
	
	
	
	public static void main(String[] args) {

		//parametros de creacion
		int cantidadHebras=500;
		int ciclosPorHebra=50;
		
		//configuracion dde los arrays para lamport
		HebraLamportLimiteVuelta.setNumeroHebras(cantidadHebras);
		
		//iniciar todas las hebras
		HebraLamportLimiteVuelta[] hebras= new HebraLamportLimiteVuelta[cantidadHebras];		
		for (int i = 0; i<cantidadHebras;i++) {
			int valor=1;
			//alternas hebras sumando y restando (el resultado final deberia se 0)
			if (i%2==0)
				valor=-1;
			HebraLamportLimiteVuelta hebra= new HebraLamportLimiteVuelta(i,ciclosPorHebra,valor);
			hebras[i]=hebra;
			hebra.start();
		}
		
		
		//esperar a todas las hebras para imprimir el resultado final
		for (HebraLamportLimiteVuelta hebra : hebras) {
			try {
				hebra.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("******************************");
		System.out.println("Valor final (0 es OK):"+contadorCritico);
		System.out.println("******************************");
	}	
}
