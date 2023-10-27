package reto4b.dekker;

public class MainDekker {

	
	public static int contadorCritico=0;
	
	
	
	
	public static void main(String[] args) {

		//parametros de creacion
		int cantidadHebras=2;
		int ciclosPorHebra=10000;
		

		//iniciar todos los hilos
		HebraDekker[] hebras= new HebraDekker[cantidadHebras];		
		for (int i = 0; i<cantidadHebras;i++) {
			int valor=1;
			//alternas hebras sumando y restando (el resultado final deberia se 0)
			if (i%2==0)
				valor=-1;
			HebraDekker hebra= new HebraDekker(i,ciclosPorHebra,valor);
			hebras[i]=hebra;
			hebra.start();
		}
		
		
		//esperar a todos los hilos para imprimir el resultado final
		for (HebraDekker hebra : hebras) {
			try {
				hebra.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Valor final Dekker(0 es OK):"+contadorCritico);
	}	
}
