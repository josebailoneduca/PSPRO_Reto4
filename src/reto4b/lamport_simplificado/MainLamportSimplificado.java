package reto4b.lamport_simplificado;





/**
 * Prueba del algoritmo de Lamport
 * Se generan multiples hebras cada una sumando +1 o -1 a una variable compartida en cada ciclo.
 * La exclusion mutua a la hora de acceder a la variable compartida se hace usando el algoritmo de Lamport
 * el cual esta implementado en las hebras y simplificado sin tener en cuenta el limite de numero maximo.
 * 
 * Cada hebra da el mismo numero finito de ciclos y hay tantas hebras +1 como -1 con lo que el resultado
 * debe ser 0 si el algoritmo de Lamport ha funcionado 
 */
public class MainLamportSimplificado {

	//variable compartida
	public static int contadorCritico=0;
	
	
	
	
	public static void main(String[] args) {

		//parametros de creacion
		int cantidadHebras=100;
		int ciclosPorHebra=100;
		
		//configuracion de los arrays del algoritmo Lamport para poder tener banderas para todas las hebras
		HebraLamportSimplificado.setNumeroHebras(cantidadHebras);
		
		//iniciar todas las hebras
		HebraLamportSimplificado[] hebras= new HebraLamportSimplificado[cantidadHebras];		
		for (int i = 0; i<cantidadHebras;i++) {
			try {Thread.sleep(5);} catch (InterruptedException e) {}
			//alternar hebras sumando y restando 
			int valor=(i%2==0)?-1:1;
			HebraLamportSimplificado hebra= new HebraLamportSimplificado(i,ciclosPorHebra,valor);
			hebras[i]=hebra;
			hebra.start();
		}
		
		
		//esperar a todas las hebras para imprimir el resultado final
		for (HebraLamportSimplificado hebra : hebras) {
			try {
				hebra.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//imprimir el resultado final
		System.out.println("Valor final (0 es OK):"+contadorCritico);
	}	
}
