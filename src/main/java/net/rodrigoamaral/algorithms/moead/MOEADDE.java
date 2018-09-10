package net.rodrigoamaral.algorithms.moead;

import org.uma.jmetal.algorithm.multiobjective.moead.MOEAD;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;

@SuppressWarnings({ "serial" })
public class MOEADDE extends MOEAD {

	public MOEADDE(Problem<DoubleSolution> problem, int populationSize, int resultPopulationSize, int maxEvaluations,
			MutationOperator<DoubleSolution> mutation, CrossoverOperator<DoubleSolution> crossover,
			FunctionType functionType, String dataDirectory,
			double neighborhoodSelectionProbability, int maximumNumberOfReplacedSolutions, int neighborSize) {
		super(problem, populationSize, resultPopulationSize, maxEvaluations, mutation, crossover, functionType, dataDirectory,
				neighborhoodSelectionProbability, maximumNumberOfReplacedSolutions, neighborSize);
	}

}
