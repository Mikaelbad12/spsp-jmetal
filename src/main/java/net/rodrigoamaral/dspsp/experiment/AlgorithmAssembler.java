package net.rodrigoamaral.dspsp.experiment;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.gde3.GDE3Builder;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.pseudorandom.impl.MersenneTwisterGenerator;

import net.rodrigoamaral.algorithms.ISwarm;
import net.rodrigoamaral.algorithms.cmode.CMODEBuilder;
import net.rodrigoamaral.algorithms.cmode.CMODEDynamicBuilder;
import net.rodrigoamaral.algorithms.gde3.GDE3DynamicBuilder;
import net.rodrigoamaral.algorithms.moead.MOEADDEBuilder;
import net.rodrigoamaral.algorithms.moead.MOEADDEDynamicBuilder;
import net.rodrigoamaral.algorithms.ms2mo.MS2MOBuilder;
import net.rodrigoamaral.algorithms.nsgaii.NSGAIIDynamicBuilder;
import net.rodrigoamaral.algorithms.nsgaiii.NSGAIIIBuilder;
import net.rodrigoamaral.algorithms.nsgaiii.NSGAIIIDynamicBuilder;
import net.rodrigoamaral.algorithms.smpso.SMPSOBuilder;
import net.rodrigoamaral.algorithms.smpso.SMPSODynamicBuilder;
import net.rodrigoamaral.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

/**
 * Assembles an algorithm from given parameters for experiment purposes.
 *
 * @author Rodrigo Amaral
 */
public class AlgorithmAssembler {

    private final String algorithmID;
    private List<DoubleSolution> initialPopulation;
    private int objectiveEvaluations = 12000;
    private int numberOfSwarms = 30;
    private int swarmSize = 160;
    private final int maxMultiSwarmIterations;
    private int populationSize = 100;
    private NonDominatedSolutionListArchive<DoubleSolution> bigArchive;
    private boolean useHistoryArchive;

    public AlgorithmAssembler(final String algorithmID, ExperimentSettings settings) {
        this.objectiveEvaluations = getValueOrDefault(settings.getObjectiveEvaluations(), objectiveEvaluations);
        this.numberOfSwarms = getValueOrDefault(settings.getNumberOfSwarms(), numberOfSwarms);
        this.swarmSize = getValueOrDefault(settings.getSwarmSize(), swarmSize);
        this.algorithmID = algorithmID;
        this.populationSize = getValueOrDefault(settings.getPopulationSize(), populationSize);
        maxMultiSwarmIterations = getMaxMultiSwarmIterations();
    }
    
    private int getValueOrDefault(Integer o, int defaultValue){
    	return o != null ? o : defaultValue;
    }

    private int getMaxMultiSwarmIterations() {
        return (objectiveEvaluations / numberOfSwarms) / swarmSize;
    }

    private int getMaxIterations() {
        return objectiveEvaluations / populationSize;
    }

    public String getAlgorithmID() {
        return algorithmID;
    }

    public Algorithm<List<DoubleSolution>> assemble(Problem<DoubleSolution> problem, 
    												List<DoubleSolution> initialPopulation) {
        this.initialPopulation = initialPopulation;
        return assemble(problem);
    }
    
    public Algorithm<List<DoubleSolution>> assemble(Problem<DoubleSolution> problem, 
    												NonDominatedSolutionListArchive<DoubleSolution> bigArchive) {
        this.bigArchive = bigArchive;
        return assemble(problem);
    }
    
    public Algorithm<List<DoubleSolution>> assemble(Problem<DoubleSolution> problem, 
													NonDominatedSolutionListArchive<DoubleSolution> bigArchive,
													boolean useHistoryArchive) {
    	this.bigArchive = bigArchive;
    	this.useHistoryArchive = useHistoryArchive;
    	return assemble(problem);
    }
    
    public Algorithm<List<DoubleSolution>> assemble(Problem<DoubleSolution> problem, 
													boolean useHistoryArchive) {
    	this.useHistoryArchive = useHistoryArchive;
    	return assemble(problem);
    }

    public void setObjectiveEvaluations(int objectiveEvaluations) {
        this.objectiveEvaluations = objectiveEvaluations;
    }

    public int getObjectiveEvaluations() {
        return objectiveEvaluations;
    }

