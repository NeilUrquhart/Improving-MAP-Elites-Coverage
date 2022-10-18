package edu.napier.ULSG.MAP_Elites.emitters;

import edu.napier.ULSG.MAP_Elites.Elite;
import edu.napier.ULSG.problem.Individual;
import edu.napier.ULSG.problem.MVehicleProblem;
import edu.napier.ULSG.problem.RandomSingleton;
import edu.napier.ULSG.problem.Individual.Gene;
import edu.napier.ULSG.vehicles.Vehicle;
import edu.napier.ULSG.vehicles.VehicleType;

public class EmCrossOver extends Emitter {

	public EmCrossOver(MVehicleProblem prob) {
		super(prob);
	}

	@Override
	public Elite emitt(Elite input) {
		return null;
	}

	public Elite emitt(Elite e1, Elite e2) {
		Individual p1=(Individual)e1;
		Individual p2=(Individual)e2;

		try {
			p1=(Individual)p1.clone();
			p2=(Individual)p2.clone();
			Individual i = new Individual((MVehicleProblem)p1.getProblem(),p1,p2);
			i.setEmitterTypeUsed(this);
			return i;
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public String name() {

		return "CrossOver";
	}

}
