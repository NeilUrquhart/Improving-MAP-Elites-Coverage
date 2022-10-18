package edu.napier.ULSG.problem;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
/*
 * This class represents a TSP problem.
 * Neil Urquhart, 2017 
 * 
 */

public class TSPProblem implements Serializable{
	/*
	 * Constructors
	 */
	public TSPProblem(){}

	/*
	 * Properties
	 */

	protected ArrayList<Visit> currentSolution = new ArrayList<Visit>();
	//The list of visits that comprise the TSP - once solve() has been called the vists are
	//in solution order.
	public void setRoute(ArrayList<Visit> route){
		this.currentSolution = route;
	}
	public void addVisit(Visit aCity){
		currentSolution.add(aCity);
	}
	public ArrayList<Visit> getSolution(){
		return currentSolution;
	}

	private Visit start;//The starting and finishing point of the TSP
	public Visit getStart() {
		return start;
	}
	public void setStart(Visit aStart) {
		this.start = aStart;
	}

	/*
	 * Actions
	 */
	public void solve(TSPSolver mySolver){
		//Solve the problem using the supplied solver
		mySolver.setProblem(this);
		mySolver.solve(); 
	}

	public void shuffle(){
		//Shuffle the list of visits into a random order
		Collections.shuffle(currentSolution);
	}

	public int getSize(){
		//Return the number of cities within the TSP (not including the start)
		return currentSolution.size();
	}

	/*
	 * Distance calculations
	 * This TSP problem defines the distance between two visists as the Euclidean (straight line) distance
	 */    	
	public double getDistance(){
		//Get the distance of current solution
		return getDistance(currentSolution);
	}

	public double getDistance(ArrayList<Visit> possibleRoute){
		//Get the distance of the visits within possibleRoute - include the start point as well to complete the circuit
		double dist =0;
		Visit previousCity = start;
		for (Visit city : possibleRoute){//go through each city
			dist = dist + getDistance(previousCity, city);
			previousCity = city;
		}
		dist = dist + getDistance(previousCity, start);
		return dist;
	}
	public double getDistance(Visit x, Visit y){
		//Get the distance between two visits
		if ((x == null)||(y==null))
			return 0;
		else			
			return x.distance(y);
	}

	/*
	 *Return the defualt string repesentation of the class. 
	 */

	public String toString(){
		String buffer = start +" ";
		for (Visit c : currentSolution){
			buffer +=  c +"->";
		}
		buffer += start + "\n";
		buffer += "Distance = " + getDistance();
		return buffer;
	} 
}