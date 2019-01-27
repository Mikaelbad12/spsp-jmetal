package net.rodrigoamaral.dspsp.project;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.solution.DoubleSolution;

import net.rodrigoamaral.dspsp.exceptions.InvalidSolutionException;
import net.rodrigoamaral.dspsp.project.events.DynamicEvent;
import net.rodrigoamaral.dspsp.project.events.EventType;
import net.rodrigoamaral.dspsp.project.tasks.DynamicTask;
import net.rodrigoamaral.dspsp.project.tasks.TaskManager;
import net.rodrigoamaral.dspsp.solution.DedicationMatrix;
import net.rodrigoamaral.logging.SPSPLogger;


public class DynamicExtendedProject extends DynamicProject {

    private List<List<DynamicEvent>> parallelEvents;
    private List<Double> employeeArrivalTimes;

    public DynamicExtendedProject() {
        employeeArrivalTimes = new ArrayList<>();
    }

    public List<Double> getEmployeeArrivalTimes() {
        return employeeArrivalTimes;
    }
    
    /**
     * Updates project state based on the dynamic events and current schedule
     *
     * @param lastSchedule
     * @param events
     */
    public void update(List<DynamicEvent> events, DoubleSolution lastSchedule) {
        // REFACTOR: lastSchedule should be a DedicationMatrix to avoid dependencies with jMetal
        setPreviousSchedule(lastSchedule);
        setLastAvailableEmployees(getAvailableEmployees());
        updateFinishedEffort(getAvailableEmployees(), events.iterator().next().getTime());
        updateCurrentStatus(events);
        setLastSchedulingTime(events.iterator().next().getTime());
    }
    
    public void updateCurrentStatus(List<DynamicEvent> events) {
        updateEmployeeAvailability(events);
        updateTaskAvailability(events);
        updateCurrentStatus();
    }

    protected void updateEmployeeAvailability(List<DynamicEvent> events) {
    	for(DynamicEvent event: events){
	        int id = event.getSubject().getId();
	        if (event.getType() == EventType.EMPLOYEE_LEAVE) {
	            getEmployeeById(id).setAvailable(false);
	        } else if (event.getType() == EventType.EMPLOYEE_RETURN) {
	            getEmployeeById(id).setAvailable(true);
	        } else if (event.getType() == EventType.NEW_EMPLOYEE_ARRIVE) {
	        	getEmployeeById(id).setAvailable(true);
	        }
    	}
    }

    protected void updateTaskAvailability(List<DynamicEvent> events) {
        List<Integer> incomingTasksIDs = getIncomingTasks(events.iterator().next());
        makeTasksAvailable(incomingTasksIDs, events);
    }
    
    protected void makeTasksAvailable(List<Integer> incomingTaskIDs, List<DynamicEvent> events) {
        List<Integer> urgentTasksIndex = new ArrayList<>();
        for(DynamicEvent event: events){
	        if (event.getType() == EventType.NEW_URGENT_TASK) {
	            if (!incomingTaskIDs.isEmpty()) {
	                urgentTasksIndex.add(incomingTaskIDs.remove(incomingTaskIDs.size() - 1));
	            }
	        }else if (event.getType() == EventType.REMOVE_TASK){
	        	DynamicTask removedTask = getTaskById(event.getSubject().getId());
	        	removedTask.setRemoved(true);
	        	removedTask.setAvailable(false);
	        	getTaskPrecedenceGraph().remove(removedTask.index());
	    		////
	    		SPSPLogger.info("Task removal T_" + removedTask.index());
	    		////
	        	removeSuccessors(removedTask.index());
	        }
        }
        
        for (int t : incomingTaskIDs) {
            DynamicTask newTask = getTaskByIndex(t);
            if(!newTask.isRemoved()){
	            boolean available = TaskManager.isAvailable(newTask, getAvailableEmployees(), this, getTaskPrecedenceGraph());
	            newTask.setAvailable(available);

	            List<Integer> predecessors = chooseRandomTasks();
	            for (int p : predecessors) {
	            	DynamicTask pred = getTaskByIndex(p);
	            	if(!pred.isRemoved()){
		                getTaskPrecedenceGraph().addEdge(p, t);
		                ////
		                SPSPLogger.info("Regular T_" + t + " added after T_" + p + " (T_"+p+" -> T_" + t +")");
		                ////
	            	}
	            }
            }
        }
        
        if (!urgentTasksIndex.isEmpty()) {
        	for(Integer urgentTaskIndex: urgentTasksIndex){
	        	DynamicTask newUrgentTask = getTaskByIndex(urgentTaskIndex);
	        	if(!newUrgentTask.isRemoved()){
	        		boolean available = TaskManager.isAvailable(newUrgentTask, getAvailableEmployees(), this, getTaskPrecedenceGraph());
	        		newUrgentTask.setAvailable(available);
	        		
	        		List<Integer> successors = chooseRandomTasks();
	        		for (int s : successors) {
	        			DynamicTask succ = getTaskByIndex(s);
		            	if(!succ.isRemoved()){
		        			getTaskPrecedenceGraph().addEdge(urgentTaskIndex, s);
		        			////
		        			SPSPLogger.info("Urgent T_" + urgentTaskIndex + " added before T_" + s + " (T_"+ urgentTaskIndex +" -> T_" + s +")");
		        			////
		            	}
	        		}
	        	}
        	}
        }
    }

