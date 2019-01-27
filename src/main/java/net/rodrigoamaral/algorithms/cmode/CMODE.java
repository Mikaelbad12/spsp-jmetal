package net.rodrigoamaral.algorithms.cmode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.math3.util.FastMath;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.solutionattribute.impl.StrengthRawFitness;

@SuppressWarnings({ "serial", "rawtypes" })
public class CMODE implements Algorithm {
	
	private static final String DENSITY_X = "densityX";
	private int maxEvaluations;
	private int subpopulationSize;
	private int archiveSize;
//	private SolutionListEvaluator<DoubleSolution> evaluator;
	private Comparator<DoubleSolution> comparator;
	private DoubleProblem problem;
	private Archive<DoubleSolution> archive;
	private int evaluations;
	private Random random;
	private JMetalRandom jMetalRandom;
	private Comparator<DoubleSolution> densityComparator;
	private final double c = 0.1;
	
	private final StrengthRawFitness<DoubleSolution> strenghtRawFitness = new StrengthRawFitness<>();
	
	public CMODE(int maxEvaluations, int subpopulationSize, int archiveSize, 
//					SolutionListEvaluator<DoubleSolution> evaluator,
					DoubleProblem problem){
		this.maxEvaluations = maxEvaluations;
		this.subpopulationSize = subpopulationSize;
		this.archiveSize = archiveSize;
//		this.evaluator = evaluator;
		this.problem = problem;
		
		comparator = new DominanceComparator<DoubleSolution>();
		archive = new NonDominatedSolutionListArchive<>();
		random = new Random();
		jMetalRandom = JMetalRandom.getInstance();
		
		densityComparator = new Comparator<DoubleSolution>(){

			@Override
			public int compare(DoubleSolution o1, DoubleSolution o2) {
				Double value1 = (Double) o1.getAttribute(DENSITY_X);
				Double value2 = (Double) o2.getAttribute(DENSITY_X);//strenghtRawFitness.getAttributeIdentifier()
				return value1.compareTo(value2);
			}
			
		};
	}
	
