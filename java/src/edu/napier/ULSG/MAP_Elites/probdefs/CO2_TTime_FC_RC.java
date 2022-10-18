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

public class CO2_TTime_FC_RC extends ProblemDefinition{
	/*
	 * Neil Urquhart 2021
	 * An implementation of MAPElitesKeyGen for the Supermarket home deliveries example
	 * 
	 * 5 buckets over 4 dimensions 
	 */
	int CO2=0;
	int TTime=1;
	int FCost=2;
	int RCcost=3;



	/*
	 * store the range for each dimension
	 */
	private  double MAX_TTime=0;
	private  double MIN_TTime= Double.MAX_VALUE;

	private  double MAX_CO2=0;
	private  double MIN_CO2= Double.MAX_VALUE;

	private  double MAX_FCost=0;
	private  double MIN_FCost= Double.MAX_VALUE;

	private  double MAX_RCost=0;
	private  double MIN_RCost= Double.MAX_VALUE;

	public CO2_TTime_FC_RC() {
		buckets = 5;
		dimensions = 4;
	}


	public void resetRanges() {
		/*
		 * Reset the dimensions to their default values
		 */

		MAX_TTime =0;
		MIN_TTime = Double.MAX_VALUE;

		MAX_CO2 =0;
		MIN_CO2 = Double.MAX_VALUE;

		MAX_FCost =0;
		MIN_FCost = Double.MAX_VALUE;

		MAX_RCost =0;
		MIN_RCost = Double.MAX_VALUE;
	}

