package net.rodrigoamaral.dspsp.adapters;

import java.io.FileNotFoundException;

import org.uma.jmetal.solution.DoubleSolution;

import net.rodrigoamaral.dspsp.config.DynamicProjectExtendedConfigLoader;
import net.rodrigoamaral.dspsp.exceptions.InvalidSolutionException;
import net.rodrigoamaral.dspsp.objectives.Efficiency;
import net.rodrigoamaral.dspsp.project.DynamicExtendedProject;
import net.rodrigoamaral.dspsp.project.DynamicProject;
import net.rodrigoamaral.dspsp.solution.DedicationMatrix;

public class JMetalDSPSPExtendedAdapter extends JMetalDSPSPAdapter {
	
	private enum Objective {
		DURATION(0), COST(1), ROBUSTNESS(2), SKILL(3), STABILITY(4);
		
		private int value;
		
		private Objective(int value){
			this.value = value;
		}
		
		public int getValue(){
			return value;
		}
	}

    private static final int[] STATIC_OBJECTIVES = {Objective.DURATION.getValue(), Objective.COST.getValue(), 
    												Objective.ROBUSTNESS.getValue(), Objective.SKILL.getValue()}; 
    private static final int[] DYNAMIC_OBJECTIVES = {Objective.DURATION.getValue(), Objective.COST.getValue(), 
    												Objective.ROBUSTNESS.getValue(), Objective.SKILL.getValue(), 
    												Objective.STABILITY.getValue()};

    private static final String PROBLEM_NAME = "DSPSPExtended";

    
    public JMetalDSPSPExtendedAdapter(String configFile) throws FileNotFoundException {
        super(configFile);
    }

	protected void createDynamicProjectFromFile(String configFile) throws FileNotFoundException {
		this.project = new DynamicProjectExtendedConfigLoader(configFile).createProject();
	}

    public JMetalDSPSPExtendedAdapter(DynamicProject project) {
        super(project);
    }
    
    protected int[] getStaticObjectives(){
    	return STATIC_OBJECTIVES;
    }
    
    protected int[] getDynamicObjectives(){
    	return DYNAMIC_OBJECTIVES;
    }

    public String getProblemName() {
        return PROBLEM_NAME;
    }

    protected void evaluateExtraObjectives(DoubleSolution solution, DedicationMatrix dm, Efficiency efficiency, double robustness) throws InvalidSolutionException {
    	solution.setObjective(Objective.SKILL.getValue(), ((DynamicExtendedProject)project).calculateSkill(dm));
	}

	protected void penalizeExtraObjectives(DoubleSolution solution, int missingSkills) {
		solution.setObjective(Objective.SKILL.getValue(), ((DynamicExtendedProject)project).penalizeSkill(missingSkills));
	}

	protected int getObjectiveStabilityValue() {
		return Objective.STABILITY.getValue();
	}

	protected int getObjectiveRobustnessValue() {
		return Objective.ROBUSTNESS.getValue();
	}

	protected int getObjectiveCostValue() {
		return Objective.COST.getValue();
	}

	protected int getObjectiveDurationValue() {
		return Objective.DURATION.getValue();
	}
	
}
