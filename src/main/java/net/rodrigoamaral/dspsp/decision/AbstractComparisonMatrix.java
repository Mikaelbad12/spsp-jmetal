package net.rodrigoamaral.dspsp.decision;

import java.util.Arrays;

/**
 * Represents the comparison matrix used in decision making procedure
 * to choose the preferred schedule to use after rescheduling happens.
 *
 * Indexing starts in 1 due to readability and compatibility issues with
 * original MATLAB code and mathematical notation.
 */
public abstract class AbstractComparisonMatrix {

	protected double[][] cm;
	
	/**
     * Calculates weight vector for decision making
     * of the fisrt schedule.
     *
     * @return weight vector used in decision making
     */
    public double[] initialWeights() {
    	double[][] im = initialMatrix();
    	double[] weights = new double[im.length];
        double sum = 0;
        for (int i = 0; i < weights.length; i++) {
            weights[i] = geometricMean(im[i]);
            sum += weights[i];
        }
        // Normalizing weights
        for (int i = 0; i < weights.length; i++) {
            weights[i] = weights[i] / sum;
        }
        return weights;
    }
    
    protected abstract double[][] initialMatrix();

    /**
     * Calculates weight vector for decision making
     * of the reschedulings.
     *
     * @return weight vector used in decision making
     */
    public double[] reschedulingWeights() {
        double[] weights = new double[cm.length];
        double sum = 0;
        for (int i = 0; i < weights.length; i++) {
            weights[i] = geometricMean(cm[i]);
            sum += weights[i];
        }
        // Normalizing weights
        for (int i = 0; i < weights.length; i++) {
            weights[i] = weights[i] / sum;
        }
        return weights;
    }
    
    private double geometricMean(double[] values) {
        double prod = 1;
        for (int i = 0; i < values.length; i++) {
            prod *= values[i];
        }
        return Math.pow(prod, 1.0 / values.length);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ComparisonMatrix{cm=[\n");
        for (int i = 1; i <= cm.length ; i++) {
            sb.append(Arrays.toString(getRow(i)));
            sb.append("\n");
        }
        sb.append("]}");
        return sb.toString();
    }
    
    private double[] getRow(int r) {
        return cm[r-1];
    }

}