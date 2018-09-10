package net.rodrigoamaral.algorithms.gde3;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.algorithm.multiobjective.gde3.GDE3;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.selection.DifferentialEvolutionSelection;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

@SuppressWarnings("serial")
public class GDE3Dynamic extends GDE3 {

    private List<DoubleSolution> initialPopulation;

	public GDE3Dynamic(DoubleProblem problem, int populationSize, int maxEvaluations,
				       DifferentialEvolutionSelection selection, DifferentialEvolutionCrossover crossover,
				       SolutionListEvaluator<DoubleSolution> evaluator, List<DoubleSolution> initialPopulation) {
        super(problem, populationSize, maxEvaluations, selection, crossover, evaluator);
        this.initialPopulation = initialPopulation;
    }

    @Override
    protected List<DoubleSolution> createInitialPopulation() {
        List<DoubleSolution> population = new ArrayList<>(getMaxPopulationSize());
        for (int i = 0; i < getMaxPopulationSize(); i++) {
            DoubleSolution newIndividual;
            if (initialPopulation == null || initialPopulation.isEmpty()) {
                newIndividual = (DoubleSolution) getProblem().createSolution();
            } else {
                newIndividual = new DefaultDoubleSolution((DefaultDoubleSolution) initialPopulation.get(i));
            }
            population.add(newIndividual);
        }
        return population;
    }
}