	@Override
	public void run() {
		//crossover
		double[] miCR = new double[problem.getNumberOfObjectives()];
		//mutation
		double[] miF = new double[problem.getNumberOfObjectives()];
		
		Map<Integer, List<DoubleSolution>> population = new HashMap<>();
		Map<Integer, DoubleSolution> localBest = new HashMap<>();
		for(int m = 0; m < problem.getNumberOfObjectives(); m++){
			//TODO verificar como considerar apenas 1 objetivo para cada subpopulação
			miCR[m] = 0.5;
			miF[m] = 0.5;
//			List<DoubleSolution> pop = new ArrayList<>(subpopulationSize);
			population.put(m, new ArrayList<>(subpopulationSize));
			for(int i = 0; i < subpopulationSize; i++){
				DoubleSolution newIndividual = problem.createSolution();
				problem.evaluate(newIndividual);
				evaluations++;
//				pop.add(newIndividual);
				population.get(m).add(newIndividual);
			}
//			population.put(m, pop);//evaluator.evaluate(pop, problem));
//			evaluations += subpopulationSize;
			DoubleSolution best = population.get(m).get(0);
			for(int i = 1; i < subpopulationSize; i++){
				if(comparator.compare(best, population.get(m).get(i)) > 0){
					best = population.get(m).get(i);
				}
			}
			localBest.put(m, best);
		}
		
		for(List<DoubleSolution> list: population.values()){
			for(DoubleSolution s: list){
				archive.add(new ExtendedDefaultDoubleSolution((DefaultDoubleSolution)s, 0.5, 0.9));
			}
		}

		while(!isStoppingConditionReached()){
			for(int m = 0; m < problem.getNumberOfObjectives(); m++){
				List<Double> successfulMutationFactor = new ArrayList<>();
				List<Double> successfulCrossoverFactor = new ArrayList<>();
				
				List<DoubleSolution> trialList = new ArrayList<>(subpopulationSize);
				List<DoubleSolution> populationM = population.get(m);
				for(DoubleSolution individual: populationM){
					double individualMutationFactorM = generateMutationFactor(miF[m]);
					double individualCrossoverFactorM = generateCrossoverFactor(miCR[m]);
					
					DoubleSolution individualArchive = archive.get(random.nextInt(archive.size()));
					
					DoubleSolution mutantVector = generateMutantVector(individual, individualMutationFactorM, 
																  localBest.get(m), individualArchive,
																  populationM.get(0), populationM.get(1));
					DoubleSolution trialVector = generateTrialVector(individual, mutantVector, individualCrossoverFactorM);
					problem.evaluate(trialVector);
					evaluations++;
					trialList.add(new ExtendedDefaultDoubleSolution((DefaultDoubleSolution)trialVector, 
													individualMutationFactorM, individualCrossoverFactorM));
				}
//				trialList = evaluator.evaluate(trialList, problem);
//				evaluations += trialList.size();
				
				for(int i = 0; i < subpopulationSize; i++){
					if(comparator.compare(trialList.get(i), populationM.get(i)) < 0){
						population.get(m).remove(i);
						population.get(m).add(i, trialList.get(i));
						ExtendedDefaultDoubleSolution solution = (ExtendedDefaultDoubleSolution)trialList.get(i);
						successfulCrossoverFactor.add(solution.getCrossoverFactor());
						successfulMutationFactor.add(solution.getMutationFactor());
					}
				}
				
				miF[m] = updateLocationParameter(miF[m], lehmerMean(successfulMutationFactor));
				miCR[m] = updateMean(miCR[m], arithmeticMean(successfulCrossoverFactor));
				
				DoubleSolution best = population.get(m).get(0);
				for(int i = 1; i < subpopulationSize; i++){
					if(comparator.compare(best, population.get(m).get(i)) > 0){
						best = population.get(m).get(i);
					}
				}
				localBest.put(m, best);
			}
			List<DoubleSolution> offspringArchive = executeDEforArchive();
			
			List<DoubleSolution> allSolutions = new ArrayList<>();
			for(List<DoubleSolution> p: population.values()){
				allSolutions.addAll(p);
			}
			allSolutions.addAll(archive.getSolutionList());
			allSolutions.addAll(offspringArchive);
			
			archive = updateArchive(SolutionListUtils.getNondominatedSolutions(allSolutions));
			
			if(archive.size() > archiveSize){
//				List<DoubleSolution> aux = new ArrayList<>(archive.getSolutionList());//TODO verificar se eh necessario
				archiveTruncationStrategy(false);
			}
			//TODO G??
		}
	}
	
