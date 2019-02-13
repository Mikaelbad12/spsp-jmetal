package net.rodrigoamaral.dspsp.solution.repair;

import net.rodrigoamaral.dspsp.adapters.SolutionConverter;
import net.rodrigoamaral.dspsp.project.DynamicEmployee;
import net.rodrigoamaral.dspsp.project.DynamicProject;
import net.rodrigoamaral.dspsp.project.tasks.DynamicTask;
import net.rodrigoamaral.dspsp.project.tasks.TaskManager;
import net.rodrigoamaral.dspsp.solution.DedicationMatrix;
import net.rodrigoamaral.logging.SPSPLogger;
import org.uma.jmetal.solution.DoubleSolution;

public class EmployeeReturnStrategy extends ScheduleRepairStrategy {

    protected final DynamicEmployee employee;

    public EmployeeReturnStrategy(DoubleSolution _solution, DynamicProject _project, DynamicEmployee employee) {
        super(_solution, _project);
        this.employee = employee;
    }
    
    public EmployeeReturnStrategy(DynamicProject _project, DynamicEmployee employee) {
        super(_project);
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
    	for (DynamicTask task: project.getAvailableTasks()) {
            if (TaskManager.teamSize(task, schedule) < task.getMaximumHeadcount()) {
                for (Integer sk: employee.getSkills()) {
                    if (task.getSkills().contains(sk)) {
                        int i = SolutionConverter.encode(employee.index(), task.index());
                        double newDed = 0.1;
                        if (solution.getVariableValue(i) < DedicationMatrix.MIN_DED_THRESHOLD) {
                            addDebugLog(task, i, newDed, solution);
                            solution.setVariableValue(i, newDed);
                        }
                    }
                }
            }
        }
        normalize(solution);
    }

	protected void addDebugLog(DynamicTask task, int i, double newDed, DoubleSolution solution) {
		SPSPLogger.debug("Repairing employee return (e = " + employee.index() + ", t = " + task.index() + ") " + solution.getVariableValue(i) + " -> " + newDed);
	}
}
