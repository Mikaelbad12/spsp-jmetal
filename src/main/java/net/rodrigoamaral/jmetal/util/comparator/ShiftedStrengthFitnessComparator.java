package net.rodrigoamaral.jmetal.util.comparator;

import java.io.Serializable;
import java.util.Comparator;

import org.uma.jmetal.solution.Solution;

import net.rodrigoamaral.jmetal.util.solutionattribute.impl.ShiftedStrengthRawFitness;

@SuppressWarnings("serial")
public class ShiftedStrengthFitnessComparator <S extends Solution<?>> implements Comparator<S>, Serializable{

	private final ShiftedStrengthRawFitness<S> fitnessValue = new ShiftedStrengthRawFitness<S>();
	
	@Override
	public int compare(S solution1, S solution2) {
		int result ;
		if (solution1 == null) {
			if (solution2 == null) {
				result = 0;
			} else {
				result = 1 ;
			}
		} else if (solution2 == null) {
			result = -1;
		} else {
			double strengthFitness1 = Double.MIN_VALUE ;
			double strengthFitness2 = Double.MIN_VALUE ;

			if (fitnessValue.getAttribute(solution1) != null) {
				strengthFitness1 = (double) fitnessValue.getAttribute(solution1);
			}

			if (fitnessValue.getAttribute(solution2) != null) {
				strengthFitness2 = (double) fitnessValue.getAttribute(solution2);
			}

			if (strengthFitness1 < strengthFitness2) {
				result = -1;
			} else  if (strengthFitness1 > strengthFitness2) {
				result = 1;
			} else {
				result = 0;
			}
		}
		return result;
	}

}