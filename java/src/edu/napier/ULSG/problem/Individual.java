package edu.napier.ULSG.problem;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.stream.Collector.Characteristics;

import edu.napier.ULSG.Experiment;
import edu.napier.ULSG.MAP_Elites.Elite;
import edu.napier.ULSG.MAP_Elites.emitters.Emitter;
import edu.napier.ULSG.MAP_Elites.probdefs.ProblemDefinition;
import edu.napier.ULSG.vehicles.Vehicle;
import edu.napier.ULSG.vehicles.VehicleType;


/*
 * Neil Urquhart 2019
 * This class represents a single CVRP solution to be used within an Evolutionary Algorithm.
 * 
 * The basic solution (a grand tour) is stored in the genotype. Once the solution has been
 * evaluated then the solution is stored in the genotype.
 * 
 */
public class Individual implements Cloneable, Elite,Serializable  {
	//Solution characteristics
	public enum Characteristic{//Solution characteristics
		Distance,NoRoutes,CO2,TotalCost,FixedCost,RunningCost,
		TimeOfLastDel,AvgDelTime,CostPerDelivery,CO2PerDelivery,
		PCICRoutes,PCWalkRoutes,PCEVRoutes,PCCycleRoutes,
		PCICVisits,PCWalkVisits,PCEVVisits,PCCycleVisits,
		PCICDist,PCWalkDist,PCEVDist,PCCycleDist,
		PCICVol,PCWalkVol,PCEVVol,PCCycleVol
	} 
	public enum EvalType  {simple,advanced};//,multi};
	
	private static EvalType currentEvalType; //EvalType currently in use
	
	public static void setEvalType(EvalType et) {
		currentEvalType = et;
	}
	
	private static ProblemDefinition keygen = null;
	
	
	//Used when calculating fitness - distance is the default. Important that this is consistant, hence static
	public static void setKeyGen(ProblemDefinition k) {
		keygen =k;
	}


	private static Characteristic fitness = Characteristic.Distance;
	//Used when calculating fitness - distance is the default. Important that this is consistant, hence static
	public static void setCharacteristic(Characteristic c) {
		fitness = c;
	}

	//Use the RandomSingleton object to get access to a seeded random number generator
	protected RandomSingleton rnd =RandomSingleton.getInstance();

	//The genotype is a "grand tour" list of visits
	protected ArrayList<Gene> genotype;

	//The phenotype is a set of routes created from the genotype
	protected ArrayList<Route> phenotype;

	//THe problem being solved
	protected MVehicleProblem problem;


	public class Gene implements Cloneable, Serializable{
		public Gene(VRPVisit v, Vehicle t) {
			this.visit = v;
			this.prefered = t;
		}

		public Gene(VRPVisit v, Vehicle t, boolean newRoute) {
			this.visit = v;
			this.prefered = t;
			this.newRoute = newRoute;
		}
		protected Object clone() throws CloneNotSupportedException {
			Gene g = (Gene)super.clone();
			g.newRoute = this.newRoute;
			g.prefered = this.prefered;
			return g;
		}

		public void setPrefered(Vehicle v) {
			prefered  = v;
		}

		public Vehicle getPrefered() {
			return prefered;
		}

		public boolean getNewRoute() {
			return newRoute;
		}

		public void setNewRoute(boolean nr) {
			newRoute = nr;
		}

		public VRPVisit getVisit() {
			return visit;
		}

		VRPVisit visit;
		private boolean newRoute=false;
		private Vehicle prefered;
	}

	public void setEmitterTypeUsed(Emitter em) {
		this.emitterTypeUsed = em;
	}
	
	public Individual( MVehicleProblem prob, Vehicle vh) {
		/*
		 * Constructor to create a new random genotype
		 */
		problem = prob;
		genotype = new ArrayList<Gene>();
		for (Visit v : prob.getSolution()){
			Gene g = new Gene((VRPVisit)v,vh);
			genotype.add(g);
		}
		
		int sz = genotype.size();
		genotype = randomize(genotype);
		if (sz!=genotype.size()) {
			System.out.println("ERROR!!!");
			System.exit(-1);
		}
			
		phenotype = null;
	}

