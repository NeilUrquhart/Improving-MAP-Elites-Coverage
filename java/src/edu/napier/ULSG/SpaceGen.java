package edu.napier.ULSG;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Collector.Characteristics;

import edu.napier.ULSG.problem.Individual.Characteristic;

public class SpaceGen {

	public static void main(String[] args) throws IOException {
	for (Characteristic c : Characteristic.values()){
			System.out.println(c);
		}
		//Print nodes
		int buckets=5;
		//		System.out.println("Id,Fitness,CO2,Time,Fixed Cost,Running Cost");
		//		for (int a=0; a < buckets; a ++)
		//			for (int b=0; b < buckets; b ++)
		//				for (int c=0; c < buckets; c ++)
		//					for (int d=0; d < buckets; d ++)
		//						System.out.println(a+":"+b+":"+c+":"+d +",-1,"+a+","+b+","+c+","+d);
		//
		String plusCO2 ="Source,Target,Description\n";
		String plusTime="Source,Target,Description\n";
		String plusFCost="Source,Target,Description\n";
		String plusRCost="Source,Target,Description\n";
		String lessCO2 ="Source,Target,Description\n";
		String lessTime="Source,Target,Description\n";
		String lessFCost="Source,Target,Description\n";
		String lessRCost="Source,Target,Description\n";
		//Print relations
		for (int a=0; a < buckets; a ++)
			for (int b=0; b < buckets; b ++)
				for (int c=0; c < buckets; c ++)
					for (int d=0; d < buckets; d ++) {

						if(a<buckets-1)
							plusCO2 = plusCO2 + (a+":"+b+":"+c+":"+d+","+(a+1)+":"+b+":"+c+":"+d+",\"+CO2\"\n");
						if(b<buckets-1)
							plusTime = plusTime+(a+":"+b+":"+c+":"+d+","+a+":"+(b+1)+":"+c+":"+d+",\"+Time\"\n");
						if(c<buckets-1)
							plusFCost = plusFCost+(a+":"+b+":"+c+":"+d+","+a+":"+b+":"+(c+1)+":"+d+",\"+Fixed Cost\"\n");
						if(d<buckets-1)
							plusRCost = plusRCost+(a+":"+b+":"+c+":"+d+","+a+":"+b+":"+c+":"+(d+1)+",\"+Running Cost\"\n");
						if(a>0)
							lessCO2 = lessCO2+(a+":"+b+":"+c+":"+d+","+(a-1)+":"+b+":"+c+":"+d+",\"-CO2\"\n");
						if(b>0)
							lessTime=lessTime+(a+":"+b+":"+c+":"+d+","+a+":"+(b-1)+":"+c+":"+d+",\"-Time\"\n");
						if(c>0)
							lessFCost = lessFCost +(a+":"+b+":"+c+":"+d+","+a+":"+b+":"+(c-1)+":"+d+",\"-Fixed Cost\"\n");
						if(d>0)
							lessRCost = lessRCost +(a+":"+b+":"+c+":"+d+","+a+":"+b+":"+c+":"+(d-1)+",\"-Running Cost\"\n");
					}

		BufferedWriter writer = new BufferedWriter(new FileWriter("plusCO2.csv"));
		writer.write(plusCO2);
		writer.close();

		writer = new BufferedWriter(new FileWriter("plusTime.csv"));
		writer.write(plusTime);
		writer.close();

		writer = new BufferedWriter(new FileWriter("plusRCost.csv"));
		writer.write(plusRCost);
		writer.close();

		writer = new BufferedWriter(new FileWriter("plusFCost.csv"));
		writer.write(plusFCost);
		writer.close();

		writer = new BufferedWriter(new FileWriter("lessCO2.csv"));
		writer.write(lessCO2);
		writer.close();

		writer = new BufferedWriter(new FileWriter("lessRCost.csv"));
		writer.write(lessRCost);
		writer.close();

		writer = new BufferedWriter(new FileWriter("lessFCost2.csv"));
		writer.write(lessFCost);
		writer.close();

		writer = new BufferedWriter(new FileWriter("lessTime.csv"));
		writer.write(lessTime);
		writer.close();
	}

}
