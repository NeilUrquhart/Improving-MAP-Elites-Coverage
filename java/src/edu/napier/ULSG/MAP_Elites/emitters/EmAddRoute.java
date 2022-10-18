package edu.napier.ULSG.MAP_Elites.emitters;

import edu.napier.ULSG.MAP_Elites.Elite;
import edu.napier.ULSG.problem.Individual;
import edu.napier.ULSG.problem.MVehicleProblem;
import edu.napier.ULSG.problem.RandomSingleton;
import edu.napier.ULSG.problem.Individual.Gene;
import edu.napier.ULSG.vehicles.Vehicle;
import edu.napier.ULSG.vehicles.VehicleType;

public class EmAddRoute extends Emitter {
	
	private Vehicle vehicle;
	private String vName;

	public EmAddRoute(MVehicleProblem prob, Vehicle v) {
		super(prob);
		vehicle=v;
		vName = v.getClass().getSimpleName();
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
		Gene g = i.getGenotype().get(rndGene);
		g.setPrefered(vehicle);
		g.setNewRoute(true);
		i.setEmitterTypeUsed(this);
		return i;
	}

	@Override
	public String name() {
		
		return "AddRoute:"+vName;
	}

}