	public Individual( MVehicleProblem prob, String[] csv) {
		/*
		 * Constructor to create a new Individual with a gene based on CSV
		 */
		problem = prob;
		genotype = new ArrayList<Gene>();
		int c=0;
		while(c < csv.length) {
		   String v = csv[c]; c++;
		   VRPVisit visit = null;
		   Boolean newR = false;
		   for (Object o : prob.getVisits()) {
			   VRPVisit vis = (VRPVisit)o;
			   if (vis.theName.equals(v))
				   visit = vis;
		   }
		   
		   String r = csv[c]; c++;
		   if (r.equals("TRUE"))
			   newR = true;
		   
		   Vehicle veh=null;
		   String vh = csv[c]; c++;
		   for (Vehicle pV :prob.vehicles)
			   if (pV.getDescription().equals(vh))
				   veh = pV;
		   
		  
			Gene g = new Gene(visit,veh,newR);
			genotype.add(g);
		}
		
		phenotype = null;
	}


	public Object clone() throws CloneNotSupportedException {
		Individual i = (Individual)super.clone();
		i.phenotype = null;
		i.genotype = new ArrayList<Gene>();
		for (Gene g : this.genotype) {
			i.genotype.add((Gene)g.clone());
		}
		return i;
	}

	public ArrayList<Gene> getGenotype(){
		return genotype;
	}

	public MVehicleProblem getProblem() {
		return this.problem;
		
	}
	
	public Individual (MVehicleProblem prob, Individual parent1, Individual parent2) throws CloneNotSupportedException{
		/*
		 * Create a new Individual based on the recombination of genes from <parent1> and <parent2>
		 */
		problem = prob;
		genotype = new ArrayList<Gene>();
		int xPoint = rnd.getRnd().nextInt(parent1.genotype.size());

		//copy all of p1 to the xover point
		for (int count =0; count < xPoint; count++ ){
			genotype.add((Gene)parent1.genotype.get(count).clone());
		}

		//Now add missing genes from p2
		for (int count =0; count < parent2.genotype.size(); count++){
			Gene v = parent2.genotype.get(count);
			if  (!contains(genotype,v)){//(!genotype.contains(v)){
				genotype.add((Gene)v.clone());
			}
		}
	}





	private boolean contains(ArrayList<Gene> gt, Gene v) {
		for (Gene g : gt) {
			if (g.visit == v.visit)
				return true;
		}
		return false;
	}

	private ArrayList randomize(ArrayList list) {
		// Randomly shuffle the contents of <list>
		Random  r= rnd.getInstance().getRnd();

		for (int c=0; c < list.size();c++) {
			Object o = list.remove(r.nextInt(list.size()));
			list.add(r.nextInt(list.size()),o);
		}
		return list;
	}
	public void mutate() {
		//Mutate the genotype, by randomly moving a gene.
		//OR flipping  new Route
		//OR changing preferred vehicle

		phenotype = null;
		float c = rnd.getRnd().nextFloat();
		if(c < 0.8) {
			int rndGene = rnd.getRnd().nextInt(genotype.size());
			Gene v = genotype.remove(rndGene);
			int addPoint = rnd.getRnd().nextInt(genotype.size());
			genotype.add(addPoint,v);
		}else if (c < 0.95) {
			int rndGene = rnd.getRnd().nextInt(genotype.size());
			Gene v = genotype.get(rndGene);
			v.prefered = problem.rndVehicle();
		}else {


			int rndGene = rnd.getRnd().nextInt(genotype.size());
			Gene v = genotype.get(rndGene);
			v.newRoute = !v.newRoute;
		}
	}

	
	
//	public class EvalResult{
//		public double fitness;
//		public EvalType type;
//	}
	
	private EvalType evalTypeUsed;
	private Emitter emitterTypeUsed;
	
	public EvalType getEvalTypeUsed() {
		return evalTypeUsed;
	}
	
