package net.rodrigoamaral.dspsp.solution;


import net.rodrigoamaral.dspsp.experiment.ExperimentSettings;
import net.rodrigoamaral.dspsp.solution.mutation.DSPSPRepairMutation;
import net.rodrigoamaral.dspsp.solution.repair.IScheduleRepairStrategy;
import net.rodrigoamaral.logging.SPSPLogger;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DynamicPopulationCreatorExtended {

    private DoubleProblem problem;
    private SchedulingHistory history;
    private int size;
    private double propRepairedSolutions;
    private double propPreviousEventSolutions;
    private List<IScheduleRepairStrategy> repairStrategies;
    private List<String> dynamicStrategies;

//    REFACTOR: Remove string literals
    public DynamicPopulationCreatorExtended(DoubleProblem problem, SchedulingHistory history, ExperimentSettings settings, 
    										String algorithmID, List<IScheduleRepairStrategy> repairStrategies) {
        this.problem = problem;
        this.history = history;
        this.dynamicStrategies = settings.getDynamicStrategies();
        this.repairStrategies = repairStrategies;
        this.propRepairedSolutions = settings.getRepairedSolutions();
        this.propPreviousEventSolutions = settings.getHistPropPreviousEventSolutions();
        if (propRepairedSolutions + propPreviousEventSolutions > 1.0) {
            throw new IllegalArgumentException("Sum of repairedSolution proportions must not exceed 1.0");
        }
        if (algorithmID.equals("MS2MO")
                || algorithmID.equals("MS2MODynamic")
                ) {
            this.size = settings.getSwarmSize();
        } else {
            this.size = settings.getPopulationSize();
        }
    }

    public List<DoubleSolution> create(int event) {
        List<DoubleSolution> population = new ArrayList<>(size);

        if (repairStrategies != null && dynamicStrategies.contains("proactive_repair")) {
            SPSPLogger.debug("Using proactive repair");
            population.addAll(repairedSolutions());
        }

        if (dynamicStrategies.contains("history_info")) {
            SPSPLogger.debug("Using history info");
            population.addAll(getRandomSublist(history.getPrevious(event), propPreviousEventSolutions));
        }

        population.addAll(getRandomSolutions(size - population.size()));

        SPSPLogger.debug("Initial population created (size " + population.size() + ")");
        return population;
    }

    private List<DoubleSolution> repairedSolutions() {
        List<DoubleSolution> repairedSolutions = new ArrayList<>();

        List<DoubleSolution> repairSolutions = new ArrayList<>(this.repairStrategies.size());
        for(IScheduleRepairStrategy repairStrategy: this.repairStrategies){
	        DoubleSolution seed = repairStrategy.repair();
	
	        for (int i = 0; i < size; i++) {
	            repairedSolutions.add(getMutatedCopy(seed));
	        }
	
	        repairedSolutions = getRandomSublist(repairedSolutions, propRepairedSolutions);
	        repairedSolutions.set(0, seed);
	        repairSolutions.add(seed);
        }

        for(int i = 0; i < repairSolutions.size(); i++){
        	repairedSolutions.set(i, repairSolutions.get(i));
        }
        
        return repairedSolutions;
    }

    private DoubleSolution getMutatedCopy(DoubleSolution seed) {
        double mutationProbability = 1.0;
        double mutationDistributionIndex = 20.0 ;
        MutationOperator<DoubleSolution> mutation;
        mutation = new DSPSPRepairMutation(mutationProbability, mutationDistributionIndex);

        return mutation.execute(new DefaultDoubleSolution((DefaultDoubleSolution) seed));
    }

    @SuppressWarnings("finally")
	private List<DoubleSolution> getRandomSublist(List<DoubleSolution> list, double proportionalSize) {
        List<DoubleSolution> randomSublist = new ArrayList<>();

        int n = (int) Math.round(size * proportionalSize);
        Collections.shuffle(list);

        try {
            randomSublist = new ArrayList<>(list.subList(0, n));
        } catch (IndexOutOfBoundsException iobe) {
            SPSPLogger.warning(iobe.getMessage() + " - List size: " + list.size());
            iobe.printStackTrace();
        } finally {
            return randomSublist;
        }

    }

    private List<DoubleSolution> getRandomSolutions(int listSize) {
        List<DoubleSolution> randomList = new ArrayList<>(listSize);
        for (int i = 0; i < listSize; i++) {
            randomList.add(new DefaultDoubleSolution(problem));
        }

        return randomList;
    }
}
