package edu.napier.ULSG;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import edu.napier.ULSG.MAP_Elites.Archive;
import edu.napier.ULSG.MAP_Elites.Archive.ArchiveResult;
import edu.napier.ULSG.MAP_Elites.emitters.Emitter;
import edu.napier.ULSG.problem.Individual.EvalType;

public class Logger {
	//Singleton

	/*

   Log using Logger
	 */


	//Log constants
	int[][] timeLine;
	double[] qdTimeLine;

	//TimeLine positions
	int ARCHIVE_SIZE=0;
	int SUCCESFUL_EMIT=1;
	int SUCCESSFUL_XO=2;
	int FAIL_EMIT=3;
	int FAIL_XO=4;
	int SIMP_DECODE=5;
	int ADV_DECODE=6;
	//int QD=7;

	//TimeLine counters
	int successful_emit=0;
	int successful_xo=0;
	int fail_emit=0;
	int fail_xo=0;
	int evals=0;
	int simp_decode=0;
	int adv_decode=0;


	private long[][] increases;
	private long[][] decreases;
	private ArrayList<Emitter> emitters;// Reference to the collection of emitters used
	private Archive archive; //Reference to the archive currently used

	public enum Action{
		replace, init
	}

	//Emitter tracking
	private ArrayList<int[]> emTimeLine;

	//Singleton

	private static Logger instance;
	private static String logfName="";
	private static String emfName="";
	private static String timeFName="";
	private static String emtimeFName="";


	private Logger() {
		buffer.add("evals,origin,p1,p1fit,p2,p2Fit,child,archive action,fitness,decoder");
	}


	public  void setupEmitterRecording(ArrayList<Emitter> ems, int characteristics, Archive a) {
		archive = a;
		//set up emitter recording
		emitters = ems;
		if (Experiment.getInstance().getMultiDecoder()) {
			increases = new long[emitters.size()*2][]; 
			decreases = new long[emitters.size()*2][];
		}else {
			increases = new long[emitters.size()][]; 
			decreases = new long[emitters.size()][];  
		}


		for (int c=0; c < increases.length; c++) {
			increases[c] = new long[characteristics];  
			decreases[c] = new long[characteristics];  
		}

		//Setup log
		timeLine = new int[Experiment.getInstance().getMapEliteEvals()+(ems.size()*2)+1][];
		emTimeLine = new ArrayList<int[]>(); //= new int[Experiment.getInstance().getMapEliteEvals()+(ems.size()*2)+1][];
		qdTimeLine = new double[Experiment.getInstance().getMapEliteEvals()+(ems.size()*2)+1];
		successful_emit=0;
		successful_xo=0;
		fail_emit=0;
		fail_xo=0;
		evals=0;
		simp_decode=0;
		adv_decode=0;
	}


	public static  Logger getInstance() {
		if (instance == null) 
			instance = new Logger();
		return instance;
	}

	//done Singleton

	private ArrayList<String> buffer = new ArrayList<String>();

	public void setFname(String id) {
		logfName = "./"+id+".log.csv";//Ensure unique log name per run
		emfName = "./"+id+".logEM.csv";
		timeFName = "./"+id+".logTime.csv"; 
		emtimeFName = "./"+id+".emTime.csv"; 

	}


	//3 public methods for logging different types of events

	public void log_failedXO(int evals) {
		fail_xo++;
		this.evals=evals;
		updateLog();


	}

	public void log_failedEM(int evals) {
		fail_emit++;
		this.evals=evals;
		updateLog();

	}

	public void log(EvalType et,int evals,   int[] p1, double p1Fit,  int[]p2, double p2Fit, int[] child, ArchiveResult action, double fitness) {
		//XO
		this.log(et,"XO",evals,null,p1,p1Fit,p2,p2Fit,child,action,fitness);
		if (et == EvalType.simple)
			simp_decode++;
		else
			adv_decode++;
		successful_xo++;
	}
	public void log(EvalType et,int evals,  int[] p1, double p1Fit) {
		//Initialisation
		this.log(et,"Init",0, null, p1,p1Fit,null,0,p1,ArchiveResult.first,p1Fit);
	}

	public void log(EvalType et,int evals,  Emitter em, int[] p1, double p1Fit,  int[] child, ArchiveResult action, double fitness) {
		//Creation using an em
		this.log(et,em.getClass().getSimpleName(),evals,em,p1,p1Fit,null,0,child,action,fitness);
		if (et == EvalType.simple)
			simp_decode++;
		else
			adv_decode++;
		successful_emit++;
	}

	//actual log method
	private void log(EvalType et,String desc, int evals, Emitter em,  int[] p1, double p1Fit,  int[]p2, double p2Fit, int[] child, ArchiveResult action, double fitness) {
		this.evals = evals;
		String line = ""+evals;
		line = line + "," + desc;
		line = line +",";
		for (int i : p1) {
			line = line + i + ":";
		}
		line = line +"," +p1Fit +",";
		if (p2!=null)
			for (int i : p2) {
				line = line + i + ":";
			}
		line = line +"," +p2Fit +",";

		for (int i : child) {
			line = line + i + ":";
		}
		line = line +"," + action;
		line = line +"," + fitness;
		line = line +"," + et.toString();
		buffer.add(line);
		if (em!=null)
			updateEmitters(em,et,p1,child);



		updateLog();
	}