	public double evaluate() {
		/*
		 * Build a phenotype based upon the genotype
		 * Only build the genotyoe if the phenotype has been set to null
		 * Return the fitness 
		 */
//		if (phenotype == null) {//Only evaluate if necessary
//			if (currentEvalType == EvalType.multi) {
//				//Multi decoder!!
//					simpleDecode();
//					double sf = getFitness();
//					phenotype=null; //reset
//					advDecode();
//					double af = this.getFitness();
//					if (sf < af) {
//						phenotype=null; //reset
//						simpleDecode();
//						evalTypeUsed = EvalType.simple;
//					}
//					else
//						evalTypeUsed = EvalType.advanced;
//			}
//			else {
//				if (currentEvalType == EvalType.advanced)
//					advDecode();
//				else
//					simpleDecode();
			//}
//		}

//		return this.getFitness();
		return evaluate(this.currentEvalType);
	}
	
	public double evaluate(EvalType et) {
		/*
		 * Build a phenotype based upon the genotype
		 * Only build the genotyoe if the phenotype has been set to null
		 * Return the fitness 
		 */
		if (phenotype == null) {//Only evaluate if necessary
			
			
			if (et == EvalType.simple) {
				simpleDecode();
				this.evalTypeUsed = EvalType.simple;
			}
			else {
				advDecode();
				this.evalTypeUsed = EvalType.advanced;
			}
		}
		return this.getFitness();
	}
	
	private void advDecode() {
		//Advanced decoder

		phenotype = new ArrayList<Route> ();
		boolean added = false;
		for (Gene g : genotype){
			added = false;
			if (!g.newRoute) {
				for (Route r: phenotype) {
					if (r.getVehicle() == g.getPrefered()) {
						//Can we add to this?
						if (r.getDemand() + g.getVisit().demand < r.getVehicle().getCapacity()) {
							r.add(g.getVisit());
							added = true;
							break;
						}
					}
				}
			}
			if (!added) {
				Route nr = new Route(this);
				nr.setVehicle(g.getPrefered());
				nr.add(g.getVisit());
				phenotype.add(nr);
			}
		}
		//		/*
		//		 * Check code. Comment out for production
		//		 */
		//		int vCount = genotype.size();
		//		int gCount =0;
		//		for (Route r : phenotype)
		//		   gCount = gCount + r.size();
		//		
		//		
		//		if (vCount != gCount) {
		//			System.out.println("Rats....!");
		//		}
		//		/*
		//		 * Done check
		//		 * 
		//		 */
		this.updateCharacteristics();
	}

	private void simpleDecode() {
		phenotype = new ArrayList<Route> ();
		Route newRoute = new Route(this);
		newRoute.setVehicle(genotype.get(0).prefered);
		for (Gene v : genotype){
			if ((v.getVisit().getDemand() + routeDemand(newRoute) > newRoute.getVehicle().getCapacity()/*problem.getCapacity()*/)
					||(v.newRoute == true)){
				//If next visit cannot be added  due to capacity constraint then
				//start new route.
				phenotype.add(newRoute);
				newRoute = new Route(this);
				newRoute.setVehicle(v.prefered);
			}
			newRoute.add(v.getVisit());
		}
		phenotype.add(newRoute);
		this.updateCharacteristics();
	}

	public ArrayList<Route> getPhenotype(){
		return phenotype;
	}

	public double getDistance(){
		if (phenotype == null)
			//If the genotype has been changed then evaluate
			evaluate();
		double dist=0;
		for (Route r : this.phenotype)
			dist = dist + r.getDist();
		return dist;
		//return problem.getDistance(phenotype);
	}

	public int getVehicles() {
		if (phenotype == null)
			//If the genotype has been changed then evaluate
			evaluate();
		return phenotype.size();
	}

	protected int routeDemand(ArrayList<VRPVisit> route){
		//Return the total cumulative demand within <route>
		int demand=0;
		for (VRPVisit visit: route){
			demand += visit.getDemand();
		}
		return demand;
	}


	//	public Individual copy() {
	//		//Create a new individual that is a direct copy of this individual
	//		Individual copy = new Individual(this.problem);
	//		copy.genotype = (ArrayList<Gene>) this.genotype.clone();
	//		return copy;
	//	}

	public void check() {
		/*
		 * Use this method when testing new crossover or mutation operators
		 * 
		 */
		int targetCusts = this.problem.getVisits().size();

		if (targetCusts != this.genotype.size()) {
			System.out.println("Genotype size error\n Should be "+ targetCusts +" actual "+ this.genotype.size());

			System.exit(-1);
		}

		int phenocount=0;
		if (phenotype != null) {
			for (ArrayList route: phenotype) {
				phenocount = phenocount + route.size();
			}
			if (targetCusts != phenocount) {
				System.out.println("Phenotype size error");
				System.exit(-1);
			}
		}
	}

