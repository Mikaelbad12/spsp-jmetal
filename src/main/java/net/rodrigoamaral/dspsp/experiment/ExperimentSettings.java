package net.rodrigoamaral.dspsp.experiment;

import java.util.List;

/**
 * Experiment settings for DSPSP.
 *
 * Instances of this class are populated by the settings read from
 * a JSON file.
 *
 * @author Rodrigo Amaral
 */
@SuppressWarnings("unused")
public class ExperimentSettings {

    private Integer numberOfRuns;
    private Integer objectiveEvaluations;
    private Integer numberOfSwarms;
    private Integer swarmSize;
    private List<String> instanceFiles;
    private List<String> algorithms;
    private Integer populationSize;
    private Double histPropGlobalSolutions;
    private Double histPropPreviousEventSolutions;

    public Integer getNumberOfRuns() {
        return numberOfRuns;
    }

    public void setNumberOfRuns(Integer numberOfRuns) {
        this.numberOfRuns = numberOfRuns;
    }

    public Integer getObjectiveEvaluations() {
        return objectiveEvaluations;
    }

    public void setObjectiveEvaluations(Integer objectiveEvaluations) {
        this.objectiveEvaluations = objectiveEvaluations;
    }

    public Integer getNumberOfSwarms() {
        return numberOfSwarms;
    }

    public void setNumberOfSwarms(Integer numberOfSwarms) {
        this.numberOfSwarms = numberOfSwarms;
    }

    public Integer getSwarmSize() {
        return swarmSize;
    }

    public void setSwarmSize(Integer swarmSize) {
        this.swarmSize = swarmSize;
    }

    public Integer getPopulationSize() {
        return populationSize;
    }

    public void setPopulationSize(Integer populationSize) {
        this.populationSize = populationSize;
    }

    public Double getHistPropGlobalSolutions() {
        return histPropGlobalSolutions;
    }

    public void setHistPropGlobalSolutions(Double histPropGlobalSolutions) {
        this.histPropGlobalSolutions = histPropGlobalSolutions;
    }

    public Double getHistPropPreviousEventSolutions() {
        return histPropPreviousEventSolutions;
    }

    public void setHistPropPreviousEventSolutions(Double histPropPreviousEventSolutions) {
        this.histPropPreviousEventSolutions = histPropPreviousEventSolutions;
    }

    public List<String> getInstanceFiles() {
        return instanceFiles;
    }

    public void setInstanceFiles(List<String> instanceFiles) {
        this.instanceFiles = instanceFiles;
    }

    public List<String> getAlgorithms() {
        return algorithms;
    }

    public void setAlgorithms(List<String> algorithms) {
        this.algorithms = algorithms;
    }
}
