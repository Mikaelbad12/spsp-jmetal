package net.rodrigoamaral.algorithms.gde3;

import java.util.List;

import org.uma.jmetal.algorithm.multiobjective.gde3.GDE3;
import org.uma.jmetal.algorithm.multiobjective.gde3.GDE3Builder;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

public class GDE3DynamicBuilder extends GDE3Builder{

	private DoubleProblem problem;
    private List<DoubleSolution> initialPopulation;

	public GDE3DynamicBuilder(DoubleProblem problem) {
        super(problem);
        this.problem = problem;
    }

    public GDE3DynamicBuilder setInitialPopulation(List<DoubleSolution> initialPopulation) {
        this.initialPopulation = initialPopulation;
        return this;
    }

    @Override
    public GDE3 build() {
        return new GDE3Dynamic(problem, populationSize, maxEvaluations, selectionOperator,
                				crossoverOperator, evaluator, initialPopulation);
    }
}