	public String toCSV() {
		String res="";
		for (Gene v : genotype) {
			res = res + v.visit +"["+v.newRoute+"-"+v.prefered +"]"+":";
		}

		if (phenotype != null) {
			res = res+",SOL";
			for (Route route: phenotype) {
				res= res +":R:";
				res = res + route.getVehicle() + ":";
				for (VRPVisit v : route) {
					res = res + v.theName+":";
				}

			}
		}
		//res = res + this.characteristics;	
		for (Characteristic c : Characteristic.values()){
			res= res+(","+characteristics.get(c));
		}

		return res;
	}

	public static String toCSVHeader() {//Note static to allow use to generate a CSV header
		String res="Genotype";



		res = res+",Solution String";

		//res = res + this.characteristics;	
		for (Characteristic c : Characteristic.values()){
			res= res+(","+c.toString());
		}

		return res;
	}
	// Solution characteristics
	public double getCO2() {
		double co2=0;
		for(Route r: phenotype) { 
			co2 = co2 +r.getCO2(); 
		} 
		return co2;
	}

	public double getRCost() {
		double rCost =0;
		for(Route r: phenotype) { 
			rCost = rCost +r.getRCost(); 
		} 
		return rCost;
	}

	public double  timeToLast() {
		double time=0;
		for (Route r : phenotype) {
			if (r.timeToLast() > time)
				time = r.timeToLast();
		}
		return time;
	}

	public double getFixedCost() {
		double fCost=0;
		for(Route r : phenotype)
			fCost = fCost + r.getFixedCost();
		return fCost;
	}

	public double getAvgTime() {
		double tot=0;
		for(Route r: phenotype) {
			tot = tot + r.getAvgTime();
		}
		double res = tot/(double)this.getRoutes();
		//		if (Double.isNaN(res))
		//			System.out.println("NaN!!!");

		return res;
	}

	public double costPerDelivery() {
		return (this.getFixedCost() + this.getRCost())/problem.getSize() ;
	}

	public double co2PerDelivery() {
		return this.getCO2()/problem.getSize() ;
	}

	public int getRoutes() {
		return phenotype.size();
	}

	//	public int countVehicles(VehicleType vt) {
	//		//Count the ocurrencies of a particular type
	//		int c=0;
	//		for (Route r: phenotype)
	//			if(r.getVehicle().getType() == vt)
	//				c++;
	//		return c;
	//	}

	public int countVehiclesPC(VehicleType vt) {
		//PC of the total vehicles that are this type
		float c=0;
		for (Route r: phenotype)
			if(r.getVehicle().getType() == vt)
				c++;
		return Math.round((c/(float)phenotype.size())*100);
	}

	public int countVisitsPC(VehicleType vt) {
		//PC of the total vehicles that are this type
		float c=0;
		for (Route r: phenotype)
			if(r.getVehicle().getType() == vt)
				c= c+ r.size();

		return Math.round((c/ problem.getVisits().size())*100);
	}

	public long countVolPC(VehicleType vt) {
		//PC of the total vehicles that are this type
		double c=0;
		for (Route r: phenotype)
			if(r.getVehicle().getType() == vt)
				c= c+ r.getDemand();

		return Math.round((c/ problem.getDemand())*100);
	}

	public long countDistPC(VehicleType vt) {
		//PC of the total vehicles that are this type
		double c=0;
		for (Route r: phenotype)
			if(r.getVehicle().getType() == vt)
				c= c+ r.getDist();

		return Math.round((c/ this.getDistance())*100);
	}
	//Methods for Elite


	private HashMap<Characteristic,Double> characteristics = new HashMap<Characteristic,Double> ();

