package reto4c;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Punto de entrada a la aplicacion.
 * Configura el estado inicial, crea el objeto de base de datos y los hilos que leeran y escribiran
 * al terminar muestra informacion sobre la ejecucion
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class MainBaseDatos {
	
	/**
	 * Milisegundos maximos que cada hebra espera entre cada ciclo. 
	 * Se puede dejar a 0 o aumentar si se quiere aleatoriedad en la frecuecia en la que cada hilo 
	 * intenta acceder a la base de datos
	 */
	public static int esperaAleatoriaEnCiclosDeHebras = 0;
	
	/**
	 * Mostrar los mensajes de lectura de base de datos
	 */
	public static boolean debugSelect=true;
	
	/**
	 * Mostrar los mensajes de escritura de base de datos
	 */
	public static boolean debugUpdate=true;
	
	/**
	 * Mostrar mensajes de progreso de ciclos de los hilos de lectura 
	 */
	public static boolean debugProgresoSelect=true;
	
	/**
	 * Mostrar mensajes de progreso de ciclos de los hilos de escritura 
	 */
	public static boolean debugProgresoUpdate=true;
	
	
	
	/**
	 * Funcion Main del programa
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		//PARAMETROS DE CONFIGURACION
		
		//ruta del archivo de la base de datos
		String rutaArchivo = "basedatos.dat";
		//establecer que hay prioridad en la lectura
		boolean prioridadEnLectura=false;
		//Usar semaforos fuertes en vez de debiles
		boolean semaforosFuertes = true;
		
		//catindad de hebras que leeran
		int hebrasLectura = 30;
		
		//cantidad de hebras que escribiran
		int hebrasEscritura = 30;
		
		//cantidad de lecturas o escrituras por hebra 
		int ciclos = 50;
		
		//cantidad de tuplas existentes en la base de datos
		int numeroTuplas = 30;
		
		//iniciar las hebras en un orden aleatorio
		boolean startAleatorioDeHebras = true;

		
		
		
		
		//CREACION DE BASE DE DATOS E HILOS
		
		//registro del tiempo de inicio para informe final
		long tiempoInicio = System.currentTimeMillis();
		
		//calculo del total de hebras
		int totalHebras = hebrasLectura + hebrasEscritura;
		
		//creacion de la base de datos
		BaseDatos bd = new BaseDatos(rutaArchivo, numeroTuplas, totalHebras, prioridadEnLectura,semaforosFuertes);

		// crear los hilos
		ArrayList<HiloConsultaBD> hilos = new ArrayList<HiloConsultaBD>();
		//crear hebras lectores
		int i = 0;
		for (i = 0; i < hebrasLectura; i++) {
			hilos.add(new HiloLector(i, bd, ciclos));
		}
		
		//crear hebras escritoras
		for (i = hebrasLectura; i < totalHebras; i++) {
			hilos.add(new HiloEscritor(i, bd, ciclos));
		}
		
		
		//start de hilos en una secuencia aleatoria
		if (startAleatorioDeHebras) {
			ArrayList<HiloConsultaBD> listaInicioHilos = new ArrayList<HiloConsultaBD>(hilos);
			while (listaInicioHilos.size() > 0)
				listaInicioHilos.remove(new Random().nextInt(listaInicioHilos.size())).start();
		//start de los hilos de manera ordenada
		} else
			for (HiloConsultaBD hilo : hilos)
				hilo.start();
		
		
		// esperar a que terminen todos los hilos
		System.out.println("Realizando consultas a la base de datos:");
		for (int j = 0; j < totalHebras; j++) {
			try {
				hilos.get(j).join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// Mostrar informe de resultado de los datos almacenados en la base de datos
		List<Integer> tuplas = bd.getTodasLasTuplas();
		System.out.println("************************************");
		System.out.println("Contenido de la base de datos. Ruta: "+bd.getRuta());
		System.out.println("************************************");
		System.out.println(tuplas);
		System.out.println("Suma total de tuplas esperada:\t" + (hebrasEscritura * ciclos));
		System.out.println("Suma real de tuplas en archivo:\t"+ tuplas.stream().reduce(0, (a, b) -> a + b));
		System.out.println("Total de tiempo: " + ((System.currentTimeMillis() - tiempoInicio) / 1000) + " segundos");
		System.out.println("Hebras de lectura: "+hebrasLectura);
		System.out.println("Hebras de escritura: "+hebrasEscritura);
		System.out.println("Consulas realizados por hebra: "+ciclos);
	}

}
