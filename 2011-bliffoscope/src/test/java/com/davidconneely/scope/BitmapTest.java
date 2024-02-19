package com.davidconneely.scope;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public final class BitmapTest {

	@Test
	public void testToString_Empty() {
		boolean[][] data = new boolean[0][0];
		String val = Bitmap.fromArray(data).toString();
        assertEquals("", val);
	}

	@Test
	public void testToString_BlankLine() {
		boolean[][] data = new boolean[1][0];
		String val = Bitmap.fromArray(data).toString();
        assertEquals("\n", val);
	}

	@Test
	public void testToString_1x1() {
		boolean[][] data = new boolean[][] {
				{ true }
		};
		String val = Bitmap.fromArray(data).toString();
        assertEquals("+\n", val);
	}

	@Test
	public void testToString_4x3() {
		boolean[][] data = new boolean[][] {
				{ true, false, true, false },
				{ false, true, false, true },
				{ true, false, true, false }
		};
		String val = Bitmap.fromArray(data).toString();
        assertEquals(("+ + \n" + " + +\n" + "+ + \n"), val);
	}

	@Test
	public void testTrim_Empty() {
		boolean[][] data = new boolean[0][0];
		Bitmap b = Bitmap.fromArray(data).trim();
        assertNotNull(b);
        assertEquals(0, b.height());
        assertEquals(0, b.width());
	}

	@Test
	public void testTrim_BlankLine() {
		boolean[][] data = new boolean[1][0];
		Bitmap b = Bitmap.fromArray(data).trim();
        assertNotNull(b);
        assertEquals(0, b.height());
        assertEquals(0, b.width());
	}

	@Test
	public void testTrim_1x1() {
		boolean[][] data = new boolean[][] { { false } };
		Bitmap b = Bitmap.fromArray(data).trim();
        assertNotNull(b);
        assertEquals(0, b.height());
        assertEquals(0, b.width());

		data = new boolean[][] { { true } };
		b = Bitmap.fromArray(data).trim();
        assertNotNull(b);
        assertEquals(1, b.height());
        assertEquals(1, b.width());
		assertTrue(b.at(0, 0));
	}

	@Test
	public void testTrim_4x3() {
		boolean[][] data = new boolean[][] {
				{ false, false, false, false },
				{ false, false, false, false },
				{ false, false, false, false }
		};
		Bitmap b = Bitmap.fromArray(data).trim();
        assertNotNull(b);
        assertEquals(0, b.height());
        assertEquals(0, b.width());

		data = new boolean[][] {
				{ false, false, false, false },
				{ false, true, true, false },
				{ false, false, false, false }
		};
		b = Bitmap.fromArray(data).trim();
        assertNotNull(b);
        assertEquals(1, b.height());
        assertEquals(2, b.width());
		assertTrue(b.at(0, 0) && b.at(1, 0));
	}
}
