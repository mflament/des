package org.mf.tools.des;

public final class SBox {

	private final int[][] data;

	public SBox(int[][] data) {
		super();
		this.data = data;
	}

	public long get(int row, int col) {
		return data[row][col];
	}

}
