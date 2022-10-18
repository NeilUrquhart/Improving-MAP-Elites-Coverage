package edu.napier.ULSG;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.OptionalDouble;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import edu.napier.ULSG.problem.MVehicleFactory;
import edu.napier.ULSG.problem.MVehicleProblem;
import edu.napier.ULSG.problem.VRPVisit;
import edu.napier.ULSG.problem.Visit;
import edu.napier.ULSG.vehicles.Vehicle;
import edu.napier.ULSG.vehicles.VehicleFactory;

public class ProblemStats {

	public static void main(String[] args) {
		ArrayList<String> out = new ArrayList<String>();
		String[] probs =  new String[]{"A-n32-k5","A-n45-k6","A-n34-k5","A-n36-k5","A-n37-k5","A-n44-k6",
				"A-n45-k7","A-n46-k7","A-n48-k7","A-n53-k7",
				"A-n38-k5","A-n39-k5","A-n39-k6","A-n54-k7","A-n55-k9",
				"A-n60-k9","A-n61-k9","A-n62-k8","A-n63-k9","A-n63-k10","A-n64-k9",
				"A-n65-k9","A-n69-k9","A-n80-k10","A-n33-k5","A-n33-k6","A-n37-k6"};
		
		String features = "CO2_TTime_FC_RC";

		for (String p : probs)
			out.add(getStats("./data/", p,features, new String[] {"CityBike","CityVan"}));

		//Header
		
		System.out.print("\nProblem,Size,DemandTot,DemandAvg,nnDist,totDist,AbgDist,v1cap,v1co2,v1fcost,v1rcost,v1speed,v2cap,v2co2,v2fcost,v2rcost,v2speed,");
		System.out.println(getRanges(probs[0],features,true));
		out.forEach((line) -> System.out.println(line));

	}

	private static String getStats(String dataDir,String prob,String features, String[] fleet) {
		String res="";
		MVehicleProblem myVRP = MVehicleFactory.buildProblem(dataDir+prob+".vrp", VehicleFactory.createFleet(fleet));//Load instance from file

		ArrayList<Double> distList = new ArrayList<Double>();

		myVRP.getVisits().forEach((x) ->{
			myVRP.getVisits().forEach((y) ->{
				distList.add(myVRP.getDistance((VRPVisit)x,(VRPVisit)y));
			});
		});

		res=res+(prob +","
				+myVRP.getSize() +","
				+myVRP.getDemand() +","
				+(myVRP.getDemand()/myVRP.getSize())+","
				+nnDist(myVRP) +","
				+(distList.stream().mapToDouble(f -> f.doubleValue()).sum())+"," 
				+(distList.stream().mapToDouble(f -> f.doubleValue()).average()).getAsDouble()+","
				);

		for(Vehicle v: myVRP.vehicles) {
			res= res +(v.getCapacity()+","+
					v.getCo2()+","+
					v.getfCost() +","+
					v.getRcost() +","+
					v.getSpeed() +","
					);
		}
		//Add ranges
		res = res +getRanges(prob,features,false );
		return res;


	}

	private static String getRanges(String prob, String features,boolean header) {
		String res="";
		try  
		{  
			
			File file=new File("./ranges/"+prob+"-"+features+"-initRange.csv");    //creates a new file instance  
			FileReader fr=new FileReader(file);   //reads the file  
			BufferedReader br=new BufferedReader(fr);  //creates a buffering character input stream  
			String line;  
			br.readLine();//header
			while((line=br.readLine())!=null)  
			{  
				String[] data = line.split(",");
				if(header)
					res = res + "min-"+data[0] +",max-"+data[0]+",";
				else
					res = res + data[1] +","+data[2]+",";
			}  
			fr.close();    //closes the stream and release the resources  
		}  
		catch(IOException e)  
		{  
			e.printStackTrace();  
		}  
		return res;
	}

	private static Double nnDist(MVehicleProblem prob) {
		double dist=0;
		Visit current  = prob.getStart();		
		ArrayList<Visit> remaining = prob.getVisits();

		while(remaining.size()>0) {
			double bst = Double.MAX_VALUE;
			Visit closest = null;
			for (Visit v : remaining) {
				double t = prob.getDistance(current, v);
				if (t < bst) {
					closest = v;
					bst = t;
				}
			}		
			remaining.remove(closest);
			dist = dist + bst; 
		}
		return dist;
	}

}
