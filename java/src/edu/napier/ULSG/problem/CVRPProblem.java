package edu.napier.ULSG.problem;

import java.util.ArrayList;



/*
 * Neil Urquhart 2019
 * This class represents a CVRP problem.
 * It extends the basic TSPProblem class.
 * 
 */
public class CVRPProblem extends TSPProblem {
	
	protected Individual currentVRPSolution ;
	//Represents the colection  of routes that comprise the solution
	
	
	
	public void setSolution(Individual aVRPSolution){
		currentVRPSolution = aVRPSolution;
	}
	
	public Individual getCVRPSolution(){
		return this.currentVRPSolution;
	}
	
	
	
	public void solve(VRPSolver mySolver){
		//Solve the problem using the supplied solver
		mySolver.setProblem(this);
		mySolver.solve(); 
	}

	public double getDemand() {
		double res=0;
		for (Object viz : this.getVisits()) {
			VRPVisit v = (VRPVisit)viz;
			res = res + v.demand;
		}
		return res;
	}
	
	public ArrayList getVisits(){
		return super.getSolution();
	}
	
	
}
