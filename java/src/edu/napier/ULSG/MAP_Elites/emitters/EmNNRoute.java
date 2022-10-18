package edu.napier.ULSG.MAP_Elites.emitters;

import java.util.ArrayList;

import edu.napier.ULSG.MAP_Elites.Elite;
import edu.napier.ULSG.problem.Individual;
import edu.napier.ULSG.problem.MVehicleProblem;
import edu.napier.ULSG.problem.RandomSingleton;
import edu.napier.ULSG.problem.Visit;
import edu.napier.ULSG.problem.Individual.Gene;
import edu.napier.ULSG.vehicles.Vehicle;
import edu.napier.ULSG.vehicles.VehicleType;

public class EmNNRoute extends Emitter {

	public EmNNRoute(MVehicleProblem prob) {
		super(prob);
	}

	@Override
	public Elite emitt(Elite input) {
		/*
		 * Select a route and randomly reorder it
		 * 
		 */
		Individual i = (Individual)input;
		try {
			i = (Individual) i.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
			
		//Tries 20
		int tries =20;
		
		Gene v = i.getGenotype().get(rnd.nextInt(i.getGenotype().size()));
		while (!v.getNewRoute()) {
			v = i.getGenotype().get(rnd.nextInt(i.getGenotype().size()));
			tries--;
			if (tries==0) return null;
		}
		
		int insPoint = i.getGenotype().indexOf(v);
		
		if (insPoint  > (i.getGenotype().size()-3))
			return null;
		
		
		//Remove route
		Gene g = i.getGenotype().remove(insPoint);
		ArrayList<Gene> r = new ArrayList<Gene>();
		r.add(g);
		g = i.getGenotype().get(insPoint);
		
		while (!g.getNewRoute()) {
			r.add(g);
			i.getGenotype().remove(g);
			if (i.getGenotype().size() == (insPoint+1)) {//End of chromosome
				break;
			}
			g = i.getGenotype().get(insPoint);
			
		}
			
		//Sort R
		
		Visit current = problem.getStart();
		Gene best = null;
		double dist;
		while (r.size()>0) {
			dist = Double.MAX_VALUE;
			for (Gene gn : r) {
				if (gn.getVisit().getDist(current) < dist) {
					dist = gn.getVisit().getDist(current);
					best = gn;
				}
			}
			r.remove(best);
			i.getGenotype().add(insPoint, best);
			insPoint++;
		}
	
		i.setEmitterTypeUsed(this);
		return i;
	}

	@Override
	public String name() {
		
		return "NNRoute";
	}

}
