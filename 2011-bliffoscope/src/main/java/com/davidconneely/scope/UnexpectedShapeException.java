package com.davidconneely.scope;

import java.text.MessageFormat;

/** Indicates a line of input data had an unexpected number of columns. */
public final class UnexpectedShapeException extends IllegalArgumentException {
  private final int lineNo;
  private final int actualCols;
  private final int expectedCols;

  UnexpectedShapeException(int lineNo, int actualCols, int expectedCols) {
    super(
        MessageFormat.format(
            "Failed to process line #{0,number,integer}"
                + " because it contains {1,number,integer}"
                + " {1,choice,0#columns|1#column|1<columns} instead of"
                + " {2,number,integer}",
            lineNo,
            actualCols,
            expectedCols));
    this.lineNo = lineNo;
    this.actualCols = actualCols;
    this.expectedCols = expectedCols;
  }

  int getLineNo() {
    return lineNo;
  }

  int getActualCols() {
    return actualCols;
  }

  int getExpectedCols() {
    return expectedCols;
  }
}
