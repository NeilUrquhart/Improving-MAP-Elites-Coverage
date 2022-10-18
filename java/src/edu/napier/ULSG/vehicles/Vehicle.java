package edu.napier.ULSG.vehicles;

import java.io.Serializable;

public abstract class Vehicle implements Serializable{

	protected int capacity;//Load Capacity
	protected int co2;//CO2 g per unit
	protected int speed; //1 unit of time per unit of distance
	protected int rcost; //4 units of currency per unit of distance
	protected int fCost;//fixed cost
	protected VehicleType type ;
	protected String desc ="";
	
	public int getCapacity() {
		return capacity;
	}
	
	public int getCo2() {
		return co2;
	}

	public int getSpeed() {
		return speed;
	}

	public int getRcost() {
		return rcost;
	}

	public int getfCost() {
		return fCost;
	}
	
	public String toString() {
		return desc;
	}
	
	public String getDescription() {
		return desc;
	}
	
	public VehicleType getType() {
		return type;
	}
}
