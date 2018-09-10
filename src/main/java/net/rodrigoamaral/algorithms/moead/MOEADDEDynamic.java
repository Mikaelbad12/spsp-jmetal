package net.rodrigoamaral.algorithms.moead;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;

@SuppressWarnings({ "serial" })
public class MOEADDEDynamic extends MOEADDE {
	
	private List<DoubleSolution> initialPopulation;

	public MOEADDEDynamic(Problem<DoubleSolution> problem, int populationSize, int resultPopulationSize, int maxEvaluations,
			MutationOperator<DoubleSolution> mutation, CrossoverOperator<DoubleSolution> crossover,
			FunctionType functionType, String dataDirectory,
			double neighborhoodSelectionProbability, int maximumNumberOfReplacedSolutions, int neighborSize, List<DoubleSolution> initialPopulation) {
		super(problem, populationSize, resultPopulationSize, maxEvaluations, mutation, crossover, functionType, dataDirectory,
				neighborhoodSelectionProbability, maximumNumberOfReplacedSolutions, neighborSize);
		this.initialPopulation = initialPopulation;
	}

	@Override
    protected void initializePopulation() {
        population = new ArrayList<>(populationSize);
        for (int i = 0; i < populationSize; i++) {
            DoubleSolution newIndividual;
            if (initialPopulation == null || initialPopulation.isEmpty()) {
                newIndividual = (DoubleSolution) problem.createSolution();
            } else {
                newIndividual = new DefaultDoubleSolution((DefaultDoubleSolution) initialPopulation.get(i));
            }
            problem.evaluate(newIndividual);
            population.add(newIndividual);
        }
    }
	
}
