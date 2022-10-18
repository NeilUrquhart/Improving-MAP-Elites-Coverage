package edu.napier.ULSG.bandits;


import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import co.jxnl.bandits.arms.BanditArm;
import co.jxnl.bandits.arms.Metric;
import edu.napier.ULSG.EvalsCounter;
import edu.napier.ULSG.Logger;
import edu.napier.ULSG.MAP_Elites.Archive;
import edu.napier.ULSG.MAP_Elites.Archive.ArchiveResult;
import edu.napier.ULSG.MAP_Elites.Elite;
import edu.napier.ULSG.MAP_Elites.emitters.EmCrossOver;
import edu.napier.ULSG.MAP_Elites.emitters.Emitter;
import edu.napier.ULSG.problem.Individual;
import edu.napier.ULSG.problem.Individual.EvalType;


/**
 * This implements a Bandit Arm with sampling characteristic of a Bernoulli
 * trial Return rate is randomly set within 0-20%.
 * 
 * @author jliu
 * 
 */
public class EmitArm extends BanditArm{// implements Cloneable {

	private double trueWinRate;
	private Emitter emitter;
	private EvalType evalType;
	private Archive  archive;
	private Logger logger;

	//	public EmitArm(String name, Map<String, Object> metaData) {
	//		super(name, metaData);
	//		this.trueWinRate = getRandom().nextDouble() * 0.2;
	//	}

	public EmitArm(Archive ar, Emitter em, EvalType et) {
		super(em.name()+":"+et);
		emitter = em;
		evalType = et;
		archive = ar;
		logger = Logger.getInstance();
		//this.trueWinRate = getRandom().nextDouble() * 0.2;
	}



	@Override
	public void sample() {
		incrementPullCounter();
		/*
		 * Emitt
		 * 
		 */
		boolean emitted = false;

		if (emitter.getClass().getSimpleName().contains("EmCrossOver")) {//Crossover
			EmCrossOver emc  = (EmCrossOver) emitter;
			Elite p1 = archive.getRandom();
			Elite p2 = archive.getRandom();
			if((p1==null)||(p2==null))
					return ;

			Elite child = emc.emitt(p1, p2);

			if (child != null) {
				((Individual)child).evaluate(this.evalType);
				ArchiveResult res = archive.put(child);
				if (res != ArchiveResult.rejected) {
					emitted=true;
					logger.log(this.evalType,EvalsCounter.evals() , p1.getKey(),p1.getFitness(),p2.getKey(),p2.getFitness(),child.getKey(), res, child.getFitness());
				}
				else {
					logger.log_failedEM(EvalsCounter.evals() );
					emitted=false;
				}
			}
		}
		else {//Not xover
			Elite adult = archive.getRandom();
			if (adult==null)
				return;
			Elite child = emitter.emitt(adult);
			if (child != null) {
				((Individual)child).evaluate(this.evalType);
				ArchiveResult res = archive.put(child);
				if (res != ArchiveResult.rejected) {
					emitted=true;
					logger.log(EvalType.simple,EvalsCounter.evals() ,this.emitter, adult.getKey(),adult.getFitness(),child.getKey(), res, child.getFitness());
				}
				else {
					logger.log_failedEM(EvalsCounter.evals() );
					emitted=false;
				}
			}

		}
		if (emitted) {
			incrementSuccessCounter();
		}
	}

	@Override
	public JSONObject toJson() {
		JSONObject report = new JSONObject();
		try {
			// report.put("name", this.getBanditName());
			report.put("success", this.getSuccessCounter());
			report.put("pulled", this.getPullCounter());
			report.put("mle", Metric.mle(this));
			report.put("true_value", this.trueWinRate);
			report.put("properties", this.getPropertiesMap());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return report;
	}

	@Override
	public BanditArm clone() {
		// TODO Auto-generated method stub
		return null;
	}




}