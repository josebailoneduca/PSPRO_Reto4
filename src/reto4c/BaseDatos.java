package reto4c;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

public class BaseDatos {
	int numeroLectores = 0;

//	Semaforo lectores;
//	Semaforo escritores;
	Semaforo lectores;
	Semaforo escritores;
	SemaforoEstricto cola;
	int tupla = 0;
	File f;

	public BaseDatos(int nHebras) {
//		lectores = new Semaforo(1, nHebras);
//		escritores= new Semaforo(1, nHebras);
		lectores = new Semaforo(1, nHebras);
		escritores = new Semaforo(1, nHebras);
		cola = new SemaforoEstricto(1, nHebras);
		f = new File("bd.dat");
		if (f.exists())
			f.delete();
		try {
			f.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RandomAccessFile raf =null;
		try {
			 raf = new RandomAccessFile(f, "rw");
			for (int i=0;i<100;i++) {
				raf.writeInt(0);
			}
			raf.seek(0);
			for (int i=0;i<100;i++) {
				System.out.println(raf.readInt());
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				if (raf!=null)
				raf.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(f.getAbsolutePath());
	}

	public void update() {
		cola.esperar();
		escritores.esperar();
		cola.senalar();
		tupla++;
		// try {Thread.currentThread().sleep(1);} catch (InterruptedException e) {}
		escritores.senalar();
	}

	public int select() {
		int leido = 0;
		cola.esperar();
		lectores.esperar();
		numeroLectores++;
		// try {Thread.currentThread().sleep(1);} catch (InterruptedException e) {}
		if (numeroLectores == 1)
			escritores.esperar();
		cola.senalar();
		lectores.senalar();

		leido = tupla;
		lectores.esperar();
		numeroLectores--;
		try {
			Thread.currentThread().sleep(1);
		} catch (InterruptedException e) {
		}
		if (numeroLectores == 0)
			escritores.senalar();
		lectores.senalar();
		return leido;
	}

	public int getTupla() {
		return tupla;
	}

}
