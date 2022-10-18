package edu.napier.ULSG.MAP_Elites.probdefs;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import edu.napier.ULSG.MAP_Elites.Archive;
import edu.napier.ULSG.MAP_Elites.Elite;
import edu.napier.ULSG.problem.Individual;
import edu.napier.ULSG.problem.Individual.Characteristic;
import edu.napier.ULSG.problem.MVehicleProblem;
import edu.napier.ULSG.vehicles.VehicleType;

public class CO2_CostPerDelivery_TimeOfLastDel_PCWalkRoutes_PCCycleRoutes extends ProblemDefinition{
	/*
	 * Neil Urquhart 2021
	 * An implementation of MAPElitesKeyGen for the Supermarket home deliveries example
	 * No Routes, Total Cost, No of Bikes, Deliveries by Bike, CO2
	 * 
	 * 5 buckets over 5 dimensions 
	 */

	int CO2=0;
	int CostDel=1;
	int TimeLastDel=2;
	int Walks=3;
	int Bikes=4;

	/*
	 * store the range for each dimension
	 */

	private  double MAX_CO2=0;
	private  double MIN_CO2= Double.MAX_VALUE;

	private  double MAX_CostDel=0;
	private  double MIN_CostDel= Double.MAX_VALUE;

	private  double MAX_TimeLastDel=0;
	private  double MIN_TimeLastDel= Double.MAX_VALUE;

	private  double MAX_Bikes=0;
	private  double MIN_Bikes= Double.MAX_VALUE;

	private  double MAX_Walks=0;
	private  double MIN_Walks= Double.MAX_VALUE;



	public CO2_CostPerDelivery_TimeOfLastDel_PCWalkRoutes_PCCycleRoutes() {
		buckets = 5;
		dimensions = 5;
	}


	public void resetRanges() {
		/*
		 * Reset the dimensions to their default values
		 */

		MAX_CO2=0;
		MIN_CO2= Double.MAX_VALUE;

		MAX_CostDel=0;
		MIN_CostDel= Double.MAX_VALUE;

		MAX_TimeLastDel=0;
		MIN_TimeLastDel= Double.MAX_VALUE;

		MAX_Bikes=0;
		MIN_Bikes= Double.MAX_VALUE;

		MAX_Walks=0;
		MIN_Walks= Double.MAX_VALUE;
	}

