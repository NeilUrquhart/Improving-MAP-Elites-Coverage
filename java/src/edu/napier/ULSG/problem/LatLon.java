package edu.napier.ULSG.problem;


import java.io.Serializable;

public class LatLon implements Serializable{
	private double lat;
	private double lon;
	
	public LatLon(double lt, double ln){

		lat = lt;
		lon = ln;
	}
	
	public double getLat() {
		return lat;
	}
	
	public double getLon() {
		return lon;
	}
	
	public String toString(){
		return lat + "\t" + lon;
	}
	public double getDist(LatLon other){
		return haversine(this.lat,this.lon,other.lat,other.lon);
	}
	
	public boolean same(LatLon other) {
		return this.lat == other.lat && other.lon == this.lon;
	}
	
	private static double haversine(double lat1, double lon1, double lat2, double lon2) {
		/* from https://rosettacode.org/wiki/Haversine_formula */
		final double R = 6372.8; // In kilometers
	    
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
 
        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }
}
