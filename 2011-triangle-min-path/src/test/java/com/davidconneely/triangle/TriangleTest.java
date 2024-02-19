package com.davidconneely.triangle;

import org.junit.jupiter.api.Test;

import java.util.ListIterator;

import static org.junit.jupiter.api.Assertions.*;

public final class TriangleTest {

	@Test
	public void testAddRow_SimpleExample() {
		Triangle t = new Triangle();
		t.addRow(new int[] { 7 });
		t.addRow(new int[] { 6, 3 });
		t.addRow(new int[] { 3, 8, 5 });
		t.addRow(new int[] { 11, 2, 10, 9 });
        assertEquals(4, t.size());
	}

	@Test
	public void testAddRow_Null() {
		Triangle t = new Triangle();
		try {
			t.addRow(null);
			fail("No exception when row is null: " + t.toString());
		} catch (NullPointerException npe) {
			/* expected */
		}
	}

	@Test
	public void testAddRow_Empty() {
		Triangle t = new Triangle();
		try {
			t.addRow(new int[0]);
			fail("No exception when row is empty: " + t.toString());
		} catch (TriangleShapeException tse) {
			assertTrue(tse.getLineNo() == 1 && tse.getNumValues() == 0);
		}
	}

	@Test
	public void testAddRow_TooShort() {
		Triangle t = new Triangle();
		t.addRow(new int[] { 7 });
		t.addRow(new int[] { 6, 3 });
		t.addRow(new int[] { 3, 8, 5 });
		t.addRow(new int[] { 11, 2, 10, 9 });
		try {
			t.addRow(new int[] { 3, 8, 5 });
			fail("No exception when row is too short: " + t.toString());
		} catch (TriangleShapeException tse) {
			assertTrue(tse.getLineNo() == 5 && tse.getNumValues() == 3);
		}
	}

	@Test
	public void testAddRow_TooLong() {
		Triangle t = new Triangle();
		t.addRow(new int[] { 7 });
		try {
			t.addRow(new int[] { 3, 8, 5 });
			fail("No exception when row is too long: " + t.toString());
		} catch (TriangleShapeException tse) {
			assertTrue(tse.getLineNo() == 2 && tse.getNumValues() == 3);
		}
	}

	@Test
	public void testFirstRowIterator() {
		Triangle t = new Triangle();
		t.addRow(new int[] { 7 });
		t.addRow(new int[] { 6, 3 });
		t.addRow(new int[] { 3, 8, 5 });
		t.addRow(new int[] { 11, 2, 10, 9 });
		ListIterator<int[]> it = t.firstRowIterator();
        assertEquals(1, it.next().length);
        assertEquals(2, it.next().length);
        assertEquals(3, it.next().length);
	}

	@Test
	public void testLastRowIterator() {
		Triangle t = new Triangle();
		t.addRow(new int[] { 7 });
		t.addRow(new int[] { 6, 3 });
		t.addRow(new int[] { 3, 8, 5 });
		t.addRow(new int[] { 11, 2, 10, 9 });
		ListIterator<int[]> it = t.lastRowIterator();
        assertEquals(4, it.previous().length);
        assertEquals(3, it.previous().length);
        assertEquals(2, it.previous().length);
	}

	@Test
	public void testClone() {
		Triangle t1 = new Triangle();
		t1.addRow(new int[] { 7 });
		t1.addRow(new int[] { 6, 3 });
		t1.addRow(new int[] { 3, 8, 5 });
		t1.addRow(new int[] { 11, 2, 10, 9 });
		Triangle t2 = t1.clone();
        assertEquals(t2.size(), t1.size());
		ListIterator<int[]> it1 = t1.firstRowIterator();
		ListIterator<int[]> it2 = t2.firstRowIterator();
		while (it1.hasNext()) {
			int[] row1 = it1.next();
			int[] row2 = it2.next();
			// check the rows have the same values
			assertArrayEquals(row1, row2);
			// check the rows are separate array instances
			row1[0]++;
			assertTrue(row1[0] != row2[0]);
		}
        assertFalse(it2.hasNext());
	}
}