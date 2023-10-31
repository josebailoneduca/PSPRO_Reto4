package reto4b.peterson;

import java.util.Random;

public class HebraPeterson extends Thread {

	//ATRIBUTOS DE CLASE
	//banderas
	public static boolean[] bandera=new boolean[2];
	public static int turno=0;

	
	//ATRIBUTOS DE OBJETO
	private int id=0;
	private int ciclos;
	private int valorOperacionCritica=1;
	private Random r=new Random();
 
	
	//METODOS DE OBJETO
 /**
  * 
  * @param id Identificador del hilo
  * @param ciclos Ciclos a realizar por el hilo
  * @param valorOperacionCritica Valor a sumar a la variable compartida
  */
	public HebraPeterson(int id, int ciclos,int valorOperacionCritica){
		this.id=id;
		this.ciclos=ciclos;
		this.valorOperacionCritica=valorOperacionCritica;
		}
	
	
	@Override
	public void run() {
		while (ciclos>0) {
			//levantar bandera propia
			bandera[this.id]=true;
			//ceder el turno a la otra hebra
			turno=(this.id==0)?1:0;
			//esperar mientras la otra hegra tenga bandera levantada y no sea el turno propio
			while(bandera[(this.id==0)?1:0] && turno!=this.id) {
					Thread.yield();
				}
			
			
 
			//inicio seccion critica>>
			int t = MainPeterson.contadorCritico;
			
			//complejidad de seccion critica forzada para testear ruptura de coherencia
			try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
			
			//incrementar variable compartida
			MainPeterson.contadorCritico=t+this.valorOperacionCritica;
			//<< fin seccion critica
 
			//bajar bandera propia
			bandera[this.id]=false;
			
			
			ciclos--;
			//simular tiempos diferentes en seccion no critica
			try {Thread.sleep(r.nextInt(5));} catch (InterruptedException e) {}
			//imprimir progreso cada 5 ciclos
			if (ciclos%5==0)
			System.out.println("Hebra Peterson nº "+this.id+" Ciclos restantes:"+ciclos);
		}
	}
}
 