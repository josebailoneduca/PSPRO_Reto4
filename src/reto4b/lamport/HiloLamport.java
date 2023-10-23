package reto4b.lamport;

public class HiloLamport extends Thread {

	//ATRIBUTOS DE CLASE
	private static boolean[] escoger=new boolean[2];
	private static int[][] numero=new int[2][2];
	private static int tamanoVuelta=0;
	private static int vueltaMenor=1;
	
	
	//ATRIBUTOS DE OBJETO
	private int id=0;
	private int ciclos;
	private int valor=1;
	// METODOS DE CLASE
	/**
	 * Configurar la cantidad de hilos soportados por Lamport	
	 * @param n Cantidad de hios
	 */
	public static void setNumeroHebras(int n) {
		escoger=new boolean[n];
		numero=new int[n][2];
		tamanoVuelta=n;
	}
	
	
	//METODOS DE OBJETO
	
	/**
	 * Constructor
	 * @param id indice del hilo para su gestion en Lamport
	 */
	public HiloLamport(int id, int ciclos,int valor){
		this.id=id;
		this.ciclos=ciclos;
		this.valor=valor;
	}
	
	
	@Override
	public void run() {
		while (ciclos>0) {
			int t = MainLamport.contadorCritico;
			MainLamport.contadorCritico=t+this.valor;
			ciclos--;
		}
		
		
	}
	
	
	
}





//while (true)
//{
//	escoger[i] = true;
//	número[i] = max(número[0],..., número[N - 1]) + 1;
//	escoger[i] = false;
//	for (int j = 0; j < N; ++j)
//	{
//		while (escoger[j]);
//		while (número[j] != 0 && (número[j],j) < (número[i], i));
//	}
//	sección_crítica();
//	número[i] = 0;
//	sección_no_crítica();
//}