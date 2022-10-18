package edu.napier.ULSG.MAP_Elites;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import co.jxnl.bandits.AbstractBanditAlgorithm;
import co.jxnl.bandits.arms.BanditArm;
import co.jxnl.bandits.impl.DecayingGreedyEpsilonBanditImpl;
import co.jxnl.bandits.impl.GreedyEpsilonBanditImpl;
import edu.napier.ULSG.EvalsCounter;
import edu.napier.ULSG.Experiment;
import edu.napier.ULSG.Logger;
import edu.napier.ULSG.MAP_Elites.Archive.ArchiveResult;
import edu.napier.ULSG.MAP_Elites.emitters.EmAddRoute;
import edu.napier.ULSG.MAP_Elites.emitters.EmCrossOver;
import edu.napier.ULSG.MAP_Elites.emitters.EmDelRoute;
import edu.napier.ULSG.MAP_Elites.emitters.EmNNRoute;
import edu.napier.ULSG.MAP_Elites.emitters.EmMove;
import edu.napier.ULSG.MAP_Elites.emitters.EmSwapDelFrom;
import edu.napier.ULSG.MAP_Elites.emitters.EmSwapDelTo;
import edu.napier.ULSG.MAP_Elites.emitters.EmSwapNN;
import edu.napier.ULSG.MAP_Elites.emitters.Emitter;
import edu.napier.ULSG.MAP_Elites.probdefs.ProblemDefinition;
import edu.napier.ULSG.bandits.EmitArm;
import edu.napier.ULSG.problem.Individual;
import edu.napier.ULSG.problem.Individual.EvalType;
import edu.napier.ULSG.problem.MVehicleProblem;
import edu.napier.ULSG.problem.RandomSingleton;
import edu.napier.ULSG.problem.VRPSolver;
import edu.napier.ULSG.vehicles.Vehicle;
import main.java.co.jxnl.bandits.impl.UpperConfidenceBoundBanditImpl;

/*
 * Neil Urquhart 2019
 * A simple Evolutionary Algorithm to solve the CVRP problem
 * 
 * Use Individual.check() to ensure that Individuals contain valid solutions. Advisable to turn this on when testing
 * modificatins, and comment it out when testing is completed
 */
public class MAPElites extends VRPSolver {
	private Archive archive;
	private int b,d;
	private ProblemDefinition keys;
	private Logger log = Logger.getInstance();
	private Experiment exp = Experiment.getInstance();
	private RandomSingleton rnd = RandomSingleton.getInstance();
	private AbstractBanditAlgorithm strat;

	private ArrayList<Emitter> emitters = new ArrayList<Emitter>();

	public MAPElites(ProblemDefinition kg,MVehicleProblem aProblem) {
		d=kg.getDimensions();
		b=kg.getBuckets();
		keys = kg;
		archive = new Archive(kg.getDimensions(),kg.getBuckets());
		super.setProblem(aProblem);

		emitters.add(new EmMove(aProblem));
		if (exp.useExtraEmitters()) {
			emitters.add(new EmSwapNN(aProblem));
			emitters.add(new EmNNRoute(aProblem));
		}

		for (Vehicle v : aProblem.vehicles) {
			emitters.add(new EmDelRoute(aProblem, v));
			emitters.add(new EmAddRoute(aProblem, v));
			if (exp.useExtraEmitters()) {
				emitters.add(new EmSwapDelFrom(aProblem, v));
				emitters.add(new EmSwapDelTo(aProblem, v));
			}
		}
		
		if (exp.getUseBandits())
			emitters.add(new EmCrossOver(aProblem));

		Logger.getInstance().setupEmitterRecording(emitters, d,archive);
		//Bandit stuff
		List<BanditArm> baseBanditArms = new ArrayList<BanditArm>();

		for (Emitter em : emitters) {
			baseBanditArms.add(new EmitArm(archive,em, EvalType.simple));
			baseBanditArms.add(new EmitArm(archive,em, EvalType.advanced));
		}


		if (exp.useBanditGreedyE()) {
			strat = new GreedyEpsilonBanditImpl("", baseBanditArms, 0.5);
			strat.init();}
		
		if (exp.useBanditDecayGreedyE()) {
			strat = new DecayingGreedyEpsilonBanditImpl("", baseBanditArms,0.5,0.999);
			strat.init();
		}

		if (exp.useBanditUCB()) {
			strat = new UpperConfidenceBoundBanditImpl("", baseBanditArms);
			strat.init();}

		// Initialize and sample;



	}
	@Override
	public void solve()  {
		System.out.println("Running MAP-Elites");

		System.out.println(archive.size());

		EvalsCounter.setZero();
		while ( EvalsCounter.evals() < exp.getMapEliteEvals()) {

			if (exp.getUseEmitters()) 
				emitters();
			else if (exp.getUseBandits())
				bandits();
			else
				tradMapElites();

		}

		for(Elite el: archive.toList()) {
			for(int c :el.getKey()) {
				System.out.print(c+":");
			}
			System.out.println();
		}
		System.out.println("Size "+archive.size());
		log.flush();//Force write to file


	}

