package net.rodrigoamaral.dspsp;

import net.rodrigoamaral.dspsp.adapters.JMetalDSPSPAdapter;
import net.rodrigoamaral.dspsp.adapters.SolutionConverter;
import net.rodrigoamaral.dspsp.project.DynamicEmployee;
import net.rodrigoamaral.dspsp.project.DynamicProject;
import net.rodrigoamaral.dspsp.project.tasks.DynamicTask;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import java.io.FileNotFoundException;

/**
 *
 * Wraps MODPSP model as a jMetal DoubleProblem.
 *
 * @author Rodrigo Amaral
 *
 */
@SuppressWarnings("serial")
public class DSPSProblem extends AbstractDoubleProblem {

    protected JMetalDSPSPAdapter dspsp;

    public DSPSProblem(String projectPropertiesFileName) throws FileNotFoundException {
        initDSPSPAdapterFromFile(projectPropertiesFileName);
        init();
    }

	protected void initDSPSPAdapterFromFile(String projectPropertiesFileName) throws FileNotFoundException {
		dspsp = new JMetalDSPSPAdapter(projectPropertiesFileName);
	}

    public DSPSProblem(DynamicProject project) {
        initDSPSPAdapterFromProject(project);
        init();
    }

	protected void initDSPSPAdapterFromProject(DynamicProject project) {
		dspsp = new JMetalDSPSPAdapter(project);
	}

    private void init() {
        setName(dspsp.getProblemName());
        setNumberOfVariables(dspsp.getNumberOfVariables());
        setNumberOfObjectives(dspsp.getNumberOfObjectives());
        setNumberOfConstraints(dspsp.getNumberOfConstraints());
        setLowerLimit(dspsp.getLowerLimit());
        setUpperLimit(dspsp.getUpperLimit());
    }

    @Override
    public void evaluate(DoubleSolution solution) {
        dspsp.evaluateObjectives(solution);
    }

    @Override
    public DoubleSolution createSolution() {
        DoubleSolution newSolution = super.createSolution();
        newSolution = enableOnlyAvailableEmployees(newSolution);
        newSolution = enableOnlyAvailableTasks(newSolution);
        return newSolution;
    }

    private DoubleSolution enableOnlyAvailableTasks(DoubleSolution solution) {
        for (DynamicTask task : getProject().getTasks()) {
            if (!task.isAvailable()) {
                for (DynamicEmployee employee : getProject().getEmployees()) {
                    solution.setVariableValue(
                            SolutionConverter.encode(employee.index(), task.index()),
                            0.0
                    );
                }
            }
        }
        return solution;
    }

    private DoubleSolution enableOnlyAvailableEmployees(DoubleSolution solution) {
        for (DynamicEmployee employee : getProject().getEmployees()) {
            if (!employee.isAvailable()) {
                for (DynamicTask task : getProject().getAvailableTasks()) {
                    solution.setVariableValue(
                            SolutionConverter.encode(employee.index(), task.index()),
                            0.0
                    );
                }
            }
        }
        return solution;
    }


    public DynamicProject getProject() {
        return dspsp.getProject();
    }

    public String getInstanceDescription() {
        return getProject().getInstanceDescription();
    }
    
}
