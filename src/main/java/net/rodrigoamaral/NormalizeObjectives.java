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
import java.util.List;
import java.util.stream.Collectors;

public class NormalizeObjectives {

	public static void main(String[] args) throws IOException {
		normalize();
	}
	
	private static void normalize() throws IOException{
		List<File> files = Files.walk(Paths.get("results"))
							.filter(Files::isRegularFile)
							.map(Path::toFile)
							.filter(file -> file.getName().startsWith("OBJ"))
							.collect(Collectors.toList());
		
		double[] maxValue = new double[]{Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, 
									Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY};
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
					}else if(objValue > maxValue[i]){
						maxValue[i] = objValue;
					}
				}
			}
		}
		
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
