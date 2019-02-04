package net.rodrigoamaral.dspsp.decision;

/**
 * Represents the comparison matrix used in decision making procedure
 * to choose the preferred schedule to use after rescheduling happens.
 *
 * Indexing starts in 1 due to readability and compatibility issues with
 * original MATLAB code and mathematical notation.
 */
public class ComparisonMatrixExtended extends AbstractComparisonMatrix {

    public ComparisonMatrixExtended() {
    	cm = new double[5][5];
        set(1, 1, 1);
        set(1, 2, 1);
        set(1, 3, 2);
        set(1, 4, 2);
        set(1, 5, 2);
        set(2, 1, 1 / get(1, 2));
        set(2, 2, 1);
        set(2, 3, 2);
        set(2, 4, 2);
        set(2, 5, 2);
        set(3, 1, 1 / get(1, 3));
        set(3, 2, 1 / get(2, 3));
        set(3, 3, 1);
        set(3, 4, 1);
        set(3, 5, 1);
        set(4, 1, 1 / get(1, 4));
        set(4, 2, 1 / get(2, 4));
        set(4, 3, 1 / get(3, 4));
        set(4, 4, 1);
        set(4, 5, 1);
        set(5, 1, 1 / get(1, 5));
        set(5, 2, 1 / get(2, 5));
        set(5, 3, 1 / get(3, 5));
        set(5, 4, 1 / get(4, 5));
        set(5, 5, 1);
    }

    private void set(int r, int c, double value) {
        cm[r-1][c-1] = value;
    }

    public double get(int r, int c) {
        return cm[r-1][c-1];
    }

    protected double[][] initialMatrix() {
        double[][] im = new double[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                im[i][j] = cm[i][j];
            }
        }
        return im;
    }

}
