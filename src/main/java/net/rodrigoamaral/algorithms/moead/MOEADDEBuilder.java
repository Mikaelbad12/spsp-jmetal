package net.rodrigoamaral.algorithms.moead;

import org.uma.jmetal.algorithm.multiobjective.moead.AbstractMOEAD;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADBuilder;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;

public class MOEADDEBuilder extends MOEADBuilder{

	public MOEADDEBuilder(Problem<DoubleSolution> problem) {
		super(problem, MOEADBuilder.Variant.MOEAD);
	}

	@Override
	public AbstractMOEAD<DoubleSolution> build() {
		return new MOEADDE(problem, populationSize, resultPopulationSize, maxEvaluations, mutation,
		          crossover, functionType, dataDirectory, neighborhoodSelectionProbability,
		          maximumNumberOfReplacedSolutions, neighborSize);
	}
}