	public int getQtyEmitters() {
		return emitters.size();
	}
	private  void emitters() {

		if ((EvalsCounter.evals() % 50000)==0)
			System.out.println(EvalsCounter.evals()  + " " + archive.size());

		//for (Emitter em : emitters) {
		for (int c=0; c < emitters.size(); c++) {
			Emitter em = emitters.get(c);
			Elite adult = archive.getRandom();
			Elite child = em.emitt(adult);
			if (child != null) {
				if (!exp.getMultiDecoder()) {
					((Individual)child).evaluate();
					ArchiveResult res = archive.put(child);
					if (res != ArchiveResult.rejected) 
						log.log(((Individual)child).getEvalTypeUsed(),EvalsCounter.evals() ,em, adult.getKey(),adult.getFitness(),child.getKey(), res, child.getFitness());
					else
						log.log_failedEM(EvalsCounter.evals() );
					EvalsCounter.increment();
				}else {//Using multi decoder
					//Try simple
					((Individual)child).evaluate(EvalType.simple);

					ArchiveResult res = archive.put(child);
					if (res != ArchiveResult.rejected) 
						log.log(EvalType.simple,EvalsCounter.evals() ,em, adult.getKey(),adult.getFitness(),child.getKey(), res, child.getFitness());
					else
						log.log_failedEM(EvalsCounter.evals());
					EvalsCounter.increment();
					//Now try complex

					Individual child2 = (Individual)child;
					try {
						child2 = (Individual)child2.clone();
					} catch (CloneNotSupportedException e) {

						e.printStackTrace();
					}

					child2.evaluate(EvalType.advanced);

					res = archive.put(child2);
					if (res != ArchiveResult.rejected) 
						log.log(EvalType.advanced,EvalsCounter.evals() ,em, adult.getKey(),adult.getFitness(),child.getKey(), res, child.getFitness());
					else
						log.log_failedEM(EvalsCounter.evals() );
					EvalsCounter.increment();;
				}
			}
		}

		//Xover is a special case

		Individual p1  = (Individual)archive.getRandom();
		Individual p2  = (Individual)archive.getRandom();

		try {
			p1=(Individual)p1.clone();
			p2=(Individual)p2.clone();

			Individual child = new Individual((MVehicleProblem)super.theProblem,p1,p2);
			if (!exp.getMultiDecoder()) {
				child.evaluate();
				ArchiveResult res = archive.put(child);
				if (res != ArchiveResult.rejected)
					log.log(child.getEvalTypeUsed(),EvalsCounter.evals() , p1.getKey(),p1.getFitness(), p2.getKey(), p2.getFitness(),child.getKey(), res, child.getFitness());
				else
					log.log_failedXO(EvalsCounter.evals() );
				EvalsCounter.increment();;
			}else {//Used multi decoder
				//Try simple
				((Individual)child).evaluate(EvalType.simple);

				ArchiveResult res = archive.put(child);
				if (res != ArchiveResult.rejected) 
					log.log(EvalType.simple,EvalsCounter.evals() , p1.getKey(),p1.getFitness(), p2.getKey(), p2.getFitness(),child.getKey(), res, child.getFitness());
				else
					log.log_failedEM(EvalsCounter.evals() );
				EvalsCounter.increment();;
				//Now try complex

				Individual child2 = (Individual)child;
				try {
					child2 = (Individual)child2.clone();
				} catch (CloneNotSupportedException e) {

					e.printStackTrace();
				}

				child2.evaluate(EvalType.advanced);

				res = archive.put(child2);
				if (res != ArchiveResult.rejected) 
					log.log(EvalType.advanced,EvalsCounter.evals() ,p1.getKey(),p1.getFitness(), p2.getKey(), p2.getFitness(),child2.getKey(), res, child2.getFitness());
				else
					log.log_failedEM(EvalsCounter.evals() );
				EvalsCounter.increment();;

			}


		} catch (CloneNotSupportedException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}


	private  void bandits() {
		if ((EvalsCounter.evals()  % 5000)==0)
			System.out.println(EvalsCounter.evals()  + " " + archive.size());

		strat.sample();
		EvalsCounter.increment();
	}

	private void tradMapElites() {
		Individual i = null;
		Individual p1 = null;
		Individual p2 = null;
		boolean mutate = false;
		//Origin o = Origin.clone;

		if (rnd.getRnd().nextBoolean()) {
			Elite e = archive.getRandom();
			p1 = (Individual) e;
			p2 = null;
			try {
				i = (Individual)p1.clone();
			} catch (CloneNotSupportedException ex) {

				ex.printStackTrace();
			}
			i.mutate();
			mutate = true;
			//	o = Origin.clone;

		}
		else {
			p1  = (Individual)archive.getRandom();
			p2  = (Individual)archive.getRandom();

			try {
				p1=(Individual)p1.clone();
				p2=(Individual)p2.clone();

				i = new Individual((MVehicleProblem)super.theProblem,p1,p2);
				mutate = false;
				if(rnd.getRnd().nextBoolean()) {
					i.mutate();
					mutate = true;
				}

				//		o = Origin.crossover;
			} catch (CloneNotSupportedException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}

		}

		i.evaluate();
		EvalsCounter.increment();
		ArchiveResult res = archive.put((Elite)i);

		//		if(res != ArchiveResult.rejected) {
		//			System.out.println("Added:"+archive.size());
		//			if (o == Origin.clone)
		//				log.log(evals,exp.getId(), exp.getRun(), o, p1.getKey(), null, mutate, i.getKey(), res, i.getFitness());
		//			else
		//				log.log(evals,exp.getId(), exp.getRun(), o, p1.getKey(), p2.getKey(), mutate, i.getKey(), res, i.getFitness());
		//
		//		}
	}

	public void addPool(ArrayList<Elite> pool) {
		Logger log = Logger.getInstance();
		Experiment ex = Experiment.getInstance();
		for (Elite e : pool) {
			archive.put(e);
			EvalType et = ((Individual)e).getEvalTypeUsed();
			log.log(et,0,e.getKey(), e.getFitness());
		}

	}
	public Archive getArchive() {
		// TODO Auto-generated method stub
		return archive;
	}
}
