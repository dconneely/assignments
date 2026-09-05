package com.davidconneely.triangle;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;

/**
 * Reads triangular data from provided input. The specification isn't very detailed about the text
 * format, so some assumptions about what is allowed and what is not have been made.
 *
 * <p><b>Assumptions:</b>
 *
 * <ul>
 *   <li>Lines may have leading or trailing space characters
 *   <li>Values must be 32-bit signed integers only
 *   <li>Values can be separated by one or more space characters
 *   <li>Blank lines are not allowed in the input data
 * </ul>
 */
public final class TriangleReader {
  private final LineNumberReader rdr;

  /**
   * Initialize from an <code>InputStream</code> instance, using the default charset to read
   * character data from it.
   */
  public TriangleReader(InputStream is) {
    this(new InputStreamReader(is));
  }

  /** Initialize from a <code>Reader</code> instance. */
  TriangleReader(Reader rdr) {
    this.rdr = new LineNumberReader(rdr);
  }

  /** Read a line of space-separated numbers. */
  private int[] readLine() throws IOException {
    String line = rdr.readLine();
    if (line == null) {
      return null;
    }
    // " +" to allow one or more space chars as delimiters between values
    String[] values = line.split(" +");
    // ignore leading whitespace by skipping initial blank value
    int firstValue = (values.length > 0 && values[0].length() == 0) ? 1 : 0;
    int[] numbers = new int[values.length - firstValue];
    for (int i = 0; i < numbers.length; ++i) {
      String value = values[firstValue + i];
      try {
        numbers[i] = Integer.parseInt(value);
      } catch (NumberFormatException nfe) {
        // lineNumber is already the next line, so no need to +1 it.
        throw new TriangleValueException(rdr.getLineNumber(), i + 1, value);
      }
    }
    return numbers;
  }

  /**
   * Read the triangle data from the input provided.
   *
   * @return a constructed <code>Triangle</code> instance.
   * @throws IOException If an I/O error occurs.
   * @throws TriangleShapeException If a line of triangle data is too short or too long.
   */
  public Triangle readTriangle() throws IOException {
    Triangle t = new Triangle();
    int[] row = readLine();
    while (row != null) {
      t.addRow(row);
      row = readLine();
    }
    return t;
  }
}
