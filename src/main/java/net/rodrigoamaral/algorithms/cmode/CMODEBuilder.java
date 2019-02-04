package net.rodrigoamaral.algorithms.cmode;

import java.util.List;

import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmBuilder;
import org.uma.jmetal.util.archive.impl.AbstractBoundedArchive;

import net.rodrigoamaral.algorithms.ISwarm;
import net.rodrigoamaral.algorithms.ms2mo.MS2MO;
import net.rodrigoamaral.jmetal.util.archive.impl.SDEArchive;
import net.rodrigoamaral.jmetal.util.archive.impl.SPEA2DensityArchive;

@SuppressWarnings({"rawtypes", "unused"})
public class CMODEBuilder implements AlgorithmBuilder {

    protected DoubleProblem problem;
    protected int maxEvaluations;
    protected int archiveSize;
    protected int subpopulationSize;
    protected AbstractBoundedArchive<DoubleSolution> archive;

    public CMODEBuilder(DoubleProblem problem) {
        setDefaultParams(problem);
    }

    private void setDefaultParams(DoubleProblem problem) {
        this.problem = problem;
        this.archiveSize = 100;
        this.subpopulationSize = 20;
    }

    public CMODEBuilder setMaxEvaluations(int maxEvaluations) {
        this.maxEvaluations = maxEvaluations;
        return this;
    }
    
    public CMODEBuilder setArchiveSize(int archiveSize) {
        this.archiveSize = archiveSize;
        return this;
    }
    
    public CMODEBuilder setSubpopulationSize(int subpopulationSize) {
        this.subpopulationSize = subpopulationSize;
        return this;
    }
    
    public CMODEBuilder setArchive(AbstractBoundedArchive<DoubleSolution> archive) {
        this.archive = archive;
        return this;
    }

    public CMODE build() {
        return new CMODE(maxEvaluations, subpopulationSize, archive, problem);
    }
    
    public CMODE buildDefault() {
        return new CMODE(maxEvaluations, subpopulationSize, new SPEA2DensityArchive<>(archiveSize), problem);
    }
    
    public CMODE buildSDE() {
        return new CMODESDE(maxEvaluations, subpopulationSize, new SDEArchive<>(archiveSize), problem);
    }
    
    public CMODE buildSDENorm() {
        return new CMODESDENorm(maxEvaluations, subpopulationSize, new SDEArchive<>(archiveSize, true), problem);
    }
    
    @SuppressWarnings("serial")
	class CMODESDE extends CMODE{

		public CMODESDE(int maxEvaluations, int subpopulationSize, AbstractBoundedArchive<DoubleSolution> archive,
				DoubleProblem problem) {
			super(maxEvaluations, subpopulationSize, archive, problem);
		}
    	
    }
    
    @SuppressWarnings("serial")
	class CMODESDENorm extends CMODE{

		public CMODESDENorm(int maxEvaluations, int subpopulationSize, AbstractBoundedArchive<DoubleSolution> archive,
				DoubleProblem problem) {
			super(maxEvaluations, subpopulationSize, archive, problem);
		}
    	
    }
}



