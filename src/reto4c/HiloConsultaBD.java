package reto4c;

import java.util.Random;

public class HiloConsultaBD extends Thread {

	int indice;
	BaseDatos bd;
	int ciclos;
	
	public HiloConsultaBD(int indice, BaseDatos bd, int ciclos) {
		super();
		this.indice = indice;
		this.bd=bd;
		this.ciclos=ciclos;
	}

	public int getIndice() {
		return indice;
	}

	
}
