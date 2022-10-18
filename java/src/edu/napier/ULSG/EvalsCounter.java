package edu.napier.ULSG;

public class EvalsCounter {
	private static int evals;
	
	public static void increment() {
		evals++;
	}
	
	public static int evals() {
		return evals;
		
	}
	
	public static void setZero() {
		evals=0;
	}
	
}
