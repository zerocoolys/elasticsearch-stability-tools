package com.nelo2.benchmark.result;

public class DSLStats {

	private String name;
	private int size;
	private String totalTime;
	private double evarageTime;

	public DSLStats() {

	}

	public DSLStats(String name, int size, String totalTime, double evarageTime) {
		super();
		this.name = name;
		this.size = size;
		this.totalTime = totalTime;
		this.evarageTime = evarageTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(String totalTime) {
		this.totalTime = totalTime;
	}

	public double getEvarageTime() {
		return evarageTime;
	}

	public void setEvarageTime(double evarageTime) {
		this.evarageTime = evarageTime;
	}

}
