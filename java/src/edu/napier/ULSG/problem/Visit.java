package edu.napier.ULSG.problem;


import java.awt.geom.Point2D;

/*
 * Neil Urquhart 2019
 * A simple class representing a visit that extends the Java Point class
 * 
 */
public class Visit extends LatLon{
 	protected String theName;
 	
 	public Visit(String name, double lat, double lon){
 		super(lat,lon);
 		theName = name;
 	}
 	
 	public String toString(){
 	  return theName;	
 	}
 	
 	public String getName() {
 		return theName;
 	}
 	
 	public double distance(Visit v) {
 		//Return the distance between 2 points based upon the Haversine
 		//as implemented within LatLon
 		return super.getDist(v);
 	}

	
 }