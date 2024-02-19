package com.davidconneely.triangle;

import java.text.MessageFormat;

/**
 * Indicates a row of triangle data contained an invalid (non-integer) value.
 */
public final class TriangleValueException extends IllegalArgumentException {
	private final int lineNo;
	private final int valueNo;
	private final String valueText;

	TriangleValueException(int lineNo, int valueNo, String valueText) {
		super(MessageFormat.format("Failed to add row #{0,number,integer}"
				+ " because value #{1,number,integer} is not a whole"
				+ " number: \"{2}\"", lineNo, valueNo, valueText));
		this.lineNo = lineNo;
		this.valueNo = valueNo;
		this.valueText = valueText;
	}

	int getLineNo() {
		return lineNo;
	}

	int getValueNo() {
		return valueNo;
	}

	String getValueText() {
		return valueText;
	}
}