	@Override
	public void displayRanges(String fname) {
		//Display the max-min vales for each dimension
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fname));
			writer.write("characteristic,min,max\n");
			writer.write("Cost Del, " + MIN_CostDel + " : " + MAX_CostDel+"\n");
			writer.write("CO2, " + MIN_CO2 + " : " + MAX_CO2+"\n");
			writer.write("Time, To LastDel " + MIN_TimeLastDel + " : " + MAX_TimeLastDel+"\n");
			writer.write("Bikes, " + MIN_Bikes+ " : " + MAX_Bikes+"\n");
			writer.write("Walks, " + MIN_Walks+ " : " + MAX_Walks+"\n");
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public  void updateRanges(ArrayList<Elite> pool) {
		//Update the maximum and minimum values for each dimension.
		//based on the EliteIndividuals in the <pool>

		for (Elite e : pool) {
			updateRanges((Individual)e);
		}
	}


	@Override
	public void updateRanges(Individual i) {
		/*
		 * Update the ranges based on the values found in the solution<i> if the
		 * value is outwith the current range
		 */


		if (i.getCO2()<MIN_CO2)
			MIN_CO2 = i.getCO2();
		if(i.getCO2() >MAX_CO2)
			MAX_CO2 = i.getCO2();

		if (i.costPerDelivery()<MIN_CostDel)
			MIN_CostDel = i.costPerDelivery();
		if(i.costPerDelivery() >MAX_CostDel)
			MAX_CostDel = i.costPerDelivery();

		if (i.timeToLast()<MIN_TimeLastDel)
			MIN_TimeLastDel = i.timeToLast();
		if(i.timeToLast() >MAX_TimeLastDel)
			MAX_TimeLastDel = i.timeToLast();

		if (i.countVehiclesPC(VehicleType.CYCLE)<MIN_Bikes)
			MIN_Bikes = i.countVehiclesPC(VehicleType.CYCLE);
		if(i.countVehiclesPC(VehicleType.CYCLE) >MAX_Bikes)
			MAX_Bikes = i.countVehiclesPC(VehicleType.CYCLE);

		if (i.countVisitsPC(VehicleType.PEDESTRIAN)<MIN_Walks)
			MIN_Walks = i.countVisitsPC(VehicleType.PEDESTRIAN);
		if(i.countVisitsPC(VehicleType.PEDESTRIAN) >MAX_Walks)
			MAX_Walks = i.countVisitsPC(VehicleType.PEDESTRIAN);
	}

	@Override
	public  int getBuckets() { 
		return buckets;
	}

	@Override
	public int[] getKey(HashMap<Characteristic,Double> chars) {
		/*
		 * Map characteristics to key
		 */
		int[] key = new int[getDimensions()];
		key[this.CO2] = getBucket(chars.get(Characteristic.CO2),MIN_CO2,MAX_CO2);
		key[this.CostDel] = getBucket(chars.get(Characteristic.CostPerDelivery),MIN_CostDel,MAX_CostDel);
		key[this.TimeLastDel] = getBucket(chars.get(Characteristic.TimeOfLastDel),MIN_TimeLastDel,MAX_TimeLastDel);
		key[this.Bikes] = getBucket(chars.get(Characteristic.PCCycleRoutes),MIN_Bikes,MAX_Bikes);
		key[this.Walks] = getBucket(chars.get(Characteristic.PCWalkRoutes),MIN_Walks,MAX_Walks);

		return key;
	}



	@Override
	public Characteristic[] getChracteristics() {
		Characteristic[] chars = {Characteristic.CO2,Characteristic.CostPerDelivery,Characteristic.TimeOfLastDel,Characteristic.PCCycleRoutes,Characteristic.PCWalkRoutes};
		return chars;
	}

	@Override
	public  void writeData(Archive ar,String id){
		try {
			//Print nodes
			String nodes="Id,Fitness,CO2,CostPerDel,TimeLastDel,PCCycleRoutes,PCWalkRoutes,";//+Individual.toCSVHeader();
			int buckets=5;
			for (Characteristic c : Characteristic.values()){
				nodes = nodes +(","+c);
			}
			nodes = nodes +"source\n";
			for (int a=0; a < buckets; a ++)
				for (int b=0; b < buckets; b ++)
					for (int c=0; c < buckets; c ++)
						for (int d=0; d < buckets; d ++) 
							for (int e=0; e < buckets; e++) {
								double fit =-1;
								int[] key = {a,b,c,d,e};
								Elite el = ar.get(key);
								String solStr="";
								if (el!=null) {
									fit =el.getFitness();
									solStr = ((Individual)el).toCSV();
								}
								else {
									solStr = "na,na,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1";
								}
								String source = "";
								if (el!= null)
									if (el.getEm() != null)
										source = el.getEm().name()+":"+el.getDecode();
									else
										source = "init";
								nodes= nodes +(a+":"+b+":"+c+":"+d +":"+e+","+fit+","+a+","+b+","+c+","+d+","+e+","+solStr+"," +source+"\n");
							}


			BufferedWriter writer = new BufferedWriter(new FileWriter(id+".nodes.csv"));
			writer.write(nodes);
			writer.close();

			String plusCO2 ="Source,Target,Description\n";
			String lessCO2 ="Source,Target,Description\n";
			String plusCost ="Source,Target,Description\n";
			String lessCost ="Source,Target,Description\n";
			String plusRoutes ="Source,Target,Description\n";
			String lessRoutes ="Source,Target,Description\n";
			String plusCycles ="Source,Target,Description\n";
			String lessCycles ="Source,Target,Description\n";
			String plusCycleDels ="Source,Target,Description\n";
			String lessCycleDels ="Source,Target,Description\n";


			//Print relations
			for (int a=0; a < buckets; a ++)
				for (int b=0; b < buckets; b ++)
					for (int c=0; c < buckets; c ++)
						for (int d=0; d < buckets; d ++)
							for (int e=0; e < buckets; e++){

								if(a<buckets-1)
									plusCO2 = plusCO2 + (a+":"+b+":"+c+":"+d+","+e+","+(a+1)+":"+b+":"+c+":"+d+","+e+",\"+CO2\"\n");
								if(b<buckets-1)
									plusCost = plusCost+(a+":"+b+":"+c+":"+d+","+e+","+a+":"+(b+1)+":"+c+":"+d+","+e+",\"+Cost\"\n");
								if(c<buckets-1)
									plusRoutes = plusRoutes+(a+":"+b+":"+c+":"+d+","+e+","+a+":"+b+":"+(c+1)+":"+d+","+e+",\"+Routes\"\n");
								if(d<buckets-1)
									plusCycles = plusCycles+(a+":"+b+":"+c+":"+d+","+e+","+a+":"+b+":"+c+":"+(d+1)+","+e+",\"+Cycle Routes\"\n");
								if(e<buckets-1)
									plusCycleDels = plusCycleDels+(a+":"+b+":"+c+":"+d+","+e+","+a+":"+b+":"+c+":"+d+","+(e+1)+",\"+Cycle Dels\"\n");

								if(a>0)
									lessCO2 = lessCO2+(a+":"+b+":"+c+":"+d+","+e+","+(a-1)+":"+b+":"+c+":"+d+","+e+",\"-CO2\"\n");
								if(b>0)
									lessCost = lessCost+(a+":"+b+":"+c+":"+d+","+e+","+a+":"+(b-1)+":"+c+":"+d+","+e+",\"-Cost\"\n");
								if(c>0)
									lessRoutes = lessRoutes+(a+":"+b+":"+c+":"+d+","+e+","+a+":"+b+":"+(c-1)+":"+d+","+e+",\"-Routes\"\n");
								if(d>0)
									lessCycles = lessCycles+(a+":"+b+":"+c+":"+d+","+e+","+a+":"+b+":"+c+":"+(d-1)+","+e+",\"-Cycle Routes\"\n");
								if(e>0)
									lessCycleDels = lessCycleDels+(a+":"+b+":"+c+":"+d+","+e+","+a+":"+b+":"+c+":"+d+","+(e-1)+",\"-Cycle Dels\"\n");

							}
			writer = new BufferedWriter(new FileWriter(id+".plusCO2.csv"));
			writer.write(plusCO2);
			writer.close();

			writer = new BufferedWriter(new FileWriter(id+".plusCost.csv"));
			writer.write(plusCost);
			writer.close();

			writer = new BufferedWriter(new FileWriter(id+".plusRoutes.csv"));
			writer.write(plusRoutes);
			writer.close();

			writer = new BufferedWriter(new FileWriter(id+".plusCycles.csv"));
			writer.write(plusCycles);
			writer.close();

			writer = new BufferedWriter(new FileWriter(id+".plusCycleDels.csv"));
			writer.write(plusCycleDels);
			writer.close();

			writer = new BufferedWriter(new FileWriter(id+"lessCO2.csv"));
			writer.write(lessCO2);
			writer.close();

			writer = new BufferedWriter(new FileWriter(id+".lessCost.csv"));
			writer.write(plusCost);
			writer.close();

			writer = new BufferedWriter(new FileWriter(id+".lessRoutes.csv"));
			writer.write(plusRoutes);
			writer.close();

			writer = new BufferedWriter(new FileWriter(id+".lessCycles.csv"));
			writer.write(plusCycles);
			writer.close();

			writer = new BufferedWriter(new FileWriter(id+".lessCycleDels.csv"));
			writer.write(plusCycleDels);
			writer.close();




		}

		catch(IOException e) {
			e.getStackTrace();

		}
	}


}
