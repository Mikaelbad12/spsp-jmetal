package net.rodrigoamaral.algorithms.nsgaiii;

@SuppressWarnings({ "serial", "rawtypes" })
public class NSGAIII extends org.uma.jmetal.algorithm.multiobjective.nsgaiii.NSGAIII {

    @SuppressWarnings("unchecked")
	public NSGAIII(NSGAIIIBuilder builder) {
        super(builder);
        setMaxPopulationSize(builder.getPopulationSize());
    }

}
