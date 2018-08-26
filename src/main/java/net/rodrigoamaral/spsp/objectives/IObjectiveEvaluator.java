package net.rodrigoamaral.spsp.objectives;

import net.rodrigoamaral.spsp.project.Project;
import net.rodrigoamaral.spsp.solution.DedicationMatrix;

/**
 *
 * Basic interface for a class which evaluates all objectives
 * in a {@link net.rodrigoamaral.spsp.SPSProblem}.
 *
 * @author Rodrigo Amaral
 *
 */
public interface IObjectiveEvaluator {
    IObjectiveEvaluator addObjective(IObjective objective);

    double evaluate(int index, Project project, DedicationMatrix solution);
    int size();
}
