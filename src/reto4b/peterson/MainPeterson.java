package reto4b.peterson;

public class MainPeterson {

	
	public static int contadorCritico=0;
	
	
	
	
	public static void main(String[] args) {

		//parametros de creacion
		int cantidadHebras=2;
		int ciclosPorHebra=10000;
		

		//iniciar todas las hebras
		HebraPeterson[] hebras= new HebraPeterson[cantidadHebras];		
		for (int i = 0; i<cantidadHebras;i++) {
			int valor=1;
			//alternas hebras sumando y restando (el resultado final deberia se 0)
			if (i%2==0)
				valor=-1;
			HebraPeterson hebra= new HebraPeterson(i,ciclosPorHebra,valor);
			hebras[i]=hebra;
			hebra.start();
		}
		
		
		//esperar a todas las hebras para imprimir el resultado final
		for (HebraPeterson hebra : hebras) {
			try {
				hebra.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Valor final Peterson(0 es OK):"+contadorCritico);
	}	
}
