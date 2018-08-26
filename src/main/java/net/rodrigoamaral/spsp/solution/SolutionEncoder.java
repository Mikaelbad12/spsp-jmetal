package net.rodrigoamaral.spsp.solution;

public class SolutionEncoder {
    private int employees;
    private int tasks;
    @SuppressWarnings("unused")
	private double[] dedicationMatrix;

    public SolutionEncoder(int employees, int tasks) {
        this.employees = employees;
        this.tasks = tasks;
        this.dedicationMatrix = new double[size()];
    }

    public int size() {
        return employees * tasks;
    }

}