	@Override
	public void displayRanges(String fname) {
		//Display the max-min vales for each dimension
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fname));
			writer.write("characteristic,min,max\n");
			writer.write("Total Time, " + MIN_TTime + " , " + MAX_TTime +"\n");
			writer.write("CO2, " + MIN_CO2 + " , " + MAX_CO2 +"\n");
			writer.write("Fixed Veh Cost, " + MIN_FCost + " , " + MAX_FCost +"\n");
			writer.write("Veh Run cost, " + MIN_RCost+ " , " + MAX_RCost +"\n");

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

	//	@Override
	//	public boolean inRange(Individual i) {
	//
	//		if (i.getCO2()<MIN_CO2)
	//			return false;
	//		if(i.getCO2() >MAX_CO2)
	//			return false;
	//		if (i.timeToLast()<MIN_TTime)
	//			return false;
	//		if(i.timeToLast() >MAX_TTime)
	//			return false;
	//
	//		if (i.getRCost()<MIN_RCost)
	//			return false;
	//		if(i.getRCost() >MAX_RCost)
	//			return false;
	//		if (i.getFixedCost()<MIN_FCost)
	//			return false;
	//		if(i.getFixedCost() >MAX_FCost)
	//			return false;
	//
	//		return true;
	//	}

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

		if (i.timeToLast()<MIN_TTime)
			MIN_TTime = i.timeToLast();
		if(i.timeToLast() >MAX_TTime)
			MAX_TTime = i.timeToLast();

		if (i.getRCost()<MIN_RCost)
			MIN_RCost = i.getRCost();
		if(i.getRCost() >MAX_RCost)
			MAX_RCost = i.getRCost();

		if (i.getFixedCost()<MIN_FCost)
			MIN_FCost = i.getFixedCost();
		if(i.getFixedCost() >MAX_FCost)
			MAX_FCost = i.getFixedCost();
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
		key[this.FCost] = getBucket(chars.get(Characteristic.FixedCost),MIN_FCost,MAX_FCost);
		key[this.RCcost] = getBucket(chars.get(Characteristic.RunningCost),MIN_RCost,MAX_RCost);
		key[this.TTime] = getBucket(chars.get(Characteristic.TimeOfLastDel),MIN_TTime,MAX_TTime);

		return key;
	}



	@Override
	public Characteristic[] getChracteristics() {
		Characteristic[] chars = {Characteristic.CO2,Characteristic.TimeOfLastDel,Characteristic.RunningCost,Characteristic.FixedCost};
		return chars;
	}

	@Override
	public  void writeData(Archive ar,String id){
		try {
			id=id.replace(' ','.');
			id=id.replace('_','.');
			id=id.replace('-','.');
			//Print nodes
			String nodes="Id,Fitness,CO2,Time,Fixed Cost,Running Cost,,";//+Individual.toCSVHeader();
			int buckets=5;
			for (Characteristic c : Characteristic.values()){
				nodes = nodes +(",Raw"+c);
			}
			nodes = nodes +",source\n";
			for (int a=0; a < buckets; a ++)
				for (int b=0; b < buckets; b ++)
					for (int c=0; c < buckets; c ++)
						for (int d=0; d < buckets; d ++) {
							double fit =-1;
							int[] key = {a,b,c,d};
							Elite e = ar.get(key);
							String solStr="";
							if (e!=null) {
								fit =e.getFitness();
								solStr = ((Individual)e).toCSV();
							}else {
								solStr= ",na,na,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1";
							}

							String source = "-1";
							if (e!= null)
								if (e.getEm() != null)
									source = e.getEm().name()+":"+e.getDecode();
								else
									source = "init";

							nodes= nodes +(a+":"+b+":"+c+":"+d +","+fit+","+a+","+b+","+c+","+d+","+solStr+"," +source+"\n");
						}


			BufferedWriter writer = new BufferedWriter(new FileWriter(id+".nodes.csv"));
			writer.write(nodes);
			writer.close();

			String plusCO2 ="";//"Source,Target,Weight,Description\n";
			String plusTime="";//"Source,Target,Weight,Description\n";
			String plusFCost="";//"Source,Target,Weight,Description\n";
			String plusRCost="";//"Source,Target,Weight,Description\n";
			String lessCO2 ="";//"Source,Target,Weight,Description\n";
			String lessTime="";//"Source,Target,Weight,Description\n";
			String lessFCost="";//"Source,Target,Weight,Description\n";
			String lessRCost="";//"Source,Target,Weight,Description\n";
			//Print relations
			for (int a=0; a < buckets; a ++)
				for (int b=0; b < buckets; b ++)
					for (int c=0; c < buckets; c ++)
						for (int d=0; d < buckets; d ++) {

							//							if(a<buckets-1)
							//								plusCO2 = plusCO2 + (a+":"+b+":"+c+":"+d+","+(a+1)+":"+b+":"+c+":"+d+",\"+CO2\"\n");
							//							if(b<buckets-1)
							//								plusTime = plusTime+(a+":"+b+":"+c+":"+d+","+a+":"+(b+1)+":"+c+":"+d+",\"+Time\"\n");
							//							if(c<buckets-1)
							//								plusFCost = plusFCost+(a+":"+b+":"+c+":"+d+","+a+":"+b+":"+(c+1)+":"+d+",\"+Fixed Cost\"\n");
							//							if(d<buckets-1)
							//								plusRCost = plusRCost+(a+":"+b+":"+c+":"+d+","+a+":"+b+":"+c+":"+(d+1)+",\"+Running Cost\"\n");
							//							if(a>0)
							//								lessCO2 = lessCO2+(a+":"+b+":"+c+":"+d+","+(a-1)+":"+b+":"+c+":"+d+",\"-CO2\"\n");
							//							if(b>0)
							//								lessTime=lessTime+(a+":"+b+":"+c+":"+d+","+a+":"+(b-1)+":"+c+":"+d+",\"-Time\"\n");
							//							if(c>0)
							//								lessFCost = lessFCost +(a+":"+b+":"+c+":"+d+","+a+":"+b+":"+(c-1)+":"+d+",\"-Fixed Cost\"\n");
							//							if(d>0)
							//								lessRCost = lessRCost +(a+":"+b+":"+c+":"+d+","+a+":"+b+":"+c+":"+(d-1)+",\"-Running Cost\"\n");

							int[] key = {a,b,c,d};
							Elite e = ar.get(key);
							if (e!=null) {//If node exists


								for (int pco2 = a+1; pco2 < buckets;pco2++) {
									int[] newKey = {pco2,b,c,d};
									if (ar.get(newKey)!=null) {
										int diff = Math.abs(pco2-a);
										plusCO2 = plusCO2 + (a+":"+b+":"+c+":"+d+","+pco2+":"+b+":"+c+":"+d+","+diff+",Inc CO2\n");
										break;
									}
								}

								for (int ptime = b+1; ptime < buckets;ptime++) {
									int[] newKey = {a,ptime,c,d};
									if (ar.get(newKey)!=null) {
										int diff = Math.abs(ptime-b);
										plusTime = plusTime+(a+":"+b+":"+c+":"+d+","+a+":"+ptime+":"+c+":"+d+","+diff+",Inc Time\n");
										break;
									}
								}


								for (int pfcost = c+1; pfcost < buckets;pfcost++) {
									int[] newKey = {a,b,pfcost,d};
									if (ar.get(newKey)!=null) {
										int diff = Math.abs(pfcost-c);
										plusFCost = plusFCost+(a+":"+b+":"+c+":"+d+","+a+":"+b+":"+pfcost+":"+d+","+diff+",Inc Fixed Cost\n");
										break;
									}
								}

								for (int prcost = d+1; prcost < buckets;prcost++) {
									int[] newKey = {a,b,c,prcost};
									if (ar.get(newKey)!=null) {
										int diff = Math.abs(prcost-d);
										plusRCost = plusRCost+(a+":"+b+":"+c+":"+d+","+a+":"+b+":"+c+":"+(prcost)+","+diff+",Inc Running Cost\n");
										break;
									}
								}

								for (int pco2 = a-1; pco2 >= 0;pco2--) {
									int[] newKey = {pco2,b,c,d};
									if (ar.get(newKey)!=null) {
										int diff = Math.abs(pco2-a);
										lessCO2 = lessCO2+(a+":"+b+":"+c+":"+d+","+(a-1)+":"+b+":"+c+":"+d+","+diff+",Dec CO2\n");
										break;
									}
								}

								for (int ptime = b-1; ptime >= 0;ptime--) {
									int[] newKey = {a,ptime,c,d};
									if (ar.get(newKey)!=null) {
										int diff = Math.abs(ptime-b);
										lessTime=lessTime+(a+":"+b+":"+c+":"+d+","+a+":"+(b-1)+":"+c+":"+d+","+diff+",Dec Time\n");
										break;
									}
								}


								for (int pfcost = c-1; pfcost >= 0;pfcost--) {
									int[] newKey = {a,b,pfcost,d};
									if (ar.get(newKey)!=null) {
										int diff = Math.abs(pfcost-c);
										lessFCost = lessFCost +(a+":"+b+":"+c+":"+d+","+a+":"+b+":"+pfcost+":"+d+","+diff+",Dec Fixed Cost\n");
										break;
									}
								}
								for (int prcost = d-1; prcost >= 0;prcost--) {
									int[] newKey = {a,b,c,prcost};
									if (ar.get(newKey)!=null) {
										int diff = Math.abs(prcost-d);
										lessRCost = lessRCost +(a+":"+b+":"+c+":"+d+","+a+":"+b+":"+c+":"+(prcost)+","+diff+",Dec Running Cost\n");
										break;
									}
								}
							}
						}

			writer = new BufferedWriter(new FileWriter(id+".relations.csv"));
			writer.write("Source,Target,Weight,Description\n");
			writer.write(plusCO2);
			writer.write(plusTime);
			writer.write(plusRCost);
			writer.write(plusFCost);
			writer.write(lessCO2);
			writer.write(lessRCost);
			writer.write(lessFCost);
			writer.write(lessTime);
			writer.close();
			//			writer = new BufferedWriter(new FileWriter(id+".plusCO2.csv"));
			//			writer.write(plusCO2);
			//			writer.close();
			//
			//			writer = new BufferedWriter(new FileWriter(id+".plusTime.csv"));
			//			writer.write(plusTime);
			//			writer.close();
			//
			//			writer = new BufferedWriter(new FileWriter(id +".plusRCost.csv"));
			//			writer.write(plusRCost);
			//			writer.close();
			//
			//			writer = new BufferedWriter(new FileWriter(id +".plusFCost.csv"));
			//			writer.write(plusFCost);
			//			writer.close();
			//
			//			writer = new BufferedWriter(new FileWriter(id+".lessCO2.csv"));
			//			writer.write(lessCO2);
			//			writer.close();
			//
			//			writer = new BufferedWriter(new FileWriter(id+".lessRCost.csv"));
			//			writer.write(lessRCost);
			//			writer.close();
			//
			//			writer = new BufferedWriter(new FileWriter(id+".lessFCost.csv"));
			//			writer.write(lessFCost);
			//			writer.close();
			//
			//			writer = new BufferedWriter(new FileWriter(id+".lessTime.csv"));
			//			writer.write(lessTime);
			//			writer.close();
		}

		catch(IOException e) {
			e.getStackTrace();

		}
	}


}
