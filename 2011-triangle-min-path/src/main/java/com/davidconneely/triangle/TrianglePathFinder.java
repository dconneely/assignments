package com.davidconneely.triangle;

import java.util.ListIterator;

/**
 * Find a minimal path through a triangle, see {@linkplain #findMinPath}.
 */
public final class TrianglePathFinder {
	/**
	 * Returns the values along a minimal path from vertex to base of the
	 * <code>Triangle</code> instance given using backward induction.
	 * <p>
	 * Consider the triangle with the bottom row removed, and for each node on
	 * the base of this smaller triangle, consider whether, if a minimal path
	 * went through this node, that path would continue down to the left or the
	 * right node on the removed row. The decision depends whether the value on
	 * the bottom row is smaller to the left or right - because this value is
	 * the length of the minimal path to the bottom from that point.
	 * <p>
	 * If we keep removing rows, and keep track of the lengths of the minimal
	 * paths to the bottom from each point at the bottom of the diminished
	 * triangle through the removed rows in the same way, then we will
	 * eventually reach the vertex of the triangle and be able to retrace down
	 * the triangle the steps that always have a minimal path length down to the
	 * bottom.
	 * <p>
	 * To do this, we can keep track of running totals (as we move up the
	 * triangle) of the lengths of the minimal path from each value in each row
	 * to the bottom of the original triangle, by using an additional triangle
	 * structure.
	 * <p>
	 * Note that there can be multiple minimal paths through the triangle (if
	 * all values in the triangle are equal, for example, or see
	 * <code>MinTrianglePathTest.testFindMinPathNonunique</code>).
	 * <p>
	 * Of all minimal paths, this algorithm returns the minimal path found by
	 * keeping left at every point during the walk down the triangle where a
	 * choice could be made between the multiple minimal paths (if there are any
	 * such points).
	 */
	public static int[] findMinPath(Triangle t) {
		// if there are no rows, then there is an empty minimal path
		if (t.size() == 0) {
			return new int[0];
		}
		// create a copy of the triangle and get hold of its bottom row
		Triangle tRemaining = t.clone();
		ListIterator<int[]> itRemaining = tRemaining.lastRowIterator();
		int[] rowRemainingBelow = itRemaining.previous();

		while (itRemaining.hasPrevious()) {
			int[] rowRemaining = itRemaining.previous();
			// process the row of running totals of minimal remaining paths
			for (int i = 0; i < rowRemaining.length; ++i) {
				int lowestBelow = Math.min(rowRemainingBelow[i],
						rowRemainingBelow[i + 1]);
				checkIntAdd(rowRemaining[i], lowestBelow);
				rowRemaining[i] += lowestBelow;
			}
			rowRemainingBelow = rowRemaining;
		}

		// now walk down the original triangle, always taking the path that has
		// the lowest value in the corresponding position on the triangle of
		// minimal remaining lengths
		int[] path = new int[t.size()];
		ListIterator<int[]> it = t.firstRowIterator();
		itRemaining = tRemaining.firstRowIterator();

		int rowNo = 0;
		int colNo = 0;
		while (it.hasNext()) {
			int[] rowValues = it.next();
			int[] rowRemaining = itRemaining.next();
			// decide whether to walk left or right down to the next row
			// (walk left if it doesn't matter).
			if (rowNo > 0 && rowRemaining[colNo] > rowRemaining[colNo + 1]) {
				++colNo;
			}
			path[rowNo++] = rowValues[colNo];
		}
		return path;
	}

	/**
	 * If <code>i+j</code> is likely to overflow or underflow, then throw an
	 * <code>ArithmeticException</code>. This is really being paranoid and in
	 * practice (if we know the range of values, for example) may not be
	 * necessary.
	 */
	private static final void checkIntAdd(int i, int j) {
		if (i > 0 && j > Integer.MAX_VALUE - i) {
			throw new ArithmeticException("Integer addition would overflow: "
					+ i + " + " + j);
		} else if (i < 0 && j < Integer.MIN_VALUE - i) {
			throw new ArithmeticException("Integer addition would underflow: "
					+ "(" + i + ") + (" + j + ")");
		}
	}
}