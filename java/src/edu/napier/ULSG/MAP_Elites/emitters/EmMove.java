package edu.napier.ULSG.MAP_Elites.emitters;

import edu.napier.ULSG.MAP_Elites.Elite;
import edu.napier.ULSG.problem.Individual;
import edu.napier.ULSG.problem.MVehicleProblem;
import edu.napier.ULSG.problem.RandomSingleton;
import edu.napier.ULSG.problem.Individual.Gene;
import edu.napier.ULSG.vehicles.Vehicle;
import edu.napier.ULSG.vehicles.VehicleType;

public class EmMove extends Emitter {

	public EmMove(MVehicleProblem prob) {
		super(prob);
	}

	@Override
	public Elite emitt(Elite input) {
		Individual i = (Individual)input;
		try {
			i = (Individual) i.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
			
		int rndGene = rnd.nextInt(i.getGenotype().size());
		Gene v = i.getGenotype().remove(rndGene);
		int addPoint = rnd.nextInt(i.getGenotype().size());
		i.getGenotype().add(addPoint,v);
		
		i.setEmitterTypeUsed(this);
		return i;
	}

	@Override
	public String name() {
		
		return "RndSwap";
	}

}
