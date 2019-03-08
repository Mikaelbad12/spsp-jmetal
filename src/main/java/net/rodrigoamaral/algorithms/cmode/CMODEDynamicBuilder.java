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

	private NonDominatedSolutionListArchive<DoubleSolution> externalArchive;
	private boolean useHistoryArchive;
	private boolean evaluateExternalArchive;
	private List<IScheduleRepairStrategy> repairStrategies;
	
    public CMODEDynamicBuilder(DoubleProblem problem) {
        super(problem);
    }
    
    public CMODEDynamicBuilder setExternalArchive(NonDominatedSolutionListArchive<DoubleSolution> externalArchive) {
    	this.externalArchive = externalArchive;
        return this;
    }
    
    public CMODEDynamicBuilder setUseHistoryArchive(boolean useHistoryArchive) {
    	this.useHistoryArchive = useHistoryArchive;
        return this;
    }
    
    public CMODEDynamicBuilder setEvaluateExternalArchive(boolean evaluateExternalArchive) {
    	this.evaluateExternalArchive = evaluateExternalArchive;
        return this;
    }
    
    public CMODEDynamicBuilder setRepairStrategies(List<IScheduleRepairStrategy> repairStrategies) {
    	this.repairStrategies = repairStrategies;
        return this;
    }
    
    public CMODE build() {
    	return new CMODEDynamic(maxEvaluations, subpopulationSize, archive, problem, externalArchive, 
    							useHistoryArchive, evaluateExternalArchive, repairStrategies);
    }
    
    public CMODE buildDefault() {
    	return new CMODEBigDynamic(maxEvaluations, subpopulationSize, new SPEA2DensityArchive<>(archiveSize), problem, 
    							   externalArchive, useHistoryArchive, evaluateExternalArchive, repairStrategies);
    }
    
    public CMODE buildSDE() {
    	return new CMODESDEBigDynamic(maxEvaluations, subpopulationSize, new SDEArchive<>(archiveSize), problem, 
    								  externalArchive, useHistoryArchive, evaluateExternalArchive, repairStrategies);
    }
    
    public CMODE buildSDENorm() {
    	return new CMODESDENormBigDynamic(maxEvaluations, subpopulationSize, new SDEArchive<>(archiveSize, true), 
    									  problem, externalArchive, useHistoryArchive, evaluateExternalArchive, repairStrategies);
    }
    
    @SuppressWarnings("serial")
	class CMODEBigDynamic extends CMODEDynamic{

    	public CMODEBigDynamic(int maxEvaluations, int subpopulationSize, 
				AbstractBoundedArchive<DoubleSolution> archive,
				DoubleProblem problem, NonDominatedSolutionListArchive<DoubleSolution> externalArchive,
				boolean useHistoryArchive, boolean evaluateExternalArchive, List<IScheduleRepairStrategy> repairStrategies) {
			super(maxEvaluations, subpopulationSize, archive, problem, externalArchive, useHistoryArchive, evaluateExternalArchive, repairStrategies);
		}
    	
    }
    
    @SuppressWarnings("serial")
	class CMODESDEDynamic extends CMODEDynamic{

		public CMODESDEDynamic(int maxEvaluations, int subpopulationSize, 
								AbstractBoundedArchive<DoubleSolution> archive,
								DoubleProblem problem, NonDominatedSolutionListArchive<DoubleSolution> externalArchive,
								boolean useHistoryArchive, boolean evaluateExternalArchive,
								List<IScheduleRepairStrategy> repairStrategies) {
			super(maxEvaluations, subpopulationSize, archive, problem, externalArchive, useHistoryArchive, evaluateExternalArchive, repairStrategies);
		}
    	
    }
    
    @SuppressWarnings("serial")
	class CMODESDEBigDynamic extends CMODEDynamic{

		public CMODESDEBigDynamic(int maxEvaluations, int subpopulationSize, 
				AbstractBoundedArchive<DoubleSolution> archive,
				DoubleProblem problem, NonDominatedSolutionListArchive<DoubleSolution> externalArchive,
				boolean useHistoryArchive, boolean evaluateExternalArchive,
				List<IScheduleRepairStrategy> repairStrategies) {
			super(maxEvaluations, subpopulationSize, archive, problem, externalArchive, useHistoryArchive, evaluateExternalArchive, repairStrategies);
		}
    	
    }
    
    @SuppressWarnings("serial")
	class CMODESDENormDynamic extends CMODEDynamic{

		public CMODESDENormDynamic(int maxEvaluations, int subpopulationSize, 
									AbstractBoundedArchive<DoubleSolution> archive,
									DoubleProblem problem, NonDominatedSolutionListArchive<DoubleSolution> externalArchive,
									boolean useHistoryArchive, boolean evaluateExternalArchive, 
									List<IScheduleRepairStrategy> repairStrategies) {
			super(maxEvaluations, subpopulationSize, archive, problem, externalArchive, useHistoryArchive, evaluateExternalArchive, repairStrategies);
		}
		
    }
    
    @SuppressWarnings("serial")
	class CMODESDENormBigDynamic extends CMODEDynamic{

		public CMODESDENormBigDynamic(int maxEvaluations, int subpopulationSize, 
				AbstractBoundedArchive<DoubleSolution> archive,
				DoubleProblem problem, NonDominatedSolutionListArchive<DoubleSolution> externalArchive,
				boolean useHistoryArchive, boolean evaluateExternalArchive,
				List<IScheduleRepairStrategy> repairStrategies) {
			super(maxEvaluations, subpopulationSize, archive, problem, externalArchive, useHistoryArchive, evaluateExternalArchive, repairStrategies);
		}
    	
    }
}



