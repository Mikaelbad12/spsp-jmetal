package net.rodrigoamaral;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

public class NormalizeObjectives {

	public static void main(String[] args) throws IOException {
		removeAllNOBJfolder();
		removedLastArchiveIfAllValuesZero();
		normalize("sT10_dT10_sE5_dE1_SK4-5", Arrays.asList("CMODESDEDynamic", "CMODESDERepairDynamic", "CMODESDEExternalDynamic", 
												"CMODESDEExternalReDynamic", "CMODESDEFullDynamic", "CMODESDEFullReDynamic", "NSGAIIIDynamic"));
		normalize("sT10_dT10_sE10_dE1_SK4-5", Arrays.asList("CMODESDEDynamic", "CMODESDERepairDynamic", "CMODESDEExternalDynamic", 
												"CMODESDEExternalReDynamic", "CMODESDEFullDynamic", "CMODESDEFullReDynamic", "NSGAIIIDynamic"));
		normalize("sT10_dT10_sE15_dE1_SK4-5", Arrays.asList("CMODESDEDynamic", "CMODESDERepairDynamic", "CMODESDEExternalDynamic", 
												"CMODESDEExternalReDynamic", "CMODESDEFullDynamic", "CMODESDEFullReDynamic", "NSGAIIIDynamic"));
		normalize("sT10_dT10_sE5_dE1_SK6-7", Arrays.asList("CMODESDEDynamic", "CMODESDERepairDynamic", "CMODESDEExternalDynamic", 
												"CMODESDEExternalReDynamic", "CMODESDEFullDynamic", "CMODESDEFullReDynamic", "NSGAIIIDynamic"));
		normalize("sT10_dT10_sE10_dE1_SK6-7", Arrays.asList("CMODESDEDynamic", "CMODESDERepairDynamic", "CMODESDEExternalDynamic", 
												"CMODESDEExternalReDynamic", "CMODESDEFullDynamic", "CMODESDEFullReDynamic", "NSGAIIIDynamic"));
		normalize("sT10_dT10_sE15_dE1_SK6-7", Arrays.asList("CMODESDEDynamic", "CMODESDERepairDynamic", "CMODESDEExternalDynamic", 
												"CMODESDEExternalReDynamic", "CMODESDEFullDynamic", "CMODESDEFullReDynamic", "NSGAIIIDynamic"));
		normalize("sT20_dT10_sE5_dE1_SK4-5", Arrays.asList("CMODESDEDynamic", "CMODESDERepairDynamic", "CMODESDEExternalDynamic", 
												"CMODESDEExternalReDynamic", "CMODESDEFullDynamic", "CMODESDEFullReDynamic", "NSGAIIIDynamic"));
		normalize("sT20_dT10_sE10_dE1_SK4-5", Arrays.asList("CMODESDEDynamic", "CMODESDERepairDynamic", "CMODESDEExternalDynamic", 
												"CMODESDEExternalReDynamic", "CMODESDEFullDynamic", "CMODESDEFullReDynamic", "NSGAIIIDynamic"));
		normalize("sT20_dT10_sE15_dE1_SK4-5", Arrays.asList("CMODESDEDynamic", "CMODESDERepairDynamic", "CMODESDEExternalDynamic", 
												"CMODESDEExternalReDynamic", "CMODESDEFullDynamic", "CMODESDEFullReDynamic", "NSGAIIIDynamic"));
		normalize("sT20_dT10_sE5_dE1_SK6-7", Arrays.asList("CMODESDEDynamic", "CMODESDERepairDynamic", "CMODESDEExternalDynamic", 
												"CMODESDEExternalReDynamic", "CMODESDEFullDynamic", "CMODESDEFullReDynamic", "NSGAIIIDynamic"));
		normalize("sT20_dT10_sE10_dE1_SK6-7", Arrays.asList("CMODESDEDynamic", "CMODESDERepairDynamic", "CMODESDEExternalDynamic", 
												"CMODESDEExternalReDynamic", "CMODESDEFullDynamic", "CMODESDEFullReDynamic", "NSGAIIIDynamic"));
		normalize("sT20_dT10_sE15_dE1_SK6-7", Arrays.asList("CMODESDEDynamic", "CMODESDERepairDynamic", "CMODESDEExternalDynamic", 
												"CMODESDEExternalReDynamic", "CMODESDEFullDynamic", "CMODESDEFullReDynamic", "NSGAIIIDynamic"));
		normalize("sT30_dT10_sE5_dE1_SK4-5", Arrays.asList("CMODESDEDynamic", "CMODESDERepairDynamic", "CMODESDEExternalDynamic", 
												"CMODESDEExternalReDynamic", "CMODESDEFullDynamic", "CMODESDEFullReDynamic", "NSGAIIIDynamic"));
		normalize("sT30_dT10_sE10_dE1_SK4-5", Arrays.asList("CMODESDEDynamic", "CMODESDERepairDynamic", "CMODESDEExternalDynamic", 
												"CMODESDEExternalReDynamic", "CMODESDEFullDynamic", "CMODESDEFullReDynamic", "NSGAIIIDynamic"));
		normalize("sT30_dT10_sE15_dE1_SK4-5", Arrays.asList("CMODESDEDynamic", "CMODESDERepairDynamic", "CMODESDEExternalDynamic", 
												"CMODESDEExternalReDynamic", "CMODESDEFullDynamic", "CMODESDEFullReDynamic", "NSGAIIIDynamic"));
		normalize("sT30_dT10_sE5_dE1_SK6-7", Arrays.asList("CMODESDEDynamic", "CMODESDERepairDynamic", "CMODESDEExternalDynamic", 
												"CMODESDEExternalReDynamic", "CMODESDEFullDynamic", "CMODESDEFullReDynamic", "NSGAIIIDynamic"));
		normalize("sT30_dT10_sE10_dE1_SK6-7", Arrays.asList("CMODESDEDynamic", "CMODESDERepairDynamic", "CMODESDEExternalDynamic", 
												"CMODESDEExternalReDynamic", "CMODESDEFullDynamic", "CMODESDEFullReDynamic", "NSGAIIIDynamic"));
		normalize("sT30_dT10_sE15_dE1_SK6-7", Arrays.asList("CMODESDEDynamic", "CMODESDERepairDynamic", "CMODESDEExternalDynamic", 
												"CMODESDEExternalReDynamic", "CMODESDEFullDynamic", "CMODESDEFullReDynamic", "NSGAIIIDynamic"));
	}
	
