package net.rodrigoamaral.dspsp.solution.repair;

import org.uma.jmetal.solution.DoubleSolution;

import net.rodrigoamaral.dspsp.project.DynamicEmployee;
import net.rodrigoamaral.dspsp.project.DynamicProject;
import net.rodrigoamaral.dspsp.project.tasks.DynamicTask;
import net.rodrigoamaral.logging.SPSPLogger;

public class NewEmployeeStrategy extends EmployeeReturnStrategy {

    public NewEmployeeStrategy(DynamicProject _project, DynamicEmployee employee) {
        super(_project, employee);
    }

    @Override
    protected void addDebugLog(DynamicTask task, int i, double newDed, DoubleSolution solution) {
    	SPSPLogger.debug("Repairing new employee arrival (e = " + employee.index() + ", t = " + task.index() + ") " + solution.getVariableValue(i) + " -> " + newDed);
	}
}