	private void updateCharacteristics() {
		//characteristics.clear();
		characteristics = new HashMap<Characteristic,Double> ();
		characteristics.put(Characteristic.Distance, new Double(this.getDistance()));
		characteristics.put(Characteristic.NoRoutes, new Double(this.getRoutes()));
		characteristics.put(Characteristic.CO2, new Double(this.getCO2()));
		characteristics.put(Characteristic.TotalCost, new Double(this.getFixedCost()+this.getRCost()));
		characteristics.put(Characteristic.FixedCost, new Double(this.getFixedCost()));
		characteristics.put(Characteristic.RunningCost, new Double(this.getRCost()));
		characteristics.put(Characteristic.TimeOfLastDel, new Double(this.timeToLast()));
		characteristics.put(Characteristic.AvgDelTime, new Double(this.getAvgTime()));
		characteristics.put(Characteristic.CostPerDelivery, new Double(this.costPerDelivery()));
		characteristics.put(Characteristic.CO2PerDelivery, new Double(this.co2PerDelivery()));
		//		characteristics.put(Characteristic.NoICRoutes, new Double(this.countVehicles(VehicleType.INTERNAL_COMBUSTION)));
		//		characteristics.put(Characteristic.NoWalkRoutes, new Double(this.countVehicles(VehicleType.PEDESTRIAN)));
		//		characteristics.put(Characteristic.NoEVRoutes, new Double(this.countVehicles(VehicleType.ELECTRIC)));
		//		characteristics.put(Characteristic.NoCycleRoutes, new Double(this.countVehicles(VehicleType.CYCLE)));
		characteristics.put(Characteristic.PCICRoutes, new Double(this.countVehiclesPC(VehicleType.INTERNAL_COMBUSTION)));
		characteristics.put(Characteristic.PCEVRoutes, new Double(this.countVehiclesPC(VehicleType.ELECTRIC)));
		characteristics.put(Characteristic.PCCycleRoutes, new Double(this.countVehiclesPC(VehicleType.CYCLE)));
		characteristics.put(Characteristic.PCWalkRoutes, new Double(this.countVehiclesPC(VehicleType.PEDESTRIAN)));

		characteristics.put(Characteristic.PCICVisits, new Double(this.countVisitsPC(VehicleType.INTERNAL_COMBUSTION)));
		characteristics.put(Characteristic.PCEVVisits, new Double(this.countVisitsPC(VehicleType.ELECTRIC)));
		characteristics.put(Characteristic.PCCycleVisits, new Double(this.countVisitsPC(VehicleType.CYCLE)));
		characteristics.put(Characteristic.PCWalkVisits, new Double(this.countVisitsPC(VehicleType.PEDESTRIAN)));

		characteristics.put(Characteristic.PCICVol, new Double(this.countVolPC(VehicleType.INTERNAL_COMBUSTION)));
		characteristics.put(Characteristic.PCEVVol, new Double(this.countVolPC(VehicleType.ELECTRIC)));
		characteristics.put(Characteristic.PCCycleVol, new Double(this.countVolPC(VehicleType.CYCLE)));
		characteristics.put(Characteristic.PCWalkVol, new Double(this.countVolPC(VehicleType.PEDESTRIAN)));

		characteristics.put(Characteristic.PCICDist, new Double(this.countDistPC(VehicleType.INTERNAL_COMBUSTION)));
		characteristics.put(Characteristic.PCEVDist, new Double(this.countDistPC(VehicleType.ELECTRIC)));
		characteristics.put(Characteristic.PCCycleDist, new Double(this.countDistPC(VehicleType.CYCLE)));
		characteristics.put(Characteristic.PCWalkDist, new Double(this.countDistPC(VehicleType.PEDESTRIAN)));

	}


