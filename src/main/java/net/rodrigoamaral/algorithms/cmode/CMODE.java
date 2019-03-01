package net.rodrigoamaral.algorithms.cmode;

import java.util.ArrayList;
import java.util.Collections;
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
import org.uma.jmetal.util.archive.impl.AbstractBoundedArchive;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

@SuppressWarnings({ "serial", "rawtypes" })
public class CMODE implements Algorithm {
	
	protected int maxEvaluations;
	protected int subpopulationSize;
	protected DoubleProblem problem;
	private AbstractBoundedArchive<DoubleSolution> archive;
	protected int evaluations;
	protected Random random;
	protected JMetalRandom jMetalRandom;
	protected final double c = 0.1;
	protected double[] miCR;
	protected double[] miF;
	protected Map<Integer, List<DoubleSolution>> population;
	protected Map<Integer, DoubleSolution> localBest;
	
	public CMODE(int maxEvaluations, int subpopulationSize, 
				 AbstractBoundedArchive<DoubleSolution> archive, DoubleProblem problem){
		this.maxEvaluations = maxEvaluations;
		this.subpopulationSize = subpopulationSize;
		this.archive = archive;
		this.problem = problem;
		
		//crossover
		miCR = new double[problem.getNumberOfObjectives()];
		//mutation
		miF = new double[problem.getNumberOfObjectives()];
		
		population = new HashMap<>();
		localBest = new HashMap<>();
		
		random = new Random();
		jMetalRandom = JMetalRandom.getInstance();
	}
	
	@Override
	public void run() {
		createInitialPopulationAndSetBest();
		
		updateInitialArchive();

		while(!isStoppingConditionReached()){
			executeDEforSubpopulations();
			
			List<DoubleSolution> offspringArchive = executeDEforArchive();
			
			List<DoubleSolution> allSolutions = new ArrayList<>();
			for(List<DoubleSolution> p: population.values()){
				allSolutions.addAll(p);
			}
			allSolutions.addAll(getArchive().getSolutionList());
			allSolutions.addAll(offspringArchive);
			
			updateArchive(SolutionListUtils.getNondominatedSolutions(allSolutions));
			
			//Since the archive used is a bounded archive, then there no need for archive truncation code
			//Also no need to add generation (G) variable
		}
	}
	
	protected void executeDEforSubpopulations(){
		for(int m = 0; m < problem.getNumberOfObjectives(); m++){
			List<Double> successfulMutationFactor = new ArrayList<>();
			List<Double> successfulCrossoverFactor = new ArrayList<>();
			
			List<DoubleSolution> trialList = new ArrayList<>(subpopulationSize);
			List<DoubleSolution> subpopulation = population.get(m);
			for(int i=0; i < subpopulation.size(); i++){
				DoubleSolution individual = subpopulation.get(i);
				double individualMutationFactorM = generateMutationFactor(miF[m]);
				double individualCrossoverFactorM = generateCrossoverFactor(miCR[m]);
				
				DoubleSolution individualArchive = getArchive().get(random.nextInt(getArchive().size()));
				
				int x1, x2;
				do{
					x1 = random.nextInt(subpopulation.size());
				}while(i == x1);
				do{
					x2 = random.nextInt(subpopulation.size());
				}while(x1 == x2 || i == x2);
				DoubleSolution mutantVector = generateMutantVector(individual, individualMutationFactorM, 
															  localBest.get(m), individualArchive,
															  subpopulation.get(x1), 
															  subpopulation.get(x2));
				DoubleSolution trialVector = generateTrialVector(individual, mutantVector, individualCrossoverFactorM);
				evaluate(trialVector);
				trialList.add(new ExtendedDefaultDoubleSolution((DefaultDoubleSolution)trialVector, 
												individualMutationFactorM, individualCrossoverFactorM));
			}
			
			for(int i = 0; i < subpopulationSize; i++){
				if(trialList.get(i).getObjective(m) < subpopulation.get(i).getObjective(m)){
					population.get(m).remove(i);
					population.get(m).add(i, trialList.get(i));
					ExtendedDefaultDoubleSolution solution = (ExtendedDefaultDoubleSolution)trialList.get(i);
					successfulCrossoverFactor.add(solution.getCrossoverFactor());
					successfulMutationFactor.add(solution.getMutationFactor());
				}
			}
			
			miF[m] = updateLocationParameter(miF[m], lehmerMean(successfulMutationFactor));
			miCR[m] = updateMean(miCR[m], arithmeticMean(successfulCrossoverFactor));
			
			updateBest(m);
		}
	}
	
	protected void updateInitialArchive(){
		for(List<DoubleSolution> subpop: population.values()){
			List<DoubleSolution> nonDominateSolutions = SolutionListUtils.getNondominatedSolutions(subpop);
			for(DoubleSolution s: nonDominateSolutions){
				getArchive().add(new ExtendedDefaultDoubleSolution((DefaultDoubleSolution)s, 0.5, 0.9));
			}
		}
	}
	
	protected void createInitialPopulationAndSetBest(){
		for(int m = 0; m < problem.getNumberOfObjectives(); m++){
			miCR[m] = 0.5;
			miF[m] = 0.5;
			
			createInitialSubpopulation(m);
			
			updateBest(m);
		}
	}
	
	protected void updateBest(int m){
		DoubleSolution best = population.get(m).get(0);
		for(int i = 1; i < subpopulationSize; i++){
			if(best.getObjective(m) > population.get(m).get(i).getObjective(m)){
				best = population.get(m).get(i);
			}
		}
		localBest.put(m, best);
	}
	
