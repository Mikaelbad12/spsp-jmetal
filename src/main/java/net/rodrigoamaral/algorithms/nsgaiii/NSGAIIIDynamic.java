package net.rodrigoamaral.algorithms.nsgaiii;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;

@SuppressWarnings("serial")
public class NSGAIIIDynamic extends NSGAIII {

    private List<DoubleSolution> initialPopulation;

	public NSGAIIIDynamic(NSGAIIIDynamicBuilder builder, List<DoubleSolution> initialPopulation) {
        super(builder);
        this.initialPopulation = initialPopulation;
    }

    @Override
    protected List<DoubleSolution> createInitialPopulation() {
        List<DoubleSolution> population = new ArrayList<>(getMaxPopulationSize());
        for (int i = 0; i < getMaxPopulationSize(); i++) {
            DoubleSolution newIndividual;
            if (initialPopulation == null || initialPopulation.isEmpty() || i >= initialPopulation.size()) {
                newIndividual = (DoubleSolution) getProblem().createSolution();
            } else {
                newIndividual = new DefaultDoubleSolution((DefaultDoubleSolution) initialPopulation.get(i));
            }
            population.add(newIndividual);
        }
        return population;
    }
}
