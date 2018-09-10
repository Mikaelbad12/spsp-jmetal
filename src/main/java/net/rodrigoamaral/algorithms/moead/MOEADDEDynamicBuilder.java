package net.rodrigoamaral.algorithms.moead;

import java.util.List;

import org.uma.jmetal.algorithm.multiobjective.moead.AbstractMOEAD;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADBuilder;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;

public class MOEADDEDynamicBuilder extends MOEADBuilder{

	private List<DoubleSolution> initialPopulation;
	
	public MOEADDEDynamicBuilder(Problem<DoubleSolution> problem) {
		super(problem, MOEADBuilder.Variant.MOEAD);
	}
	
	public MOEADDEDynamicBuilder setInitialPopulation(List<DoubleSolution> initialPopulation) {
        this.initialPopulation = initialPopulation;
        return this;
    }

	@Override
	public AbstractMOEAD<DoubleSolution> build() {
		return new MOEADDEDynamic(problem, populationSize, resultPopulationSize, maxEvaluations, mutation,
		          crossover, functionType, dataDirectory, neighborhoodSelectionProbability,
		          maximumNumberOfReplacedSolutions, neighborSize, initialPopulation);
	}
}