	protected void createInitialSubpopulation(int m){
		population.put(m, new ArrayList<>(subpopulationSize));
		for(int i = 0; i < subpopulationSize; i++){
			DoubleSolution newIndividual = problem.createSolution();
			evaluate(newIndividual);
			population.get(m).add(newIndividual);
		}
	}

	protected void evaluate(DoubleSolution newIndividual) {
		problem.evaluate(newIndividual);
		evaluations++;
	}
	
	protected void updateArchive(List<DoubleSolution> nonDominatedSolutions){
		for(DoubleSolution s: nonDominatedSolutions){
			getArchive().add(s);
		}
	}

	protected List<DoubleSolution> executeDEforArchive() {
		if(getArchive().size() < 4){
			return Collections.emptyList();
		}
		
		List<DoubleSolution> offspring = new ArrayList<>();
		for(int i=0; i < getArchive().size(); i++){
			DoubleSolution s = getArchive().get(i);
			double mutationFactor = generateMutationFactorArchive(((ExtendedDefaultDoubleSolution)s).getMutationFactor());
			double crossoverFactor = generateCrossoverFactorArchive(((ExtendedDefaultDoubleSolution)s).getCrossoverFactor());
			
			int x2, x3;
			do{
				x2 = random.nextInt(getArchive().size());
			}while(i == x2);
			do{
				x3 = random.nextInt(getArchive().size());
			}while(x2 == x3 || i == x3);
			DoubleSolution mutantVector = generateMutantVectorArchive(s, getArchive().get(x2), getArchive().get(x3), mutationFactor);
			DoubleSolution trialVector = generateTrialVector(s, mutantVector, crossoverFactor);

			evaluate(trialVector);
			
			offspring.add(new ExtendedDefaultDoubleSolution((DefaultDoubleSolution)trialVector, 
															mutationFactor, crossoverFactor));
		}
		return offspring;
	}

	protected DoubleSolution generateMutantVectorArchive(DoubleSolution solution, DoubleSolution individual2,
														DoubleSolution individual3, double mutationFactor) {
		DoubleSolution mutant = (DoubleSolution)solution.copy();
		for (int i = 0; i < solution.getNumberOfVariables(); i++) {
			double value = solution.getVariableValue(i);
			double value2 = individual2.getVariableValue(i);
			double value3 = individual3.getVariableValue(i);
			
			double mutantValue = value + mutationFactor * (value2 - value3);
			mutant.setVariableValue(i, getMutantValueInsideBound(mutantValue, i));
		}
		return mutant;
	}

	protected double generateCrossoverFactorArchive(double crossoverFactor) {
		if(random.nextDouble() < 0.1){
			return random.nextDouble();
		}
		return crossoverFactor;
	}

	protected double generateMutationFactorArchive(double mutationFactor) {
		if(random.nextDouble() < 0.1){
			return jMetalRandom.nextDouble(0.1, 1);
		}
		return mutationFactor;
	}

	protected double updateMean(double miCR, double arithmeticMean) {
		return (1 - c) * miCR + c * arithmeticMean;
	}

	protected double arithmeticMean(List<Double> successfulCrossoverFactor) {
		double sum = 0;
		for(Double d: successfulCrossoverFactor){
			sum += d;
		}
		return sum/successfulCrossoverFactor.size();
	}

	protected double updateLocationParameter(double miF, double lehmerMean) {
		return (1 - c) * miF + c * lehmerMean;
	}

	protected double lehmerMean(List<Double> successfulMutationFactor) {
		double powsum = 0;
		double sum = 0;
		for(Double d: successfulMutationFactor){
			powsum += FastMath.pow(d, 2);
			sum += d;
		}
		return powsum/sum;
	}

	//Crossover
	protected DoubleSolution generateTrialVector(DoubleSolution solution, DoubleSolution mutantVector, double cr) {
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
	protected double generateMutationFactor(double locationParameter){
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
	protected double generateCrossoverFactor(double mean){
		double standardDeviation = 0.1;
		return random.nextGaussian() * standardDeviation + mean;
	}
	
	protected DoubleSolution generateMutantVector(DoubleSolution solution, double individualMutationFactorM,
			DoubleSolution best, DoubleSolution individualArchive, DoubleSolution individual1,
			DoubleSolution individual2) {
		DoubleSolution mutant = (DoubleSolution)solution.copy();
		for (int i = 0; i < solution.getNumberOfVariables(); i++) {
			double value = solution.getVariableValue(i);
			double bestValue = best.getVariableValue(i);
			double value1 = individual1.getVariableValue(i);
			double value2 = individual2.getVariableValue(i);
			double archiveValue = individualArchive.getVariableValue(i);
			
			double mutantValue = value + individualMutationFactorM * (bestValue - value)
											+ individualMutationFactorM * (value1 - value2)
											+ individualMutationFactorM * (archiveValue - value);
			mutant.setVariableValue(i, getMutantValueInsideBound(mutantValue, i));
		}
		return mutant;
	}
	
	protected double getMutantValueInsideBound(double mutantValue, int index) {
		if(mutantValue > problem.getUpperBound(index)) {
			return problem.getUpperBound(index);
		}else if(mutantValue < problem.getLowerBound(index)) {
			return problem.getLowerBound(index);
		}else {
			return mutantValue;
		}
	}

	protected boolean isStoppingConditionReached(){
		return evaluations >= maxEvaluations;
	}
	
	protected AbstractBoundedArchive<DoubleSolution> getArchive(){
		return archive;
	}
	
	protected int getSubpopulationSize(){
		return subpopulationSize;
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
		return getArchive().getSolutionList();
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
