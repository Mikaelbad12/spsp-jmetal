package net.rodrigoamaral.algorithms.cmode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.archive.impl.AbstractBoundedArchive;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;

@SuppressWarnings({ "serial" })
public class CMODEDynamic extends CMODE {
	
	private static AbstractBoundedArchive<DoubleSolution> historyArchive;
	private NonDominatedSolutionListArchive<DoubleSolution> bigArchive;
	private boolean useHistoryArchive;

	public CMODEDynamic(int maxEvaluations, int subpopulationSize, AbstractBoundedArchive<DoubleSolution> archive, 
						DoubleProblem problem, boolean useHistoryArchive) {
		super(maxEvaluations, subpopulationSize, archive, problem);
		
		this.useHistoryArchive = useHistoryArchive;
		if(!useHistoryArchive){
			System.out.println("history archive");
			historyArchive = archive;
		}
	}
	
	public CMODEDynamic(int maxEvaluations, int subpopulationSize, AbstractBoundedArchive<DoubleSolution> archive, 
						DoubleProblem problem, NonDominatedSolutionListArchive<DoubleSolution> bigArchive, 
						boolean useHistoryArchive) {
		super(maxEvaluations, subpopulationSize, archive, problem);

		System.out.println("big archive");
		this.bigArchive = bigArchive;
		this.useHistoryArchive = useHistoryArchive;
		historyArchive = null;
	}

	//TODO implementar arquivo que não zera a cada nova execução do algoritmo mas é reavaliado e 
	//outro arquivo que guarda sem reavaliar e eh usado no deforarchive (vulgo arquivao)
	@Override
	public void run() {
		if(useHistoryArchive){
			for(DoubleSolution ds: historyArchive.getSolutionList()){
				evaluate(ds);
			}
		}
		
		super.run();
	}
	
	@Override
	protected List<DoubleSolution> executeDEforArchive() {
		//TODO validar com andre
		if(bigArchive == null || bigArchive.size() == 0){
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
			DoubleSolution mutantVector = generateMutantVectorBigArchive(s, getArchive().get(x2), getArchive().get(x3), 
																		bigArchive.get(random.nextInt(bigArchive.size())),
																		mutationFactor);
			DoubleSolution trialVector = generateTrialVector(s, mutantVector, crossoverFactor);

			evaluate(trialVector);
			
			offspring.add(new ExtendedDefaultDoubleSolution((DefaultDoubleSolution)trialVector, 
															mutationFactor, crossoverFactor));
		}
		return offspring;
	}
	
	protected DoubleSolution generateMutantVectorBigArchive(DoubleSolution solution, DoubleSolution individual2,
			DoubleSolution individual3, DoubleSolution individualArchive, double mutationFactor) {
		DoubleSolution mutant = (DoubleSolution)solution.copy();
		for (int i = 0; i < solution.getNumberOfVariables(); i++) {
			double value = solution.getVariableValue(i);
			double value2 = individual2.getVariableValue(i);
			double value3 = individual3.getVariableValue(i);
			double archiveValue = individualArchive.getVariableValue(i);
			
			mutant.setVariableValue(i, value + mutationFactor * (value2 - value3)
										+ mutationFactor * (archiveValue - value));
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
