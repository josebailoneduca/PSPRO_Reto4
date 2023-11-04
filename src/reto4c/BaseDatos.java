package reto4c;

public class BaseDatos {
	int numeroLectores=0;
	
	Semaforo lectores;
	Semaforo escritores;
	int tupla=0;
	
	
	
	
	public BaseDatos(int nHebras) {
		lectores = new Semaforo(1, nHebras);
		escritores= new Semaforo(1, nHebras);
	}


	public void update() {
		escritores.esperar();
		tupla++;
		try {Thread.currentThread().sleep(1);} catch (InterruptedException e) {}
		escritores.senalar();
	}
	
	
	public int select() {
		int leido=0;
		lectores.esperar();
		numeroLectores++;
		try {Thread.currentThread().sleep(1);} catch (InterruptedException e) {}
		if (numeroLectores==1)
			escritores.esperar();
		lectores.senalar();
		
		leido=tupla;
		lectores.esperar();
		numeroLectores--;
		try {Thread.currentThread().sleep(1);} catch (InterruptedException e) {}
		if (numeroLectores==0)
			escritores.senalar();
		lectores.senalar();
		return leido;
	}


	public int getTupla() {
		return tupla;
	}
	
	
	
}

