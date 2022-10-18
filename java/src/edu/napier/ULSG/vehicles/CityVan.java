package edu.napier.ULSG.vehicles;

public class CityVan extends Vehicle{
	public CityVan() {
		capacity=50;//Load Capacity
		co2=4;//CO2 g per unit
		speed=3; //1 unit of time per unit of distance
		rcost=15; //6 units of currency per unit of distance
		fCost=80;//fixed cost
		type = VehicleType.INTERNAL_COMBUSTION;
		desc ="Van-30";
	}
}
