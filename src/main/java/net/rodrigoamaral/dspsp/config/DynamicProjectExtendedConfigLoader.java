package net.rodrigoamaral.dspsp.config;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import net.rodrigoamaral.dspsp.instances.DynamicExtendedInstance;
import net.rodrigoamaral.dspsp.instances.DynamicInstance;
import net.rodrigoamaral.dspsp.instances.InstanceParser;
import net.rodrigoamaral.dspsp.project.DynamicEmployee;
import net.rodrigoamaral.dspsp.project.DynamicExtendedProject;
import net.rodrigoamaral.dspsp.project.DynamicProject;
import net.rodrigoamaral.dspsp.project.events.DynamicEvent;
import net.rodrigoamaral.dspsp.project.events.EventType;
import net.rodrigoamaral.dspsp.project.events.IEventSubject;
import net.rodrigoamaral.spsp.project.Project;

public class DynamicProjectExtendedConfigLoader extends DynamicProjectConfigLoader {

	private DynamicExtendedInstance extendedInstance;

    /**
     * Loads a dynamic project configuration from a json file
     *
     * @param configFile Relative path to the configuration file
     * @throws FileNotFoundException
     */
    public DynamicProjectExtendedConfigLoader(String configFile) throws FileNotFoundException {
    	super(configFile);
    }

    protected DynamicInstance loadFromFile(String fileName) throws FileNotFoundException {
        InstanceParser parser = new InstanceParser();
        extendedInstance = parser.parse(fileName, DynamicExtendedInstance.class);
        return extendedInstance;
    }

    /**
     * Creates a project from the configuration file
     *
     * @return a {@link Project} instance
     */
    public DynamicProject createProject() {
        project = new DynamicExtendedProject();
        loadInstanceDescription();
        loadTasks();
        loadEmployees();
        loadTaskPrecedenceGraph(project.getTasks());
        loadAvailableDisconnectedTasks();
        loadEventTimeline();
        loadTaskProficiency();
        project.updateCurrentStatus();
        return project;
    }

    protected void loadInstanceDescription() {
        String description = String.format("sT%d_dT%d_sE%d_dE%d_SK%d-%d",
                instance.getTask_number(),
                instance.getNewtask_nmb(),
                instance.getEmployee_number(),
                extendedInstance.getNewemployee_nmb(),
                instance.getEmployee_skill_min(),
                instance.getEmployee_skill_max()
                );
        project.setInstanceDescription(description);
    }

    protected void loadEmployees() {
        for (int employeeIndex = 0; employeeIndex < extendedInstance.getEmployee_total_number(); employeeIndex++) {
            DynamicEmployee emp = loadEmployee(employeeIndex);
            if (emp != null) {
                project.getEmployees().add(emp);
                project.getEmployeeIndices().put(emp.getId(), emp.index());
            }
            loadEmployeeSkill(employeeIndex);
        }
        
        for(Double time: extendedInstance.getEmployee_arrival_time()){
        	((DynamicExtendedProject)project).getEmployeeArrivalTimes().add(time); 
        }
    }

    protected DynamicEmployee loadEmployee(int employeeIndex) {
        int employeeId = employeeIndex + 1;
        double salary = extendedInstance.getEmployee_salary_total().get(employeeIndex);
        double overtimeSalary = extendedInstance.getEmployee_salary_over_total().get(employeeIndex);
        double maxDedication = extendedInstance.getEmployee_maxded_total().get(employeeIndex);

        DynamicEmployee emp = new DynamicEmployee(employeeId, salary, overtimeSalary, employeeIndex);
        emp.setMaxDedication(maxDedication);
        emp.setAvailable(instance.getAvailable_employee().contains(employeeId));
        return emp;
    }

    protected void loadEmployeeSkill(int employeeIndex) {
        List<Integer> skills = new ArrayList<>(extendedInstance.getEmployee_skill_set_total().get(employeeIndex));
        List<Double> skillsProficiency = new ArrayList<>(extendedInstance.getEmployee_skill_proficiency_set_total().get(employeeIndex));
        DynamicEmployee emp = project.getEmployees().get(employeeIndex);
        emp.setSkills(skills);
        for (int sk = 0; sk < skills.size(); sk++) {
            emp.getSkillsProficiency().put(skills.get(sk), skillsProficiency.get(sk));
        }
    }

    protected void loadEventTimeline() {
        int numberOfEvents = extendedInstance.getDynamic_parallel_time().size();
        List<List<DynamicEvent>> events = new ArrayList<>(numberOfEvents);

        int index = 0; 
        for (int i = 0; i < numberOfEvents; i++) {
        	double time = extendedInstance.getDynamic_parallel_time().get(i);
        	List<Integer> eventsCode = extendedInstance.getDynamic_parallel_class().get(i);
        	events.add(i, new ArrayList<DynamicEvent>());
//        	System.out.println("Rescheduling - " + (i+1)); //TODO ativar para ver index de eventos que tornam o problema insoluvel
        	for(Integer eventCode: eventsCode){
//        		System.out.println(index);
	            int urgentTaskId = instance.getDynamic_rushjob_number().get(index) + instance.getTask_number();
	            int leavingEmployeeId = instance.getDynamic_labour_leave_number().get(index);
	            int returningEmployeeId = instance.getDynamic_labour_return_number().get(index);
	            int newEmployeeId = extendedInstance.getDynamic_new_labour_number().get(index);
	            int removeTaskId = extendedInstance.getDynamic_remove_task_number().get(index);
	            EventType type = EventType.valueOf(eventCode);
	            IEventSubject subject = null;
	
	            switch (type) {
	                case NEW_URGENT_TASK:
	                    subject = project.getTaskById(urgentTaskId);
	                    break;
	                case EMPLOYEE_LEAVE:
	                    subject = project.getEmployeeById(leavingEmployeeId);
	                    break;
	                case EMPLOYEE_RETURN:
	                    subject = project.getEmployeeById(returningEmployeeId);
	                    break;
	                case NEW_EMPLOYEE_ARRIVE:
	                    subject = project.getEmployeeById(newEmployeeId);
	                    break;
	                case REMOVE_TASK:
	                    subject = project.getTaskById(removeTaskId);
	                    break;
	            }
	            events.get(i).add(new DynamicEvent(index, time, type, subject));
	            index++;
        	}
        }
        ((DynamicExtendedProject)project).setParallelEvents(events);
    }

}
