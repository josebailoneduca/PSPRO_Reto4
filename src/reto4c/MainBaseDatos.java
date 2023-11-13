package reto4c;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainBaseDatos {
	public static int esperaAleatoriaEnCiclosDeHebras = 100;
	public static boolean debugSelect=false;
	public static boolean debugUpdate=false;
	public static boolean debugProgresoSelect=true;
	public static boolean debugProgresoUpdate=true;
	
	public static void main(String[] args) {
		
		
		//PARAMETROS DE CONFIGURACION
		
		//ruta del archivo de la base de datos
		String rutaArchivo = "basedatos.dat";
		//establecer que hay prioridad en la lectura
		boolean prioridadEnLectura=false;
		//controla de manera estricta la no inanicion de un hilo
		boolean controlEstrictoDeInanicion = true;
		
		//catindad de hebras que leeran
		int hebrasLectura = 30;
		
		//cantidad de hebras que escribiran
		int hebrasEscritura = 30;
		
		//cantidad de lecturas o escrituras por hebra 
		int ciclos = 50;
		
		//cantidad de tuplas existentes en la base de datos
		int numeroTuplas = 30;
		
		//iniciar las hebras en un orden aleatorio
		boolean startAleatorioDeHebras = false;

		
		
		
		
		long tiempoInicio = System.currentTimeMillis();
		int totalHebras = hebrasLectura + hebrasEscritura;
		BaseDatos bd = new BaseDatos(rutaArchivo, numeroTuplas, totalHebras, prioridadEnLectura,controlEstrictoDeInanicion);

		// crear los hilos
		ArrayList<HiloConsultaBD> hilos = new ArrayList<HiloConsultaBD>();
		int i = 0;
		for (i = 0; i < hebrasLectura; i++) {
			hilos.add(new HiloLector(i, bd, ciclos));
		}

		for (i = hebrasLectura; i < totalHebras; i++) {
			hilos.add(new HiloEscritor(i, bd, ciclos));
		}

		
		
		//start de hilos
		if (startAleatorioDeHebras) {
			ArrayList<HiloConsultaBD> listaInicioHilos = new ArrayList<HiloConsultaBD>(hilos);
			while (listaInicioHilos.size() > 0)
				listaInicioHilos.remove(new Random().nextInt(listaInicioHilos.size())).start();

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

		// Mostrar resultado de los datos almacenados en la base de datos
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
		System.out.println("Ciclos realizados: "+ciclos);
	}

}
