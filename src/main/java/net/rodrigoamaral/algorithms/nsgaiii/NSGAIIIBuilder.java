package net.rodrigoamaral.algorithms.nsgaiii;

import org.uma.jmetal.problem.Problem;

@SuppressWarnings("rawtypes")
public class NSGAIIIBuilder extends org.uma.jmetal.algorithm.multiobjective.nsgaiii.NSGAIIIBuilder{

    @SuppressWarnings("unchecked")
	public NSGAIIIBuilder(Problem problem) {
        super(problem);
    }

    @Override
    public NSGAIII build() {
        return new NSGAIII(this);
    }
}
