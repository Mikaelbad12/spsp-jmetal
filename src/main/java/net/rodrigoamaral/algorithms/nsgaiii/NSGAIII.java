package net.rodrigoamaral.algorithms.nsgaiii;

@SuppressWarnings({ "serial", "rawtypes", "unchecked" })
public class NSGAIII extends org.uma.jmetal.algorithm.multiobjective.nsgaiii.NSGAIII {

	protected int evaluations;
	protected int maxEvaluations;
	
	public NSGAIII(NSGAIIIBuilder builder) {
        super(builder);
        maxEvaluations = builder.getMaxIterations();
    }
    
	@Override
    protected void initProgress() {
		evaluations = getMaxPopulationSize();
    }

	@Override 
	protected void updateProgress() {
		evaluations += getMaxPopulationSize() ;
	}

	@Override 
	protected boolean isStoppingConditionReached() {
		return evaluations >= maxEvaluations;
	}
}
