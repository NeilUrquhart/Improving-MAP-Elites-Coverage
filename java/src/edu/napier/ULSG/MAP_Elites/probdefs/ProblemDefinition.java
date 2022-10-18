package edu.napier.ULSG.MAP_Elites.probdefs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import edu.napier.ULSG.MAP_Elites.Archive;
import edu.napier.ULSG.MAP_Elites.Elite;
import edu.napier.ULSG.problem.Individual;
import edu.napier.ULSG.problem.Individual.Characteristic;

/*
 * Neil Urquhart 2021
 * An interface that defines the basic operations needed from 
 * a key generator when used with MAPElites
 * 
 */
public abstract class ProblemDefinition {
	public abstract int getBuckets();
	public abstract void displayRanges(String fName); 
	public abstract void updateRanges(ArrayList<Elite> pool) ;
	public abstract int[] getKey(HashMap<Characteristic,Double> chars);
	public abstract void updateRanges(Individual i) ;
	public abstract	Characteristic[] getChracteristics();
	public abstract void writeData(Archive ar,String id) ;
	

	protected int buckets = 5;
	protected  int dimensions = 4;
	
	public  int getDimensions() { 
		return dimensions; 
	}
	
	protected int getBucket(double actual, double min, double max) {
		/*
		 * Calculate the bucket for a value <actual> in a dimension of the range <min> to <max>
		 */
		double delta = max - min;
		double period = delta/buckets;
		int bucket = (int)((actual-min) / period);
		//What if actual is higher than max, in that case bucket should be retained at the highest value
		if (bucket >= buckets) {
			bucket = buckets-1;
		}
		//What if actual is lower than min, in that case bucket should be retained at the lowest value
		if (bucket < 0) {
			bucket = 0;
		}
		return bucket;
	}

}