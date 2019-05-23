package net.rodrigoamaral;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FindMaxHV {

	//CMODE ST10_DT10_E5_SK4-5 1 116 1.439311
	//CMODE ST10_DT10_SE5_DE1_SK4-5 6 47 1.606921
	
	public static void main(String[] args) {
		String file = "hypervolume/metrics-modelo2.csv";
		List<String> hvList = new ArrayList<>();
		try(BufferedReader br = Files.newBufferedReader(Paths.get(file))){
			hvList = br.lines().collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		Comparator<String> c = new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				String hv1 = o1.split(" ")[4];
				String hv2 = o2.split(" ")[4];
				return Double.valueOf(hv2).compareTo(Double.valueOf(hv1));
			}
			
		};
		
		Collections.sort(hvList, c);
		
		System.out.println(hvList.get(0));
		
		double max = 0;
		for(String hv : hvList) {
			double v = Double.valueOf(hv.split(" ")[4]);
			if(v > max) {
				max = v;
			}
		}
		System.out.println(max);
	}
}
