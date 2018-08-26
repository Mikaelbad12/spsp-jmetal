package net.rodrigoamaral.spsp;

import net.rodrigoamaral.spsp.meapr.MultiSwarmMEAPRBuilder;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.runner.AbstractAlgorithmRunner;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.impl.MersenneTwisterGenerator;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Class for configuring and running the MEAPR algorithm
 */
public class SPSP_MultiSwarmMEAPRRunner extends AbstractAlgorithmRunner {

    public static void printFinalSolutionSet(List<? extends Solution<?>> population) {

        String varFile = "VAR_MultiSwarmMEAPR.csv";
        String funFile = "FUN_MultiSwarmMEAPR.csv";
        new SolutionListOutput(population)
                .setSeparator(";")
                .setVarFileOutputContext(new DefaultFileOutputContext(varFile))
                .setFunFileOutputContext(new DefaultFileOutputContext(funFile))
                .print();

        JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
        JMetalLogger.logger.info("Objectives values have been written to file " + funFile);
        JMetalLogger.logger.info("Variables values have been written to file " + varFile);
    }

    private static SPSProblem loadProjectInstanceFromFile(String projectPropertiesFileName) throws FileNotFoundException {
        return new SPSProblem(projectPropertiesFileName);
    }
    /**
     * @param args Command line arguments. The first (optional) argument specifies
     *             the problem to solve.
     * @throws org.uma.jmetal.util.JMetalException
     * @throws java.io.IOException
     * @throws SecurityException
     * Invoking command:
    java org.uma.jmetal.runner.multiobjective.SPSP_MultiSwarmMEAPRRunner problemName [referenceFront]
     */
    @SuppressWarnings({ "unused", "unchecked" })
	public static void main(String[] args) throws Exception {
        DoubleProblem problem;
        Algorithm<List<DoubleSolution>> algorithm;
        MutationOperator<DoubleSolution> mutation;

        String referenceParetoFront = "" ;

        // Creates a SPSP project instance
        String filename = "";
        if (args.length == 1) {
            filename = args[0];
        } else if (args.length == 2) {
            filename = args[0];
            referenceParetoFront = args[1];
        }

        problem = loadProjectInstanceFromFile(filename);

        algorithm = new MultiSwarmMEAPRBuilder(problem)
                .setRandomGenerator(new MersenneTwisterGenerator())
                .setSolutionListEvaluator(new SequentialSolutionListEvaluator<DoubleSolution>())
                .build();

        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
                .execute();

        List<DoubleSolution> population = algorithm.getResult();
        long computingTime = algorithmRunner.getComputingTime();

        JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

        printFinalSolutionSet(population);
        if (!referenceParetoFront.equals("")) {
            printQualityIndicators(population, referenceParetoFront) ;
        }
    }
}