    private void removeSuccessors(int index) {
    	for(Integer t: getTaskPrecedenceGraph().getTaskSuccessors(index)){
    		DynamicTask dt = getTaskByIndex(t);
    		dt.setRemoved(true);
    		dt.setAvailable(false);
    		getTaskPrecedenceGraph().remove(dt.index());
    		////
    		SPSPLogger.info("Successors task removal T_" + t);
    		////
    		removeSuccessors(t);
    	}
	}

	public List<List<DynamicEvent>> getParallelEvents() {
        return parallelEvents;
    }

    public void setParallelEvents(List<List<DynamicEvent>> parallelEvents) {
        this.parallelEvents = parallelEvents;
    }

    public double calculateSkill(DedicationMatrix solution) throws InvalidSolutionException {
    	List<DynamicEmployee> employeesProfTask = getEmployeeProciencyOnTasks();
    	double avgEmployeesProfTask = avgEmployeesProficiencyOnTask(employeesProfTask);
    	
    	double skillValue = 0;
    	for(DynamicTask dt: getAvailableTasks()){
    		for(DynamicEmployee de: getAvailableEmployees()){
    			skillValue += (de.getProficiencyOnTask().get(dt.index()) - avgEmployeesProfTask) 
    							* solution.getDedication(de.index(), dt.index());
    		}
    	}
    	
    	return skillValue * -1; //TODO validar
    }

	private List<DynamicEmployee> getEmployeeProciencyOnTasks() {
		List<DynamicEmployee> employees = new ArrayList<>();
    	for(DynamicEmployee de: getAvailableEmployees()){
    		for(DynamicTask dt: getAvailableTasks()){
    			if(de.getProficiencyOnTask().get(dt.index()) > 0){
    				employees.add(de);
    				break;
    			}
    		}
    	}
    	return employees;
	}
	
	private double avgEmployeesProficiencyOnTask(List<DynamicEmployee> employeesProfTask){
		double sum = 0;
		for(DynamicEmployee de: employeesProfTask){
			for(DynamicTask dt: getAvailableTasks()){
				sum += de.getProficiencyOnTask().get(dt.index());
			}
		}
		return sum / employeesProfTask.size();
	}
    
    public double penalizeSkill(int missingSkills) {
    	double penalizeValue = 0;
    	for(DynamicTask dt: getAvailableTasks()){
    		penalizeValue += dt.getMaximumHeadcount() * -5;
    	}
    	return (penalizeValue * missingSkills) * -1; //TODO validar
    }

    public boolean isFinished() {
        for (DynamicTask task : getTasks()) {
            if (!task.isFinished() && !task.isRemoved()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "DynamicExtendedProject{\n\t" +
                "tasks=" + getTasks() + ",\n\t" +
                "employees=" + getEmployees() + ",\n\t" +
                "taskPrecedenceGraph=" + getTaskPrecedenceGraph() +
                "\n}";
    }

}
