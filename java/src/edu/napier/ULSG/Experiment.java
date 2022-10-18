package edu.napier.ULSG;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
//Singleton data structue for experiment
//Holds details of the experiment being run
public class Experiment{

	//Singleton
	private static Experiment instance;
	private Experiment() {}

	public static Experiment getInstance() {
		if (instance == null)
			instance = new Experiment();

		return instance;
	}

	public static Experiment getInstance(String id) {
		instance = new Experiment();
		instance.setup(id);

		return instance;
	}
	//Done Singleton
	private boolean useBandits=false;
	private String[] fleet;
	private String[] geoFiles;
	private String problemDefinition;
	private String id;
	private int run;//Current run
	private int initEvals;
	private int mapEliteEvals;
	private String dataDir;
	private String currentGeo;//currentGeo
	private boolean useemitters = false;
	private boolean advDecoder = false;
	private boolean multiDecoder = false;
	private boolean usedBanditDecayGreedyE = false;
	private boolean usedBanditGreedyE = false;
	private boolean splitDecode =false;
	private boolean useBanditUCB = false;
	private boolean useExtraEmitters=false;
	private boolean calcQD = false;
	
	private void setup(String  id) {
		//this.id = id;

		StringBuilder sb = new StringBuilder();

		try (BufferedReader br = Files.newBufferedReader(Paths.get(id+".cfg"))) {

			// read line by line
			String line;
			while ((line = br.readLine()) != null) {
				String[] split = line.split("=");
				if (split[0].equals("fleet")) {
					fleet = split[1].split(",");
					for (int x=0; x < fleet.length;x++)
						fleet[x] = fleet[x].trim();
				}
				if (split[0].equals("problem")) {
					problemDefinition = split[1].trim();

				}
				if (split[0].equals("calcqd")) {
					String t = split[1].trim();
					if (t.contains("true"))
						calcQD=true;

				}
				if (split[0].equals("useemitters")) {
					String t = split[1].trim();
					if (t.contains("true"))
						useemitters=true;

				}
				if (split[0].equals("xtraEmitters")) {
					String t = split[1].trim();
					if (t.contains("true"))
						useExtraEmitters=true;

				}
				
				
				if (split[0].equals("splitDecode")) {
					String t = split[1].trim();
					if (t.contains("true"))
						splitDecode=true;

				}
				if (split[0].equals("usebanditGreedyE")) {
					String t = split[1].trim();
					if (t.contains("true")) {
						useBandits=true;
						usedBanditGreedyE = true;
					}

				}
				
				if (split[0].equals("usebanditDecayGreedyE")) {
					String t = split[1].trim();
					if (t.contains("true")) {
						useBandits=true;
						this.usedBanditDecayGreedyE = true;
					}

				}
				

				if (split[0].equals("usebanditUCB")) {
					String t = split[1].trim();
					if (t.contains("true")) {
						useBandits=true;
						useBanditUCB =true;
					}

				}
				
				if (split[0].equals("multiDecoder")) {
					String t = split[1].trim();
					if (t.contains("true"))
						multiDecoder=true;

				}
				if (split[0].equals("useAdvDecoder")) {
					String t = split[1].trim();
					if (t.contains("true"))
						advDecoder=true;
				}
				if (split[0].equals("geofiles")) {
					geoFiles = split[1].split(",");
					for (int x=0; x < geoFiles.length;x++)
						geoFiles[x] = geoFiles[x].trim();
				}
				
				if (split[0].equals("initEvals"))
					initEvals = Integer.parseInt(split[1]);
				
				if (split[0].equals("mapEliteEvals"))
					mapEliteEvals = Integer.parseInt(split[1]);
				
				if (split[0].equals("dataDir"))
					dataDir = split[1];
			}

		} catch (IOException e) {
			System.err.format("IOException: %s%n", e);
		}
	}
	
	public boolean getSplitDecode() {
		return splitDecode;
	}

	public boolean calcQD() {
		return calcQD;
	}
	
	public boolean useExtraEmitters() {
		return this.useExtraEmitters;
	}

	public boolean useBanditGreedyE() {
		return usedBanditGreedyE;
	}
	
	public boolean useBanditDecayGreedyE() {
		return this.usedBanditDecayGreedyE;
	}


	public boolean useBanditUCB() {
		return useBanditUCB;
	}

	public boolean getUseBandits() {
		return this.useBandits;
	}
	public String getCurrentGeo() {
		return currentGeo;
	}

	public void setCurrentGeo(String currentGeo) {
		this.currentGeo = currentGeo;
	}
	
	public boolean getAdvDecoder() {
		return this.advDecoder;
	}

	public boolean getMultiDecoder() {
		return this.multiDecoder;
	}

	public String getDataDir() {
		return dataDir;
	}
	public void setRun(int r) {
		run = r;
	}

	public int getRun() {
		return run;
	}

	public String[] getFleet() {
		return fleet;
	}

	public String[] getGeoFiles() {
		return geoFiles;
	}

	public String getProblemDefinition() {
		return problemDefinition;
	}

	public String getId() {
		return currentGeo +" "+problemDefinition;
	}

	public int getInitEvals() {
		return initEvals;
	}

	public int getMapEliteEvals() {
		return mapEliteEvals;
	}
	
	public boolean getUseEmitters() {
		return useemitters;
	}
}