	private static void removeAllNOBJfolder() throws IOException {
		Files.walk(Paths.get("results"))
			 .filter(Files::isDirectory)
			 .map(Path::toFile)
			 .filter(file -> file.getName().equals("NOBJ"))
			 .forEach(t -> {
				try {
					FileUtils.deleteDirectory(t);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
	}

	private static void removedLastArchiveIfAllValuesZero() throws IOException{
		List<File> files = Files.walk(Paths.get("results"))
				.filter(Files::isRegularFile)
				.map(Path::toFile)
				.filter(file -> file.getName().startsWith("OBJ"))
				.collect(Collectors.toList());
		
		Map<String, List<File>> map = new HashMap<>();
		for(File f: files){
			String key = f.getName().substring(0, f.getName().lastIndexOf("-"));
			if(map.containsKey(key)){
				map.get(key).add(f);
			}else{
				List<File> aux = new ArrayList<>();
				aux.add(f);
				map.put(key, aux);
			}
		}
		
		Comparator<File> c = new Comparator<File>() {

			@Override
			public int compare(File o1, File o2) {
				return Integer.valueOf(o2.getName().substring(
							o2.getName().lastIndexOf("-")+1, 
							o2.getName().lastIndexOf(".")))
						.compareTo(Integer.valueOf(o1.getName().substring(
									o1.getName().lastIndexOf("-")+1, 
									o1.getName().lastIndexOf("."))));
			}
			
		};
		
		List<File> toRemove = new ArrayList<>();
		for(String key: map.keySet()){
			List<File> list = map.get(key);
			Collections.sort(list, c);
			for(int i = 0; i < list.size(); i++){
				BufferedReader br = new BufferedReader(new FileReader(list.get(i)));
				List<String> lines = br.lines().collect(Collectors.toList());
				br.close();
				boolean onlyZeros = true;
				inside:
				for(String line: lines){
					String[] objs = line.split(" ");
					for(String o: objs){
						if(!o.equals("0.0") && !o.equals("-0.0")){
							onlyZeros = false;
							break inside;
						}
					}
				}
				if(!onlyZeros){
					break;
				}else{
					toRemove.add(list.get(i));
				}
			}
		}
		
		toRemove.forEach(f -> f.delete());
	}
	
	private static List<File> getOBJFiles(String filterInstance, List<String> filterAlgorithms) throws IOException{
		Predicate<File> instancePredicate = new Predicate<File>() {

			@Override
			public boolean test(File file) {
				if(filterInstance == null || filterInstance.trim().isEmpty()) {
					return true;
				}
				return file.getName().contains(filterInstance.toUpperCase());
			}
		};
		
		Predicate<File> algorithmPredicate = new Predicate<File>() {

			@Override
			public boolean test(File file) {
				if(filterAlgorithms == null || filterAlgorithms.isEmpty()) {
					return true;
				}

				for(String algorithm: filterAlgorithms) {
					if(file.getName().contains(algorithm.toUpperCase()+"-")) {
						return true;
					}
				}
				return false;
			}
		};
		
		return Files.walk(Paths.get("results"))
					.filter(Files::isRegularFile)
					.map(Path::toFile)
					.filter(file -> file.getName().startsWith("OBJ"))
					.filter(instancePredicate)
					.filter(algorithmPredicate)
					.collect(Collectors.toList());
	}
	
	private static double[] findMaxValues(String filterInstance, List<String> filterAlgorithms) throws IOException {
		List<File> files = getOBJFiles(filterInstance, filterAlgorithms);
		
		double[] maxValue = new double[]{Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, 
										Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY};
		
		for(File file: files){
			BufferedReader br = new BufferedReader(new FileReader(file));
			List<String> lines = br.lines().collect(Collectors.toList());
			br.close();
			for(String line: lines){
				String[] objs = line.split(" ");
				for(int i = 0; i < objs.length; i++){
					double objValue = Double.valueOf(objs[i]);
					if(objValue > maxValue[i]){
						maxValue[i] = objValue;
					}
				}
			}
		}
		return maxValue;
	}
	
	private static double[] findMinValues(String filterInstance, List<String> filterAlgorithms) throws IOException {
		List<File> files = getOBJFiles(filterInstance, filterAlgorithms);
		
		double[] minValue = new double[]{Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, 
										Double.MAX_VALUE, Double.MAX_VALUE};
		
		for(File file: files){
			BufferedReader br = new BufferedReader(new FileReader(file));
			List<String> lines = br.lines().collect(Collectors.toList());
			br.close();
			for(String line: lines){
				String[] objs = line.split(" ");
				for(int i = 0; i < objs.length; i++){
					double objValue = Double.valueOf(objs[i]);
					if (objValue < minValue[i]) {
						minValue[i] = objValue;
					}
				}
			}
		}
		return minValue;
	}
	
	private static void normalize(String filterInstance, List<String> filterAlgorithms) throws IOException{
		double[] maxValue = findMaxValues(filterInstance, filterAlgorithms);
		double[] minValue = findMinValues(filterInstance, filterAlgorithms);
		
		List<File> files = getOBJFiles(filterInstance, filterAlgorithms);
		
		for(File file: files){
			BufferedReader br = new BufferedReader(new FileReader(file));
			List<String> lines = br.lines().collect(Collectors.toList());
			br.close();
			
			List<String> nobjList = new ArrayList<>();
			for(String line: lines){
				String[] objs = line.split(" ");
				StringBuilder nobj = new StringBuilder();
				for(int i = 0; i < objs.length; i++){
					double objValue = Double.valueOf(objs[i]);
					nobj.append((objValue - minValue[i]) / (maxValue[i] - minValue[i]));
					nobj.append(" ");
				}
				nobjList.add(nobj.toString().trim());
			}
			
			String nobjPath = file.getPath().replace("OBJ", "NOBJ");
			createParentDirectory(nobjPath);
			PrintWriter pw = new PrintWriter(Files.newBufferedWriter(Paths.get(nobjPath)));
			nobjList.forEach(pw::println);
			pw.close();
		}
		
		print(maxValue);
		print(minValue);
		
	}
	
	private static void createParentDirectory(String path) {
		File f = new File(path).getParentFile();
		if(!f.exists()){
			f.mkdirs();	
		}
	}

	private static void print(double[] obj){
		for(int i = 0; i < obj.length; i++){
			System.out.println(obj[i]);
		}
		System.out.println("--------------------");
	}
}
