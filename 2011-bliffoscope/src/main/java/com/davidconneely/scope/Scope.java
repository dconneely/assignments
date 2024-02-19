package com.davidconneely.scope;

import java.io.IOException;
import java.util.List;

public final class Scope {

	/**
	 * Entry-point to the Scope processing engine. This is placed in the
	 * default package only to make it simple to invoke from the command-line.
	 * <p>
	 * Executing the class takes two non-negative integers as command-line
	 * arguments. The second argument defaults to 50 if it is omitted.
	 * <ul>
	 * <li>the maximum number of top candidate targets to list (0-2^31).</li>
	 * <li>the minimal percentage confidence we would like for any listed
	 *     candidate target (0-100).</li>
	 * </ul>
	 * The standard input this program expects is the contents of the scope, in
	 * the same format as a <code>.blf</code> file.
	 * <p>
	 * It will produce a list of candidate targets, their locations and
	 * confidences in a human-readable text format to its standard output.
	 */
	public static void main(String[] args) {
		// parse the command-line arguments.
		int limitCount = -1;
		if (args.length >= 1) {
			try {
				limitCount = Integer.parseInt(args[0]);
			} catch (NumberFormatException nfe) {
				/* limitCount will be -1 */
			}
		}
		int limitConfidence = 50;
		if (args.length >= 2) {
			try {
				limitConfidence = Integer.parseInt(args[1]);
			} catch (NumberFormatException nfe) {
				limitConfidence = -1;
			}
		}
		// show usage if anything looks wrong.
		if (args.length < 1 || args.length > 2 || limitCount < 0
				|| limitConfidence < 0 || limitConfidence > 100) {
			usage();
			System.exit(64);
		}
		// do the real work in here.
		try {
			Bitmap scope = BitmapLoader.fromInputStream(System.in, "US-ASCII");
			List<CandidateTarget> candidates = Analyzer.findCandidateTargets(scope, limitConfidence / 100.0f, limitCount);
			System.out.println("Candidate targets are located by their midpoint coordinates:");
			for (CandidateTarget candidate: candidates) {
				System.out.println(candidate.toString());
			}
		} catch (UnexpectedShapeException tse) {
			System.err.println("There is a line that is too short or too long"
					+ " in the input data:");
			System.err.println(tse.getMessage());
			System.exit(65);
		} catch (UnexpectedValueException tve) {
			System.err.println("There is an invalid value in the input data:");
			System.err.println(tve.getMessage());
			System.exit(65);
		} catch (IOException ioe) {
			System.err.println("There was an I/O error:");
			System.err.println(ioe.getMessage());
			System.exit(74);
		}
	}

	private static void usage() {
		System.err.println();
		System.err.println("Lists candidate targets found in scope data read from standard input.");
		System.err.println();
		System.err.println("    java -jar scope.jar <count> [<confidence>]");
		System.err.println();
		System.err.println("where <count> is the maximum number of candidate targets to list, and");
		System.err.println("<confidence> is the percentage confidence (0-100) those target must exceed.");
		System.err.println("If no confidence percentage is provided, a default value of 50 is used.");
		System.err.println();
		System.err.println("For example,");
		System.err.println();
		System.err.println("    cat TestData.blf | java -jar scope.jar 10 60");
		System.err.println();
		System.err.println("would find the top 10 candidates with confidence level above 60% from the");
		System.err.println("file TestData.blf, and output them to standard output.");
		System.err.println();
		System.err.println("And,");
		System.err.println();
		System.err.println("    cat TestData.blf | java -jar scope.jar 12");
		System.err.println();
		System.err.println("would find the top 12 candidates with confidence level above 50% from the");
		System.err.println("file TestData.blf, and output them to standard output.");
		System.err.println();
	}
}
