package com.davidconneely.scope;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

public final class BitmapLoaderTest {
	@Test
	public void testFromResource() throws IOException {
		Bitmap b;
		// the resources needed at runtime.
		b = BitmapLoader.fromResource("SlimeTorpedo.blf");
		assertTrue(b != null && b.height() == 13 && b.width() == 11);
        assertEquals(47, b.cardinality());
		b = BitmapLoader.fromResource("Starship.blf");
		assertTrue(b != null && b.height() == 11 && b.width() == 14);
        assertEquals(54, b.cardinality());
		// now the test resources.
		b = BitmapLoader.fromResource("TestData20.blf");
		assertTrue(b != null && b.height() == 20 && b.width() == 20);
        assertEquals(120, b.cardinality());
		b = BitmapLoader.fromResource("TestData100.blf");
		assertTrue(b != null && b.height() == 100 && b.width() == 100);
        assertEquals(2127, b.cardinality());
		// now test loading an absent resource.
		try {
			BitmapLoader.fromResource("I_DO_NOT_EXIST");
			fail("Loading a non-existent resource should throw IOException");
		} catch (IOException ioe) {
			/* expected */
		}
	}

	@Test
	public void testFromReader_Empty() throws IOException {
		StringReader rdr = new StringReader("");
		Bitmap b = BitmapLoader.fromReader(rdr);
        assertNotNull(b);
        assertEquals(0, b.height());
        assertEquals(0, b.width());
	}

	@Test
	public void testFromReader_BlankLine() throws IOException {
		StringReader rdr = new StringReader("\n");
		Bitmap b = BitmapLoader.fromReader(rdr);
        assertNotNull(b);
        assertEquals(1, b.height());
        assertEquals(0, b.width());
	}

	@Test
	public void testFromReader_1x1() throws IOException {
		StringReader rdr = new StringReader("+");
		Bitmap b = BitmapLoader.fromReader(rdr);
        assertNotNull(b);
        assertEquals(1, b.height());
        assertEquals(1, b.width());
		assertTrue(b.at(0, 0));
	}

	@Test
	public void testFromReader_4x3() throws IOException {
		StringReader rdr = new StringReader("+ + \n + +\n+ + \n");
		Bitmap b = BitmapLoader.fromReader(rdr);
        assertNotNull(b);
        assertEquals(3, b.height());
        assertEquals(4, b.width());
		assertTrue(b.at(0, 0) && !b.at(1, 0) && b.at(2, 0) && !b.at(3, 0));
		assertTrue(!b.at(0, 1) && b.at(1, 1) && !b.at(2, 1) && b.at(3, 1));
		assertTrue(b.at(0, 2) && !b.at(1, 2) && b.at(2, 2) && !b.at(3, 2));
	}

	@Test
	public void testFromReader_UnexpectedValue() throws IOException {
		StringReader rdr = new StringReader("+ + \n +X+\n+ + \n");
		try {
			BitmapLoader.fromReader(rdr);
			fail("UnexpectedValueException not thrown");
		} catch (UnexpectedValueException uve) {
            assertEquals(2, uve.getLineNo());
            assertEquals(3, uve.getColNo());
            assertEquals("X", uve.getValue());
		}
	}

	@Test
	public void testFromReader_LineTooShort() throws IOException {
		StringReader rdr = new StringReader("+ + \n + \n+ + \n");
		try {
			BitmapLoader.fromReader(rdr);
			fail("UnexpectedShapeException not thrown");
		} catch (UnexpectedShapeException use) {
            assertEquals(2, use.getLineNo());
            assertEquals(4, use.getExpectedCols());
            assertEquals(3, use.getActualCols());
		}
	}

	@Test
	public void testFromReader_LineTooLong() throws IOException {
		StringReader rdr = new StringReader("+ + \n + +\n + + \n");
		try {
			BitmapLoader.fromReader(rdr);
			fail("UnexpectedShapeException not thrown");
		} catch (UnexpectedShapeException use) {
            assertEquals(3, use.getLineNo());
            assertEquals(4, use.getExpectedCols());
            assertEquals(5, use.getActualCols());
		}
	}
}
