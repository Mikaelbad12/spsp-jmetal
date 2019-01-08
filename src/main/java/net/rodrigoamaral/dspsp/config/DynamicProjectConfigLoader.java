package net.rodrigoamaral.dspsp.config;

import net.rodrigoamaral.dspsp.project.*;
import net.rodrigoamaral.dspsp.project.events.DynamicEvent;
import net.rodrigoamaral.dspsp.project.events.EventType;
import net.rodrigoamaral.dspsp.project.events.IEventSubject;
import net.rodrigoamaral.dspsp.project.tasks.DynamicTask;
import net.rodrigoamaral.dspsp.project.tasks.TaskManager;
import net.rodrigoamaral.dspsp.instances.DynamicInstance;
import net.rodrigoamaral.dspsp.instances.InstanceParser;
import net.rodrigoamaral.spsp.project.Project;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Rodrigo Amaral
 *
 */
public class DynamicProjectConfigLoader {

	protected DynamicInstance instance;
	protected DynamicProject project;

    /**
     * Loads a dynamic project configuration from a json file
     *
     * @param configFile Relative path to the configuration file
     * @throws FileNotFoundException
     */
    public DynamicProjectConfigLoader(String configFile) throws FileNotFoundException {
        instance = loadFromFile(configFile);
    }

    protected DynamicInstance loadFromFile(String fileName) throws FileNotFoundException {
        InstanceParser parser = new InstanceParser();
        return parser.parse(fileName, DynamicInstance.class);
    }

    /**
     * Creates a project from the configuration file
     *
     * @return a {@link Project} instance
     */
    public DynamicProject createProject() {
        project = new DynamicProject();
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
        String description = String.format("sT%d_dT%d_E%d_SK%d-%d",
                instance.getTask_number(),
                instance.getNewtask_nmb(),
                instance.getEmployee_number(),
                instance.getEmployee_skill_min(),
                instance.getEmployee_skill_max()
                );
        project.setInstanceDescription(description);
    }

    protected void loadAvailableDisconnectedTasks() {
        for (int t: project.getTaskPrecedenceGraph().getDisconnectedTasks()) {
            if (t < instance.getTask_number()) {
                DynamicTask newTask = project.getTaskByIndex(t);
                boolean available = TaskManager.isAvailable(newTask, project.getEmployees(), project, project.getTaskPrecedenceGraph());
                newTask.setAvailable(available);
            }
        }
    }

    protected void loadTasks() {

        for (int taskIndex = 0; taskIndex < instance.getTask_total_number(); taskIndex++) {
            DynamicTask t = loadTask(taskIndex);
            if (t != null) {
                project.getTasks().add(t);
                project.getTaskIndices().put(t.getId(), t.index());
            }
            loadTaskSkill(taskIndex);
        }
        for (double time: instance.getArrival_time()) {
            project.getTaskArrivalTimes().add(time);
        }
    }


    protected DynamicTask loadTask(int taskIndex) {
        int taskId = taskIndex + 1;
        double initialEstimatedEffort = instance.getTask_effort_real_secnario_total().get(taskIndex);
        double meanEstimatedEffort = instance.getTask_effort_mu_total().get(taskIndex);
        double effortDeviation = instance.getTask_effort_sigma_total().get(taskIndex);
        int maximumHeadcount = instance.getTask_headcount_total().get(taskIndex);
        return new DynamicTask(taskId,
                               initialEstimatedEffort,
                               meanEstimatedEffort,
                               effortDeviation,
                               taskIndex,
                               maximumHeadcount);
    }

    protected void loadTaskSkill(int taskIndex) {
        List<Integer> skills = instance.getTask_skill_set_total().get(taskIndex);
        DynamicTask t = project.getTasks().get(taskIndex);
        for (Integer skill: skills) {
            t.getSkills().add(skill);
        }
    }

    protected void loadEmployees() {
        for (int employeeIndex = 0; employeeIndex < instance.getEmployee_number(); employeeIndex++) {
            DynamicEmployee emp = loadEmployee(employeeIndex);
            if (emp != null) {
                project.getEmployees().add(emp);
                project.getEmployeeIndices().put(emp.getId(), emp.index());
            }
            loadEmployeeSkill(employeeIndex);
        }
    }

    protected DynamicEmployee loadEmployee(int employeeIndex) {
        int employeeId = instance.getAvailable_employee().get(employeeIndex);
        double salary = instance.getEmployee_salary().get(employeeIndex);
        double overtimeSalary = instance.getEmployee_salary_over().get(employeeIndex);
        double maxDedication = instance.getEmployee_maxded().get(employeeIndex);

        DynamicEmployee emp = new DynamicEmployee(employeeId, salary, overtimeSalary, employeeIndex);
        emp.setMaxDedication(maxDedication);
        return emp;
    }

    protected void loadEmployeeSkill(int employeeIndex) {
        List<Integer> skills = new ArrayList<>(instance.getEmployee_skill_set().get(employeeIndex));
        List<Double> skillsProficiency = new ArrayList<>(instance.getEmployee_skill_proficieny_set().get(employeeIndex));
        DynamicEmployee emp = project.getEmployees().get(employeeIndex);
        emp.setSkills(skills);
        for (int sk = 0; sk < skills.size(); sk++) {
            emp.getSkillsProficiency().put(skills.get(sk), skillsProficiency.get(sk));
        }
    }

    protected void loadTaskPrecedenceGraph(List<DynamicTask> tasks) {
        int initialSize = instance.getTask_total_number();
        project.setTaskPrecedenceGraph(new DynamicTaskPrecedenceGraph(initialSize));
        for (List<Integer> edge : instance.getEdge_set()) {
            DynamicTask t1 = project.getTaskById(edge.get(0));
            DynamicTask t2 = project.getTaskById(edge.get(1));
            t1.setAvailable(true);
            t2.setAvailable(true);
            project.getTaskPrecedenceGraph().addEdge(t1.index(),
                                                     t2.index());
        }
    }

    protected void loadEventTimeline() {

        int numberOfEvents = instance.getDynamic_time().size();
        List<DynamicEvent> events = new ArrayList<>(numberOfEvents);

        for (int i = 0; i < numberOfEvents; i++) {

            double time = instance.getDynamic_time().get(i);
            int eventCode = instance.getDynamic_class().get(i);
            int urgentTaskId = instance.getDynamic_rushjob_number().get(i) + instance.getTask_number();
            int leavingEmployeeId = instance.getDynamic_labour_leave_number().get(i);
            int returningEmployeeId = instance.getDynamic_labour_return_number().get(i);
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
                default:
                	continue;
            }

            events.add(new DynamicEvent(i, time, type, subject));
        }
        project.setEvents(events);
    }

    protected void loadTaskProficiency() {
        for (DynamicEmployee employee: project.getEmployees()) {
            int i = employee.index();
            project.getTaskProficiency().put(i, instance.getTask_Proficieny_total().get(i));
            for (int t = 0; t < instance.getTask_Proficieny_total().get(i).size(); t++) {
                employee.getProficiencyOnTask().put(t, instance.getTask_Proficieny_total().get(i).get(t));
            }
        }
    }

}
