package net.rodrigoamaral.jmetal.qualityindicator.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Stability{

	private String file;
	
	public static void main(String[] args) {
		new Stability("hypervolume/accuracy.csv").evaluate();
	}
	
	public Stability(String accFilePath) {
		file = accFilePath;
	}
	
	public void evaluate() {
		List<String> accList = new ArrayList<>();
		try(BufferedReader br = Files.newBufferedReader(Paths.get(file))){
			accList = br.lines().collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		Map<String, Map<String, Map<String, List<String>>>> algorithms = new HashMap<>();
		for(String s: accList) {
			String[] info = s.split(" ");
			if(algorithms.containsKey(info[0])) {
				if(algorithms.get(info[0]).containsKey(info[1])) {
					if(algorithms.get(info[0]).get(info[1]).containsKey(info[2])) {
						algorithms.get(info[0]).get(info[1]).get(info[2]).add(s);
					}else {
						List<String> acc = new ArrayList<>();
						acc.add(s);
						algorithms.get(info[0]).get(info[1]).put(info[2], acc);
					}
				}else {
					Map<String, List<String>> exec = new HashMap<>();
					List<String> acc = new ArrayList<>();
					acc.add(s);
					exec.put(info[2], acc);
					algorithms.get(info[0]).put(info[1], exec);
				}
			}else {
				Map<String, Map<String, List<String>>> instance = new HashMap<>();
				Map<String, List<String>> exec = new HashMap<>();
				List<String> acc = new ArrayList<>();
				acc.add(s);
				exec.put(info[2], acc);
				instance.put(info[1], exec);
				algorithms.put(info[0], instance);
			}
		}
		
		Comparator<String> c = new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				String event1 = o1.split(" ")[3];
				String event2 = o2.split(" ")[3];
				return Integer.valueOf(event1).compareTo(Integer.valueOf(event2));
			}
			
		};
		
		List<String> stabList = new ArrayList<>();
		for(Map<String, Map<String, List<String>>> alg: algorithms.values()) {
			for(Map<String, List<String>> instance: alg.values()) {
				for(List<String> exec: instance.values()) {
					Collections.sort(exec, c);
					for(int i = 1; i < exec.size(); i++) {
						String[] currentInfo = exec.get(i).split(" ");
						String[] previousInfo = exec.get(i-1).split(" ");
						Double currentAcc = Double.valueOf(currentInfo[4]);
						Double previousAcc =  Double.valueOf(previousInfo[4]);
						Double stab = evaluate(previousAcc, currentAcc);
						String stability = stab.toString();
						if(stability.length() > 8) {
							stability = stability.substring(0, 8);
						}
						stabList.add(String.format("%s %s %s %s %s", currentInfo[0], currentInfo[1], currentInfo[2], currentInfo[3], stability));
					}
				}
			}
		}
		
		try(PrintWriter pw = new PrintWriter(Files.newBufferedWriter(Paths.get("hypervolume/stability.csv")))){
			stabList.forEach(pw::println);
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Double evaluate(Double previousAccuracy, Double currentAccuracy) {
		return Math.max(0, previousAccuracy - currentAccuracy);
	}

	public boolean isTheLowerTheIndicatorValueTheBetter() {
		return true;
	}
}
