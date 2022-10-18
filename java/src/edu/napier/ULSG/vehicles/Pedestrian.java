package edu.napier.ULSG.vehicles;

public  class Pedestrian extends Vehicle{
	public Pedestrian() {
		capacity=5;//Load Capacity
		co2=0;//CO2 g per unit
		speed=1; //1 unit of time per unit of distance
		rcost=2; //4 units of currency per unit of distance
		fCost=50;//fixed cost
		type = VehicleType.PEDESTRIAN;
		desc ="Walker";
	}
}
