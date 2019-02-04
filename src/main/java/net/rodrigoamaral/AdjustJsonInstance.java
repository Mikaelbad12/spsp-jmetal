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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AdjustJsonInstance {

	public static void main(String[] args) throws IOException {
//		adjustValuesToList();
		removeEventsUnsolvable(Arrays.asList(3, 4, 32, 34, 44, 45, 55, 56, 70, 72, 133, 134, 145, 146), 
								"dynamic_example_new1.json");
	}

	@SuppressWarnings("unchecked")
	protected static void removeEventsUnsolvable(List<Integer> eventsIndexToRemove, String instanceName) 
			throws JsonParseException, JsonMappingException, IOException{
		File instance = new File("project-conf/dynamic-extended/"+instanceName);
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = mapper.readValue(instance, Map.class);
		List<List<Integer>> dynamicParallelEvents = (List<List<Integer>>)map.get("dynamic_parallel_class");
		
		List<List<Integer>> remainingParallelEvents = new ArrayList<List<Integer>>();
		int index = 0;
		for(List<Integer> events: dynamicParallelEvents){
			List<Integer> remainingEvents = new ArrayList<>();
			for(Integer event: events){
				if(!eventsIndexToRemove.contains(index)){
					remainingEvents.add(event);
				}
				index++;
			}
			if(!remainingEvents.isEmpty()){
				remainingParallelEvents.add(remainingEvents);
			}
		}
		map.put("dynamic_parallel_class", remainingParallelEvents);
		
		List<Double> dynamicParallelTime = (List<Double>)map.get("dynamic_parallel_time");
		List<Double> remainingParallelTime = new ArrayList<>();
		for(int i = 0; i < remainingParallelEvents.size(); i++){
			remainingParallelTime.add(dynamicParallelTime.get(i));
		}
		map.put("dynamic_parallel_time", remainingParallelTime);
		
		List<Integer> rushjob = (List<Integer>)map.get("dynamic_rushjob_number");
		List<Integer> newRush = new ArrayList<>();
		for(int i = 0; i < rushjob.size(); i++){
			if(!eventsIndexToRemove.contains(i)){
				newRush.add(rushjob.get(i));
			}
		}
		map.put("dynamic_rushjob_number", newRush);
		
		List<Integer> labourLeave = (List<Integer>)map.get("dynamic_labour_leave_number");
		List<Integer> newLeave = new ArrayList<>();
		for(int i = 0; i < labourLeave.size(); i++){
			if(!eventsIndexToRemove.contains(i)){
				newLeave.add(labourLeave.get(i));
			}
		}
		map.put("dynamic_labour_leave_number", newLeave);

		List<Integer> labourReturn = (List<Integer>)map.get("dynamic_labour_return_number");
		List<Integer> newReturn = new ArrayList<>();
		for(int i = 0; i < labourReturn.size(); i++){
			if(!eventsIndexToRemove.contains(i)){
				newReturn.add(labourReturn.get(i));
			}
		}
		map.put("dynamic_labour_return_number", newReturn);
		
		List<Integer> newLabour = (List<Integer>)map.get("dynamic_new_labour_number");
		List<Integer> newL = new ArrayList<>();
		for(int i = 0; i < newLabour.size(); i++){
			if(!eventsIndexToRemove.contains(i)){
				newL.add(newLabour.get(i));
			}
		}
		map.put("dynamic_new_labour_number", newL);
		
		List<Integer> removeTask = (List<Integer>)map.get("dynamic_remove_task_number");
		List<Integer> newRemove = new ArrayList<>();
		for(int i = 0; i < removeTask.size(); i++){
			if(!eventsIndexToRemove.contains(i)){
				newRemove.add(removeTask.get(i));
			}
		}
		map.put("dynamic_remove_task_number", newRemove);
		
		DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
		prettyPrinter.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
		String text = mapper.writer(prettyPrinter).writeValueAsString(map);
		Files.write(Paths.get(instance.getPath()), text.getBytes());
	}
	
	@SuppressWarnings("unchecked")
	protected static void adjustValuesToList()
			throws IOException, JsonParseException, JsonMappingException, JsonProcessingException {
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
			
			DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
			prettyPrinter.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
			String text = mapper.writer(prettyPrinter).writeValueAsString(map);
			Files.write(Paths.get(file.getPath()), text.getBytes());
		}
	}
}
