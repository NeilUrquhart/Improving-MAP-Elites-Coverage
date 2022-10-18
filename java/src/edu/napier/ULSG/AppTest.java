package edu.napier.ULSG;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import edu.napier.ULSG.MAP_Elites.Archive;
import edu.napier.ULSG.MAP_Elites.Elite;
import edu.napier.ULSG.MAP_Elites.MAPElites;
import edu.napier.ULSG.MAP_Elites.probdefs.CO2_TTime_FC_RC;
import edu.napier.ULSG.MAP_Elites.probdefs.ProblemDefinition;
import edu.napier.ULSG.MAP_Elites.probdefs.ProbDefFactory;
import edu.napier.ULSG.problem.Individual;
import edu.napier.ULSG.problem.Individual.Characteristic;
import edu.napier.ULSG.problem.Individual.EvalType;
import edu.napier.ULSG.problem.MVehicleFactory;
import edu.napier.ULSG.problem.MVehicleProblem;
import edu.napier.ULSG.problem.RandomSingleton;
import edu.napier.ULSG.problem.VRPVisit;
import edu.napier.ULSG.problem.VRPea;
import edu.napier.ULSG.vehicles.CargoBike;
import edu.napier.ULSG.vehicles.LargeVan;
import edu.napier.ULSG.vehicles.Pedestrian;
import edu.napier.ULSG.vehicles.Vehicle;
import edu.napier.ULSG.vehicles.VehicleFactory;

/*
 * Neil Urquhart 2019
 * This programme tests a set of CVRP problem instances, using a range of solvers to produce solutions.
 * 
 */


public class AppTest {
	public static void main(String[] args){
		/*Problem instances from.
		 Augerat, P., Belenguer, J., Benavent, E., Corber´an, A., Naddef, D., Rinaldi, G., 1995.
Computational results with a branch and cut code for the capacitated vehicle routing
problem. Tech. Rep. 949-M, Universit´e Joseph Fourier, Grenoble, France.


		 */
		Experiment ex = Experiment.getInstance(args[0]);
		//String[] fleet = {"CargoBike","LargeVan","Pedestrian"};
		for (String fName : ex.getGeoFiles()) {
			ex.setCurrentGeo(fName);
			run(fName,ex.getFleet(),ex.getProblemDefinition());
		}
	}

	private static void run(String probName,String[] fleet, String probDef) {
		/*
		 * Solve the instance named in  <probName>
		 */

		MVehicleProblem myVRP = MVehicleFactory.buildProblem(Experiment.getInstance().getDataDir()+ probName+".vrp", VehicleFactory.createFleet(fleet));//Load instance from file
		ProblemDefinition keyGen = ProbDefFactory.createKeyGen(probDef);
		Individual.setEvalType(EvalType.simple); //simple eval type for init



		ArrayList<Elite> pool = createPool(Experiment.getInstance().getDataDir()+ probName+"-"+probDef+"-pool.csv",myVRP, keyGen.getChracteristics());



		keyGen.updateRanges(pool);
		keyGen.displayRanges(probName +"-"+probDef+"-initRange.csv");
		Individual.setKeyGen(keyGen);

		Individual.setCharacteristic(Characteristic.Distance);
		Experiment ex = Experiment.getInstance();
		Archive combined = new Archive(keyGen.getDimensions(),keyGen.getBuckets());
		//Run ME 10 times

//		if (ex.getMultiDecoder())
//			Individual.setEvalType(EvalType.multi);
		if (ex.getAdvDecoder())
			Individual.setEvalType(EvalType.advanced);

		for (int c=0; c<10; c++) {
			if (Experiment.getInstance().getSplitDecode()) {
				if  (c<5)
					Individual.setEvalType(EvalType.simple);
				else 
					Individual.setEvalType(EvalType.advanced);
			}
			
			Logger.getInstance().setFname(c+"."+probName);
			ex.setRun(c);
			MAPElites me = new MAPElites(keyGen,myVRP);
			me.addPool(pool);

			myVRP.solve(me);
			combined.putAll(me.getArchive().toList());
		}
		keyGen.writeData(combined,ex.getId());
	}

	private static ArrayList<Elite> createPool(String cacheName, MVehicleProblem myVRP,Characteristic[] chars) {
		ArrayList<Elite> pool = new ArrayList<Elite>();

		//Load pool from cache, if it exists
		boolean loaded = true;
		
		try (BufferedReader br = new BufferedReader(new FileReader(cacheName))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		       Individual i = new Individual(myVRP, line.split(","));
		       i.evaluate();
		       pool.add(i);
		    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			loaded = false;
		}

		if (!loaded) {

			for(Characteristic ch: chars) {
				for (int c=0; c<10; c++) {
					VRPea eaSolve = new VRPea();
					RandomSingleton.getInstance().setSeed(c);
					Individual.setCharacteristic(ch);
					myVRP.solve(eaSolve);
					pool.addAll(eaSolve.getPopulation());
				}
			}

			//Save pool

			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(cacheName));
				for (Elite e : pool)
					writer.write(e.getCSV());
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return pool;
	}
}
