package net.rodrigoamaral.jmetal.util.solutionattribute.impl;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.solutionattribute.DensityEstimator;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

@SuppressWarnings("serial")
public class ShiftedStrengthRawFitness <S extends Solution<?>> 
										extends GenericSolutionAttribute<S, Double> 
										implements DensityEstimator<S>{
	
	private static final Comparator<Solution<?>> DOMINANCE_COMPARATOR = new DominanceComparator<Solution<?>>();
	private boolean normalize;
	
	public ShiftedStrengthRawFitness(){
	}
	
	public ShiftedStrengthRawFitness (boolean normalize){
		this.normalize = normalize;
	}
	
	@Override
	public void computeDensityEstimator(List<S> solutionSet) {
		double [][] distance = shiftedDistanceMatrix(solutionSet);
		double []   strength    = new double[solutionSet.size()];
		double []   rawFitness  = new double[solutionSet.size()];
		double kDistance                                          ;

		// strength(i) = |{j | j <- SolutionSet and i dominate j}|
		for (int i = 0; i < solutionSet.size(); i++) {
			for (int j = 0; j < solutionSet.size();j++) {
				if (DOMINANCE_COMPARATOR.compare(solutionSet.get(i),solutionSet.get(j))==-1) {
					strength[i] += 1.0;
				}
			}
		}

		//Calculate the raw fitness
		// rawFitness(i) = |{sum strenght(j) | j <- SolutionSet and j dominate i}|
		for (int i = 0;i < solutionSet.size(); i++) {
			for (int j = 0; j < solutionSet.size();j++) {
				if (DOMINANCE_COMPARATOR.compare(solutionSet.get(i),solutionSet.get(j))==1) {
					rawFitness[i] += strength[j];
				}
			}
		}

		// Add the distance to the k-th individual. In the reference paper of SPEA2,
		// k = sqrt(population.size()), but a value of k = 1 is recommended. See
		// http://www.tik.ee.ethz.ch/pisa/selectors/spea2/spea2_documentation.txt
		int k = 1 ;
		for (int i = 0; i < distance.length; i++) {
			Arrays.sort(distance[i]);
			kDistance = 1.0 / (distance[i][k] + 2.0);
			solutionSet.get(i).setAttribute(getAttributeIdentifier(), rawFitness[i] + kDistance);
		}
	}
	
	/**
	 * Returns a matrix with the euclidean distance between each pair of solutions in the population
	 * where the second solution is shifted. the value of shifted solution is firstSolution if 
	 * secondSolution < firstSolution otherwise is secondSolution
	 * Distances are measured in the objective space
	 * @param solutionSet
	 * @return
	 */
	private double [][] shiftedDistanceMatrix(List<S> solutionSet) {
		double max = Double.MIN_VALUE;
		
		double [][] distance = new double [solutionSet.size()][solutionSet.size()];
		for (int i = 0; i < solutionSet.size(); i++){
			distance[i][i] = 0.0;
			for (int j = i + 1; j < solutionSet.size(); j++){
				S shiftedSolution = getShiftSolution(solutionSet.get(i),solutionSet.get(j));
				distance[i][j] = distanceBetweenObjectives(solutionSet.get(i), shiftedSolution);                
				distance[j][i] = distance[i][j];
				
				if(distance[i][j] > max){
					max = distance[i][j];
				}
			}
		}
		//to normalize need find max and min, then d[i][j] - min / max - min
		//min = 0
		if(normalize){
			for (int i = 0; i < distance.length; i++){
				for (int j = i + 1; j < distance.length; j++){
					distance[i][j] = distance[i][j] / max; 
				}
			}
		}
		return distance;
	}
	
	@SuppressWarnings("unchecked")
	private S getShiftSolution(S firstSolution, S secondSolution){
		S shiftedSolution = (S)secondSolution.copy();
		for(int obj = 0; obj < firstSolution.getNumberOfObjectives(); obj++){
			if(secondSolution.getObjective(obj) < firstSolution.getObjective(obj)){
				shiftedSolution.setObjective(obj, firstSolution.getObjective(obj));
			}else{
				shiftedSolution.setObjective(obj, secondSolution.getObjective(obj));
			}
		}
		return shiftedSolution;
	}
	
	/**
	 * Returns the euclidean distance between a pair of solutions in the objective space
	 * @param firstSolution
	 * @param secondSolution
	 * @return
	 */
	private double distanceBetweenObjectives(S firstSolution, S secondSolution) {
		double diff;  
		double distance = 0.0;
		//euclidean distance
		for (int nObj = 0; nObj < firstSolution.getNumberOfObjectives();nObj++){
			diff = firstSolution.getObjective(nObj) - secondSolution.getObjective(nObj);
			distance += Math.pow(diff,2.0);           
		}
		return Math.sqrt(distance);
	}
	
}
