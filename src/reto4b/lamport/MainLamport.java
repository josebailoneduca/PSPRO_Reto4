package reto4b.lamport;

public class MainLamport {

	
	public static int contadorCritico=0;
	
	
	
	
	public static void main(String[] args) {

		//parametros de creacion
		int cantidadHebras=400;
		int ciclosPorHebra=100;
		
		//configuracion de los arrays para lamport
		HebraLamport.setNumeroHebras(cantidadHebras);
		
		//iniciar todas las hebras
		HebraLamport[] hebras= new HebraLamport[cantidadHebras];		
		for (int i = 0; i<cantidadHebras;i++) {
			int valor=1;
			//alternas hebras sumando y restando (el resultado final deberia se 0)
			if (i%2==0)
				valor=-1;
			HebraLamport hebra= new HebraLamport(i,ciclosPorHebra,valor);
			hebras[i]=hebra;
			hebra.start();
		}
		
		
		//esperar a todas las hebras para imprimir el resultado final
		for (HebraLamport hebra : hebras) {
			try {
				hebra.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Valor final (0 es OK):"+contadorCritico);
	}	
}
