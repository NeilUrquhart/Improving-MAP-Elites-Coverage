package edu.napier.ULSG.problem;
import edu.napier.ULSG.vehicles.Vehicle;

public class MVehicleProblem extends CVRPProblem  {

	public Vehicle[] vehicles;
	
	public Vehicle rndVehicle() {
		RandomSingleton rnd  = RandomSingleton.getInstance();
		
		int i = rnd.getInstance().getRnd().nextInt(vehicles.length);
		return vehicles[i];
	}
}
