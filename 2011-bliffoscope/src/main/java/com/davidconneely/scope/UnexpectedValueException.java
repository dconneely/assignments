package com.davidconneely.scope;

import java.text.MessageFormat;

/**
 * Indicates a line of input data contained an unexpected value.
 */
public final class UnexpectedValueException extends IllegalArgumentException {
	private final int lineNo;
	private final int colNo;
	private final String value;

	UnexpectedValueException(int lineNo, int colNo, String value) {
		super(MessageFormat.format("Failed to process line #{0,number,integer}"
				+ " because column #{1,number,integer} is ''{2}'',"
				+ " not ''+'' or '' ''",
				lineNo, colNo, value));
		this.lineNo = lineNo;
		this.colNo = colNo;
		this.value = value;
	}

	int getLineNo() {
		return lineNo;
	}

	int getColNo() {
		return colNo;
	}

	String getValue() {
		return value;
	}
}