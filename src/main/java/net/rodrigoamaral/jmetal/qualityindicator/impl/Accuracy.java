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

public class Accuracy{

	private String file;
	
	public static void main(String[] args) {
		new Accuracy("hypervolume/metrics.csv").evaluate();
	}
	
	public Accuracy(String hvFilePath) {
		file = hvFilePath;
	}
	
	public void evaluate() {
		List<String> hvList = new ArrayList<>();
		try(BufferedReader br = Files.newBufferedReader(Paths.get(file))){
			hvList = br.lines().collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		Map<String, Map<String, Map<String, List<String>>>> algorithms = new HashMap<>();
		for(String s: hvList) {
			String[] info = s.split(" ");
			if(algorithms.containsKey(info[0])) {
				if(algorithms.get(info[0]).containsKey(info[1])) {
					if(algorithms.get(info[0]).get(info[1]).containsKey(info[2])) {
						algorithms.get(info[0]).get(info[1]).get(info[2]).add(s);
					}else {
						List<String> hv = new ArrayList<>();
						hv.add(s);
						algorithms.get(info[0]).get(info[1]).put(info[2], hv);
					}
				}else {
					Map<String, List<String>> exec = new HashMap<>();
					List<String> hv = new ArrayList<>();
					hv.add(s);
					exec.put(info[2], hv);
					algorithms.get(info[0]).put(info[1], exec);
				}
			}else {
				Map<String, Map<String, List<String>>> instance = new HashMap<>();
				Map<String, List<String>> exec = new HashMap<>();
				List<String> hv = new ArrayList<>();
				hv.add(s);
				exec.put(info[2], hv);
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
		
		//TODO tirar duvida com Andre se eh em relacao a todas as execucoes ou nao
		List<String> accList = new ArrayList<>();
		for(Map<String, Map<String, List<String>>> alg: algorithms.values()) {
			for(Map<String, List<String>> instance: alg.values()) {
				for(List<String> exec: instance.values()) {
					double maxHv = Double.MIN_VALUE;
					Collections.sort(exec, c);
					for(String infoHv: exec) {
						String[] info = infoHv.split(" ");
						double hv = Double.valueOf(info[4]);
						if(hv > maxHv) {
							maxHv = hv;
						}
						Double acc = evaluate(hv, maxHv);
						String accuracy = acc.toString();
						if(accuracy.length() > 8) {
							accuracy = accuracy.substring(0, 8);
						}
						accList.add(String.format("%s %s %s %s %s", info[0], info[1], info[2], info[3], accuracy));
					}
				}
			}
		}
		
		try(PrintWriter pw = new PrintWriter(Files.newBufferedWriter(Paths.get("hypervolume/accuracy.csv")))){
			accList.forEach(pw::println);
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Double evaluate(Double hv, Double maxHv) {
		return hv/maxHv;
	}

	public boolean isTheLowerTheIndicatorValueTheBetter() {
		return false;
	}
}
