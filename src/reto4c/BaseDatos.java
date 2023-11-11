package reto4c;

import java.io.EOFException;
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

	public void update(int id) {
		cola.esperar();
		escritores.esperar();
		cola.senalar();
		//tupla++;
		escribirADisco(id);
		// try {Thread.currentThread().sleep(1);} catch (InterruptedException e) {}
		escritores.senalar();
	}

	private void escribirADisco(int id) {
		RandomAccessFile raf =null;
		try {
			 raf = new RandomAccessFile(f, "rw");
			 raf.seek(id*4);
			 int actual=raf.readInt();
			 raf.seek(id*4);
			 raf.writeInt(actual+1);

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
		
	}

	public int select(int id) {
		int leido = 0;
		cola.esperar();
		lectores.esperar();
		numeroLectores++;
		// try {Thread.currentThread().sleep(1);} catch (InterruptedException e) {}
		if (numeroLectores == 1)
			escritores.esperar();
		cola.senalar();
		lectores.senalar();

		leido = leerDeDisco(id);
		
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

	private int leerDeDisco(int id) {
		int salida=0;
		RandomAccessFile raf =null;
		try {
			 raf = new RandomAccessFile(f, "r");
			 raf.seek(id*4);
			 salida=raf.readInt();

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
		return salida;
	}

	public int getTupla() {
		int salida=0;
		RandomAccessFile raf =null;
		try {
			 raf = new RandomAccessFile(f, "r");
			 raf.seek(0);
			 while(true)
			 salida+=raf.readInt();
		

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(EOFException e) {	
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
		return salida;
	}

}
