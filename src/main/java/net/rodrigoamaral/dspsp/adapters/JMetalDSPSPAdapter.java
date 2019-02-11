package net.rodrigoamaral.dspsp.adapters;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.solution.DoubleSolution;

import net.rodrigoamaral.dspsp.config.DynamicProjectConfigLoader;
import net.rodrigoamaral.dspsp.constraints.AllTasksAllocatedConstraint;
import net.rodrigoamaral.dspsp.constraints.DSPSPConstraintEvaluator;
import net.rodrigoamaral.dspsp.constraints.IConstraintEvaluator;
import net.rodrigoamaral.dspsp.constraints.MaximumHeadcountConstraint;
import net.rodrigoamaral.dspsp.constraints.NoEmployeeOverworkConstraint;
import net.rodrigoamaral.dspsp.constraints.TaskSkillsConstraint;
import net.rodrigoamaral.dspsp.exceptions.InvalidSolutionException;
import net.rodrigoamaral.dspsp.objectives.Efficiency;
import net.rodrigoamaral.dspsp.project.DynamicProject;
import net.rodrigoamaral.dspsp.solution.DedicationMatrix;
import net.rodrigoamaral.logging.SPSPLogger;

/**
 *
 * Adapts Project interface to a JMetal AbstractDoubleProblem
 * that implements ConstrainedProble&lt;DoubleSolution&gt;. {@link JMetalDSPSPAdapter}
 * must provide all the methods needed for these interfaces.
 *
 * @author Rodrigo Amaral
 *
 */
public class JMetalDSPSPAdapter {

	private enum Objective {
		DURATION(0), COST(1), ROBUSTNESS(2), STABILITY(3);
		
		private int value;
		
		private Objective(int value){
			this.value = value;
		}
		
		public int getValue(){
			return value;
		}
	}
	
    private static final int[] STATIC_OBJECTIVES = {Objective.DURATION.getValue(), Objective.COST.getValue(), 
    												Objective.ROBUSTNESS.getValue()};
    private static final int[] DYNAMIC_OBJECTIVES = {Objective.DURATION.getValue(), Objective.COST.getValue(), 
    												Objective.ROBUSTNESS.getValue(), Objective.STABILITY.getValue()};

    private static final String PROBLEM_NAME = "DSPSP";
    private static final double LOWER_LIMIT = 0.0;
    private static final double UPPER_LIMIT = 1.0;
    private static final double MAX_OVERWORK = 0.2;

    protected DynamicProject project;
    protected IConstraintEvaluator constraintEvaluator;
    private int[] objectives;

    private SolutionConverter converter;
    
    /**
     * Creates a {@link DynamicProject} instance and evaluate all objectives and constraints
     * needed for SPSP.
     *
     * @param configFile Relative path to the configuration file
     * @throws FileNotFoundException
     */
    public JMetalDSPSPAdapter(String configFile) throws FileNotFoundException {
        createDynamicProjectFromFile(configFile);
        init();
    }

	protected void createDynamicProjectFromFile(String configFile) throws FileNotFoundException {
		this.project = new DynamicProjectConfigLoader(configFile).createProject();
	}

    public JMetalDSPSPAdapter(DynamicProject project) {
        this.project = project;
        initDynamic();
    }
    
    private void init() {
        this.objectives = getStaticObjectives();
        this.constraintEvaluator = new DSPSPConstraintEvaluator()
                .addConstraint(new NoEmployeeOverworkConstraint())
                .addConstraint(new AllTasksAllocatedConstraint())
                .addConstraint(new MaximumHeadcountConstraint())
                .addConstraint(new TaskSkillsConstraint())
                ;
        this.converter = new SolutionConverter(this.project);
    }

    private void initDynamic() {
        init();
        this.objectives = getDynamicObjectives();
    }
    
    protected int[] getStaticObjectives(){
    	return STATIC_OBJECTIVES;
    }
    
    protected int[] getDynamicObjectives(){
    	return DYNAMIC_OBJECTIVES;
    }

