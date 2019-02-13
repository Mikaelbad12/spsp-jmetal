package net.rodrigoamaral.algorithms.cmode;

import java.util.List;

import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.archive.impl.AbstractBoundedArchive;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;

import net.rodrigoamaral.dspsp.solution.repair.IScheduleRepairStrategy;
import net.rodrigoamaral.jmetal.util.archive.impl.SDEArchive;
import net.rodrigoamaral.jmetal.util.archive.impl.SPEA2DensityArchive;

public class CMODEDynamicBuilder extends CMODEBuilder {

	private NonDominatedSolutionListArchive<DoubleSolution> bigArchive;
	private boolean useHistoryArchive;
	private boolean evaluateBigArchive;
	private List<IScheduleRepairStrategy> repairStrategies;
	
    public CMODEDynamicBuilder(DoubleProblem problem) {
        super(problem);
    }
    
    public CMODEDynamicBuilder setBigArchive(NonDominatedSolutionListArchive<DoubleSolution> bigArchive) {
    	this.bigArchive = bigArchive;
        return this;
    }
    
    public CMODEDynamicBuilder setUseHistoryArchive(boolean useHistoryArchive) {
    	this.useHistoryArchive = useHistoryArchive;
        return this;
    }
    
    public CMODEDynamicBuilder setEvaluateBigArchive(boolean evaluateBigArchive) {
    	this.evaluateBigArchive = evaluateBigArchive;
        return this;
    }
    
    public CMODEDynamicBuilder setRepairStrategies(List<IScheduleRepairStrategy> repairStrategies) {
    	this.repairStrategies = repairStrategies;
        return this;
    }
    
    public CMODE build() {
    	if(bigArchive == null){
    		return new CMODEDynamic(maxEvaluations, subpopulationSize, archive, problem, useHistoryArchive, repairStrategies);
    	}
    	return new CMODEDynamic(maxEvaluations, subpopulationSize, archive, problem, bigArchive, useHistoryArchive, evaluateBigArchive);
    }
    
    public CMODE buildDefault() {
    	if(bigArchive == null){
    		return new CMODEDynamic(maxEvaluations, subpopulationSize, new SPEA2DensityArchive<>(archiveSize), 
    								problem, useHistoryArchive, repairStrategies);
    	}
    	return new CMODEBigDynamic(maxEvaluations, subpopulationSize, new SPEA2DensityArchive<>(archiveSize), problem, 
    							   bigArchive, useHistoryArchive, evaluateBigArchive);
    }
    
    public CMODE buildSDE() {
    	if(bigArchive == null){
    		return new CMODESDEDynamic(maxEvaluations, subpopulationSize, new SDEArchive<>(archiveSize), problem, 
    									useHistoryArchive, repairStrategies);
    	}
    	return new CMODESDEBigDynamic(maxEvaluations, subpopulationSize, new SDEArchive<>(archiveSize), problem, 
    								  bigArchive, useHistoryArchive, evaluateBigArchive);
    }
    
    public CMODE buildSDENorm() {
    	if(bigArchive == null){
    		return new CMODESDENormDynamic(maxEvaluations, subpopulationSize, new SDEArchive<>(archiveSize, true), 
    										problem, useHistoryArchive, repairStrategies);
    	}
    	return new CMODESDENormBigDynamic(maxEvaluations, subpopulationSize, new SDEArchive<>(archiveSize, true), 
    									  problem, bigArchive, useHistoryArchive, evaluateBigArchive);
    }
    
    @SuppressWarnings("serial")
	class CMODEBigDynamic extends CMODEDynamic{

    	public CMODEBigDynamic(int maxEvaluations, int subpopulationSize, 
				AbstractBoundedArchive<DoubleSolution> archive,
				DoubleProblem problem, NonDominatedSolutionListArchive<DoubleSolution> bigArchive,
				boolean useHistoryArchive) {
			super(maxEvaluations, subpopulationSize, archive, problem, bigArchive, useHistoryArchive);
		}
    	
    	public CMODEBigDynamic(int maxEvaluations, int subpopulationSize, 
				AbstractBoundedArchive<DoubleSolution> archive,
				DoubleProblem problem, NonDominatedSolutionListArchive<DoubleSolution> bigArchive,
				boolean useHistoryArchive, boolean evaluateBigArchive) {
			super(maxEvaluations, subpopulationSize, archive, problem, bigArchive, useHistoryArchive, evaluateBigArchive);
		}
    	
    }
    
    @SuppressWarnings("serial")
	class CMODESDEDynamic extends CMODEDynamic{

		public CMODESDEDynamic(int maxEvaluations, int subpopulationSize, 
								AbstractBoundedArchive<DoubleSolution> archive,
								DoubleProblem problem, boolean useHistoryArchive, 
								List<IScheduleRepairStrategy> repairStrategies) {
			super(maxEvaluations, subpopulationSize, archive, problem, useHistoryArchive, repairStrategies);
		}
    	
    }
    
    @SuppressWarnings("serial")
	class CMODESDEBigDynamic extends CMODEDynamic{

		public CMODESDEBigDynamic(int maxEvaluations, int subpopulationSize, 
				AbstractBoundedArchive<DoubleSolution> archive,
				DoubleProblem problem, NonDominatedSolutionListArchive<DoubleSolution> bigArchive,
				boolean useHistoryArchive) {
			super(maxEvaluations, subpopulationSize, archive, problem, bigArchive, useHistoryArchive);
		}
		
		public CMODESDEBigDynamic(int maxEvaluations, int subpopulationSize, 
				AbstractBoundedArchive<DoubleSolution> archive,
				DoubleProblem problem, NonDominatedSolutionListArchive<DoubleSolution> bigArchive,
				boolean useHistoryArchive, boolean evaluateBigArchive) {
			super(maxEvaluations, subpopulationSize, archive, problem, bigArchive, useHistoryArchive, evaluateBigArchive);
		}
    	
    }
    
    @SuppressWarnings("serial")
	class CMODESDENormDynamic extends CMODEDynamic{

		public CMODESDENormDynamic(int maxEvaluations, int subpopulationSize, 
									AbstractBoundedArchive<DoubleSolution> archive,
									DoubleProblem problem, boolean useHistoryArchive, 
									List<IScheduleRepairStrategy> repairStrategies) {
			super(maxEvaluations, subpopulationSize, archive, problem, useHistoryArchive, repairStrategies);
		}
		
    }
    
    @SuppressWarnings("serial")
	class CMODESDENormBigDynamic extends CMODEDynamic{

		public CMODESDENormBigDynamic(int maxEvaluations, int subpopulationSize, 
				AbstractBoundedArchive<DoubleSolution> archive,
				DoubleProblem problem, NonDominatedSolutionListArchive<DoubleSolution> bigArchive,
				boolean useHistoryArchive) {
			super(maxEvaluations, subpopulationSize, archive, problem, bigArchive, useHistoryArchive);
		}
		
		public CMODESDENormBigDynamic(int maxEvaluations, int subpopulationSize, 
				AbstractBoundedArchive<DoubleSolution> archive,
				DoubleProblem problem, NonDominatedSolutionListArchive<DoubleSolution> bigArchive,
				boolean useHistoryArchive, boolean evaluateBigArchive) {
			super(maxEvaluations, subpopulationSize, archive, problem, bigArchive, useHistoryArchive, evaluateBigArchive);
		}
    	
    }
}



