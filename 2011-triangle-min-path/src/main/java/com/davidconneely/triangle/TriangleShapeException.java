package com.davidconneely.triangle;

import java.text.MessageFormat;

/**
 * Indicates a row of triangle data had the wrong number of values.
 */
public final class TriangleShapeException extends IllegalArgumentException {
	private final int lineNo;
	private final int numValues;

	TriangleShapeException(int numValues, int lineNo) {
		super(MessageFormat.format("Failed to add row #{1,number,integer}"
				+ " because it contains {0,number,integer}"
				+ " {0,choice,0#numbers|1#number|1<numbers} instead"
				+ " of {1,number,integer}", numValues, lineNo));
		this.lineNo = lineNo;
		this.numValues = numValues;
	}

	int getLineNo() {
		return lineNo;
	}

	int getNumValues() {
		return numValues;
	}
}