	@Override
	public double getFitness() {
		this.updateCharacteristics();
		return characteristics.get(fitness);

		//		//TODO: use characteristics...
		//		if (fitness == Characteristic.NoRoutes){
		//			return this.getRoutes();
		//		}
		//		else if (fitness == Characteristic.CO2) {
		//			return this.getCO2();
		//		}
		//		else if (fitness == Characteristic.TotalCost) {
		//			return (this.getFixedCost() +this.getRCost());
		//		}
		//		else if (fitness == Characteristic.FixedCost) {
		//			return this.getFixedCost();
		//		}
		//		else if (fitness == Characteristic.RunningCost) {
		//			return this.getRCost();
		//		}
		//		else if (fitness == Characteristic.TimeOfLastDel) {
		//			return this.timeToLast();
		//		}
		//		else if (fitness == Characteristic.AvgDelTime) {
		//			return this.getAvgTime();
		//		}
		//		else if (fitness == Characteristic.CostPerDelivery) {
		//			return this.costPerDelivery();
		//		}
		//		else if (fitness == Characteristic.CO2PerDelivery) {
		//			return this.co2PerDelivery();
		//		}
		////		else if (fitness == Characteristic.NoICRoutes) {
		////			return this.countVehicles(VehicleType.INTERNAL_COMBUSTION);
		////		}
		////		else if (fitness == Characteristic.NoWalkRoutes) {
		////			return this.countVehicles(VehicleType.PEDESTRIAN);
		////		}
		////		else if (fitness == Characteristic.NoEVRoutes) {
		////			return this.countVehicles(VehicleType.ELECTRIC);
		////		}
		////		else if (fitness == Characteristic.NoCycleRoutes) {
		////			return this.countVehicles(VehicleType.CYCLE);
		////		}
		//		else if (fitness == Characteristic.PCICRoutes) {
		//			return this.countRoutesPC(VehicleType.INTERNAL_COMBUSTION);
		//		}
		//		else if (fitness == Characteristic.PCWalkRoutes) {
		//			return this.countRoutesPC(VehicleType.PEDESTRIAN);
		//		}
		//		else if (fitness == Characteristic.PCEVRoutes) {
		//			return this.countRoutesPC(VehicleType.ELECTRIC);
		//		}
		//		else if (fitness == Characteristic.PCCycleRoutes) {
		//			return this.countRoutesPC(VehicleType.CYCLE);
		//		}
		//		
		//		else if (fitness == Characteristic.PCICDist) {
		//			return this.distPC(VehicleType.INTERNAL_COMBUSTION);
		//		}
		//		else if (fitness == Characteristic.PCWalkDist) {
		//			return this.distPC(VehicleType.PEDESTRIAN);
		//		}
		//		else if (fitness == Characteristic.PCEVDist) {
		//			return this.distPC(VehicleType.ELECTRIC);
		//		}
		//		else if (fitness == Characteristic.PCCycleDist) {
		//			return this.distPC(VehicleType.CYCLE);
		//		}
		//		
		//		else if (fitness == Characteristic.PCICVisits) {
		//			return this.countVisitsPC(VehicleType.INTERNAL_COMBUSTION);
		//		}
		//		else if (fitness == Characteristic.PCWalkVisits) {
		//			return this.countVisitsPC(VehicleType.PEDESTRIAN);
		//		}
		//		else if (fitness == Characteristic.PCEVVisits) {
		//			return this.countVisitsPC(VehicleType.ELECTRIC);
		//		}
		//		else if (fitness == Characteristic.PCCycleVisits) {
		//			return this.countVisitsPC(VehicleType.CYCLE);
		//		}
		//
		//		else if (fitness == Characteristic.PCICVol) {
		//			return this.countVolPC(VehicleType.INTERNAL_COMBUSTION);
		//		}
		//		else if (fitness == Characteristic.PCWalkVol) {
		//			return this.countVolPC(VehicleType.PEDESTRIAN);
		//		}
		//		else if (fitness == Characteristic.PCEVVol) {
		//			return this.countVolPC(VehicleType.ELECTRIC);
		//		}
		//		else if (fitness == Characteristic.PCCycleVol) {
		//			return this.countVolPC(VehicleType.CYCLE);
		//		}
		//		return this.getDistance();//Default
	} 



	@Override
	public int[] getKey() {
		
		return keygen.getKey(this.characteristics);
	}

	@Override
	public String getSummary() {
	
		return null;
	}

	public HashMap<Characteristic, Double> getCharacteristics() {
		return this.characteristics;
	}


	@Override
	public Emitter getEm() {
		
		return this.emitterTypeUsed;
	}


	@Override
	public EvalType getDecode() {

		return this.evalTypeUsed;
	}
	@Override
	public String getCSV() {
		String csv="";
		for (int g=0; g < genotype.size();g++) {
			Gene gene =genotype.get(g);
			csv = csv + gene.getVisit().getName()+",";
			csv = csv + gene.getNewRoute() +",";
			csv = csv + gene.getPrefered() +",";
		}
		csv=csv+"\n";
		return csv;
	}




}
