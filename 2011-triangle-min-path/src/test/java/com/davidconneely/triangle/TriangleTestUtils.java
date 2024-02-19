package com.davidconneely.triangle;

import java.util.ListIterator;

/**
 * This utility class is only used by the tests. The methods in here are not
 * intended as serious implementations, only to validate other code.
 */
final class TriangleTestUtils {

	/**
	 * Find minimal path by trying every path. This is not practical for large
	 * triangles. It will only allow triangles up to 32 rows to be searched (and
	 * it is best not to go much above 24) because it uses an integer bit-field
	 * to enumerate the possible paths.
	 */
	static int[] findMinPathAltImpl(Triangle t) {
		if (t.size() < 1 || t.size() > 32) {
			throw new IllegalArgumentException("Triangle out of range");
		}
		int numPaths = (1 << (t.size() - 1));
		int[] minPath = getPathById(t, 0);
		int minLen = getPathLen(minPath);
		for (int i = 1; i < numPaths; ++i) {
			int[] path = getPathById(t, i);
			int len = getPathLen(path);
			if (len < minLen) {
				minPath = path;
				minLen = len;
			}
		}
		return minPath;
	}

	/**
	 * Return the path through the tree identified by <code>pathId</code>. This
	 * path is from the top vertex and then on each following row the path moves
	 * either left or right depending on the next bit in the binary
	 * representation of <code>pathId</code> (0 for left, 1 for right).
	 * <p>
	 * Consider a 5-row triangle - every path from vertex to base can be
	 * represented in 4 bits
	 * <ul>
	 * <li>path 0 is binary '0000', so it follows the left edge of the triangle</li>
	 * <li>path 1 is binary '0001', so it keeps left until the final row (where
	 * it goes right)</li>
	 * <li>path 7 is binary '0111', so it takes an initial left branch, but then
	 * stays on the right branch to the base</li>
	 * <li>path 15 is binary '1111', so it follows the right edge of the
	 * triangle</li>
	 * </ul>
	 */
	private static int[] getPathById(Triangle t, int pathId) {
		if (t.size() < 1 || t.size() > 32) {
			throw new IllegalArgumentException("Triangle out of range");
		}
		int[] path = new int[t.size()];
		ListIterator<int[]> it = t.firstRowIterator();
		int colNo = 0;
		int rowNo = 0;
		while (it.hasNext()) {
			int[] row = it.next();
			if (rowNo > 0) {
				// go right if pathId bit rowNo (from left) is set
				// (otherwise left, so prefer left branches initially)
				if ((pathId & (1 << (t.size() - 1 - rowNo))) != 0) {
					++colNo;
				}
			}
			path[rowNo++] = row[colNo];
		}
		return path;
	}

	/**
	 * Total up the length of a path.
	 */
	private static int getPathLen(int[] path) {
		int len = 0;
		for (int value : path) {
			len += value;
		}
		return len;
	}

	/**
	 * Returns a randomly-generated triangle of the specified number of rows,
	 * with values varying between the specified min and max values.
	 */
	static Triangle makeRandomTriangle(int numRows, int minValue, int maxValue) {
		Triangle t = new Triangle();
		for (int i = 0; i < numRows; ++i) {
			int[] row = new int[i + 1];
			for (int j = 0; j < row.length; ++j) {
				row[j] = minValue
						+ (int) (Math.random() * (1 + maxValue - minValue));
			}
			t.addRow(row);
		}
		return t;
	}
}