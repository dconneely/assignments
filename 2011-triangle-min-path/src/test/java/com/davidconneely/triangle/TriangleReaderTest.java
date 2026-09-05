package com.davidconneely.triangle;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import org.junit.jupiter.api.Test;

public final class TriangleReaderTest {

  @Test
  public void testReadTriangle_SimpleExample() throws IOException {
    Reader rdr = new StringReader("7\n6 3\n3 8 5\n11 2 10 9");
    TriangleReader tr = new TriangleReader(rdr);
    Triangle t = tr.readTriangle();
    assertEquals(4, t.size());
  }

  @Test
  public void testReadTriangle_EmptyInput() throws IOException {
    Reader rdr = new StringReader("");
    TriangleReader tr = new TriangleReader(rdr);
    Triangle t = tr.readTriangle();
    assertEquals(0, t.size());
  }

  @Test
  public void testReadTriangle_SingleBlankLine() throws IOException {
    Reader rdr = new StringReader("\n");
    TriangleReader tr = new TriangleReader(rdr);
    try {
      Triangle t = tr.readTriangle();
      fail("No exception when row is empty: " + t.toString());
    } catch (TriangleShapeException tse) {
      assertTrue(tse.getLineNo() == 1 && tse.getNumValues() == 0);
    }
  }

  @Test
  public void testReadTriangle_RowTooShort() throws IOException {
    Reader rdr = new StringReader("7\n6 3\n3 8 5\n11 2 10 9\n3 8 5");
    TriangleReader tr = new TriangleReader(rdr);
    try {
      Triangle t = tr.readTriangle();
      fail("No exception when row is too short: " + t.toString());
    } catch (TriangleShapeException tse) {
      assertTrue(tse.getLineNo() == 5 && tse.getNumValues() == 3);
    }
  }

  @Test
  public void testReadTriangle_RowTooLong() throws IOException {
    Reader rdr = new StringReader("7\n3 8 5");
    TriangleReader tr = new TriangleReader(rdr);
    try {
      Triangle t = tr.readTriangle();
      fail("No exception when row is too long: " + t.toString());
    } catch (TriangleShapeException tse) {
      assertTrue(tse.getLineNo() == 2 && tse.getNumValues() == 3);
    }
  }

  @Test
  public void testReadTriangle_NonNumericValue() throws IOException {
    Reader rdr = new StringReader("7\n6 3\n3 8 5\n11 2 splat 9");
    TriangleReader tr = new TriangleReader(rdr);
    try {
      Triangle t = tr.readTriangle();
      fail("No exception when non-numeric value: " + t.toString());
    } catch (TriangleValueException tse) {
      assertTrue(
          tse.getLineNo() == 4 && tse.getValueNo() == 3 && "splat".equals(tse.getValueText()));
    }
  }

  @Test
  public void testReadTriangle_ExtraSpaces() throws IOException {
    Reader rdr =
        new StringReader(
            """
                  7 \s
                  6   3 \s
                 3   8    5      \s
                11  2   10   9     \s
                """);
    TriangleReader tr = new TriangleReader(rdr);
    Triangle t = tr.readTriangle();
    assertEquals(4, t.size());
  }
}
