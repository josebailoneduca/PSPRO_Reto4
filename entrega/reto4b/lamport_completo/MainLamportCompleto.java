package reto4b.lamport_completo;

/**
 * Prueba del algoritmo de Lamport
 * Se generan multiples hebras cada una sumando +1 o -1  a una variable compartida en cada ciclo.
 * La exclusion mutua a la hora de acceder a la variable compartida se hace usando el algoritmo de Lamport
 * el cual esta implementado en las hebras. Esta version contempla un numero maximo finito y la gestion 
 * de vueltas para solventar ese limite
 * 
 * Cada hebra da el mismo numero finito de ciclos y en cada ciclo incrementa la variable compartada. 
 * Como  hay tantas hebras +1 como -1 el resultado debe ser 0 si el algoritmo de Lamport ha funcionado
 * 
 *  @author Jose Javier Bailon Ortiz
 */
public class MainLamportCompleto {

	/**
	 * variable compartida
	 */
	public static int contadorCritico=0;
	
	
	
	
	public static void main(String[] args) {

		//parametros de creacion
		int mitadDeHebras=50;
		int ciclosPorHebra=100;
		
		//configuracion de los arrays para lamport
		int cantidadHebras=mitadDeHebras*2;
		HebraLamportCompleto.setNumeroHebras(cantidadHebras);
		
		//iniciar todas las hebras
		HebraLamportCompleto[] hebras= new HebraLamportCompleto[cantidadHebras];		
		for (int i = 0; i<cantidadHebras;i++) {
			//alternas hebras sumando y restando 
			int valor=(i%2==0)?-1:1;
			HebraLamportCompleto hebra= new HebraLamportCompleto(i,ciclosPorHebra,valor);
			hebras[i]=hebra;
			hebra.start();
		}
		
		
		//esperar a todas las hebras para imprimir el resultado final
		for (HebraLamportCompleto hebra : hebras) {
			try {
				hebra.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("******************************");
		System.out.println("Valor final (0 es OK):"+contadorCritico);
		System.out.println("******************************");
		System.out.println("Cantidad de hebras: "+cantidadHebras);
		System.out.println("Accesos críticos realizados: "+cantidadHebras*ciclosPorHebra);

	}	
}
