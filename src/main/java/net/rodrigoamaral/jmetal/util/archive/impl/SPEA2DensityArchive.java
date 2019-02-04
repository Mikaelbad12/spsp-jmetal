package net.rodrigoamaral.jmetal.util.archive.impl;

import java.util.Comparator;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.archive.impl.AbstractBoundedArchive;
import org.uma.jmetal.util.comparator.StrengthFitnessComparator;
import org.uma.jmetal.util.solutionattribute.impl.StrengthRawFitness;

@SuppressWarnings("serial")
public class SPEA2DensityArchive <S extends Solution<?>> extends AbstractBoundedArchive<S> {

	private Comparator<S> fitnessComparator;
	private StrengthRawFitness<S> strenghtRawFitness;
	
	public SPEA2DensityArchive(int maxSize) {
		super(maxSize);
		fitnessComparator = new StrengthFitnessComparator<S>();
		strenghtRawFitness = new StrengthRawFitness<S>();
	}

	@Override
	public Comparator<S> getComparator() {
		return fitnessComparator;
	}

	@Override
	public void computeDensityEstimator() {
		strenghtRawFitness.computeDensityEstimator(getSolutionList());
	}

	@Override
	public void prune() {
		if (size() > getMaxSize()){            
			computeDensityEstimator();
			S worst = new SolutionListUtils().findWorstSolution(getSolutionList(), fitnessComparator);
			getSolutionList().remove(worst);
		}        
	}

}
