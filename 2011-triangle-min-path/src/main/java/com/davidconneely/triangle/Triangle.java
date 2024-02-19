package com.davidconneely.triangle;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Extremely simple class to hold the triangular numeric data. New rows of
 * numeric data will always be added to the bottom, and must always be of the
 * right number of values.
 * <p>
 * The iterator methods return read-only iterators on the underlying collection.
 * <p>
 * Note that this class protects the "shape" of the triangle, but does not make
 * any attempt to protect the numeric values inside the triangle (for example by
 * making defensive copies).
 * <p>
 * <b>Assumptions:</b>
 * <ul>
 * <li>Values must be 32-bit signed integers only</li>
 * </ul>
 */
public final class Triangle implements Cloneable {
	private final List<int[]> rows = new LinkedList<int[]>();
	private final List<int[]> readOnlyRows = Collections.unmodifiableList(rows);

	/**
	 * Add a row to the bottom of the triangle, validating that it is of an
	 * appropriate length. Note that the data added is not copied.
	 */
	void addRow(int[] row) {
		if (row.length != rows.size() + 1) {
			throw new TriangleShapeException(row.length, rows.size() + 1);
		}
		rows.add(row);
	}

	/**
	 * Return an iterator over the rows positioned before the first row of data
	 * in the triangle. Note that although the row cannot be removed or changed,
	 * the values can be modified as the iterated-over arrays are the real data,
	 * not copies.
	 */
	ListIterator<int[]> firstRowIterator() {
		return readOnlyRows.listIterator();
	}

	/**
	 * Return an iterator over the rows positioned after the first row of data
	 * in the triangle. Note that although the row cannot be removed or changed,
	 * the values can be modified as the iterated-over arrays are the real data,
	 * not copies.
	 */
	ListIterator<int[]> lastRowIterator() {
		return readOnlyRows.listIterator(readOnlyRows.size());
	}

	/**
	 * Returns the number of rows in the triangle.
	 */
	int size() {
		return rows.size();
	}

	/**
	 * Create an independent copy of the Triangle. Changing the values in the
	 * cloned copy should have no impact on the original instance.
	 */
	@Override
	public Triangle clone() {
		Triangle t = new Triangle();
		ListIterator<int[]> it = firstRowIterator();
		while (it.hasNext()) {
			// int[] is cloneable
			t.addRow(it.next().clone());
		}
		return t;
	}

	/**
	 * Used for debugging only - the returned value currently has extra trailing
	 * spaces on each line, and an extra trailing newline.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int[] row : rows) {
			for (int number : row) {
				sb.append(number);
				sb.append(' ');
			}
			sb.append('\n');
		}
		return sb.toString();
	}
}