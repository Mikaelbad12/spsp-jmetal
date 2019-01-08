package net.rodrigoamaral.dspsp.instances;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DynamicExtendedInstance extends DynamicInstance {

	private int MTBEA;
	private int newemployee_nmb;
	private List<Double> newemployee_arrivalrate;
	private List<Double> newemployee_arrival_time;
	private List<Double> employee_salary_new;
	private List<Double> employee_salary_over_new;
	private List<List<Integer>> employee_skill_set_new;
	private List<List<Double>> employee_skill_proficiency_set_new;
	private List<Integer> employee_skill_number_new;
	private List<Double> employee_maxded_new;
	private List<Integer> Randperm_new;
	private int l1_new;
	private int l2_new;
	private List<Double> employee_salary_total;
	private List<Double> employee_salary_over_total;
	private List<List<Integer>> employee_skill_set_total;
	private List<Integer> employee_skill_number_total;
	private List<List<Double>> employee_skill_proficiency_set_total;
	private List<Double> employee_maxded_total;
	private int MTBTR;
	private int task_removal_nmb;
	private List<Double> task_removalrate;
	private List<Double> task_removal_time;
	private int l3;
	private int l4;
	private List<Double> employee_arrival_time;
	private List<Integer> dynamic_new_labour_number;
	private List<Integer> dynamic_remove_task_number;
	private int new_labour_counter;
	private List<List<Integer>> dynamic_parallel_class;
	private List<Double> dynamic_parallel_time;
	private int lenght_dynamic_class;
	private int employee_total_number;
	
	public int getMTBEA() {
		return MTBEA;
	}
	
	@JsonProperty("MTBEA")
	public void setMTBEA(int mTBEA) {
		MTBEA = mTBEA;
	}
	
	public int getNewemployee_nmb() {
		return newemployee_nmb;
	}
	
	public void setNewemployee_nmb(int newemployee_nmb) {
		this.newemployee_nmb = newemployee_nmb;
	}
	
	public List<Double> getNewemployee_arrivalrate() {
		return newemployee_arrivalrate;
	}
	
	public void setNewemployee_arrivalrate(List<Double> newemployee_arrivalrate) {
		this.newemployee_arrivalrate = newemployee_arrivalrate;
	}
	
	public List<Double> getNewemployee_arrival_time() {
		return newemployee_arrival_time;
	}

	public void setNewemployee_arrival_time(List<Double> newemployee_arrival_time) {
		this.newemployee_arrival_time = newemployee_arrival_time;
	}
	
	public List<Double> getEmployee_salary_new() {
		return employee_salary_new;
	}
	
	public void setEmployee_salary_new(List<Double> employee_salary_new) {
		this.employee_salary_new = employee_salary_new;
	}
	
	public List<Double> getEmployee_salary_over_new() {
		return employee_salary_over_new;
	}
	
	public void setEmployee_salary_over_new(List<Double> employee_salary_over_new) {
		this.employee_salary_over_new = employee_salary_over_new;
	}
	
	public List<List<Integer>> getEmployee_skill_set_new() {
		return employee_skill_set_new;
	}
	
	public void setEmployee_skill_set_new(List<List<Integer>> employee_skill_set_new) {
		this.employee_skill_set_new = employee_skill_set_new;
	}
	
	public List<List<Double>> getEmployee_skill_proficiency_set_new() {
		return employee_skill_proficiency_set_new;
	}
	
	public void setEmployee_skill_proficiency_set_new(List<List<Double>> employee_skill_proficiency_set_new) {
		this.employee_skill_proficiency_set_new = employee_skill_proficiency_set_new;
	}
	
	public List<Integer> getEmployee_skill_number_new() {
		return employee_skill_number_new;
	}
	
	public void setEmployee_skill_number_new(List<Integer> employee_skill_number_new) {
		this.employee_skill_number_new = employee_skill_number_new;
	}
	
	public List<Double> getEmployee_maxded_new() {
		return employee_maxded_new;
	}
	
	public void setEmployee_maxded_new(List<Double> employee_maxded_new) {
		this.employee_maxded_new = employee_maxded_new;
	}
	
	public List<Integer> getRandperm_new() {
		return Randperm_new;
	}
	
	@JsonProperty("Randperm_new")
	public void setRandperm_new(List<Integer> randperm_new) {
		Randperm_new = randperm_new;
	}
	
	public int getL1_new() {
		return l1_new;
	}
	
	public void setL1_new(int l1_new) {
		this.l1_new = l1_new;
	}
	
	public int getL2_new() {
		return l2_new;
	}
	
	public void setL2_new(int l2_new) {
		this.l2_new = l2_new;
	}
	
	public List<List<Integer>> getEmployee_skill_set_total() {
		return employee_skill_set_total;
	}
	
	public void setEmployee_skill_set_total(List<List<Integer>> employee_skill_set_total) {
		this.employee_skill_set_total = employee_skill_set_total;
	}
	
	public List<Integer> getEmployee_skill_number_total() {
		return employee_skill_number_total;
	}
	
	public void setEmployee_skill_number_total(List<Integer> employee_skill_number_total) {
		this.employee_skill_number_total = employee_skill_number_total;
	}
	
	public List<List<Double>> getEmployee_skill_proficiency_set_total() {
		return employee_skill_proficiency_set_total;
	}
	
	public void setEmployee_skill_proficiency_set_total(List<List<Double>> employee_skill_proficiency_set_total) {
		this.employee_skill_proficiency_set_total = employee_skill_proficiency_set_total;
	}
	
	public List<Double> getEmployee_maxded_total() {
		return employee_maxded_total;
	}
	
	public void setEmployee_maxded_total(List<Double> employee_maxded_total) {
		this.employee_maxded_total = employee_maxded_total;
	}
	
	public int getMTBTR() {
		return MTBTR;
	}
	
	@JsonProperty("MTBTR")
	public void setMTBTR(int mTBTR) {
		MTBTR = mTBTR;
	}
	
	public int getTask_removal_nmb() {
		return task_removal_nmb;
	}
	
	public void setTask_removal_nmb(int task_removal_nmb) {
		this.task_removal_nmb = task_removal_nmb;
	}
	
	public List<Double> getTask_removalrate() {
		return task_removalrate;
	}
	
	public void setTask_removalrate(List<Double> task_removalrate) {
		this.task_removalrate = task_removalrate;
	}
	
	public List<Double> getTask_removal_time() {
		return task_removal_time;
	}
	
	public void setTask_removal_time(List<Double> task_removal_time) {
		this.task_removal_time = task_removal_time;
	}
	
	public int getL3() {
		return l3;
	}
	
	public void setL3(int l3) {
		this.l3 = l3;
	}
	
	public int getL4() {
		return l4;
	}
	
	public void setL4(int l4) {
		this.l4 = l4;
	}
	
	public List<Integer> getDynamic_new_labour_number() {
		return dynamic_new_labour_number;
	}
	
	public void setDynamic_new_labour_number(List<Integer> dynamic_new_labour_number) {
		this.dynamic_new_labour_number = dynamic_new_labour_number;
	}
	
	public List<Integer> getDynamic_remove_task_number() {
		return dynamic_remove_task_number;
	}
	
	public void setDynamic_remove_task_number(List<Integer> dynamic_remove_task_number) {
		this.dynamic_remove_task_number = dynamic_remove_task_number;
	}
	
	public int getNew_labour_counter() {
		return new_labour_counter;
	}
	
	public void setNew_labour_counter(int new_labour_counter) {
		this.new_labour_counter = new_labour_counter;
	}
	
	public List<List<Integer>> getDynamic_parallel_class() {
		return dynamic_parallel_class;
	}
	
	public void setDynamic_parallel_class(List<List<Integer>> dynamic_parallel_class) {
		this.dynamic_parallel_class = dynamic_parallel_class;
	}
	
	public List<Double> getDynamic_parallel_time() {
		return dynamic_parallel_time;
	}
	
	public void setDynamic_parallel_time(List<Double> dynamic_parallel_time) {
		this.dynamic_parallel_time = dynamic_parallel_time;
	}
	
	public int getLenght_dynamic_class() {
		return lenght_dynamic_class;
	}
	
	public void setLenght_dynamic_class(int lenght_dynamic_class) {
		this.lenght_dynamic_class = lenght_dynamic_class;
	}
	
	public int getEmployee_total_number() {
		return employee_total_number;
	}

	public void setEmployee_total_number(int employee_total_number) {
		this.employee_total_number = employee_total_number;
	}

	public List<Double> getEmployee_arrival_time() {
		return employee_arrival_time;
	}

	public void setEmployee_arrival_time(List<Double> employee_arrival_time) {
		this.employee_arrival_time = employee_arrival_time;
	}

	public List<Double> getEmployee_salary_total() {
		return employee_salary_total;
	}

	public void setEmployee_salary_total(List<Double> employee_salary_total) {
		this.employee_salary_total = employee_salary_total;
	}

	public List<Double> getEmployee_salary_over_total() {
		return employee_salary_over_total;
	}

	public void setEmployee_salary_over_total(List<Double> employee_salary_over_total) {
		this.employee_salary_over_total = employee_salary_over_total;
	}

}
