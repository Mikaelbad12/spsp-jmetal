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
public class ExperimentSettings {

    private Integer numberOfRuns;
    private Integer objectiveEvaluations;
    private Integer numberOfSwarms;
    private Integer swarmSize;
    private List<String> instanceFiles;
    private List<String> algorithms;
    private Integer populationSize;
    private Double repairedSolutions;
    private Double histPropPreviousEventSolutions;
    private List<String> dynamicStrategies;
    private Boolean useExtendedIntance = Boolean.FALSE;
    private Integer subpopulationSize;
    private Boolean useExternalArchive = Boolean.FALSE;
    private Boolean useHistoryArchive = Boolean.FALSE;
    private Boolean evaluateExternalArchive = Boolean.FALSE;
    private Boolean useRapairStrategies = Boolean.FALSE;
    private Boolean reusePopulation = Boolean.FALSE;

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

    public Double getRepairedSolutions() {
        return repairedSolutions;
    }

    public void setRepairedSolutions(Double repairedSolutions) {
        this.repairedSolutions = repairedSolutions;
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

    public List<String> getDynamicStrategies() {
        return dynamicStrategies;
    }

    public void setDynamicStrategies(List<String> dynamicStrategies) {
        this.dynamicStrategies = dynamicStrategies;
    }
    
    public Boolean getUseExtendedIntance() {
    	return useExtendedIntance;
    }
    
    public void setUseExtendedIntance(Boolean useExtendedIntance) {
    	this.useExtendedIntance = useExtendedIntance;
    }
    
    public Integer getSubpopulationSize() {
    	return subpopulationSize;
    }
    
    public void setSubpopulationSize(Integer subpopulationSize) {
    	this.subpopulationSize = subpopulationSize;
    }
    
    public Boolean getUseExternalArchive() {
    	return useExternalArchive;
    }
    
    public void setUseBigArchive(Boolean useExternalArchive) {
    	this.useExternalArchive = useExternalArchive;
    }
    
    public Boolean getUseHistoryArchive() {
    	return useHistoryArchive;
    }
    
    public void setUseHistoryArchive(Boolean useHistoryArchive) {
    	this.useHistoryArchive = useHistoryArchive;
    }
    
    public Boolean getEvaluateExternalArchive() {
    	return evaluateExternalArchive;
    }
    
    public void setEvaluateExternalArchive(Boolean evaluateExternalArchive) {
    	this.evaluateExternalArchive = evaluateExternalArchive;
    }
    
    public Boolean getUseRapairStrategies() {
    	return useRapairStrategies;
    }
    
    public void setUseRapairStrategies(Boolean useRapairStrategies) {
    	this.useRapairStrategies = useRapairStrategies;
    }

	public Boolean getReusePopulation() {
		return reusePopulation;
	}

	public void setReusePopulation(Boolean reusePopulation) {
		this.reusePopulation = reusePopulation;
	}

	@Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString() + "{ " );
        sb.append("\n\tnumberOfRuns = " + numberOfRuns);
        sb.append("\n\tobjectiveEvaluations = " + objectiveEvaluations);
        sb.append("\n\tnumberOfSwarms = " + numberOfSwarms);
        sb.append("\n\tswarmSize = " + swarmSize);
        sb.append("\n\tinstanceFiles = " + instanceFiles);
        sb.append("\n\talgorithms = " + algorithms);
        sb.append("\n\tpopulationSize = " + populationSize);
        sb.append("\n\trepairedSolutions = " + repairedSolutions);
        sb.append("\n\thistPropPreviousEventSolutions = " + histPropPreviousEventSolutions);
        sb.append("\n\tdynamicStrategies = " + dynamicStrategies);
        sb.append("\n\tuseExtendedIntance = " + useExtendedIntance);
        sb.append("\n\tsubpopulationSize = " + subpopulationSize);
        sb.append("\n}");
        return sb.toString();
    }
}