    public DynamicProject getProject() {
        return project;
    }

    public String getProblemName() {
        return PROBLEM_NAME;
    }

    public int getNumberOfVariables() {
        return project.size();
    }

    public int getNumberOfObjectives() {
        return objectives.length;
    }

    private List<Double> populateLimitList(double value) {
        List<Double> limit = new ArrayList<>(getNumberOfVariables());
        for (int i = 0; i < getNumberOfVariables(); i++) {
            limit.add(value);
        }
        return limit;
    }

    public List<Double> getLowerLimit() {
        return populateLimitList(LOWER_LIMIT);
    }

    public List<Double> getUpperLimit() {
        return populateLimitList(UPPER_LIMIT + MAX_OVERWORK);
    }
    
    /**
     * Evaluates all objectives registered by the objectiveEvaluator in the
     * constructor. Before evaluation, it repairs the repairedSolution according to
     * the constraints registered by the constraintEvaluator in the
     * constructor. Objectives are penalized if there is any skill missing
     * in the available emplyee team.
     *
     * @param solution
     * @return repaired repairedSolution
     */
    public DoubleSolution evaluateObjectives(DoubleSolution solution) {

        DedicationMatrix dm = repair(solution);
        int missingSkills = missingSkills();

        if (missingSkills > 0) {
        	System.out.println("---------situacao insoluvel-----------"); //TODO verificando quais pontos tornam schedule insoluvel
            solution.setObjective(getObjectiveDurationValue(), project.penalizeDuration(missingSkills));
            solution.setObjective(getObjectiveCostValue(), project.penalizeCost(missingSkills));
            solution.setObjective(getObjectiveRobustnessValue(), project.penalizeRobustness(missingSkills));

            penalizeExtraObjectives(solution, missingSkills);
            
            if (mustIncludeStability(solution)) {
                solution.setObjective(getObjectiveStabilityValue(), project.penalizeStability(missingSkills));
            }
            

        } else {

            try {
                Efficiency efficiency = project.evaluateEfficiency(dm);
                double robustness = project.calculateRobustness(dm, efficiency);

                solution.setObjective(getObjectiveDurationValue(), efficiency.duration);
                solution.setObjective(getObjectiveCostValue(), efficiency.cost);
                solution.setObjective(getObjectiveRobustnessValue(), robustness);

                evaluateExtraObjectives(solution, dm, efficiency, robustness);
                
                if (mustIncludeStability(solution)) {
                    double stability = project.calculateStability(dm);
                    solution.setObjective(getObjectiveStabilityValue(), stability);
                }
                
            } catch (InvalidSolutionException e) {
            	
                SPSPLogger.trace("Penalizing invalid repairedSolution: " + dm);

                solution.setObjective(getObjectiveDurationValue(), project.penalizeDuration(1));
                solution.setObjective(getObjectiveCostValue(), project.penalizeCost(1));
                solution.setObjective(getObjectiveRobustnessValue(), project.penalizeRobustness(1));

                penalizeExtraObjectives(solution, 1);
                
                if (mustIncludeStability(solution)) {
                    solution.setObjective(getObjectiveStabilityValue(), project.penalizeStability(1));
                }
                
            }
        }

        return solution;
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
    
    protected void evaluateExtraObjectives(DoubleSolution solution, DedicationMatrix dm, Efficiency efficiency, double robustness) throws InvalidSolutionException {
	}

	protected void penalizeExtraObjectives(DoubleSolution solution, int missingSkills) {
	}

	protected boolean mustIncludeStability(DoubleSolution solution) {
        return project.getPreviousSchedule() != null && 
        		solution.getNumberOfObjectives() > getStaticObjectives().length;
    }

    private DedicationMatrix repair(DoubleSolution solution) {
        return constraintEvaluator.repair(converter.convert(solution), project);
    }

    public int getNumberOfConstraints() {
        return constraintEvaluator.size();
    }
    
    public int missingSkills() {
        return project.missingSkills();
    }

}