	private void updateLog() {
		timeLine[evals]= new int[7];
		timeLine[evals][ARCHIVE_SIZE] = archive.size();
		timeLine[evals][SUCCESFUL_EMIT] = successful_emit;
		timeLine[evals][SUCCESSFUL_XO] = successful_xo;
		timeLine[evals][FAIL_EMIT] = fail_emit;
		timeLine[evals][FAIL_XO]=fail_xo;
		timeLine[evals][SIMP_DECODE] = simp_decode;
		timeLine[evals][ADV_DECODE] = adv_decode;
		if (Experiment.getInstance().calcQD())
			qdTimeLine[evals]=archive.QD();

	}

	private void updateEmitters(Emitter em,EvalType et, int[] in, int[] out) {

		int emIndex = emitters.indexOf(em);

		if (Experiment.getInstance().getMultiDecoder()) {
			if(et == EvalType.advanced)
				emIndex = emIndex *2;

		}
		for (int k=0; k < in.length; k++) {
			if (out[k]<in[k])
				decreases[emIndex][k]++; 
			if (out[k]>in[k])
				increases[emIndex][k]++;  
		}
	}

	public void emitterTracking( Emitter emNew, EvalType etNew, Emitter emReplaced, EvalType etReplaced) {
		if (emNew ==null)
			return;

		int[] entry = new int[(emitters.size()*2)+1];

		if (emTimeLine.size()>0) {
			int[] prev = emTimeLine.get(emTimeLine.size()-1);
			for (int x=0; x < prev.length; x++ ) {
				entry[x] = prev[x];
			}
		}

		entry[0] = evals;

		int emIndex = emitters.indexOf(emNew);
		if (emIndex > -1) {

			//if (Experiment.getInstance().getMultiDecoder()) {
			if(etNew == EvalType.advanced)
				emIndex = emIndex *2;
			//}
			entry[emIndex+1]++;

			if (emReplaced != null) {
				emIndex = emitters.indexOf(emReplaced);
				if (emIndex > -1) {
					//	if (Experiment.getInstance().getMultiDecoder()) {
					if(etReplaced == EvalType.advanced)
						emIndex = emIndex *2;
					//	}
					entry[emIndex+1]--;
				}
			}
		}
//		for(int v : entry)
//		   System.out.print(v + "\t");
//		System.out.println();
		emTimeLine.add(entry);
	}

	public void flush() {
		//Write to disc
		try {

			BufferedWriter writer;
			writer = new BufferedWriter(new FileWriter(logfName,true)); //append

			for (String line : buffer)
				writer.write(line +"\n");

			writer.close();
			buffer.clear();

			//Wite emitters
			//Write out increases and decreases:
			String buffer = "";

			if (Experiment.getInstance().getMultiDecoder()) {
				for (int em = 0; em < emitters.size();em++)
				{
					buffer = buffer + emitters.get(em).name() +"-simple,inc,";
					for (long l : increases[em]) { 
						buffer = buffer + l +",";
					}
					buffer = buffer + "dec,";
					for (long l : decreases [em]) {  
						buffer = buffer + l +",";
					}
					buffer = buffer +"\n";
				}
				for (int em = 0; em < emitters.size();em++)
				{
					buffer = buffer + emitters.get(em).name() +"-adv,inc,";
					for (long l : increases[em+emitters.size()]) { 
						buffer = buffer + l +",";
					}
					buffer = buffer + "dec,";
					for (long l : decreases [em+emitters.size()]) {  
						buffer = buffer + l +",";
					}
					buffer = buffer +"\n";
				}
			}else {
				for (int em = 0; em < emitters.size();em++)
				{
					buffer = buffer + emitters.get(em).name() +",inc,";
					for (long l : increases[em]) { 
						buffer = buffer + l +",";
					}
					buffer = buffer + "dec,";
					for (long l : decreases [em]) {  
						buffer = buffer + l +",";
					}
					buffer = buffer +"\n";
				}
			}
			buffer = buffer + "\n";
			writer = new BufferedWriter(new FileWriter(emfName));
			writer.append(buffer);
			writer.close();


			//Wite timeLine
			//Write out increases and decreases:
			writer = new BufferedWriter(new FileWriter(timeFName));
			writer.append("evals,archive size, succesful emit, successful xo, failed emit, failed xo,simple decoder, advanced decoder,qd\n");
			for (int t = 0; t < timeLine.length; t++){	
				if (timeLine[t]!=null) {
					buffer= t+",";
					for (int v =0; v < timeLine[t].length;v++) {
						buffer = buffer + timeLine[t][v] +",";	
					}
					buffer = buffer + qdTimeLine[t]+",";
					buffer = buffer +"\n";
					writer.append(buffer);
				}
			}

			writer.close();

			//Wite emIndex
			//Write out increases and decreases:
			writer = new BufferedWriter(new FileWriter(emtimeFName));
			String header = "evals,";
			for (Emitter em: emitters) {
				header = header + em.name()+":Simple," ;
			}
			for (Emitter em: emitters) {
				header = header + em.name()+":Complex," ;
			}
			writer.append(header+"\n");
			
			for (int[] entry :  emTimeLine){	
				//buffer= t+",";
				buffer ="";
				for (int v : entry) {
					buffer = buffer + v +",";	
				}
				buffer = buffer +"\n";
				writer.append(buffer);

			}
			emTimeLine = new ArrayList<int[]>();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
