package edu.napier.ULSG.MAP_Elites.emitters;

import java.util.Random;

import edu.napier.ULSG.MAP_Elites.Elite;
import edu.napier.ULSG.problem.MVehicleProblem;
import edu.napier.ULSG.problem.RandomSingleton;

public abstract class Emitter {
	
	protected MVehicleProblem problem;
	protected Random rnd = RandomSingleton.getInstance().getRnd();
	
	public Emitter(MVehicleProblem prob) {
		problem = prob;
	}
	public abstract Elite emitt(Elite input);
		
	
	
	public abstract String name();
}
