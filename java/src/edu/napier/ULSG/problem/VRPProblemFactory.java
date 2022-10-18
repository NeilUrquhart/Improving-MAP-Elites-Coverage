package edu.napier.ULSG.problem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;



/*
 * Nel Urquhart 2019
 * Create VRPPProblem objects from file based instances
 * This Factory assumes that:
 * 
 *  * the problems are in the ORLIB format
 *  * the problems are CVRP instances only
 *  * distancd calculation is EUC2D
 * 
 */

public class VRPProblemFactory {
	public static CVRPProblem buildProblem(String fName){
		try {
			File f = new File(fName);
			BufferedReader b = new BufferedReader(new FileReader(f));

			int capacity =0;
			int size=0;
			int[][] coords = null;
			int[] demand = null;
			int depot = -1;

			String readLine = "";
			while ((readLine = b.readLine()) != null) {
				if (readLine.contains("CAPACITY :")){
					String[] buffer = readLine.split(":");
					capacity = Integer.parseInt(buffer[1].trim());
				}

				if (readLine.contains("COMMENT")){
					System.out.print(fName + "," +readLine + ",");
				}

				if (readLine.contains("DIMENSION :")){
					String[] buffer = readLine.split(":");
					size = Integer.parseInt(buffer[1].trim());
					coords = new int[size][2];
					demand = new int[size];
				}	
				if (readLine.contains("NODE_COORD_SECTION")){
					for (int c=0; c < size; c++){
						String[] buffer = b.readLine().trim().split(" ");
						coords[c][0] = Integer.parseInt(buffer[1].trim());
						coords[c][1] = Integer.parseInt(buffer[2].trim());
					}
				}
				if (readLine.contains("DEMAND_SECTION")){
					for (int c=0; c < size; c++){
						String[] buffer = b.readLine().trim().split(" ");
						demand[c] = Integer.parseInt(buffer[1].trim());
					}
				}
				if (readLine.contains("DEPOT_SECTION")){
					String buffer = b.readLine();
					depot = Integer.parseInt(buffer.trim()) -1;
				}
			}

			//Now build problem
			CVRPProblem result = new CVRPProblem();
			for (int c=0; c <size; c++){
				if (c==depot){
					Visit v = new Visit("Depot",coords[c][0],coords[c][1]);
					result.setStart(v);
				}else{
					VRPVisit v = new VRPVisit(""+(c+1),coords[c][0],coords[c][1],demand[c]);
					result.addVisit(v);
				}
			}
			return result;

		} catch (IOException e) {
			System.out.println("Error reading problem instance file.");
			e.printStackTrace();
		}
		return null;
	}
}
