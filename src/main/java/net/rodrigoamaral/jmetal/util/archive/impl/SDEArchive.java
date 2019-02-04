package net.rodrigoamaral.jmetal.util.archive.impl;

import java.util.Comparator;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.archive.impl.AbstractBoundedArchive;

import net.rodrigoamaral.jmetal.util.comparator.ShiftedStrengthFitnessComparator;
import net.rodrigoamaral.jmetal.util.solutionattribute.impl.ShiftedStrengthRawFitness;

/**
 * Shift-based density estimation
 * @author Allan
 *
 * @param <S>
 */
@SuppressWarnings("serial")
public class SDEArchive <S extends Solution<?>> extends AbstractBoundedArchive<S> {

	private Comparator<S> fitnessComparator;
	private ShiftedStrengthRawFitness<S> shiftedStrenghtRawFitness;
	
	public SDEArchive(int maxSize, boolean normalize) {
		super(maxSize);
		fitnessComparator = new ShiftedStrengthFitnessComparator<S>();
		shiftedStrenghtRawFitness = new ShiftedStrengthRawFitness<S>(normalize);
	}
	
	public SDEArchive(int maxSize) {
		super(maxSize);
		fitnessComparator = new ShiftedStrengthFitnessComparator<S>();
		shiftedStrenghtRawFitness = new ShiftedStrengthRawFitness<S>();
	}

	@Override
	public Comparator<S> getComparator() {
		return fitnessComparator;
	}

	@Override
	public void computeDensityEstimator() {
		shiftedStrenghtRawFitness.computeDensityEstimator(getSolutionList());
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
