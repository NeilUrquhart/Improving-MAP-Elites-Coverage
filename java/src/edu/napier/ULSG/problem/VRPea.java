package edu.napier.ULSG.problem;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import edu.napier.ULSG.Experiment;
import edu.napier.ULSG.problem.Individual.EvalType;



/*
 * Neil Urquhart 2019
 * A simple Evolutionary Algorithm to solve the CVRP problem
 * 
 * Use Individual.check() to ensure that Individuals contain valid solutions. Advisable to turn this on when testing
 * modifications, and comment it out when testing is completed
 */
public class VRPea extends VRPSolver {
	protected ArrayList <Individual> population = new ArrayList<Individual>();
	//population stores our pool of potential solutions
	protected RandomSingleton rnd = RandomSingleton.getInstance();
	//Note that we use the RandomSingleton object to generate random numbers

	//EA Parameters
	protected int POP_SIZE = 100;
	protected int TOUR_SIZE = 2;
	protected double XO_RATE = 0.7;
	protected int evalsBudget = Experiment.getInstance().getInitEvals();

	@Override
	public void solve() {
		
		//Reference to the best individual in the population
		Individual bestSoFar = InitialisePopution();
		System.out.println("\nNEW "+bestSoFar.evaluate());
	//	double prev = bestSoFar.evaluate();
		while(evalsBudget >0) {	
			//Create child
			Individual child = null;
			if (rnd.getRnd().nextDouble() < XO_RATE){
				//Create a new Individual using recombination, randomly selecting the parents
				try {
					child = new Individual((MVehicleProblem)super.theProblem, tournamentSelection(TOUR_SIZE),tournamentSelection(TOUR_SIZE));
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
					System.exit(-1);
				}				
			}
			else{
				//Create a child by copying a single parent
				try {
					child = (Individual) tournamentSelection(TOUR_SIZE).clone();
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.exit(-1);
				}

			}
			child.mutate();
			child.evaluate();
			child.check();
			evalsBudget --;

			//Select an Individual with a poor fitness to be replaced
			Individual poor = tournamentSelectWorst(TOUR_SIZE);
			if (poor.evaluate() > child.evaluate()){
				//Only replace if the child is an improvement

				if (child.evaluate() < bestSoFar.evaluate()){
					bestSoFar = child;
					System.out.println("NEW " + bestSoFar.evaluate());

				}
				//child.check();//Check child contains a valid solution
				population.remove(poor);
				population.add(child);
			}
			bestSoFar.phenotype = null;
//			if (bestSoFar.evaluate()>prev)
//			   System.out.println(bestSoFar.evaluate() +"***************" );
//			prev = bestSoFar.evaluate();
			if (evalsBudget%10000 ==0)
				System.out.println(evalsBudget);
		}
		bestSoFar.phenotype = null;
		bestSoFar.evaluate();

		super.theProblem.setSolution(bestSoFar);
	}

	protected Individual InitialisePopution() {
		//Initialise population with random solutions
		Individual best = null;

		for (int count=0; count < POP_SIZE; count++){
			
			Individual i = new Individual((MVehicleProblem)super.theProblem,((MVehicleProblem)super.theProblem).rndVehicle());

			//i.check();//Check individual contains a valid solution
			if (best == null) 
				best = i;
			if (i.evaluate()< best.evaluate()) 
				best = i;
			population.add(i);
			evalsBudget--;
		}
		return best;
	}

	protected Individual tournamentSelection(int poolSize){
		//Return the best individual from a randomly selected pool of individuals
		Individual bestI = null;
		double bestFit = Double.MAX_VALUE;
		for (int tries=0; tries < poolSize; tries++){
			Individual i = population.get(rnd.getRnd().nextInt(population.size()));
			if (i.getDistance() < bestFit){
				bestFit = i.getDistance();
				bestI = i;
			}
		}
		return bestI;
	}

	protected Individual tournamentSelectWorst(int poolSize){
		//Return the worst individual from a ransomly selected pool of individuals
		Individual bestI = null;
		double bestFit = 0;
		for (int tries=0; tries < poolSize; tries++){
			Individual i = population.get(rnd.getRnd().nextInt(population.size()));
			if (i.getDistance() > bestFit){
				bestFit = i.getDistance();
				bestI = i;
			}
		}
		return bestI;
	}
	
	public ArrayList<Individual> getPopulation(){
		return this.population;
	}
}
