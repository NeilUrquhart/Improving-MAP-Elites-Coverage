package edu.napier.ULSG.vehicles;

public class LargeVan extends Vehicle{
	public LargeVan() {
		capacity=100;//Load Capacity
		co2=5;//CO2 g per unit
		speed=3; //1 unit of time per unit of distance
		rcost=15; //6 units of currency per unit of distance
		fCost=120;//fixed cost
		type = VehicleType.INTERNAL_COMBUSTION;
		desc ="Van";
	}
}
