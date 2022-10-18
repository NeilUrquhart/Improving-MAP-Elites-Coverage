package edu.napier.ULSG.MAP_Elites;

import edu.napier.ULSG.MAP_Elites.emitters.Emitter;
import edu.napier.ULSG.problem.Individual.EvalType;

/*
 * Neil Urquhart 2021
 * 
 * Elite is used to define the interface required for solutions that are to be stored in the MAPofElite
 * 
 * getFitness() must return a double value that represents the fitness of the solution (lower == better)
 * getKey() must return an array of int which represents the "key" of the individual (ie the bucket location
 * getSummary must return a string that summarises the individual (for the log file)
 */

public interface Elite {
	public double getFitness();
	public int[] getKey();
	public String getSummary();
	public Emitter getEm();
	public EvalType getDecode();
	public String getCSV();
	
	public default String keyToString() {
		/*
		 * Return the key as a string 
		 */
		String res = "";
		for (int i=0; i < getKey().length-1; i++)
			res = res + getKey()[i] +":";
			
		res = res + getKey()[getKey().length-1];
		return res;
	}
	

}
