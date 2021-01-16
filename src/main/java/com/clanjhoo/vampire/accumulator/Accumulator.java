package com.clanjhoo.vampire.accumulator;

import com.clanjhoo.vampire.util.MathUtil;

// Class that modifies an integer variable by increments that may not be integer
public abstract class Accumulator
{
	// Returns the current value of the integer variable
	protected abstract int real();

	// Sets the current value of the integer variable
	protected abstract void real(int val);

	// Minimum value that the integer variable can have
	protected Integer min = null;

	// Returns min
	public Integer getMin() {
		return this.min;
	}

	// Sets min
	public void setMin(Integer min) {
		this.min = min;
	}

	// Maximum value that the integer variable can have
	protected Integer max = null;

	// Returns max
	public Integer getMax() {
		return this.max;
	}

	// Sets max
	public void setMax(Integer max) {
		this.max = max;
	}

	// The accumulated quantity less than one
	protected double diff = 0;

	//
	protected void update() {
		int delta = (int) this.diff;
		if (delta == 0) return;
		this.diff -= delta;
		int target = MathUtil.limitNumber(this.real() + delta, min, max);
		this.real(target);
	}
	
	public double get() {
		return this.real() + this.diff;
	}
	
	public void set(double val) {
		int intVal = (int) val;
		this.diff = val - intVal;
		this.real(MathUtil.limitNumber(intVal, min, max));
	}
	
	public double add(double val) {
		double before = this.get();
		this.diff += val;
		this.update();
		double after = this.get();
		return after - before;
	}
}
