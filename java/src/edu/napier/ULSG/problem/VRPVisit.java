package edu.napier.ULSG.problem;

/*
 * VRPVisit - extends Visit adds demand.
 * 
 */


public class VRPVisit extends Visit {

	protected int demand;
	
	public VRPVisit(String name, double lat, double lon, int demand) {
		super(name, lat, lon);
		this.demand = demand;
	}
	
	public int getDemand(){
		return demand;
	}
	
	public String toString(){
		return super.toString() + " " +demand+" (" + super.getLat() +":" + super.getLon() +")";
	}
	
	

	@Override
	  public boolean equals(Object o) {
	     if (!(o instanceof VRPVisit)){ 
	       return false;
	     }
	     VRPVisit other = (VRPVisit) o;
	     return super.theName.equals(other.theName);
	  }
}
