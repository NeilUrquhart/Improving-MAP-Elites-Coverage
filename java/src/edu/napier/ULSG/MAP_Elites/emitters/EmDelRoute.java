package edu.napier.ULSG.MAP_Elites.emitters;

import edu.napier.ULSG.MAP_Elites.Elite;
import edu.napier.ULSG.problem.Individual;
import edu.napier.ULSG.problem.MVehicleProblem;
import edu.napier.ULSG.problem.RandomSingleton;
import edu.napier.ULSG.problem.Individual.Gene;
import edu.napier.ULSG.vehicles.Vehicle;
import edu.napier.ULSG.vehicles.VehicleType;

public class EmDelRoute extends Emitter {

	private Vehicle vehicle;
	private String vehName;
	
	public EmDelRoute(MVehicleProblem prob, Vehicle v) {
		super(prob);
		vehicle =v;
		vehName = v.getClass().getSimpleName();
	}

	@Override
	public Elite emitt(Elite input) {
		Individual i = (Individual)input;
		try {
			i = (Individual) i.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		for (int tries = 0; tries <10; tries++) {
			int rndGene = rnd.nextInt(i.getGenotype().size());
			Gene g = i.getGenotype().get(rndGene);
			if ((g.getPrefered() == vehicle)&&(g.getNewRoute()==true)) {
				g.setNewRoute(false);
				tries =10;
			}
		}
		i.setEmitterTypeUsed(this);
		return i;
	}

	@Override
	public String name() {
		
		return "DelRoute:"+vehName;
	}

}