	protected void archiveTruncationStrategy(boolean sde){
		if(!sde){
			double [][] distance = SolutionListUtils.distanceMatrix(archive.getSolutionList());

			// Add the distance to the k-th individual. In the reference paper of SPEA2 and CMODE,
		    // k = sqrt(population.size()), but a value of k = 1 is recommended. See
		    // http://www.tik.ee.ethz.ch/pisa/selectors/spea2/spea2_documentation.txt
		    int k = (int)FastMath.sqrt(archive.size()); //TODO testar com 1
		    List<Double> kDistances = new ArrayList<>();
		    for (int i = 0; i < distance.length; i++) {
		    	Arrays.sort(distance[i]);
		    	//TODO normalizar o kdistance?
		    	double kDistance = 1.0 / (distance[i][k] + 2.0);
		    	kDistances.add(kDistance);
		    	archive.getSolutionList().get(i).setAttribute(DENSITY_X, kDistance);
		    }
		    Collections.sort(kDistances); 
		    Collections.sort(archive.getSolutionList(), densityComparator);
		    archive = updateArchive(archive.getSolutionList().subList(0, archiveSize));
		    
//			strenghtRawFitness.computeDensityEstimator(archive.getSolutionList());
		}else{
			double [][] distance = shiftedDistanceMatrix(archive.getSolutionList());
			//TODO e agora faz o que?
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
	private double [][] shiftedDistanceMatrix(List<DoubleSolution> solutionSet) {
		double [][] distance = new double [solutionSet.size()][solutionSet.size()];
		for (int i = 0; i < solutionSet.size(); i++){
			distance[i][i] = 0.0;
			for (int j = i + 1; j < solutionSet.size(); j++){
				DoubleSolution shiftedSolution = getShiftSolution(solutionSet.get(i),solutionSet.get(j));
				distance[i][j] = distanceBetweenObjectives(solutionSet.get(i), shiftedSolution);                
				distance[j][i] = distance[i][j];            
			}
		}
		return distance;
	}
	
	private DoubleSolution getShiftSolution(DoubleSolution firstSolution, DoubleSolution secondSolution){
		DoubleSolution shiftedSolution = (DoubleSolution)secondSolution.copy();
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
	private double distanceBetweenObjectives(DoubleSolution firstSolution, DoubleSolution secondSolution) {
		double diff;  
		double distance = 0.0;
		//euclidean distance
		for (int nObj = 0; nObj < firstSolution.getNumberOfObjectives();nObj++){
			diff = firstSolution.getObjective(nObj) - secondSolution.getObjective(nObj);
			distance += Math.pow(diff,2.0);           
		}
		return Math.sqrt(distance);
	}
	
	private Archive<DoubleSolution> updateArchive(List<DoubleSolution> nonDominatedSolutions) {
		Archive<DoubleSolution> newArchive = new NonDominatedSolutionListArchive<>();
		for(DoubleSolution s: nonDominatedSolutions){
			newArchive.add(s);
		}
		return newArchive;
	}

	private List<DoubleSolution> executeDEforArchive() {
		List<DoubleSolution> offspring = new ArrayList<>();
		if(archive.size() < 4){
			return Collections.emptyList(); //TODO validar
		}
		
//		List<DoubleSolution> aux = new ArrayList<>();
		for(DoubleSolution s: archive.getSolutionList()){
			double mutationFactor = generateMutationFactorArchive(((ExtendedDefaultDoubleSolution)s).getMutationFactor());
			double crossoverFactor = generateCrossoverFactorArchive(((ExtendedDefaultDoubleSolution)s).getCrossoverFactor());
			
			DoubleSolution mutantVector = generateMutantVectorArchive(s, archive.get(2), archive.get(3), mutationFactor);
			DoubleSolution trialVector = generateTrialVector(s, mutantVector, crossoverFactor);
//			aux.add(trialVector);
			problem.evaluate(trialVector);
			evaluations++;
			offspring.add(trialVector);
		}
		
//		offspring.addAll(evaluator.evaluate(aux, problem));
//		evaluations += offspring.size();
		return offspring;
	}

	private DoubleSolution generateMutantVectorArchive(DoubleSolution solution, DoubleSolution individual2,
														DoubleSolution individual3, double mutationFactor) {
		DoubleSolution mutant = (DoubleSolution)solution.copy();
		for (int i = 0; i < solution.getNumberOfVariables(); i++) {
			double value = solution.getVariableValue(i);
			double value2 = individual2.getVariableValue(i);
			double value3 = individual3.getVariableValue(i);
			
			//TODO verificar se precisar verificar o lowerBound and upperBound
			mutant.setVariableValue(i, value + mutationFactor * (value2 - value3));
		}
		return mutant;
	}

	private double generateCrossoverFactorArchive(double crossoverFactor) {
		if(random.nextDouble() < 0.1){
			return random.nextDouble(); //TODO retornar o que foi gerado ou um novo
		}
		return crossoverFactor;
	}

	private double generateMutationFactorArchive(double mutationFactor) {
		if(random.nextDouble() < 0.1){
			return jMetalRandom.nextDouble(0.1, 1);
		}
		return mutationFactor;
	}

	private double updateMean(double miCR, double arithmeticMean) {
		return (1 - c) * miCR + c * arithmeticMean;
	}

	private double arithmeticMean(List<Double> successfulCrossoverFactor) {
		double sum = 0;
		for(Double d: successfulCrossoverFactor){
			sum += d;
		}
		return sum/successfulCrossoverFactor.size();
	}

	private double updateLocationParameter(double miF, double lehmerMean) {
		return (1 - c) * miF + c * lehmerMean;
	}

	private double lehmerMean(List<Double> successfulMutationFactor) {
		double powsum = 0;
		double sum = 0;
		for(Double d: successfulMutationFactor){
			powsum += FastMath.pow(d, 2);
			sum += d;
		}
		return powsum/sum;
	}

	//Crossover
	private DoubleSolution generateTrialVector(DoubleSolution solution, DoubleSolution mutantVector, double cr) {
		DoubleSolution trial = (DoubleSolution)solution.copy();
		int jrand = random.nextInt(solution.getNumberOfVariables());
		for (int j = 0; j < solution.getNumberOfVariables(); j++) {
			if (random.nextDouble() <= cr || j == jrand) {
				trial.setVariableValue(j, mutantVector.getVariableValue(j)); 
			}else{
				trial.setVariableValue(j, solution.getVariableValue(j));
			}
		}
		return trial;
	}

	//https://stackoverflow.com/questions/43454078/how-to-draw-random-number-from-a-cauchy-distribution-in-matlab
	//org.apache.commons.math3.distribution.CauchyDistribution#sample -> inverseCumulativeProbability
	//	new CauchyDistribution(miF[m], scale).sample();
	private double generateMutationFactor(double locationParameter){
		double scale = 0.1;
		double factor;
		do{
			factor = locationParameter + scale * FastMath.tan(FastMath.PI * (random.nextDouble() -0.5)); 
		}while(factor <= 0);
		
		if(factor >= 1){
			factor = 1;
		}
		return factor;
	}

	//https://www.javamex.com/tutorials/random_numbers/gaussian_distribution_2.shtml
	//org.apache.commons.math3.distribution.NormalDistribution#sample
	//	new NormalDistribution(miCR[m], standardDeviation).sample();
	private double generateCrossoverFactor(double mean){
		double standardDeviation = 0.1;
		return random.nextGaussian() * standardDeviation + mean;
	}
	
	//TODO verificar se modificar o mesmo ou um novo eh criado
	private DoubleSolution generateMutantVector(DoubleSolution solution, double individualMutationFactorM,
			DoubleSolution best, DoubleSolution individualArchive, DoubleSolution individual1,
			DoubleSolution individual2) {
		DoubleSolution mutant = (DoubleSolution)solution.copy();
		for (int i = 0; i < solution.getNumberOfVariables(); i++) {
			double value = solution.getVariableValue(i);
			double bestValue = best.getVariableValue(i);
			double value1 = individual1.getVariableValue(i);
			double value2 = individual2.getVariableValue(i);
			double archiveValue = individualArchive.getVariableValue(i);
			
			//TODO verificar se precisar verificar o lowerBound and upperBound
			mutant.setVariableValue(i, value + individualMutationFactorM * (bestValue - value)
											+ individualMutationFactorM * (value1 - value2)
											+ individualMutationFactorM * (archiveValue - value));
		}
		return mutant;
	}

	protected boolean isStoppingConditionReached(){
		return evaluations >= maxEvaluations;
	}

	@Override
	public String getName() {
		return this.getClass().getName();
	}

	@Override
	public String getDescription() {
		return "Cooperative MultiObjective Differential Evolution with Multiple Populations";
	}

	@Override
	public Object getResult() {
		return archive.getSolutionList();
	}

	public static void main(String[] args) {
		List<Integer> il = new ArrayList<>();
		il.add(5);il.add(10);il.add(3);
		Collections.sort(il, new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				return o1.compareTo(o2);
			}
		});
		System.out.println(il);
		Collections.sort(il, new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				return o2.compareTo(o1);
			}
		});
		System.out.println(il);
	}
}

@SuppressWarnings("serial")
class ExtendedDefaultDoubleSolution extends DefaultDoubleSolution{
	
	private double mutationFactor;
	private double crossoverFactor;
	
	public ExtendedDefaultDoubleSolution(DefaultDoubleSolution solution, double mutationFactor, double crossoverFactor){
		super(solution);
		this.mutationFactor = mutationFactor;
		this.crossoverFactor = crossoverFactor;
	}

	public double getMutationFactor() {
		return mutationFactor;
	}

	public void setMutationFactor(double mutationFactor) {
		this.mutationFactor = mutationFactor;
	}

	public double getCrossoverFactor() {
		return crossoverFactor;
	}

	public void setCrossoverFactor(double crossoverFactor) {
		this.crossoverFactor = crossoverFactor;
	}
}
