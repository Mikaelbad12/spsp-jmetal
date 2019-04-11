package net.rodrigoamaral.algorithms.cmode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.archive.impl.AbstractBoundedArchive;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;

import net.rodrigoamaral.dspsp.solution.repair.IScheduleRepairStrategy;

@SuppressWarnings({ "serial" })
public class CMODEDynamic extends CMODE {
	
	private static AbstractBoundedArchive<DoubleSolution> historyArchive;
	private NonDominatedSolutionListArchive<DoubleSolution> externalArchive;
	private boolean useHistoryArchive;
	private boolean evaluateExternalArchive;

	public CMODEDynamic(int maxEvaluations, int subpopulationSize, AbstractBoundedArchive<DoubleSolution> archive, 
			DoubleProblem problem, NonDominatedSolutionListArchive<DoubleSolution> externalArchive, 
			boolean useHistoryArchive, boolean evaluateExternalArchive, List<IScheduleRepairStrategy> repairStrategies) {
		super(maxEvaluations, subpopulationSize, archive, problem);

		this.externalArchive = externalArchive;
		this.evaluateExternalArchive = evaluateExternalArchive;
		this.useHistoryArchive = useHistoryArchive;
		if(!useHistoryArchive){
			historyArchive = archive;
		}
		
		if(repairStrategies != null) {
			for(IScheduleRepairStrategy repairStrategy: repairStrategies) {
				for(DoubleSolution s: historyArchive.getSolutionList()) {
					repairStrategy.repair(s);
				}
			}
		}
	}

	@Override
	public void run() {
		if(useHistoryArchive){
			for(DoubleSolution ds: historyArchive.getSolutionList()){
				evaluate(ds);
			}
		}
		if(externalArchive != null && evaluateExternalArchive) {
			for(DoubleSolution ds: externalArchive.getSolutionList()){
				evaluate(ds);
			}
		}
		
		super.run();
	}
	
	@Override
	protected List<DoubleSolution> executeDEforArchive() {
		if(externalArchive == null || externalArchive.size() == 0){
			return super.executeDEforArchive();
		}
		
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
			DoubleSolution mutantVector = generateMutantVectorExternalArchive(s, getArchive().get(x2), getArchive().get(x3), 
																		externalArchive.get(random.nextInt(externalArchive.size())),
																		mutationFactor);
			DoubleSolution trialVector = generateTrialVector(s, mutantVector, crossoverFactor);

			evaluate(trialVector);
			
			offspring.add(new ExtendedDefaultDoubleSolution((DefaultDoubleSolution)trialVector, 
															mutationFactor, crossoverFactor));
		}
		return offspring;
	}
	
	protected DoubleSolution generateMutantVectorExternalArchive(DoubleSolution solution, DoubleSolution individual2,
			DoubleSolution individual3, DoubleSolution individualArchive, double mutationFactor) {
		DoubleSolution mutant = (DoubleSolution)solution.copy();
		for (int i = 0; i < solution.getNumberOfVariables(); i++) {
			double value = solution.getVariableValue(i);
			double value2 = individual2.getVariableValue(i);
			double value3 = individual3.getVariableValue(i);
			double archiveValue = individualArchive.getVariableValue(i);
			
			double mutantValue = value + mutationFactor * (value2 - value3)
										+ mutationFactor * (archiveValue - value);
			mutant.setVariableValue(i, getMutantValueInsideBound(mutantValue, i));
		}
		return mutant;
	}
	
	@Override
	protected AbstractBoundedArchive<DoubleSolution> getArchive() {
		if(useHistoryArchive){
			return historyArchive;
		}
		return super.getArchive();
	}
	
}
