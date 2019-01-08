package net.rodrigoamaral.algorithms.nsgaiii;

import java.util.List;

import org.uma.jmetal.algorithm.multiobjective.nsgaiii.NSGAIII;
import org.uma.jmetal.algorithm.multiobjective.nsgaiii.NSGAIIIBuilder;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;

@SuppressWarnings("rawtypes")
public class NSGAIIIDynamicBuilder extends NSGAIIIBuilder{

    private List<DoubleSolution> initialPopulation;

    @SuppressWarnings("unchecked")
	public NSGAIIIDynamicBuilder(Problem problem) {
        super(problem);
    }

    public NSGAIIIDynamicBuilder setInitialPopulation(List<DoubleSolution> initialPopulation) {
        this.initialPopulation = initialPopulation;
        return this;
    }

    @Override
    public NSGAIII build() {
        return new NSGAIIIDynamic(this, initialPopulation);
    }
}
