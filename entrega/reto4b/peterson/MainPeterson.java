package reto4b.peterson;


/**
 * Prueba del algoritmo de Peterson
 * Se generan 2 hebras cada una sumando +1 y -1 a una variable compartida en cada ciclo.
 * La exclusion mutua a la hora de acceder a la variable compartida se hace usando el algoritmo de Peterson
 * el cual esta implementado en las hebras.
 * Cada hebra da el mismo numero finito de ciclos con lo que el resultado
 * debe ser 0 si el algoritmo de Peterson ha funcionado 
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class MainPeterson {

	/**
	 * Variable compartida
	 */
	public static int contadorCritico=0;
	
	
	
	
	public static void main(String[] args) {

		//parametros de creacion

		int ciclosPorHebra=5000;
		

		//iniciar las hebras
		HebraPeterson hebra0 =new HebraPeterson(0,ciclosPorHebra,1);
		hebra0.start();
		HebraPeterson hebra1 =new HebraPeterson(1,ciclosPorHebra,-1);
		hebra1.start();
		
		
		//esperar a todas las hebras para imprimir el resultado final
			try {
				hebra0.join();
				hebra1.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
		System.out.println("Valor final Peterson(0 es OK):"+contadorCritico);
	}	
}
