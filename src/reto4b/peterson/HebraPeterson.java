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
 
	public HebraPeterson(int id, int ciclos,int valorOperacionCritica){
		this.id=id;
		this.ciclos=ciclos;
		this.valorOperacionCritica=valorOperacionCritica;
		}
	
	
	@Override
	public void run() {
		while (ciclos>0) {
			
			bandera[this.id]=true;
			turno=(this.id==0)?1:0;
			while(bandera[(this.id==0)?1:0] && turno!=this.id) {
					Thread.yield();
				}
			
			
 
			//inicio seccion critica>>
			//complejidad de seccion critica forzada para testear ruptura de coherencia
			int t = MainPeterson.contadorCritico;
			try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
			MainPeterson.contadorCritico=t+this.valorOperacionCritica;
			//<< fin seccion critica
 
			bandera[this.id]=false;
			
			
			ciclos--;
			//simular tiempos diferentes en seccion no critica
			try {
				Thread.sleep(r.nextInt(1));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//imprimir progreso
			if (ciclos%1==0)
			System.out.println("Hebra Peterson "+this.id+" ciclos restantes:"+ciclos);
		}
	}
}
 