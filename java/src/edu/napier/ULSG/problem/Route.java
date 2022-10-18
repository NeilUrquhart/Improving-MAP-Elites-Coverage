package edu.napier.ULSG.problem;
import java.util.ArrayList;

import edu.napier.ULSG.vehicles.Vehicle;

public class Route extends ArrayList<VRPVisit> {
	private Vehicle vehicle;
	private Individual individual;
	
	public Route(Individual i) {
		this.individual = i;
	}
	
	public void setVehicle(Vehicle v) {
		this.vehicle = v;
	}
	
	public Vehicle getVehicle() {
		return vehicle;
	}

	public Visit getStart() {
		return individual.problem.getStart();
	}
	
	public double getCO2() {
		return this.vehicle.getCo2()*this.getDist();
	}
	
	public double getRCost() {
		return this.vehicle.getRcost() * this.getDist();
	}
	
	public double  timeToLast() {
		//t=d/v
		double time=0;
		Visit prev = this.getStart();
		for (Visit v : this) {
			double d = prev.distance(v);
			time = time + (d/this.vehicle.getSpeed());
			prev =v;
		}
		if (Double.isNaN(time))
			System.out.println("NaN!!!!");
		return time;
	}
	
	public float getFixedCost() {
		return this.vehicle.getfCost();
	}
	
	public double getAvgTime() {
		double ttl = timeToLast();
		double res =(ttl/this.size());
//		System.out.println("*" +ttl);
//		if (Double.isNaN(res))
//			System.out.println("NaN!!!!");
		return res;
	}
	
	public double getDist() {
		double res=0;
		Visit prev = individual.problem.getStart();
		for(VRPVisit v : this) {
			res = res + v.distance(v);
			prev = v;
		}
		res = res + prev.distance(individual.problem.getStart());
		return res;
	}
	
	public double getDemand() {
		//Return the total demand on this route
		double res=0;
		for(VRPVisit v : this) {
			res = res + v.demand;
		}
		return res;
	}
}
