package net.rodrigoamaral;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AdjustJsonInstance {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		List<File> files = Files.walk(Paths.get("project-conf/dynamic-extended"))
								.filter(Files::isRegularFile)
								.map(Path::toFile)
								.collect(Collectors.toList());
		ObjectMapper mapper = new ObjectMapper();
		for(File file: files){
			System.out.println(file.getName());
			
			Map<String, Object> map = mapper.readValue(file, Map.class);
			if(!map.get("Randperm_new").toString().contains("[")){
				List<Object> list = new ArrayList<>(1);
				list.add(map.get("Randperm_new"));
				map.put("Randperm_new", list);
			}
			
			if(!map.get("newemployee_arrivalrate").toString().contains("[")){
				List<Object> list = new ArrayList<>(1);
				list.add(map.get("newemployee_arrivalrate"));
				map.put("newemployee_arrivalrate", list);
			}
			
			if(!map.get("employee_arrival_time").toString().contains("[")){
				List<Object> list = new ArrayList<>(1);
				list.add(map.get("employee_arrival_time"));
				map.put("employee_arrival_time", list);
			}
			
			if(!map.get("employee_maxded_new").toString().contains("[")){
				List<Object> list = new ArrayList<>(1);
				list.add(map.get("employee_maxded_new"));
				map.put("employee_maxded_new", list);
			}
			
			if(!map.get("employee_salary_new").toString().contains("[")){
				List<Object> list = new ArrayList<>(1);
				list.add(map.get("employee_salary_new"));
				map.put("employee_salary_new", list);
			}
			
			if(!map.get("employee_salary_over_new").toString().contains("[")){
				List<Object> list = new ArrayList<>(1);
				list.add(map.get("employee_salary_over_new"));
				map.put("employee_salary_over_new", list);
			}
			
			if(!map.get("employee_skill_number_new").toString().contains("[")){
				List<Object> list = new ArrayList<>(1);
				list.add(map.get("employee_skill_number_new"));
				map.put("employee_skill_number_new", list);
			}
			
			if(!map.get("employee_skill_proficiency_set_new").toString().contains("[[")){
				List<Object> list = new ArrayList<>(1);
				list.add(map.get("employee_skill_proficiency_set_new"));
				map.put("employee_skill_proficiency_set_new", list);
			}
			
			if(!map.get("employee_skill_set_new").toString().contains("[[")){
				List<Object> list = new ArrayList<>(1);
				list.add(map.get("employee_skill_set_new"));
				map.put("employee_skill_set_new", list);
			}
			
			try{
				List<List<Integer>> parallelList = (List<List<Integer>>)map.get("dynamic_parallel_class");
				for(List<Integer> l: parallelList){
					System.out.print(l);
				}
				System.out.println();
			}catch (ClassCastException e){
				System.out.println();
				List<Object> dynamicParallelClass = (List<Object>)map.get("dynamic_parallel_class");
				List<Object> list = new ArrayList<>(dynamicParallelClass.size());
				for(Object o: dynamicParallelClass){
					if(o instanceof List){
						list.add(o);
					}else{
						list.add(Arrays.asList(o));
					}
				}
				System.out.println(list);
				map.put("dynamic_parallel_class", list);
			}
			String text = mapper.writeValueAsString(map);
			Files.write(Paths.get(file.getPath()), text.getBytes());
			
		}
		
	}
}
