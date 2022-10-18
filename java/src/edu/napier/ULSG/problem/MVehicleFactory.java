package edu.napier.ULSG.problem;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import edu.napier.ULSG.vehicles.Vehicle;

public class MVehicleFactory extends VRPProblemFactory {

	public static MVehicleProblem buildProblem(String fName, Vehicle[] vehicles) {
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
					readLine = strip(readLine);
					if (readLine.contains("CAPACITY")){
						String[] buffer = readLine.split(":");
						capacity = Integer.parseInt(buffer[1].trim());
					}

					if (readLine.contains("COMMENT")){
						System.out.print(fName + "," +readLine + ",");
					}

					if (readLine.contains("DIMENSION")){
						String[] buffer = readLine.split(":");
						size = Integer.parseInt(buffer[1].trim());
						coords = new int[size][2];
						demand = new int[size];
					}	
					if (readLine.contains("NODE_COORD_SECTION")){
						for (int c=0; c < size; c++){
							String buf = b.readLine();
							buf = strip(buf);
							String[] buffer = buf.split(" ");
							coords[c][0] = Integer.parseInt(buffer[1].trim());
							coords[c][1] = Integer.parseInt(buffer[2].trim());
						}
					}
					if (readLine.contains("DEMAND_SECTION")){
						for (int c=0; c < size; c++){
							String buf =b.readLine().trim();
							buf = strip(buf);
							String[] buffer = buf.split(" ");
							demand[c] = Integer.parseInt(buffer[1].trim());
						}
					}
					if (readLine.contains("DEPOT_SECTION")){
						String buffer = b.readLine();
						depot = Integer.parseInt(buffer.trim()) -1;
					}
				}

				//Now build problem
				MVehicleProblem result = new MVehicleProblem();
				result.vehicles = vehicles;
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
	
	private static String strip(String in) {
		return in.replaceAll("[\\n\\t]", " ");
	}
	
}
