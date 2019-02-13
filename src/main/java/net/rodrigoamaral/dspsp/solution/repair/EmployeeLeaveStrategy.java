package net.rodrigoamaral.dspsp.solution.repair;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.uma.jmetal.solution.DoubleSolution;

import net.rodrigoamaral.dspsp.adapters.SolutionConverter;
import net.rodrigoamaral.dspsp.project.DynamicEmployee;
import net.rodrigoamaral.dspsp.project.DynamicProject;
import net.rodrigoamaral.dspsp.project.tasks.DynamicTask;
import net.rodrigoamaral.dspsp.project.tasks.TaskManager;
import net.rodrigoamaral.dspsp.solution.DedicationMatrix;
import net.rodrigoamaral.logging.SPSPLogger;

public class EmployeeLeaveStrategy extends ScheduleRepairStrategy {

    private final DynamicEmployee employee;

    public EmployeeLeaveStrategy(DoubleSolution schedule, DynamicProject project, DynamicEmployee employee) {
        super(schedule, project);
        this.employee = employee;
    }
    
    public EmployeeLeaveStrategy(DynamicProject project, DynamicEmployee employee) {
        super(project);
        this.employee = employee;
    }
    
    public void repair(DoubleSolution solution) {
    	DedicationMatrix schedule = new SolutionConverter(project).convert(solution);
    	repair(solution, schedule);
    }

    @Override
    public DoubleSolution repair() {
    	repair(repairedSolution, schedule);
        return repairedSolution;
    }
    
    private void repair(DoubleSolution solution, DedicationMatrix schedule) {
    	List<Integer> assignedTasks = TaskManager.getEmployeeAssignedTasks(schedule, employee);

        for (Integer t: assignedTasks) {

            DynamicTask assignedTask = project.getTaskByIndex(t);

            Set<Integer> missingSkills = new HashSet<>(assignedTask.getSkills());

            // remaining employees in the task team can satisfy the task skill constraint
            for (Integer e: remainingEmployees()) {
                DynamicEmployee remainingEmployee = project.getEmployeeByIndex(e);
                if (TaskManager.getEmployeesAssignedToTask(schedule, assignedTask, project).contains(remainingEmployee)) {
                    missingSkills.removeAll(remainingEmployee.getSkills());
                }
            }

            // Otherwise, other available employees with relatively higher proficiencies are found to join the task team to satisfy the skill requirement.
            if (!missingSkills.isEmpty()) {
                List<DynamicEmployee> employeesNotAssigned = TaskManager.getEmployeesNotAssignedToTask(schedule, assignedTask, project);
                employeesNotAssigned = project.getSortedTeamByProficiencyInTask(employeesNotAssigned, assignedTask);
                List<DynamicEmployee> employeesWithSkills = new ArrayList<>();
                for (DynamicEmployee ena: employeesNotAssigned) {
                    Set<Integer> empSkills = new HashSet<>(ena.getSkills());
                    if (empSkills.containsAll(missingSkills)) {
                        employeesWithSkills.add(ena);
                    }
                }

                // If there are employees with the required skills allocate dedication to the task
                if (!employeesWithSkills.isEmpty()) {
                    for (DynamicEmployee ews: employeesWithSkills) {
                        double newDed = schedule.getDedication(employee.index(), t);

                        int i = SolutionConverter.encode(ews.index(), t);
                        SPSPLogger.debug("Repairing employee leave (e = " + ews.index() + ", t = " + t + ") " + solution.getVariableValue(i) + " -> " + newDed);
                        solution.setVariableValue(i, newDed);

                    }
                } else {
                    // TODO: What if there are no employees with the required skills?
                }

            }

        }
        normalize(solution);
    }

    private List<Integer> remainingEmployees() {
        List<Integer> remaining = new ArrayList<>();
        for (Integer e: project.getLastAvailableEmployees()) {
            if (e != employee.index()) {
                remaining.add(e);
            }
        }
        return remaining;
    }
}
