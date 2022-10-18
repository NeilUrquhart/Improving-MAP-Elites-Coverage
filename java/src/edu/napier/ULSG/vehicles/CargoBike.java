package edu.napier.ULSG.vehicles;

public class CargoBike extends Vehicle{

	public CargoBike() {
		capacity=10;//Load Capacity
		co2=0;//CO2 g per unit
		speed=2; //1 unit of time per unit of distance
		rcost=2; //4 units of currency per unit of distance
		fCost=50;//fixed cost
		type = VehicleType.CYCLE;
		desc ="City Cargo Bike";
	}

}
