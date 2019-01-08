package net.rodrigoamaral.dspsp.solution.repair;

import org.uma.jmetal.solution.DoubleSolution;

import net.rodrigoamaral.dspsp.project.DynamicEmployee;
import net.rodrigoamaral.dspsp.project.DynamicProject;
import net.rodrigoamaral.dspsp.project.tasks.DynamicTask;
import net.rodrigoamaral.logging.SPSPLogger;

public class NewEmployeeStrategy extends EmployeeReturnStrategy {

    public NewEmployeeStrategy(DoubleSolution _solution, DynamicProject _project, DynamicEmployee employee) {
        super(_solution, _project, employee);
    }

    protected void addDebugLog(DynamicTask task, int i, double newDed) {
    	SPSPLogger.debug("Repairing new employee arrival (e = " + employee.index() + ", t = " + task.index() + ") " + repairedSolution.getVariableValue(i) + " -> " + newDed);
	}
}
