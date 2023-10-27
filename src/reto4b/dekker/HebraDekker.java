package reto4b.dekker;

import java.util.Random;

public class HebraDekker extends Thread {

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
 
	public HebraDekker(int id, int ciclos,int valorOperacionCritica){
		this.id=id;
		this.ciclos=ciclos;
		this.valorOperacionCritica=valorOperacionCritica;
		}
	
	
	@Override
	public void run() {
		while (ciclos>0) {
			
			bandera[this.id]=true;
			if (turno!=this.id) {
				bandera[this.id]=false;
				while(turno!=this.id) {
					Thread.yield();
				}
				bandera[this.id]=true;
			}
			
 
			//inicio seccion critica>>
			//complejidad de seccion critica forzada para testear ruptura de coherencia
			int t = MainDekker.contadorCritico;
			try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
			MainDekker.contadorCritico=t+this.valorOperacionCritica;
			//<< fin seccion critica
 
			turno=(this.id==0)?1:0;
			bandera[this.id]=false;
			
			
			ciclos--;
			//simular tiempos diferentes en seccion no critica
			try {
				Thread.sleep(r.nextInt(2));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//imprimir progreso
			if (ciclos%1==0)
			System.out.println("Hebra Dekker "+this.id+" ciclos restantes:"+ciclos);
		}
	}
}
 