    @SuppressWarnings("unchecked")
	public Algorithm<List<DoubleSolution>> assemble(Problem<DoubleSolution> problem) {
        CrossoverOperator<DoubleSolution> crossover;
        MutationOperator<DoubleSolution> mutation;
        SelectionOperator<List<DoubleSolution>, DoubleSolution> selection;

        double crossoverProbability = 0.9 ;
        double crossoverDistributionIndex = 20.0 ;

        crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex) ;

        double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
        double mutationDistributionIndex = 20.0 ;

        mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex) ;

        selection = new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<>());

        // REFACTOR: Make algorithm builder instantiation dynamic
        if ("NSGAII".equals(algorithmID.toUpperCase())) {
            return new NSGAIIBuilder<>(problem, crossover, mutation)
                    .setSelectionOperator(selection)
                    .setMaxEvaluations(getMaxIterations())
                    .setPopulationSize(populationSize)
                    .build();
        } else if ("NSGAIIDYNAMIC".equals(algorithmID.toUpperCase())) {
                return new NSGAIIDynamicBuilder(problem, crossover, mutation)
                        .setInitialPopulation(initialPopulation)
                        .setSelectionOperator(selection)
                        .setMaxEvaluations(getMaxIterations())
                        .setPopulationSize(populationSize)
                        .build();
        } else if ("SMPSO".equals(algorithmID.toUpperCase())) {
            BoundedArchive<DoubleSolution> archive = new CrowdingDistanceArchive<DoubleSolution>(populationSize) ;
            return new SMPSOBuilder((DoubleProblem) problem, archive)
                    .setMutation(mutation)
                    .setMaxIterations(getMaxIterations())
                    .setSwarmSize(populationSize)
                    .setRandomGenerator(new MersenneTwisterGenerator())
                    .setSolutionListEvaluator(new SequentialSolutionListEvaluator<DoubleSolution>())
                    .build();
        } else if ("SMPSODYNAMIC".equals(algorithmID.toUpperCase())) {
            BoundedArchive<DoubleSolution> archive = new CrowdingDistanceArchive<DoubleSolution>(populationSize) ;
            return new SMPSODynamicBuilder((DoubleProblem) problem, archive)
                    .setInitialPopulation(initialPopulation)
                    .setMutation(mutation)
                    .setMaxIterations(getMaxIterations())
                    .setSwarmSize(populationSize)
                    .setRandomGenerator(new MersenneTwisterGenerator())
                    .setSolutionListEvaluator(new SequentialSolutionListEvaluator<DoubleSolution>())
                    .build();
        }  else if ("MS2MO".equals(algorithmID.toUpperCase())) {

            List<ISwarm> swarms = createSwarms((DoubleProblem) problem, mutation, null);

            return new MS2MOBuilder((DoubleProblem)problem)
                    .addSwarms(swarms)
                    .setMaxIterations(maxMultiSwarmIterations)
                    .build();

        } else if ("MS2MODYNAMIC".equals(algorithmID.toUpperCase())) {
//            mutation = new DSPSPRepairMutation();

            List<ISwarm> swarms = createSwarms((DoubleProblem) problem, mutation, initialPopulation);

            return new MS2MOBuilder((DoubleProblem)problem)
                    .addSwarms(swarms)
                    .setMaxIterations(maxMultiSwarmIterations)
                    .build();
        } else if("GDE3".equals(algorithmID.toUpperCase())){
        	return new GDE3Builder((DoubleProblem) problem)
        			.setPopulationSize(populationSize)
        			.setMaxEvaluations(getMaxIterations())
        			.build();
        } else if("GDE3DYNAMIC".equals(algorithmID.toUpperCase())){
        	return new GDE3DynamicBuilder((DoubleProblem) problem)
        			.setInitialPopulation(initialPopulation)
        			.setPopulationSize(populationSize)
        			.setMaxEvaluations(getMaxIterations())
        			.build();
        } else if("MOEADDE".equals(algorithmID.toUpperCase())){
        	return new MOEADDEBuilder(problem)
        			.setPopulationSize(populationSize)
        			.setMaxEvaluations(getMaxIterations())
        			.setNeighborhoodSelectionProbability(0.9)
        			.setDataDirectory("MOEAD_Weights")
        			.build();
        } else if("MOEADDEDYNAMIC".equals(algorithmID.toUpperCase())){
        	return new MOEADDEDynamicBuilder(problem)
        			.setInitialPopulation(initialPopulation)
        			.setPopulationSize(populationSize)
        			.setMaxEvaluations(getMaxIterations())
        			.setNeighborhoodSelectionProbability(0.9)
        			.setDataDirectory("MOEAD_Weights")
        			.build();
        }else if ("NSGAIII".equals(algorithmID.toUpperCase())) {
            return new NSGAIIIBuilder(problem)
            		.setCrossoverOperator(crossover)
            		.setMutationOperator(mutation)
                    .setSelectionOperator(selection)
                    .setMaxIterations(getMaxIterations())
                    .setPopulationSize(populationSize)
                    .build();
        } else if ("NSGAIIIDYNAMIC".equals(algorithmID.toUpperCase())) {
        	return new NSGAIIIDynamicBuilder(problem)
        			.setInitialPopulation(initialPopulation)
        			.setCrossoverOperator(crossover)
        			.setMutationOperator(mutation)
        			.setSelectionOperator(selection)
        			.setMaxIterations(getMaxIterations())
        			.setPopulationSize(populationSize)
        			.build();
        } else if ("CMODE".equals(algorithmID.toUpperCase())){
        	return new CMODEBuilder((DoubleProblem)problem)
        			.setMaxEvaluations(getMaxIterations())
        			.buildDefault();
        } else if ("CMODESDE".equals(algorithmID.toUpperCase())){
        	return new CMODEBuilder((DoubleProblem)problem)
        			.setMaxEvaluations(getMaxIterations())
        			.buildSDE();
        } else if ("CMODESDENORM".equals(algorithmID.toUpperCase())){
        	return new CMODEBuilder((DoubleProblem)problem)
        			.setMaxEvaluations(getMaxIterations())
        			.buildSDENorm();
        } else if ("CMODEDYNAMIC".equals(algorithmID.toUpperCase())){
        	return new CMODEDynamicBuilder((DoubleProblem)problem)
        			.setBigArchive(bigArchive)
        			.setUseHistoryArchive(useHistoryArchive)
        			.setMaxEvaluations(getMaxIterations())
        			.buildDefault();
        } else if ("CMODESDEDYNAMIC".equals(algorithmID.toUpperCase())){
        	return new CMODEDynamicBuilder((DoubleProblem)problem)
        			.setBigArchive(bigArchive)
        			.setUseHistoryArchive(useHistoryArchive)
        			.setMaxEvaluations(getMaxIterations())
        			.buildSDE();
        } else if ("CMODESDENORMDYNAMIC".equals(algorithmID.toUpperCase())){
        	return new CMODEDynamicBuilder((DoubleProblem)problem)
        			.setBigArchive(bigArchive)
        			.setUseHistoryArchive(useHistoryArchive)
        			.setMaxEvaluations(getMaxIterations())
        			.buildSDENorm();
        }
        
        else {
            throw new IllegalArgumentException("Invalid algorithm ID: " + algorithmID);
        }

    }

    private List<ISwarm> createSwarms(DoubleProblem problem, MutationOperator<DoubleSolution> mutation, List<DoubleSolution> initialPopulation_) {
        BoundedArchive<DoubleSolution> archive = new CrowdingDistanceArchive<DoubleSolution>(100) ;

        List<ISwarm> swarms = new ArrayList<>();
        for (int i = 0; i < numberOfSwarms; i++) {
            if (initialPopulation_ == null) {
                swarms.add(
                    new SMPSOBuilder(problem, archive)
                            .setMutation(mutation)
                            .setMaxIterations(1)
                            .setSwarmSize(swarmSize)
                            .setRandomGenerator(new MersenneTwisterGenerator())
                            .setSolutionListEvaluator(new SequentialSolutionListEvaluator<DoubleSolution>())
                            .build()
                );
            } else {
                swarms.add(
                    new SMPSODynamicBuilder(problem, archive)
                            .setInitialPopulation(initialPopulation_)
                            .setMutation(mutation)
                            .setMaxIterations(1)
                            .setSwarmSize(swarmSize)
                            .setRandomGenerator(new MersenneTwisterGenerator())
                            .setSolutionListEvaluator(new SequentialSolutionListEvaluator<DoubleSolution>())
                            .build()
                );
            }
        }
        return swarms;
    }


}