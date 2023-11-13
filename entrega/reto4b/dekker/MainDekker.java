package reto4b.dekker;

/**
 * Prueba del algoritmo de Dekker
 * Se generan 2 hebras cada una sumando +1 y -1 a una variable compartida en cada ciclo.
 * La exclusion mutua a la hora de acceder a la variable compartida se hace usando el algoritmo de Dekker
 * el cual esta implementado en las hebras.
 * Cada hebra da el mismo numero finito de ciclos con lo que el resultado
 * debe ser 0 si el algoritmo de Dekker ha funcionado
 *  
 * @author Jose Javier Bailon Ortiz
 */
public class MainDekker {

	//variable compartida que cada hebra aumenta y disiminuye
	public static int contadorCritico=0;
	
	
	
	
	public static void main(String[] args) {

		//parametros de creacion
		int ciclosPorHebra=3000;
		

		//iniciar los hilos
		HebraDekker hebra0 =new HebraDekker(0,ciclosPorHebra,1);
		hebra0.start();
		HebraDekker hebra1 =new HebraDekker(1,ciclosPorHebra,-1);
		hebra1.start();
		
		
		//esperar a todos los hilos para imprimir el resultado final
			try {
				hebra0.join();
				hebra1.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		System.out.println("Valor final Dekker(0 es OK): "+contadorCritico);
	}	
}
